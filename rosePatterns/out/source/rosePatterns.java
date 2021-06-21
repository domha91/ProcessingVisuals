import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.io.*; 
import java.util.Iterator; 
import oscP5.*; 
import netP5.*; 
import ddf.minim.*; 
import ddf.minim.analysis.*; 
import peasy.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class rosePatterns extends PApplet {

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

// Variables that define the "areas" of the spectrum
// For example, for bass, we take only the first 4% of the total spectrum
float specLow = 0.03f; // 3%
float specMid = 0.125f;  // 12.5%
float specHi = 0.20f;   // 20%

// This leaves 64% of the possible spectrum that will not be used.
// These values are generally too high for the human ear anyway.

// Score values for each zone
float scoreLow = 0;
float scoreMid = 0;
float scoreHi = 0;
float scoreGlobal = 0;

// Previous values, to soften the reduction
float oldScoreLow = scoreLow;
float oldScoreMid = scoreMid;
float oldScoreHi = scoreHi;

// Softening value
float scoreDecreaseRate = 25;

// Lines that appear on the sides
int nbwalls = 500;
Wall[] walls;

//Array of orbits
ArrayList<Orbit> orbits = new ArrayList<Orbit>();
PFont font;

//Cycles per second
float cps = 0;
//Cycles per hour
float cph = 0;
//Number of cycles data that are shown?
float showCycles = 4;
//TODO: find the number of orbits automatically
int orbitn = 12;
float lastCycle = 0;
float lastTime = 0;
int orbitHeight = 9;
int h = orbitHeight - 4;
float inc = 0.00f;
float theta;
float theta_vel;
float theta_acc;
int roseRadius = 400;
int r = 200;
float eyeRadius = 150;
int offWidth = 1920/2;
int offHeight = 1080/2;

public void setup(){
  //Set title of the window
  surface.setTitle("dominic Live");
  
  
  textSize(40);
  oscP5 = new OscP5(this,2020);
  minim = new Minim(this);
  
  // use the getLineIn method of the Minim object to get an AudioInput
  in = minim.getLineIn();
  //TODO: can BeatDetect work with in? (Previous line)
  beat= new BeatDetect();
  // FFT for audio in
  fft = new FFT(in.bufferSize(), in.sampleRate());
  
  //As many walls as we want
  walls = new Wall[nbwalls];
  
  //Create the walls objects
  //left walls
  for (int i = 0; i < nbwalls; i+=4) {
   walls[i] = new Wall(0, height/2, 10, height); 
  }
  
  //right walls
  for (int i = 1; i < nbwalls; i+=4) {
   walls[i] = new Wall(width, height/2, 10, height); 
  }
  
  //bottom walls
  for (int i = 2; i < nbwalls; i+=4) {
   walls[i] = new Wall(width/2, height, width, 10); 
  }
  
  //top walls
  for (int i = 3; i < nbwalls; i+=4) {
   walls[i] = new Wall(width/2, 0, width, 10); 
  }
  
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
  theta = 0;
  theta_vel = 0;
  theta_acc = 0.0001f;
  noCursor();
}

public void draw(){
  
   //forward FFT frame
   fft.forward(in.mix);
   
   stroke(0);
   
  //  translate(width/2, height/2);
   
   //Calculate number of cycles per hour ( a set will last about an hour)
   cph = cps * 3600;
   
   
   //TODO: Use value of lastCycle to sequence different scenes 
   //(trigger different functions)
   sceneSwitcher();

  if(inc >= 0 && inc < 40 ){
   inc+=0.001f;
   }
   else if(inc > 40){
     inc = 0;
   }
  
   theta_vel += theta_acc;
   theta += theta_vel;
  
}

public void sceneSwitcher(){
  // Each scene will last about 5 minutes 
  float scene = cph / 12;

    if( lastCycle <= (scene * 1) ){
    drawTidalCycles();
    drawAllSeeingEye();
  }
  else if( lastCycle > (scene * 1) && lastCycle <= (scene * 2) ){
    drawFFTCorridor();
    drawTidalCycles();
  }
  else if( lastCycle > (scene * 2) && lastCycle <= (scene * 3) ){
    drawFFTCorridor();
    drawTidalCycles();
  }
  else if( lastCycle > (scene * 3) && lastCycle <= (scene * 4) ){
    drawFFTCorridor();
    drawTidalCycles();
  }
  else if( lastCycle > (scene * 4) && lastCycle <= (scene * 5) ){
    drawFFTCorridor();
    drawTidalCycles();
  }
  else if( lastCycle > (scene * 5) && lastCycle <= (scene * 6) ){
    drawTidalCycles();
    drawRosePatterns();
  }
  else if( lastCycle > (scene * 6) && lastCycle <= (scene * 7) ){
    drawFFTCorridor();
    drawTidalCycles();
    drawAllSeeingEye();
    drawRosePatterns();
  }
  else if( lastCycle > (scene * 7) && lastCycle <= (scene * 8) ){
    drawFFTCorridor();
    drawTidalCycles();
    drawRosePatterns();
  }
  else if( lastCycle > (scene * 8) && lastCycle <= (scene * 9) ){
    drawFFTCorridor();
    drawTidalCycles();
    drawRosePatterns();
  }
  else if( lastCycle > (scene * 9) && lastCycle <= (scene * 10) ){
    drawFFTCorridor();
    drawTidalCycles();
    drawRosePatterns();
  }
  else if( lastCycle > (scene * 10) && lastCycle <= (scene * 11) ){
    drawFFTCorridor();
    drawTidalCycles();
    drawRosePatterns();
  }
  else if (lastCycle >= (scene * 11) && lastCycle < cph){
    drawFFTCorridor();
    drawTidalCycles();
    drawAllSeeingEye();
    drawRosePatterns();
    drawCrucifix();
     
     fill(0);
     text(lastCycle, 0,0,0);
  }
  else{
     fill(0);
     text(lastCycle, 0,0,0);
  }
}

public void drawTidalCycles(){
  float now = millis();
  float elapsed = now - lastTime;
  float cycle = ((elapsed * cps)/1000) + lastCycle;
   
  
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
}

public void drawAllSeeingEye(){
  background(-1);
  beat.detect(in.mix);
  fill(random(255),random(255), random(255));
  
  if (beat.isOnset()) eyeRadius = eyeRadius*0.9f;
  else eyeRadius = 150;
  ellipse(offWidth, offHeight, 2*eyeRadius, 2*eyeRadius);
  stroke(0, 50);
  
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
    strokeWeight(1);
    point(x2, y2);
    popStyle();
  }
  endShape();
}

