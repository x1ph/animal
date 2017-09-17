package generators.sorting;

import java.awt.Font;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import algoanim.animalscript.AnimalScript;
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
public class LocalOutlierFactor_backup implements ValidatingGenerator {
	
	/*
	 * ANIMAL
	 */
	private Language lang;
	private Locale loc;
	private Translator trl;
    private Variables vars;
    private SourceCode sc;
    private Text title;
	
    /*
     * PRIMITIVES
     */
	private Collection<Point> points;
	private int k;
	
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
	public LocalOutlierFactor_backup(String path, Locale loc) {
		this.loc = loc;
		points = new LinkedList<Point>();
		trl = new Translator(path, loc);
		
		//hardcoded propertys
		props_title = new TextProperties();
		props_title.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.PLAIN, 20));
	}

	/**
	 * Creates a Point object at coordinates (x,y) and adds it tho this animations list of points. The Point will also
	 * be added as neighbor to all existing points making it easier to estimate the k'th nearest neighbor etc.
	 * 
	 * @param a X-Coordiante of the new Point
	 * @param b Y-Coordinate of the new Point
	 */
	private void addPoint(double a, double b) {
		Point p = new Point(a, b);
		for(Point q : points) {
			q.addNeighbor(p);
		}
		p.addNeighbors(points);
		points.add(p);
	}
	
	public Collection<Point> getPoints() {
		return points;
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
        
        //get primitives
        k = (Integer)primitives.get(ARG_K);
        String[][] stringMatrix = (String[][])primitives.get(ARG_POINTS);
        for(int i = 0; i < stringMatrix[0].length; i++) {
        	float x = Float.parseFloat(stringMatrix[0][i]);
        	float y = Float.parseFloat(stringMatrix[1][i]);
        	addPoint(x, y);
        }
        int cnt = 0;
        for(Point p: points) {
        	double lrd = p.getKNearestNeighbors().stream().mapToDouble(o -> o.getKDist()).sum();
        	lrd = 1 / (lrd / (double)p.getKNearestNeighbors().size());
        	System.out.println("Point " + cnt + ": lrd=" + lrd + " p.lrd = " + p.lrd);
        }
        
        
        //Draw Title
		title = lang.newText(new Coordinates(30,30), trl.translateMessage(TRL_TITLE), NAME_TITLE, null, props_title);
        
		// Draw SourceCode
		
 		sc = lang.newSourceCode(new Offset(0, 20, title, AnimalScript.DIRECTION_SW), NAME_SOURCECODE, null, props_sourcecode);
 		
 		sc.addCodeLine(trl.translateMessage("SRC_0"), null, 0, null);	// line 0
 		sc.addCodeLine(trl.translateMessage("SRC_1"), null, 1, null);	// line 1
 		sc.addCodeLine(trl.translateMessage("SRC_2"), null, 2, null);	// line 2
 		sc.addCodeLine(trl.translateMessage("SRC_3"), null, 2, null);	// line 3
 		sc.addCodeLine(trl.translateMessage("SRC_4"), null, 1, null);	// line 4
 		sc.addCodeLine(trl.translateMessage("SRC_5"), null, 2, null);	// line 5
 		sc.addCodeLine(trl.translateMessage("SRC_6"), null, 3, null);	// line 6
 		sc.addCodeLine(trl.translateMessage("SRC_7"), null, 3, null);	// line 7
 		sc.addCodeLine(trl.translateMessage("SRC_8"), null, 1, null);	// line 8
 		sc.addCodeLine(trl.translateMessage("SRC_9"), null, 2, null); 	// line 9
 		sc.addCodeLine(trl.translateMessage("SRC_10"), null, 2, null);	// line 10
 		sc.addCodeLine(trl.translateMessage("SRC_11"), null, 1, null);	// line 11
 		sc.addCodeLine(trl.translateMessage("SRC_12"), null, 1, null);	// line 12
 		sc.addCodeLine(trl.translateMessage("SRC_13"), null, 2, null);	// line 13
 		sc.addCodeLine(trl.translateMessage("SRC_14"), null, 1, null);	// line 14
 		sc.addCodeLine(trl.translateMessage("SRC_15"), null, 2, null);	// line 15
 		sc.addCodeLine(trl.translateMessage("SRC_16"), null, 3, null);	// line 16
 		sc.addCodeLine(trl.translateMessage("SRC_17"), null, 3, null);	// line 17
 		sc.addCodeLine(trl.translateMessage("SRC_18"), null, 3, null);	// line 18
        
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
	
	public class Point {
		
		private double x;
		private double y;
		/**
		 * This list stores Neighbors (Points and there distances to this Point) sorted by thair distance to this Point.
		 */
		private List<Neighbor> neighbors;
		
		private double lrd;
		private double lof;
		
		public Point(double x, double y) {
			this.x = x;
			this.y = y;
			lrd = Double.NaN;
			lof = Double.NaN;
			neighbors = new LinkedList<Neighbor>();
		}
		
		/**
		 * Adds a neighbor to this Points list of neighbors.
		 * 
		 * @param p - the neighbor that will be added.
		 */
		public void addNeighbor(Point p) {
			double dst = dstTo(p);
			Neighbor n = new Neighbor(p, dst);
			
			for(int i = 0; i < neighbors.size(); i++) {
				if(neighbors.get(i).dst > dst) {
					neighbors.add(i, n);
					return;
				}
			}
			neighbors.add(n);
		}
		
		public void addNeighbors(Collection<Point> points) {
			for(Point p: points) {
				addNeighbor(p);
			}
		}
		
		/**
		 * Returns all neighbors within the k-distance of this Point as Collection.
		 * 
		 * That are all neighbors that are closer or as close as the k-th nearest neighbor.
		 * 
		 * @return A Collection of this Points K-Nearest-Neighbors
		 */
		public Collection<Point> getKNearestNeighbors() {
			List<Point> knn = new LinkedList<Point>();
			for(int i = 0; i < neighbors.size(); i++) {
				Neighbor n = neighbors.get(i);
				if(n.dst <= getKDist()) {
					knn.add(n.getP());
				}else {
					break;
				}
			}
			return knn;
		}
		
		/**
		 * Returns the euclidean distance from this Point to Point p.
		 * @param p the other Point
		 * @return distance between this Point and p.
		 */
		public double dstTo(Point p) {
			return Math.sqrt(
						Math.pow(x - p.getX(), 2) + 
						Math.pow(y - p.getY(), 2)
					);
		}
		
		/**
		 * Returns the reachability distance of Point a from Point b.
		 * 
		 * The reachability distance is the euclidean distance between a and b but at least the k-distance of b.
		 * 
		 * @param a - The reachable Point
		 * @param b - The point from what a is reached
		 * @return The reachability distance of Point a from Point b
		 */
		public double reachabilityDistance(Point a, Point b) {
			return Math.max(b.getKDist(), a.dstTo(b));
		}
		
		/**
		 * Returns the distance to the k-th nearest neighbor of this Point.
		 * 
		 * @return The distance to the k-th nearest neighbor
		 */
		public double getKDist() {
			return neighbors.get(k).dst;
		}
		
		public double getLRD() {
			if(Double.isNaN(lrd)) {
				double sum = 0;
				int cnt = 0;
				for(Point p: getKNearestNeighbors()) {
					sum += reachabilityDistance(this, p);
					cnt++;
				}
				lrd = 1/(sum/(double)cnt);
			}
			return lrd;
		}
		
		public double getLOF() {
			if(Double.isNaN(lof)) {
				double sum = 0;
				int cnt = 0;
				for(Point p: getKNearestNeighbors()) {
					sum += p.getLRD();
					cnt++;
				}
				lof = (sum / (double)cnt)/ getLRD();
			}
			return lof;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}
		
		/**
		 * Printing the values of this Point as well as the List of neighbors.
		 * 
		 * Example:
		 * 
		 * Point: x: 35, y: 27, k_dst: 19, lrd: 1.7, lof: 0.7
		 * Neighbors:
		 * 	0: Point[x,y] dst: x
		 * 	1: ...
		 * 	2: ...
		 * 	.
		 * 	.
		 */
		public String toString() {
			String s = new String();
			
			s += "Point: x: " + x + ", y: " + y + ", k_dst: " + getKDist() + ", lrd: " + getLRD() + ", lof: " + getLOF() + "\n";
			s += "Neighbors:\n";
			int cnt = 0;
			for(Neighbor n: neighbors) {
				s += cnt + ": Point[" + n.getP().getX() + "," + n.getP().getY() + "] dst:" + n.getDst() + "\n";
			}
			
			return s;
		}
		
		/**
		 * A Neighbor is a tuple of a Point and this Points distance to that point to create a sort of distance matrix.
		 * 
		 * @author Andre Challier <andre.challier@stud.tu-darmstadt.de>
		 *
		 */
		private class Neighbor {
			
			private Point p;
			private double dst;
			
			public Neighbor(Point p, double dst) {
				this.p = p;
				this.dst = dst;
			}
			
			public Point getP() {
				return p;
			}
			public double getDst() {
				return dst;
			}
		}
	}

}
