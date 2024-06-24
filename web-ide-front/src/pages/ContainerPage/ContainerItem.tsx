import Modal from '@/components/Modal'
import { useAppSelector } from '@/hooks'
import { Container } from '@/models/container'
import { selectId } from '@/store/userSlice'
import { DeleteIcon, SettingsIcon } from '@chakra-ui/icons'
import {
  Avatar,
  Badge,
  Button,
  Card,
  CardFooter,
  CardHeader,
  Flex,
  Heading,
  IconButton,
  Input,
  Select,
  Spacer,
  Text,
  Textarea,
  useDisclosure,
} from '@chakra-ui/react'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

interface Props extends Container {
  category: string
  onEditInfoButtonClick: (
    id: string,
    title: string,
    desc: string,
    sharingOption: string
  ) => void
  onDeleteButtonClick: (id: string) => void
}

const CATEGORY: { [key: string]: string } = {
  '내 컨테이너': 'MY',
  '강의 컨테이너': 'LECTURE',
  '질문 컨테이너': 'QUESTION',
}

const ContainerItem = ({
  id,
  title,
  language,
  description,
  nickname,
  username,
  awsS3SavedFileUrl,
  category,
  status,
  onEditInfoButtonClick,
  onDeleteButtonClick,
}: Props) => {
  const navigate = useNavigate()

  const [newTitle, setNewTitle] = useState(title)
  const [newDesc, setNewDesc] = useState(description)
  const [sharingOption, setSharingOption] = useState(CATEGORY[category])
  const currentUserId = useAppSelector(selectId)

  const { isOpen, onOpen, onClose } = useDisclosure()
  const {
    isOpen: isDeleteModalOpen,
    onOpen: onDeleteModalOpen,
    onClose: onDeleteModalClose,
  } = useDisclosure()

  const modalClose = () => {
    setNewTitle(title)
    setNewDesc(description)
    setSharingOption('MY')
    onClose()
  }

  const statusText = () => {
    if (category === '강의 컨테이너' && status === 'COMPLETE') {
      return '종료'
    }

    if (category === '질문 컨테이너' && status === 'SOLVE') {
      return '해결'
    }

    return ''
  }

  const settingVisibility = () => {
    if (category === '내 컨테이너') {
      return true
    } else {
      if (status === 'DEFAULT' && username === currentUserId) {
        return true
      }
    }

    return false
  }

  const executeText = () => {
    if (status === 'SOLVE' || status === 'COMPLETE') {
      return '코드 보기'
    } else if (category === '강의 컨테이너' || category === '질문 컨테이너') {
      return '참여'
    }

    return '실행'
  }

  return (
    <>
      <Card
        minH={280}
        borderColor={
          !status ? 'none' : status !== 'DEFAULT' ? 'green.500' : 'none'
        }
        borderWidth={0.5}
      >
        <CardHeader>
          <Flex align="center">
            <Heading size="md">{title}</Heading>
            <Spacer />
            <Flex visibility={settingVisibility() ? 'visible' : 'hidden'}>
              <IconButton
                aria-label="Settings"
                bg="transparent"
                size="sm"
                icon={<SettingsIcon />}
                onClick={onOpen}
              />
              <IconButton
                aria-label="Delete Container"
                bg="transparent"
                size="sm"
                icon={<DeleteIcon />}
                onClick={onDeleteModalOpen}
              />
            </Flex>
            <Text color="green">{statusText()}</Text>
          </Flex>
          <Badge colorScheme="green" size="sm" mt={2}>
            {language}
          </Badge>
        </CardHeader>
        {/* Card Body */}
        <Text px={5}>{description}</Text>
        <Spacer />
        <Flex align="center" px={5} pt={3} display={nickname ? 'flex' : 'none'}>
          <Spacer />
          <Text fontSize="sm" pr={1}>
            {nickname}
          </Text>
          <Avatar size="sm" src={awsS3SavedFileUrl || ''} />
        </Flex>
        {/* Card Body */}
        <CardFooter pt={3}>
          <Button
            w="100%"
            size="sm"
            variant="outline"
            onClick={() => {
              navigate(`/container/${id}/workspace`, {
                state: {
                  category: category,
                  ownerId: username,
                  title: title,
                  status: status,
                },
              })
            }}
          >
            {executeText()}
          </Button>
        </CardFooter>
      </Card>
      <Modal
        isOpen={isOpen}
        onClose={modalClose}
        title="컨테이너 정보 수정"
        cancelMessage="취소"
        confirmMessage="수정"
        confirmCallback={() => {
          onEditInfoButtonClick(id.toString(), newTitle, newDesc, sharingOption)
          modalClose()
        }}
      >
        <Text fontWeight="bold" mb={1}>
          이름
        </Text>
        <Input
          placeholder="컨테이너 이름을 입력해주세요."
          size="sm"
          borderRadius="md"
          mb={4}
          value={newTitle}
          onChange={e => setNewTitle(e.target.value)}
          isInvalid={newTitle === ''}
        />
        <Flex align="center">
          <Text mb={1}>
            <b>설명</b>(선택)
          </Text>
          <Spacer />
          <Text fontSize="sm" color="gray">
            {description.length < 60 ? description.length : 60}
            /60 자
          </Text>
        </Flex>
        <Textarea
          value={newDesc}
          onChange={e => setNewDesc(e.target.value)}
          placeholder="컨테이너 설명을 입력해주세요."
          size="sm"
          borderRadius="md"
          mb={4}
          resize="none"
          maxLength={60}
          isInvalid={newDesc === ''}
        />
        <Text mb={1}>공유 설정</Text>
        <Select
          size="sm"
          borderRadius="md"
          value={sharingOption}
          onChange={e => setSharingOption(e.target.value)}
        >
          <option value="MY">
            비공개(나의 컨테이너에서만 확인 가능합니다.)
          </option>
          <option value="LECTURE">강의 컨테이너에 공개</option>
          <option value="QUESTION">질문 컨테이너에 공개</option>
        </Select>
      </Modal>
      <Modal
        isOpen={isDeleteModalOpen}
        onClose={onDeleteModalClose}
        title="정말로 삭제하시겠습니까?"
        cancelMessage="취소"
        confirmMessage="삭제"
        confirmCallback={() => {
          onDeleteButtonClick(id.toString())
          onDeleteModalClose()
        }}
        confirmButtonColorScheme="red"
      ></Modal>
    </>
  )
}

export default ContainerItem
