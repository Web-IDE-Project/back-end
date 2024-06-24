![header](https://capsule-render.vercel.app/api?type=waving&height=250&color=48BB78&text=3Ever%20-%20Web%20IDE&textBg=false&fontColor=ffffff&fontSize=40&fontAlign=50&fontAlignY=39&section=header)

# 3Ever

## Web IDE Project - Backend

- 개발 기간 : 2024/5/30 ~ 2024/6/24

<br><br>

## 배포 주소

> [3Ever](http://ec2-3-34-144-78.ap-northeast-2.compute.amazonaws.com:8080/)

<br><br>

## 프로젝트 소개
3Ever는 누구든, 언제든, 어디서든 강의자가 문제를 해결하는 과정을 실시간으로 보여주고, 수강생의 코딩하는 사고도 기를 수 있도록 도와주는 라이브코딩 웹 IDE 서비스입니다.

<br><br>

## 멤버 소개
| <img src="https://avatars.githubusercontent.com/u/147473025?v=4" alt="공태현" width="150" height="150"> | <img src="https://avatars.githubusercontent.com/u/131665874?v=4" alt="김근호" width="150" height="150"> | <img src="https://avatars.githubusercontent.com/u/159746126?v=4" alt="이다솜" width="150" height="150"> | <img src="https://avatars.githubusercontent.com/u/120402129?v=4" alt="최민지" width="150" height="150"> |
|:----------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------:|
|                              [공태현(BE)](https://github.com/runtime-zer0)                              |                               [김근호(BE)](https://github.com/geunhokinn)                               |                              [이다솜(BE)](https://github.com/serahissomi)                               |                                [최민지(BE)](https://github.com/meanzi3)                                 |

<br><br>

## 역할 및 담당 기능

| 이름  | 공태현                                                                | 김근호                                                                         | 이다솜                                                              | 최민지                                                               |
| --- | ------------------------------------------------------------------ | --------------------------------------------------------------------------- | ---------------------------------------------------------------- | ---------------------------------------------------------------- |
| 역할  | 백엔드 리더                                                        | 팀원                                                                        | 팀원                                                           | 팀원                                                           |
| 기능  | * 백엔드 CI/CD 구축<br /> * ERD 설계 및 구현<br /> * AWS 인프라 구축 및 배포<br /> * 로그인 및 유저 관리, 언어별 독립적인 컴파일 환경 구축, 터미널 로직 구현, 파일 시스템 구현 | * 폴더 및 디렉토리 생성, 수정, 저장, 삭제 기능<br />* 컨테이너 공유, 상태 수정, 삭제 기능 | * 회원 정보 수정 기능<br />* 컨테이너 생성/실행 기능<br />* 파일 시스템 구현 | * 채팅/터미널 기능 웹소켓 연결, 채팅 저장 구현<br />* 음성 채팅 시그널링 서버 구현 |

<br><br>

## 주요기능

### 소셜 로그인
일반 회원가입, 로그인 기능 제공과 동시에 소셜 로그인 기능을 제공하여 사용자는 별도의 회원가입과 로그인 과정을 거치지 않고도 카카오, 네이버, 구글 계정을 통해 서비스를 이용할 수 있습니다.
Spring Security, OAuth2를 이용하였으며 인증된 사용자의 상태는 세션을 통해 유지하고, 세션 유효성 검사를 통해 자동 로그인 기능을 구현하였습니다.

### 컨테이너 생성, 실행
사용자는 컨테이너의 이름, 설명을 입력하고 언어를 선택하여 컨테이너를 생성하고 실행할 수 있습니다. 컨테이너 실행 시에는 생성 시 선택했던 언어의 기본 템플릿을 제공합니다.

### 파일 및 폴더 생성
사용자는 파일 및 폴더를 생성할 수 있습니다. 계층 구조를 구현하여 폴더 내에는 여러 개의 파일 및 폴더를 생성할 수 있습니다.

### 컴파일 및 실행
선택한 언어를 통해 도커 컨테이너 이미지를 생성하여 다양한 언어를 컴파일 하고 실행할 수 있습니다. (Java, Javascript, Python, C, CPP 지원)

### 터미널
사용자는 컨테이너 내에서 터미널 기능을 이용할 수 있습니다. 웹소켓 서버를 이용하여 실시간으로 입력한 터미널 명령의 결과를 얻을 수 있습니다.

### 컨테이너 공유 및 상태 수정
사용자는 컨테이너를 강의 또는 질문 컨테이너로 공유할 수 있고, 공유된 컨테이너에는 다른 사용자들이 참여할 수 있습니다.
공유 상태인 컨테이너를 완료 또는 해결 상태로 수정하면 모든 사용자는 해당 컨테이너의 내용을 확인할 수 있습니다.

### 실시간 동시 편집
강의 컨테이너에 접속한 사용자들은 강의자의 코드 편집을 실시간으로 확인할 수 있습니다. 질문 컨테이너에 접속한 사용자들은 코드 편집을 실시간으로 확인은 물론, 동시 편집이 가능합니다.  

### 채팅
공유된 컨테이너에 참여한 사용자들은 실시간 채팅을 이용할 수 있습니다. 웹소켓 서버를 이용하여 실시간으로 채팅을 주고 받을 수 있습니다. 


<br><br>

## 아키텍쳐
![image](https://github.com/meanzi3/Programmers/assets/120402129/9c412f53-857f-4d11-a5cf-5e9387e92928)

<br>

### 백엔드 주요 기술 스택
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"><img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">
<img src="https://img.shields.io/badge/hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white">
<img src="https://img.shields.io/badge/springdatajpa-6DB33F?style=for-the-badge&logo=springdatajpa&logoColor=white">
<img src="https://img.shields.io/badge/qeurydsl-1572B6?style=for-the-badge&logo=querydsl&logoColor=white">
<img src="https://img.shields.io/badge/mariadb-003545?style=for-the-badge&logo=mariadb&logoColor=white">
<img src="https://img.shields.io/badge/WebSockets-E9711C?style=for-the-badge&logo=websocket&logoColor=white">
<img src="https://img.shields.io/badge/STOMP-FF9900?style=for-the-badge&logo=stomp&logoColor=white">
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">
<img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white">
<img src="https://img.shields.io/badge/Amazon RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white">
<img src="https://img.shields.io/badge/Amazon S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white">

<br><br>

### ERD
![image](https://github.com/meanzi3/Programmers/assets/120402129/c8c8c0cd-c8e5-423f-a300-3f1e10051273)

<br><br>

## 시연 영상
[보러가기](https://www.canva.com/design/DAGI4FX3p20/xFEiQMyan_C39bIc9s2HEA/watch?utm_content=DAGI4FX3p20&utm_campaign=designshare&utm_medium=link&utm_source=editor)

<br><br>

![footer](https://capsule-render.vercel.app/api?type=waving&height=150&color=48BB78&section=footer)