public void drawFFTCorridor(){
   //Calculation of "scores" (power) for three categories of sound
  //First, save the old values
  oldScoreLow = scoreLow;
  oldScoreMid = scoreMid;
  oldScoreHi = scoreHi;
  
  //Reset values
  scoreLow = 0;
  scoreMid = 0;
  scoreHi = 0;
 
  //Calculate the new "scores"
  for(int i = 0; i < fft.specSize()*specLow; i++)
  {
    scoreLow += fft.getBand(i);
  }
  
  for(int i = (int)(fft.specSize()*specLow); i < fft.specSize()*specMid; i++)
  {
    scoreMid += fft.getBand(i);
  }
  
  for(int i = (int)(fft.specSize()*specMid); i < fft.specSize()*specHi; i++)
  {
    scoreHi += fft.getBand(i);
  }
  
   //Slow down the descent.
   if (oldScoreLow > scoreLow) {
     scoreLow = oldScoreLow - scoreDecreaseRate;
   }
  
   if (oldScoreMid > scoreMid) {
     scoreMid = oldScoreMid - scoreDecreaseRate;
   }
  
   if (oldScoreHi > scoreHi) {
     scoreHi = oldScoreHi - scoreDecreaseRate;
   }
  
   //Volume for all frequencies at this time, with higher sounds more prominent.
   //This allows the animation to go faster for higher pitched sounds that are more noticeable.
   float scoreGlobal = 0.66f*scoreLow + 0.8f*scoreMid + 1*scoreHi;
  
   //Subtle color of background
   background(scoreLow/10+inc*2, scoreMid/10+inc*240, scoreHi/10+inc*240);
   //background(-1);
   
  //Walls lines, here you have to keep the value of the previous strip and the next one to connect 
  //them together
  float previousBandValue = fft.getBand(0);
   
   //Distance between each line point, negative because on dimension z
  float dist = -25;
  
  //Multiply the height by this constant
  float heightMult = 2;
  
  //For each band
  for(int i = 1; i < fft.specSize(); i++)
  {
    //Value of the frequency band, we multiply the bands further away so that they are more visible.
    float bandValue = fft.getBand(i)*(1 + (i/50));
    
    //Selection of color according to the strengths of different types of sounds
    stroke((random(20)+scoreLow), (random(240)+scoreMid), (random(240)+scoreHi), 255-i);
    strokeWeight(1 + (scoreGlobal/120));
    
    //Lower left line
    line(0, height-(previousBandValue*heightMult), dist*(i-1), 0, height-(bandValue*heightMult), dist*i);
    line((previousBandValue*heightMult), height, dist*(i-1), (bandValue*heightMult), height, dist*i);
    line(0, height-(previousBandValue*heightMult), dist*(i-1), (bandValue*heightMult), height, dist*i);
    
    //top left line
    line(0, (previousBandValue*heightMult), dist*(i-1), 0, (bandValue*heightMult), dist*i);
    line((previousBandValue*heightMult), 0, dist*(i-1), (bandValue*heightMult), 0, dist*i);
    line(0, (previousBandValue*heightMult), dist*(i-1), (bandValue*heightMult), 0, dist*i);
    
    //lower straight line
    line(width, height-(previousBandValue*heightMult), dist*(i-1), width, height-(bandValue*heightMult), dist*i);
    line(width-(previousBandValue*heightMult), height, dist*(i-1), width-(bandValue*heightMult), height, dist*i);
    line(width, height-(previousBandValue*heightMult), dist*(i-1), width-(bandValue*heightMult), height, dist*i);
    
    //upper straight line
    line(width, (previousBandValue*heightMult), dist*(i-1), width, (bandValue*heightMult), dist*i);
    line(width-(previousBandValue*heightMult), 0, dist*(i-1), width-(bandValue*heightMult), 0, dist*i);
    line(width, (previousBandValue*heightMult), dist*(i-1), width-(bandValue*heightMult), 0, dist*i);
    
    //Save the value for the next loop round
    previousBandValue = bandValue;
  }
  
  //Rectangle walls
  for(int i = 0; i < nbwalls; i++)
  {
    //Each Wall is assigned a band, and its strength is sent to it.
    float intensity = fft.getBand(i%((int)(fft.specSize()*specHi)));
    walls[i].display(scoreLow, scoreMid, scoreHi, intensity, scoreGlobal);
  }
}

