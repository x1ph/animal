package generators.sorting;

import java.awt.Font;
import java.awt.PageAttributes.ColorType;
import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

import algoanim.animalscript.AnimalScript;
import algoanim.primitives.Circle;
import algoanim.primitives.Primitive;
import algoanim.primitives.SourceCode;
import algoanim.primitives.Text;
import algoanim.primitives.Variables;
import algoanim.primitives.generators.Language;
import algoanim.properties.AnimationPropertiesKeys;
import algoanim.properties.ArcProperties;
import algoanim.properties.CircleProperties;
import algoanim.properties.PolylineProperties;
import algoanim.properties.SourceCodeProperties;
import algoanim.properties.TextProperties;
import algoanim.util.Coordinates;
import algoanim.util.Offset;
import animal.editor.properties.PropEditor;
import generators.framework.Generator;
import generators.framework.GeneratorType;
import generators.framework.ValidatingGenerator;
import generators.framework.properties.AnimationPropertiesContainer;
import translator.Translator;

/**
 * LocalOutlierFactor is a ValidatingGenerator to create an ANIMAL-animation for the LOF-Algorithm.
 * 
 * This  animation contains the Source code on the right side with highlightings on the currently active lines, a
 * notification-box at the top, showing hints about the current operations and a cartesian coordinate system on the 
 * left side showing the points and currently calculated distances and values.
 * 
 * The constant k and the list of points will be served by parameters as well as properties for
 *  - Source Highlighting Color
 *  - Current Point Color
 *  - Near Points Color
 *  - Distance Line Color
 *  - LOF-Ring Color.
 *  - inlier-color
 *  - outlier-color
 * @author Andre Challier <andre.challier@stud.tu-darmstadt.de>
 *
 */
public class LocalOutlierFactor implements ValidatingGenerator {
	
	/*
	 * ANIMAL
	 */
	private Language lang;
	private Locale loc;
	private Translator trl;
    private Variables vars;
    private SourceCode sc;
    private Text title;
    private Text hint;
	
    /*
     * PRIMITIVES
     */
	private List<Point> points;
	private int k;
	private int pointcnt = 0;
	
	/*
	 * PROPERTYS
	 */
    private CircleProperties props_points;
    private SourceCodeProperties props_sourcecode;
    private CircleProperties props_nearPoints;
    private CircleProperties props_LOFRing;
    private CircleProperties props_currentPoint;
    private CircleProperties props_inliner;
    private CircleProperties props_outliner;
    private PolylineProperties props_distance;
    //hardcoded
    private TextProperties props_title;
    private TextProperties props_text;
    
    /*
     * STATIC STRINGS
     */
    // Animal Primitives Arguments
    private static final String ARG_K = "k";
    private static final String ARG_POINTS = "Points";
    // Animal Properties Arguments
    private static final String ARG_SOURCECODE = "SourceCode";
    private static final String ARG_NEARPOINTS = "NearPoints";
    private static final String ARG_LOF_RING = "LOF-Ring";
    private static final String ARG_CURRENTPOINT = "CurrentPoint";
    private static final String ARG_INLIER = "Inlier";
    private static final String ARG_OUTLIER = "Outlier";
    private static final String ARG_DISTANCE = "Distance";
    // Animal Primitives Names
    private static final String NAME_TITLE = "title";
    private static final String NAME_SOURCECODE = "sourcecode";
    private static final String NAME_HINT = "hint";
    // Translator General Keys
    private static final String TRL_TITLE = "TITLE";
    // Translator Section Keys
    private static final String TRL_SECTION_DESCRIPTION = "SECTION_DESCRIPTION";
	private static final String TRL_SECTION_KDIST = "SECTION_KDIST";
	private static final String TRL_SECTION_LRD = "SECTION_LRD";
	private static final String TRL_SECTION_LOF = "SECTION_LOF";
	// Translator Variable Keys
	private static final String TRL_VAR_K = "VAR_K";
	private static final String TRL_VAR_POINT = "VAR_POINT";

