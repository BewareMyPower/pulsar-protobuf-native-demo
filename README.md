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

# pulsar-protobuf-native-demo

The demo to show the ProtobufNative schema support in Pulsar Python client.

## Build the Java CLI tools

See [./pulsar-pb-native-java/README.md](./pulsar-pb-native-java/README.md) for instructions to build the Java CLI tools.

## Build

Install Python client 3.12.0 with protobuf support:

```bash
python3 -m pip install 'pulsar_client[protobuf]==3.12.0' --force-reinstall
```

## Demo

### Step 1: start a Pulsar standalone

```bash
./bin/pulsar standalone -nss -nfw
```

### Step 2: Run a Java producer to produce some messages with ProtobufNative schema

```bash
cd ./pulsar-pb-native-java
mvn clean package -DskipTests
java -jar target/pulsar-pb-native-java-1.0.0-SNAPSHOT.jar produce --topic test-topic
cd ..
```

Example logs:

```
Produced messageId=20:0:-1 sequence=1 id=54cd1111-d920-4ad3-a255-bc6b702fab56 content=message-1 created_at=2026-05-18T08:55:02.054Z created_at_ms=1779094502054
Produced messageId=20:1:-1 sequence=2 id=382e139d-ddb5-4237-b07d-928615c766e3 content=message-2 created_at=2026-05-18T08:55:02.075Z created_at_ms=1779094502075
Produced messageId=20:2:-1 sequence=3 id=20c8738d-3d4f-4442-bb98-94f66853a8bf content=message-3 created_at=2026-05-18T08:55:02.078Z created_at_ms=1779094502078
Produced messageId=20:3:-1 sequence=4 id=e1b142de-b868-4716-b08a-3ec79d3b468e content=message-4 created_at=2026-05-18T08:55:02.081Z created_at_ms=1779094502081
Produced messageId=20:4:-1 sequence=5 id=e9317862-cf0a-4831-b4dc-834b8b76b9ea content=message-5 created_at=2026-05-18T08:55:02.084Z created_at_ms=1779094502084
```

### Step 3: Run a Python script to produce and consume messages of ProtobufNative schema

The Python client example `pb_demo.py` provides two approaches to specify ProtobufNative schema:

1. (Default) From pre-generated class `demo_message_pb2.py` generated with the command above:

    ```bash
    protoc \
      --proto_path=./pulsar-pb-native-java/src/main/proto \
      --python_out=. \
      ./pulsar-pb-native-java/src/main/proto/demo_message.proto
    ```

2. Dynamically generate the class from the proto file at runtime via the `build_dynamic_class()` function in the script. You can specify the `--message-class dynamic` option to choose this approach.

```bash
python3 pb_demo.py --topic test-topic
```

Outputs:

```
Consumed message_id=(20,0,-1,-1) sequence=1 id=54cd1111-d920-4ad3-a255-bc6b702fab56 content=message-1 created_at_ms=1779094502054
Consumed message_id=(20,1,-1,-1) sequence=2 id=382e139d-ddb5-4237-b07d-928615c766e3 content=message-2 created_at_ms=1779094502075
Consumed message_id=(20,2,-1,-1) sequence=3 id=20c8738d-3d4f-4442-bb98-94f66853a8bf content=message-3 created_at_ms=1779094502078
Consumed message_id=(20,3,-1,-1) sequence=4 id=e1b142de-b868-4716-b08a-3ec79d3b468e content=message-4 created_at_ms=1779094502081
Consumed message_id=(20,4,-1,-1) sequence=5 id=e9317862-cf0a-4831-b4dc-834b8b76b9ea content=message-5 created_at_ms=1779094502084
Consumed message_id=(20,5,-1,-1) sequence=-1 id=100 content=Python! created_at_ms=1234567890
```

You can see the first 5 messages match the ones produced by the Java producer, and the last message is produced by the Python script itself.

### Step 4: Consume these messages via a Java consumer

```bash
cd ./pulsar-pb-native-Java
java -jar target/pulsar-pb-native-java-1.0.0-SNAPSHOT.jar consume --topic test-topic --subscription sub --max-messages 6
```

Outputs:

```
Consumed messageId=20:0:-1 sequence=1 id=54cd1111-d920-4ad3-a255-bc6b702fab56 content=message-1 created_at=2026-05-18T08:55:02.054Z created_at_ms=1779094502054
Consumed messageId=20:1:-1 sequence=2 id=382e139d-ddb5-4237-b07d-928615c766e3 content=message-2 created_at=2026-05-18T08:55:02.075Z created_at_ms=1779094502075
Consumed messageId=20:2:-1 sequence=3 id=20c8738d-3d4f-4442-bb98-94f66853a8bf content=message-3 created_at=2026-05-18T08:55:02.078Z created_at_ms=1779094502078
Consumed messageId=20:3:-1 sequence=4 id=e1b142de-b868-4716-b08a-3ec79d3b468e content=message-4 created_at=2026-05-18T08:55:02.081Z created_at_ms=1779094502081
Consumed messageId=20:4:-1 sequence=5 id=e9317862-cf0a-4831-b4dc-834b8b76b9ea content=message-5 created_at=2026-05-18T08:55:02.084Z created_at_ms=1779094502084
Consumed messageId=20:5:-1 sequence=-1 id=100 content=Python! created_at=1970-01-15T06:56:07.89Z created_at_ms=1234567890
```
