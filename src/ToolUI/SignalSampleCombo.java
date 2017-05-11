/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;
//Stores the locations of two files: 
// (1) the signal file (stores the RFU values from thermocycler

import java.io.File;

// (2) the sample info file (stores the sample names as entered into thermocycler 
/**
 *
 * @author kamin
 */
public class SignalSampleCombo {
    public File signalFile;
    public File sampleFile;
    public boolean isMeltCurve;
    
    public SignalSampleCombo(File signal, File sample){
        signalFile = signal;
        sampleFile = sample;
        isMeltCurve = false;
    }
    
    public SignalSampleCombo(File signal, File sample, boolean meltCurve){
        signalFile = signal;
        sampleFile = sample;
        isMeltCurve = meltCurve;
    }
    
    public boolean isMelt(){
        return isMeltCurve;
    }
    
    public SignalSampleCombo(File signal){
        signalFile = signal;
        sampleFile = null;
    }
    
    public SignalSampleCombo(){
        signalFile = null;
        sampleFile = null;
    }
    
    public File getSignal(){
        return signalFile;
    }
    
    public File getSample(){
        return sampleFile;
    }
    
    public void setSample(File sample){
        sampleFile = sample;
    }
    
    public void setSignal(File signal){
        signalFile = signal;
    }
}
