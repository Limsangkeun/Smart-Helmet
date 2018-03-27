// 블루투스 통신을 위한 SoftwareSerial 라이브러리
#include <SoftwareSerial.h>

// SoftwareSerial(RX, TX)
SoftwareSerial BTSerial(4, 5);

// 초음파센서의 송신부를 8번핀으로 설정합니다.
int trig = 8;
// 초음파센서의 수신부를 9번핀으로 설정합니다.
int echo = 9;

int GasPin = A0;                        // 가스센서 입력을 위한 아날로그 핀

int GasPin2 =0;

int LEDpin = 12;

int warning[10] ; 

int count= 0;

int sum = 0;


float Ro = 10000.0;
int val = 0;        // variable to store the value coming from the sensor
float Vrl = 0.0;
float Rs = 0.0;
float ratio = 0.0;

int pub = 100;

void setup()
{
    Serial.begin(9600);
    Serial.println("Hello! OrangeBoard");
    BTSerial.begin(9600);
      // 초음파센서의 송신부로 연결된 핀을 OUTPUT으로 설정합니다.
  pinMode(trig, OUTPUT);
  // 초음파센서의 수신부로 연결된 핀을 INPUT으로 설정합니다.
  pinMode(echo, INPUT);

  pinMode(GasPin ,INPUT);               // 아날로그 핀 A0를 입력모드로 설정

  pinMode(LEDpin, OUTPUT);
  
}

// get CO ppm
float get_CO (float ratio){
  float ppm = 0.0;
  ppm = 37143 * pow (ratio, -3.178);
return ppm;
}

void loop()
{
    // 블루투스로 부터 수신된 데이터를 읽는다.
//    if (BTSerial.available()) {
//        byte buf[20];
//        Serial.print("recv: ");
//         블루투스로부터 데이터를 수신한다.
//        byte len = BTSerial.readBytes(buf, 20);
//         수신된 데이터를 시리얼 모니터에 출력한다.
//        Serial.write(buf, len);
//        Serial.println();
//    }

  digitalWrite(trig, LOW);
  digitalWrite(echo, LOW);
  delayMicroseconds(2);
  digitalWrite(trig, HIGH);
  delayMicroseconds(10);
  digitalWrite(trig, LOW);

  // 수신부의 초기 로직레벨을 HIGH로 설정하고, 반사된 초음파에 의하여 ROW 레벨로 바뀌기 전까지의 시간을 측정합니다.
  // 단위는 마이크로 초입니다.
  unsigned long duration = pulseIn(echo, HIGH);

GasPin2 = analogRead(GasPin);

  // 초음파의 속도는 초당 340미터를 이동하거나, 29마이크로초 당 1센치를 이동합니다.
  // 따라서, 초음파의 이동 거리 = duration(왕복에 걸린시간) / 29 / 2 입니다.
  float Fdistance = duration / 29.0 / 2.0;

  int distance = (int)Fdistance; 

    delay(500);
    // 시리얼 모니터로 부터 입력 받은 데이터를 블루투스로 송신한다.


     Vrl = GasPin2 * ( 5.00 / 1024.0  );      // V
    
     Rs = 20000 * ( 5.00 - Vrl) / Vrl ;   // Ohm 
    
     ratio =  Rs/Ro;      
     
     Serial.print ( "CO ppm :");
     Serial.println((int)(get_CO(ratio)));
     BTSerial.print("CO:");   
     BTSerial.println((int)(get_CO(ratio)));


     if (BTSerial.available()) {
    char buf[20];
    Serial.print("recv: ");
    // 블루투스로부터 데이터를 수신한다.
    byte len = BTSerial.readBytes(buf, 20);
    // 수신된 데이터를 시리얼 모니터에 출력한다.
    for (int i = 0 ; i < len ; i++) {
      Serial.write((byte)buf[i]);
    }
    Serial.println();
    
    if( buf[0] == '1'){
    Serial.println("1번 입력됨");
    digitalWrite(LEDpin ,HIGH);
    }
    else if (buf[0] == '2')
    {
    digitalWrite(LEDpin ,LOW);
      
    }

    }

     warning[count] = (int)(get_CO(ratio));


       Serial.print( "count 값 : ");
    Serial.println(count);

    count++;

       Serial.print( "count 값 : ");
    Serial.println(count);
    
     if(count==9 )
     {
       for(int i = 0 ; i< count ; i++)
       {
         
         sum = sum + warning[i];
        
       }
       if(sum>2500)
       {
         digitalWrite(LEDpin,HIGH);

       }
       else{
       digitalWrite(LEDpin,LOW);
       }
        count = 0;
        sum = 0;      
     }
    
     delay(500);
     BTSerial.print("distance:");   
     BTSerial.println((int)distance);
   
     Serial.print((int)distance);
     Serial.println();

  delay(500);
        
}
