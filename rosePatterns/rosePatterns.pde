//musicVisEx1
//No extra imports needed 
//GOD
//copy the whole sketch and change the colours to create an all seeing eye effect to be in the background
//trigger when OSC SceneChange occurs

//sketch102928 (superformula)
//No extra imports needed
//SUPERFORMULA
//TODO:watch superformula videos on YT 
//use to modify the other elements' shape semirandomly (OSC Twitch-Style Bot) and for own scene

//cubes(Processing cubes)
//No extra imports needed
//FFT EDGES
//TODO: Study the code
//change the overall shape based on the music and superformula/randomly/with SceneChange OSC message
//keep the FFT extending into the background / projection technique

//Super3dvisualiser
//imports needed: java.io, PeasyCam
//TODO: Add a mix to listen to 
//TODO: Study the code 
//Use the camera and 3D FFT somehow ( put the FFT in the corners? ) in a separate scene

//TODO:STUDY HOW TO SET UP AND TRIGGER 3 SCENES (7.5-15-30mins each)
//2D (maybe eventually 3D)SUPERFORMULA 
//3D FFT CAVERN with 3D SHAPES(based on super3dvisualiser with processing cubes)
//ROSE PATTERNS with FFT edges and 3D SHAPES(this file and processing cubes)
//EVERY SCENE features GOD 

//TODO: Add more scenes


import java.io.*;
import java.util.Iterator;
import oscP5.*;
import netP5.*;
import ddf.minim.*;
import ddf.minim.analysis.*;
import peasy.*;

OscP5 oscP5;
Minim minim;
AudioInput in;
BeatDetect beat;
FFT fft;
//TODO: Look up PeasyCam
PeasyCam cam;

float height3;
float height23;
float spectrumScale = 32;

//Array of orbits
ArrayList<Orbit> orbits = new ArrayList<Orbit>();
PFont font;

//Cycles per second
float cps = 0;
//Number of cycles data that are shown?
float showCycles = 4;
//TODO: find the number of orbits automatically
int orbitn = 12;
float lastCycle = 0;
float lastTime = 0;
int orbitHeight = 9;
int h = orbitHeight - 4;
float inc = 0.00;
int roseRadius = 400;
int r = 200;
float eyeRadius = 150;
int offWidth = 1920/2;
int offHeight = 1080/2;

void setup(){
  //Set title of the window
  surface.setTitle("dominic Live");
  
  size(1920,1080,P3D);
  textSize(40);
  oscP5 = new OscP5(this,2020);
  minim = new Minim(this);
  
  // use the getLineIn method of the Minim object to get an AudioInput
  in = minim.getLineIn();
  //TODO: can BeatDetect work with in? (Previous line)
  beat= new BeatDetect();
  // FFT for audio in
  fft = new FFT(in.bufferSize(), in.sampleRate());
  
  height3 = height/3;
  height23 = 2*height/3;
  
  //TODO: Change fonts
  //font = loadFont("Inconsolata-48.vlw");
  //textFont(font,48);
  synchronized(orbits){
    for (int i= 0; i < orbitn; ++i){
      orbits.add(new Orbit());
    }
  }
  noCursor();
}

