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
	StringBuffer sb = new StringBuffer(1024);
	UniBlox bloxx = new UniBlox();
	String seedString = "";
	String outString = "";
	int depth = 5;
	public ArrayList<BezShape> shapes;
	IgnoCodeLib igno;

	public static final float GOLDEN = (float)((Math.sqrt(5) - 1)/2.0 + 1);
	public static final float INVGOLDEN = GOLDEN - 1;
	public static final float NEXTGOLDEN = GOLDEN + 1;
	float w = GOLDEN;
	float tw = 1;
	int[] FIB = { 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 
							  2584, 4181, 6765, 10946, 17711, 28657, 46368, 75025, 121393, 196418, 
							  317811, 514229, 832040, 1346269 };

	String filePath = "/Users/paulhz/Desktop/Eclipse_output/fibotree";
	String basename = "";
	int fileCount = 1;

	
	public void setup() {
		// scaled size for NSF lobby, 1 foot = 24 pixels
		size(1536, 276);
		smooth();
		noLoop();
		igno = new IgnoCodeLib(this);
		sb = new StringBuffer(1024);
		shapes = new ArrayList<BezShape>();
		// depth of 17 yields 1597 bands
		depth = 11;
		initLindenmeyer_000();
		expandString(seedString, depth);
		decodeString();
		loadShapes();
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "FiboTreeGrid" });
	}

	public void draw() {
		background(255);
		for (BezShape bez: shapes) {
			bez.draw();
		}
	}
	
	public void keyPressed() {
		if ('s' == key || 'S' == key) {
			saveAI();
		}
	}
	
	public String pad(char padCh, int len) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; i++) {
			sb.append(padCh);
		}
		return sb.toString();
	}
	

	public void expandString(String tokens, int level) {
		// println("level is "+ level + "\n  "+ tokens);
		int pos = level + 1;
		int fib1 = FIB[pos + 1] - 1;
		int fib0 = FIB[pos] - 1;
		String pad1 = pad('_', fib1);
		String pad0 = pad(' ', fib0);
		StringBuffer gridBuf = new StringBuffer();
		for (int i = 0; i < tokens.length(); i++) {
			char ch = tokens.charAt(i);
			if ('0' == ch) gridBuf.append(ch + pad0);
			else if ('1' == ch) gridBuf.append(ch + pad1);
			else println("error: ch = "+ ch);
		}
		println(gridBuf.toString());
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
		Pattern p = Pattern.compile("11");
		Matcher m = p.matcher(temp.toString());
		// replace all "11" with "2"
		outString = m.replaceAll("2");
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
	
	
	public void loadShapes() {
		float x = 0;
		float y = 0;	
		StringBuffer buf = new StringBuffer(outString);
		w = width/tw;
		float bigW = w * GOLDEN;
		float littleW = w * INVGOLDEN;
		for (int i = 0; i < buf.length(); i++) {
			char ch = buf.charAt(i);
			if ('0' == ch) {
				noStroke();
				fill(233, 144, 55);
				shapes.add(BezRectangle.makeLeftTopRightBottom(x, 0, x + littleW, height));
				x += littleW;
			}
			else if ('1' == ch) {
				noStroke();
				fill(55, 144, 233);
				shapes.add(BezRectangle.makeLeftTopRightBottom(x, 0, x + w, height));
				x += w;				
			}
			else if ('2' == ch) {
				noStroke();
				fill(55, 233, 144);
				shapes.add(BezRectangle.makeLeftTopRightBottom(x, 0, x + bigW, height));
				x += bigW;				
			}
			else { 
				println("error: ch = "+ ch); 
			}
		}
		println("---- x = "+ x);		
	}
	

	public void initLindenmeyer_000() {
		bloxx.put('0', "1");
		bloxx.put('1', "01");		
		bloxx.encode('0', "0");
		bloxx.encode('1', "1");
		seedString = "0";
	}
	
	
	public String getTimestamp() {
		return nf(day(),2) + nf(hour(),2) + nf(minute(),2) + nf(second(),2);
	}
	
	
	public void saveAI() {
		String filename = filePath +"/fpat_"+ getTimestamp() +"_"+ fileCount++ + ".ai";
		ArrayList<Integer> colors = new ArrayList<Integer>();
		colors.add(Palette.composeColor(233, 144, 55, 255));
		colors.add(Palette.composeColor(55, 144, 233, 255));
		colors.add(Palette.composeColor(55, 233, 144, 255));
		saveAI(filename, shapes, colors);
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
		AIFileWriter.setUseTransparency(true);
		// comps.add(0, bgRect());
		println("adding components...");
		// create a layer for the shapes
		LayerComponent comp = new LayerComponent(this, "Shapes", 1);
		doc.add(comp);
		for (BezShape b : comps) {
			comp.add(b);
		}
		// create some guides (finish making them into guides in Illustrator)
		int panelCount = 16;
		int panelWidth = width/panelCount;
		comp = new LayerComponent(this, "Guidelines", 2);
		comp.hide();
		doc.add(comp);
		for (int i = 1; i < panelCount; i++) {
			BezLine bzline = BezLine.makeCoordinates(i * panelWidth, 0, i * panelWidth, height);
			comp.add(bzline);
		}
		fill(255,255,255,192);
		BezRectangle bzrect = BezRectangle.makeLeftTopRightBottom(10 * panelWidth, 0, 13 * panelWidth, height);
		comp.add(bzrect);
		doc.write(output);
	}

	
}
