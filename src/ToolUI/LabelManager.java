/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

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
            System.out.println("Making new rack " + thisTime.getRack() + " from time "
                    + thisTime.getTime());
        } else {
            Rack[] newRacks = new Rack[allRacks.length+1];
            for (int i = 0; i < allRacks.length; i++){
                newRacks[i] = allRacks[i];
                if(allRacks[i].doesBelong(thisTime)){
                    allRacks[i].addTime(thisTime);
                    System.out.println("Adding time " + thisTime.getTime() + " of rack "
                            + thisTime.getRack());
                    System.out.println(" To rack " + allRacks[i].getRackNumber());
                    LabeledTime[] otherTimesInRack = allRacks[i].getTimes();
                    for (int k = 0; k < otherTimesInRack.length; k++) {
                        System.out.println(" The other time in this rack is " + otherTimesInRack[k].getTime());

                    }
                    return;
                }
            }
            Rack newRack = new Rack();
            newRack.addTime(thisTime);
            newRacks[allRacks.length] = newRack;
            allRacks = newRacks;
            System.out.println("Adding time " + thisTime.getTime() + " of rack " + 
                    thisTime.getRack());
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
        System.out.println("Converting rack " + rackNumberToLabel);
        LabeledSample[] myConvertedSample = new LabeledSample[0];
        Rack rackToLabel = null;
        
        for (int i = 0; i < allRacks.length; i++){
            rackToLabel = allRacks[i];
            if (rackToLabel.getRackNumber() == rackNumberToLabel){
                LabeledTime[] timesToConvert = rackToLabel.getTimes();
                for (int j = 0; j < timesToConvert.length; j++){
                    System.out.println("Labeling time " + timesToConvert[j].getTime() + " at rack "
                    + timesToConvert[j].getRack());
                }
                LabeledSample converted = rackToLabel.labelRack(time);
                officialImage.addSample(converted);
            }
        }
        if (rackToLabel == null){
            System.out.println("Only one time in this rack");
            LabeledSample mySample = new LabeledSample();
            mySample.addTime(time);
            officialImage.addSample(mySample);
        }         
    }
    
    private LabeledSample[] appendValue(LabeledSample[] currentSample, LabeledSample newSample){
        ArrayList<LabeledSample> temp = new ArrayList<LabeledSample>(Arrays.asList(currentSample));
        temp.add(newSample);
        return (LabeledSample[])temp.toArray();
        
    }
    
    public void saveImages(){
        System.out.println("We are gonna save");
        
        LabeledSample[] allSamples = officialImage.getSamples();
        for (int i = 0; i < allSamples.length; i++){
            LabeledSample aSample = allSamples[i];
            System.out.println(aSample.getSampleString());
            LabeledTime[] ourTimes = aSample.getTimes();
            for (int j = 0; j < ourTimes.length; j++){
                System.out.println(ourTimes[j].getTime());
            }
        }
        
         
    }
}
    

