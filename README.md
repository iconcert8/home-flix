![HOMEFLIX](https://user-images.githubusercontent.com/51566869/210201171-55c8770c-4348-49de-9c7c-b14ece64f9f9.png)

# home-flix

Let's watch videos of home computers everywhere.

PC에 있는 동영상을 웹으로 접근하여 시청할 수 있도록 하는 서버

## Environment

- Java 11
- JWT
- Thymeleaf

## Folder Structure

```
main - java
        - /**
            - /login                #Login service with JWT
            - /video                #Video service
            - HomeFlixApplication   #Main method
            - HomeFlixController    #Controller
            - ScreenController      #Page(screen) controller
            - VideoController       #Video service controller
            - LoginController       #Login service controller
            - WebConfig             #Web configuration(CORS, ..)
            - WebSecurityConfig     #Security configuration(JWT, allow urls, pwd encoder, ..)
     - resource
        - /static                   #Image, CSS, favicon ..
        - /templates                #Thymeleaf template
        - application.yml           #Application configuration
        - logback-spring.xml        #Log configuration
```

## JWT flow

- WebSecurityConfig에 설정에 의하여 **login/JwtAuthenticationFilter가 모든 요청에 앞서 호출** 된다.
- login/JwtAuthenticationFilter는 **JWT를 검사**하고, **request에 결과를 담아 넘긴다.**
- login/JwtAspect는 request에 담겨 있는 JWT 결과를 확인, **재인증이 요구되는 요청에 대해 로그인 페이지로 강제 이동** 시킨다.

## Page(screen)

### login

- 최초 로그인 (username, password)는 **(admin, 1234)이다.**
- 실행파일이 있는 경로에 **home-flix-users.json을** 수정하여 (username, password)를 관리할 수 있다.
  <img width="553" alt="homeflix-login" src="https://user-images.githubusercontent.com/51566869/210688263-5827d558-8f3b-4185-a8cc-c37969327adc.png">

### list

- **실핼파일(.jar)의 경로를 root**로 잡고 하위 파일들을 보여준다.
- **폴더**와 **동영상(mp4, mkv)파일**만 보여준다.
  <img width="554" alt="homflix-videos" src="https://user-images.githubusercontent.com/51566869/210688444-c6c9476d-860e-4a65-be48-9cee9bd885a3.png">

### video stream

- 브라우저에 따라, mkv 확장자 동영상은 실행이 안될 수 있다. **mp4로 인코딩을 추천**한다.
- 자막 파일은 **동영상 파일과 동일한 이름**으로 ".vtt" 확장자로 변환 필요. **동영상 파일 경로**에 위치하면 된다.

```
subtitle file(.vtt)
- /dir
    - video-name.mp4
    - video-name.vtt
```

<img width="551" alt="homflix-video" src="https://user-images.githubusercontent.com/51566869/210688498-35acfb77-de67-4225-9fba-fc8ff2d3cd14.png">

## Quick Start

1. application.yml 수정

```
server.
    port: 원하는 포트 수정
jwt.
    sercret: secret 값 수정(32자 이상). 그냥 빌드 하면 보안에 취약.  
```  

2. **gradle build**
3. **java -jar** 명령어를 통해 실행한다.
4. 브라우저 **http://localhost:port/** 이동한다.
5. 로그인 창이 뜨면, **[id: admin, pw: 1234]** 입력. 로그인 되는지 확인한다.
6. jar파일의 경로에 **home-flix-users.json** 파일 생성되었는지 확인한다.
7. home-flix-users.json파일을 열어 원하는 id, pw로 변경 혹은 추가한다.

```
[
  // 변경하거나, json array에 추가.
  {
    "username": "admin", 
    "password": "1234"
  },
  {
    "username": ..., 
    "password": ...
  }
]
```

8. **jar파일이 있는 폴더를 root**로 하고 하위 폴더를 검색하여 video 파일을 탐색하도록 되어있다. 그렇기 때문에 jar파일의 위치를 원하는 폴더에 위치 시켜야한다.
