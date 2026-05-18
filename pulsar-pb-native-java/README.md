<!--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

# Pulsar ProtobufNative Java Demo

This Maven project contains producer and consumer examples for Apache Pulsar's Java client using a shared `Schema.PROTOBUF_NATIVE(DemoMessage.class)` schema.

## Build

```bash
mvn package
```

The shaded runnable jar is written to:

```bash
target/pulsar-pb-native-java-1.0.0-SNAPSHOT.jar
```

## Run

Start Pulsar standalone first:

```bash
./bin/pulsar standalone -nss -nfw
```

Produce messages:

```bash
java -jar target/pulsar-pb-native-java-1.0.0-SNAPSHOT.jar produce --count 3
```

Consume messages:

```bash
java -jar target/pulsar-pb-native-java-1.0.0-SNAPSHOT.jar consume --max-messages 3 --timeout-seconds 5
```

If fewer than `--max-messages` messages arrive, the consumer exits when `--timeout-seconds` is reached and then closes the consumer.

## Options

Producer:

```text
--service-url <url>       Default: pulsar://localhost:6650
--topic <topic>           Default: persistent://public/default/protobuf-native-demo
--count <n>               Default: 5
--content-prefix <text>   Default: message
```

Consumer:

```text
--service-url <url>       Default: pulsar://localhost:6650
--topic <topic>           Default: persistent://public/default/protobuf-native-demo
--subscription <name>     Default: protobuf-native-demo-sub
--max-messages <n>        Default: 5
--timeout-seconds <n>     Default: 10
```

Flags can be passed as `--name value` or `--name=value`.
