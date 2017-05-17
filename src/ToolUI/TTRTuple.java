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
public class TTRTuple {
    public final String Label;
    public final Double TTR;
    public final String wellCoordinate;
    
    public TTRTuple(String lab, Double time){
        Label = lab;
        TTR = time;
        wellCoordinate = "";
    }
    
    
    public TTRTuple(String lab, Double time, String thisWell){
        Label = lab;
        TTR = time;
        wellCoordinate = thisWell;
    }
   
}
