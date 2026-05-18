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

import java.util.concurrent.TimeUnit;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;

public final class ConsumerExample {
  private ConsumerExample() {
  }

  public static void main(String[] args) throws Exception {
    if (DemoConfig.isHelp(args)) {
      DemoMain.printConsumerUsage(System.out);
      return;
    }
    run(DemoConfig.consumer(args));
  }

  static void run(DemoConfig config) throws Exception {
    int received = 0;
    try (PulsarClient client = PulsarClient.builder()
        .serviceUrl(config.serviceUrl)
        .build();
        Consumer<DemoMessage> consumer = client.newConsumer(DemoSchemas.DEMO_MESSAGE)
            .topic(config.topic)
            .subscriptionName(config.subscription)
            .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
            .subscribe()) {
      while (received < config.maxMessages) {
        Message<DemoMessage> message = consumer.receive(config.timeoutSeconds, TimeUnit.SECONDS);
        if (message == null) {
          System.out.printf(
              "No message received for %d seconds; closing consumer after %d message(s).%n",
              config.timeoutSeconds, received);
          break;
        }

        DemoMessage value = message.getValue();
        System.out.printf(
            "Consumed messageId=%s sequence=%d id=%s content=%s created_at=%s created_at_ms=%d%n",
            message.getMessageId(),
            value.getSequence(),
            value.getId(),
            value.getContent(),
            DemoTimestamps.formatCreatedAt(value.getCreatedAtMs()),
            value.getCreatedAtMs());
        consumer.acknowledge(message);
        received++;
      }
    }
    System.out.printf("Consumer closed after receiving %d message(s).%n", received);
  }
}
