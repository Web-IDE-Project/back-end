import { PlusSquareIcon } from '@chakra-ui/icons'
import {
  Button,
  Divider,
  Flex,
  FormControl,
  FormLabel,
  Input,
  Text,
  Box,
  Avatar,
  useToast,
} from '@chakra-ui/react'
import React, { useEffect, useRef, useState } from 'react'
import { useForm, SubmitHandler } from 'react-hook-form'
import Modal from '@/components/Modal'
import { useAppSelector } from '@/hooks'
import {
  selectId,
  selectNickname,
  selectProfileUrl,
  setNickName,
  setProfileUrl,
} from '@/store/userSlice'
import { checkLoginStatus, checkPassword, editUserInfo } from '@/services/user'
import { useDispatch } from 'react-redux'

interface FormValues {
  nickname: string
  currentPassword: string
  newPassword: string | null
  confirmNewPassword: string | null
}

const ERROR_MESSAGES = {
  REQUIRED: '이 필드는 필수입니다.',
  NICKNAME_INVALID: '닉네임은 한글, 알파벳 대소문자, 숫자만 허용됩니다.',
  NICKNAME_LENGTH: '아이디는 최소 2자에서 최대 20자까지 가능합니다.',
  PASSWORD_INVALID: '비밀번호는 알파벳, 숫자, 특수 문자를 포함해야 합니다.',
  PASSWORD_LENGTH: '비밀번호는 최소 8자에서 최대 20자까지 가능합니다.',
  PASSWORD_MATCH: '비밀번호가 일치하지 않습니다.',
}

