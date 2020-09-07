## Pact Samples

이 프로젝트는 Consumer Driven Contracts를 돕는 도구인 [pact](https://pact.io)의
사용법을 이해하기 위한 샘플 프로젝트이다.

이 샘플은 `frontend`, `server1`, `server2` 세 컴포넌트로 구성된다. node.js로
작성된 `frontend`와 스프링을 이용하여 작성된 `server1`이 역시 스프링으로 작성된
`server2`의 API를 호출하며, 이 과정에서 `frontend`와 `server1`은 자신들의
요구사항(pacts)을 `server2`에게 전달하고 이것이 만족되면 배포한다. `server2`는
`frontend`와 `server1`의 pacts를 만족시키고 그 사실을 보고한다.

```
frontend ---> server2
server1  ---> server2
```

### 준비

#### Pact Broker CLI 설치

pacts가 준수되어 배포가 가능한지 확인하려면 pact-broker cli를 설치해야한다.

    sudo gem install pact_broker-client

#### Pact Broker API 키

API consumer(API를 사용하는 쪽)와 API provider(API를 제공하는 쪽) 사이의 계약을
Pact Broker를 통해 관리하므로, Pact Broker 에 접근하기 위한 설정을 해야한다.

다음 파일에서 `<pact-broker-token>` 값을
https://tossbank.pactflow.io/settings/api-tokens 에서 발급해주는 Read/write
token (CI) 토큰으로 변경한다.

* frontend/publish.js
* server1/build.gradle.kts
* server2/build.gradle.kts
* server2/src/test/resources/application.yml

`./init-broker-token.sh` 스크립트로 변경할도 수 있다. 예를 들어 token이 abc1234라면:

    ./init-broker-token.sh abcd1234

(주의: 이 스크립트는 BSD sed 호환성 이슈로 인해 리눅스에서는 동작하지 않는다)

### Workflow

#### 1. Frontend 의 pact 작성 및 발행

`frontend` 디렉토리로 이동한다.

`frontend` 가 pact를 발행한다.

    npm run pactPublish

위에서 실행한 `pactPublish` 스크립트는 `package.json`에 정의되어있는대로, `rimraf pacts`를 실행하여 기존에 로컬에 생성한 pacts를 모두 삭제하고, `mocha`를 실행하여 테스트 케이스를 실행하여 그 내용대로 `pacts` 디렉토리 밑에 pacts를 생성한다. 그리고 `node publish.js`를 실행하여 pacts를 pact broker로 발행한다.

`frontend`가 발행한 pacts가 모두 만족된(verified) 상태라면 `frontend`를 배포하도 괜찮을 것이다. 이를 확인하기 위해 다음의 명령을 실행한다.

    pact-broker can-i-deploy --broker-base-url=tossbank.pactflow.io -k '<your-broker-token>' --pacticipant=frontend --latest

위 명령은 `tossbank.pactflow.io`에 올라온 pacts 중에서 참여자가 최신 버전의 `frontend` 인 것이 verified 되었는지 확인하고, 그렇다면 배포해도 괜찮다고 출력한다. 하지만 아직 만족되지 않았을 것이므로 배포할 수 없다고 나올 것이다.

#### 2. Server1의 pact 작성 및 발행

`server1` 디렉토리로 이동한다.

`server1` 이 pact를 발행한다.

    ./gradlew test pactPublish

위에서 `test` 태스크를 실행하면 테스트를 실행하는데, 미리 준비된 테스트가 pact를 생성하는 테스트이므로 `build/pacts` 디렉토리에 pacts가 생성된다. 뒤이어 `pactPublish` 태스크가 이 생성된 pacts를 broker로 발행한다.

`frontend` 때와 마찬가지로 `pact-broker` 의 `can-i-deploy` 명령으로 배포 가능한지 확인할 수 있다.

    pact-broker can-i-deploy --broker-base-url=tossbank.pactflow.io -k '<your-broker-token>' --pacticipant=server1 --latest

#### 3. Server2가 pact를 지키는지 verification

`server2` 디렉토리로 이동한다.

`server2` 서버를 띄운다.

    ./gradlew bootRun

위에서 띄운 서버가 계약을 만족하는지 확인하고, 그 결과를 pact broker로 보고한다.

    ./gradlew pactVerify -Ppact.verifier.publishResults=true

#### 4. frontend와 server1이 배포 가능함

이제 frontend와 server1 모두가 배포 가능하다. 다음을 실행하여 확인 가능하다.

    pact-broker can-i-deploy --broker-base-url=tossbank.pactflow.io -k '<your-broker-token>' --pacticipant=frontend --latest
    pact-broker can-i-deploy --broker-base-url=tossbank.pactflow.io -k '<your-broker-token>' --pacticipant=server1 --latest
