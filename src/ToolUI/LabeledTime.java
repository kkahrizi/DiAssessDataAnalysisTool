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
public class LabeledTime implements Comparable {
    Tube[] allTubes;
    String time;
    String label;
    int rack;
    
    public LabeledTime(Tube[] theseTubes, String theTime, int theRack){
        allTubes = theseTubes;
        time = theTime;
        rack = theRack;
    }
    
    public LabeledTime(){
        allTubes = new Tube[0];
        time = "";
        
    }
    
    public boolean hasLabel(){
        if (label == null){
            return false;
        }
        return true;
    }
    
    public String getLabel(){
        return label;
    }
    
    public void label(String newLabel){
        label = newLabel;
    }
    
    public boolean isSameRack(LabeledTime otherTime){
        if(otherTime.getRack()==rack){
            return true;
        }
        return false;
    }

    public void setRack(int thisRack){
        rack = thisRack;
    }
    
    public int getRack(){
        return rack;
    }
    
    public Tube[] getTubes(){
        return allTubes;
    }
    
    public String getTime(){
        return time;
    }
    
    public void setTime(String time){
         this.time = time;
    }
    
     
    
    public LabeledTime label(LabeledTime labels){
        LabeledTime convertedSample = new LabeledTime();
        convertedSample.setTime(labels.getTime());
        convertedSample.setRack(labels.getRack());
        convertedSample.label(labels.getLabel());
        Tube[] labeledTubes = labels.getTubes();
        for (int i = 0; i < labeledTubes.length; i++){
            Tube labeledTube = labeledTubes[i];
            for (int j = 0; j < allTubes.length; j++){
                Tube toBeLabeled = allTubes[j];
                if (toBeLabeled.getPosition() == labeledTube.getPosition()){
                    toBeLabeled.addLabel(labeledTube.getLabel());
                    convertedSample.addTube(toBeLabeled);
                }
            }
        }
        
        return convertedSample;
    }
    
    public int compareTo(Object o){
        LabeledTime otherTime = (LabeledTime) o;
        int otherTimeInSeconds = Integer.parseInt(otherTime.getTime());
        int thisTimeInSeconds = Integer.parseInt(getTime());
        return thisTimeInSeconds - otherTimeInSeconds;
    }
    
    public void addTube(Tube tubeToAdd){
        if(allTubes.length == 0){
            allTubes = new Tube[1];
            allTubes[0] = tubeToAdd;
            time = tubeToAdd.getTime();
           
            return;
        }
        Tube[] newTubes = new Tube[allTubes.length+1];
        for (int i = 0; i < allTubes.length; i++){
            newTubes[i] = allTubes[i];
        }
        newTubes[allTubes.length] = tubeToAdd;
        allTubes = newTubes;
    }
}
