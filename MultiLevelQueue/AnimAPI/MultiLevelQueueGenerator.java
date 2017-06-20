import java.awt.Color;
import java.awt.Font;

import algoanim.exceptions.LineNotExistsException;
import algoanim.primitives.ArrayMarker;
import algoanim.primitives.IntArray;
import algoanim.primitives.SourceCode;
import algoanim.primitives.generators.AnimationType;
import algoanim.primitives.generators.Language;
import algoanim.properties.*;
import algoanim.util.Coordinates;
import algoanim.util.TicksTiming;
import algoanim.util.Timing;
import generators.maths.grid.GridProperty;

/**
 * @author  Andre Challier <andre.challier@stud.tu-darmstadt.de>, Christian Richter <chrisrichter145@gmail.com>
 * @version 0.1
 */
public class MultiLevelQueueGenerator {

  /**
   * The concrete language object used for creating output
   */
  private Language lang;

  /**
   * Default constructor
   *
   * @param l
   *          the conrete language object used for creating output
   */
  public MultiLevelQueueGenerator(Language l) {
    // Store the language object
    lang = l;
    // This initializes the step mode. Each pair of subsequent steps has to
    // be divdided by a call of lang.nextStep();
    lang.setStepMode(true);
  }

  private static final String DESCRIPTION     = "A Multi Level Queue for scheduling uses a predefined number of levels to"
          + "schedule processes. Processes get assigned to a particular level at insert."
          + "The processes in queues of higher level will then be executed first, lower level"
          + "queues will be executed when all higher level queues are empty. Each queue is"
          + "free to use its own scheduling, thus adding greater flexibility then merely"
          + "having multiple levels in a queue."

          + "\n\nIn this scenario each process has a tima of arrival (process.time), and a number"
          + "of execution timeslides (process.work). The algorithm loops until all processes"
          + "are done. To schedule the processes the algorithm first adds all arriving"
          + "processes to the queues, then picks the non-empty queue with the highest level"
          + "and executes the upcoming process."

          + "\n\nThe first queue in this example uses First-Come-First-Serve-Scheduling while the"
          + "second queue uses Round-Robin-Scheduling."  ;

  private static final String SOURCE_CODE     = "WHILE sum(proc.work) != 0"
          + "\n   FOR process IN procList"
          + "\n     IF process.time == step"
          + "\n       queue = queueList[process.level]"
          + "\n       queue.add(process)"
          + "\n   FOR i FROM 0 TO (queueList.size - 1)"
          + "\n     IF queueList[i].current() != null"
          + "\n       queue = queueList[i]"
          + "\n       BREAK"
          + "\n   IF queue == null"
          + "\n     CONTINUE"
          + "\n   run(queue.current())"
          + "\n   IF queue.current().work == 0"
          + "\n     queue.removeCurrent();"
          + "\n   ELSE"
          + "\n     if(queue.useRoundRobin)"
          + "\n       temp = queue.current()"
          + "\n       queue.removeCurrent()"
          + "\n       queue.add(temp)";

  /**
   * default duration for swap processes
   */
  public final static Timing  defaultDuration = new TicksTiming(30);

