# EventBus 아키텍처 패턴 실습 정리

## 1) 시스템 요구사항 소개

### System A
- `CourseID`를 입력받아 과목 삭제 기능 제공
- `StudentID`를 입력받아 학생 삭제 기능 제공
- 결과(성공/실패)를 `ClientOutput`에 표시

### System B
- `CourseID`와 `StudentID`를 입력받아 수강 신청 기능 제공
- 아래 조건에서 수강 신청 거부
  1. 존재하지 않는 학생
  2. 존재하지 않는 과목
  3. 선수과목 미이수

## 2) System A, B 아키텍처 설명

### 공통 아키텍처
- `ClientInputMain`: 사용자 입력을 Event로 발행(Publisher)
- `RMIEventBusImpl`: 모든 구독 컴포넌트에 Event 브로드캐스트
- `StudentMain`, `CourseMain`: Event 구독(Subscriber) 후 처리
- `ClientOutputMain`: `ClientOutput` 이벤트 메시지 출력

### Event 설계
- `DeleteStudents`: 학생 삭제 요청
- `DeleteCourses`: 과목 삭제 요청
- `EnrollCourse`: 수강 신청 요청 (`"<courseId> <studentId>"`)

### 처리 흐름
1. 사용자가 메뉴에서 기능 선택
2. `ClientInputMain`이 요청 Event 발행
3. 대상 컴포넌트가 Event 처리
4. 결과를 `ClientOutput` Event로 재발행
5. `ClientOutputMain`이 결과를 콘솔에 출력

## 3) 구현 소스코드/출력 설명

### 구현 포인트
- `ClientInputMain`
  - 메뉴에 삭제/수강신청 항목 추가
- `StudentComponent`, `CourseComponent`
  - `find...ById`, `delete...` 유틸 메서드 추가
- `CourseMain`
  - `DeleteCourses` 처리 추가
- `StudentMain`
  - `DeleteStudents` 처리 추가
  - `EnrollCourse` 처리 추가 (학생/과목 존재 여부 + 선수과목 검증)
  - `RegisterCourses`, `DeleteCourses` 수신 시 내부 course 캐시 동기화
- `EventId`
  - `EnrollCourse` 추가

### 예시 출력
- 삭제 성공: `The selected student(20100123) is deleted.`
- 삭제 실패: `The selected course(99999) does not exist.`
- 수강 신청 성공: `Enrollment succeeded: student(20100125) -> course(17654).`
- 수강 신청 실패: `Enrollment failed: prerequisite(17651) is not completed.`

## 4) Event-Bus 패턴 장단점

### 장점
- 컴포넌트 간 결합도 감소(발행자/구독자 분리)
- 기능 확장 용이(새 이벤트/구독자 추가 쉬움)
- 비동기 처리 구조에 적합

### 단점
- 이벤트 흐름 추적/디버깅이 어려움
- 브로드캐스트 기반이라 불필요한 수신이 발생 가능
- 이벤트 스키마(메시지 포맷) 관리가 느슨하면 런타임 오류 가능

## 5) 대체 가능한 패턴

- **Mediator 패턴**: 중앙 조정자가 컴포넌트 상호작용 제어
- **Observer 패턴**: 이벤트 구독/통지에 초점, 단일 프로세스 내에 주로 적합
- **Command 패턴 + 큐**: 요청을 명령 객체로 캡슐화해 실행/재시도/로깅 관리
- **Request/Response (RPC/REST)**: 즉시 응답 필요 시 명시적 호출 구조가 유리

---

## 발표 순서(10분 발표 + 5분 Q&A) 제안
1. 요구사항(System A/B) 소개
2. EventBus 기반 아키텍처 다이어그램/흐름 설명
3. 구현 핵심 코드와 콘솔 출력 시연
4. EventBus 적용 시 장단점 분석
5. 대체 패턴(Mediator/Observer/Command/RPC) 비교
