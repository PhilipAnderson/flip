#include <Arduino.h>
#include <Scheduler.h>
#include <ArduinoJson.h>

#define DEBUG

const long task_stack_size = 4096;
const int reward_relay_pin = 9;

void button_control();
void rfid_control();
void usb_control();


void setup() {
  SerialUSB.begin(115200);
  delay(4000);
  Scheduler.start(button_control, task_stack_size);
  Scheduler.start(rfid_control, task_stack_size);
  Scheduler.start(usb_control, task_stack_size);
}

void usb_serial_write(JsonDocument &document) {
  static bool mutex = false;

  while (mutex) {
    delay(10);
  }

  mutex = true;
  serializeJson(document, SerialUSB);
  #ifdef DEBUG
  SerialUSB.println();
  #endif
  mutex = false;
}

void report_error_message(String message) {
  StaticJsonDocument<512> document;
  document["type"] = "error_message";
  document["message"] = message;
  usb_serial_write(document);
}

void report_debug_message(String message) {
  StaticJsonDocument<512> document;
  document["type"] = "debug_message";
  document["message"] = message;
  usb_serial_write(document);
}

// A stream that yields when there is no data, allowing the scheduler to function.
struct SerialUSBYield {
  int read() {
    while (!SerialUSB.available()) {
      delay(1);
    }
    return SerialUSB.read();
  }

  size_t readBytes(char *buffer, size_t length) {
    for (size_t i = 0; i < length; i++) {
      if (char c = read(); c >= 0) {
        buffer[i] = c;
      } else {
        return i;
      }
    }
  }
};

void usb_control() {

  StaticJsonDocument<512> command;
  SerialUSBYield reader;

  pinMode(reward_relay_pin, OUTPUT);
  digitalWrite(reward_relay_pin, LOW);

  while (true) {

    DeserializationError err = deserializeJson(command, reader);

    if (err) {
      report_error_message(String("Failed to deserialize json command: ") + err.c_str());
    } else {
      if (command["type"] == "dispense") {
        digitalWrite(reward_relay_pin, HIGH);
        delay(500);
        digitalWrite(reward_relay_pin, LOW);
        delay(100);
      } else {
        report_error_message(String("Unknown command type: ") + command["type"].as<String>());
      }
    }

    delay(10);
  }
}

void loop() {
  delay(1000);
}
