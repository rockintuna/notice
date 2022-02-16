## 요구 기능

 - 공지사항
   - 등록 : 제목, 내용, 공지 시작일시, 공지 종료일시, 첨부파일(여러개)을 사용하여 공지사항 생성, 인증된 유저만 생성 가능
   - 수정 : 공지사항 ID를 통해 수정, 인증된 유저만 수정 가능
   - 삭제 : 공지사항 ID를 통해 제거, 인증된 유저만 제거 가능
   - 조회 : 공지사항 ID를 통해 공지의 제목, 내용, 등록일시, 조회수, 작성자를 조회, 모든 사용자가 조회 가능
 
 - 사용자
   - 등록 : 이메일, 이름, 비밀번호를 통해 사용자 생성
   - 로그인 : 이메일, 비밀번호를 통해 로그인
   
## 모델
요구 기능을 구현하기 위해 아래와 같은 모델과 속성이 필요함.
 - 공지사항
   - 제목
   - 내용
   - 공지 시작 일자
   - 공지 종료 일자
   - 등록 일자
   - 조회 수
   - 파일 목록
   - 작성자
 - 사용자
   - 이메일
   - 이름
   - 비밀번호
 - 파일 정보
   - 파일 이름
   - 파일 경로
   
## 구현

 - 공지사항 작성자 속성을 위해 계정 및 로그인 구현
   - [사용자 컨트롤러](https://github.com/rockintuna/notice/blob/main/src/main/java/me/rockintuna/notice/controller/UserController.java) 

 - REST API에 적합한 JWT를 이용하여 사용자 인증을 구현하였습니다.
   - [JWT 인증 필터](https://github.com/rockintuna/notice/blob/main/src/main/java/me/rockintuna/notice/configuration/JwtAuthenticationFilter.java)
   - [JWT 관리 클래스](https://github.com/rockintuna/notice/blob/main/src/main/java/me/rockintuna/notice/configuration/JwtTokenProvider.java)
   
 - 요청으로부터 파일과 json을 함께 받기 위해 MultiPartFile 및 @RequestPart 어노테이션을 사용하였습니다.
   - [공지사항 컨트롤러](https://github.com/rockintuna/notice/blob/main/src/main/java/me/rockintuna/notice/controller/NoticeController.java)

 - 예외 처리를 위해 @ExceptionHandler 어노테이션 사용
   - [글로번 컨트롤러](https://github.com/rockintuna/notice/blob/main/src/main/java/me/rockintuna/notice/exception/GlobalController.java)

 - 테스트 코드
   - build-check-operate 패턴을 통해서 보기 쉽게 작성 
     - [사용자 컨트롤러 테스트 코드](https://github.com/rockintuna/notice/blob/main/src/test/java/me/rockintuna/notice/controller/UserControllerTest.java)
   - 테스트 내에서 setter를 사용하지 않고 필드를 변경하기 위해 power mock 사용
   ```
   //power mock
    testImplementation 'org.powermock:powermock-module-junit4:2.0.4'
    testImplementation 'org.powermock:powermock-api-mockito2:2.0.4'
   ```
   
## 실행 방법

 - application.properties의 file.destination 프로퍼티에 파일이 저장될 위치를 지정합니다.

 - 프로젝트 디렉토리에서 gradle을 통해 jar로 빌드합니다.
```bash
./gradlew build
```
 - jar를 실행합니다.
```bash
java -jar ./build/libs/notice-0.0.1-SNAPSHOT.jar
```

## 요청 테스트
 - 회원 가입 
   - username, email, password에 적당한 값을 넣고 요청합니다.
```
curl --location --request POST 'http://localhost:8080/api/user' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username":"tester",
    "email":"tester@test.com",
    "password":"password"
}'
```

 - 로그인
   - 회원가입에 사용한 email, password를 통해 Access Token을 발급받습니다.
```
curl --location --request POST 'http://localhost:8080/api/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email":"tester@test.com",
    "password":"password"
}'
```

- 공지사항 등록
  - 로그인으로부터 발급받은 Access Token의 앞에 'Bearer '를 추가한 문자열을 Authorization 헤더에 담아서 요청합니다.
  - files에는 현재 경로의 여러 파일을 0개 이상 추가할 수 있습니다.
  - noticePostRequest에는 공지사항에 대한 데이터를 json 형식으로 입력합니다. 
```
curl --location --request POST 'http://localhost:8080/api/notice' \
--header 'Authorization: Bearer accessToken' \
--form 'files=@"file1.txt"' \
--form 'files=@"file2.txt"' \
--form 'noticePostRequest="{
    \"title\":\"title\",
    \"content\":\"content\",
    \"startedDate\":\"2022-01-10T12:00:00\",
    \"endDate\":\"2022-02-20T12:00:00\"
}";type=application/json'
```

- 공지사항 조회
  - 생성된 공지사항의 ID를 추가한 URL을 통해 조회합니다.
```
curl --location --request GET 'http://localhost:8080/api/notice/3'
```

- 공지사항 수정
  - 변경할 내용과 accessToken이 필요합니다. 
```
curl --location --request PUT 'http://localhost:8080/api/notice/3' \
--header 'Authorization: Bearer accessToken' \
--header 'Content-Type: application/json' \
--data-raw '{
    "title":"title2",
    "content":"content2",
    "startedDate":"2022-02-10T12:00:00",
    "endDate":"2022-03-20T12:00:00"
}'
```

- 공지사항 제거
  - accessToken이 필요합니다.
```
curl --location --request DELETE 'http://localhost:8080/api/notice/3' \
--header 'Authorization: Bearer accessToken'
```
