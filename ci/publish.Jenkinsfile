#!/usr/bin/env groovy

def bob = "./bob/bob"
def ruleset = "ci/local_ruleset.yaml"
def ci_ruleset = "ci/common_ruleset2.0.yaml"

stage('Custom Upload Marketplace Documentation') {
    withCredentials([usernamePassword(credentialsId: 'SELI_ARTIFACTORY', usernameVariable: 'SELI_ARTIFACTORY_REPO_USER', passwordVariable: 'SELI_ARTIFACTORY_REPO_PASS'),
                 string(credentialsId: 'PMSE_ADP_PORTAL_API_KEY', variable: 'ADP_PORTAL_API_KEY')]) {
        sh "${bob} -r ${ruleset} generate-docs"
        script {
            echo "Marketplace upload"
            if (env.RELEASE) {
                sh "${bob} -r ${ruleset} marketplace-upload-release"
            }
            else {
                sh "${bob} -r ${ruleset} marketplace-upload-dev"
            }
        }
    }
}

if (env.RELEASE) {
    try {
        stage('Custom Publish') {
            withCredentials([usernamePassword(credentialsId: 'SELI_ARTIFACTORY', usernameVariable: 'SELI_ARTIFACTORY_REPO_USER', passwordVariable: 'SELI_ARTIFACTORY_REPO_PASS'),
                        file(credentialsId: 'docker-config-json', variable: 'DOCKER_CONFIG_JSON')]) {
                ci_pipeline_scripts.checkDockerConfig()
                sh "${bob} -r ${ruleset} upload-mvn-jars"
                ci_pipeline_scripts.retryMechanism("${bob} -r ${ci_ruleset} publish:package-helm-public",3)
                ci_pipeline_scripts.retryMechanism("${bob} -r ${ci_ruleset} publish:image-pull-internal",3)
                ci_pipeline_scripts.retryMechanism("${bob} -r ${ci_ruleset} publish:image-tag-public",3)
                ci_pipeline_scripts.retryMechanism("${bob} -r ${ci_ruleset} publish:image-push-public",3)
                ci_pipeline_scripts.retryMechanism("${bob} -r ${ci_ruleset} publish:helm-upload",3)
                ci_pipeline_scripts.retryMechanism("${bob} -r ${ci_ruleset} publish:helm-upload-to-release-repo",3)
                ci_pipeline_scripts.retryMechanism("${bob} -r ${ruleset} publish-jars",3)
            }
        }
    } catch (e) {
    } finally {
         sh "${bob} -r ${ci_ruleset} delete-images-from-agent:delete-internal-image delete-images-from-agent:delete-drop-image"
    }
}
