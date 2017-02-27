/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;


import java.util.ArrayList;

/**
 *
 * @author kamin
 */
public class Rack {
    
     int rackNumber;
     LabeledTime[] myTimes;
     
     public Rack(int noRack, LabeledTime[] timeImages){
         rackNumber = noRack;
         myTimes = timeImages;
     }
        
     public Rack(){
         myTimes = new LabeledTime[0];
         rackNumber = -1;
     }
     
     public LabeledTime[] getTimes(){
         return myTimes;
     }
     
     public boolean doesBelong(LabeledTime thisTime){
         if (thisTime.getRack() == rackNumber){
             return true;
         }
         return false;
     }
     
     public int getRackNumber(){
         return rackNumber;
     }
     
     public boolean labelRack(LabeledTime labeledRack){
         for (int i = 0; i < myTimes.length; i++){
             if(myTimes[i].label(labeledRack)){
                 return true;
             } else {
                 return false;
             }
         }
         return true;
     }
     
  
     public void addTime(LabeledTime thisTime){
         if (myTimes == null || myTimes.length == 0){
             myTimes = new LabeledTime[1];
             myTimes[0] = thisTime;
             rackNumber = thisTime.getRack();
             return;
         }
         LabeledTime[] newTimes = new LabeledTime[myTimes.length + 1];
         for (int i = 0; i < myTimes.length; i++){
             newTimes[i] = myTimes[i];
         }
         newTimes[myTimes.length] = thisTime;
         myTimes = newTimes;       
            
     }
     
     public LabeledSample[] convertToSamples(){
        
         ArrayList<LabeledTime> separatedTimes = null;
         for (int i = 0; i < myTimes.length; i++) {
             Tube[] labeledTubes = myTimes[i].getTubes();
             separatedTimes = new ArrayList<LabeledTime>();

             for (int j = 0; j < labeledTubes.length; j++) {
                 boolean isNewTime = true;
                 Tube thisTube = labeledTubes[j];
                 for (LabeledTime labeledTime : separatedTimes) {
                     if (labeledTime.hasLabel() && labeledTime.getLabel().equals(thisTube.getLabel())
                             && labeledTime.getTime().equals(thisTube.getTime())) {
                         labeledTime.addTube(thisTube);
                         isNewTime = false;
                         break;
                     }
                 }
                 if (isNewTime) {
                     LabeledTime newTime = new LabeledTime();
                     newTime.label(thisTube.getLabel());
                     newTime.setTime(thisTube.getTime());
                     newTime.addTube(thisTube);
                     separatedTimes.add(newTime);
                 }
             }

         }
         LabeledSample[] allSamples = new LabeledSample[0];
         while(!separatedTimes.isEmpty()){
             LabeledSample newSample = new LabeledSample();
             LabeledTime labeledTime = separatedTimes.get(0);
             newSample.addTime(labeledTime);
             separatedTimes.remove(labeledTime);
             String time = labeledTime.getTime();
             String label = labeledTime.getLabel();
             for (LabeledTime otherTime : separatedTimes){
                 if(otherTime.getTime().equals(time) && otherTime.getLabel().equals(label)){
                     newSample.addTime(otherTime);
                     separatedTimes.remove(otherTime);
                 }
             }
             allSamples = addNewSample(allSamples, newSample);
            
         }
         return allSamples;
     }
     
     public LabeledSample[] addNewSample(LabeledSample[] original, LabeledSample toAdd){
         LabeledSample[] newSamples = new LabeledSample[original.length + 1];
         for (int i = 0; i < original.length; i++) {
             newSamples[i] = original[i];
         }
         newSamples[original.length] = toAdd;
         return newSamples;
             
     }
    
}
