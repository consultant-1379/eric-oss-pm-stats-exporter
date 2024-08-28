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

CHARTS=$(helm list -n "$NAMESPACE" | awk '{ print $1 }' | grep -v NAME)

for CHART in $CHARTS; do
  helm delete "$CHART" -n "$NAMESPACE"
done

PODS=$(kubectl get pod -n "$NAMESPACE" | awk '{ print $1 }' | grep -v NAME)

for POD in $PODS; do
  kubectl delete pod -n "$NAMESPACE" "$POD"
done

PVCS=$(kubectl get pvc -n "$NAMESPACE" | awk '{ print $1 }' | grep -v NAME)

for PVC in $PVCS; do
  sleep 5s
  kubectl delete pvc "$PVC" -n "$NAMESPACE" --force --grace-period=0
done
