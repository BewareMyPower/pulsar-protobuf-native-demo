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

import java.io.PrintStream;
import java.util.Arrays;

public final class DemoMain {
  private DemoMain() {
  }

  public static void main(String[] args) throws Exception {
    if (args.length == 0 || DemoConfig.isHelp(args)) {
      printUsage(System.out);
      return;
    }

    String command = args[0];
    String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
    try {
      switch (command) {
        case "produce" -> ProducerExample.main(commandArgs);
        case "consume" -> ConsumerExample.main(commandArgs);
        default -> {
          System.err.println("Unknown command: " + command);
          printUsage(System.err);
          System.exit(2);
        }
      }
    } catch (IllegalArgumentException e) {
      System.err.println(e.getMessage());
      printCommandUsage(command, System.err);
      System.exit(2);
    }
  }

  static void printUsage(PrintStream out) {
    out.println("Usage:");
    out.println("  java -jar target/pulsar-pb-native-java-1.0.0-SNAPSHOT.jar produce [options]");
    out.println("  java -jar target/pulsar-pb-native-java-1.0.0-SNAPSHOT.jar consume [options]");
    out.println();
    printProducerUsage(out);
    out.println();
    printConsumerUsage(out);
  }

  static void printProducerUsage(PrintStream out) {
    out.println("Produce options:");
    out.println("  --service-url <url>       Pulsar service URL. Default: " + DemoConfig.DEFAULT_SERVICE_URL);
    out.println("  --topic <topic>           Topic name. Default: " + DemoConfig.DEFAULT_TOPIC);
    out.println("  --count <n>               Number of messages to send. Default: " + DemoConfig.DEFAULT_PRODUCE_COUNT);
    out.println("  --content-prefix <text>   Message content prefix. Default: " + DemoConfig.DEFAULT_CONTENT_PREFIX);
  }

  static void printConsumerUsage(PrintStream out) {
    out.println("Consume options:");
    out.println("  --service-url <url>       Pulsar service URL. Default: " + DemoConfig.DEFAULT_SERVICE_URL);
    out.println("  --topic <topic>           Topic name. Default: " + DemoConfig.DEFAULT_TOPIC);
    out.println("  --subscription <name>     Subscription name. Default: " + DemoConfig.DEFAULT_SUBSCRIPTION);
    out.println("  --max-messages <n>        Maximum messages to receive. Default: "
        + DemoConfig.DEFAULT_MAX_MESSAGES);
    out.println("  --timeout-seconds <n>     Receive timeout before exiting. Default: "
        + DemoConfig.DEFAULT_TIMEOUT_SECONDS);
  }

  private static void printCommandUsage(String command, PrintStream out) {
    switch (command) {
      case "produce" -> printProducerUsage(out);
      case "consume" -> printConsumerUsage(out);
      default -> printUsage(out);
    }
  }
}
