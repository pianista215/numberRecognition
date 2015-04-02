import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Recognize a number in a image, comparing it with different patterns already proccessed
 * Based on the tutorial of : sentdex
 * https://www.youtube.com/watch?v=qKc8gi1muH4
 * 
 * Only for black/white pixels
 * @author Pianista
 *
 */
public class NumberRecognition {
	
	private static final String IMG_SUFIX = ".png";
	
	/**
	 * Return as BufferedImage the file specified
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private BufferedImage readImage(File file) throws IOException{
		BufferedImage img = ImageIO.read(file);
		return img;
	}
	
	/**
	 * Convert image into list of pixels (Assuming all the images have the same width&height)
	 * @param img
	 * @return
	 */
	private int[] obtainPixelsList(BufferedImage img){
		
		/**
		 * Like we are using only pixels totally white, or totally black, we store the entire RGB, don't worry about colours
		 */
		int[] pixelsValues = new int[img.getWidth()*img.getHeight()];
		
		for(int i=0;i<img.getWidth();i++){
			for(int j=0;j<img.getHeight();j++){
				
				int rgb = img.getRGB(i, j);
				pixelsValues[j*img.getWidth() +i] = rgb;

			}
		}
		
		return pixelsValues;
	}
	
	/**
	 * Instead of using a file, store in a Hashmap the list of the array of pixels, for each pattern
	 * Example:
	 * 0 -> [[0_pattern_1],[0_pattern_2]...]
	 * 1 -> [[1_pattern_1],[1_pattern_2]...]
	 * ....
	 */
	private HashMap<Integer, List<int[]>> patterns = new HashMap<>();
	
	/**
	 * Load the patterns into the Hashmap
	 * It proccess each image pixel by pixel storing as an Array of pixels
	 * @throws IOException 
	 */
	private void loadPatterns() throws IOException{
		for(int i=0;i<10;i++){
			List<int[]> patternsForCurrentNumber = new ArrayList<>();
			for(int j=1;j<10;j++){
				String patternName = i +"."+j+IMG_SUFIX;
				File f = new File(getClass().getResource("/patterns/"+patternName).getFile());
				BufferedImage pattern = readImage(f);

				//Store the pattern
				patternsForCurrentNumber.add(obtainPixelsList(pattern));
			}
			
			//Store the list of patterns for the current number
			patterns.put(i, patternsForCurrentNumber);
		}
	}
	
	/**
	 * Compare the pixels of the pattern of the number [patternNumber] with the pixels of the image provided
	 * @param patternNumber
	 * @param imagePixels
	 * @return
	 */
	private int numberOfCoincidences(int patternNumber, int[] imagePixels){
		int coincidences = 0;
		
		//Retrieve from the patterns preread the pixels of the patterns of number i
		List<int[]> patternImages = patterns.get(patternNumber);
		
		for(int[] patternPixels : patternImages){
			for(int i=0;i<patternPixels.length;i++){
				if(patternPixels[i] == imagePixels[i]){
					coincidences++;
				}
			}
		}
		
		System.out.println("Number of coincidences for number:"+patternNumber+" = "+coincidences);
		return coincidences;
	}
	
	/**
	 * Recognize the number of the image provided comparing with the patterns 
	 * The number with more coincidences is the winner
	 * @param f
	 * @return
	 * @throws IOException 
	 */
	private int recognize(File f) throws IOException{
		BufferedImage img = readImage(f);
		
		//Get the entire list of pixels for img
		int[] imgPixels = obtainPixelsList(img);
		
		int[] results = new int[10];
		
		//First load the patterns
		loadPatterns();
		
		//Then compare each number and store the result
		for(int i=0;i<10;i++){
			results[i] = numberOfCoincidences(i,imgPixels);
		}
		
		//Identify the winner
		int winner = -1;
		int maxCoincidences = -1;
		for(int i=0;i<10;i++){
			if(results[i]>maxCoincidences){
				winner = i;
				maxCoincidences = results[i];
			}
		}
		
		return winner;
	}

	/**
	 * Main entry
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		NumberRecognition r = new NumberRecognition();
		File test = new File("D:\\Investigacion\\numberRecognition\\test.png"); //Change it for your image
		int winner = r.recognize(test);
		System.out.println("And the winner is the number: "+winner);
	}

}
