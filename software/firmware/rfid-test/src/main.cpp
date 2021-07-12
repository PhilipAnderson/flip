#include <Arduino.h>

#define RFID_PACKET_HEADER (0x02)
#define RFID_PACKET_FOOTER (0x03)
#define RFID_PACKET_CHECKSUM_BEGIN (1)
#define RFID_PACKET_CHECKSUM_LENGTH (26)

const int rfid_reset_pin = 18;

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
};

static_assert(sizeof(rfid_packet_t) == 30, "error");
static_assert(sizeof(rfid_packet_t::checksum_inverted) == 1, "error");

void setup() {
  SerialUSB.begin(9600);
  Serial1.begin(9600);

  pinMode(rfid_reset_pin, INPUT);
}

void rfid_reset() {
  pinMode(rfid_reset_pin, OUTPUT);
  digitalWrite(rfid_reset_pin, LOW);
  delay(200);
  pinMode(rfid_reset_pin, INPUT);
}

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


uint8_t rfid_packet_checksum(rfid_packet_t &packet) {

  uint8_t *packet_as_bytes = (uint8_t*)&packet;
  uint8_t result = 0;

  for (int i = RFID_PACKET_CHECKSUM_BEGIN; i < (RFID_PACKET_CHECKSUM_BEGIN + RFID_PACKET_CHECKSUM_LENGTH); i++) {
    result ^= packet_as_bytes[i];
  }

  return result;
}

bool rfid_packet_is_ok(rfid_packet_t &packet) {

  uint8_t checksum = rfid_packet_checksum(packet);
  uint8_t checksum_inverted = ~checksum;

  return
    packet.header == RFID_PACKET_HEADER &&
    packet.footer == RFID_PACKET_FOOTER &&
    packet.checksum == checksum &&
    packet.checksum_inverted == checksum_inverted;
}

String rfid_packet_id(rfid_packet_t &packet) {

  char buffer[40];
  sprintf(buffer,
          "%x%x%x%x%x%x%x%x%x%x",
          packet.card_number[0],
          packet.card_number[1],
          packet.card_number[2],
          packet.card_number[3],
          packet.card_number[4],
          packet.card_number[5],
          packet.card_number[6],
          packet.card_number[7],
          packet.card_number[8],
          packet.card_number[9]
          );

  return buffer;
}

void rfid_packet_serial_dump(rfid_packet_t &packet) {
  print_key_value("header", &packet.header, sizeof(packet.header));
  print_key_value("card_number", packet.card_number, sizeof(packet.card_number));
  print_key_value("country_number", packet.country_number, sizeof(packet.country_number));
  print_key_value("data_block", &packet.data_block, sizeof(packet.data_block));
  print_key_value("animal_flag", &packet.animal_flag, sizeof(packet.animal_flag));
  print_key_value("checksum", &packet.checksum, sizeof(packet.checksum));
  print_key_value("checksum_inverted", &packet.checksum_inverted, sizeof(packet.checksum_inverted));
  print_key_value("footer", &packet.footer, sizeof(packet.footer));
  SerialUSB.println();

  SerialUSB.print("formatted card id ");
  SerialUSB.print(rfid_packet_id(packet));
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

  uint8_t checksum = rfid_packet_checksum(packet);
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

void loop() {

  rfid_reset();

  delay(400);

  int available = Serial1.available();

  if (available == sizeof(rfid_packet_t)) {
    SerialUSB.println("Reading rfid packet.");

    rfid_packet_t packet;
    int bytes_read = Serial1.readBytes((uint8_t*)&packet, sizeof(rfid_packet_t));

    if (bytes_read == sizeof(rfid_packet_t)) {

      // SerialUSB.println(rfid_packet_is_ok(packet) ? "OK" : "ERROR");
      rfid_packet_serial_dump(packet);

    } else {
      SerialUSB.print("Expected ");
      SerialUSB.print(sizeof(rfid_packet_t));
      SerialUSB.print(" bytes but ");
      SerialUSB.print(bytes_read);
      SerialUSB.print(" were read. Flushing serial buffer.");
      SerialUSB.println();

      while (Serial1.available()) {
        Serial1.read();
      }
    }

  } else {
    SerialUSB.print("Expected ");
    SerialUSB.print(sizeof(rfid_packet_t));
    SerialUSB.print(" bytes but ");
    SerialUSB.print(available);
    SerialUSB.print(" are available. Flushing serial buffer.");
    SerialUSB.println();

    while (Serial1.available()) {
      SerialUSB.println(Serial1.read());
    }
  }

  // SerialUSB.println();
  // SerialUSB.println();

  delay(1000);
}
