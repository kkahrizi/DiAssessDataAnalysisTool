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
public class LabeledImage {
    LabeledSample[] mySamples;
    String[] sampleStrings;
    
    public LabeledImage(){
        mySamples = new LabeledSample[0];
        sampleStrings = new String[0];
    }
    
    public LabeledImage(LabeledSample[] theseRacks){
        mySamples = theseRacks;
        for (int i = 0; i < theseRacks.length; i++){
            addSampleString(theseRacks[i].getSampleString());
        }
    }
    
  
 
    
    public String[] getSampleStrings(){
        return sampleStrings;
        
    }
    
    public LabeledSample[] getSamples(){
        return mySamples;
    }
        
    public void addSample(LabeledSample thisSample){
        if(mySamples.length < 1){
            mySamples = new LabeledSample[1];
            mySamples[0] = thisSample;
            
            addSampleString(thisSample.getSampleString());
            return;
        }
        LabeledSample[] newSamples = new LabeledSample[mySamples.length+1];
        for (int i = 0; i < mySamples.length; i++){
            newSamples[i] = mySamples[i];
        }
        newSamples[mySamples.length] = thisSample;
        mySamples = newSamples;
        addSampleString(thisSample.getSampleString());
    }
    
    private void addSampleString(String newSample){
        if (sampleStrings == null || sampleStrings.length < 1){
            sampleStrings = new String[1];
            sampleStrings[0] = newSample;
            return;
        }
        
        String[] newSampleStrings = new String[sampleStrings.length + 1];
        for (int i = 0; i < sampleStrings.length; i++){
            if(newSample.equalsIgnoreCase(sampleStrings[i])){
                return;
            }
            newSampleStrings[i] = sampleStrings[i];
        }
        newSampleStrings[sampleStrings.length] = newSample;
        sampleStrings = newSampleStrings;
        
    }
}
