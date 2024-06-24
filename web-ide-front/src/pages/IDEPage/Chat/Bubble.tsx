import { Box, Center, Flex, Text, Avatar } from '@chakra-ui/react'
import { useAppSelector } from '@/hooks'
import { selectId, selectProfileUrl } from '@/store/userSlice'

interface BubbleProps {
  messageType: 'TALK' | 'ENTER' | 'EXIT'
  message: string
  senderName: string
  isHighlighted: boolean
  senderId: string
}

const Bubble: React.FC<BubbleProps> = ({
  messageType,
  message,
  senderName,
  senderId,
  isHighlighted,
}) => {
  const profileUrl: string = useAppSelector(selectProfileUrl) || ''
  const userId = useAppSelector(selectId)

  if (messageType === 'ENTER' || messageType === 'EXIT') {
    return (
      <Center my={3}>
        <Text fontSize="small">{message}</Text>
      </Center>
    )
  }
  if (senderId === userId) {
    return (
      <Box
        bg={isHighlighted ? 'yellow.200' : 'green.100'}
        p={2}
        fontSize="small"
        w="fit-content"
        borderRadius={6}
        alignSelf="end"
        maxW="70%"
      >
        <Text>{message}</Text>
      </Box>
    )
  }
  return (
    <Flex maxW="70%">
      <Avatar src={profileUrl} boxSize="40px" mr={2} mt={1} />
      <Box>
        <Text fontSize="small" fontWeight="500">
          {senderName}
        </Text>
        <Box
          bg={isHighlighted ? 'yellow.200' : 'gray.200'}
          p={2}
          fontSize="small"
          w="fit-content"
          borderRadius={6}
        >
          <Text>{message}</Text>
        </Box>
      </Box>
    </Flex>
  )
}

export default Bubble
