/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;

import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author kamin
 */
public class ThermoReplicate implements Comparable {
    public String wellCoordinate;
    public String sampleName;
    public double[] signal;
    public double TTR;
    
    public ThermoReplicate(){
        wellCoordinate = null;
        sampleName = null;
        signal = new double[0];
    }
    
    public ThermoReplicate(String coordinate, String label, double[] rfuData){
        wellCoordinate = coordinate;
        sampleName = label;
        signal = rfuData;
    }
    
    public ThermoReplicate(String coordinate){
        wellCoordinate = coordinate;
        sampleName = null;
        signal = new double[0];
    }
    
    public void addDataPoint(double newDataPoint){
        double[] newSignal = new double[signal.length+1];
        if(signal.length!=0){
            newSignal = Arrays.copyOfRange(signal, 0, signal.length);
            signal = ArrayUtils.addAll(newSignal, newDataPoint);
        } else {
            newSignal[signal.length] = newDataPoint;
            signal = newSignal;
        }
    }
    
    public void label(String label){
        sampleName = label;
    }
    
    public double[] getMinutes(double secondsPerCycle){
        double[] timeArray = new double[signal.length];
        for (int i = 0; i < timeArray.length; i++){
            timeArray[i] = ((double) i * secondsPerCycle ) / 60;
        }
        return timeArray;
    }
    
    public String getLabel(){
        return sampleName;
    }
    
    public boolean hasLabel(){
        return sampleName == null;
    }
    
    public String getCoordinates(){
        return wellCoordinate;
    }
    
    public void setCoordinates(String coordinate){
        wellCoordinate = coordinate;
    }
    
    //Checks if the passed argument is the same coordinate as this object's coordinates
    //A01 an A1 should be equal
    //Assume first character is alpha 
    public boolean checkCoordinate(String coordinate){
        char thatAlpha = coordinate.charAt(0);
        char thisAlpha = wellCoordinate.charAt(0);
        if (!(thatAlpha==thisAlpha)){
            return false;
        } 
        int thatLength = coordinate.length();
        int thisLength = wellCoordinate.length();
        int thatNumber = Integer.parseInt(coordinate.substring(1,thatLength));
        int thisNumber = Integer.parseInt(wellCoordinate.substring(1,thisLength));
        return (thisNumber == thatNumber);
    }
    
    public double[] getSignal(){
        return signal;
    }
    
    public void setSignal(double[] rfuData){
        signal = rfuData;
    }
    
    public int getPeakIndex(){
        int peakIndex = 0;
        for (int i = 0; i < signal.length; i++){
            if (signal[i] > signal[peakIndex]) {
                    peakIndex = i;
            }
        }
        return peakIndex;
    }

    public double getMidpointTTR(double secondsPerCycle){
        int startCycle = 10;
        int endCycle = 16;
        double baseValue = getBaseline(startCycle,endCycle);
        double maxValue = getMax();
        double halfWay = (baseValue + maxValue)/2;
        for (int i = 0; i < signal.length; i++){
            if (signal[i] > halfWay){
                TTR = i*secondsPerCycle/60.0;
                return TTR;
            }
        }
        TTR =  (signal.length-1)*secondsPerCycle/60.0;
        return TTR;
    }
    
    public int getMidpointTTRIndex(){
        int startCycle = 10;
        int endCycle = 16;
        double baseValue = getBaseline(startCycle,endCycle);
        double maxValue = getMax();
        double halfWay = (baseValue + maxValue)/2;
        for (int i = 0; i < signal.length; i++){
            if (signal[i] > halfWay){
                TTR = i;
                return i;
            }
        }
        TTR =  signal.length-1;
        return signal.length;
    }
        
    //Returns the average value of the signal array between 
    //[startCycle, endCycle)
    public double getBaseline(int startCycle, int endCycle){
        double runningSum = 0;
        for (int i = startCycle; (i < signal.length && i < endCycle); i++){
            runningSum += signal[i];
        }
        if (endCycle > signal.length){
            return runningSum/(signal.length-startCycle);
        } else {
            return runningSum/(endCycle-startCycle);
        }
           
    }
    
    //Returns the largest value of signal array
    public double getMax() {
        double runningMax = 0;
        for (int i = 0; i < signal.length; i++) {
            if (runningMax < signal[i]) {
                runningMax = signal[i];
            }
        }
        return runningMax;
    }
    
    public int compareTo(Object other){
        ThermoReplicate otherReplicate = (ThermoReplicate) other;
        return this.getLabel().compareTo(otherReplicate.getLabel());
    }
}
