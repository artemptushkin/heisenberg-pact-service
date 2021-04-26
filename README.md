### Sample Pact provider service

This service along with [Jessy-pinkman](https://github.com/artemptushkin/jesse-pinkman-pact-service) and [GustavoFring](https://github.com/artemptushkin/gustavo-fring-pact-service)
is a set of demo services for pact contract testing pipeline demonstrations.

-----

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

    1. :white_check_mark: assemble: `./mvnw clean package` :white_check_mark:
    2. :white_check_mark: test: `./mvnw pact:publish -Dpact.consumer.tags=refs/pull/2/merge -Dpact.consumer.version=fe388ea8` :white_check_mark:
    3. :x: `can-i-deploy` fails due to absent of pact verification on the provider side:
    ![](scenarios/scenario-1-deploy-fails-can-i-deploy.png)  

3. Provider opens [pull request](https://github.com/artemptushkin/heisenberg-pact-service/pull/1)
with code updates to meet Jessy-pinkman's expectations

4. Provider pipeline is green
![](scenarios/scenario-1-provider-pipeline.png)
  
    1. :white_check_mark: build:
  
          ```bash
            ./mvnw clean package \
                 -Dpactbroker.consumerversionselectors.tags=scenario/1-consumer-first \
                 -Dpact.provider.tag=refs/pull/1/merge \
                 -Dpact.verifier.publishResults=true \
                 -Dpact.provider.version=c474f134
          ```
    2. :white_check_mark: `can-i-deploy`:
   ![](scenarios/scenario-1-provider-can-i-deploy.png)
    3. :white_check_mark: tagging before deployment to `test` 
   ![](scenarios/scenario-1-provider-create-test-tag.png)

5. Provide merges to master and deploys to production
6. Consumer reruns pipeline without any code updates
7. Consumer merges to master and deploys to production

**Resume**:
* With `can-i-deploy step` consumer isn't allowed to deploy until all the pacts verified and providers deployed
* In consumer driven contracts consumer leads the API design development, but the provider deploys first  

#### scenario-2

Heisenberg as provider goes breaking bad and break backward compatibility with Jessy-pinkman.

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
Namely, one `red` and one `blue` crystals

2. Heisenberg changes code to send `green` instead of `red` crystal and opens [pull request](https://github.com/artemptushkin/heisenberg-pact-service/pull/2)

3. :x: Build fails as a pact is violated
    1. build:
          ```bash
            ./mvnw clean package \
                 -Dpactbroker.consumerversionselectors.tags=$GIT_BRANCH,test,prod \
                 -Dpact.provider.tag=1-provider-breaks-backward-compatibility \
                 -Dpact.verifier.publishResults=true \
                 -Dpact.provider.version=91010641
          ```
    
    fails on:
    
        1) Verifying a pact between jesse-pinkman and heisenberg - GET REQUEST
        
            1.1) BodyMismatch: $.crystals.0.color BodyMismatch: $.crystals.0.color Expected 'red' (String) but received 'green' (String)

**Resume**:
* To prevent deploying provider with breaking changes verify him against all the tags, including `prod`,
it will guarantee that all the expectations from consumers at production will be met.

#### scenario-3

Heisenberg as provider verified not all the pacts he is expected to.

1. Gustavo-fring consumer expects on production that heisenberg on GET request to `/heisenberg/v1/cook` will respond with:
    ```json
    {
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
    The expectations formalized by [the contract in Pact Broker](https://hello.pactflow.io/pacts/provider/heisenberg/consumer/gustavo-fring/version/0.0.1-SNAPSHOT) 
2. The Pact Broker has the next state where the pact between Gustavo-fring and Heisenberg hasn't yet been verified
![](scenarios/scenario-3-provider-verified-no-all-the-contracts.png)

3. :white_check_mark: Heisenberg tests pass on any next merge to master as he doesn't expect any new consumer:
    ```bash
    ./mvnw test -Dpactbroker.consumerversionselectors.tags=prod
    ```

4. :x: Heisenberg fails to deploy the latest version with tag `prod` on production due to violated `can-i-deploy` step:
    ```bash
    pact-broker can-i-deploy --pacticipant=heisenberg --all=prod --to=prod
    ```
![](scenarios/scenario-3-provider-can-i-deploy-fails.png)

**Resume**:
* To prevent deploying provider without all verified contracts use the parameter `--all=$TAG`
* If providers tests pass on some reason, `can-i-deploy` and Pact Broker prevents deploying breaking changes  

> This would be used when ensuring you have backwards compatiblity with all production mobile clients for a provider. Note, when using this option, you need to specify dependency explicitly