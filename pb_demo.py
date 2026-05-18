#!/usr/bin/env python3
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

from __future__ import annotations

import argparse

from google.protobuf import descriptor_pb2 as pb2
from google.protobuf import message_factory
import pulsar
from pulsar.schema import ProtobufNativeSchema

import demo_message_pb2

# Construct a dynamic class equivalent to ./pulsar-pb-native-java/src/main/proto/demo_message.proto
def build_dynamic_class() -> type:
    file_proto = pb2.FileDescriptorProto()
    file_proto.name = "demo_message.proto"
    file_proto.package = "pulsar.protobuf.demo"
    file_proto.syntax = "proto3"
    file_proto.options.java_multiple_files = True
    file_proto.options.java_package = "io.github.bewaremypower.pulsar.protobuf"
    file_proto.options.java_outer_classname = "DemoMessageProto"

    message_proto = file_proto.message_type.add()
    message_proto.name = "DemoMessage"

    def add_scalar_field(
        message_proto: pb2.DescriptorProto,
        name: str,
        number: int,
        field_type,
    ) -> None:
        field = message_proto.field.add()
        field.name = name
        field.number = number
        field.label = pb2.FieldDescriptorProto.LABEL_OPTIONAL
        field.type = field_type

    add_scalar_field(
        message_proto,
        name="id",
        number=1,
        field_type=pb2.FieldDescriptorProto.TYPE_STRING,
    )
    add_scalar_field(
        message_proto,
        name="content",
        number=2,
        field_type=pb2.FieldDescriptorProto.TYPE_STRING,
    )
    add_scalar_field(
        message_proto,
        name="sequence",
        number=3,
        field_type=pb2.FieldDescriptorProto.TYPE_INT32,
    )
    add_scalar_field(
        message_proto,
        name="created_at_ms",
        number=4,
        field_type=pb2.FieldDescriptorProto.TYPE_INT64,
    )
    return message_factory.GetMessages([file_proto])[f"{file_proto.package}.{message_proto.name}"]


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--message-class",
        choices=("generated", "dynamic"),
        default="generated",
        help="Use the pre-generated protobuf class or build one dynamically.",
    )
    parser.add_argument(
        "--topic",
        default="pb-topic",
        help="Pulsar topic to consume from.",
    )
    return parser.parse_args()


if __name__ == '__main__':
    args = parse_args()
    message_class = (
        demo_message_pb2.DemoMessage
        if args.message_class == "generated"
        else build_dynamic_class()
    )
    schema = ProtobufNativeSchema(message_class)


    client = pulsar.Client('pulsar://localhost:6650')
    try:
        producer = client.create_producer(
            args.topic,
            schema=schema,
        )
        producer.send(message_class(id="100", content="Python!", sequence=-1, created_at_ms=1234567890))
        consumer = client.subscribe(
            args.topic,
            subscription_name='py-pb-sub',
            schema=schema,
            initial_position=pulsar.InitialPosition.Earliest,
        )
        try:
            consumed = 0
            while True:
                try:
                    message = consumer.receive(3000)
                except pulsar.Timeout:
                    break

                value = message.value()
                print(
                    "Consumed "
                    f"message_id={message.message_id()} "
                    f"sequence={value.sequence} "
                    f"id={value.id} "
                    f"content={value.content} "
                    f"created_at_ms={value.created_at_ms}"
                )
                consumer.acknowledge(message)
                consumed += 1
        finally:
            consumer.close()
    finally:
        client.close()
