import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by chris on 08.07.2017.
 */
public class LOF {
    public static void main(String[] args) {
        compute cp = new compute();
        cp.make();
    }

}

class compute {

    public compute() {

    }

    void make() {
        int randomNum = ThreadLocalRandom.current().nextInt(20, 50 + 1);
        int k = 3;

        Dot[] points = new Dot[randomNum];
        double[] lof = new double[randomNum];

        for (int i = 0; i < randomNum; i++) {
            points[i] = new Dot(ThreadLocalRandom.current().nextInt(0, 50 + 1), ThreadLocalRandom.current().nextInt(0, 50 + 1));
        }


        int i = 0;
        for (Dot t : points) {
            lof[i] = LOF(t, points, k);
            System.out.println("Ergebnis: " + t.toString() + ", LOF: " + lof[i]);
            i++;
        }
    }

    /**
     * Rechnet die Distanzen aller Punkte zu einem Bestimmten Punkt aus.
     *
     * @param a      Point of interest
     * @param points all points
     * @return die Distanzen aller Punkte zu einem Bestimmten Punkt
     */
    private double[] distToTuple(Dot a, Dot[] points) {
        double j[] = new double[points.length - 1];
        for (int i = 1; i < points.length; i++) {
            float dx = Math.abs(a.getX() - points[i].getX());
            float dy = Math.abs(a.getY() - points[i].getY());
            j[i - 1] = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        }
        for (double d : j) {
            //   System.out.println(d);
        }

        return j;
    }

    /**
     * rechnet alle distanzen zu a aus
     */
    private double[] allKDist(Dot a, Dot[] points, int k) {
        //System.out.println(a.getX() + ":" + a.getY() + ", " + k);
        double j[] = distToTuple(a, points);
        Arrays.sort(j);
        return j;
    }

    /**
     * gibt den Abstand zum k-ten nachbarn zurück
     */
    private double kDist(Dot a, Dot[] points, int k) {
        double j[] = allKDist(a, points, k);
        return j[k - 1];
    }

    /**
     * erreichbarkeitsdistanz: das max aus realem abstand oder der k-distanz
     */
    private double reachdist(Dot a, Dot b, Dot[] points, int k) {
        double kdist = kDist(a, points, k);
        double realdist = distance(a, b);
        if (kdist > realdist)
            return kdist;
        return realdist;
    }

    /**
     * berechnet distanzen zwischen punkten aus
     */
    private double distance(Dot a, Dot b) {
        return Math.sqrt(Math.pow(Math.abs(a.getX() - b.getX()), 2) + Math.pow(Math.abs(a.getY() - b.getY()), 2));
    }

    /**
     * gibt alle Dots im radius k zu a aus
     */
    private Dot[] tuplesInKDist(Dot a, Dot[] points, int k) {
        Dot[] t = new Dot[k];
        int i = 0;
        for (Dot tup : t) {
            if (distance(a, tup) <= kDist(a, points, k)) {
                t[i] = tup;
                i++;
            }
        }
        return t;
    }

    /**
     * filtert den dot a aus allen punkten raus
     */
    private Dot[] filterDots(Dot a, Dot[] points) {

        Dot[] dots = new Dot[points.length - 1];
        int j = 0;
        for (int i = 0; i < points.length; i++) {
            //System.out.println(points[i] == null);
            if (!points[i].equals(a)) {
                dots[j] = points[i];
                j++;
            }
        }
        return dots;
    }

    /**
     * ??magic??
     */
    private double lrd(Dot a, Dot[] points, int k) {
        //System.out.println("Computing lrd...");
        double sum = 0;
        for (Dot t : points) {
            if (distance(a, t) <= kDist(a, points, k)) {
                sum += reachdist(a, t, points, k);
            }
        }
        return 1 / (sum / k);
    }


    private double LOF(Dot currentDot, Dot[] allDots, int k) {
        Dot[] dots = filterDots(currentDot, allDots);
        double sumLOF = 0;
        for (Dot dot : dots) {
            if (distance(currentDot, dot) <= kDist(currentDot, dots, k)) {
                sumLOF += lrd(dot, dots, k);
            }
        }
        return (sumLOF / k) / lrd(currentDot, dots, k);
    }
}

class Dot {
    private float x;
    private float y;

    float getX() {
        return x;
    }

    float getY() {
        return y;
    }

    Dot(float a, float b) {
        this.x = a;
        this.y = b;
    }

    public String toString() {
        return "(" + this.getX() + ":" + this.getY() + ")";
    }

}
