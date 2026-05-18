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

import java.util.UUID;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;

public final class ProducerExample {
  private ProducerExample() {
  }

  public static void main(String[] args) throws Exception {
    if (DemoConfig.isHelp(args)) {
      DemoMain.printProducerUsage(System.out);
      return;
    }
    run(DemoConfig.producer(args));
  }

  static void run(DemoConfig config) throws Exception {
    try (PulsarClient client = PulsarClient.builder()
        .serviceUrl(config.serviceUrl)
        .build();
        Producer<DemoMessage> producer = client.newProducer(DemoSchemas.DEMO_MESSAGE)
            .topic(config.topic)
            .create()) {
      for (int i = 1; i <= config.count; i++) {
        DemoMessage message = DemoMessage.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setContent(config.contentPrefix + "-" + i)
            .setSequence(i)
            .setCreatedAtMs(System.currentTimeMillis())
            .build();

        MessageId messageId = producer.newMessage()
            .value(message)
            .send();

        System.out.printf(
            "Produced messageId=%s sequence=%d id=%s content=%s created_at=%s created_at_ms=%d%n",
            messageId,
            message.getSequence(),
            message.getId(),
            message.getContent(),
            DemoTimestamps.formatCreatedAt(message.getCreatedAtMs()),
            message.getCreatedAtMs());
      }
    }
  }
}
