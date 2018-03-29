// 블루투스 통신을 위한 SoftwareSerial 라이브러리
#include <SoftwareSerial.h>

// SoftwareSerial(RX, TX)
SoftwareSerial BTSerial(4, 5);

// 데이터를 20byte 기준으로 전송하기 위한 버퍼
byte buf[20];
// 데이터 버퍼이 데이터 사이즈 또는 end offset
int pos = 0;
// 작업을 수행한 기준 시간
unsigned long baseTime;

void setup()
{
    Serial.begin(9600);
    Serial.println("Hello!");

    BTSerial.begin(9600);
}

void loop()
{
    // 블루투스로 부터 수신된 데이터를 읽는다.
    if (BTSerial.available()) {
        // 수신된 데이터를 시리얼 모니터에 출력한다.
        Serial.write(BTSerial.read());
    }
    // 시리얼 모니터로 부터 입력 받은 데이터를 블루투스로 송신한다.
    if (Serial.available()) {
        buf[pos++] = Serial.read();
        baseTime = millis();
        // 시리얼 모니터에서 입력한 데이터가 20byte에 도달하면 버퍼를 비우도록 시간을 조정한다.
        if (pos >= 19) delay(100);
    }

    if (pos != 0) {
        unsigned long elapsed = millis() - baseTime;
        // 시리얼 모니터에서의 하나의 문장은 연속적인 입력값으로 들어오게되며
        // 이 경우 millisecond 단위이며, 이를 넘어갈 경우 다른 메시지로 간주한다.
        // 아래에서는 100 milliseconds 이하를 하나의 연속적인 메시지로 정의한다.
        if (elapsed > 100 && elapsed < 1000) {
            Serial.print("send :");
            // BLE의 메시지 전송 트리거링 단위는 20byte이므로
            // 메시지가 20byte보다 작을 경우 end of text로 채워준다.
            for (int i = 0 ; i < 20 ; i++) {
                if (i < pos) {
                    BTSerial.write(buf[i]);
                    Serial.write(buf[i]);
                } else {
                    BTSerial.write(0x03);
                }
            }
            Serial.println();
            pos = 0;
        }
    }
}