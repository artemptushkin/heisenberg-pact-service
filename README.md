### Sample Pact provider service

The sample Pact provider service based on Spring Boot with contract testing GitHub Actions CI/CD pipeline.

This repository implements the contract testing pipeline following [the general documentation](https://docs.pact.io/pact_nirvana/step_4/#provider-pipeline).
![asd](https://docs.pact.io/assets/images/advanced-pact-workshop-diagrams-provider-pipeline-42b395152d061dc28d060675af34ac72.png)

On merge to any branch it triggers [the GitHub Actions pipeline](https://github.com/artemptushkin/heisenberg-pact-service/actions)
that includes steps:
1. build
2. tests
3. verify pacts as provider
4. can-i-deploy
5. dummy deploy
6. create-version-tag

### Tags strategy

1. It publishes tag equal to an environment after the actual deployment to that env: `test, prod`
2. It fetches pacts to verify with tags: (it differs from the consumer set of tags!)
* locally: `develop`
* CI/CD pipeline: `{GIT_BRANCH}, test, prod`
3. It uses version
* locally: from maven
* CI/CD pipeline: first 6 letters of a git commit hash