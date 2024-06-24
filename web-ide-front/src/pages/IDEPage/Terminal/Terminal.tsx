import { useAppDispatch, useAppSelector } from '@/hooks'
import {
  selectFileExecuteResult,
  setFileExecuteResult,
  setTree,
} from '@/store/ideSlice'
import { selectId } from '@/store/userSlice'
import { isEditable } from '@/utils/ide'
import { Client, IMessage } from '@stomp/stompjs'
import { Terminal as xterm } from '@xterm/xterm'
import '@xterm/xterm/css/xterm.css'
import { useEffect, useRef } from 'react'
import { flattenTree } from 'react-accessible-treeview'

interface Props {
  containerId: string | undefined
  category: string
  isOwner: boolean
  status: string
}

const Terminal = ({ containerId, category, isOwner, status }: Props) => {
  const terminalRef = useRef(null)
  const terminal = useRef<xterm>()
  const fileExecuteResult = useAppSelector(selectFileExecuteResult)
  const dispatch = useAppDispatch()

  const clientRef = useRef<Client | null>(null)
  const currentCommandRef = useRef<string>('')
  const isConnected = useRef<boolean>(false)

  const currentPath = useRef<string>('/')

  const userId = useAppSelector(selectId)

  // 터미널 초기 세팅
  useEffect(() => {
    terminal.current = new xterm({
      cursorBlink: isEditable(status, category, isOwner),
    })
    terminal.current.open(terminalRef.current!)
    terminal.current.resize(100, 12)
    terminal.current.write(`\x1B[1;3;31m${currentPath.current}\x1B[0m % `)

    terminal.current.onKey(({ key, domEvent }) => {
      if (!isEditable(status, category, isOwner)) return

      const printable =
        !domEvent.altKey && !domEvent.ctrlKey && !domEvent.metaKey

      if (domEvent.key === 'Enter') {
        sendCommand()
      } else if (domEvent.key === 'Backspace') {
        // Do not delete the prompt
        if (
          terminal.current!.buffer.active.cursorX >
          currentPath.current.length + 3
        ) {
          terminal.current!.write('\b \b')
          currentCommandRef.current = currentCommandRef.current.slice(0, -1)
        }
      } else if (printable) {
        terminal.current!.write(key)
        currentCommandRef.current += key
      }
    })

    return () => {
      terminal.current!.dispose()
    }
  }, [])

  // socket 클라이언트 생성
  useEffect(() => {
    const client = new Client({
      brokerURL: `${import.meta.env.VITE_SERVER_WS_BASE_URL}/api/ws`,
      beforeConnect: () => console.log('[Terminal] Attempting to connect...'),
      onConnect: () => handleWebSocketConnect(client),
      onDisconnect: () => handleWebSocketDisconnect(client),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      debug: str => console.debug(str),
      onWebSocketClose: () => {
        console.error('[Terminal] WebSocket connection closed')
        isConnected.current = false
      },
      onStompError: frame => {
        console.error(
          '[Terminal] Broker reported error: ' + frame.headers['message']
        )
        console.error('[Terminal] Additional details: ' + frame.body)
      },
      onWebSocketError: event => {
        console.error('[Terminal] WebSocket error', event)
      },
    })

    client.activate()
    clientRef.current = client

    return () => {
      console.log(
        '[Terminal] Component unmounting, deactivating WebSocket connection...'
      )
      if (client) {
        client.deactivate()
      }
    }
  }, [])

  const handleWebSocketConnect = (client: Client) => {
    console.log('[Terminal] Connected to WebSocket')
    isConnected.current = true

    // 구독
    client.subscribe(
      `/api/sub/terminal/${containerId}/${userId}`,
      (message: IMessage) => {
        const newMessage = JSON.parse(message.body)
        const result = newMessage.result
        const allEntries = newMessage.allEntries

        const currentCommand = currentCommandRef.current
        currentCommandRef.current = ''

        if (currentCommand.includes('touch') || currentCommand.includes('rm')) {
          dispatch(setTree(flattenTree(allEntries)))
          terminal.current!.writeln(result)
        } else if (currentCommand.includes('cat')) {
          const resultArray = result.split('\n')
          for (const line of resultArray) {
            terminal.current!.writeln(`${line}`)
          }
        } else if (currentCommand.includes('cd')) {
          if (!result.includes('not found')) {
            if (!result.includes('WORKSPACE')) {
              currentPath.current = '/'
            } else {
              currentPath.current = result.replace(
                /^Changed directory to \/WORKSPACE-[^/]+/,
                ''
              )

              if (currentPath.current === '') {
                currentPath.current = '/'
              }
            }
          }
          terminal.current!.writeln(`${result}`)
        } else if (currentCommand.includes('ls')) {
          const resultArray = result.split('\n')
          for (const line of resultArray) {
            terminal.current!.writeln(`${line}`)
          }
        } else {
          const resultArray = result.split('\n')
          for (const line of resultArray) {
            terminal.current!.writeln(`${line}`)
          }
        }

        terminal.current!.write(`\x1B[1;3;31m${currentPath.current}\x1B[0m % `)
      }
    )
  }

  const sendCommand = () => {
    // 빈 문자열은 보내지 않는다.
    if (currentCommandRef.current === '') return

    // 연결이 되어있지 않으면 보내지 않는다.
    if (!clientRef.current || !isConnected) return

    // 발행
    clientRef.current.publish({
      destination: `/api/pub/terminal/${containerId}/${userId}`,
      body: JSON.stringify({
        command: currentCommandRef.current.trim(),
      }),
    })
    terminal.current!.write('\r\n')
  }

  const handleWebSocketDisconnect = (client: Client) => {
    console.log('[Terminal] Disconnected from WebSocket')

    isConnected.current = false
    client.deactivate()
  }

  // 실행 버튼 클릭으로 파일이 실행되었을 때의 결과
  useEffect(() => {
    if (fileExecuteResult) {
      const resultArray = fileExecuteResult.split('\n')
      terminal.current!.write('\r\n')

      for (const line of resultArray) {
        terminal.current!.writeln(`${line}`)
      }
      terminal.current!.write(`\x1B[1;3;31m${currentPath.current}\x1B[0m % `)
      dispatch(setFileExecuteResult(''))
    }
  }, [fileExecuteResult])

  return <div ref={terminalRef} />
}

export default Terminal
