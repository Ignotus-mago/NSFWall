package net.paulhertz.fibowall;

import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;
import processing.core.PApplet;
import net.paulhertz.aifile.*;
import net.paulhertz.geom.Matrix3;
import net.paulhertz.util.*;


public class FiboTreeGrid extends PApplet {
	RandUtil rando;
	StringBuffer sb = new StringBuffer(1024);
	UniBlox bloxx = new UniBlox();
	String seedString = "";
	String outString = "";
	StringBuffer gridBuf;
	int depth;
	int inDepth1;
	int inDepth2;
	public ArrayList<BezShape> shapes;
	public ArrayList<BezShape> grid;
	public ArrayList<BezShape> panels;
	IgnoCodeLib igno;
	float tw = 1;
	float gridX = 0;
	float gridY = 0;
	public static final float GOLDEN = (float)((Math.sqrt(5) - 1)/2.0 + 1);
	public static final float INVGOLDEN = GOLDEN - 1;
	public static final float NEXTGOLDEN = GOLDEN + 1;
	float w = GOLDEN;
	// sum a them Fibonacci numbers
	int[] FIB = { 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 
							  2584, 4181, 6765, 10946, 17711, 28657, 46368, 75025, 121393, 196418, 
							  317811, 514229, 832040, 1346269 };
	
	// palette
	int[] oneColors;
	int[] zeroColors;
	ArrayList<Integer> allColors;
	int[] p142 = { 21, 34, 55,   144, 152, 233,   29, 47, 76,   178, 199, 246,   
      110, 128, 152,   22, 199, 246, 	 123, 131, 144,   36, 94, 152 };
	int[] pLight = { 199, 233, 220,   34, 144, 233,   76, 123, 199,   178, 199, 246,   
      110, 178, 152,   233, 220, 246, 	 123, 131, 144,   36, 94, 152 };


