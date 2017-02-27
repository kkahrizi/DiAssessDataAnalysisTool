/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;

import java.awt.image.BufferedImage;

/**
 *
 * @author kamin
 */
public class Tube {
    String label;
    BufferedImage tubeImage;
    int rack;
    String time;
    int tubePosition;
    
    
    public Tube(String newLabel, BufferedImage cropImage, int aRack, String aTime, int position){
        label = newLabel;
        tubeImage = cropImage;
        rack = aRack;
        time = aTime;
        tubePosition = position;
    }
    
    public void addLabel(String thisLabel){
        label = thisLabel;
    }
    
    public int getPosition(){
        return tubePosition;
    }
    
    public void setPosition(int position){
        tubePosition = position;
    }
    
    public String getLabel(){
        return label;
    }
    
    public BufferedImage getImage(){
        return tubeImage;
    }
    
    public int getRack(){
        return rack;
    }
   
    public String getSample(){
        return label;
    }
    
    public String getTime(){
        return time;
    }
}