const MyPage = () => {
  const dispatch = useDispatch()
  const username = useAppSelector(selectId)
  const [profileImage, setProfileImage] = useState({
    profileImage: null as File | null,
    previewProfileImage: useAppSelector(selectProfileUrl) || '',
  })
  const fileInputRef = useRef<HTMLInputElement | null>(null)
  const [changePassword, setChangePassword] = useState(false)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [errorPassword, setErrorPassword] = useState(false)
  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors },
  } = useForm<FormValues>({
    defaultValues: {
      nickname: useAppSelector(selectNickname),
      currentPassword: '',
      newPassword: null,
      confirmNewPassword: null,
    },
  })
  const [isSocialLogined, setIsSocialLogined] = useState<boolean>(false)
  const toast = useToast()

  useEffect(() => {
    const socials: string[] = ['naver ', 'kakao ', 'google ']
    const isSocialLogin: boolean = socials.some(social =>
      username.startsWith(social)
    )
    setIsSocialLogined(isSocialLogin)
  }, [])

  const onSubmit: SubmitHandler<FormValues> = async data => {
    const requestData = {
      nickname: data.nickname || null,
      password: data.newPassword || null,
    }

    const response = await editUserInfo(requestData, profileImage.profileImage)

    if (response.success) {
      const userStatus = await checkLoginStatus()

      if (userStatus.success) {
        const userInfo = userStatus.data?.userInfo

        if (userInfo) {
          dispatch(setNickName(userInfo.nickname))
          dispatch(
            setProfileUrl(`http://localhost:8080${userInfo.awsS3SavedFileURL}`)
          )
        } else {
          toast({
            title: '유저 정보를 불러오는데 오류가 발생했습니다.',
            position: 'top-right',
            status: 'error',
            duration: 3000,
            isClosable: true,
          })
        }
      }

      toast({
        title: '유저 정보가 변경되었습니다.',
        position: 'top-right',
        status: 'success',
        duration: 3000,
        isClosable: true,
      })
    } else {
      toast({
        title: '유저 정보 수정에 문제가 발생하였습니다.',
        description: response.error,
        position: 'top-right',
        status: 'error',
        duration: 3000,
        isClosable: true,
      })
    }

    setChangePassword(false)
  }

  const handleOpenModal = () => {
    setIsModalOpen(true)
    setValue('currentPassword', '')
  }

  const handleCloseModal = () => {
    setIsModalOpen(false)
    setValue('currentPassword', '')
  }

  const handleConfirm = async () => {
    const response = await checkPassword(watch('currentPassword'))

    if (response.success) {
      setChangePassword(true)
      setErrorPassword(false)
    } else {
      setErrorPassword(true)
    }
    setIsModalOpen(false)
  }

  const changeProfileImage = (e: React.ChangeEvent<HTMLInputElement>) => {
    const profileImageFile = e.target.files?.[0]
    if (profileImageFile) {
      const reader = new FileReader()
      reader.onload = () => {
        setProfileImage({
          profileImage: profileImageFile,
          previewProfileImage: reader.result as string,
        })
      }
      reader.readAsDataURL(profileImageFile)
    }
  }

  const deleteProfileImage = () => {
    setProfileImage({
      profileImage: null,
      previewProfileImage: '',
    })
  }

  return (
    <Flex flexDir="column" p={16} align="center">
      <Flex
        flexDir="column"
        w={600}
        h="full"
        as="form"
        onSubmit={handleSubmit(onSubmit)}
      >
        <Flex flexDir="column" align="center" position="relative">
          <Input
            type="file"
            ref={fileInputRef}
            onChange={changeProfileImage}
            accept="image/png, image/jpeg, image/jpg"
            display="none"
          />
          <Box
            position="relative"
            onClick={() => fileInputRef.current?.click()}
            cursor="pointer"
            _hover={{
              '& img': {
                filter: 'brightness(0.7)',
                transition: 'filter 0.3s',
              },
              '& svg': {
                filter: 'brightness(0.7)',
                transition: 'filter 0.3s',
              },
            }}
          >
            <Avatar
              boxSize={200}
              src={profileImage.previewProfileImage}
              mb={4}
            />
            <PlusSquareIcon
              boxSize={8}
              color="gray.600"
              position="absolute"
              bottom={6}
              right={3}
            />
          </Box>
          <Button
            colorScheme="red"
            variant="outline"
            onClick={() => deleteProfileImage()}
          >
            이미지 삭제
          </Button>
        </Flex>

        <Divider marginY={8} />

        <Flex my={4}>
          <Text fontWeight="500" mr={4}>
            아이디
          </Text>
          <Text>{username}</Text>
        </Flex>

        <FormControl my={4}>
          <Flex align="center">
            <FormLabel htmlFor="nickname" mb="0" mr={4}>
              닉네임
            </FormLabel>
            <Input
              id="nickname"
              type="text"
              {...register('nickname', {
                required: ERROR_MESSAGES.REQUIRED,
                pattern: {
                  value: /^[가-힣a-zA-Z0-9]{2,20}$/,
                  message: ERROR_MESSAGES.NICKNAME_INVALID,
                },
                minLength: {
                  value: 2,
                  message: ERROR_MESSAGES.NICKNAME_LENGTH,
                },
                maxLength: {
                  value: 20,
                  message: ERROR_MESSAGES.NICKNAME_LENGTH,
                },
              })}
              focusBorderColor="green.400"
              flex={1}
            />
          </Flex>
          {errors.nickname && (
            <Text color="tomato">{errors.nickname.message}</Text>
          )}
        </FormControl>

        {!isSocialLogined && (
          <Box my={4}>
            <Flex justifyContent="space-between" align="center">
              <Text fontWeight="500">비밀번호</Text>
              {!changePassword && (
                <Button
                  colorScheme="green"
                  variant="outline"
                  onClick={handleOpenModal}
                >
                  비밀번호 변경
                </Button>
              )}
              {changePassword && (
                <Button
                  colorScheme="red"
                  variant="outline"
                  onClick={() => {
                    setChangePassword(false)
                    setValue('newPassword', null)
                    setValue('confirmNewPassword', null)
                  }}
                >
                  취소
                </Button>
              )}
              <Modal
                isOpen={isModalOpen}
                onClose={handleCloseModal}
                title="현재 비밀번호를 입력하세요"
                cancelMessage="취소"
                confirmMessage="확인"
                confirmCallback={handleConfirm}
                confirmButtonColorScheme="green"
              >
                <Input
                  {...register('currentPassword')}
                  type="password"
                  placeholder="비밀번호"
                  focusBorderColor="green.400"
                />
              </Modal>
            </Flex>
            {errorPassword && (
              <Text color="tomato">입력하신 비밀번호가 올바르지 않습니다.</Text>
            )}
          </Box>
        )}

        {changePassword && (
          <FormControl my={4}>
            <Box mb={2}>
              <FormLabel htmlFor="newPassword">새 비밀번호</FormLabel>
              <Input
                id="newPassword"
                type="password"
                {...register('newPassword', {
                  pattern: {
                    value:
                      /^(?=.*[a-zA-Z])(?=.*[~!@#$%^&*()_+-=])(?=.*[0-9]).{8,15}$/,
                    message: ERROR_MESSAGES.PASSWORD_INVALID,
                  },
                  minLength: {
                    value: 8,
                    message: ERROR_MESSAGES.PASSWORD_LENGTH,
                  },
                  maxLength: {
                    value: 20,
                    message: ERROR_MESSAGES.PASSWORD_LENGTH,
                  },
                })}
                focusBorderColor="green.400"
                flex={1}
              />
              {errors.newPassword && (
                <Text color="tomato">{errors.newPassword.message}</Text>
              )}
            </Box>

            <Box>
              <FormLabel htmlFor="confirmNewPassword">
                새 비밀번호 확인
              </FormLabel>
              <Input
                id="confirmNewPassword"
                type="password"
                {...register('confirmNewPassword', {
                  validate: value =>
                    value === watch('newPassword') ||
                    ERROR_MESSAGES.PASSWORD_MATCH,
                })}
                focusBorderColor="green.400"
                flex={1}
              />
              {errors.confirmNewPassword && (
                <Text color="tomato">{errors.confirmNewPassword.message}</Text>
              )}
            </Box>
          </FormControl>
        )}

        <Flex flexGrow={1} />

        <Button colorScheme="green" type="submit">
          유저 정보 변경하기
        </Button>
      </Flex>
    </Flex>
  )
}

export default MyPage
