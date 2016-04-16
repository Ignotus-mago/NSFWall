package net.paulhertz.fibowall;

import processing.core.PApplet;
import java.io.*;
import java.util.*;
import java.awt.Frame;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import controlP5.*;
import net.paulhertz.aifile.*;
import net.paulhertz.util.*;


/**
 * @author paulhz
 * Class to design wall for NSF building, derived from Fibo_001.java
 * We are sticking to Processing 2.2.5 because ControlP5's external window class (ControlWindow)
 * depends on PApplet acting as a Java AWT Component. Processing 3 eliminates all Java AWT dependencies.
 * There are apparently some workarounds--but they can wait.
 */
public class Fibo_002 extends PApplet {
	static enum NodeType {zero, one};
	public static final float GOLDEN = (float)((Math.sqrt(5) - 1)/2.0 + 1);
	public static final float INVGOLDEN = GOLDEN - 1;
	public static final float ROOTTWO = (float)(Math.sqrt(2.0));
	public static final float INVROOTTWO = (float)(1/Math.sqrt(2.0));
	// sum a them Fibonacci numbers
	public static int[] FIB = { 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 
							  2584, 4181, 6765, 10946, 17711, 28657, 46368, 75025, 121393, 196418, 
							  317811, 514229, 832040, 1346269 };

	/** array of rectangles for the wall */
	public ArrayList<TaggedRectangle> blockList; 
	/** colors for shapes tagged with "1" */
	int[] oneColors;
	/** colors for shapes tagged with "0" */
	int[] zeroColors;
	/** colors for Illustrator palette */
	ArrayList<Integer> allColors;
	/** number triplets to generate the colors from fibotree142.ai */
	int[] p142 = { 21, 34, 55,   144, 152, 233,   29, 47, 76,   178, 199, 246,   
      110, 128, 152,   22, 199, 246, 	 123, 131, 144,   36, 94, 152 };
	/** another set of number triplets for generating colors */
	int[] pLight = { 199, 233, 220,   34, 144, 233,   76, 123, 199,   178, 199, 246,   
      110, 178, 152,   233, 220, 246, 	 123, 131, 144,   36, 94, 152 };
	/** an instance of net.paulhertz.util.RandUtil, for random operations */
	RandUtil rando;
	boolean animate = false;
	/** ControlP5 control interface */
	private ControlP5 controlP5;
	/** frame for the controls, breaks in Processing 3 */
	public ControlFrame cf;
	/** tracking of layer visibility, checkboxes */
	ArrayList<Integer> chx;
	
	/** reference to the graphics and i/o library */
	IgnoCodeLib igno;
	/** gap between rectangles, which we won't use for the NSF wall */
	float gap = 0;
	/** depth of recursion, 21 for the original fibotree142 art */
	int depth = 17;
	/** threshold for splitting rectangles: values below 1 favor horizontal, values above 1 favor vertical. */
	float verticality = 4.0f;
	/** level where we start to do vertical divisions */
	int startVertical = 9;
	/** interpolation array for calculating probability of a vertical or horizontal break
	 *  values < 1 bias to horizontal, values > 1 bias to vertical
	 *  verticality = arrayLerp(vertArray, (depth - seedTR.level)/((float) depth));
	 *  where finer divisions have smaller level values.
	 */
	float[] vertArray = {1, 0.75f, 0.5f, 0.75f, 0, 6, 12f, 24f };
	/** interpolation array with break probabilities */
	float[] breakArray = {0, 0.01f, 0.02f, 0.25f, 0};
	/** variable for panel width */
	int panelWidth;
	/** variable for number of panels */
	int panelCount = 16;

	// number formats
	DecimalFormat fourPlaces;
	DecimalFormat twoPlaces;
	DecimalFormat noPlaces;
	DecimalFormat fourFrontPlaces;
	
