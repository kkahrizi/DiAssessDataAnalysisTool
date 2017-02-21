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
public class LabeledTime {
    Tube[] allTubes;
    String time;
    String sample;
    
    public LabeledTime(Tube[] theseTubes, String theTime, String theSample){
        allTubes = theseTubes;
        time = theTime;
        sample = theSample;
    }
    
    public LabeledTime(){
        allTubes = new Tube[0];
        time = "";
        sample = "";
    }
    
    public Tube[] getTubes(){
        return allTubes;
    }
    
    public String getTime(){
        return time;
    }
    
    public String getSample(){
        return sample;
    }
    
    public void addTube(Tube tubeToAdd){
        if(allTubes.length == 0){
            allTubes = new Tube[1];
            allTubes[0] = tubeToAdd;
            time = tubeToAdd.getTime();
            sample = tubeToAdd.getSample();
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
