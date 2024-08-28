#!/usr/bin/env groovy

def bob = "./bob/bob"
def ruleset = "ci/local_ruleset.yaml"
def ci_ruleset = "ci/common_ruleset2.0.yaml"

try {
    stage('Custom Lint') {
        parallel(
            "lint markdown": {
                sh "${bob} -r ${ci_ruleset} lint:markdownlint lint:vale"
            },
            "lint helm": {
                sh "${bob} -r ${ci_ruleset} lint:helm"
            },
            "lint helm design rule checker": {
                sh "${bob} -r ${ruleset} lint:helm-chart-check"
            },
            "lint code": {
                sh "${bob} -r ${ci_ruleset} lint:license-check"
            },
            "lint static-checking": {
                sh "${bob} -r ${ruleset} lint:static-checking"
                archiveArtifacts([
                    allowEmptyArchive: true,
                    artifacts: '**/target/site/checkstyle.html, **/target/site/pmd.html, **/target/site/cpd.html'
                ])
            },
            "lint metrics": {
                sh "${bob} -r ${ruleset} lint:metrics-check"
            }
        )
    }
} catch (e) {
    throw e
} finally {
    archiveArtifacts allowEmptyArchive: true, artifacts: '**/design-rule-check-report.*, **/target/site/checkstyle.html, **/target/site/pmd.html, **/target/site/cpd.html'
}

stage('Custom Generate') {
    sh "${bob} -r ${ruleset} generate-docs"
    archiveArtifacts 'build/doc/**/*.*'
    publishHTML (target: [
        allowMissing: false,
        alwaysLinkToLastBuild: false,
        keepAll: true,
        reportDir: 'build/doc',
        reportFiles: 'CTA_api.html',
        reportName: 'Documentation'
    ])
}

stage('Custom Build') {
    sh "${bob} -r ${ruleset} build"
}

try {
    stage('Custom Test') {
        withCredentials([usernamePassword(credentialsId: 'SELI_ARTIFACTORY', usernameVariable: 'SELI_ARTIFACTORY_REPO_USER', passwordVariable: 'SELI_ARTIFACTORY_REPO_PASS')]){
            if (env.RELEASE) {
                sh "${bob} -r ${ruleset} integration-test"
            }
            else {
                sh "${bob} -r ${ruleset} test"
            }
        }
    }
} catch (e) {
    throw e
} finally {
    archiveArtifacts allowEmptyArchive: true, artifacts: '**/design-rule-check-report.*, **/target/site/checkstyle.html, **/target/site/pmd.html, **/target/site/cpd.html'
}
