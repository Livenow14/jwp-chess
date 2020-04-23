# java-chess
체스 게임 구현을 위한 저장소

## 기능 요구사항

- 콘솔 UI에서 체스 게임을 할 수 있는 기능을 구현한다.
- 체스판에서 말의 위치 값은 가로 위치는 왼쪽부터 a ~ h이고, 세로는 아래부터 위로 1 ~ 8로 구현한다.
- 체스판에서 각 진영은 검은색(대문자)과 흰색(소문자) 편으로 구분한다.
- `move source위치 target위치`을 실행해 이동한다.
- 한쪽의 King이 잡혔을 때 게임을 종료해야 한다.
- "status" 명령을 입력하면 각 진영의 점수를 출력하고 어느 진영이 이겼는지 결과를 볼 수 있어야 한다.

### 점수 계산 규칙

- 체스 프로그램에서 현재까지 남아 있는 말에 따라 점수를 계산할 수 있어야 한다.
- 각 말의 점수는 queen은 9점, rook은 5점, bishop은 3점, knight는 2.5점이다.
- pawn의 기본 점수는 1점이다. 하지만 같은 세로줄에 같은 색의 폰이 있는 경우 1점이 아닌 0.5점을 준다.
- king은 잡히는 경우 경기가 끝나기 때문에 점수가 없다.

```
> 체스 게임을 시작합니다.
> 게임 시작 : start
> 게임 종료 : end
> 게임 이동 : move source위치 target위치 - 예. move b2 b3
start
RNBQKBNR
PPPPPPPP
........
........
........
........
pppppppp
rnbqkbnr

move b2 b3
RNBQKBNR
PPPPPPPP
........
........
........
.p......
p.pppppp
rnbqkbnr
```

## 구현해야할 기능 목록
- [x] 메인 메뉴 출력
    - [x] start or end 입력
- [x] 기본 체스판 출력
    - [x] 체스판 생성
- [x] move (from to) or status 입력
    - [x] move 입력 시 입력 형식 검사
        - [x] 기물 이동
            - [x] 출발지 기물 존재 여부 검사
            - [x] 이동 가능여부 검사
                - [x] 이동 경로 중 장애물 검사
                    - [x] 이동 경로 생성
                - [x] 목적지에 아군 여부 검사
                - [x] 목적지가 선택 기물의 이동 가능 범위인지 검사
                    - [x] 기물별 이동 범위 생성
        - [x] 킹 사망 시 게임 종료
    - [x] status 입력 시 점수 출력
        - [x] 각 진영 별 점수 계산
            - [x] 세로줄 내 폰 복수 존재 시 점수 계산
        - [x] 계산된 점수로 승패 판결