#include <Arduino.h>
#include <ArduinoJson.h>

// Annoyingly Arduino.h #defines min and max, breaking the optional header.
#undef min
#undef max

#include <optional>

#include "rfid.hpp"


const unsigned long rfid_baudrate         = 9600;
const unsigned long rfid_poll_period      = 1000;
const unsigned long rfid_not_seen_timeout = 6000;

const int rfid_reset_pin = 18;

void report_debug_message(String message);
void usb_serial_write(JsonDocument &document);


void report_arrival(String &tag_id) {
  StaticJsonDocument<512> doc;
  doc["type"] = "tag_arrived";
  doc["tag"]  = tag_id;
  usb_serial_write(doc);
}

void report_removal() {
  StaticJsonDocument<512> doc;
  doc["type"] = "tag_removed";
  usb_serial_write(doc);
}

void rfid_control() {

  rfid_t rfid(Serial1, rfid_reset_pin);

  Serial1.begin(rfid_baudrate);

  report_debug_message("RFID initialized.");

  unsigned long last_poll_time = 0;
  unsigned long last_seen_time = 0;

  std::optional<String> current_tag;


  while (true) {

    auto t0 = millis();

    auto tag = rfid.poll();

    if (tag) {

      if (tag != current_tag) {
        if (current_tag) {
          report_removal();
        }
        current_tag = tag;
        report_arrival(*tag);
      }
      last_seen_time = millis();
    }

    if (rfid_not_seen_timeout < millis() - last_seen_time && current_tag) {
      report_removal();
      current_tag = {};
    }

    auto t1 = millis();

    delay(rfid_poll_period - (t1 - t0));
  }
}
