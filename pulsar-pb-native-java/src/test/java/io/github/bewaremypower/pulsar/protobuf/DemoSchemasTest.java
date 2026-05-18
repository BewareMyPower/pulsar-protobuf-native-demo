/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.bewaremypower.pulsar.protobuf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.pulsar.common.schema.SchemaType;
import org.junit.jupiter.api.Test;

class DemoSchemasTest {
  @Test
  void exposesSharedProtobufNativeSchema() {
    assertNotNull(DemoSchemas.DEMO_MESSAGE);
    assertEquals(SchemaType.PROTOBUF_NATIVE, DemoSchemas.DEMO_MESSAGE.getSchemaInfo().getType());
  }

  @Test
  void demoMessageBuilderCreatesExpectedPayload() {
    DemoMessage message = DemoMessage.newBuilder()
        .setId("id-1")
        .setContent("message-1")
        .setSequence(1)
        .setCreatedAtMs(123L)
        .build();

    assertEquals("id-1", message.getId());
    assertEquals("message-1", message.getContent());
    assertEquals(1, message.getSequence());
    assertEquals(123L, message.getCreatedAtMs());
  }
}
