import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MotionDetection {
	public static void main(String args[]){
		if(args.length == 2){
			String img1 = args[0];
			String img2 = args[1];
			try {
				BufferedImage image1 = ImageIO.read(new File(img1));
				BufferedImage image2 = ImageIO.read(new File(img2));
				BufferedImage ImageDiff;
				
				MotionDetection Detect = new MotionDetection();
				ImageDiff = Detect.ImageSubtract(image1, image2);
				System.out.println("The resulting difference of the image is stored in MotionDetected.jpg");
				double speed = Detect.SpeedCalc(ImageDiff);
                // convert speed from mm/s to meters per second
                speed = speed / 1000;
				System.out.printf("Speed of car is %.2f metres/second", speed);
			} catch (IOException e) {
				System.err.println(e);
			    System.exit(1);
			}
		}
	}
	public BufferedImage ImageSubtract(BufferedImage img1 , BufferedImage img2){
		
		int imageheight = img1.getHeight();
		int imagewidth = img1.getWidth();
		
		WritableRaster image1 = img1.getRaster();
		WritableRaster image2 = img2.getRaster();
		
		int diffred;
		int diffblue;
		int diffgreen;
		Color treshold = new Color(30,30,30);
		BufferedImage DiffImage = new BufferedImage(imagewidth,imageheight,BufferedImage.TYPE_INT_RGB);
	
		for(int y = 0 ; y < imageheight ; y++){
			for( int x = 0 ; x < imagewidth ; x++){
				diffred = Math.abs( image1.getSample(x, y, 0) - image2.getSample(x,y,0));
				diffgreen = Math.abs(image2.getSample(x,y,1) - image1.getSample(x, y, 1));
				diffblue = Math.abs(image2.getSample(x,y,2) - image1.getSample(x, y, 2)); 
				Color diff = new Color(diffred,diffgreen,diffblue);
				DiffImage.setRGB(x, y, diff.getRGB());
				if(DiffImage.getRGB(x, y) < treshold.getRGB()){
					diff= Color.WHITE;
					DiffImage.setRGB(x, y,diff.getRGB());
				}
				else{
					diff = Color.BLACK;
					DiffImage.setRGB(x,y,diff.getRGB());
				}
			}
		}
		try {
			  ImageIO.write(DiffImage,"jpg",new File("MotionDetected.jpg"));
		} catch (IOException e) { e.printStackTrace(); }
		
		
		return DiffImage;
	}
	public double SpeedCalc(BufferedImage img){
		double speed = 0.0;
		int x1 = 0;
		int x2 = 0;
		WritableRaster newImg = img.getRaster();
		for(int y = 0; y< img.getHeight(); y++){
			for(int x = 0; x < img.getWidth(); x++){
				if( newImg.getSample(x, y, 0)== 0){
					x1 = x; // those are the first x black value
					break;
				}
				else{
					continue;
				}
			}
			if(x1 > 0){
				break;
			}
		}
		for(int y=0; y< img.getHeight(); y++){
			for(int x = 0; x<img.getWidth(); x++){
				if( newImg.getSample(x, y, 0)== 0){
					x2 = x; // those are the last x black value
					break;
				}
			}
		}
		// we only really need the x values 
		int timepassed = 7; // in seconds
		System.out.printf("Time Passed between 2 images %d seconds \n" , timepassed );		
		double mag = 0.09; // assumed magnification more details in report
		int car_length = 6000; // in millimeters
		int car_px = (int)(car_length * mag);
		System.out.printf("Final Position of Car:%d, Initial Position of Car:%d \n" , x1,x2);
		int distance_moved = Math.abs(x2 - x1) - car_px;
		speed = (double) ( distance_moved / timepassed ) / mag;
		return speed;
	}
}