  /**
   * Sort the int array passed in
   *
   * @param a
   *          the array to be sorted
   */
  public void sort(int[] a) {
    // Create Array: coordinates, data, name, display options,
    // default properties

    // first, set the visual properties (somewhat similar to CSS)
    ArrayProperties arrayProps = new ArrayProperties();
    arrayProps.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLACK);
    arrayProps.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.WHITE);
    arrayProps.set(AnimationPropertiesKeys.FILLED_PROPERTY, Boolean.TRUE);
    arrayProps.set(AnimationPropertiesKeys.ELEMENTCOLOR_PROPERTY, Color.BLACK);
    arrayProps.set(AnimationPropertiesKeys.ELEMHIGHLIGHT_PROPERTY, Color.RED);
    arrayProps.set(AnimationPropertiesKeys.CELLHIGHLIGHT_PROPERTY, Color.YELLOW);

    // now, create the IntArray object, linked to the properties
    IntArray ia = lang.newIntArray(new Coordinates(20, 100), a, "intArray",
            null, arrayProps);

    MatrixProperties matrixProps = new MatrixProperties();
    matrixProps.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLACK);
    matrixProps.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.WHITE);
    matrixProps.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.WHITE);
    matrixProps.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.WHITE);
    matrixProps.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.WHITE);


    // start a new step after the array was created
    lang.nextStep();

    // Create SourceCode: coordinates, name, display options,
    // default properties

    // first, set the visual properties for the source code
    SourceCodeProperties scProps = new SourceCodeProperties();
    scProps.set(AnimationPropertiesKeys.CONTEXTCOLOR_PROPERTY, Color.BLUE);
    scProps.set(AnimationPropertiesKeys.FONT_PROPERTY,
            new Font("Monospaced", Font.PLAIN, 12));

    scProps.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.RED);
    scProps.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLACK);

    // now, create the source code entity
    SourceCode sc = lang.newSourceCode(new Coordinates(40, 140), "sourceCode",
            null, scProps);

    // Add the lines to the SourceCode object.
    // Line, name, indentation, display dealy
    sc.addCodeLine("public void quickSort(int[] array, int l, int r)", null, 0,
            null); // 0
    sc.addCodeLine("{", null, 0, null);
    sc.addCodeLine("int i, j, pivot;", null, 1, null);
    sc.addCodeLine("if (r>l)", null, 1, null); // 3
    sc.addCodeLine("{", null, 1, null); // 4
    sc.addCodeLine("pivot = array[r];", null, 2, null); // 5
    sc.addCodeLine("for (i = l; j = r - 1; i < j; )", null, 2, null); // 6
    sc.addCodeLine("{", null, 2, null); // 7
    sc.addCodeLine("while (array[i] <= pivot && j > i)", null, 3, null); // 8
    sc.addCodeLine("i++;", null, 4, null); // 9
    sc.addCodeLine("while (pivot < array[j] && j > i)", null, 3, null); // 10
    sc.addCodeLine("j--;", null, 4, null); // 11
    sc.addCodeLine("if (i < j)", null, 3, null); // 12
    sc.addCodeLine("swap(array, i, j);", null, 4, null); // 13
    sc.addCodeLine("}", null, 2, null); // 14
    sc.addCodeLine("if (pivot < array[i])", null, 2, null); // 15
    sc.addCodeLine("swap(array, i, r);", null, 3, null); // 16
    sc.addCodeLine("else", null, 2, null); // 17
    sc.addCodeLine("i=r;", null, 3, null); // 18
    sc.addCodeLine(" quickSort(array, l, i - 1);", null, 2, null); // 19
    sc.addCodeLine(" quickSort(array, i + 1, r);", null, 2, null); // 20
    sc.addCodeLine(" }", null, 1, null); // 21
    sc.addCodeLine("}", null, 0, null); // 22

    lang.nextStep();
    // Highlight all cells
    ia.highlightCell(0, ia.getLength() - 1, null, null);
    try {
      // Start quicksort
      quickSort(ia, sc, 0, (ia.getLength() - 1));
    } catch (LineNotExistsException e) {
      e.printStackTrace();
    }
    sc.hide();
    ia.hide();
    lang.nextStep();

  }

  /**
   * counter for the number of pointers
   *
   */
  private int pointerCounter = 0;

  /**
   * Quicksort: Sort elements using a pivot element between [l, r]
   *
   * @param array
   *          the IntArray to be sorted
   * @param codeSupport
   *          the underlying code instance
   * @param l
   *          the lower border of the subarray to be sorted
   * @param l
   *          the upper border of the subarray to be sorted
   */
  private void quickSort(IntArray array, SourceCode codeSupport, int l, int r)
          throws LineNotExistsException {
    // Highlight first line
    // Line, Column, use context colour?, display options, duration
    codeSupport.highlight(0, 0, false);
    lang.nextStep();

    // Highlight next line
    codeSupport.toggleHighlight(0, 0, false, 2, 0);

    // Create two markers to point on i and j
    pointerCounter++;
    // Array, current index, name, display options, properties
    ArrayMarkerProperties arrayIMProps = new ArrayMarkerProperties();
    arrayIMProps.set(AnimationPropertiesKeys.LABEL_PROPERTY, "i");
    arrayIMProps.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLACK);
    ArrayMarker iMarker = lang.newArrayMarker(array, 0, "i" + pointerCounter,
            null, arrayIMProps);
    pointerCounter++;

    ArrayMarkerProperties arrayJMProps = new ArrayMarkerProperties();
    arrayJMProps.set(AnimationPropertiesKeys.LABEL_PROPERTY, "j");
    arrayJMProps.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLACK);
    ArrayMarker jMarker = lang.newArrayMarker(array, 0, "j" + pointerCounter,
            null, arrayJMProps);

    int i, j;

    lang.nextStep();
    // Highlight next line
    codeSupport.toggleHighlight(2, 0, false, 3, 0);
    // this statement is equivalent to
    // codeSupport.unhighlight(2, 0, false);
    // codeSupport.highlight(3, 0, false);

    // Create a marker for the pivot element
    int pivot;
    pointerCounter++;
    ArrayMarkerProperties arrayPMProps = new ArrayMarkerProperties();
    arrayPMProps.set(AnimationPropertiesKeys.LABEL_PROPERTY, "pivot");
    arrayPMProps.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLUE);

    ArrayMarker pivotMarker = lang.newArrayMarker(array, 0,
            "pivot" + pointerCounter, null, arrayPMProps);

    lang.nextStep();
    codeSupport.unhighlight(3, 0, false);
    if (r > l) {
      lang.nextStep();
      // Highlight next line
      codeSupport.highlight(5, 0, false);

      // Receive the value of the pivot element
      pivot = array.getData(r);
      // Move marker to that position
      pivotMarker.move(r, null, defaultDuration);

      lang.nextStep();
      codeSupport.unhighlight(5, 0, false);
      for (i = l, j = r - 1; i < j;) {
        // Highlight next line
        codeSupport.highlight(6, 0, false);
        // Move the two markers i,j to their proper positions
        iMarker.move(i, null, defaultDuration);
        jMarker.move(j, null, defaultDuration);

        lang.nextStep();
        // Highlight next line
        codeSupport.toggleHighlight(6, 0, false, 8, 0);

        while (array.getData(i) <= pivot && j > i) {
          lang.nextStep();
          i++;

          // Highlight next line
          codeSupport.toggleHighlight(8, 0, false, 9, 0);
          // Move marker i to its next position
          iMarker.move(i, null, defaultDuration);

          lang.nextStep();
          // Highlight next line
          codeSupport.toggleHighlight(9, 0, false, 8, 0);
        }

        lang.nextStep();
        // Highlight next line
        codeSupport.toggleHighlight(8, 0, false, 10, 0);
        while (pivot < array.getData(j) && j > i) {
          lang.nextStep();

          j--;
          // Highlight next line
          codeSupport.toggleHighlight(10, 0, false, 11, 0);

          // Move marker j to its next position
          jMarker.move(j, null, defaultDuration);

          lang.nextStep();
          // Highlight next line
          codeSupport.toggleHighlight(11, 0, false, 10, 0);

        }

        lang.nextStep();
        // Highlight next line
        codeSupport.toggleHighlight(10, 0, false, 12, 0);

        if (i < j) {
          lang.nextStep();
          // Highlight next line
          codeSupport.toggleHighlight(12, 0, false, 13, 0);

          // Swap the array elements at position i and j
          array.swap(i, j, null, defaultDuration);
        }
        lang.nextStep();
        // Highlight next line
        codeSupport.toggleHighlight(13, 0, false, 12, 0);

      } // end for...
      // Highlight next line
      codeSupport.toggleHighlight(6, 0, false, 13, 0);

      lang.nextStep();
      if (pivot < array.getData(i)) {
        // Highlight next line
        codeSupport.toggleHighlight(15, 0, false, 16, 0);

        // Swap the array elements at position i and r
        array.swap(i, r, null, defaultDuration);
        // Set pivot marker to position i
        pivotMarker.move(i, null, defaultDuration);

        lang.nextStep();
        codeSupport.unhighlight(16, 0, false);
      } else {
        i = r;
        // Highlight next line
        codeSupport.toggleHighlight(15, 0, false, 18, 0);
        // Move marker i to position r
        iMarker.move(r, null, defaultDuration);

        lang.nextStep();
        codeSupport.unhighlight(18, 0, false);
      }
      // Highlight the i'th array element
      array.highlightElem(i, null, null);

      lang.nextStep();
      codeSupport.highlight(19, 0, false);

      lang.nextStep();
      codeSupport.unhighlight(19, 0, false);

      // Unhighlight cells from i to r
      // this part is not scheduled...
      array.unhighlightCell(i, r, null, null);
      // Apply quicksort to the left array part
      iMarker.hide();
      jMarker.hide();
      pivotMarker.hide();
      quickSort(array, codeSupport, l, i - 1);
      iMarker.show();
      jMarker.show();
      pivotMarker.show();

      // Left recursion finished.
      lang.nextStep();
      // Highlight cells l to r
      array.highlightCell(l, r, null, null);
      codeSupport.highlight(20, 0, false);

      lang.nextStep();
      codeSupport.unhighlight(20, 0, false);
      // Unhighlight cells l to i
      array.unhighlightCell(l, i, null, null);
      // Apply quicksort to the right array part
      iMarker.hide();
      jMarker.hide();
      pivotMarker.hide();
      quickSort(array, codeSupport, i + 1, r);
      iMarker.show();
      jMarker.show();
      pivotMarker.show();
    }
    lang.nextStep();
    // Highlight next line
    codeSupport.highlight(21, 0, false);
    lang.nextStep();
    // Highlight next line
    codeSupport.highlight(22, 0, false);

    lang.nextStep();
    // Unhighlight cells from l to r
    array.unhighlightCell(l, r, null, null);
    lang.nextStep();
    iMarker.hide();
    jMarker.hide();
    pivotMarker.hide();
  }

  protected String getAlgorithmDescription() {
    return DESCRIPTION;
  }

  protected String getAlgorithmCode() {
    return SOURCE_CODE;
  }

  public String getName() {
    return "Quicksort (pivot=last)";
  }

  public String getDescription() {
    return DESCRIPTION;
  }

  public String getCodeExample() {
    return SOURCE_CODE;
  }

  public static void main(String[] args) {
    // Create a new language object for generating animation code
    // this requires type, name, author, screen width, screen height
    Language l = Language.getLanguageInstance(AnimationType.ANIMALSCRIPT,
            "Multi Level Queue", "Andre Challier, Christian Richter", 640, 480);
    MultiLevelQueueGenerator s = new MultiLevelQueueGenerator(l);
    int[] a = { 7, 3, 2, 4, 1, 13, 52, 13, 5, 1 };
    s.sort(a);
    System.out.println(l);
  }
}
