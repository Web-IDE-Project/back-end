import { ArrowLeftIcon } from '@chakra-ui/icons'
import {
  Avatar,
  Box,
  Divider,
  Flex,
  IconButton,
  Image,
  Spacer,
  Text,
} from '@chakra-ui/react'
import { NavLink, useNavigate } from 'react-router-dom'
import Logo from '@/assets/images/logo.png'
import { logout } from '@/services/user'
import {
  logout as logoutAction,
  selectNickname,
  selectProfileUrl,
} from '@/store/userSlice'
import { useDispatch } from 'react-redux'
import { useAppSelector } from '@/hooks'

const Sidebar = ({ toggleSidebar }: { toggleSidebar: () => void }) => {
  const categories = [
    { name: '내 컨테이너', path: '/container/my' },
    { name: '강의 컨테이너', path: '/container/lecture' },
    { name: '질문 컨테이너', path: '/container/question' },
  ]

  const navigate = useNavigate()

  const nickname = useAppSelector(selectNickname)
  const profileUrl = useAppSelector(selectProfileUrl)

  const onSidebarOpenButton = () => {
    toggleSidebar()
  }

  const dispatch = useDispatch()
  const onClickLogout = async () => {
    try {
      const response = await logout()
      if (response.success) {
        dispatch(logoutAction())
        navigate('/')
      } else {
        alert('로그아웃에 실패했습니다. 잠시 후 다시 시도해주세요.')
        console.error('Error:', response.error)
      }
    } catch (error) {
      alert('로그아웃에 실패했습니다. 잠시 후 다시 시도해주세요.')
      console.error('Error:', error)
    }
  }

  return (
    <Flex direction="column" p={4} h="full" position="fixed" width="250px">
      <Flex align="center">
        <Image width="80px" src={Logo} alt="3Ever" objectFit="contain" />
        <Spacer />
        <IconButton
          aria-label="Hide sidebar"
          icon={<ArrowLeftIcon />}
          bg="transparent"
          onClick={onSidebarOpenButton}
        />
      </Flex>
      <Flex align="center" py={8} pl={2} gap={2}>
        <Avatar src={profileUrl || ''} />
        <Text fontSize="md" as="b">
          {nickname}
        </Text>
      </Flex>
      <Divider orientation="horizontal" mb={3} />
      {categories.map((category, index) => {
        return (
          <NavLink key={index} to={category.path}>
            {({ isActive }) => (
              <Box
                w="full"
                h={55}
                as="button"
                bg={isActive ? 'gray.100' : ''}
                _hover={{ bg: 'gray.100' }}
                mt={2}
                borderRadius="xl"
                transition="background-color 0.3s"
              >
                <Flex p={4}>
                  <Text fontSize="md" mr={2}>
                    {category.name}
                  </Text>
                </Flex>
              </Box>
            )}
          </NavLink>
        )
      })}
      <Spacer />
      <Divider orientation="horizontal" mb={3} />
      <Flex direction="column">
        <Box
          w="full"
          h={55}
          as="button"
          _hover={{ bg: 'gray.100' }}
          mt={2}
          borderRadius="xl"
          transition="background-color 0.3s"
        >
          <Flex
            p={4}
            onClick={() => {
              navigate('setting')
            }}
          >
            <Text fontSize="md">마이페이지</Text>
          </Flex>
        </Box>
        <Box
          w="full"
          h={55}
          as="button"
          _hover={{ bg: 'gray.100' }}
          mt={2}
          borderRadius="xl"
          transition="background-color 0.3s"
        >
          <Flex p={4} onClick={onClickLogout}>
            <Text fontSize="md">로그아웃</Text>
          </Flex>
        </Box>
      </Flex>
    </Flex>
  )
}

export default Sidebar
