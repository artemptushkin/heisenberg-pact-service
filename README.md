### Sample Pact provider service

The sample Pact provider service based on Spring Boot with contract testing CI/CD pipeline based on GitHub Actions.

This repository implements the contract testing pipeline following [the general documentation](https://docs.pact.io/pact_nirvana/step_4/#provider-pipeline).
![asd](https://docs.pact.io/assets/images/advanced-pact-workshop-diagrams-provider-pipeline-42b395152d061dc28d060675af34ac72.png)

On merge to any branch it triggers [the GitHub Actions pipeline](https://github.com/artemptushkin/heisenberg-pact-service/actions)
that includes steps:
1. Build
2. Test
3. Publish verification result into Pact Broker
4. [Can-i-deploy](https://docs.pact.io/pact_broker/can_i_deploy/)
5. Dummy deploy step
6. [Create-version-tag](https://github.com/pact-foundation/pact_broker-client#create-version-tag)

See [pipeline](.github/workflows) for more

### Tags strategy

1. It publishes tag equal to an environment after the actual deployment to an env, i.e. `test, prod`
2. It fetches pacts by the next tags to run tests against it: (the set of pacts is different then consumers one!)
* locally: `develop`
* CI/CD pipeline: `{GIT_BRANCH}, test, prod`
3. It uses versions for Pact broker:
* locally: from maven
* CI/CD pipeline: first 6 letters of a git commit hash

### Use case provider scenarios

### scenario-1

Jessy-pinkman as consumer expects that Heisenberg on GET request to `/heisenberg/v1/crystalls` will respond with body:

```json
{
    "amount": 20,
    "crystals": [
        {
            "color": "red",
            "id": 1
        },
        {
            "color": "blue",
            "id": 2
        }
    ]
}
```

1. Consumer opens [pull request](https://github.com/artemptushkin/jesse-pinkman-pact-service/pull/2)

2. CI pipeline fails
![](scenarios/scenario-1-deploy-fails.png)
  
  1. assemble: `./mvnw clean package` :white_check_mark:
  2. test: `./mvnw pact:publish -Dpact.consumer.tags=refs/pull/2/merge -Dpact.consumer.version=fe388ea8` :white_check_mark:
  3. `can-i-deploy`:
  
  ![](scenarios/scenario-1-deploy-fails-can-i-deploy.png)
  Due to absent of pact verification on the provider side

3. Provider opens [pull request](https://github.com/artemptushkin/heisenberg-pact-service/pull/1)
with code updates to meet Jessy-pinkman's expectations

4. Provider pipeline is green

![](scenarios/scenario-1-provider-pipeline.png)
  
  1. build:
  
  ```
    ./mvnw clean package \
         -Dpactbroker.consumerversionselectors.tags=scenario/1-consumer-first \
         -Dpact.provider.tag=refs/pull/1/merge -Dpact.verifier.publishResults=true \
         -Dpact.provider.version=c474f134
  ```
  :white_check_mark:
  
  2. `can-i-deploy`:
  ![](scenarios/scenario-1-provider-can-i-deploy.png)
  3. tagging before deployment to `test`
  ![](scenarios/scenario-1-provider-create-test-tag.png)
  
5. Provide merges to master and deploys to production
6. Consumer reruns pipeline without any code updates
7. Consumer merges to master and deploys to production

#### scenario-2

Heisenberg as provider goes breaking bad and break backward compatibility with Jessy-pinkman.

Branch: https://github.com/artemptushkin/heisenberg-pact-service/tree/1-provider-breaks-backward-compatibility

1. Jessy-pinkman expects this body in response on [prod](https://hello.pactflow.io/pacts/provider/heisenberg/consumer/jesse-pinkman/version/6321fdef)
```json
{
    "amount": 20,
    "crystals": [
        {
            "color": "red",
            "id": 1
        },
        {
            "color": "blue",
            "id": 2
        }
    ]
}
```
Namely, red one red and one blue crystals

2. Heisenberg changes code to send `green` instead of `red` crystal

3. Heisenberg runs tests against prod pact from Jessy-pinkman 

```bash
./mvwn test -Dpactbroker.consumerversionselectors.tags=prod
```

4. :x: Pact is violated

#### scenario-2

Jessy-pinkman as consumer expects a new field

#### scenario-3

Heisenberg as provider verified not all the pacts he is expected to.