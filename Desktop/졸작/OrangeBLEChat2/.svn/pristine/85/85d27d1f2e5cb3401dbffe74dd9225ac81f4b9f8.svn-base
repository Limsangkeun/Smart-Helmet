// 블루투스 통신을 위한 SoftwareSerial 라이브러리
#include <SoftwareSerial.h>

// SoftwareSerial(RX, TX)
SoftwareSerial BTSerial(4, 5);

void setup()
{
    Serial.begin(9600);
    Serial.println("Hello! OrangeBoard");

    BTSerial.begin(9600);
}

void loop()
{
    // 블루투스로 부터 수신된 데이터를 읽는다.
    if (BTSerial.available()) {
        byte buf[20];
        Serial.print("recv: ");
        // 블루투스로부터 데이터를 수신한다.
        byte len = BTSerial.readBytes(buf, 20);
        // 수신된 데이터를 시리얼 모니터에 출력한다.
        Serial.write(buf, len);
        Serial.println();
    }
    // 시리얼 모니터로 부터 입력 받은 데이터를 블루투스로 송신한다.
    if (Serial.available()) {
        byte buf[20];
        byte len = Serial.readBytes(buf, 20);
        // 입력받은 데이터 길이가 20보다 작다면 20byte를 end of text문자열로 송신 버퍼를 채워준다.
        BTSerial.write(buf, len);
        if (len < 20) {
            for (int i = len ; i < 20 ; i++) {
                BTSerial.write(0x03);
            }
        }
        Serial.print("send: ");
        Serial.write(buf, len);
        Serial.println();
    }
}