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
    public double Cq;
    public final int LOCAL_MAXIMA_WINDOW = 4;
    
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
    
    public double[] getCycles(){
        double[] cycleArray = new double[signal.length];
        for (int i = 0; i < cycleArray.length; i++){
            cycleArray[i] = ((double) i );
        }
        return cycleArray;
    }
    
    public double[] getSecondDerivativeMinutes(double secondsPerCycle, int numBack){
        double[] timeArray = new double[signal.length - 2*numBack];
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
    
    public void setCq(double val){
        Cq = val;
    }
    
    public double getCq(){
        return Cq;
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

    public double[] getSecondDerivative(int numBack){
        double[] firstDerivative = new double[signal.length - numBack];
        double[] secondDerivative = new double[firstDerivative.length - numBack];
        for(int i = 0; i < firstDerivative.length; i++){
            firstDerivative[i] = signal[i+numBack] - signal[i];
        }
        for (int i = 0; i < secondDerivative.length; i++){
            secondDerivative[i] = firstDerivative[i+numBack] - firstDerivative[i]; 
        }
        return secondDerivative;
    }
    
    
    //Returns the TTR as defined by the second derivative reaching some threshold
    //Each derivative is taken by looking back numBack elements
    public double getInflectionPointTTR(double secondsPerCycle, double threshold, int numBack){
       
        return (double)(getInflectionPointTTRIndex(secondsPerCycle,threshold,numBack)) * secondsPerCycle / 60.0;
        
    }
    
    public int getInflectionPointTTRIndex(double secondsPerCycle, double threshold, int numBack){
        double[] firstDerivative = new double[signal.length - numBack];
        double[] secondDerivative = new double[firstDerivative.length - numBack];
        for(int i = 0; i < firstDerivative.length; i++){
            firstDerivative[i] = signal[i+numBack] - signal[i];
        }
        for (int i = 0; i < secondDerivative.length; i++){
            secondDerivative[i] = firstDerivative[i+numBack] - firstDerivative[i]; 
        }
        for (int i = 0; i < secondDerivative.length; i++){
            double thisValue = secondDerivative[i];
            boolean foundTTR = true; 
            int startValue = i - LOCAL_MAXIMA_WINDOW;
            int endValue = i + LOCAL_MAXIMA_WINDOW;
            if(startValue < 0 ){
                continue;
            }
            if (endValue >= secondDerivative.length){
                endValue = secondDerivative.length - 1;
            }
            for (int j = startValue; j < endValue; j++){
                double thatValue = secondDerivative[j];
                if (thatValue > thisValue){
                    foundTTR = false;
                    break;
                }
            }
//            for (int j = i + 1; j < secondDerivative.length; j++){
//                double thatValue = secondDerivative[j];
//                if (thatValue > thisValue){
//                    foundTTR = false;
//                    break;
//                }
//            }
            if (foundTTR){
                return i;
            }
        }
        return signal.length - 1;
        
    }
    
    public double getMidpointTTR(double secondsPerCycle, int amplitudeThreshold){
        double ampThres = (double) amplitudeThreshold;
        double defaultTTR = (signal.length-1)*secondsPerCycle/60.0;
        int startCycle = 10;
        int endCycle = 16;
        double baseValue = getBaseline(startCycle,endCycle);
        double maxValue = getMax();
        double halfWay = (baseValue + maxValue)/2;
        if (halfWay - baseValue < ampThres){
            TTR = defaultTTR;
            return defaultTTR;
        }
        for (int i = 0; i < signal.length; i++){
            if (signal[i] > halfWay){
                TTR = i*secondsPerCycle/60.0;
                return TTR;
            }
        }
        TTR =  defaultTTR;
        return TTR;
    }
    
    public int getMidpointTTRIndex(int amplitudeThreshold) {
        double ampThres = (double) amplitudeThreshold;

        int startCycle = 10;
        int endCycle = 16;
        double baseValue = getBaseline(startCycle, endCycle);
        double maxValue = getMax();
        double halfWay = (baseValue + maxValue)/2;
        if (halfWay - baseValue < ampThres){
            return signal.length - 1;
            
        }
        for (int i = 0; i < signal.length; i++){
            if (signal[i] > halfWay){
              
                return i;
            }
        }
        
        return signal.length - 1;
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
