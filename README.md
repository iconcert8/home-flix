# home-flix

Let's watch videos of home computers everywhere.

![다운로드](https://github.com/iconcert8/home-flix/issues/1#issue-1516081768)

PC에 있는 동영상을 웹으로 접근하여 시청할 수 있도록 하는 서버

## 실행 방법

1. application.yml 수정

```
server.
    port: 원하는 포트 수정
jwt.
    sercret: secret 값 수정(32자 이상). 그냥 빌드 하면 보안에 취약.  
```  

2. java -jar 명령어를 통해 실행(Gradle을 통해 빌드를 했다는 가정)한다.
3. 브라우저 http://localhost:port/ 이동한다.
4. 로그인 창이 뜨면, [id: admin, pw: 1234] 입력. 로그인 되는지 확인한다.
5. jar파일의 경로에 home-flix-users.json 파일 생성되었는지 확인한다.
6. home-flix-users.json파일을 열어 원하는 id, pw로 변경 혹은 추가한다.

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

7. jar파일이 있는 폴더를 root로 하고 하위 폴더를 검색하여 video 파일을 탐색하도록 되어있다. 그렇기 때문에 jar파일의 위치를 원하는 폴더에 위치 시켜야한다.
