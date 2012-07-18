char val[4];
int pin1 = 22;
int pin2 = 24;  
int pin3 = 26;
int pin4 = 28;
int pin5 = 30;
int pin6 = 32;
int index = 0;

void setup() 
{
  pinMode(pin1, OUTPUT);
  pinMode(pin2, OUTPUT);
  pinMode(pin3, OUTPUT);
  pinMode(pin4, OUTPUT);
  pinMode(pin5, OUTPUT);
  pinMode(pin6, OUTPUT);
  Serial.begin(9600); 
} 

void loop()
{
  if(Serial.available()){
    if(index < 3){
      val[index] = Serial.read();       
      Serial.print(val[index]);
      index++;
    }

    val[index] = '\0';

    Serial.println("");

    if(index == 3){
      if(strcmp("p11", val) == 0){
        digitalWrite(pin1, HIGH);
      }
      else if(strcmp("p10", val) == 0){
        digitalWrite(pin1, LOW);
      }
      else if(strcmp("p21", val) == 0){
        digitalWrite(pin2, HIGH);
      }
      else if(strcmp("p20", val) == 0){
        digitalWrite(pin2, LOW);
      }
      else if(strcmp("p31", val) == 0){
        digitalWrite(pin3, HIGH);
      }
      else if(strcmp("p30", val) == 0){
        digitalWrite(pin3, LOW);
      }
      else if(strcmp("p41", val) == 0){
        digitalWrite(pin4, HIGH);
      }
      else if(strcmp("p40", val) == 0){
        digitalWrite(pin4, LOW);
      }
      else if(strcmp("p51", val) == 0){
        digitalWrite(pin5, HIGH);
      }
      else if(strcmp("p50", val) == 0){
        digitalWrite(pin5, LOW);
      }
      else if(strcmp("p61", val) == 0){
        digitalWrite(pin6, HIGH);
      }
      else if(strcmp("p60", val) == 0){
        digitalWrite(pin6, LOW);
      }
      else if(strcmp("ar1", val) == 0){
        digitalWrite(pin1, HIGH);
        digitalWrite(pin2, HIGH);
        digitalWrite(pin3, HIGH);

      }
      else if(strcmp("ar0", val) == 0){
        digitalWrite(pin1, LOW);
        digitalWrite(pin2, LOW);
        digitalWrite(pin3, LOW);

      }
      else if(strcmp("ag1", val) == 0){
        digitalWrite(pin4, HIGH);
        digitalWrite(pin5, HIGH);
        digitalWrite(pin6, HIGH);

      }
      else if(strcmp("ag0", val) == 0){
        digitalWrite(pin4, LOW);
        digitalWrite(pin5, LOW);
        digitalWrite(pin6, LOW);

      }

      index = 0;
    }
  }
}