public void drawRosePatterns(){
   int circSides = 72;
   drawCircle(circSides,(523 + (scoreGlobal/10)));
   
   //Rose Pattern Example
   float d = 8 + inc;
   float n = 5;
   float k = n / d;
   
   beginShape();
   
   noFill();
   strokeWeight(2);
   for (float a = 0; a < TWO_PI * d; a += 0.02f) {
   float r = 400 * cos(k * a) + (scoreLow * 0.4f)*(inc/10);
   float x = r * sin(a);
   float y = r * cos(a);
   vertex(x, y);
   }
   
   endShape();
}

public void drawCrucifix(){
  //TODO: make more complex Crucifix code
  stroke(255, (lastCycle/5));
  fill(255, (lastCycle/5));
  rect(-100, -50, 200, 30, 7);
  rect(-15,-120,30,250, 7);
}




public void oscEvent(OscMessage m){
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
  
  public void add(Event event){
    events.add(0,event);
    state = !state;
  }
  public void draw(float cycle){
    Boolean state = this.state;
    
    noFill();
   
    beginShape();
    strokeWeight(2);
    //TODO: Change stroke colour based on audio and/or OSC
    stroke(random(255),random(255), random(255));
    
    vertex(width, state ? 0 : h);
    // vertex(circleEndX,circleStartY);
    Iterator<Event> i = events.iterator();
    while(i.hasNext()){
      Event event = i.next();
      float posX = (event.cycle-(cycle-showCycles))/showCycles;
      float posY =  state ? 0 : h;
      float posYInverse = state ? h : 0;
     
     
      if (posX < 0){
        i.remove();
      }
      else{
        // Linear plot
        vertex(width * posX, posY);
        vertex(width * posX, posYInverse);
        state = !state;
        
        //TODO: Circular plot
        
        
        
        //Show positional values in console
        println("posX: " + posX + ". posY: " + posY);
      }
    }
    vertex(0,state ? 0 : h);

    endShape();
     
  }
}

//Draws a circular border
//TODO: Change via OSC/audio/superformula
public void drawCircle(int sides, float r)
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

class Event{
  float cycle;
  float start;
  
  Event (float cycle, float start){
    this.cycle = cycle;
    this.start = start;
  }
}

//Class to display the lines on the sides
class Wall {
  //Minimum and maximum position Z
  float startingZ = -10000;
  float maxZ = 50;
  
  //Position values
  float x, y, z;
  float sizeX, sizeY;
  
  //Constructor
  Wall(float x, float y, float sizeX, float sizeY) {
    //Make the line appear at the specified location
    this.x = x;
    this.y = y;
    //Random depth
    this.z = random(startingZ, maxZ);  
    
    //We determine the size because the walls on the floors have a different size than those on the sides
    this.sizeX = sizeX;
    this.sizeY = sizeY;
  }
  
  //Display function
  public void display(float scoreLow, float scoreMid, float scoreHi, float intensity, float scoreGlobal) {
    //Color determined by low, medium and high sounds
    //Opacity determined by overall volume
    int displayColor = color(scoreLow, scoreMid, scoreHi, scoreGlobal);
    
    //Make the lines disappear in the distance to give an illusion of fog
    fill(displayColor, ((scoreGlobal-5)/1000)*(255+(z/25)));
    noStroke();
    
    //First band, the one that moves according to the force
    //Transformation matrix
    pushMatrix();
    
    //Shifting
    translate(x, y, z);
    
    //Enlargement
    if (intensity > 100) intensity = 100;
    scale(sizeX*(intensity/100), sizeY*(intensity/100), 20);
    
    //Creation of the "box"
    box(1);
    popMatrix();
    
    //Second strip, the one that is always the same size
    displayColor = color(scoreLow*0.5f, scoreMid*0.5f, scoreHi*0.5f, scoreGlobal);
    fill(displayColor, (scoreGlobal/5000)*(255+(z/25)));
    //Transformation matrix
    pushMatrix();
    
    //Shifting
    translate(x, y, z);
    
    //Enlargement
    scale(sizeX, sizeY, 10);
    
    //Creation of the "box"
    box(1);
    popMatrix();
    
    //Z displacement
    z+= (pow((scoreGlobal/150), 2));
    if (z >= maxZ) {
      z = startingZ;  
    }
  }
}
  public void settings() {  size(1920,1080,P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "rosePatterns" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
