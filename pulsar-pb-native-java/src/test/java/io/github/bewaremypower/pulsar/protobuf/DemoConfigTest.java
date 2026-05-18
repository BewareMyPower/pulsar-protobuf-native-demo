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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DemoConfigTest {
  @Test
  void producerConfigUsesDefaults() {
    DemoConfig config = DemoConfig.producer(new String[0]);

    assertEquals(DemoConfig.DEFAULT_SERVICE_URL, config.serviceUrl);
    assertEquals(DemoConfig.DEFAULT_TOPIC, config.topic);
    assertEquals(DemoConfig.DEFAULT_PRODUCE_COUNT, config.count);
    assertEquals(DemoConfig.DEFAULT_CONTENT_PREFIX, config.contentPrefix);
  }

  @Test
  void producerConfigParsesEqualsAndSeparatedFlags() {
    DemoConfig config = DemoConfig.producer(new String[] {
        "--service-url=pulsar://broker:6650",
        "--topic", "persistent://tenant/ns/topic",
        "--count=7",
        "--content-prefix", "event"
    });

    assertEquals("pulsar://broker:6650", config.serviceUrl);
    assertEquals("persistent://tenant/ns/topic", config.topic);
    assertEquals(7, config.count);
    assertEquals("event", config.contentPrefix);
  }

  @Test
  void consumerConfigParsesReceiveLimits() {
    DemoConfig config = DemoConfig.consumer(new String[] {
        "--subscription=my-sub",
        "--max-messages", "12",
        "--timeout-seconds=3"
    });

    assertEquals("my-sub", config.subscription);
    assertEquals(12, config.maxMessages);
    assertEquals(3, config.timeoutSeconds);
  }

  @Test
  void consumerConfigRejectsInvalidLimits() {
    assertThrows(IllegalArgumentException.class,
        () -> DemoConfig.consumer(new String[] {"--max-messages", "0"}));
    assertThrows(IllegalArgumentException.class,
        () -> DemoConfig.consumer(new String[] {"--timeout-seconds", "0"}));
  }

  @Test
  void parserRejectsUnexpectedArgument() {
    assertThrows(IllegalArgumentException.class,
        () -> DemoConfig.producer(new String[] {"topic"}));
  }

  @Test
  void parserRejectsUnknownFlag() {
    assertThrows(IllegalArgumentException.class,
        () -> DemoConfig.producer(new String[] {"--max-messages", "1"}));
  }
}
