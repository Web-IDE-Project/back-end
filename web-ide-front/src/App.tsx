import { Navigate, Route, Routes, useNavigate } from 'react-router-dom'
import LandingPage from './pages/LandingPage/LandingPage'
import LoginPage from './pages/LoginPage/LoginPage'
import SignUpPage from './pages/SignUpPage/SignUpPage'
import OauthLoginHandler from './pages/LoginPage/OauthLoginHandler'
import ContainerPage from './pages/ContainerPage/ContainerPage'
import ContainerList from './pages/ContainerPage/ContainerList'
import IDEPage from './pages/IDEPage/IDEPage'
import { useEffect } from 'react'
import Setting from './pages/MyPage/MyPage'
import { useAppDispatch } from './hooks'
import { checkLoginStatus } from './services/user'
import { login } from '@/store/userSlice'

function App() {
  const navigate = useNavigate()
  const dispatch = useAppDispatch()

  useEffect(() => {
    const isLogined = async () => {
      try {
        const response = await checkLoginStatus()
        if (response.success && response.data) {
          const userInfo = response.data.userInfo
          dispatch(
            login({
              id: userInfo.username,
              nickname: userInfo.nickname,
              profileUrl: userInfo.awsS3SavedFileURL,
            })
          )
          navigate('/container/my')
        } else {
          navigate('/')
        }
      } catch (error) {
        console.error('Error:', error)
      }
    }
    isLogined()
  }, [])

  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/signup" element={<SignUpPage />} />
      <Route path="/login/oauth/callback" element={<OauthLoginHandler />} />
      <Route path="/container" element={<ContainerPage />}>
        <Route index element={<Navigate to="my" />} />
        <Route path="my" element={<ContainerList category="내 컨테이너" />} />
        <Route
          path="lecture"
          element={<ContainerList category="강의 컨테이너" />}
        />
        <Route
          path="question"
          element={<ContainerList category="질문 컨테이너" />}
        />
        <Route path="setting" element={<Setting />} />
      </Route>
      <Route path="/container/:containerId/workspace" element={<IDEPage />} />
    </Routes>
  )
}

export default App
