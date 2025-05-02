# 🎵 마이 플레이리스트 – 나만의 플레이리스트 만들기

**마이 플레이리스트**는 유튜브 영상을 활용해 자신만의 커스터마이징 플레이리스트를 생성하고 공유할 수 있는 웹 애플리케이션입니다.

---

## 📌 핵심 기능

- YouTube 영상들을 활용해 나만의 플레이리스트를 구성하고 커스터마이징할 수 있는 웹 서비스
- 제목, 아티스트, 썸네일, URL 등을 자유롭게 수정 가능
- (예정) 플레이리스트 공유 기능

---

## 🛠️ 기술 스택

| 영역 | 사용 기술 |
|------|-----------|
| 백엔드 | Spring Boot (Java), MySQL |
| 프론트엔드 | Thymeleaf, Bootstrap |

---

## 🧪 개발 과정

### 1. 데이터 모델링
- 요구 사항 분석 → 테이블 정의 → 정규화/반정규화

![1 요구사항 분석](https://github.com/user-attachments/assets/461e52d5-481a-4b6b-a201-bba3e6ad0c81)
![2 스키마 설계](https://github.com/user-attachments/assets/01d7809c-25c8-4c5c-9199-b478998b5b78)
![3 데이터 모델링](https://github.com/user-attachments/assets/cf510393-43c3-4dd2-a48e-8c69092863e9)
![4 ERD](https://github.com/user-attachments/assets/05f2a770-5e1d-4be7-b8b3-fd3b61a3528c)


### 2. UI 설계 (Mockup)

![5 목업](https://github.com/user-attachments/assets/a74c5383-aad3-45ed-afa7-dbd2b1748049)


### 3. 소셜 로그인
- Google OAuth2.0 (Authorization Code Grant) 적용
- JWT 기반 인증 구현

![6 소셜 로그인](https://github.com/user-attachments/assets/496de565-7468-4635-a362-c3e5cd69f3f8)


### 4. 프론트엔드 구성 및 구현 화면

![7 메인 화면](https://github.com/user-attachments/assets/61516404-b650-41d6-bcd8-747b9787264a)
![8 빈 플레이리스트 화면](https://github.com/user-attachments/assets/9fdaffdf-0580-4162-9932-704ce22a410e)
![9 플레이리스트 화면](https://github.com/user-attachments/assets/2e965d44-2670-4723-b22d-2ddb4c32c5a1)
![10 플레이리스트 가져오기](https://github.com/user-attachments/assets/5c27067d-8f43-46d3-93e7-f5657ad23668)
![11 유튜브 검색 및 노래 추가](https://github.com/user-attachments/assets/794201ce-eafb-4873-809a-186d76375488)
![12 노래 추가 설정](https://github.com/user-attachments/assets/dcbdeaa5-c126-4849-81d6-12cf2f3955c7)
![13 노래 이름 바꾸기](https://github.com/user-attachments/assets/52d5def1-0e80-46a8-8735-277eee70b73d)
![14 노래 URL 바꾸기](https://github.com/user-attachments/assets/89d4e78b-b700-4c46-9b24-9083a8d7b7c9)


---

## 🎯 학습 및 성장

### ✅ Spring Boot & MVC 아키텍처 이해
- 컨트롤러/서비스/레포지토리 구조 학습
- DI 개념 체득

### ✅ OAuth2 & JWT 인증
- 인증 흐름 전체 구현 경험
- 보안 및 세션 관리에 대한 이해 심화

### ✅ 프레임워크 디버깅
- Spring Security 내부 OAuth 과정 중 구글링을 통해 알 수 없는 문제 발견
- 공식 문서, 코드 디버깅을 통한 분석 경험 축적

### ✅ UI/UX 구성
- Thymeleaf, Bootstrap을 활용한 웹 구현 - Admin 페이지 구현 가능

---

## 👤 팀 구성
- 개인 프로젝트
