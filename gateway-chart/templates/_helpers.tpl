{{- define "gateway-chart.name" -}}
gateway
{{- end }}

{{- define "gateway-chart.fullname" -}}
{{ include "gateway-chart.name" . }}-{{ .Release.Name }}
{{- end }}
