import {
  Avatar,
  Flex,
  IconButton,
  Spacer,
  Text,
  useDisclosure,
  useToast,
} from '@chakra-ui/react'
import { VscFiles } from 'react-icons/vsc'
import { VscTerminal } from 'react-icons/vsc'
import { ArrowBackIcon } from '@chakra-ui/icons'
import { useNavigate } from 'react-router-dom'
import { useAppDispatch, useAppSelector } from '@/hooks'
import {
  selectShowExplorer,
  selectShowTerminal,
  toggleExplorer,
  toggleTerminal,
} from '@/store/ideSlice'
import { selectProfileUrl } from '@/store/userSlice'
import Modal from '@/components/Modal'
import { editContainerStatus } from '@/services/container'

interface Props {
  containerId: string | undefined
  category: string
  isOwner: boolean
  status: string
}

const Tab = ({ containerId, category, isOwner, status }: Props) => {
  const navigate = useNavigate()

  const showTerminal = useAppSelector(selectShowTerminal)
  const showExplorer = useAppSelector(selectShowExplorer)
  const profileUrl = useAppSelector(selectProfileUrl)

  const dispatch = useAppDispatch()

  const { isOpen, onOpen, onClose } = useDisclosure()

  const toast = useToast()

  const handleBackButtonClick = () => {
    if (!isOwner) {
      navigate('/container')
    } else if (category === 'LECTURE' || category === 'QUESTION') {
      if (status === 'DEFAULT') {
        onOpen()
      } else {
        navigate('/container')
      }
    }
  }

  const onSolveQuestion = async () => {
    const response = await editContainerStatus(containerId!, 'solve')

    if (response.success) {
      toast({
        title: '해당 질문 컨테이너가 해결 상태로 바뀌었습니다.',
        position: 'top-right',
        status: 'success',
        duration: 3000,
        isClosable: true,
      })
    } else {
      toast({
        title: '컨테이너 상태 변경에 문제가 발생하였습니다.',
        description: `${response.error}`,
        position: 'top-right',
        status: 'error',
        duration: 3000,
        isClosable: true,
      })
    }

    onClose()
    navigate('/container')
  }

  const onCompleteLecture = async () => {
    const response = await editContainerStatus(containerId!, 'complete')

    if (response.success) {
      toast({
        title: '해당 강의 컨테이너가 종료 상태로 바뀌었습니다.',
        position: 'top-right',
        status: 'success',
        duration: 3000,
        isClosable: true,
      })
    } else {
      toast({
        title: '컨테이너 상태 변경에 문제가 발생하였습니다.',
        description: `${response.error}`,
        position: 'top-right',
        status: 'error',
        duration: 3000,
        isClosable: true,
      })
    }
    onClose()
    navigate('/container')
  }

  return (
    <>
      <Flex
        direction="column"
        p={2}
        gap={2}
        align="center"
        borderRight="1px"
        borderColor="gray.200"
      >
        <IconButton
          aria-label="explorer"
          icon={<VscFiles />}
          fontSize="20px"
          bgColor={showExplorer ? 'gray.100' : 'transparent'}
          onClick={() => {
            dispatch(toggleExplorer())
          }}
        />
        <IconButton
          aria-label="terminal"
          icon={<VscTerminal />}
          fontSize="20px"
          bgColor={showTerminal ? 'gray.100' : 'transparent'}
          onClick={() => {
            dispatch(toggleTerminal())
          }}
        />
        <Spacer />
        <Avatar size="sm" src={profileUrl || ''} />
        <IconButton
          aria-label="back"
          icon={<ArrowBackIcon />}
          fontSize="20px"
          onClick={handleBackButtonClick}
        />
      </Flex>
      <Modal
        isOpen={isOpen}
        onClose={() => {
          onClose()
          navigate('/container')
        }}
        title={
          category === 'LECTURE'
            ? '강의를 종료하시겠어요?'
            : '질문이 해결되셨나요?'
        }
        cancelMessage="아니오"
        confirmMessage="네"
        confirmCallback={
          category === 'LECTURE' ? onCompleteLecture : onSolveQuestion
        }
      >
        <Text>
          {category === 'LECTURE'
            ? '강의 종료 후에는 해당 컨테이너의 코드 편집이 불가능합니다.'
            : '질문 해결 후에는 해당 컨테이너의 코드 편집이 불가합니다.'}
        </Text>
      </Modal>
    </>
  )
}

export default Tab
