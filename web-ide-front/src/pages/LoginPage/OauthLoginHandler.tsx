import { useAppDispatch } from '@/hooks'
import { checkLoginStatus } from '@/services/user'
import { login } from '@/store/userSlice'
import { Flex, Text } from '@chakra-ui/react'
import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'

const OauthLoginHandler: React.FC = () => {
  const navigate = useNavigate()
  const params = new URLSearchParams(location.search)
  const dispatch = useAppDispatch()

  useEffect(() => {
    const handleOauthLogin = async () => {
      if (params.get('isSuccess') === 'true') {
        const response = await checkLoginStatus()

        if (response.success) {
          const userInfo = response.data?.userInfo!
          dispatch(
            login({
              id: userInfo.username,
              nickname: userInfo.nickname,
              profileUrl: userInfo.awsS3SavedFileURL,
            })
          )
        }
        // 유저 정보 확인 API에 요청해서 정보 받아오기
        navigate('/container/my')
      } else if (params.get('isSuccess') === 'false') {
        alert(params.get('error'))
        navigate('/login')
      } else {
        alert('요청이 잘못되었습니다.')
        navigate('/login')
      }
    }

    handleOauthLogin()
  }, [params])

  return (
    <Flex align="center" justify="center" h="full">
      <Text>로그인 중...</Text>
    </Flex>
  )
}

export default OauthLoginHandler
