#include <Arduino.h>
#include <ArduinoJson.h>
// #include <cstring>

const int button_count = 6;
const int button_pins[button_count] = { A0, A1, A2, A3, A4, A5 };

const long button_press_delay         = 200;
const long button_callibrate_readings = 1000;
const long button_callibrate_delay    = 1;

void report_debug_message(String message);
void usb_serial_write(JsonDocument &document);

inline int count_bits(uint64_t x) {
  int bits = 0;
  while (x) {
    bits += (x & 1);
    x >>= 1;
  }
  return bits;
}

void callibrate(int button_threshold_out[]) {

  uint64_t sums[button_count];

  for (int button = 0; button < button_count; button++) {
    sums[button] = 0;
  }

  for (int reading = 0; reading < button_callibrate_readings; reading++) {
    for (int button = 0; button < button_count; button++) {
      sums[button] += analogRead(button_pins[button]);
    }
    delay(button_callibrate_delay);
  }

  String message;
  message.reserve(100);
  message += "Button callibration: [";

  for (int button = 0; button < button_count; button++) {
    button_threshold_out[button] = (int)(sums[button] / ((uint64_t)button_callibrate_readings)) + 50;
    message += button_threshold_out[button];
    message += ",";
  }

  message[message.length()-1] = ']';
  report_debug_message(message);
}

void report_press(int button) {
  StaticJsonDocument<512> doc;
  doc["type"] = "key_pressed";
  doc["key"]  = button;
  usb_serial_write(doc);
}


void button_control() {

  uint64_t button_bounce_mask[button_count] = {0};
  bool     button_pressed[button_count] = {0};
  int      button_thresholds[button_count] = {0};

  callibrate(button_thresholds);

  unsigned long last_press_time = millis();
  unsigned long last_read_time = millis();

  while (true) {

    for (int button = 0; button < button_count; button++) {
      int value = analogRead(button_pins[button]);

      button_bounce_mask[button] <<= 1;
      button_bounce_mask[button] |=  (value > button_thresholds[button] ? 1 : 0);

      int bits = count_bits(button_bounce_mask[button]);

      if (bits >= 32 && !button_pressed[button] && button_press_delay < millis() - last_press_time) {
        button_pressed[button] = true;
        last_press_time = millis();
        report_press(button);
      }

      if (bits <= 0 && button_pressed[button]) {
        button_pressed[button] = false;
      }
    }

    delay(1);
  }
}
