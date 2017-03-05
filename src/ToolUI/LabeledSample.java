/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;

/**
 *
 * @author kamin
 */
public class LabeledSample {
    String sampleString;
    String[] timeStrings;
    LabeledTime[] myTimes;
    
    public LabeledSample(LabeledTime[] allTimes, String sample){
        sampleString = sample;
        myTimes = allTimes;
        timeStrings = new String[allTimes.length];
        for(int i = 0; i < myTimes.length; i++){
            timeStrings[i] = myTimes[i].getTime();
        }
    }
    
    public LabeledSample(){
        myTimes = new LabeledTime[0];
        sampleString = "";
        timeStrings = new String[0];
    }
    
    public LabeledTime[] getTimes(){
        return myTimes;
    }
    
    public void setSampleString(String val){
        sampleString = val;
    }
    
    public String getSampleString(){
        return sampleString;
    }
    
    public String[] getTimeStrings(){
        return timeStrings;
    }
    
    public void addTime(LabeledTime thisTime){
        if (myTimes.length == 0){
            myTimes = new LabeledTime[1];
            myTimes[0] = thisTime;
            sampleString = thisTime.getLabel();
            addTimeString(thisTime.getTime());
                return;
        }
        LabeledTime[] newTimes = new LabeledTime[myTimes.length+1];
        for(int i = 0; i < myTimes.length; i++){
            newTimes[i] = myTimes[i];
        }
        newTimes[myTimes.length] = thisTime;
        myTimes = newTimes;
        
    }
    
    private void addTimeString(String aTime){
        for (int i = 0; i < timeStrings.length; i++){
            if(timeStrings[i].equalsIgnoreCase(aTime)){ 
                return;
            }
        }
        String[] newTimeStrings = new String[timeStrings.length+1];
        for(int i = 0; i < timeStrings.length; i++){
            newTimeStrings[i] = timeStrings[i];
        }
        newTimeStrings[timeStrings.length] = aTime;
        timeStrings = newTimeStrings;
    }
    
}
