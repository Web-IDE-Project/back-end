import { ApiResponse } from '@/models/ApiData.ts'
import { loginResponse } from '@/models/user.ts'
import { AxiosResponse } from 'axios'
import API from './API.ts'

/** 회원가입 API */
export async function signup(
  username: string,
  password: string,
  nickname: string,
  email: string
): Promise<ApiResponse<loginResponse>> {
  try {
    const response: AxiosResponse = await API.post(`/api/auth/register`, {
      username: username,
      password: password,
      nickname: nickname,
      email: email,
    })

    return {
      success: true,
      data: response.data,
    }
  } catch (err: any) {
    return {
      success: false,
      error:
        err.response?.data?.message || err.message || 'Unknown error occurred',
    }
  }
}

/** 로그인 API */
export async function login(
  username: string,
  password: string
): Promise<ApiResponse<loginResponse>> {
  try {
    const response: AxiosResponse = await API.post(`/api/auth/login`, {
      username: username,
      password: password,
    })

    return {
      success: true,
      data: response.data,
    }
  } catch (err: any) {
    return {
      success: false,
      error:
        err.response?.data?.message || err.message || 'Unknown error occurred',
    }
  }
}

/** 로그아웃 API */
export async function logout(): Promise<ApiResponse<loginResponse>> {
  try {
    const response: AxiosResponse = await API.post(`/api/auth/logout`)

    return {
      success: true,
      data: response.data,
    }
  } catch (err: any) {
    return {
      success: false,
      error:
        err.response?.data?.message || err.message || 'Unknown error occurred',
    }
  }
}

/** 유저 로그인 확인 API */
export async function checkLoginStatus(): Promise<ApiResponse<loginResponse>> {
  try {
    const response: AxiosResponse = await API.get(`/api/auth/status`)

    return {
      success: true,
      data: response.data,
    }
  } catch (err: any) {
    return {
      success: false,
      error:
        err.response?.data?.message || err.message || 'Unknown error occurred',
    }
  }
}

/** 유저 정보 수정 API */
export async function editUserInfo(
  updateMemberRequestDTO: { nickname: string | null; password: string | null },
  profileImage: File | null
): Promise<ApiResponse<{ result?: string; message: string }>> {
  try {
    const formData = new FormData()
    formData.append(
      'updateMemberRequestDTO',
      new Blob([JSON.stringify(updateMemberRequestDTO)], {
        type: 'application/json',
      })
    )
    formData.append('profileImage', profileImage || 'null')

    const response: AxiosResponse = await API.put(`/api/member`, formData)

    return {
      success: true,
      data: response.data,
    }
  } catch (err: any) {
    return {
      success: false,
      error:
        err.response?.data?.message || err.message || 'Unknown error occurred',
    }
  }
}

/** 유저 비밀번호 일치 확인 API */
export async function checkPassword(
  password: string
): Promise<ApiResponse<{ result: string; message: string }>> {
  try {
    const response: AxiosResponse = await API.post(`/api/auth/password`, {
      password: password,
    })

    return {
      success: true,
      data: response.data,
    }
  } catch (err: any) {
    return {
      success: false,
      error:
        err.response?.data?.message || err.message || 'Unknown error occurred',
    }
  }
}
