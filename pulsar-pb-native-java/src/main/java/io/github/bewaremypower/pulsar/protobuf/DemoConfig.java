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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

final class DemoConfig {
  static final String DEFAULT_SERVICE_URL = "pulsar://localhost:6650";
  static final String DEFAULT_TOPIC = "persistent://public/default/protobuf-native-demo";
  static final String DEFAULT_SUBSCRIPTION = "protobuf-native-demo-sub";
  static final int DEFAULT_PRODUCE_COUNT = 5;
  static final int DEFAULT_MAX_MESSAGES = 5;
  static final int DEFAULT_TIMEOUT_SECONDS = 10;
  static final String DEFAULT_CONTENT_PREFIX = "message";

  private static final Set<String> PRODUCER_FLAGS = Set.of(
      "--service-url",
      "--topic",
      "--count",
      "--content-prefix");
  private static final Set<String> CONSUMER_FLAGS = Set.of(
      "--service-url",
      "--topic",
      "--subscription",
      "--max-messages",
      "--timeout-seconds");

  final String serviceUrl;
  final String topic;
  final String subscription;
  final int count;
  final int maxMessages;
  final int timeoutSeconds;
  final String contentPrefix;

  private DemoConfig(
      String serviceUrl,
      String topic,
      String subscription,
      int count,
      int maxMessages,
      int timeoutSeconds,
      String contentPrefix) {
    this.serviceUrl = serviceUrl;
    this.topic = topic;
    this.subscription = subscription;
    this.count = count;
    this.maxMessages = maxMessages;
    this.timeoutSeconds = timeoutSeconds;
    this.contentPrefix = contentPrefix;
  }

  static DemoConfig producer(String[] args) {
    Map<String, String> values = parseFlags(args, PRODUCER_FLAGS);
    return new DemoConfig(
        values.getOrDefault("--service-url", DEFAULT_SERVICE_URL),
        values.getOrDefault("--topic", DEFAULT_TOPIC),
        DEFAULT_SUBSCRIPTION,
        intFlag(values, "--count", DEFAULT_PRODUCE_COUNT, 0),
        DEFAULT_MAX_MESSAGES,
        DEFAULT_TIMEOUT_SECONDS,
        values.getOrDefault("--content-prefix", DEFAULT_CONTENT_PREFIX));
  }

  static DemoConfig consumer(String[] args) {
    Map<String, String> values = parseFlags(args, CONSUMER_FLAGS);
    return new DemoConfig(
        values.getOrDefault("--service-url", DEFAULT_SERVICE_URL),
        values.getOrDefault("--topic", DEFAULT_TOPIC),
        values.getOrDefault("--subscription", DEFAULT_SUBSCRIPTION),
        DEFAULT_PRODUCE_COUNT,
        intFlag(values, "--max-messages", DEFAULT_MAX_MESSAGES, 1),
        intFlag(values, "--timeout-seconds", DEFAULT_TIMEOUT_SECONDS, 1),
        DEFAULT_CONTENT_PREFIX);
  }

  static boolean isHelp(String[] args) {
    for (String arg : args) {
      if ("--help".equals(arg) || "-h".equals(arg)) {
        return true;
      }
    }
    return false;
  }

  private static Map<String, String> parseFlags(String[] args, Set<String> allowedFlags) {
    Map<String, String> values = new LinkedHashMap<>();
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if ("--help".equals(arg) || "-h".equals(arg)) {
        continue;
      }
      if (!arg.startsWith("--")) {
        throw new IllegalArgumentException("Unexpected argument: " + arg);
      }

      String key;
      String value;
      int separator = arg.indexOf('=');
      if (separator > 0) {
        key = arg.substring(0, separator);
        value = arg.substring(separator + 1);
      } else {
        key = arg;
        if (i + 1 >= args.length || args[i + 1].startsWith("--")) {
          throw new IllegalArgumentException("Missing value for " + key);
        }
        value = args[++i];
      }
      if (!allowedFlags.contains(key)) {
        throw new IllegalArgumentException("Unknown option: " + key);
      }
      if (value.isBlank()) {
        throw new IllegalArgumentException("Blank value for " + key);
      }
      values.put(key, value);
    }
    return values;
  }

  private static int intFlag(Map<String, String> values, String key, int defaultValue, int minValue) {
    String value = values.get(key);
    if (value == null) {
      return defaultValue;
    }
    try {
      int parsed = Integer.parseInt(value);
      if (parsed < minValue) {
        throw new IllegalArgumentException(key + " must be >= " + minValue);
      }
      return parsed;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(key + " must be an integer: " + value, e);
    }
  }
}
