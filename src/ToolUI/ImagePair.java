/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;
import java.awt.image.BufferedImage;

/**
 *@param time is the time
 *@param image is the buffered image
 * @author kamin
 */
public class ImagePair {
    public int rackNumber;
    private String time;
    private BufferedImage image;
    private String tubeLabel;
    
    public ImagePair(int num, String when, BufferedImage what){
        rackNumber = num;
        if(when.contains(".jpg")){
            time = when.substring(0,4);
        } else {
            time = when;
        }
        image = what;
    }
    
    public String getLabel(){
        return tubeLabel;
    }
    
    public void setLabels(String toLabel){
        tubeLabel = toLabel;
    }
    
    public int getRackNumber(){
        return rackNumber;
    }
    
    public String getTime(){
        return time;
    }
    
    public int getIntTime(){
        return Integer.parseInt(time);
    }
    
    public BufferedImage getImage(){
        return image;
    }
    
    @Override
    public String toString(){
        return "Rack Number: " + rackNumber + " Time: " + time;
    }
}
