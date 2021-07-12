#ifndef RFID_HPP_
#define RFID_HPP_

#include <Arduino.h>
#include <optional>

#define RFID_BAUDRATE (9600)
#define RFID_PACKET_HEADER (0x02)
#define RFID_PACKET_FOOTER (0x03)
#define RFID_PACKET_CHECKSUM_BEGIN (1)
#define RFID_PACKET_CHECKSUM_LENGTH (26)
#define RFID_STARTUP_DELAY (200)

struct rfid_packet_t {
  uint8_t header;
  uint8_t card_number[10];
  uint8_t country_number[4];
  uint8_t data_block;
  uint8_t animal_flag;
  uint8_t reserved_1[4];
  uint8_t reserved_2[6];
  uint8_t checksum;
  uint8_t checksum_inverted;
  uint8_t footer;

  uint8_t compute_checksum() {
    uint8_t *packet_as_bytes = (uint8_t*)this;
    uint8_t result = 0;

    for (int i = RFID_PACKET_CHECKSUM_BEGIN; i < (RFID_PACKET_CHECKSUM_BEGIN + RFID_PACKET_CHECKSUM_LENGTH); i++) {
      result ^= packet_as_bytes[i];
    }

    return result;
  }

  bool is_okay() {

    uint8_t checksum = this->compute_checksum();
    uint8_t checksum_inverted = ~checksum;

    return
      this->header == RFID_PACKET_HEADER &&
      this->footer == RFID_PACKET_FOOTER &&
      this->checksum == checksum &&
      this->checksum_inverted == checksum_inverted;
  }

  String string_id() {

    char buffer[40];
    sprintf(buffer,
            "%x%x%x%x%x%x%x%x%x%x",
            this->card_number[0],
            this->card_number[1],
            this->card_number[2],
            this->card_number[3],
            this->card_number[4],
            this->card_number[5],
            this->card_number[6],
            this->card_number[7],
            this->card_number[8],
            this->card_number[9]
            );

    return buffer;
  }
};

static_assert(sizeof(rfid_packet_t) == 30);


struct rfid_t {

  USARTClass &serial;
  int reset_pin;

  rfid_t(USARTClass &serial, int reset_pin)
    : serial(serial)
    , reset_pin(reset_pin) {}

  void setup() {
    pinMode(this->reset_pin, INPUT);
    serial.begin(RFID_BAUDRATE);
  }

  void reset() {
    pinMode(this->reset_pin, OUTPUT);
    digitalWrite(this->reset_pin, LOW);
    delay(100);
    pinMode(this->reset_pin, INPUT);
  }

  std::optional<String> poll() {

    this->reset();
    delay(RFID_STARTUP_DELAY);

    while (this->serial.available() && this->serial.peek() != RFID_PACKET_HEADER) {
      this->serial.read();
    }

    if (this->serial.available() >= sizeof(rfid_packet_t)) {
      rfid_packet_t packet;
      int bytes_read = this->serial.readBytes((uint8_t*)&packet, sizeof(packet));

      if (bytes_read == sizeof(packet)) {
        return packet.is_okay() ? std::make_optional(packet.string_id()) : std::nullopt;
      }
    } else {
      return std::nullopt;
    }
  }
};

namespace utils {

  void print_byte(uint8_t b) {
    SerialUSB.print("0x");
    SerialUSB.print((b >> 4) & 0x0F, HEX);
    SerialUSB.print((b >> 0) & 0x0F, HEX);
  }

  void print_key_value(const char *key, uint8_t *data, uint8_t len) {
    SerialUSB.print(key);
    for (int i = 0; i < len; i++) {
      SerialUSB.print(" ");
      print_byte(data[i]);
    }
    SerialUSB.println();
  }

  void rfid_packet_serial_dump(rfid_packet_t &packet) {
    print_key_value("header",            &packet.header,            sizeof(packet.header));
    print_key_value("card_number",        packet.card_number,       sizeof(packet.card_number));
    print_key_value("country_number",     packet.country_number,    sizeof(packet.country_number));
    print_key_value("data_block",        &packet.data_block,        sizeof(packet.data_block));
    print_key_value("animal_flag",       &packet.animal_flag,       sizeof(packet.animal_flag));
    print_key_value("checksum",          &packet.checksum,          sizeof(packet.checksum));
    print_key_value("checksum_inverted", &packet.checksum_inverted, sizeof(packet.checksum_inverted));
    print_key_value("footer",            &packet.footer,            sizeof(packet.footer));
    SerialUSB.println();

    SerialUSB.print("formatted card id ");
    SerialUSB.print(packet.string_id());
    SerialUSB.println();

    if (packet.header != RFID_PACKET_HEADER) {
      SerialUSB.print("incorrect header, expected ");
      print_byte(RFID_PACKET_HEADER);
      SerialUSB.print(" found ");
      print_byte(packet.header);
      SerialUSB.print(".");
      SerialUSB.println();
    } else {
      SerialUSB.println("header ok.");
    }

    uint8_t checksum = packet.compute_checksum();
    uint8_t checksum_inverted = ~checksum;

    if (packet.checksum != checksum) {
      SerialUSB.print("incorrect checksum, read ");
      print_byte(packet.checksum);
      SerialUSB.print(" computed ");
      print_byte(checksum);
      SerialUSB.print(".");
      SerialUSB.println();
    } else {
      SerialUSB.println("checksum ok.");
    }

    if (packet.checksum_inverted != checksum_inverted) {
      SerialUSB.print("incorrect inverted checksum, read ");
      print_byte(packet.checksum_inverted);
      SerialUSB.print(" computed ");
      print_byte(checksum_inverted);
      SerialUSB.print(".");
      SerialUSB.println();
    } else {
      SerialUSB.println("inverted checksum ok.");
    }

    if (packet.footer != RFID_PACKET_FOOTER) {
      SerialUSB.print("incorrect footer, expected ");
      print_byte(RFID_PACKET_FOOTER);
      SerialUSB.print(" found ");
      print_byte(packet.footer);
      SerialUSB.print(".");
      SerialUSB.println();
    } else {
      SerialUSB.println("footer ok.");
    }
  }
}

#endif