	// file tracking
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
		// testArrayLerp();
	}
	
	
	/**
	 * Entry point used in Eclipse
	 * @param args
	 */
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "Fibo_002" });
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
		for (TaggedRectangle tr : blockList) {
			if (0 == chx.get(tr.level)) {
				tr.block.draw();
			}
		}
	}

	public void keyPressed() {
		// println("---- main keyPressed = "+ key);
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
			// shuffle the colors but don't change the geometry
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
	
	
	/**
	 * Primary method for generating geometry and colors.
	 * @param useNewColors   if TRUE, initialize a new set of colors 
	 */
	public void revYourEngines(boolean useNewColors) {
		noLoop();
		if (useNewColors) initWallColors(p142);
		blockList = new ArrayList<TaggedRectangle>();
		oneRectangleWall();
		loop();
	}
	
	public void twoRectangleWall() {
		// the long rectangle, 10 panel widths
		TaggedRectangle tr = new TaggedRectangle(this, 0, 0, width - 6 * panelWidth, height, NodeType.zero, depth);
		blockList.add(tr);
		buildRectangles(tr);
		// the short rectangle, 3 panel widths
		TaggedRectangle tr2 = new TaggedRectangle(this, 13 * panelWidth, 0, 3 * panelWidth, height, NodeType.zero, depth);
		blockList.add(tr2);
		buildRectangles(tr2);
	}
	
	public void oneRectangleWall() {
		// the entire wall
		TaggedRectangle tr = new TaggedRectangle(this, 0, 0, width, height, NodeType.zero, depth);
		blockList.add(tr);
		buildRectangles(tr);
	}
	
	/**
	 * Use the palette from the NSF example fibotree142
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
	 * @author paulhz
	 * Storage class for rectangles marked with tag and level fields, with color field and visible flag.
	 *
	 */
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


	/**
	 * Recursively splits TaggedRectangles into new TaggedRectangles.
	 * @param seedTR   a TaggedRectangle to be split into new TaggedRectangles.
	 */
	public void buildRectangles(TaggedRectangle seedTR) {
		// println("level " + seedTR.level);
		// end recursion when the level field stored in seedTR hits 0
		if (seedTR.level < 1) return;
		// select colors to use for TR tagged "1" and "0" at current level
		int zeroColor, oneColor;
		int radix = 5;
		zeroColor = zeroColors[(seedTR.level) % radix];
		oneColor = oneColors[(seedTR.level) % radix];
		if (seedTR.tag == NodeType.zero) {
			addZeroNode(seedTR, zeroColor);
		}
		else if (seedTR.tag == NodeType.one) {
			addOneNode(seedTR, zeroColor, oneColor);
		}
		else {
			println("ERROR, missing tag value!");
		}
	}
	
	/**
	 * Adds a new TaggedRectangle as a "zero" node.
	 * @param seedTR      parent TaggedRectangle for new node
	 * @param zeroColor   color for new node
	 */
	public void addZeroNode(TaggedRectangle seedTR, int zeroColor) {
		// we randomly decide to omit a branch at specified levels
		// TODO make TRs invisible instead of omitting them (?)
		
		float normLevel = (depth - seedTR.level)/((float) depth);
		float breakProb = arrayLerp(breakArray, normLevel);
		float rand = rando.randGenerator().nextFloat();
		if (rand < breakProb ) {
			println("stochastic break at level - " + seedTR.level +", breakProb = "+ breakProb +", rand = "+ rand +", normLevel = "+ normLevel);
			return;
		}
		
		// on the zero branches, the TR does not split but we copy its rectangle to a new TR
		// if gap > 0 the new rectangle is inset by gap
		BezRectangle insetR = seedTR.block.inset(gap, gap);
		insetR.setFillColor(zeroColor);
		insetR.setNoStroke();
		TaggedRectangle tr = new TaggedRectangle(this, insetR, NodeType.one, seedTR.level - 1);
		blockList.add(tr);
		// println("inset at level " + seed.level);
		buildRectangles(tr);
	}
	
	/**
	 * Adds a two new TaggedRectangles as a "one" node and a "zero" node.
	 * @param seedTR      parent TaggedRectangle for new node
	 * @param zeroColor   color for new "zero" node
	 * @param oneColor    color for new "one" node
	 */
	public void addOneNode(TaggedRectangle seedTR, int zeroColor, int oneColor) {
		// we randomly decide to omit a branch at specified levels, 
		// see earlier code and Fibo_001.java for some variations
		// TODO make TRs invisible instead of omitting them (?)
		float normLevel = (depth + 1 - seedTR.level)/((float) depth);
		float breakProb = arrayLerp(breakArray, normLevel);
		float rand = rando.randGenerator().nextFloat();
		if (rand < breakProb ) {
			println("stochastic break at level - " + seedTR.level +", breakProb = "+ breakProb +", rand = "+ rand +", normLevel = "+ normLevel);
			return;
		}
		// on the one branches, the rectangle splits in two horizontally or vertically
		// how far over to shift the split, a number in the range 0..1
		float[] notches = new float[13];
		int i = 0;
		for (; i < 8; i++) notches[i] = INVGOLDEN;
		for (; i < 13; i++) notches[i] = INVGOLDEN * INVGOLDEN;
		float shift = rando.randomElement(notches);
		float w = seedTR.block.getWidth();
		float h = seedTR.block.getHeight();
		// decide whether to split vertically or horizontally
		// DONE implemented verticality lookup table with lerp
		verticality = arrayLerp(vertArray, (depth - seedTR.level)/((float) depth));
		// println("---- verticality = "+ verticality);
		boolean splitVertical = true;
		if (w == h) {
			// if the rectangle is a square, 50-50 percent chance of splitting either way
			if (random(2.0f) > 1) {
				splitVertical = false;
			}
		}
		else if (w < h / verticality) {
			splitVertical = false;
		}
		if (splitVertical) {
			float x1 = seedTR.block.getLeft() + gap;
			float w1 = (float) ( (w - 3 * gap) * shift ) + gap;
			float w2 = (float) ( (w - 3 * gap) * (1 - shift) ) - gap;
			float x2 = x1 + w1 + gap;
			float y1 = seedTR.block.getTop() + gap;
			BezRectangle r1 = BezRectangle.makeLeftTopWidthHeight(this, x1, y1, w1, h - 2 * gap);
			BezRectangle r2 = BezRectangle.makeLeftTopWidthHeight(this, x2, y1, w2, h - 2 * gap);
			// println(r1.bezType +" "+ (r1.bezType() == BezShape.BezType.BEZ_RECTANGLE));
			TaggedRectangle tr1;
			TaggedRectangle tr2;
			r1.setFillColor(zeroColor);
			r2.setFillColor(oneColor);
			r1.setNoStroke();
			r2.setNoStroke();
			tr1 = new TaggedRectangle(this, r1, NodeType.one, seedTR.level - 1);
			tr2 = new TaggedRectangle(this, r2, NodeType.zero, seedTR.level - 1);
			blockList.add(tr1);
			blockList.add(tr2);
			//println("split at level " + seed.level);
			buildRectangles(tr1);
			buildRectangles(tr2);
		}
		else {
			// horizontal split
			float y1 = seedTR.block.getTop() + gap;
			float h1 = (float) ( (h - 3 * gap) * shift ) + gap;
			float h2 = (float) ( (h - 3 * gap) * (1 - shift) ) - gap;
			float y2 = y1 + h1 + gap;
			float x1 = seedTR.block.getLeft() + gap;
			BezRectangle r1 = BezRectangle.makeLeftTopWidthHeight(this, x1, y1, w - 2 * gap, h1);
			BezRectangle r2 = BezRectangle.makeLeftTopWidthHeight(this, x1, y2, w - 2 * gap, h2);
			TaggedRectangle tr1;
			TaggedRectangle tr2;
			r1.setFillColor(zeroColor);
			r2.setFillColor(oneColor);
			r1.setNoStroke();
			r2.setNoStroke();
			tr1 = new TaggedRectangle(this, r1, NodeType.one, seedTR.level - 1);
			tr2 = new TaggedRectangle(this, r2, NodeType.zero, seedTR.level - 1);
			blockList.add(tr1);
			blockList.add(tr2);
			// println("split at level " + seed.level);
			buildRectangles(tr1);
			buildRectangles(tr2);
		}
	}
	
	// we'd like to use a value from 0..1 to obtain an interpolated value over an entire array
	public float arrayLerp(float[] arr, float pos) {
		if (pos >= 1) return arr[arr.length - 1];
		if (pos <= 0) return arr[0];
		// scale pos to the largest index of the array
		pos *= (arr.length - 1);
		int i = (int) Math.floor(pos);
		float frac = pos - i;
		float r = arr[i] + frac * (arr[i + 1] - arr[i]);
		return r;		
	}
	
	public void testArrayLerp() {
		float kk = 0;
		float inc = 0.0625f;
		while (kk <= 1) {
			println("-- ArrayLerp at "+ kk +" = "+ arrayLerp(vertArray, kk));
			kk += inc;
		}
	}

	
	/**
	 * saves shapes to an Adobe Illustrator file
	 */
	public void saveAI(String aiFilename, ArrayList<Integer> paletteColors) {
		// println("saving Adobe Illustrator file " + aiFilename + "...");
		PrintWriter output = createWriter(aiFilename);
		DocumentComponent doc = new DocumentComponent(this, "IgnoDoc");
		doc.setVerbose(false);
		Palette pal = doc.getPalette();
		pal.addBlackWhiteGray();
		pal.addColors(paletteColors);
		doc.setCreator("Ignotus");
		doc.setOrg("IgnoStudio");
		doc.setWidth(width);
		doc.setHeight(height);
		int layer = -1;
		for (int i = depth; i >= 0; i--) {
			layer = i + 1;
			LayerComponent comp = new LayerComponent(this, "Layer " + (layer), layer);
			if (chx.get(i) == 1) {
				comp.hide();
				comp.setName("--Layer " + (layer));
			}
			doc.add(comp);
			// PApplet.println("set visible to " + comp.isVisible());
			GroupComponent gZero = new GroupComponent(this);
			GroupComponent gOne = new GroupComponent(this);
			for (TaggedRectangle tr : blockList) {
				if (tr.level == i) {
					/*
					 * hiding the layers should be sufficient
					 * then we can try hiding "omitted" geometry instead
					if (chx.get(tr.level) == 0) {
						// comp.add(tr.block);
						tr.block.show();
					} 
					else {
						tr.block.hide();
					}
					*/
					if (tr.tag == NodeType.one) gOne.add(tr.block);
					if (tr.tag == NodeType.zero) gZero.add(tr.block);
				}
			}
			comp.add(gOne);
			comp.add(gZero);
		}
		// guides
		layer = depth + 2;
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
	
	
	// the ControlFrame class extends PApplet, so we 
	// are creating a new processing applet inside a
	// new frame with a controlP5 object loaded
	// this breaks in Processing 3
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
			for (i = 0; i < depth; i++) {
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
			// println("event " + evt);
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
			// println("---- panel keyPressed = "+ key);
			parseKey(key);
		}	


	}  // end ControlFrame class


}
