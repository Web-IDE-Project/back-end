import {
  Box,
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  Flex,
} from '@chakra-ui/react'
import { EditorState, Transaction } from '@codemirror/state'
import { EditorView } from '@codemirror/view'
import { basicSetup } from 'codemirror'
import { useCallback, useEffect, useRef, useState } from 'react'
import { javascript } from '@codemirror/lang-javascript'
import { cpp } from '@codemirror/lang-cpp'
import { java } from '@codemirror/lang-java'
import { python } from '@codemirror/lang-python'
import {} from '@codemirror/language'
import { githubLight } from '@uiw/codemirror-theme-github'
import './code-editor.css'
import { LanguageSupport } from '@codemirror/language'
import { showMinimap } from '@replit/codemirror-minimap'
import yorkie, { Client, Indexable, Document, type Text } from 'yorkie-js-sdk'
import { useAppDispatch, useAppSelector } from '@/hooks'
import { selectCurrentFile, selectTree, setCurrentFile } from '@/store/ideSlice'
import { isEditable } from '@/utils/ide'
import { getExtension, getPathById } from '@/utils/entry'
import { ChevronRightIcon } from '@chakra-ui/icons'

type YorkieDoc = {
  content: Text
}

const EXTENSIONS: { [key: string]: LanguageSupport } = {
  py: python(),
  js: javascript(),
  c: cpp(),
  cpp: cpp(),
  java: java(),
}

interface Props {
  containerId: string | undefined
  category: string
  isOwner: boolean
  status: string
}

const CodeEditor = ({ containerId, category, isOwner, status }: Props) => {
  const editorRef = useRef<HTMLDivElement>(null)
  const codemirrorViewRef = useRef<EditorView>()

  const clientRef = useRef<Client>()
  const docRef = useRef<Document<YorkieDoc, Indexable>>()

  const tree = useAppSelector(selectTree)
  const currentFile = useAppSelector(selectCurrentFile)
  const dispatch = useAppDispatch()

  const [filePath, setFilePath] = useState<string[]>([])

  // 최신 저장본을 참조하기 위한 useRef
  const savedFileRef = useRef(
    tree?.find(node => node.id === currentFile?.id)?.metadata?.content
  )

  const language = getExtension(currentFile?.name!)

  const initializeYorkieEditor = useCallback(async () => {
    // 1. 클라이언트 생성 및 활성화
    const client = new yorkie.Client('https://api.yorkie.dev', {
      apiKey: import.meta.env.VITE_YORKIE_API_KEY,
    })

    clientRef.current = client
    await client.activate()

    // 2. 클라이언트와 연결된 문서 생성
    const doc = new yorkie.Document<YorkieDoc>(
      `File-${containerId}-${currentFile?.id}-${new Date()
        .toISOString()
        .substring(0, 10)
        .replace(/-/g, '')}`,
      {
        enableDevtools: true,
      }
    )

    docRef.current = doc
    await client.attach(doc, {})

    // 3. 해당 키의 문서에 content가 없으면 새로운 Text 생성
    doc.update(root => {
      if (!root.content) {
        root.content = new yorkie.Text()

        // 기존에 저장된 코드 삽입
        root.content.edit(0, 0, currentFile?.metadata?.content || '')
      }
    }, 'create content if not exists')

    // 4. 문서의 변경 이벤트 구독

    // 문서 내용을 CodeMirror 에디터에 동기화하는 함수
    const syncText = () => {
      const text = doc.getRoot().content

      codemirrorViewRef.current?.dispatch({
        changes: {
          from: 0,
          to: codemirrorViewRef.current.state.doc.length,
          insert: text.toString(),
        },
        annotations: [Transaction.remote.of(true)],
      })
    }

    doc.subscribe(event => {
      if (event.type === 'remote-change') {
        syncText()
      }
    })

    await client.sync()

    // 5. local의 변경 사항을 remote에 broadcast하기 위한 함수
    const updateListener = EditorView.updateListener.of(viewUpdate => {
      if (viewUpdate.docChanged) {
        for (const tr of viewUpdate.transactions) {
          const events = ['select', 'input', 'delete', 'move', 'undo', 'redo']
          if (!events.map(event => tr.isUserEvent(event)).some(Boolean)) {
            continue
          }
          if (tr.annotation(Transaction.remote)) {
            continue
          }
          let adj = 0
          tr.changes.iterChanges((fromA, toA, _, __, inserted) => {
            const insertText = inserted.toJSON().join('\n')
            doc.update(root => {
              root.content.edit(fromA + adj, toA + adj, insertText)
            }, `update content byA ${client.getID()}`)
            adj += insertText.length - (toA - fromA)

            doc.update(root => {
              dispatch(
                setCurrentFile({
                  ...currentFile!,
                  metadata: {
                    isDirectory: false,
                    content: root.content.toString(),
                  },
                })
              )
            })
          })
        }
      }
    })

    // 6. CodeMirror 인스턴스 생성

    // minimap 설정을 위한 코드
    const create = (_: EditorView) => {
      const dom = document.createElement('div')
      return { dom }
    }

    const state = EditorState.create({
      doc: doc.getRoot().content.toString(),
      extensions: [
        basicSetup,
        EXTENSIONS[language],
        githubLight,
        // minimap scrollbar
        showMinimap.compute(['doc'], _ => {
          return {
            create,
            /* optional */
            displayText: 'characters',
            showOverlay: 'always',
          }
        }),
        updateListener,
        EditorView.editable.of(isEditable(status, category, isOwner)),
        EditorState.readOnly.of(!isEditable(status, category, isOwner)),
      ],
    })

    codemirrorViewRef.current = new EditorView({
      state,
      parent: editorRef.current ? editorRef.current : undefined,
    })

    syncText()
  }, [containerId, currentFile, language])

  useEffect(() => {
    savedFileRef.current = tree?.find(
      node => node.id === currentFile?.id
    )?.metadata?.content
  }, [tree?.find(node => node.id === currentFile?.id)])

  useEffect(() => {
    setFilePath(getPathById(currentFile!.id!.toString(), tree!) || [])
  }, [currentFile])

  useEffect(() => {
    initializeYorkieEditor()

    // Cleanup function to destroy the editor when the component unmounts
    return () => {
      if (codemirrorViewRef.current) {
        codemirrorViewRef.current.destroy()
      }

      if (clientRef.current && docRef.current) {
        // 현재 저장된 결과를 yorkie에 반영한다.
        const savedContent = savedFileRef?.current

        docRef.current.update(root => {
          root.content.empty()
          root.content.edit(0, 0, savedContent || '')
        })

        clientRef.current.detach(docRef.current)
        clientRef.current.deactivate()
      }
    }
  }, [containerId, currentFile!.id])

  return (
    <Flex direction="column" w="full">
      <Box p={2} fontSize="sm">
        <Breadcrumb
          spacing="8px"
          separator={<ChevronRightIcon color="gray.500" />}
        >
          {filePath &&
            filePath.map(file => {
              return (
                <BreadcrumbItem key={file}>
                  <BreadcrumbLink cursor="default" textDecor="none">
                    {file}
                  </BreadcrumbLink>
                </BreadcrumbItem>
              )
            })}
        </Breadcrumb>
      </Box>
      <Box ref={editorRef} minH="calc(100vh - 285px)"></Box>
    </Flex>
  )
}

export default CodeEditor
