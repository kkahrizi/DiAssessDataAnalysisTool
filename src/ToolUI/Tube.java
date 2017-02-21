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
    String rack;
    String time;
    
    
    public Tube(String newLabel, BufferedImage cropImage, String aRack, String aTime){
        label = newLabel;
        tubeImage = cropImage;
        rack = aRack;
        time = aTime;
    }
    
    public String getLabel(){
        return label;
    }
    
    public BufferedImage getImage(){
        return tubeImage;
    }
    
    public String getRack(){
        return rack;
    }
   
    public String getSample(){
        return label;
    }
    
    public String getTime(){
        return time;
    }
}