	/**
	 * Create a new Instance of LocalOutlierFactor with the Locale defined in loc.
	 * 
	 * @param loc
	 */
	public LocalOutlierFactor(String path, Locale loc) {
		this.loc = loc;
		points = new LinkedList<Point>();
		trl = new Translator(path, loc);
		//hardcoded propertys
		props_title = new TextProperties();
		props_title.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		props_text = new TextProperties();
		props_text.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.PLAIN, 12));
	}
	
	private void calculateLOF() {
		sc.highlight(0);
		hint.setText("Calculate K-Nearest-Neighbor list and K-Distance for every Point", null, null);
		lang.nextStep();
		
		for(Point p : points) {
			hint.setText("Calculate K-Nearest-Neighbor list and K-Distance for Point" + points.indexOf(p), null, null);
			p.highlight;
		    p.setKnn(clone(points));
		    p.getKnn().sort(new Comparator<Point>() {

				@Override
				public int compare(Point o1, Point o2) {
					return Double.compare(distance(p, o1), distance(p, o2));
				}
			});
		    p.setkDst(distance(p, p.getKnn().get(k)));
		    p.getKnn().removeIf(o -> distance(o,p) > p.getkDst());
		}

		for(Point p: points) {
		    double sum_rd = 0;
		    for(Point q: p.getKnn()) {
		        sum_rd += reachabilityDistance(p,q);
		    }
		    p.lrd = 1 / (sum_rd / (double)p.getKnn().size());
		}

		for(Point p: points) {
		    double sum_lrd = 0;
		    for(Point q: p.getKnn()) {
		        sum_lrd += q.getLrd();
		    }
		    p.lof = (sum_lrd / (double)p.getKnn().size()) / p.getLrd();
		}
	}
	
	private List<Point> clone(List<Point> list) {
		List<Point> rtn = new LinkedList<Point>();
		for(Point p: list) {
			rtn.add(p);
		}
		return rtn;
	}

	@Override
	public String generate(AnimationPropertiesContainer props,Hashtable<String, Object> primitives) {
		//get properties
		props_points = (CircleProperties)props.getPropertiesByName(ARG_POINTS);
        props_sourcecode = (SourceCodeProperties)props.getPropertiesByName(ARG_SOURCECODE);
        props_nearPoints = (CircleProperties)props.getPropertiesByName(ARG_NEARPOINTS);
        props_LOFRing = (CircleProperties)props.getPropertiesByName(ARG_LOF_RING);
        props_currentPoint = (CircleProperties)props.getPropertiesByName(ARG_CURRENTPOINT);
        props_inliner = (CircleProperties)props.getPropertiesByName(ARG_INLIER);
        props_outliner = (CircleProperties)props.getPropertiesByName(ARG_OUTLIER);
        props_distance = (PolylineProperties)props.getPropertiesByName(ARG_DISTANCE);
        
        //Draw Title
  		title = lang.newText(new Coordinates(30,30), trl.translateMessage(TRL_TITLE), NAME_TITLE, null, props_title);
          
  		// Draw SourceCode
  		
   		sc = lang.newSourceCode(new Offset(0, 20, title, AnimalScript.DIRECTION_SW), NAME_SOURCECODE, null, props_sourcecode);
   		
   		sc.addCodeLine(trl.translateMessage("SRC_0"), null, 0, null);	// line 0
   		sc.addCodeLine(trl.translateMessage("SRC_1"), null, 1, null);	// line 1
   		sc.addCodeLine(trl.translateMessage("SRC_2"), null, 1, null);	// line 2
   		sc.addCodeLine(trl.translateMessage("SRC_3"), null, 1, null);	// line 3
   		sc.addCodeLine(trl.translateMessage("SRC_4"), null, 1, null);	// line 4
   		sc.addCodeLine(trl.translateMessage("SRC_5"), null, 0, null);	// line 5
   		sc.addCodeLine(trl.translateMessage("SRC_6"), null, 1, null);	// line 6
   		sc.addCodeLine(trl.translateMessage("SRC_7"), null, 1, null);	// line 7
   		sc.addCodeLine(trl.translateMessage("SRC_8"), null, 2, null);	// line 8
   		sc.addCodeLine(trl.translateMessage("SRC_9"), null, 1, null); 	// line 9
   		sc.addCodeLine(trl.translateMessage("SRC_10"), null, 0, null);	// line 10
   		sc.addCodeLine(trl.translateMessage("SRC_11"), null, 1, null);	// line 11
   		sc.addCodeLine(trl.translateMessage("SRC_12"), null, 1, null);	// line 12
   		sc.addCodeLine(trl.translateMessage("SRC_13"), null, 2, null);	// line 13
   		sc.addCodeLine(trl.translateMessage("SRC_14"), null, 1, null);	// line 14
   		lang.nextStep();
   		hint = lang.newText(new Offset(20, 0, sc, AnimalScript.DIRECTION_NE), "Hinweis:", NAME_HINT, null, props_text);
        //get primitives
        k = (Integer)primitives.get(ARG_K);
        String[][] stringMatrix = (String[][])primitives.get(ARG_POINTS);
        for(int i = 0; i < stringMatrix[0].length; i++) {
        	float x = Float.parseFloat(stringMatrix[0][i]);
        	float y = Float.parseFloat(stringMatrix[1][i]);
        	points.add(new Point(x, y));
        }
        
 		
 		calculateLOF();
 		lang.nextStep();
        
		return lang.toString();
	}

	public String getName() {
        return "Local Outlier Factor";
    }

    public String getAlgorithmName() {
        return "Local Outlier Factor";
    }

    public String getAnimationAuthor() {
        return "Andre Challier, Christian Richter";
    }

    public String getDescription(){
        return "The local outlier factor is an algorithm proposed by Markus M. Breunig, Hans-"
 +"\n"
 +"Peter Kriegel, Raymond T. Ng and Jörg Sander in 2000 for finding anomalous data"
 +"\n"
 +"points by measuring the local deviation of a given data point with respect to"
 +"\n"
 +"is neighbors."
 +"\n"
 +"\n"
 +"The algorithm starts with a constant k and a set of objects with a distance"
 +"\n"
 +"function. At first the distance to the k-th nearest neighbor will be determined"
 +"\n"
 +"for every object A ( k-distance(A) ), as well as a set of objects containing all"
 +"\n"
 +"objects within the k-distance of an object ( Nk(A) )."
 +"\n"
 +"\n"
 +"The next step is to calculate the local reachability density (lrd) for every"
 +"\n"
 +"object, wich is the inverse of the average reachability distance of an object"
 +"\n"
 +"from its neighbors, wich can be infinite at duplicate points."
 +"\n"
 +"\n"
 +"Afterwards the local reachability densities will be compared with those of the"
 +"\n"
 +"neighbors, which is the average local reachability density of the neighbors"
 +"\n"
 +"divided by the objects own local reachability density. A value near 1 indicates"
 +"\n"
 +"that the object comparable to its neighbors, a value below 1 indicates a denser"
 +"\n"
 +"region, while values significantly larger than 1 indicate outliers.";
    }

    public String getCodeExample(){
        return "CODE";
    }

    public String getFileExtension(){
        return "asu";
    }

    public Locale getContentLocale() {
    	return loc;
    }

    public GeneratorType getGeneratorType() {
        return new GeneratorType(GeneratorType.GENERATOR_TYPE_SORT);
    }

    public String getOutputLanguage() {
        return Generator.PSEUDO_CODE_OUTPUT;
    }

	@Override
	public void init() {
		lang = new AnimalScript("Local Outlier Factor", "Andre Challier, Christian Richter", 800, 600);
		lang.setStepMode(true);
		lang.setInteractionType(Language.INTERACTION_TYPE_AVINTERACTION);
		vars = lang.newVariables();
	}

	@Override
	public boolean validateInput(AnimationPropertiesContainer properties, Hashtable<String, Object> primitives)
			throws IllegalArgumentException {
		int k = (Integer)primitives.get("k");
        String[][] stringMatrix = (String[][])primitives.get("Points");
        if(stringMatrix.length != 2) throw new IllegalArgumentException("Points Matrix must have 2 columns!");
        if(stringMatrix[0].length < k) throw new IllegalArgumentException("Constant k must be lesser then the number of Points");
        for(int i = 0; i < stringMatrix[0].length; i++) {
        	float x;
        	float y;
        	try {
        		x = Float.parseFloat(stringMatrix[0][i]);
        	} catch (NumberFormatException nfe) {
        		throw new IllegalArgumentException("\"" + stringMatrix[0][i] + "\" is not a floating-point arithmetic.");
        	}
        	try {
        		y = Float.parseFloat(stringMatrix[1][i]);
        	} catch (NumberFormatException nfe) {
        		throw new IllegalArgumentException("\"" + stringMatrix[1][i] + "\" is not a floating-point arithmetic.");
        	}
        	if(0 > x || 500 < x) {
        		throw new IllegalArgumentException("\"" + x + "\" is out of range (0-500).");
        	}
        	if(0 > y || 500 < y) {
        		throw new IllegalArgumentException("\"" + y + "\" is out of range (0-500).");
        	}
        }
		return true;
	}
	
	public double distance(Point a, Point b) {
		return Math.sqrt(Math.pow(a.getX()- b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
	}
	
	public double reachabilityDistance(Point a, Point b) {
		return Math.max(b.getkDst(), distance(a,b));
	}
	
	public class Point {
		
		private double x;
		private double y;
		
		private double kDst;
		private double lrd;
		private double lof;
		private Circle dot;
		
		private List<Point> knn;
		
		public Point(double x, double y) {
			this.x = x;
			this.y = y;
			kDst = Double.NaN;
			lrd = Double.NaN;
			lof = Double.NaN;
			dot = lang.newCircle(new Offset((int)(x + 20), (int)(y + 20), hint, AnimalScript.DIRECTION_SW), 3, "point" + pointcnt++, null, props_points);
		}
		
		public double dist(Point p) {
			return Math.sqrt(Math.pow(getX()- p.getX(), 2) + Math.pow(getY() - p.getY(), 2));
		}
		
		public void setCurrent() {
			dot.changeColor(ColorType.COLOR, props_currentPoint.get(CircleProperties.), null, null);
		}
		
		public void setDefault() {
			
		}
		
		public void setNear() {
			
		}

		public double getX() {
			return x;
		}

		public void setX(double x) {
			this.x = x;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}

		public double getkDst() {
			return kDst;
		}

		public void setkDst(double kDst) {
			this.kDst = kDst;
		}

		public double getLrd() {
			return lrd;
		}

		public void setLrd(double lrd) {
			this.lrd = lrd;
		}

		public double getLof() {
			return lof;
		}

		public void setLof(double lof) {
			this.lof = lof;
		}

		public List<Point> getKnn() {
			return knn;
		}

		public void setKnn(List<Point> knn) {
			this.knn = knn;
		}
		
		public String toString() {
			return "Point [" + x + "," + y + "]";
		}
		public String toStringFull() {
			String str = "Point [" + x + "," + y + "] kdst=" + getkDst() + " lrd=" + getLrd() + " lof=" + getLof() + "\n";
			str += "\tNeighors:\n";
			for(Point p : knn) {
				str += "\t" + p.toString() + "\n";
			}
			return str;
		}
	}

}
