## Lombok Annotation 인식 오류 해결

### 문제 상황

Eclipse 환경에서 `@Getter`, `@Setter`, `@Data` 등 Lombok 어노테이션이 정상적으로 동작하지 않는 문제가 발생했다.

* 코드상에서는 Lombok 의존성이 정상적으로 추가되어 있었음
* 프로젝트 빌드도 가능했지만 Eclipse IDE에서 Lombok 어노테이션을 인식하지 못해 컴파일 오류 및 자동완성 미지원 발생

### 원인 분석

Lombok 설치 과정에서 Eclipse 실행 파일명을 기준으로 IDE를 식별하는데, 현재 Eclipse 실행 파일명이 기본값인 `eclipse.exe`가 아닌 `SpringBootsForEclipse.exe`로 되어있었다.

Lombok installer가 해당 실행 파일을 정상적인 Eclipse 환경으로 인식하지 못해 Lombok plugin 적용이 제대로 되지 않은 것으로 판단했다.

### 해결 방법

1. 실행, 설정 파일명 변경

   SpringBootsForEclipse.exe → eclipse.exe
   SpringBootsForEclipse.ini → eclipse.ini
   

2. Lombok installer 재실행 및 Eclipse 재시작

3. Lombok 정상 인식 확인 후 원래 이름으로 복구

   eclipse.exe → SpringBootsForEclipse.exe
   eclipse.ini → SpringBootsForEclipse.exe

### 결과

* Eclipse에서 Lombok 어노테이션 정상 인식
* `@Getter`, `@Setter` 등 자동 생성 코드 정상 동작

### 회고

Lombok 자체 문제라기보다 Eclipse 실행 파일명을 변경하면서 Lombok installer가 IDE를 제대로 식별하지 못한 환경 설정 문제였다.
Eclipse 기반 IDE에서 Lombok 적용 오류 발생 시 실행 파일명(`eclipse.exe`) 및 Lombok 설치 상태를 우선 확인하는 것이 좋다.