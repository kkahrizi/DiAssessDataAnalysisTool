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
public class PlotTuple {
    public BufferedImage image;
    public String label;
    
    public PlotTuple(BufferedImage toPlot, String thisLabel){
        image = toPlot;
        label = thisLabel;
    }
    
    public BufferedImage getImage(){
        return image;
    }
    
    public String getLabel(){
        return label;
    }
   
}
