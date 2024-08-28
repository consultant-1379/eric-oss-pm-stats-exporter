#!/usr/bin/env groovy

def bob = "./bob/bob"
def ruleset = "ci/local_ruleset.yaml"

if (!env.RELEASE) {
    stage('Custom K8S Test') {
        sh "${bob} -r ${ruleset} robustness-test"
    }
}
