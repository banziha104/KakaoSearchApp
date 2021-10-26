# KakaoSearchApp

- ### [KDocs 문서](https://banziha104.github.io/KakaoSearchApp/docs/html/index.html)
  
<br>

## 테스트

<br>

- Unit Test API 관련 테스트
    - Data Layer
        - Service(Source) : 실제 요청 기반 테스트 
        - Repository : Mock 서비스 테스트 
    - Domain 
        -  UseCase : Mock 기반 테스트 
    - Presentation
        -  ViewModel : Mock 기반 테스트 
- UI 테스트는 Activity 기준으로 작성 

<br>

## 실행 

- Github 에서 Clone 하는 경우, 프로젝트 루트에 아래 secret.gradle 파일을 작성해주어야 동작합니다.
- [Kakao Developer](https://developers.kakao.com)

```groovy

// ./KakaoSearchApp/secret.gradle

ext {
    // [KAKAO_API_KEY]는 카카오 개발자 콘솔에서 받은 REST 키
    kakaoApiKey = '"KAKAO_API_KEY"' 
} 
```