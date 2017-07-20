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
        points[0] = new Dot(424, 24242);


        int i = 0;
        for (Dot t : points) {
            lof[i] = LOF(t, points, k);
            System.out.println("Ergebnis: " + t.toString() + ", LOF: " + lof[i]);
            i++;
        }

        /** double[] p = distToTuple(points[0], points);
         for (double d : p) {
         //System.out.println(d);
         }

         double[] j = allKDist(points[9], points, k);
         for (double d : j) {
         System.out.println(d);
         }
         // System.out.println(k + ": " + kDist(points[0], points, k));


         for (Dot t : points) {
         //System.out.println(lrd(points[0], points, k));
         }**/
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
        //System.out.println(a.toString() + ";" + b.toString()+"; kdist: " + kdist + ", realdist: " + realdist);
        if (kdist > realdist)
            return kdist;
        return realdist;
    }

    private double distance(Dot a, Dot b) {
        return Math.sqrt(Math.pow(Math.abs(a.getX() - b.getX()), 2) + Math.pow(Math.abs(a.getY() - b.getY()), 2));
    }

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

    private Dot[] filter(Dot a, Dot[] points) {

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


    private double LOF(Dot a, Dot[] points, int k) {
        // System.out.println("Berechne LOF für: " + a.toString());
        Dot[] dots = filter(a, points);
        double sum = 0;
        for (Dot t : dots) {
            if (distance(a, t) <= kDist(a, dots, k)) {
                // System.out.println("Addiere " + t.toString());
                sum += lrd(t, dots, k);
            }
        }
        double erg = (sum / k) / lrd(a, dots, k);
        return erg;
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
