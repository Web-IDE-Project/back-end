import React from 'react'
import {
  Button,
  Flex,
  FormControl,
  FormErrorMessage,
  Image,
  Input,
  FormLabel,
  useToast,
} from '@chakra-ui/react'
import logo from '../../assets/images/logo.png'
import { useForm, SubmitHandler, FieldError } from 'react-hook-form'
import { useNavigate } from 'react-router-dom'
import { signup } from '@/services/user'

interface SignUpFormFields {
  username: string
  nickname: string
  email: string
  password: string
  confirmPassword: string
}

const ERROR_MESSAGES = {
  REQUIRED: '이 필드는 필수입니다.',
  USERNAME_INVALID: '아이디는 알파벳 대소문자, 숫자, 밑줄(_) 또는 점(.)만 허용됩니다.',
  USERNAME_LENGTH: '아이디는 최소 5자에서 최대 15자까지 가능합니다.',
  EMAIL_INVALID: '유효한 이메일을 입력해주세요.',
  NICKNAME_INVALID: '닉네임은 한글, 알파벳 대소문자, 숫자만 허용됩니다.',
  NICKNAME_LENGTH: '닉네임은 최소 2자에서 최대 20자까지 가능합니다.',
  PASSWORD_INVALID: '비밀번호는 알파벳, 숫자, 특수 문자를 포함해야 합니다.',
  PASSWORD_LENGTH: '비밀번호는 최소 8자에서 최대 20자까지 가능합니다.',
  PASSWORD_MATCH: '비밀번호가 일치하지 않습니다.',
}

const SignUp: React.FC = () => {
  const navigate = useNavigate()
  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<SignUpFormFields>()

  const toast = useToast()

  const onSubmit: SubmitHandler<SignUpFormFields> = async data => {
    const response = await signup(
      data.username,
      data.password,
      data.nickname,
      data.email
    )

    if (response.success) {
      navigate('/login')
    } else {
      toast({
        title: '회원가입에 문제가 발생하였습니다.',
        description: response.error,
        position: 'top-right',
        status: 'error',
        duration: 3000,
        isClosable: true,
      })
    }
  }

  const password = watch('password')

  return (
    <>
      <Flex w="full" h="100vh" bg="gray.50" align="center" justify="center">
        <Flex
          flexDir="column"
          bg="white"
          p={6}
          borderRadius={10}
          align="center"
          maxW="500px"
          w="full"
        >
          <Image src={logo} boxSize="150px" objectFit="contain" mb={5} />
          <form onSubmit={handleSubmit(onSubmit)} style={{ width: '100%' }}>
            <FormControl
              isInvalid={!!(errors['username'] as FieldError)}
              mb={6}
            >
              <FormLabel htmlFor="username">아이디</FormLabel>
              <Input
                {...register('username', {
                  required: ERROR_MESSAGES.REQUIRED,
                  pattern: {
                    value: /^[a-zA-Z0-9._]{5,15}$/,
                    message: ERROR_MESSAGES.USERNAME_INVALID,
                  },
                  minLength: {
                    value: 5,
                    message: ERROR_MESSAGES.USERNAME_LENGTH,
                  },
                  maxLength: {
                    value: 15,
                    message: ERROR_MESSAGES.USERNAME_LENGTH,
                  },
                })}
                id="username"
                type="text"
                placeholder="아이디"
                focusBorderColor="green.400"
              />
              <FormErrorMessage>
                {(errors['username'] as FieldError)?.message}
              </FormErrorMessage>
            </FormControl>

            <FormControl isInvalid={!!(errors['email'] as FieldError)} mb={6}>
              <FormLabel htmlFor="email">이메일</FormLabel>
              <Input
                {...register('email', {
                  required: ERROR_MESSAGES.REQUIRED,
                  pattern: {
                    value: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/,
                    message: ERROR_MESSAGES.EMAIL_INVALID,
                  },
                })}
                id="email"
                type="email"
                placeholder="3ever@example.com"
                focusBorderColor="green.400"
              />
              <FormErrorMessage>
                {(errors['email'] as FieldError)?.message}
              </FormErrorMessage>
            </FormControl>

            <FormControl
              isInvalid={!!(errors['nickname'] as FieldError)}
              mb={6}
            >
              <FormLabel htmlFor="nickname">닉네임</FormLabel>
              <Input
                {...register('nickname', {
                  required: ERROR_MESSAGES.REQUIRED,
                  pattern: {
                    value: /^[가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9]{2,20}$/,
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
                id="nickname"
                type="text"
                placeholder="닉네임"
                focusBorderColor="green.400"
              />
              <FormErrorMessage>
                {(errors['nickname'] as FieldError)?.message}
              </FormErrorMessage>
            </FormControl>

            <FormControl
              isInvalid={!!(errors['password'] as FieldError)}
              mb={2}
            >
              <FormLabel htmlFor="password">비밀번호</FormLabel>
              <Input
                {...register('password', {
                  required: ERROR_MESSAGES.REQUIRED,
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
                id="password"
                type="password"
                placeholder="비밀번호"
                focusBorderColor="green.400"
              />
              <FormErrorMessage>
                {(errors['password'] as FieldError)?.message}
              </FormErrorMessage>
            </FormControl>

            <FormControl
              isInvalid={!!(errors['confirmPassword'] as FieldError)}
              mb={12}
            >
              <Input
                {...register('confirmPassword', {
                  required: ERROR_MESSAGES.REQUIRED,
                  validate: value =>
                    value === password || ERROR_MESSAGES.PASSWORD_MATCH,
                })}
                id="confirmPassword"
                type="password"
                placeholder="비밀번호 확인"
                focusBorderColor="green.400"
              />
              <FormErrorMessage>
                {(errors['confirmPassword'] as FieldError)?.message}
              </FormErrorMessage>
            </FormControl>

            <Button
              type="submit"
              w="full"
              bg="green.400"
              color="white"
              _hover={{ bg: 'green.500' }}
              variant="solid"
              mb={4}
            >
              회원가입
            </Button>
          </form>
        </Flex>
      </Flex>
    </>
  )
}

export default SignUp
