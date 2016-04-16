package net.paulhertz.fibowall;

import processing.core.PApplet;
import java.io.*;
import java.util.*;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import controlP5.*;
import net.paulhertz.aifile.*;
import net.paulhertz.geom.Matrix3;
import net.paulhertz.util.*;

/**
 * @author paulhz
 * Class to design wall for NSF building
 * We are sticking to Processing 2.2.5 because ControlP5's external window class (ControlWindow)
 * depends on PApplet acting as a Java AWT Component. Processing 3 eliminates all Java AWT dependencies.
 * There are apparently some workarounds--but they can wait.
 */
public class Fibo_001 extends PApplet {
	static enum NodeType {zero, one};
	public ArrayList<TaggedRectangle> blockList; 
	int[] oneColors;
	int[] zeroColors;
	ArrayList<Integer> allColors;
	RandUtil rando;
	ArrayList<Integer> chx;
	public static final float GOLDEN = (float)((Math.sqrt(5) - 1)/2.0 + 1);
	public static final float INVGOLDEN = GOLDEN - 1;
	public static final float ROOTTWO = (float)(Math.sqrt(2.0));
	public static final float INVROOTTWO = (float)(1/Math.sqrt(2.0));
	boolean animate = false;
	// control interface
	private ControlP5 controlP5;
	public ControlFrame cf;
	
	/** reference to the graphics and i/o library */
	IgnoCodeLib igno;
	/** gap between rectangles, set to zero in buildRectangles */
	float gap = 5;
	/** depth of recursion, 21 for the original fibotree142 art */
	int levels = 11;
	/** threshold for splitting vertically or horizontally. A value of 1.0 makes splits evenly. */
	float verticality = 2.0f;
	/** level where we start to do vertical divisions */
	int startVertical = 9;
	/** variable for panel width */
	int panelWidth;
	/** variable for number of panels */
	int panelCount = 16;

	/** number formats */
	DecimalFormat fourPlaces;
	DecimalFormat twoPlaces;
	DecimalFormat noPlaces;
	DecimalFormat fourFrontPlaces;

	String filePath = "/Users/paulhz/Desktop/Eclipse_output/fibotree";
	String basename = "fibo_";
	int fileCount = 0;
	

