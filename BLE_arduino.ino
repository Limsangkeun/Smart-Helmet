#define E 330 // 미 
// 블루투스 통신을 위한 SoftwareSerial 라이브러리
#include <SoftwareSerial.h>
#include<Wire.h>
#include <MPU6050.h>

MPU6050 mpu;
// SoftwareSerial(RX, TX)

SoftwareSerial BTSerial(4, 5);

boolean ledState = false;
boolean freefallDetected = false;
int freefallBlinkCount = 0;
int i = 0;
int j = 0;
int speakerPin = 7;
/// byte song_table [] = {30, 30, 30, 40, 50, 60, 70, 80, 90, 100,110, 120, 130, 140, 150, 160, 170, 180, 190, 200, 210, 220, 230, 240, 250, 250, 240, 230, 220, 210, 200, 190, 180, 170, 160, 150, 140, 130, 120, 110, 100, 90, 80, 70, 60, 50, 40, 30, 30, 30};
int MAX = 50;
int count1 = 0; 


// 초음파센서의 송신부를 8번핀으로 설정합니다.
int trig = 8;
// 초음파센서의 수신부를 9번핀으로 설정합니다.
int echo = 9;

int GasPin = A0;                        // 가스센서 입력을 위한 아날로그 핀

int GasPin2 =0;

int LEDpin = 12;

int LEDpin_4 = 4;

int warning[5];

int count = 0;

int sum = 0 ;


float Ro = 10000.0;
int val = 0;        // variable to store the value coming from the sensor
float Vrl = 0.0;
float Rs = 0.0;
float ratio = 0.0;

int pub = 100;

void setup()
{
  
   Serial.begin(115200);

    Serial.println("Initialize MPU6050");

  while(!mpu.begin(MPU6050_SCALE_2000DPS, MPU6050_RANGE_16G))
  {
    Serial.println("Could not find a valid MPU6050 sensor, check wiring!");
    delay(500);
  }

  mpu.setAccelPowerOnDelay(MPU6050_DELAY_3MS);
  
  mpu.setIntFreeFallEnabled(true);
  mpu.setIntZeroMotionEnabled(false);
  mpu.setIntMotionEnabled(false);
  
  mpu.setDHPFMode(MPU6050_DHPF_5HZ);

  mpu.setFreeFallDetectionThreshold(17);
  mpu.setFreeFallDetectionDuration(2);  
  
  checkSettings();
  pinMode(LEDpin_4, OUTPUT);
  pinMode(speakerPin,OUTPUT);
  digitalWrite(4, LOW);
  
  attachInterrupt(0, doInt, RISING);
  
    Serial.println("Hello! OrangeBoard");
    BTSerial.begin(9600);
      // 초음파센서의 송신부로 연결된 핀을 OUTPUT으로 설정합니다.
  pinMode(trig, OUTPUT);
  // 초음파센서의 수신부로 연결된 핀을 INPUT으로 설정합니다.
  pinMode(echo, INPUT);

  pinMode(GasPin ,INPUT);               // 아날로그 핀 A0를 입력모드로 설정

  pinMode(LEDpin, OUTPUT);
  
}

void doInt()
{
  freefallBlinkCount = 0;
  freefallDetected = true;  
}

