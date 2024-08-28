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

# params:
# $1 - REPLICAS_TO_START: Number of pods to be ready
wait_for_service_to_start() {
  REPLICAS_TO_START=$1
  EXPECTED="${REPLICAS_TO_START}/${REPLICAS_TO_START}"
  N=150
  READY=$(kubectl -n "$K8S_NAMESPACE" get pods | grep -E '^eric-oss-pm-stats-exporter-\w+-\w+ ' | awk '{print $2}')
  while [[ "$READY" != "$EXPECTED" && ((N -gt 0)) ]]; do
    sleep 5
    READY=$(kubectl -n "$K8S_NAMESPACE" get pods | grep -E '^eric-oss-pm-stats-exporter-\w+-\w+ ' | awk '{print $2}')
    N=$((N - 5))
  done
  if [[ ((N -le 0)) ]]; then
    echo "[ERROR] Service eric-oss-pm-stats-exporter is not running"
    kill "$(jobs -l | grep port-forward | awk '{print $2}')"
    sleep 2
    exit 1
  fi
}

wait_for_service_to_stop() {
  N=90
  RESPONSE=$(kubectl -n "$K8S_NAMESPACE" get pods | grep -E '^eric-oss-pm-stats-exporter-\w+-\w+ ')
  while [[ -n "$RESPONSE" && ((N -gt 0)) ]]; do
    sleep 5
    RESPONSE=$(kubectl -n "$K8S_NAMESPACE" get pods | grep -E '^eric-oss-pm-stats-exporter-\w+-\w+ ')
    N=$((N - 5))
  done
  if [[ ((N -le 0)) ]]; then
    echo "[ERROR] Service $POD_TO_STOP_NAME is not stopped."
    kill "$(jobs -l | grep port-forward | awk '{print $2}')"
    sleep 2
    exit 1
  fi
}

# params:
# $1 - SERVICE_DESCRIPTION: The description of the service which appears in the output message
# $2 - PORT:
check_liveness_probe_of_service() {
  SERVICE_DESCRIPTION=$1
  PORT=$2
  EXPECTED='{"status":"UP","components":'
  RESULT=$(curl http://localhost:"${PORT}"/actuator/health)
  echo "${RESULT}"
  if [[ "$RESULT" == "$EXPECTED"* ]]; then
    echo "[INFO] Liveness of the $SERVICE_DESCRIPTION service: OK."
  else
    echo "[ERROR] Liveness of the $SERVICE_DESCRIPTION service: failed."
    kill "$(jobs -l | grep port-forward | awk '{print $2}')"
    sleep 2
    exit 1
  fi
}

check_readiness_probe_of_service() {
  SERVICE_DESCRIPTION=$1
  PORT=$2
  EXPECTED='{"status":"UP"'
  RESULT=$(curl http://localhost:"${PORT}"/actuator/health/customReadiness)
  echo "${RESULT}"
  if [[ "$RESULT" == "$EXPECTED"* ]]; then
    echo "[INFO] Readiness of the $SERVICE_DESCRIPTION service: OK."
  else
    echo "[ERROR] Readiness of the $SERVICE_DESCRIPTION service: failed."
    kill "$(jobs -l | grep port-forward | awk '{print $2}')"
    sleep 2
    exit 1
  fi
}



# init:
PORT1=40001
PORT2=40002
REPLICAS=$(kubectl -n "$K8S_NAMESPACE" get deployment eric-oss-pm-stats-exporter -o=jsonpath='{.spec.replicas}')
wait_for_service_to_start "$REPLICAS"
echo "[INFO] Service eric-oss-pm-stats-exporter is running."

# port forward:
kubectl -n "$K8S_NAMESPACE" port-forward svc/eric-oss-pm-stats-exporter $PORT1:8080 &
sleep 2

# check liveness probe of the original service:
check_liveness_probe_of_service original $PORT1

# shutdown service:
echo "[INFO] Shutting down service eric-oss-pm-stats-exporter..."
kubectl -n "$K8S_NAMESPACE" patch deployment eric-oss-pm-stats-exporter -p'{"spec":{"replicas": 0}}'
wait_for_service_to_stop
echo "[INFO] Service eric-oss-pm-stats-exporter is shut down, SIGTERM/SIGKILL test is successful."

# restart service:
echo "[INFO] Restarting service eric-oss-pm-stats-exporter..."
kubectl -n "$K8S_NAMESPACE" patch deployment eric-oss-pm-stats-exporter -p'{"spec":{"replicas": '"${REPLICAS}"'}}'
wait_for_service_to_start "$REPLICAS"
echo "[INFO] Service eric-oss-pm-stats-exporter is restarted."

# new port forward:
kill "$(jobs -l | grep port-forward | awk '{print $2}')"
kubectl -n "$K8S_NAMESPACE" port-forward svc/eric-oss-pm-stats-exporter $PORT2:8080 &
sleep 2

# check liveness probe of the restarted service:
check_liveness_probe_of_service restarted $PORT2
echo "[INFO] Liveness test is successful."

# check readiness probe of the restarted service:
check_readiness_probe_of_service restarted $PORT2
echo "[INFO] Readiness test is successful."

kill "$(jobs -l | grep port-forward | awk '{print $2}')"
sleep 2
echo "[INFO] Robustness test is SUCCESSFUL."