	/**
	 * Do standard Processing setup calls.
	 * Set up class instances.
	 */
	public void setup() {
		// scaled size for NSF lobby, 1 foot = 24 pixels
		// closest F-number wd be 1597
		size(1536, 276);
		panelWidth = width/panelCount;
		// square format for test 
		//size(864, 864);
		smooth();
		initDecimalFormat();
		frameRate(15);
		rando = new RandUtil();
		igno = new IgnoCodeLib(this);
		controlP5 = new ControlP5(this);
		// create the external control window
		// we will add control widgets in the ControlFrame class below		
		cf = addControlFrame("control", 240, 240);
		basename += getTimestamp();
		printHelp();
		// generate the subdivided rectangle geometry
		revYourEngines(true);
	}
	
	
	/**
	 * Entry point used in Eclipse
	 * @param args
	 */
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "Fibo_001" });
	}
	
	/**
	 * Initialize a separate window for controls
	 * @param name   title of the window
	 * @param w      width
	 * @param h      height
	 * @return       initialized ControlFrame
	 */
	public ControlFrame addControlFrame(String name, int w, int h) {
		Frame f = new Frame(name);
		ControlFrame p = new ControlFrame(this, w, h);
		f.add(p);
		p.init();
		f.setTitle(name);
		f.setSize(p.w, p.h);
		f.setLocation(100, 100);
		f.setResizable(false);
		f.setVisible(true);
		return p;
	}

	/**
	 * @return   a string in the format yymmdd_hhmmss
	 */
	public String getTimestamp() {
		return nf(year(),2).substring(2, 4) +  nf(month(),2) + nf(day(),2) +"_"+ nf(hour(),2) + nf(minute(),2) + nf(second(),2);
	}
	
	/**
	 * initializes the zero place and two place decimal number formatters
	 */
	public void initDecimalFormat() {
		// DecimalFormat sets formatting conventions from the local system, unless we tell it otherwise.
		// make sure we use "." for decimal separator, as in US, not a comma, as in many other countries 
		Locale loc = Locale.US;
		DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(loc);
		dfSymbols.setDecimalSeparator('.');
		fourPlaces = new DecimalFormat("0.0000", dfSymbols);
		twoPlaces = new DecimalFormat("0.00", dfSymbols);
		noPlaces = new DecimalFormat("00", dfSymbols);
		fourFrontPlaces = new DecimalFormat("0000", dfSymbols);
	}
	


	public void printHelp() {
		println("'s' save");
		println("'a' animate");
		println("'r' build with new colors");
		println("'d' build with same colors");
		println("'m' show / hide checkboxes");
		println("'x' shuffle colors");
		println("'h' print help");
	}


	public void draw() {
		background(255);
		smooth();
		stroke(0);
		noFill();
		if (animate) {
			// generate a new instance of subdivided rectangle geometry
			revYourEngines(true);
		}
		int lev = 0;
		for (lev = 0; lev < chx.size(); lev++) {
			if (chx.get(lev) == 0) break;
		}
		for (TaggedRectangle tr : blockList) {
			if (chx.get(tr.level) == 0) {
				tr.block.draw();
			}
		}
	}

	public void keyPressed() {
		println("---- main keyPressed = "+ key);
		parseKey(key);
	}
	
	public void parseKey(char key) {
		if (key == 's' || key == 'S') {
			String fileName = filePath +"/"+ basename +"_"+ fileCount++ +".ai";
			ArrayList<BezShape> bez = new ArrayList<BezShape>();
			for (TaggedRectangle tr : blockList) {
				if (chx.get(tr.level) == 0) {
					bez.add(tr.block);
				}
			}
			saveAI(fileName, allColors);
			println("saved" + fileName);
		}
		else if (key == 'a' || key == 'A') {
			animate = !animate;
		}
		else if (key == 'd' || key == 'D') {
			revYourEngines(false);
		}
		else if (key == 'r' || key == 'R') {
			revYourEngines(true);
		}
		else if (key == 'm') {
			if (cf.checkbox.isVisible()) {
				cf.checkbox.hide();
			}
			else {
				cf.checkbox.show();
			}
		}
		else if (key == 'x' || key == 'X'){
			rando.shuffle(zeroColors);
			rando.shuffle(oneColors);
			// println("shuffled colors");
			for (TaggedRectangle tr : blockList) {
				if (tr.tag == NodeType.zero) {
					tr.block.setFillColor(zeroColors[tr.level]);
				}
				else {
					tr.block.setFillColor(oneColors[tr.level]);
				}
			}
		}
		else if (key == 'h' || key == 'H') {
			printHelp();
		}
		else {
			;
		}		
	}
	
	public void revYourEngines(boolean useNewColors) {
		noLoop();
		// if (useNewColors) initColors();
		if (useNewColors) initWallColors();
		blockList = new ArrayList<TaggedRectangle>();
		// the long rectangle, 10 panel widths
		TaggedRectangle tr = new TaggedRectangle(this, 0, 0, width - 6 * panelWidth, height, NodeType.zero, levels);
		blockList.add(tr);
		buildRectangles(tr);
		// the short rectangle, 3 panel widths
		TaggedRectangle tr2 = new TaggedRectangle(this, 13 * panelWidth, 0, 3 * panelWidth, height, NodeType.zero, levels);
		blockList.add(tr2);
		buildRectangles(tr2);
		loop();
		/*
		// another way to do things, with multiple starting blocks
		int dim1 = 233;
		int dim2 = 377;
		TaggedRectangle tr = new TaggedRectangle(this, 0, 0, width, dim1, NodeType.zero, levels);
		TaggedRectangle tr2 = new TaggedRectangle(this, 0, dim1, width, dim2-dim1, NodeType.one, levels-2);
		TaggedRectangle tr3 = new TaggedRectangle(this, 0, dim2, width, dim1, NodeType.zero, levels-4);
		//TaggedRectangle tr4 = new TaggedRectangle(this, 0, dim1, dim1, dim2, NodeType.zero, levels);
		blockList.add(tr);
		buildRectangles(tr);
		buildRectangles(tr2);
		buildRectangles(tr3);
		//buildRectangles(tr4);
		 */
	}

	
	public void initColors() {
		allColors = new ArrayList<Integer>();
		oneColors = new int[24];
		zeroColors = new int[24];
//		int[] fib = {13, 21, 34, 55, 89, 144, 233};
//		int[] lucas = {11, 18, 29, 47, 76, 123, 199};
//		int[] dubfib = {26, 42, 68, 110, 178, 144, 233};
		int[] dublucas = {22, 36, 58, 94, 152, 246, 199};
		// another mix of numbers gives different colors
//		int[] fib = {21, 34, 55, 76, 123, 199};
//		int[] lucas = {18, 29, 47, 89, 144, 233};
//		int[] dubfib = {42, 68, 110, 152, 246, 199};
//		int[] dublucas = {36, 58, 94, 178, 144, 233};
		int[] fib = {21, 34, 55, 76, 29, 47};
		int[] lucas = {144, 178, 233, 246, 199, 152};
		int[] dubfib = {123, 144, 128, 110, 131, 152};
//		int[] dubfib = {26, 42, 68, 110, 178, 144, 233};
//		int[] dublucas = {5, 18, 8, 13, 11, 21};
//		int[] dublucas = {34, 55, 47, 233, 246, 220};
//		int[] dubfib = {123, 144, 128, 110, 131, 152};
		// shuffle the colors
		rando.shuffle(fib);
		rando.shuffle(lucas);
		rando.shuffle(dubfib);
		rando.shuffle(dublucas);
		int c = Palette.composeColor(fib[0], fib[1], fib[2], 255);
		int[] perm = Palette.colorPermutation(c);
		rando.shuffle(perm);
		for (int i = 0; i < perm.length; i++) {
			oneColors[i] = perm[i];
			allColors.add(perm[i]);
		}
		c = Palette.composeColor(lucas[0], lucas[1], lucas[2], 255);
		perm = Palette.colorPermutation(c);
		rando.shuffle(perm);
		for (int i = 0; i < perm.length; i++) {
			zeroColors[i] = perm[i];
			allColors.add(perm[i]);
		}
		c = Palette.composeColor(fib[3], fib[4], fib[5], 255);
		perm = Palette.colorPermutation(c);
		rando.shuffle(perm);
		for (int i = 0; i < perm.length; i++) {
			oneColors[i + 6] = perm[i];
			allColors.add(perm[i]);
		}
		c = Palette.composeColor(lucas[3], lucas[4], lucas[5], 255);
		perm = Palette.colorPermutation(c);
		rando.shuffle(perm);
		for (int i = 0; i < perm.length; i++) {
			zeroColors[i + 6] = perm[i];
			allColors.add(perm[i]);
		}
		// colors from double arrays
		c = Palette.composeColor(dubfib[0], dubfib[1], dubfib[2], 255);
		perm = Palette.colorPermutation(c);
		rando.shuffle(perm);
		for (int i = 0; i < perm.length; i++) {
			oneColors[i + 12] = perm[i];
			allColors.add(perm[i]);
		}
		c = Palette.composeColor(dublucas[0], dublucas[1], dublucas[2], 255);
		perm = Palette.colorPermutation(c);
		rando.shuffle(perm);
		for (int i = 0; i < perm.length; i++) {
			zeroColors[i + 12] = perm[i];
			allColors.add(perm[i]);
		}
		c = Palette.composeColor(dubfib[3], dubfib[4], dubfib[5], 255);
		perm = Palette.colorPermutation(c);
		rando.shuffle(perm);
		for (int i = 0; i < perm.length; i++) {
			oneColors[i + 18] = perm[i];
			allColors.add(perm[i]);
		}
		c = Palette.composeColor(dublucas[3], dublucas[4], dublucas[5], 255);
		perm = Palette.colorPermutation(c);
		rando.shuffle(perm);
		for (int i = 0; i < perm.length; i++) {
			zeroColors[i + 18] = perm[i];
			allColors.add(perm[i]);
		}
		rando.shuffle(zeroColors);
		rando.shuffle(oneColors);
	}
	
	
	int[] p142 = { 21, 34, 55,   144, 152, 233,   29, 47, 76,   178, 199, 246,   
			           110, 128, 152,   22, 199, 246, 	 123, 131, 144,   36, 94, 152 };
	/**
	 * Use the palette from the NSF example fibotree142
	 */
	public void initWallColors() {
		allColors = new ArrayList<Integer>();
		oneColors = new int[24];
		zeroColors = new int[24];
		int[] numbers = p142;
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
		/*
		// hard code colors from NSF example fibotree142
		// we create a color and its permutations, then colors assign alternately to oneColor or zeroColor
		int c = Palette.composeColor(21, 34, 55, 255);
		int[] perm = Palette.colorPermutation(c);
		rando.shuffle(perm);
		for (int i = 0; i < perm.length; i++) {
			oneColors[i] = perm[i];
			allColors.add(perm[i]);
		}
		c = Palette.composeColor(144, 152, 233, 255);
		perm = Palette.colorPermutation(c);
		rando.shuffle(perm);
		for (int i = 0; i < perm.length; i++) {
			zeroColors[i] = perm[i];
			allColors.add(perm[i]);
		}
		// next set
		c = Palette.composeColor(29, 47, 76, 255);
		perm = Palette.colorPermutation(c);
		rando.shuffle(perm);
		for (int i = 0; i < perm.length; i++) {
			oneColors[i + 6] = perm[i];
			allColors.add(perm[i]);
		}
		c = Palette.composeColor(178, 199, 246, 255);
		perm = Palette.colorPermutation(c);
		rando.shuffle(perm);
		for (int i = 0; i < perm.length; i++) {
			zeroColors[i + 6] = perm[i];
			allColors.add(perm[i]);
		}
		c = Palette.composeColor(110, 128, 152, 255);
		perm = Palette.colorPermutation(c);
		rando.shuffle(perm);
		for (int i = 0; i < perm.length; i++) {
			oneColors[i + 12] = perm[i];
			allColors.add(perm[i]);
		}
		c = Palette.composeColor(22, 199, 246, 255);
		perm = Palette.colorPermutation(c);
		rando.shuffle(perm);
		for (int i = 0; i < perm.length; i++) {
			zeroColors[i + 12] = perm[i];
			allColors.add(perm[i]);
		}
		c = Palette.composeColor(123, 131, 144, 255);
		perm = Palette.colorPermutation(c);
		rando.shuffle(perm);
		for (int i = 0; i < perm.length; i++) {
			oneColors[i + 18] = perm[i];
			allColors.add(perm[i]);
		}
		c = Palette.composeColor(36, 94, 152, 255);
		perm = Palette.colorPermutation(c);
		rando.shuffle(perm);
		for (int i = 0; i < perm.length; i++) {
			zeroColors[i + 18] = perm[i];
			allColors.add(perm[i]);
		}
		*/
		rando.shuffle(zeroColors);
		rando.shuffle(oneColors);
	}


	public class TaggedRectangle {
		NodeType tag;
		int color;
		int level;
		boolean visible;
		BezRectangle block;

		public TaggedRectangle(PApplet parent, float left, float top, float width, float height, NodeType tag, int level) {
			this.block = BezRectangle.makeLeftTopWidthHeight(parent, left, top, width, height);
			this.tag = tag;
			this.level = level;
			this.visible = true;
		}

		public TaggedRectangle(PApplet parent, BezRectangle r, NodeType tag, int level) {
			this.block = r;
			this.tag = tag;
			this.level = level;
			this.visible = true;
		}

	}


	public void buildRectangles(TaggedRectangle seed) {
		// println("level " + seed.level);
		/*
		if (seed.block.getWidth() < 1 || seed.block.getHeight() < 1) {
			println("reached size limit at " + "level " + seed.level);
			return;
		}
		*/
		float gg = gap;
		if (seed.level < 1) return;
		int c, z;
		/*
		c = oneColors[seed.level % oneColors.length];
		z = zeroColors[seed.level % zeroColors.length];
		 */
		/**/
		//int radix = oneColors.length < 4 ? oneColors.length : 5;
		int radix = 5;
		c = zeroColors[(seed.level) % radix];
		z = oneColors[(seed.level) % radix];
//		if (seed.level % radix == 0) {
//			// swap colors
//			int temp = c;
//			c = z;
//			z = temp;
//		}
		/**/
		if (seed.tag == NodeType.zero) {
			if (seed.level < 8 && rando.randomInRange(0, 21) > 18 ) {
				// println("stochastic return at level " + seed.level);
				return;
			}
			gg = 0;
			BezRectangle insetR = seed.block.inset(gg, gg);
			insetR.setFillColor(c);
			insetR.setNoStroke();
			TaggedRectangle tr = new TaggedRectangle(this, insetR, NodeType.one, seed.level - 1);
			blockList.add(tr);
			// println("inset at level " + seed.level);
			buildRectangles(tr);
		}
		else if (seed.tag == NodeType.one) {
			radix = 5;
			c = zeroColors[(seed.level) % radix];
			z = oneColors[(seed.level) % radix];
			if (seed.level < 8 && seed.tag == NodeType.one && rando.randomInRange(0, 21) > 13 + 7 * (seed.level)/(levels)) {
				// println("stochastic return at level - " + seed.level);
				return;
			}
			// vary gg to contrast zero and one branches
			gg = 0;
			// how far over to shift the split, a number in the range 0..1
			float shift = INVGOLDEN;
			float[] notches = new float[13];
			int i = 0;
			for (; i < 8; i++) notches[i] = INVGOLDEN;
			for (; i < 13; i++) notches[i] = INVGOLDEN * INVGOLDEN;
			shift = rando.randomElement(notches);
			// slop is an extra gap factor, varies with depth
			float slop; // = (seed.level > 2 * levels/3) ? 0 : (seed.level > levels/3) ? 0 : 0;
			slop = 0;
			float w = seed.block.getWidth();
			float h = seed.block.getHeight();
			boolean splitVertical = true;
			if (w == h) {
				if (random(2.0f) > 1) {
					splitVertical = false;
				}
			}
//			else if (w < h * ((seed.level)/(levels) * 0.67 + 0.01)) {
			else if (w < h / verticality) {
				splitVertical = false;
			}
			if (splitVertical) {
				float x1 = seed.block.getLeft() + gg;
				// to clip or not to clip
				// float w1 = (float) Math.floor( (w - 3 * gg) * shift ) + slop * gg;
				// float w2 = (float) Math.ceil( (w - 3 * gg) * (1 - shift) ) - slop * gg;
				float w1 = (float) ( (w - 3 * gg) * shift ) + slop * gg;
				float w2 = (float) ( (w - 3 * gg) * (1 - shift) ) - slop * gg;
//				w1 = Math.round(w1) > 1 ? Math.round(w1) : 1;
//				w2 = Math.round(w2) > 1 ? Math.round(w2) : 1;
				if ( random(2.0f) > 1) {
					float temp = w1;
					w1 = w2;
					w2 = temp;
				}
				float x2 = x1 + w1 + gg;
				float y1 = seed.block.getTop() + gg;
				BezRectangle r1 = BezRectangle.makeLeftTopWidthHeight(this, x1, y1, w1, h - 2 * gg);
				BezRectangle r2 = BezRectangle.makeLeftTopWidthHeight(this, x2, y1, w2, h - 2 * gg);
				//println(r1.bezType +" "+ (r1.bezType() == BezShape.BezType.BEZ_RECTANGLE));
				TaggedRectangle tr1;
				TaggedRectangle tr2;
//				if (random(2.0f) > 1) {
				if (true) {
					r1.setFillColor(c);
					r2.setFillColor(z);
					r1.setNoStroke();
					r2.setNoStroke();
					tr1 = new TaggedRectangle(this, r1, NodeType.one, seed.level - 1);
					tr2 = new TaggedRectangle(this, r2, NodeType.zero, seed.level - 1);
				}
//				else {
//					r1.setFillColor(z);
//					r2.setFillColor(c);
//					r1.setNoStroke();
//					r2.setNoStroke();
//					tr1 = new TaggedRectangle(this, r1, NodeType.zero, seed.level - 1);
//					tr2 = new TaggedRectangle(this, r2, NodeType.one, seed.level - 1);
//				}
				blockList.add(tr1);
				blockList.add(tr2);
				//println("split at level " + seed.level);
				buildRectangles(tr1);
				buildRectangles(tr2);
			}
			else {
				// horizontal split
				float y1 = seed.block.getTop() + gg;
				// To clip or not to clip?
				// float h1 = (float) Math.floor( (h - 3 * gg) * shift )  + slop * gg;
				// float h2 = (float) Math.ceil( (h - 3 * gg) * (1 - shift) ) - slop * gg;
				float h1 = (float) ( (h - 3 * gg) * shift )  + slop * gg;
				float h2 = (float) ( (h - 3 * gg) * (1 - shift) ) - slop * gg;
//				h1 = Math.round(h1) > 1 ? Math.round(h1) : 1;
//				h2 = Math.round(h2) > 1 ? Math.round(h2) : 1;
				if ( random(2.0f) > 1) {
					float temp = h1;
					h1 = h2;
					h2 = temp;
				}
				float y2 = y1 + h1 + gg;
				float x1 = seed.block.getLeft() + gg;
				BezRectangle r1 = BezRectangle.makeLeftTopWidthHeight(this, x1, y1, w - 2 * gg, h1);
				BezRectangle r2 = BezRectangle.makeLeftTopWidthHeight(this, x1, y2, w - 2 * gg, h2);
				TaggedRectangle tr1;
				TaggedRectangle tr2;
//				if (random(2.0f) > 1) {
				if (true) {
					r1.setFillColor(c);
					r2.setFillColor(z);
					r1.setNoStroke();
					r2.setNoStroke();
					tr1 = new TaggedRectangle(this, r1, NodeType.one, seed.level - 1);
					tr2 = new TaggedRectangle(this, r2, NodeType.zero, seed.level - 1);
				}
//				else {
//					r1.setFillColor(z);
//					r2.setFillColor(c);
//					r1.setNoStroke();
//					r2.setNoStroke();
//					tr1 = new TaggedRectangle(this, r1, NodeType.zero, seed.level - 1);
//					tr2 = new TaggedRectangle(this, r2, NodeType.one, seed.level - 1);
//				}
				blockList.add(tr1);
				blockList.add(tr2);
				//println("split at level " + seed.level);
				buildRectangles(tr1);
				buildRectangles(tr2);
			}
		}
		else {
			println("error, missing tag value!");
		}
	}

	/**
	 * saves shapes to an Adobe Illustrator file
	 * currently not used
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
		comps.add(0, bgRect());
		println("adding components...");
		for (BezShape b : comps) {
			doc.add(b);
		}
		doc.write(output);
	}
	
	/**
	 * saves shapes to an Adobe Illustrator file
	 */
	public void saveAI(String aiFilename, ArrayList<Integer> paletteColors) {
		// println("saving Adobe Illustrator file " + aiFilename + "...");
		PrintWriter output = createWriter(aiFilename);
		DocumentComponent doc = new DocumentComponent(this, "IgnoDoc");
		doc.setVerbose(true);
		Palette pal = doc.getPalette();
		pal.addBlackWhiteGray();
		pal.addColors(paletteColors);
		doc.setCreator("Ignotus");
		doc.setOrg("IgnoStudio");
		doc.setWidth(width);
		doc.setHeight(height);
		int layer = -1;
		for (int i = levels; i >= 0; i--) {
			layer = i + 1;
			LayerComponent comp = new LayerComponent(this, "Layer " + (layer), layer);
			if (chx.get(i) == 1) {
				comp.hide();
			}
			doc.add(comp);
			// PApplet.println("set visible to " + comp.isVisible());
			GroupComponent gZero = new GroupComponent(this);
			GroupComponent gOne = new GroupComponent(this);
			for (TaggedRectangle tr : blockList) {
				if (tr.level == i) {
					if (chx.get(tr.level) == 0) {
						// comp.add(tr.block);
						tr.block.show();
					} 
					else {
						tr.block.hide();
					}
					if (tr.tag == NodeType.one) gOne.add(tr.block);
					if (tr.tag == NodeType.zero) gZero.add(tr.block);
				}
			}
			comp.add(gOne);
			comp.add(gZero);
		}
		// guides
		layer = levels + 2;
		LayerComponent comp = new LayerComponent(this, "Guidelines", layer);
		comp.hide();
		doc.add(comp);
		for (int i = 1; i < panelCount; i++) {
			BezLine bzline = BezLine.makeCoordinates(i * panelWidth, 0, i * panelWidth, height);
			comp.add(bzline);
		}			
		doc.write(output);
	}

	public BezShape bgRect() {
		int f = Palette.composeColor(233, 220, 254, 255);
		BezShape br = BezRectangle.makeLeftTopRightBottom(this, 0, 0, width, height);
		br.setFillColor(f);
		return br;
	}

	class Node extends java.lang.Object {
		public Node left;
		public Node right;
		public Node parent;
		public NodeType type;
		public DisplayComponent comp;

		public Node(DisplayComponent comp, Node left) {
			this.comp = comp;
			this.left = left;
			this.type = NodeType.zero;
		}

		public Node(DisplayComponent comp, Node left, Node right) {
			this.comp = comp;
			this.left = left;
			this.right = right;
			this.type = NodeType.one;
		}

		/**
		 * @return the left
		 */
		public Node getLeft() {
			return left;
		}

		/**
		 * @param left the left to set
		 */
		public void setLeft(Node left) {
			this.left = left;
		}

		/**
		 * @return the right
		 */
		public Node getRight() {
			return right;
		}

		/**
		 * @param right the right to set
		 */
		public void setRight(Node right) {
			this.right = right;
		}

		/**
		 * @return the parent
		 */
		public Node getParent() {
			return parent;
		}

		/**
		 * @param parent the parent to set
		 */
		public void setParent(Node parent) {
			this.parent = parent;
		}

		/**
		 * @return the type
		 */
		public NodeType getType() {
			return type;
		}

		/**
		 * @param type the type to set
		 */
		public void setType(NodeType type) {
			this.type = type;
		}

		/**
		 * @return the comp
		 */
		public DisplayComponent getComp() {
			return comp;
		}

		/**
		 * @param comp the comp to set
		 */
		public void setComp(DisplayComponent comp) {
			this.comp = comp;
		}

	} // end Node class
	
	StringBuffer sb = new StringBuffer(1024);
	String downhook = "&#x1602;";
	String uphook = "&#x1603;";
	
	public void expandString(String tokens, int levels) {
		while (levels > 0) {
			sb.append(tokens);
			StringBuffer temp = new StringBuffer(2 * tokens.length());
			for (int i = 0; i < tokens.length(); i++) {
				if ('0' == tokens.charAt(i)) {
					temp.append("1");
				}
				else if ('1' == tokens.charAt(i)) {
					temp.append("01");
				}
			}
			expandString(temp.toString(), levels - 1);
		}
	}
	
	
	//the ControlFrame class extends PApplet, so we 
	//are creating a new processing applet inside a
	//new frame with a controlP5 object loaded
	public class ControlFrame extends PApplet {
		ControlP5 cp5;
		CheckBox checkbox;
		PApplet parent;
		int w, h;

		private ControlFrame() {
			// lock out initialization without arguments
		}

		public ControlFrame(PApplet theParent, int theWidth, int theHeight) {
			parent = theParent;
			w = theWidth;
			h = theHeight;
		}

		public void setup() {
			size(w, h);
			frameRate(25);			
			cp5 = new ControlP5(this);
			
			checkbox = cp5.addCheckBox("levels", 10, 10);
			// make adjustments to the layout of a checkbox.
			checkbox.setColorForeground(color(120));
			checkbox.setColorActive(color(255));
			checkbox.setColorLabel(color(255));
			checkbox.setItemsPerRow(3);
			checkbox.setSpacingColumn(30);
			checkbox.setSpacingRow(10);
			chx = new ArrayList<Integer>();
			// add items to a checkbox.
			int i = 0;
			for (i = 0; i < levels; i++) {
				checkbox.addItem("so_"+i, i);
				chx.add(i, new Integer(0));
			}
			chx.add(i, new Integer(0));
		}

		public void draw() {
			background(123, 123, 165);
		}

		public ControlP5 control() {
			return cp5;
		}
		
		public void controlEvent(ControlEvent evt) {
			println("event " + evt);
			if (evt.isGroup()) {
				/* print("got an event from "+evt.group().name()+"\t"); */
				// checkbox uses arrayValue to store the state of 
				// individual checkbox-items. usage:
				for(int i = 0; i < evt.getGroup().getArrayValue().length; i++) {
					int n = (int) evt.getGroup().getArrayValue(i);
					/* print(n +", "); */
					chx.set(i, new Integer(n));
				}
				/* println(); */
			}
		}

		/* (non-Javadoc)
		 * handles key presses intended as commands
		 * pass them to host app
		 * @see processing.core.PApplet#keyPressed()
		 */
		public void keyPressed() {
			println("---- panel keyPressed = "+ key);
			parseKey(key);
		}	


	}  // end ControlFrame class


}