void checkSettings()
{
  Serial.println();
  
  Serial.print(" * Sleep Mode:                ");
  Serial.println(mpu.getSleepEnabled() ? "Enabled" : "Disabled");

  Serial.print(" * Motion Interrupt:     ");
  Serial.println(mpu.getIntMotionEnabled() ? "Enabled" : "Disabled");

  Serial.print(" * Zero Motion Interrupt:     ");
  Serial.println(mpu.getIntZeroMotionEnabled() ? "Enabled" : "Disabled");

  Serial.print(" * Free Fall Interrupt:       ");
  Serial.println(mpu.getIntFreeFallEnabled() ? "Enabled" : "Disabled");

  Serial.print(" * Free Fal Threshold:          ");
  Serial.println(mpu.getFreeFallDetectionThreshold());

  Serial.print(" * Free FallDuration:           ");
  Serial.println(mpu.getFreeFallDetectionDuration());
  
  Serial.print(" * Clock Source:              ");
  switch(mpu.getClockSource())
  {
    case MPU6050_CLOCK_KEEP_RESET:     Serial.println("Stops the clock and keeps the timing generator in reset"); break;
    case MPU6050_CLOCK_EXTERNAL_19MHZ: Serial.println("PLL with external 19.2MHz reference"); break;
    case MPU6050_CLOCK_EXTERNAL_32KHZ: Serial.println("PLL with external 32.768kHz reference"); break;
    case MPU6050_CLOCK_PLL_ZGYRO:      Serial.println("PLL with Z axis gyroscope reference"); break;
    case MPU6050_CLOCK_PLL_YGYRO:      Serial.println("PLL with Y axis gyroscope reference"); break;
    case MPU6050_CLOCK_PLL_XGYRO:      Serial.println("PLL with X axis gyroscope reference"); break;
    case MPU6050_CLOCK_INTERNAL_8MHZ:  Serial.println("Internal 8MHz oscillator"); break;
  }
  
  Serial.print(" * Accelerometer:             ");
  switch(mpu.getRange())
  {
    case MPU6050_RANGE_16G:            Serial.println("+/- 16 g"); break;
    case MPU6050_RANGE_8G:             Serial.println("+/- 8 g"); break;
    case MPU6050_RANGE_4G:             Serial.println("+/- 4 g"); break;
    case MPU6050_RANGE_2G:             Serial.println("+/- 2 g"); break;
  }  

  Serial.print(" * Accelerometer offsets:     ");
  Serial.print(mpu.getAccelOffsetX());
  Serial.print(" / ");
  Serial.print(mpu.getAccelOffsetY());
  Serial.print(" / ");
  Serial.println(mpu.getAccelOffsetZ());

  Serial.print(" * Accelerometer power delay: ");
  switch(mpu.getAccelPowerOnDelay())
  {
    case MPU6050_DELAY_3MS:            Serial.println("3ms"); break;
    case MPU6050_DELAY_2MS:            Serial.println("2ms"); break;
    case MPU6050_DELAY_1MS:            Serial.println("1ms"); break;
    case MPU6050_NO_DELAY:             Serial.println("0ms"); break;
  }  
  
  Serial.println();
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

  Vector rawAccel = mpu.readRawAccel();
  Activites act = mpu.readActivites();

  Serial.print(act.isFreeFall);
  Serial.print("\n");

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

    delay(250);
    // 시리얼 모니터로 부터 입력 받은 데이터를 블루투스로 송신한다.


     Vrl = GasPin2 * ( 5.00 / 1024.0  );      // V
    
     Rs = 20000 * ( 5.00 - Vrl) / Vrl ;   // Ohm 
    
     ratio =  Rs/Ro;      


       warning[count] = (int)(get_CO(ratio)); 

//
//        Serial.print( "count 값 : "); 
//    Serial.println(count); 
// 
 
    count++; 
 
// 
//       Serial.print( "count 값 : "); 
//     Serial.println(count); 
//     
     if(count==4 ) 
     { 
       for(int i = 0 ; i< count ; i++) 
        { 
          
          sum = sum + warning[i]; 
         
        } 
        if(sum>1250) 
       { 
          digitalWrite(LEDpin,HIGH); 
          
          BTSerial.println("coDanger");
          
          
          Serial.println("coDanger");
          tone (speakerPin, 350, 5000);
        } 
        else{ 
        digitalWrite(LEDpin,LOW); 
       } 
        count = 0; 
         sum = 0;       
      } 

    
     
     Serial.print ( "CO ppm :");
     Serial.println((int)(get_CO(ratio)));
     //BTSerial.print("CO ppm :");   
     BTSerial.print("CO:");
     BTSerial.println((int)(get_CO(ratio)));

//     if (BTSerial.available()) {
//    char buf[20];
//    Serial.print("recv: ");
//    // 블루투스로부터 데이터를 수신한다.
//    byte len = BTSerial.readBytes(buf, 20);
//    // 수신된 데이터를 시리얼 모니터에 출력한다.
//    for (int i = 0 ; i < len ; i++) {
//      Serial.write((byte)buf[i]);
//    }
//    Serial.println();
//    
//    if( buf[0] == '1'){
//    Serial.println("1번 입력됨");
//    digitalWrite(LEDpin ,HIGH);
//    }
//    else if (buf[0] == '2')
//    {
//    digitalWrite(LEDpin ,LOW);
//      
//    }
//
//    }
    
    

     delay(250);
      BTSerial.print("distance:");
     BTSerial.println((int)distance);
     Serial.print("distance : ");
     Serial.print((int)distance);
     Serial.println();
  delay(250);

   if (freefallDetected)
  {  
    for(int i = 0 ; i < 10 ; i++){
    digitalWrite(LEDpin_4, HIGH);   // turn the LED on (HIGH is the voltage level)
  tone (speakerPin, 250, 150);
   delay(150);                       // wait for a second
   digitalWrite(LEDpin_4, LOW);    // turn the LED off by making the voltage LOW
   
   delay(150);                       // wait for a second

    }
    BTSerial.println("fallDanger");

    
    Serial.println("fallDanger");
   freefallDetected = false;
     
    }

  delay(100);
        
}