void draw(){
   background(-1);
   stroke(0);
   beat.detect(in.mix);
   float now = millis();
   float elapsed = now - lastTime;
   float cycle = ((elapsed * cps)/1000) + lastCycle;
   
   float posX;
   float posY;
   float topY;
   float botY;
   float nudgeLeft = -100;
   // draw the waveforms so we can see what we are monitoring
   for(int i = 0; i < in.bufferSize() - 1; i++)
   {
    posX = i + offWidth + nudgeLeft;
    posY = i + offHeight;
    topY = 50 + offHeight;
    botY = 150 + offHeight; 
    line( posX, topY + in.left.get(i)*50, posX+1, topY + in.left.get(i+1)*50 );
    line( posX, botY + in.right.get(i)*50, posX+1, botY + in.right.get(i+1)*50 );
   }
  
  String monitoringState = in.isMonitoring() ? "enabled" : "disabled";
  text( "Input monitoring is currently " + monitoringState + ".", offWidth+150, offHeight );
   
   
  float centreFreq = 0;
  
  //forward FFT
  fft.forward(in.mix);
  
  // draw the full spectrum
  {
    noFill();
    for(int i = 0; i < fft.specSize(); i++)
    {
      // if the mouse is over the spectrum value we're about to draw
      // set the stroke color to red
      if ( i == mouseX )
      {
        centreFreq = fft.indexToFreq(i);
        stroke(255, 0, 0);
      }
      else
      {
          stroke(0);
      }
      posX = i + offWidth + nudgeLeft;
      posY = height3 + offHeight;
      line(posX, posY, posX, posY - fft.getBand(i)*spectrumScale);
    }
    
    fill(0, 128);
    posX = 5 + offWidth + nudgeLeft;
    posY = height3 - 25 + offWidth;
    text("Spectrum Center Frequency: " + centreFreq, posX, posY);
  }
  
  //TODO: Draw AllSeeingEye in background
  fill(random(255),random(255), random(255));
  
  if (beat.isOnset()) eyeRadius = eyeRadius*0.9;
  else eyeRadius = 150;
  ellipse(offWidth, offHeight, 2*eyeRadius, 2*eyeRadius);
  stroke(0, 50);
  //TODO: Make this global
  int bsize = in.bufferSize();
  for (int i = 0; i < bsize - 1; i+=5)
  {
    float x = (r)*cos(i*2*PI/bsize) + offWidth;
    float y = (r)*sin(i*2*PI/bsize) + offHeight;
    float x2 = (r + in.left.get(i)*100)*cos(i*2*PI/bsize) + offWidth;
    float y2 = (r + in.left.get(i)*100)*sin(i*2*PI/bsize) + offHeight;
    line(x, y, x2, y2);
  }
  beginShape();
  noFill();
  stroke(0, 50);
  for (int i = 0; i < bsize; i+=30)
  {
    float x2 = (r + in.left.get(i)*100)*cos(i*2*PI/bsize) + offWidth;
    float y2 = (r + in.left.get(i)*100)*sin(i*2*PI/bsize) + offHeight;
    vertex(x2, y2);
    pushStyle();
    stroke(0);
    strokeWeight(2);
    point(x2, y2);
    popStyle();
  }
  endShape();
   
   //Guessing this uses as many threads as orbits and syncs them?
   synchronized(orbits){
     pushMatrix();
     for(int i = 0; i < orbitn; ++i){
       Orbit o = orbits.get(i);
       translate(0, orbitHeight);
       o.draw(cycle);
     }
     popMatrix();
   }
   
   translate(width / 2, height / 2);
   //TODO: Replace with bounds drawing function
   drawCircle(72,523);
   
   //Rose Pattern Example
   float d = 8 + inc;
   float n = 5;
   float k = n / d;
   
   beginShape();
   
   noFill();
   strokeWeight(1);
   for (float a = 0; a < TWO_PI * d; a += 0.02) {
   float r = 400 * cos(k * a);
   float x = r * sin(a);
   float y = r * cos(a);
   vertex(x, y);
   }
   
   endShape();
   
   inc+=0.001;
   
}

//Draws a circular border
//TODO: Change via OSC/audio
void drawCircle(int sides, float r)
{
    float angle = 360 / sides;
    beginShape();
    for (int i = 0; i < sides; i++) {
        float x = cos( radians( i * angle ) ) * r;
        float y = sin( radians( i * angle ) ) * r;
        vertex( x, y);    
    }
    endShape(CLOSE);
}

void oscEvent(OscMessage m){
  float t = millis();
  int i;
  int orbit = -1;
  float cycle = -1;
  
  for(i=0; i < m.typetag().length();++i){
    String name = m.get(i).stringValue();
    switch(name){
      case "orbit":
        orbit = m.get(i+1).intValue();
        break;
      case "cycle":
        cycle = m.get(i+1).floatValue();
        break;
      case "cps":
        cps = m.get(i+1).floatValue();
        break;
    }
    ++i;
  }
  if(orbit >= 0 && cycle >= 0){
    synchronized(orbits){
      Event event = new Event(cycle, t);
      Orbit o = orbits.get(orbit % orbitn);
      lastCycle = cycle;
      lastTime = t;
      o.add(event);
    }
  }
}

class Orbit{
  Boolean state = false;
  ArrayList<Event> events = new ArrayList<Event>();
  
  void add(Event event){
    events.add(0,event);
    state = !state;
  }
  void draw(float cycle){
    Boolean state = this.state;
    
    noFill();
    beginShape();
    strokeWeight(4);
    //TODO: Change stroke colour based on audio and/or OSC
    stroke(random(255),random(255), random(255));
    //TODO: Draw vertexes in a circle to create cycles border
    //use / recreate drawCircle(int sides, float r)
    vertex(width, state ? 0 : h);
    Iterator<Event> i = events.iterator();
    while(i.hasNext()){
      Event event = i.next();
      //change pos value so it stretches around as a circle (higher showCycles might be needed)
      float pos = (event.cycle-(cycle-showCycles))/showCycles;
      if (pos < 0){
        i.remove();
      }
      else{
        vertex(width * pos, state ? 0 : h);
        vertex(width * pos, state ? h : 0);
        state = !state;
      }
    }
    vertex(0,state ? 0 : h);
    endShape();
  }
}

class Event{
  float cycle;
  float start;
  
  Event (float cycle, float start){
    this.cycle = cycle;
    this.start = start;
  }
}
//Enable monitoring for in
void keyPressed()
{
  if ( key == 'm' || key == 'M' )
  {
    if ( in.isMonitoring() )
    {
      in.disableMonitoring();
    }
    else
    {
      in.enableMonitoring();
    }
  }
}
