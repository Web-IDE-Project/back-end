export function isEditable(status: string, category: string, isOwner: boolean) {
  // 상태가 종료/해결일 경우
  if (status === 'SOLVE' || status === 'COMPLETE') {
    return false
  }

  // 내 컨테이너인 경우
  if (category === 'MY') {
    return true
  } else if (category === 'LECTURE') {
    // 강의 컨테이너인 경우
    if (isOwner) return true
    else return false
  } else if (category === 'QUESTION') {
    // 질문 컨테이너인 경우
    return true
  }

  return false
}
