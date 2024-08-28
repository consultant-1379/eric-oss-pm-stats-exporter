{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "eric-oss-pm-stats-exporter.name" }}
  {{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" | quote }}
{{- end }}

{{/*
Expand the name of the chart without quotation for specific use cases.
*/}}
{{- define "eric-oss-pm-stats-exporter.name.noQuote" }}
  {{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create chart version as used by the chart label.
*/}}
{{- define "eric-oss-pm-stats-exporter.version" }}
{{- printf "%s" .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" | quote }}
{{- end }}

{{/*
Expand the name of the chart.
*/}}
{{- define "eric-oss-pm-stats-exporter.fullname" -}}
{{- if .Values.fullnameOverride -}}
  {{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" | quote -}}
{{- else -}}
  {{- $name := default .Chart.Name .Values.nameOverride -}}
  {{- printf "%s" $name | trunc 63 | trimSuffix "-" | quote -}}
{{- end -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "eric-oss-pm-stats-exporter.chart" }}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" | quote }}
{{- end }}

{{/*
Create configmap name.
*/}}
{{- define "eric-oss-pm-stats-exporter.configmap.name" }}
  {{- include "eric-oss-pm-stats-exporter.name.noQuote" . | printf "%s-configmap" | quote }}
{{- end }}

{{/*
Create log control configmap name.
*/}}
{{- define "eric-oss-pm-stats-exporter.log-control-configmap.name" }}
  {{- include "eric-oss-pm-stats-exporter.name.noQuote" . | printf "%s-log-control-configmap" | quote }}
{{- end }}

{{/*
Create allowed-use-privileged rolebinding name.
*/}}
{{- define "eric-oss-pm-stats-exporter.rolebinding-allowed-use-privileged.name" }}
  {{- include "eric-oss-pm-stats-exporter.name.noQuote" . | printf "%s-rolebinding-allowed-use-privileged" | quote }}
{{- end }}

{{/*
Create allowed-use-privileged rolebinding roleRef name.
*/}}
{{- define "eric-oss-pm-stats-exporter.rolebinding-allowed-use-privileged.roleRef.name" }}
  {{- include "eric-oss-pm-stats-exporter.name.noQuote" . | printf "%s-cluster-role-allowed-use-privileged" | quote }}
{{- end }}

{{/*
Create image pull secrets for global (outside of scope)
*/}}
{{- define "eric-oss-pm-stats-exporter.pullSecret.global" -}}
{{- $pullSecret := "" -}}
{{- if .Values.global -}}
  {{- if .Values.global.pullSecret -}}
    {{- $pullSecret = .Values.global.pullSecret -}}
  {{- end -}}
  {{- end -}}
{{- print $pullSecret | quote -}}
{{- end -}}

{{/*
Create image pull secret, service level parameter takes precedence
*/}}
{{- define "eric-oss-pm-stats-exporter.pullSecret" -}}
{{- $pullSecret := (include "eric-oss-pm-stats-exporter.pullSecret.global" . ) -}}
{{- if .Values.imageCredentials -}}
  {{- if .Values.imageCredentials.pullSecret -}}
    {{- $pullSecret = .Values.imageCredentials.pullSecret | quote -}}
  {{- end -}}
{{- end -}}
{{- print $pullSecret -}}
{{- end -}}

{{- define "eric-oss-pm-stats-exporter.mainImagePath" -}}
    {{- $productInfo := fromYaml (.Files.Get "eric-product-info.yaml") -}}
    {{- $registryUrl := (index $productInfo "images" "eric-oss-pm-stats-exporter" "registry") -}}
    {{- $repoPath := (index $productInfo "images" "eric-oss-pm-stats-exporter" "repoPath") -}}
    {{- $name := (index $productInfo "images" "eric-oss-pm-stats-exporter" "name") -}}
    {{- $tag := (index $productInfo "images" "eric-oss-pm-stats-exporter" "tag") -}}
    {{- if .Values.global -}}
        {{- if .Values.global.registry -}}
            {{- if .Values.global.registry.url -}}
                {{- $registryUrl = .Values.global.registry.url -}}
            {{- end -}}
            {{- if not (kindIs "invalid" .Values.global.registry.repoPath) -}}
                {{- $repoPath = .Values.global.registry.repoPath -}}
            {{- end -}}
        {{- end -}}
    {{- end -}}
    {{- if .Values.imageCredentials -}}
        {{- if (index .Values "imageCredentials" "eric-oss-pm-stats-exporter") -}}
            {{- if (index .Values "imageCredentials" "eric-oss-pm-stats-exporter" "registry") -}}
                {{- if (index .Values "imageCredentials" "eric-oss-pm-stats-exporter" "registry" "url") -}}
                    {{- $registryUrl = (index .Values "imageCredentials" "eric-oss-pm-stats-exporter" "registry" "url") -}}
                {{- end -}}
            {{- end -}}
            {{- if not (kindIs "invalid" (index .Values "imageCredentials" "eric-oss-pm-stats-exporter" "repoPath")) -}}
                {{- $repoPath = (index .Values "imageCredentials" "eric-oss-pm-stats-exporter" "repoPath") -}}
            {{- end -}}
        {{- end -}}
        {{- if not (kindIs "invalid" .Values.imageCredentials.repoPath) -}}
            {{- $repoPath = .Values.imageCredentials.repoPath -}}
        {{- end -}}
    {{- end -}}
    {{- if $repoPath -}}
        {{- $repoPath = printf "%s/" $repoPath -}}
    {{- end -}}
    {{- printf "%s/%s%s:%s" $registryUrl $repoPath $name $tag | quote -}}
{{- end -}}

{{/*
Timezone variable
*/}}
{{- define "eric-oss-pm-stats-exporter.timezone" }}
  {{- $timezone := "UTC" }}
  {{- if .Values.global }}
    {{- if .Values.global.timezone }}
      {{- $timezone = .Values.global.timezone }}
    {{- end }}
  {{- end }}
  {{- print $timezone | quote }}
{{- end -}}

{{/*
Helm and Kubernetes labels
*/}}
{{- define "eric-oss-pm-stats-exporter.helmK8s-labels" }}
app.kubernetes.io/name: {{ include "eric-oss-pm-stats-exporter.name" . }}
helm.sh/chart: {{ include "eric-oss-pm-stats-exporter.chart" . }}
{{ include "eric-oss-pm-stats-exporter.selectorLabels" . }}
app.kubernetes.io/version: {{ include "eric-oss-pm-stats-exporter.version" . }}
app.kubernetes.io/instance: {{ .Release.Name | quote }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service | quote }}
{{- end -}}

{{/*
Common labels
*/}}
{{- define "eric-oss-pm-stats-exporter.labels" -}}
  {{- $helmK8sLabels := include "eric-oss-pm-stats-exporter.helmK8s-labels" . | fromYaml -}}
  {{- $globalLabels := (.Values.global).labels -}}
  {{- $serviceLabels := .Values.labels -}}
  {{- $directstreaming := include "eric-oss-pm-stats-exporter.directStreamingLabel" . | fromYaml -}}
  {{- $dstLabels := include "eric-oss-pm-stats-exporter.dstLabels" . | fromYaml -}}
  {{- include "eric-oss-pm-stats-exporter.mergeLabels" (dict "location" .Template.Name "sources" (list $helmK8sLabels $globalLabels $serviceLabels $directstreaming $dstLabels)) | trim }}
{{- end -}}

{{- define "eric-oss-pm-stats-exporter.containerLabels" -}}
  {{- $commonLabels := include "eric-oss-pm-stats-exporter.labels" . | fromYaml -}}
  {{- $servicemesh := include "eric-oss-pm-stats-exporter.service-mesh-labels" . | fromYaml -}}
  {{- include "eric-oss-pm-stats-exporter.mergeLabels" (dict "location" .Template.Name "sources" (list $commonLabels $servicemesh)) | trim }}
{{- end -}}


{{/*
Define common labels for cnom configmaps
*/}}
{{- define "eric-oss-pm-stats-exporter.cnom-configmap-labels" -}}
{{- $labels := include "eric-oss-pm-stats-exporter.labels" . | fromYaml -}}
{{- $cnomLabels := include "eric-oss-pm-stats-exporter.cnom-dashboard-models" . | fromYaml -}}
{{- include "eric-oss-pm-stats-exporter.mergeLabels" (dict "location" .Template.Name "sources" (list $labels $cnomLabels)) | trim }}
{{- end -}}

{{/*
Enable CNOM Auto-Discovery
*/}}
{{- define "eric-oss-pm-stats-exporter.cnom-dashboard-models" -}}
ericsson.com/cnom-server-dashboard-models: "true"
{{- end -}}

{{/*
Selector labels
*/}}
{{- define "eric-oss-pm-stats-exporter.selectorLabels" -}}
app.kubernetes.io/name: {{ include "eric-oss-pm-stats-exporter.name" . }}
app.kubernetes.io/instance: {{ .Release.Name | quote }}
{{- end }}

{{/*
Annotations for Product Name and Product Number (DR-D1121-064).
*/}}
{{- define "eric-oss-pm-stats-exporter.product-info" }}
ericsson.com/product-name: {{ (fromYaml (.Files.Get "eric-product-info.yaml")).productName | quote }}
ericsson.com/product-number: {{ (fromYaml (.Files.Get "eric-product-info.yaml")).productNumber | quote }}
ericsson.com/product-revision: {{regexReplaceAll "(.*)[+|-].*" .Chart.Version "${1}" | quote }}
{{- end }}

{{/*
Common annotations
*/}}
{{- define "eric-oss-pm-stats-exporter.annotations" -}}
  {{- $productInfoAnn := include "eric-oss-pm-stats-exporter.product-info" . | fromYaml -}}
  {{- $globalAnn := (.Values.global).annotations -}}
  {{- $serviceAnn := .Values.annotations -}}
  {{- include "eric-oss-pm-stats-exporter.mergeAnnotations" (dict "location" .Template.Name "sources" (list $productInfoAnn $globalAnn $serviceAnn)) | trim }}
{{- end -}}

{{/*
 Common annotations with AppArmor and Seccomp
*/}}
{{- define "eric-oss-pm-stats-exporter.containerAnnotations" -}}
  {{- $commonAnn := include "eric-oss-pm-stats-exporter.annotations" . | fromYaml }}
  {{- $prometheusAnn := include "eric-oss-pm-stats-exporter.prometheus" . | fromYaml -}}
  {{- $appArmorAnn := include "eric-oss-pm-stats-exporter.appArmorProfileAnnotation" . | fromYaml -}}
  {{- $seccompAnn := include "eric-oss-pm-stats-exporter.seccompAnnotation" . | fromYaml -}}
  {{- $servicemesh := include "eric-oss-pm-stats-exporter.service-mesh-annotations" . | fromYaml -}}
  {{- include "eric-oss-pm-stats-exporter.mergeAnnotations" (dict "location" .Template.Name "sources" (list $commonAnn $prometheusAnn $appArmorAnn $seccompAnn $servicemesh)) | trim }}
{{- end -}}

{{/*
Create prometheus info
*/}}
{{- define "eric-oss-pm-stats-exporter.prometheus" -}}
prometheus.io/path: {{ .Values.prometheus.path | quote }}
prometheus.io/port: {{ .Values.service.port | quote }}
prometheus.io/scrape: {{ .Values.prometheus.scrape | quote }}
{{- end -}}

{{/*
Create the name of the service account to use without quotes
*/}}
{{- define "eric-oss-pm-stats-exporter.serviceAccountName.noQuote" -}}
  {{- if .Values.serviceAccount.create }}
    {{- default (include "eric-oss-pm-stats-exporter.fullname" . | trimAll "\"" ) .Values.serviceAccount.name }}
  {{- else }}
    {{- default "default" .Values.serviceAccount.name }}
  {{- end }}
{{- end }}

{{/*
Create the fsGroup value according to DR-D1123-136
*/}}
{{- define "eric-oss-pm-stats-exporter.fsGroup.coordinated" -}}
{{- $fsGroupValue := 10000 -}}
  {{- if .Values.global -}}
    {{- if .Values.global.fsGroup -}}
      {{- if .Values.global.fsGroup.manual -}}
        {{ .Values.global.fsGroup.manual }}
      {{- else -}}
        {{- if .Values.global.fsGroup.namespace -}}
          {{- if eq .Values.global.fsGroup.namespace true -}}
            # The 'default' defined in the Security Policy will be used.
          {{- else -}}
            {{- $fsGroupValue -}}
          {{- end -}}
        {{- else -}}
          {{- $fsGroupValue -}}
        {{- end -}}
      {{- end -}}
    {{- else -}}
      {{- $fsGroupValue -}}
    {{- end -}}
  {{- else -}}
    {{- $fsGroupValue -}}
  {{- end -}}
{{- end -}}

{{/*
Define the role reference for security policy
*/}}
{{- define "eric-oss-pm-stats-exporter.securityPolicy.reference" -}}
  {{- if .Values.global -}}
    {{- if .Values.global.security -}}
      {{- if .Values.global.security.policyReferenceMap -}}
        {{ $mapped := index .Values "global" "security" "policyReferenceMap" "default-restricted-security-policy" }}
        {{- if $mapped -}}
          {{ $mapped | quote }}
        {{- else -}}
          "default-restricted-security-policy"
        {{- end -}}
      {{- else -}}
        "default-restricted-security-policy"
      {{- end -}}
    {{- else -}}
      "default-restricted-security-policy"
    {{- end -}}
  {{- else -}}
    "default-restricted-security-policy"
  {{- end -}}
{{- end -}}

{{/*
Define Pod Disruption Budget value taking into account its type (int or string)
*/}}
{{- define "eric-oss-pm-stats-exporter.pod-disruption-budget" -}}
{{- if or (eq "0" (.Values.podDisruptionBudget.minAvailable | toString )) (not (empty .Values.podDisruptionBudget.minAvailable )) }}
minAvailable: {{ .Values.podDisruptionBudget.minAvailable }}
{{- else if or (eq "0" (.Values.podDisruptionBudget.maxUnavailable | toString )) (not (empty .Values.podDisruptionBudget.maxUnavailable )) }}
maxUnavailable: {{ .Values.podDisruptionBudget.maxUnavailable }}
{{- else }}
minAvailable: 1
{{- end }}
{{- end -}}

{{/*
Define upper limit for TerminationGracePeriodSeconds
*/}}
{{- define "eric-oss-pm-stats-exporter.terminationGracePeriodSeconds" -}}
{{- if .Values.terminationGracePeriodSeconds -}}
  {{- toYaml .Values.terminationGracePeriodSeconds -}}
{{- end -}}
{{- end -}}

{{/*
Merge global tolerations with service tolerations (DR-D1120-061-AD).
*/}}
{{- define "eric-oss-pm-stats-exporter.merge-tolerations" -}}
  {{- $global := .Values.global }}
  {{- if $global.tolerations }}
      {{- $globalTolerations := $global.tolerations -}}
      {{- $serviceTolerations := list -}}
      {{- if .Values.tolerations -}}
        {{- if eq (typeOf .Values.tolerations) ("[]interface {}") -}}
          {{- $serviceTolerations = .Values.tolerations -}}
        {{- else if eq (typeOf .Values.tolerations) ("map[string]interface {}") -}}
          {{- $serviceTolerations = index .Values.tolerations .podbasename -}}
        {{- end -}}
      {{- end -}}
      {{- $result := list -}}
      {{- $nonMatchingItems := list -}}
      {{- $matchingItems := list -}}
      {{- range $globalItem := $globalTolerations -}}
        {{- $globalItemId := include "eric-oss-pm-stats-exporter.merge-tolerations.get-identifier" $globalItem -}}
        {{- range $serviceItem := $serviceTolerations -}}
          {{- $serviceItemId := include "eric-oss-pm-stats-exporter.merge-tolerations.get-identifier" $serviceItem -}}
          {{- if eq $serviceItemId $globalItemId -}}
            {{- $matchingItems = append $matchingItems $serviceItem -}}
          {{- end -}}
        {{- end -}}
      {{- end -}}
      {{- range $globalItem := $globalTolerations -}}
        {{- $globalItemId := include "eric-oss-pm-stats-exporter.merge-tolerations.get-identifier" $globalItem -}}
        {{- $matchCount := 0 -}}
        {{- range $matchItem := $matchingItems -}}
          {{- $matchItemId := include "eric-oss-pm-stats-exporter.merge-tolerations.get-identifier" $matchItem -}}
          {{- if eq $matchItemId $globalItemId -}}
            {{- $matchCount = add1 $matchCount -}}
          {{- end -}}
        {{- end -}}
        {{- if eq $matchCount 0 -}}
          {{- $nonMatchingItems = append $nonMatchingItems $globalItem -}}
        {{- end -}}
      {{- end -}}
      {{- range $serviceItem := $serviceTolerations -}}
        {{- $serviceItemId := include "eric-oss-pm-stats-exporter.merge-tolerations.get-identifier" $serviceItem -}}
        {{- $matchCount := 0 -}}
        {{- range $matchItem := $matchingItems -}}
          {{- $matchItemId := include "eric-oss-pm-stats-exporter.merge-tolerations.get-identifier" $matchItem -}}
          {{- if eq $matchItemId $serviceItemId -}}
            {{- $matchCount = add1 $matchCount -}}
          {{- end -}}
        {{- end -}}
        {{- if eq $matchCount 0 -}}
          {{- $nonMatchingItems = append $nonMatchingItems $serviceItem -}}
        {{- end -}}
      {{- end -}}
      {{- toYaml (concat $result $matchingItems $nonMatchingItems) -}}
  {{- else -}}
      {{- if .Values.tolerations -}}
        {{- if eq (typeOf .Values.tolerations) ("[]interface {}") -}}
          {{- toYaml .Values.tolerations -}}
        {{- else if eq (typeOf .Values.tolerations) ("map[string]interface {}") -}}
          {{- toYaml (index .Values.tolerations .podbasename) -}}
        {{- end -}}
      {{- end -}}
  {{- end -}}
{{- end -}}

{{/*
Helper function to get the identifier of a tolerations array element.
Assumes all keys except tolerationSeconds are used to uniquely identify
a tolerations array element.
*/}}
{{ define "eric-oss-pm-stats-exporter.merge-tolerations.get-identifier" }}
  {{- $keyValues := list -}}
  {{- range $key := (keys . | sortAlpha) -}}
    {{- if eq $key "effect" -}}
      {{- $keyValues = append $keyValues (printf "%s=%s" $key (index $ $key)) -}}
    {{- else if eq $key "key" -}}
      {{- $keyValues = append $keyValues (printf "%s=%s" $key (index $ $key)) -}}
    {{- else if eq $key "operator" -}}
      {{- $keyValues = append $keyValues (printf "%s=%s" $key (index $ $key)) -}}
    {{- else if eq $key "value" -}}
      {{- $keyValues = append $keyValues (printf "%s=%s" $key (index $ $key)) -}}
    {{- end -}}
  {{- end -}}
  {{- printf "%s" (join "," $keyValues) -}}
{{ end }}

{{/*
Create a merged set of nodeSelectors from global and service level.
*/}}
{{- define "eric-oss-pm-stats-exporter.nodeSelector" -}}
{{- $globalValue := (dict) -}}
{{- if .Values.global -}}
    {{- if .Values.global.nodeSelector -}}
      {{- $globalValue = .Values.global.nodeSelector -}}
    {{- end -}}
{{- end -}}
{{- if .Values.nodeSelector -}}
  {{- range $key, $localValue := .Values.nodeSelector -}}
    {{- if hasKey $globalValue $key -}}
         {{- $Value := index $globalValue $key -}}
         {{- if ne $Value $localValue -}}
           {{- printf "nodeSelector \"%s\" is specified in both global (%s: %s) and service level (%s: %s) with differing values which is not allowed." $key $key $globalValue $key $localValue | fail -}}
         {{- end -}}
     {{- end -}}
    {{- end -}}
    {{- toYaml (merge $globalValue .Values.nodeSelector) | trim | nindent 2 -}}
{{- else -}}
  {{- if not ( empty $globalValue ) -}}
    {{- toYaml $globalValue | trim | nindent 2 -}}
  {{- end -}}
{{- end -}}
{{- end -}}

{{/*
Define Image Pull Policy
*/}}
{{- define "eric-oss-pm-stats-exporter.registryImagePullPolicy" -}}
    {{- $globalRegistryPullPolicy := "IfNotPresent" -}}
    {{- if .Values.global -}}
        {{- if .Values.global.registry -}}
            {{- if .Values.global.registry.imagePullPolicy -}}
                {{- $globalRegistryPullPolicy = .Values.global.registry.imagePullPolicy -}}
            {{- end -}}
        {{- end -}}
    {{- end -}}
    {{- print $globalRegistryPullPolicy | quote -}}
{{- end -}}

{{/*
Create AppArmor Profile Annotation
*/}}
{{- define "eric-oss-pm-stats-exporter.appArmorProfileAnnotation" -}}
    {{- if .Values.appArmorProfile -}}
    {{- $appArmorValue := .Values.appArmorProfile.type -}}
        {{- if .Values.appArmorProfile.type -}}
            {{- if eq .Values.appArmorProfile.type "localhost" -}}
                {{- $appArmorValue = printf "%s/%s" .Values.appArmorProfile.type .Values.appArmorProfile.localhostProfile }}
            {{- end}}
container.apparmor.security.beta.kubernetes.io/eric-oss-pm-stats-exporter: {{ $appArmorValue | quote }}
        {{- end}}
    {{- end}}
{{- end -}}

{{/*
Define JDBC connection config
*/}}
{{- define "eric-oss-pm-stats-exporter.jdbc-config" -}}
    {{- if eq (index .Values "eric-pm-kpi-data" "enabled") false -}}
       'org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration'
    {{- end -}}
{{- end -}}

{{/*
Define Seccomp annotation
*/}}
{{- define "eric-oss-pm-stats-exporter.seccompAnnotation" -}}
seccomp.security.alpha.kubernetes.io/pod: "runtime/default"
{{- end -}}

{{/*
Seccomp profile section (DR-1123-128)
*/}}
{{- define "eric-oss-pm-stats-exporter.seccomp-profile" }}
    {{- if .Values.seccompProfile }}
      {{- if .Values.seccompProfile.type }}
          {{- if eq .Values.seccompProfile.type "Localhost" }}
              {{- if .Values.seccompProfile.localhostProfile }}
seccompProfile:
  type: {{ .Values.seccompProfile.type }}
  localhostProfile: {{ .Values.seccompProfile.localhostProfile }}
            {{- end }}
          {{- else }}
seccompProfile:
  type: {{ .Values.seccompProfile.type }}
          {{- end }}
        {{- end }}
      {{- end }}
{{- end }}

{{/*
Define the log streaming method
*/}}
{{- define "eric-oss-pm-stats-exporter.streamingMethod" -}}
{{- $streamingMethod := "indirect" -}}
{{- if .Values.global -}}
  {{- if .Values.global.log -}}
      {{- if .Values.global.log.streamingMethod -}}
        {{- $streamingMethod = .Values.global.log.streamingMethod }}
      {{- end -}}
  {{- end -}}
{{- end -}}
{{- if .Values.log -}}
  {{- if .Values.log.streamingMethod -}}
    {{- $streamingMethod = .Values.log.streamingMethod }}
  {{- end -}}
{{- end -}}
{{- print $streamingMethod -}}
{{- end -}}

{{/*
Define the label needed for reaching eric-log-transformer
*/}}
{{- define "eric-oss-pm-stats-exporter.directStreamingLabel" -}}
{{- $streamingMethod := (include "eric-oss-pm-stats-exporter.streamingMethod" .) -}}
{{- if or (eq "direct" $streamingMethod) (eq "dual" $streamingMethod) }}
logger-communication-type: "direct"
{{- end -}}
{{- end -}}

{{/*
Define logging environment variables
*/}}
{{- define "eric-oss-pm-stats-exporter.loggingEnv" -}}
- name: POD_NAME
  valueFrom:
    fieldRef:
      fieldPath: metadata.name
- name: POD_UID
  valueFrom:
    fieldRef:
      fieldPath: metadata.uid
- name: CONTAINER_NAME
  value: eric-oss-pm-stats-exporter
- name: NODE_NAME
  valueFrom:
    fieldRef:
      fieldPath: spec.nodeName
{{- $streamingMethod := (include "eric-oss-pm-stats-exporter.streamingMethod" .) -}}
{{- if or (eq "direct" $streamingMethod) (eq "dual" $streamingMethod) -}}
  {{- if eq "direct" $streamingMethod }}
- name: LOGBACK_CONFIG_FILE
  value: "classpath:logback-http.xml"
  {{- end }}
  {{- if eq "dual" $streamingMethod }}
- name: LOGBACK_CONFIG_FILE
  value: "classpath:logback-dual.xml"
  {{ end }}
- name: LOGSTASH_DESTINATION
  value: {{ .Values.log.logstash_destination | default "eric-log-transformer" }}
- name: LOGSTASH_PORT
  value: {{ include "eric-oss-pm-stats-exporter.logstash-port" . | toString | default "9080" | quote }}
{{- else if eq $streamingMethod "indirect" }}
- name: LOGBACK_CONFIG_FILE
  value: "classpath:logback-json.xml"
{{- else }}
  {{- fail ".log.streamingMethod unknown" }}
{{- end -}}
{{ end }}

{{/*
check global.security.tls.enabled
*/}}
{{- define "eric-oss-pm-stats-exporter.global-security-tls-enabled" -}}
{{- if .Values -}}
    {{- if .Values.global -}}
      {{- if .Values.global.security -}}
        {{- if .Values.global.security.tls -}}
          {{- .Values.global.security.tls.enabled | toString -}}
        {{- else -}}
          {{- "false" -}}
        {{- end -}}
      {{- else -}}
        {{- "false" -}}
      {{- end -}}
    {{- else -}}
      {{- "false" -}}
    {{- end -}}
{{- end -}}
{{- end -}}

{{/*
DR-D470217-007-AD This helper defines whether this service enter the Service Mesh or not.
*/}}
{{- define "eric-oss-pm-stats-exporter.service-mesh-enabled" }}
  {{- $globalMeshEnabled := "false" -}}
  {{- if .Values -}}
      {{- if .Values.global -}}
        {{- if .Values.global.serviceMesh -}}
            {{- $globalMeshEnabled = .Values.global.serviceMesh.enabled -}}
        {{- end -}}
      {{- end -}}
  {{- end -}}
  {{- $globalMeshEnabled -}}
{{- end -}}


{{/*
DR-D470217-011 This helper defines the annotation which bring the service into the mesh.
*/}}
{{- define "eric-oss-pm-stats-exporter.service-mesh-inject" }}
{{- if eq (include "eric-oss-pm-stats-exporter.service-mesh-enabled" .) "true" }}
sidecar.istio.io/inject: "true"
{{- else -}}
sidecar.istio.io/inject: "false"
{{- end -}}
{{- end -}}

{{/*
GL-D470217-080-AD
This helper captures the service mesh version from the integration chart to
annotate the workloads so they are redeployed in case of service mesh upgrade.
*/}}
{{- define "eric-oss-pm-stats-exporter.service-mesh-version" }}
{{- if eq (include "eric-oss-pm-stats-exporter.service-mesh-enabled" .) "true" }}
  {{- if .Values.global.serviceMesh -}}
    {{- if .Values.global.serviceMesh.annotations -}}
      {{ .Values.global.serviceMesh.annotations | toYaml }}
    {{- end -}}
  {{- end -}}
{{- end -}}
{{- end -}}

{{/*
Define kafka bootstrap port
*/}}
{{- define "eric-oss-pm-stats-exporter.kafka-bootstrap-port" -}}
{{- $kafkaBootstrapPort := "" -}}
{{- $serviceMesh := ( include "eric-oss-pm-stats-exporter.service-mesh-enabled" . ) -}}
{{- $tls := ( include "eric-oss-pm-stats-exporter.global-security-tls-enabled" . ) -}}
{{- if and (eq $serviceMesh "true") (eq $tls "true") -}}
    {{- $kafkaBootstrapPort = .Values.kafka.brokerPortTls | quote -}}
{{ else }}
    {{- $kafkaBootstrapPort = .Values.kafka.brokerPort | quote -}}
{{ end }}
{{- print $kafkaBootstrapPort -}}
{{- end -}}

{{/*
Define logstash port
*/}}
{{- define "eric-oss-pm-stats-exporter.logstash-port" -}}
{{- $logstashPort := "" -}}
{{- $serviceMesh := ( include "eric-oss-pm-stats-exporter.service-mesh-enabled" . ) -}}
{{- $tls := ( include "eric-oss-pm-stats-exporter.global-security-tls-enabled" . ) -}}
{{- if and (eq $serviceMesh "true") (eq $tls "true") -}}
    {{- $logstashPort = .Values.log.logstash_port_tls -}}
{{ else }}
    {{- $logstashPort = .Values.log.logstash_port -}}
{{ end }}
{{- print $logstashPort -}}
{{- end -}}

{{/*
This helper defines the annotation for define service mesh volume.
*/}}
{{- define "eric-oss-pm-stats-exporter.service-mesh-volume" }}
{{- if and (eq (include "eric-oss-pm-stats-exporter.service-mesh-enabled" .) "true") (eq (include "eric-oss-pm-stats-exporter.global-security-tls-enabled" .) "true") }}
sidecar.istio.io/userVolume: '{"eric-oss-pm-stats-exporter-eric-pm-kpi-data-certs-tls":{"secret":{"secretName":"eric-oss-pm-stats-exporter-eric-pm-kpi-data-ro-secret","optional":true}},"eric-oss-pm-stats-exporter-kafka-bootstrap-certs-tls":{"secret":{"secretName":"eric-oss-pm-stats-exporter-eric-oss-dmm-kf-op-sz-kafka-bootstrap-secret","optional":true}},"eric-oss-pm-stats-exporter-search-engine-certs-tls":{"secret":{"secretName":"eric-oss-pm-stats-exporter-search-engine-secret","optional":true}},"eric-oss-pm-stats-exporter-eric-log-transformer-certs-tls":{"secret":{"secretName":"eric-oss-pm-stats-exporter-eric-log-transformer-secret","optional":true}},"eric-oss-pm-stats-exporter-eric-dst-collector-certs-tls":{"secret":{"secretName":"eric-oss-pm-stats-exporter-eric-dst-collector-secret","optional":true}},"eric-oss-pm-stats-exporter-certs-ca-tls":{"secret":{"secretName":"eric-sec-sip-tls-trusted-root-cert"}}}'
sidecar.istio.io/userVolumeMount: '{"eric-oss-pm-stats-exporter-eric-pm-kpi-data-certs-tls":{"mountPath":"/etc/istio/tls/eric-pm-kpi-data-ro/","readOnly":true},"eric-oss-pm-stats-exporter-kafka-bootstrap-certs-tls":{"mountPath":"/etc/istio/tls/eric-oss-dmm-kf-op-sz-kafka-bootstrap/","readOnly":true},"eric-oss-pm-stats-exporter-search-engine-certs-tls":{"mountPath":"/etc/istio/tls/search-engine/","readOnly":true},"eric-oss-pm-stats-exporter-eric-log-transformer-certs-tls":{"mountPath":"/etc/istio/tls/eric-log-transformer/","readOnly":true},"eric-oss-pm-stats-exporter-eric-dst-collector-certs-tls":{"mountPath":"/etc/istio/tls/eric-dst-collector/","readOnly":true},"eric-oss-pm-stats-exporter-certs-ca-tls":{"mountPath":"/etc/istio/tls-ca","readOnly":true}}'
{{ end }}
{{- end -}}

{{/*
This helper defines which out-mesh services are reached by the eric-oss-pm-stats-exporter.
*/}}
{{- define "eric-oss-pm-stats-exporter.service-mesh-ism2osm-labels" }}
{{- if eq (include "eric-oss-pm-stats-exporter.service-mesh-enabled" .) "true" }}
  {{- if eq (include "eric-oss-pm-stats-exporter.global-security-tls-enabled" .) "true" }}
eric-oss-dmm-kf-op-sz-kafka-ism-access: "true"
eric-pm-kpi-data-ro-ism-access: "true"
eric-log-transformer-ism-access: "true"
eric-dst-collector-ism-access: "true"
  {{- end }}
{{- end -}}
{{- end -}}

{{/*
Merged labels for Default, which includes Standard and Config
*/}}
{{- define "eric-oss-pm-stats-exporter.service-mesh-labels" -}}
  {{- $servicemeshInject := include "eric-oss-pm-stats-exporter.service-mesh-inject" . | fromYaml -}}
  {{- $servicemeshIsm2osm := include "eric-oss-pm-stats-exporter.service-mesh-ism2osm-labels" . | fromYaml -}}
  {{- include "eric-oss-pm-stats-exporter.mergeLabels" (dict "location" .Template.Name "sources" (list $servicemeshInject $servicemeshIsm2osm)) | trim }}
{{- end -}}

{{/*
Merged annotations for Default, which includes productInfo and config
*/}}
{{- define "eric-oss-pm-stats-exporter.service-mesh-annotations" -}}
  {{- $servicemeshInject := include "eric-oss-pm-stats-exporter.service-mesh-inject" . | fromYaml -}}
  {{- $servicemeshVersion := include "eric-oss-pm-stats-exporter.service-mesh-version" . | fromYaml -}}
  {{- $servicemeshVolume := include "eric-oss-pm-stats-exporter.service-mesh-volume" . | fromYaml }}
  {{- include "eric-oss-pm-stats-exporter.mergeAnnotations" (dict "location" .Template.Name "sources" (list $servicemeshInject $servicemeshVersion $servicemeshVolume)) | trim }}
{{- end -}}

{{/*
    Define supplementalGroups (DR-D1123-135)
*/}}
{{- define "eric-oss-pm-stats-exporter.supplementalGroups" -}}
  {{- $globalGroups := (list) -}}
  {{- if ( (((.Values).global).podSecurityContext).supplementalGroups) }}
    {{- $globalGroups = .Values.global.podSecurityContext.supplementalGroups -}}
  {{- end -}}
  {{- $localGroups := (list) -}}
  {{- if ( ((.Values).podSecurityContext).supplementalGroups) -}}
    {{- $localGroups = .Values.podSecurityContext.supplementalGroups -}}
  {{- end -}}
  {{- $mergedGroups := (list) -}}
  {{- if $globalGroups -}}
    {{- $mergedGroups = $globalGroups -}}
  {{- end -}}
  {{- if $localGroups -}}
    {{- $mergedGroups = concat $globalGroups $localGroups | uniq -}}
  {{- end -}}
  {{- if $mergedGroups -}}
    supplementalGroups: {{- toYaml $mergedGroups | nindent 8 -}}
  {{- end -}}
  {{- /*Do nothing if both global and local groups are not set */ -}}
{{- end -}}

{/*
Define JVM heap size
*/}}
{{- define "eric-oss-pm-stats-exporter.jvmHeapSettings" -}}
    {{- $initRAM := "" -}}
    {{- $maxRAM := "" -}}
    {{/*
       ramLimit is set by default to 1.0, this is if the service is set to use anything less than M/Mi
       Rather than trying to cover each type of notation,
       if a user is using anything less than M/Mi then the assumption is its less than the cutoff of 1.3GB
       */}}
    {{- $ramLimit := 1.0 -}}
    {{- $ramComparison := 1.3 -}}

    {{- if not (index .Values "resources" "eric-oss-pm-stats-exporter" "limits" "memory") -}}
        {{- fail "memory limit for eric-oss-pm-stats-exporter is not specified" -}}
    {{- end -}}

    {{- if (hasSuffix "Gi" (index .Values "resources" "eric-oss-pm-stats-exporter" "limits" "memory")) -}}
        {{- $ramLimit = trimSuffix "Gi" (index .Values "resources" "eric-oss-pm-stats-exporter" "limits" "memory") | float64 -}}
    {{- else if (hasSuffix "G" (index .Values "resources" "eric-oss-pm-stats-exporter" "limits" "memory")) -}}
        {{- $ramLimit = trimSuffix "G" (index .Values "resources" "eric-oss-pm-stats-exporter" "limits" "memory") | float64 -}}
    {{- else if (hasSuffix "Mi" (index .Values "resources" "eric-oss-pm-stats-exporter" "limits" "memory")) -}}
        {{- $ramLimit = (div (trimSuffix "Mi" (index .Values "resources" "eric-oss-pm-stats-exporter" "limits" "memory") | float64) 1000) | float64  -}}
    {{- else if (hasSuffix "M" (index .Values "resources" "eric-oss-pm-stats-exporter" "limits" "memory")) -}}
        {{- $ramLimit = (div (trimSuffix "M" (index .Values "resources" "eric-oss-pm-stats-exporter" "limits" "memory") | float64) 1000) | float64  -}}
    {{- end -}}

    {{- if (index .Values "resources" "eric-oss-pm-stats-exporter" "jvm") -}}
        {{- if (index .Values "resources" "eric-oss-pm-stats-exporter" "jvm" "initialMemoryAllocationPercentage") -}}
            {{- $initRAM = index .Values "resources" "eric-oss-pm-stats-exporter" "jvm" "initialMemoryAllocationPercentage" | float64 -}}
            {{- $initRAM = printf "-XX:InitialRAMPercentage=%f" $initRAM -}}
        {{- else -}}
            {{- fail "initialMemoryAllocationPercentage not set" -}}
        {{- end -}}
        {{- if and (index .Values "resources" "eric-oss-pm-stats-exporter" "jvm" "smallMemoryAllocationMaxPercentage") (index .Values "resources" "eric-oss-pm-stats-exporter" "jvm" "largeMemoryAllocationMaxPercentage") -}}
            {{- if lt $ramLimit $ramComparison -}}
                {{- $maxRAM =index .Values "resources" "eric-oss-pm-stats-exporter" "jvm" "smallMemoryAllocationMaxPercentage" | float64 -}}
                {{- $maxRAM = printf "-XX:MaxRAMPercentage=%f" $maxRAM -}}
            {{- else -}}
                {{- $maxRAM = index .Values "resources" "eric-oss-pm-stats-exporter" "jvm" "largeMemoryAllocationMaxPercentage" | float64 -}}
                {{- $maxRAM = printf "-XX:MaxRAMPercentage=%f" $maxRAM -}}
            {{- end -}}
        {{- else -}}
            {{- fail "smallMemoryAllocationMaxPercentage | largeMemoryAllocationMaxPercentage not set" -}}
        {{- end -}}
    {{- else -}}
        {{- fail "jvm heap percentages are not set" -}}
    {{- end -}}
{{- printf "%s %s" $initRAM $maxRAM -}}
{{- end -}}

{{/*
  Define role kind to SecurityPolicy - DR-D1123-134
*/}}
{{- define "eric-oss-pm-stats-exporter.securityPolicyRoleKind" -}}
{{- $rolekind := "" -}}
{{- if .Values.global -}}
    {{- if .Values.global.securityPolicy -}}
        {{- if .Values.global.securityPolicy.rolekind -}}
            {{- $rolekind = .Values.global.securityPolicy.rolekind -}}
            {{- if and (ne $rolekind "Role") (ne $rolekind "ClusterRole") -}}
                {{- printf "For global.securityPolicy.rolekind only \"Role\", \"ClusterRole\" or \"\" is allowed as values." | fail -}}
            {{- end -}}
        {{- end -}}
    {{- end -}}
{{- end -}}
{{- $rolekind -}}
{{- end -}}

{{/*
  Define RoleName to SecurityPolicy - DR-D1123-134
*/}}
{{- define "eric-oss-pm-stats-exporter.securityPolicyRolename" -}}
{{- $rolename := (include "eric-oss-pm-stats-exporter.name" .) -}}
{{- if .Values.securityPolicy -}}
    {{- if .Values.securityPolicy.rolename -}}
        {{- $rolename = .Values.securityPolicy.rolename -}}
    {{- end -}}
{{- end -}}
{{- $rolename -}}
{{- end -}}

{{/*
  Define RolebindingName to SecurityPolicy - DR-D1123-134
*/}}
{{- define "eric-oss-pm-stats-exporter.securityPolicy.rolebindingName" -}}
{{- $rolekind := "" -}}
{{- if .Values.global -}}
    {{- if .Values.global.securityPolicy -}}
        {{- if .Values.global.securityPolicy.rolekind -}}
            {{- $rolekind = .Values.global.securityPolicy.rolekind -}}
            {{- if (eq $rolekind "Role") -}}
                {{- (print (include "eric-oss-pm-stats-exporter.serviceAccountName.noQuote" .) "-r-" (include "eric-oss-pm-stats-exporter.securityPolicyRolename" .) "-sp") | quote -}}
            {{- else if (eq $rolekind "ClusterRole") -}}
                {{- (print (include "eric-oss-pm-stats-exporter.serviceAccountName.noQuote" .) "-c-" (include "eric-oss-pm-stats-exporter.securityPolicyRolename" .) "-sp") | quote -}}
            {{- end }}
        {{- end }}
    {{- end -}}
{{- end -}}
{{- end -}}

{{/*
CNOM pod label to use for grouping and splitting in the dashboard and prometheus queries, depending on service mesh and tls settings
*/}}
{{- define "eric-oss-pm-stats-exporter.cnom-pod-label" -}}
{{- $tls := include "eric-oss-pm-stats-exporter.global-security-tls-enabled" . | trim -}}
{{- if (eq $tls "true") -}}
pod_name
{{- else -}}
kubernetes_pod_name
{{- end -}}
{{- end -}}

{{/*
This helper defines whether DST is enabled or not.
*/}}
{{- define "eric-oss-pm-stats-exporter.dst-enabled" }}
  {{- $dstEnabled := "false" -}}
  {{- if .Values.dst -}}
    {{- if .Values.dst.enabled -}}
        {{- $dstEnabled = .Values.dst.enabled -}}
    {{- end -}}
  {{- end -}}
  {{- $dstEnabled -}}
{{- end -}}

{{/*
Define the labels needed for DST
*/}}
{{- define "eric-oss-pm-stats-exporter.dstLabels" -}}
{{- if eq (include "eric-oss-pm-stats-exporter.dst-enabled" .) "true" }}
eric-dst-collector-access: "true"
{{- end }}
{{- end -}}

{{/*
This helper defines which exporter port must be used depending on protocol
*/}}
{{- define "eric-oss-pm-stats-exporter.exporter-port" }}
  {{- $dstExporterPort := .Values.dst.collector.portOtlpGrpc -}}
    {{- if .Values.dst.collector.protocol -}}
      {{- if eq .Values.dst.collector.protocol "http" -}}
        {{- $dstExporterPort = .Values.dst.collector.portOtlpHttp -}}
      {{- end -}}
    {{- end -}}
  {{- $dstExporterPort -}}
{{- end -}}

{{/*
Define DST environment variables
*/}}
{{ define "eric-oss-pm-stats-exporter.dstEnv" }}
{{- if eq (include "eric-oss-pm-stats-exporter.dst-enabled" .) "true" }}
- name: ERIC_TRACING_ENABLED
  value: "true"
- name: ERIC_PROPAGATOR_PRODUCE
  value: {{ .Values.dst.producer.type }}
- name: ERIC_EXPORTER_PROTOCOL
  value: {{ .Values.dst.collector.protocol }}
{{- if eq .Values.dst.collector.protocol "grpc"}}
- name: ERIC_EXPORTER_ENDPOINT
  value: {{ .Values.dst.collector.host }}:{{ include "eric-oss-pm-stats-exporter.exporter-port" . }}
{{- else if eq .Values.dst.collector.protocol "http"}}
  value: {{ .Values.dst.collector.host }}:{{ include "eric-oss-pm-stats-exporter.exporter-port" . }}/v1/traces
{{- end }}
- name: ERIC_SAMPLER_JAEGER_REMOTE_ENDPOINT
  value: {{ .Values.dst.collector.host }}:{{ .Values.dst.collector.portJaegerGrpc }}
{{- if eq .Values.dst.collector.protocol "http"}}
- name: OTEL_EXPORTER_OTLP_TRACES_PROTOCOL
  value: http/protobuf
{{- end }}
{{- else }}
- name: ERIC_TRACING_ENABLED
  value: "false"
{{- end -}}
{{ end }}

{{/*
This helper defines which Postgres host to be used.
*/}}
{{- define "eric-oss-pm-stats-exporter.database-host" }}
{{- $serviceMesh := ( include "eric-oss-pm-stats-exporter.service-mesh-enabled" . ) -}}
{{- $tls := ( include "eric-oss-pm-stats-exporter.global-security-tls-enabled" . ) -}}
{{- if and (eq $serviceMesh "true") (eq $tls "true") -}}
{{- printf "ro-%s" (index  .Values "eric-pm-kpi-data" "nameOverride") | quote }}
{{- else }}
{{- index  .Values "eric-pm-kpi-data" "nameOverride" | quote }}
{{- end -}}
{{- end -}}