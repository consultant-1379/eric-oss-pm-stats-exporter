[IDUN-66258 - Update internal document collecting SDK content - Exp & QS](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-66258)

# Streaming output

## (Fixed topic)

You can also access the KPI results using the streaming output. This is a "push-like" mechanism to obtain all calculation results, suited for mass consumption of all new results in an efficient way.

Only full table rows are exported when all the KPIs in the table have new result for the latest aggregation period.
You can decide which KPIs should be exported by setting the _exportable_ attribute in the KPI definition.

You can access the exported KPIs using the Message Bus capability. Please refer to the [Message Bus documentation](https://developer.intelligentautomationplatform.ericsson.net/#capabilities/psd-messagebus), especially to the [How to Consume a Message](https://developer.intelligentautomationplatform.ericsson.net/#capabilities/psd-messagebus/psd-messagebus-developer-guide?chapter=how-to-consume-a-message) section of its [Developer Guide](https://developer.intelligentautomationplatform.ericsson.net/#capabilities/psd-messagebus/psd-messagebus-developer-guide).

You do not need to use Data Type Discovery for fixed topic. The Message Bus hostname and connection details are set by the system administrator (e.g.: eric-data-message-bus-kf:9092). The topic name of the Scheduled KPI results is: _pm-stats-calc-handling-avro-scheduled_. The topic name of the On-Demand KPI results is: _pm-stats-calc-handling-avro-on-demand_.

Avro schemas are used to store the KPI results in the Message Bus. These schemas describe the structure of their corresponding KPI tables (the structure of KPI tables is described in the [KPI Tables](#KPI_Tables) section). The Avro schemas are generated and registered in Schema Registry automatically when putting the KPI results into the Message Bus. So you must use these schemas when reading the KPI results from the Message Bus. For this, set the configuration parameter "_value.deserializer_" of the consumer to an avro deserializer class (e.g. in case of Java: _io.confluent.kafka.serializers.KafkaAvroDeserializer.class_). You will also need the URL of the Schema Registry, this is set by the system administrator (e.g.: "<http://schemaregistry:8081>").

If you want to obtain the calculation results which were created before starting the streaming, set the configuration parameter "_auto.offset.reset_" of the consumer to _"earliest"_.

## (With data type discovery)

You can also access the KPI results using the streaming output. This is a "push-like" mechanism to obtain all calculation results, suited for mass consumption of all new results in an efficient way.

Only full table rows are exported when all the KPIs in the table have new result for the latest aggregation period.
You can decide which KPIs should be exported by setting the _exportable_ attribute in the KPI definition.

You can access the exported KPIs using the Message Bus capability. Please refer to the [Message Bus documentation](https://developer.intelligentautomationplatform.ericsson.net/#capabilities/psd-messagebus), especially to the [How to Consume a Message](https://developer.intelligentautomationplatform.ericsson.net/#capabilities/psd-messagebus/psd-messagebus-developer-guide?chapter=how-to-consume-a-message) section of its [Developer Guide](https://developer.intelligentautomationplatform.ericsson.net/#capabilities/psd-messagebus/psd-messagebus-developer-guide).

You need to use Data Type Discovery when the topic name is not fixed.

You can obtain the Message Bus hostname and the connection details, and also the topic names of the KPI results using the [Data Type Discovery](https://developer.intelligentautomationplatform.ericsson.net/#capabilities/psd-data-type-discovery). An example to obtain these with curl (<span style="color:orange;font-weight:bold;">ToDo</span>: _PM STATS_ will be changed to something without space (%20) in it.):

```
curl 'eric-oss-data-catalog:9590/catalog/v1/data-type?dataCategory=PM%20STATS&schemaName=SCH12&dataSpace=PM_STATS_KPI'
```

You should search the response to find the _accessEndpoints_ as the Message Bus hostname and connection details, and _messageSchema.messageDataTopic.name_ as topic names. 2 different topic names shoud be found (for Scheduled and On-Demand KPIs). In the example below, the Message Bus hostname and connection details are: _eric-oss-dmm-kf-op-sz-kafka-bootstrap:9092_ , and one of the topic names is _pm-stats-calc-handling-avro-scheduled_ :

```
...
"messageSchema": {
      ...
      "messageDataTopic": {
        ...
        "name": "pm-stats-calc-handling-avro-scheduled",
        ...
        "messageBus": {
          ...
          "accessEndpoints": [
            "eric-oss-dmm-kf-op-sz-kafka-bootstrap:9092"
          ],
          ...
```

Details on Data Type Discovery APIs are here: [Data Type Discovery API Guide](https://developer.intelligentautomationplatform.ericsson.net/#capabilities/psd-data-type-discovery/psd-data-type-discovery-capability-developer-guide-rest-api).

Avro schemas are used to store the KPI results in the Message Bus. These schemas describe the structure of their corresponding KPI tables (the structure of KPI tables is described in the [KPI Tables](#KPI_Tables) section). The Avro schemas are generated and registered in Schema Registry automatically when putting the KPI results into the Message Bus. So you must use these schemas when reading the KPI results from the Message Bus. For this, set the configuration parameter "_value.deserializer_" of the consumer to an avro deserializer class (e.g. in case of Java: _io.confluent.kafka.serializers.KafkaAvroDeserializer.class_). You will also need the URL of the Schema Registry, this is set by the system administrator (e.g.: "<http://schemaregistry:8081>").

If you want to obtain the calculation results which were created before starting the streaming, set the configuration parameter "_auto.offset.reset_" of the consumer to _"earliest"_.
