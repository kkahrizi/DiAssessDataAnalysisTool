/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;

import java.io.File;

/**
 *
 * @author kamin
 */
public class LabelManager {
    private int numberOfOpenLabelingFrames;
    public LabeledImage officialImage;
    public Rack[] allRacks;
    public LabeledSample[] allSamples;
    private File outputFolder;
    
    public LabelManager(File outputLocation){
        numberOfOpenLabelingFrames = 0;
        outputFolder = outputLocation;
        officialImage = new LabeledImage();
        allRacks = new Rack[0];
    }
    
    public int getNumFrames(){
        return numberOfOpenLabelingFrames;
    }
    
    public int incrementNumFrames(){
        numberOfOpenLabelingFrames++;
        return numberOfOpenLabelingFrames;
    }
    
    public int decrementNumFrames(){
        numberOfOpenLabelingFrames--;
        return numberOfOpenLabelingFrames;
    }
    
    public void addSample(LabeledSample thisSample){
        officialImage.addSample(thisSample);
    }
    
    public void addTime(LabeledTime thisTime){
        if (allRacks.length == 0){
            allRacks = new Rack[1];
            allRacks[0] = new Rack();
            allRacks[0].addTime(thisTime);
        } else {
            Rack[] newRacks = new Rack[allRacks.length+1];
            for (int i = 0; i < allRacks.length; i++){
                newRacks[i] = allRacks[i];
                if(allRacks[i].doesBelong(thisTime)){
                    allRacks[i].addTime(thisTime);
                    return;
                }
            }
            Rack newRack = new Rack();
            newRack.addTime(thisTime);
            newRacks[allRacks.length] = newRack;
            allRacks = newRacks;
            
        }
    }
    
    public void addRack(Rack thisRack,LabeledTime thisTime){
        thisRack.addTime(thisTime);
        
    }
    
    public void storeTubes(Tube[] tubes, int rack, String time){
        LabeledTime newTime = new LabeledTime(tubes,time,rack);
        addTime(newTime);
    }
    
    public void convertRackToSample(LabeledTime time){
        int rackNumberToLabel = time.getRack();
        Rack rackToLabel = null;
        for (int i = 0; i < allRacks.length; i++){
            rackToLabel = allRacks[i];
            if (rackToLabel.getRackNumber() == rackNumberToLabel){
                if(!rackToLabel.labelRack(time)){
                    System.out.println("Failed to use labeled tube template on all racks");
                }
            }
        }
        if (rackToLabel == null){
            System.out.println("Only one time in this rack");
            rackToLabel = new Rack();  
        } 
        rackToLabel.addTime(time);
        LabeledSample[] allSamples = rackToLabel.convertToSamples();
        
        
    }
    
    public void saveImages(){
        System.out.println("We are gonna save");
        
        LabeledSample[] allSamples = officialImage.getSamples();
        for (int i = 0; i < allSamples.length; i++){
            LabeledSample aSample = allSamples[i];
            System.out.println(aSample.getSampleString());
            LabeledTime[] ourTimes = aSample.getTimes();
            
        }
        
         
    }
}
    

