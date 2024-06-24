import { Flex, Spinner, Text } from '@chakra-ui/react'

const Loading = () => {
  return (
    <Flex
      minW="100%"
      minH="100%"
      bgColor="gray.100"
      direction="column"
      justify="center"
      align="center"
    >
      <Spinner
        thickness="4px"
        speed="0.65s"
        emptyColor="gray.300"
        color="green.500"
        size="xl"
        mb={10}
      />
      <Text fontWeight="bold" fontSize="lg">
        컨테이너를 불러오는 중입니다...
      </Text>
    </Flex>
  )
}

export default Loading
