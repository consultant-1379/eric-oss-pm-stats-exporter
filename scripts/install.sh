#!/bin/bash
#
# COPYRIGHT Ericsson 2023
#
#
#
# The copyright to the computer program(s) herein is the property of
#
# Ericsson Inc. The programs may be used and/or copied only with written
#
# permission from Ericsson Inc. or in accordance with the terms and
#
# conditions stipulated in the agreement/contract under which the
#
# program(s) have been supplied.
#

function sed_line() {
  if [[ -z $1 || -z $2 || -z $3 ]]; then
    printf "You need to pass three parameters to sed_line:\n  - FROM: the string literal you want to replace\n  - TO: the string literal for replacement\n  - FILE: name of the file you want to apply the operation to"
    exit 1
  fi

  FROM=$1
  TO=$2
  FILE=$3

  sed -i "s^$FROM^$TO^" "$FILE"
}

if [[ -z $SELI_ARTIFACTORY_REPO_USER || -z $SELI_ARTIFACTORY_REPO_PASS || -z $NAMESPACE ]]; then
  echo "You need to provide a username and password for the arm repository, and the namespace you want to use via SELI_ARTIFACTORY_REPO_USER, SELI_ARTIFACTORY_REPO_PASS, and NAMESPACE env vars"
  exit 1
fi

if [[ -z $SKIP_BUILD || $SKIP_BUILD != "true" ]]; then
  version_line=$(grep "SNAPSHOT</version>" < pom.xml)
  version_line_trim_end="${version_line#*>}"

  HASH="$(echo $RANDOM | md5sum | head -c 10)"
  POM_VERSION="${version_line_trim_end%<*}"
  VERSION="$POM_VERSION-$HASH"
  JAR_FILE="eric-oss-pm-stats-exporter-$POM_VERSION.jar"

  eval "$(sed 's/  - image-product-number: /export IMAGE_PRODUCT_NUMBER=/' common-properties.yaml | grep 'IMAGE_PRODUCT_NUMBER')"
  eval "$(sed 's/  - cbos-image-repo: /export CBOS_IMAGE_REPO=/' common-properties.yaml | grep 'CBOS_IMAGE_REPO')"
  eval "$(sed 's/  - cbos-image-name: /export CBOS_IMAGE_NAME=/' common-properties.yaml | grep 'CBOS_IMAGE_NAME')"
  eval "$(sed 's/  - cbos-image-version: /export CBOS_IMAGE_VERSION=/' common-properties.yaml | grep 'CBOS_IMAGE_VERSION')"

  mvn clean install -DskipTests=true -Dcheckstyle.skip -Dpmd.skip -Dcpd.skip

  docker build -t "armdocker.rnd.ericsson.se/proj-eric-oss-dev/eric-oss-pm-stats-exporter:$VERSION" . \
    --build-arg BUILD_DATE="$(date -u +'%Y-%m-%dT%H:%M:%SZ')" \
    --build-arg COMMIT="$HASH" \
    --build-arg APP_VERSION="$VERSION" \
    --build-arg JAR_FILE="$JAR_FILE" \
    --build-arg IMAGE_PRODUCT_NUMBER="$IMAGE_PRODUCT_NUMBER" \
    --build-arg CBOS_IMAGE_TAG="$CBOS_IMAGE_VERSION" \
    --build-arg CBOS_IMAGE_REPO="$CBOS_IMAGE_REPO" \
    --build-arg CBOS_IMAGE_NAME="$CBOS_IMAGE_NAME"

  docker push "armdocker.rnd.ericsson.se/proj-eric-oss-dev/eric-oss-pm-stats-exporter:$VERSION"

  sed_line "repoPath: REPO_PATH" "repoPath: proj-eric-oss-dev" charts/eric-oss-pm-stats-exporter/eric-product-info.yaml
  sed_line "tag: VERSION" "tag: $VERSION" charts/eric-oss-pm-stats-exporter/eric-product-info.yaml

  helm package --version "$VERSION" charts/eric-oss-pm-stats-exporter

  EXPORTER_PATH="eric-oss-pm-stats-exporter-$VERSION.tgz"
elif [[ -z $VERSION || -z $REPO_PATH ]]; then
  echo "If you want to skip the build, you have to provide an already existing exporter version via the VERSION env var, and the path where it is located via the REPO_PATH env var"
  exit 1
else
  EXPORTER_PATH="https://arm.seli.gic.ericsson.se/artifactory/$REPO_PATH/eric-oss-pm-stats-exporter/eric-oss-pm-stats-exporter-$VERSION.tgz"
fi

helm install eric-oss-pm-stats-exporter -n "$NAMESPACE" -f scripts/site-values.yaml --username "$SELI_ARTIFACTORY_REPO_USER" --password "$SELI_ARTIFACTORY_REPO_PASS" "$EXPORTER_PATH"

if [[ -z $SKIP_BUILD || $SKIP_BUILD != "true" ]]; then
  git checkout origin/master charts/eric-oss-pm-stats-exporter/eric-product-info.yaml
fi

