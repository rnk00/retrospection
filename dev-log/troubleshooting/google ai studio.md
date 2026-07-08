# Google AI Studio API 키 인증 및 계정 권한 이슈

## 문제 상황

Spring Boot 프로젝트에서 Google AI Studio(Gemini API)를 연동하기 위해 API 키를 발급받아 사용했다.

개인 Gmail 계정으로 발급한 API 키는 정상적으로 AI 응답을 반환했지만, **대학교 Google 계정**으로 발급한 API 키는 API 호출 시 다음과 같은 오류가 발생했다.

- `API_KEY_INVALID`
- `401 Unauthorized`
- **"프로젝트 할당량 등급을 사용할 수 없습니다."**

동일한 코드와 요청 방식이었음에도 계정에 따라 결과가 달라지는 상황이었다.

---

## 원인 분석

### Google AI Studio API 키 발급

Google AI Studio에서는 프로젝트를 생성한 뒤 해당 프로젝트에서 API 키를 발급받아 Gemini API를 사용할 수 있다.

또한 **Dashboard → Rate limits(비율 제한)** 메뉴에서 모델별 호출 제한(Quota)과 사용량을 확인할 수 있다.

Spring Boot에서는 일반적으로 `application.yml`에 API 정보를 다음과 같이 설정한다.

```yaml
google:
  ai:
    api-key: { 발급받은 API Key }
    model: { 사용할 모델명 }
```

예를 들어

```yaml
google:
  ai:
    api-key: AIza...
    model: gemini-2.5-flash
```

처럼 설정하여 사용한다.

---

### 대학교 계정에서 오류가 발생한 이유

대학교 계정은 일반 Gmail이 아닌 **Google Workspace for Education** 환경이다.

이 경우 학교 관리자가 다음 기능을 제한할 수 있다.

- Google AI Studio 사용
- Gemini API 사용
- Google Cloud 프로젝트 생성
- API 할당량(Quota) 사용

따라서 **API 키는 정상적으로 생성되더라도 실제 API를 호출할 권한이 없어 인증 오류(401)나 API_KEY_INVALID 오류가 발생할 수 있다.**

즉, 문제는 코드가 아니라 **계정 권한 정책**이었다.

---

## 해결 방법

### 1. 개인 Gmail 계정 사용

개인 Google 계정에서 프로젝트를 생성하고 API 키를 발급받아 사용하면 정상적으로 API 호출이 가능했다.

### 2. 학교 계정을 사용해야 하는 경우

학교에서 제공하는 Google Workspace 계정을 사용해야 한다면,

Google Workspace 관리자에게

- Google AI Studio 사용 권한
- Gemini API 사용 권한
- API Quota 사용 권한

을 요청해야 한다.

---

## 결과

- 개인 Gmail 계정의 API 키를 사용했을 때 Gemini API가 정상적으로 호출되었다.
- 코드 수정 없이 계정만 변경하여 문제가 해결되었으며, 원인이 애플리케이션이 아닌 **Google Workspace의 권한 정책**임을 확인했다.

---

## 회고

처음에는 API URL이나 요청 형식, Spring Boot 설정 문제를 의심했지만, 실제 원인은 **계정별 권한 정책**이었다.

API 연동 시에는 코드뿐만 아니라 **API 키의 발급 주체와 계정 권한**도 반드시 확인해야 한다는 점을 배웠다. 특히 Google Workspace 계정은 일반 Gmail 계정과 동일하게 동작하지 않을 수 있으므로, 인증 오류가 발생한다면 **API 키 자체보다 계정의 권한과 Quota 설정을 먼저 확인하는 것이 문제 해결에 효과적**이다.