	String filePath = "/Users/paulhz/Desktop/Eclipse_output/fibotree/grid";
	String basename = "fgrid";
	int fileCount = 1;

	
	public void setup() {
		// scaled size for NSF lobby, 1 foot = 24 pixels
		size(1536, 276);
		smooth();
		// noLoop();
		igno = new IgnoCodeLib(this);
		rando = new RandUtil();
		sb = new StringBuffer(1024);
		gridBuf = new StringBuffer();
		shapes = new ArrayList<BezShape>();
		grid = new ArrayList<BezShape>();
		panels = panelOverlay();
		// depth of 17 yields 1597 bands
		// depth of 5 yields 8 bands
		// depth of 11 yields 144 bands, FIB[depth + 1] == 144;
		// 11 is ideal for the grid: it divides each of the 16 panels into 9 equal parts
		depth = 11;
		inDepth1 = 5;
		inDepth2 = 5;
		runSystem();
		// println("---- bloxx expand: "+ bloxx.expandString("0", 4, new StringBuffer(), true));
	}

	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "FiboTreeGrid" });
	}

	
	public void draw() {
		background(255);
		for (BezShape bez: shapes) {
			bez.draw();
		}
		for (BezShape bez: grid) {
			bez.draw();
		}
		for (BezShape bez: panels) {
			bez.draw();
		}
	}
	
	
	public void keyPressed() {
		if ('s' == key || 'S' == key) {
			saveAI();
		}
		if ('x' == key || 'X' == key) {
			sb.setLength(0);
			gridBuf.setLength(0);
			shapes.clear();
			grid.clear();
			runSystem();
		}
	}
	

	/**
	 * Use the palette from the NSF example fibotree142 or other palette.
	 */
	public void initWallColors(int[] numbers) {
		allColors = new ArrayList<Integer>();
		oneColors = new int[24];
		zeroColors = new int[24];
		// we create a color and its permutations, then colors assign alternately to oneColor or zeroColor
		int n = 0;
		for (int j = 0; j < 4; j++) {
			int c = Palette.composeColor(numbers[n++], numbers[n++], numbers[n++], 255);
			int[] perm = Palette.colorPermutation(c);
			rando.shuffle(perm);
			for (int i = 0; i < perm.length; i++) {
				oneColors[i + j * 6] = perm[i];
				allColors.add(perm[i]);
			}
			c = Palette.composeColor(numbers[n++], numbers[n++], numbers[n++], 255);
			perm = Palette.colorPermutation(c);
			rando.shuffle(perm);
			for (int i = 0; i < perm.length; i++) {
				zeroColors[i + j * 6] = perm[i];
				allColors.add(perm[i]);
			}
		}
		rando.shuffle(zeroColors);
		rando.shuffle(oneColors);
	}

	
	/**
	 * Generates a string of chars of a specified length. 
	 * @param padCh   char used for padding
	 * @param len     length of padding
	 * @return        a string of padCh chars of length len
	 */
	public String pad(char padCh, int len) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; i++) {
			sb.append(padCh);
		}
		return sb.toString();
	}
	
	
	/**
	 * Entry point for initializing and running the L-system and generating geometry.
	 */
	public void runSystem() {
		initWallColors(pLight);
		// prepare to attach the grid lines to the top
		gridY = height;
		initLindenmeyer_000();
		// expand string and create grid geometry
		expandString(seedString, depth);
		// decode string using bloxx and other criteria
		decodeString();
		// create the shapes for the drawing
		loadShapes();
		println(gridBuf.toString());
	}
	

	/**
	 * Expands L-system string and constructs grid geometry, recursively.
	 * @param tokens   the string to expand
	 * @param level    the current level, decremented at each pass
	 */
	public void expandString(String tokens, int level) {
		// println("level is "+ level + "\n  "+ tokens);
		buildGridGeometry(tokens, level);
		// now do the string expansion
		StringBuffer temp = new StringBuffer(2 * tokens.length());
		for (int i = 0; i < tokens.length(); i++) {
			char ch = tokens.charAt(i);
			String val = bloxx.get(ch);
			temp.append(val);
		}
		if (level > 0) {
			expandString(temp.toString(), level - 1);
		}
		else {
			sb.append(tokens);
			return;
		}
	}
	
	
	/**
	 * @return   an array of BezShapes for the panel and portal overlay
	 */
	public ArrayList <BezShape> panelOverlay() {
		int panelCount = 16;
		int panelWidth = width/panelCount;
		ArrayList <BezShape> bez = new ArrayList <BezShape>();
		// portal
		fill(192, 192, 192, 192);
		noStroke();
		bez.add(BezRectangle.makeLeftTopRightBottom(10 * panelWidth, 0, 13 * panelWidth, height));
		noFill();
		stroke(255);
		strokeWeight(1);
		bez.add(BezLine.makeCoordinates(10 * panelWidth, 0, 13 * panelWidth, height));
		bez.add(BezLine.makeCoordinates(10 * panelWidth, height, 13 * panelWidth, 0));
		// panels
		for (int i = 1; i < panelCount; i++) {
			bez.add(BezLine.makeCoordinates(i * panelWidth, 0, i * panelWidth, height));
		}
		return bez;
	}
	

	/**
	 * Builds a representation of grid determined by a Fibonacci series.
	 * @param tokens   tokens in L-system used to generate geometry
	 * @param level    current depth of recursion of in L-system
	 */
	public void buildGridGeometry(String tokens, int level) {
		// construct the string representation of the grid line by line
		// add the grid geometry as we go
		int pos = level + 1;
		int fib0 = FIB[pos];
		int fib1 = FIB[pos + 1];
		String pad0 = pad(' ', fib0 - 1);
		String pad1 = pad('_', fib1 - 1);
		gridX = 0;
		float yStep = height/(depth + 1.0f);
		float xStep = ((float) width)/(FIB[depth + 1]);
		// set the fill and stroke properties
		noFill();
		stroke(254, 144, 157, 224);
		strokeWeight(1);
		// construct the grid geometry in this loop
		for (int i = 0; i < tokens.length(); i++) {
			char ch = tokens.charAt(i);
			if ('0' == ch) {
				gridBuf.append(ch + pad0);
				// grid.add(BezRectangle.makeLeftTopWidthHeight(gridX, gridY, fib0 * xStep, height/(depth + 1)));
				grid.add(BezLine.makeCoordinates(gridX, gridY, gridX, gridY - yStep));
				gridX += fib0 * xStep;
			}
			else if ('1' == ch) {
				gridBuf.append(ch + pad1);
				// grid.add(BezRectangle.makeLeftTopWidthHeight(gridX, gridY, fib1 * xStep, height/(depth + 1)));
				grid.add(BezLine.makeCoordinates(gridX, gridY, gridX, gridY - yStep));
				gridX += fib1 * xStep;
			}
			else println("error: ch = "+ ch);
		}
		// decrement gridY for next recursive call
		gridY -= yStep;
		// add a return to the output string
		gridBuf.append(RETURN);
	}

	
	/**
	 * Create the shapes for the drawing by parsing outString.
	 */
	public void loadShapes() {
		float x = 0;
		float y = 0;	
		StringBuffer buf = new StringBuffer(outString);
		// w = width/tw;
		w = ((float) width)/(FIB[depth + 1]);   // same as stepX in expandString
		float bigW = w * GOLDEN;
		float littleW = w * INVGOLDEN;
		for (int i = 0; i < buf.length(); i++) {
			char ch = buf.charAt(i);
			if ('0' == ch) {
				noStroke();
				fill(246, 199, 178);
				//shapes.add(BezRectangle.makeLeftTopRightBottom(x, 0, x + w, height));
				shapes.addAll( makeInnerShapes("0", inDepth1, x, 0, w, height, zeroColors[2], oneColors[2]) );
				x += w;
			}
			else if ('1' == ch) {
				noStroke();
				fill(178, 199, 246);
				//shapes.add(BezRectangle.makeLeftTopRightBottom(x, 0, x + w, height));
				shapes.addAll( makeInnerShapes("1", inDepth1 - 1, x, 0, w, height, zeroColors[2], oneColors[2]) );
				x += w;				
			}
			else if ('2' == ch) {
				noStroke();
				fill(144, 152, 233, 255);
				//shapes.add(BezRectangle.makeLeftTopRightBottom(x, 0, x + 2*w, height));
				shapes.addAll( makeInnerShapes("11", inDepth1 - 1, x, 0, 2 * w, height, zeroColors[14], oneColors[14]) );
				x += 2*w;				
			}
			else { 
				println("error: ch = "+ ch); 
			}
		}
		println("---- x = "+ x);		
	}
	
	
	/**
	 * Expands geometry within a fixed grid space
	 * @param tokens     string to expand (0, 1 and 2 will be parsed)
	 * @param howDeep    depth to expand string
	 * @param gridW      fixed width of the grid space to fill
	 * @param gridH      height of the grid space
	 * @param left       left coordinate of grid space
	 * @param top        top coordinate of grid space
	 * @param color0     color to use for "0" tagged geometry
	 * @param color1     color to use for "1" tagged geometry
	 * @return           a Collection of BezShapes
	 */
	public Collection <? extends BezShape> makeInnerShapes(String tokens, int howDeep, float left, float top, 
			                                                   float gridW, float gridH, int color0, int color1) {
		StringBuffer tbuf =  bloxx.expandString(tokens, howDeep, new StringBuffer(), false);
		int ct0 = 0; 
		int ct1 = 0;
		for (int i = 0; i < tbuf.length(); i++) {
			char ch = tbuf.charAt(i);
			if ('0' == ch) ct0++;
			else if ('1' == ch) ct1++;
			else println("---- Parse error in expandShape var tokens: "+ ch);
		}
		// println("---- buf = "+ tbuf.toString() +", ct0 = "+ ct0 +", ct1 = "+ ct1);
		float w0 = (gridW) / (ct0 + ct1 * GOLDEN);
		float w1 = w0 * GOLDEN;
		ArrayList <BezShape> bz = new ArrayList <BezShape>();
		for (int i = 0; i < tbuf.length(); i++) {
			char ch = tbuf.charAt(i);
			if ('0' == ch) {
				fill(color0);
				bz.add(BezRectangle.makeLeftTopWidthHeight(left, top, w0, gridH));
				bz.addAll( ( makeInmostShapes("0", inDepth2, left, 0, w0, height, zeroColors[7], oneColors[7]) ) );
				left += w0;
			}
			else if ('1' == ch) {
				fill(color1);
				bz.add(BezRectangle.makeLeftTopWidthHeight(left, top, w1, gridH));
				bz.addAll( ( makeInmostShapes("1", inDepth2 - 1, left, 0, w1, height, zeroColors[8], oneColors[8]) ) );
				left += w1;
			}
		}
		return bz;
	}
	
	/**
	 * Expands geometry within a fixed grid space
	 * @param tokens     string to expand (0, 1 and 2 will be parsed)
	 * @param howDeep    depth to expand string
	 * @param gridW      fixed width of the grid space to fill
	 * @param gridH      height of the grid space
	 * @param left       left coordinate of grid space
	 * @param top        top coordinate of grid space
	 * @param color0     color to use for "0" tagged geometry
	 * @param color1     color to use for "1" tagged geometry
	 * @return           a Collection of BezShapes
	 */
	public Collection <? extends BezShape> makeInmostShapes(String tokens, int howDeep, float left, float top, 
                                                          float gridW, float gridH, int color0, int color1) {
		StringBuffer tbuf =  bloxx.expandString(tokens, howDeep, new StringBuffer(), false);
		int ct0 = 0; 
		int ct1 = 0;
		for (int i = 0; i < tbuf.length(); i++) {
			char ch = tbuf.charAt(i);
			if ('0' == ch) ct0++;
			else if ('1' == ch) ct1++;
			else println("---- Parse error in expandShape var tokens: "+ ch);
		}
		// println("---- buf = "+ tbuf.toString() +", ct0 = "+ ct0 +", ct1 = "+ ct1);
		float w0 = (gridW) / (ct0 + ct1 * GOLDEN);
		float w1 = w0 * GOLDEN;
		ArrayList <BezShape> bz = new ArrayList <BezShape>();
		for (int i = 0; i < tbuf.length(); i++) {
			char ch = tbuf.charAt(i);
			if ('0' == ch) {
				fill(color0);
				bz.add(BezRectangle.makeLeftTopWidthHeight(left, top, w0, gridH));
				left += w0;
			}
			else if ('1' == ch) {
				fill(color1);
				bz.add(BezRectangle.makeLeftTopWidthHeight(left, top, w1, gridH));
				left += w1;
			}
		}
		return bz;
	}
	
	

	/**
	 * Decode L-system string using bloxx.decode and other criteria, including regex replacement.
	 */
	public void decodeString() {
		StringBuffer temp = new StringBuffer(sb.length());
		for (int i = 0; i < sb.length(); i++) {
			char ch = sb.charAt(i);
			String val = bloxx.decode(ch);
			if (null != val) {
				temp.append(val);
			}
			else {
				temp.append(ch);
			}
		}
		println("---- first pass of loadTables ----");
		println("-- first pass string length = "+ temp.length());
		println(temp.toString());
		// pattern replacement 
		/*  */
		Pattern p = Pattern.compile("11");
		Matcher m = p.matcher(temp.toString());
		// replace all "11" with "2"
		outString = m.replaceAll("2");		 
		// outString = temp.toString();
		println("---- second pass of loadTables ----");
		println("-- string length = "+ outString.length());
		println(outString);
		temp.setLength(0);
		temp.append(outString);
		// count how many of each number we have 
		int zeroCount = 0, oneCount = 0, twoCount = 0;
		for (int i = 0; i < temp.length(); i++) {
			char ch = temp.charAt(i);
			if ('0' == ch) zeroCount++;
			else if ('1' == ch) oneCount++;
			else if ('2' == ch) twoCount++;
			else println("error: ch = "+ ch);
		}
		float total = zeroCount * INVGOLDEN + oneCount + twoCount * GOLDEN;
		println("---- third pass of loadTables ----");
		println("zeroCount = "+ zeroCount +", oneCount = "+ oneCount +", twoCount = "+ twoCount);
		println("-- total = "+ total);
		tw = total;
	}
	
	
	/**
	 * Initialize an L-system in bloxx. 
	 */
	public void initLindenmeyer_000() {
		bloxx.put('0', "1");
		bloxx.put('1', "01");		
		bloxx.encode('0', "0");
		bloxx.encode('1', "1");
		seedString = "0";
	}
	
	
	/**
	 * @return   a string in the format yymmdd_hhmmss, a complete timestamp down to the second
	 */
	public String getTimestamp() {
		return nf(year(),2).substring(2, 4) + nf(month(),2) + nf(day(),2) +"_"+ nf(hour(),2) + nf(minute(),2) + nf(second(),2);
	}
	
	
	/**
	 * Save to an Adobe Illustrator file
	 */
	public void saveAI() {
		String filename = filePath +"/"+ basename +"_"+ getTimestamp() +"_"+ fileCount++ + ".ai";
		saveAI(filename, shapes, allColors);
	}

	
	/**
	 * Saves shapes to an Adobe Illustrator file, called by saveAI().
	 * @param aiFilename
	 * @param comps
	 * @param paletteColors
	 */
	public void saveAI(String aiFilename, ArrayList<BezShape> comps, ArrayList<Integer> paletteColors) {
		println("saving Adobe Illustrator file " + aiFilename + "...");
		PrintWriter output = createWriter(aiFilename);
		DocumentComponent doc = new DocumentComponent(this, "IgnoDoc");
		Palette pal = doc.getPalette();
		pal.addBlackWhiteGray();
		pal.addColors(paletteColors);
		doc.setCreator("Ignotus");
		doc.setOrg("IgnoStudio");
		doc.setWidth(width);
		doc.setHeight(height);
		/* doc.setVerbose(true); */
		AIFileWriter.setUseTransparency(true);
		int layerIndex = 1;
		doc.add(makeLayerWithComps("Shapes", layerIndex++, true, false, comps));
		doc.add(makeLayerWithComps("Panels", layerIndex++, true, false, panels));
		doc.add(makeLayerWithComps("Grid", layerIndex++, true, false, grid));
		doc.write(output);
	}
	
	
	public LayerComponent makeLayerWithComps(String layerName, int layerIndex, boolean isVisible, boolean isLocked, 
      ArrayList <? extends DisplayComponent> comps) {
		LayerComponent layer = new LayerComponent(this, layerName, layerIndex);
		layer.setVisible(isVisible);
		layer.setLocked(isLocked);
		layer.add(comps);
		return layer;
	}

	
}
