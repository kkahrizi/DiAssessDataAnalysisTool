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
    private File outputFolder;
    
    public LabelManager(File outputLocation){
        numberOfOpenLabelingFrames = 0;
        outputFolder = outputLocation;
        officialImage = new LabeledImage();
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
        officialImage.addTime(thisTime);
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
                System.out.println("Just to check" + ourTimes[j].getSample());
            }
        }
        
        
    }
}
    

