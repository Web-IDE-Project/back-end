export interface userInfo {
  username: string
  nickname: string
  awsS3SavedFileURL: string
}

export interface loginResponse {
  userInfo: userInfo
  message: string
}
