/*
 * FindGSetGenerator.java
 * Valentin Kuhn, 2016 for the Animal project at TU Darmstadt.
 * Copying this file for educational purposes is permitted without further authorization.
 */
package generators.misc;

import algoanim.animalscript.AnimalScript;
import algoanim.exceptions.LineNotExistsException;
import algoanim.primitives.SourceCode;
import algoanim.primitives.StringMatrix;
import algoanim.primitives.Text;
import algoanim.primitives.generators.Language;
import algoanim.properties.MatrixProperties;
import algoanim.properties.SourceCodeProperties;
import algoanim.util.Coordinates;
import algoanim.util.Timing;
import generators.framework.Generator;
import generators.framework.GeneratorType;
import generators.framework.ValidatingGenerator;
import generators.framework.properties.AnimationPropertiesContainer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

public class FindGSetGenerator implements ValidatingGenerator {
    private static final String DESCRIPTION = "FindG-Set finds the most general hypotheses that are correct on the data.\n\n"
            + "FindG-Set starts with the most general hypothesis that covers all examples.\n"
            + "This hypothesis is iteratively refined to more specific ones where each more\n"
            + "specific hypothesis only differentiates in one condition from the previous more\n"
            + "general hypothesis. Unlike the Find-G algorithm, FindG-Set does not select one\n"
            + "of the more specific hypotheses in each refinement step but instead keeps all of\n"
            + "them that are correct on the data. This approach results in a bias towards general\n"
            + "hypotheses.\n\n"
            + "The counterpart of FindG-Set is FindS-Set, starting with the most specific hypothesis\n"
            + "and generalizing it in each step, resulting in a bias towards more specific hypotheses.";
    private static final String SOURCE_CODE = "I.   h = most general hypothesis in H (covering all examples)\n"
            + "II.  G = { h }\n"
            + "III. for each training example e\n"
            + "     a) if e is positive\n"
            + "           remove all h ? G that do not cover e\n"
            + "     b) if e is negative\n"
            + "           for all hypotheses h ? G that cover e\n"
            + "              G = G ? { h }\n"
            + "              for every condition c in e that is not part of h\n"
            + "                 for all conditions c' that negate c\n"
            + "                    h' = h ? { c' }\n"
            + "                    if h' covers all previous positive examples\n"
            + "                       G = G ? { h' }\n"
            + "IV.  return G";
    private static final String AUTHOR = "Valentin Kuhn";
    private static final String FILE_EXTENSION = "asu";
    private static final String ALGO_NAME = "FindG-Set";
    private static final String GENERATOR_NAME = "FindG-Set";
    private Language lang;
    private MatrixProperties matrixProps;
    private int classIndex;
    private String posClass;
    private String negClass;
    private String[][] trainingSet;
    private SourceCodeProperties sourceCodeProps;

    public void init() {
        lang = new AnimalScript(ALGO_NAME, AUTHOR, 1000, 800);
        lang.setStepMode(true);
    }

    public String generate(AnimationPropertiesContainer props, Hashtable<String, Object> primitives) {
        // Properties
        matrixProps = (MatrixProperties) props.getPropertiesByName("matrixProps");
        sourceCodeProps = (SourceCodeProperties) props.getPropertiesByName("sourceCodeProps");

        // Primitives
        trainingSet = (String[][]) primitives.get("trainingSet");
        classIndex = (Integer) primitives.get("classIndex");
        posClass = (String) primitives.get("posClass");
        negClass = (String) primitives.get("negClass");

        // Show the training set to the user
        StringMatrix sm = lang.newStringMatrix(new Coordinates(20, 300), this.trainingSet, "trainingSet", null, matrixProps);

        // Show the pseudo source code to the user
        SourceCode sc = lang.newSourceCode(new Coordinates(20, 20), "sourceCode", null, sourceCodeProps);
        sc.addCodeLine("I.   h = most general hypothesis in H (covering all examples)", null, 0, null); // 00
        sc.addCodeLine("II.  G = { h }", null, 0, null);                                                // 01
        sc.addCodeLine("III. for each training example e", null, 0, null);                              // 02
        sc.addCodeLine("a) if e is positive", null, 1, null);                                           // 03
        sc.addCodeLine("remove all h ∈ G that do not cover e", null, 2, null);                         // 04
        sc.addCodeLine("b) if e is negative", null, 1, null);                                           // 05
        sc.addCodeLine("for all hypotheses h ∈ G that cover e", null, 2, null);                        // 06
        sc.addCodeLine("G = G ∖ { h }", null, 3, null);                                                // 07
        sc.addCodeLine("for every condition c in e that is not part of h", null, 3, null);              // 08
        sc.addCodeLine("for all conditions c' that negate c", null, 4, null);                           // 09
        sc.addCodeLine("h' = h ∪ { c' }", null, 5, null);                                              // 10
        sc.addCodeLine("if h' covers all previous positive examples", null, 5, null);                   // 11
        sc.addCodeLine("G = G ∪ { h' }", null, 6, null);                                               // 12
        sc.addCodeLine("IV.  return G", null, 0, null);                                                 // 13

        // Show description / explanation to the user --> to be updated in each step
        Text ex = lang.newText(new Coordinates(550, 30), DESCRIPTION.replaceAll("\n", " "), "explanation", null);

        // Show the learned set to the user
        SourceCode ls = lang.newSourceCode(new Coordinates(550, 285), "learnedSet", null, sourceCodeProps);

        lang.nextStep();

        // Highlight all cells
        //sm.highlightCell(0, sm.getLength() - 1, null, null);

        try {
            findGSet(sm, sc, ex, ls, classIndex, posClass, negClass);
        } catch (LineNotExistsException e) {
            e.printStackTrace();
        }
        lang.nextStep();

        // AnimalScript output
        return lang.toString();
    }

    /**
     * runs the FindG-Set algorithm
     *
     * @param sm         training data (each row represents one example)
     * @param sc         pseudo code
     * @param ex         explanation
     * @param ls         learned sets
     * @param classIndex column index in sm describing the class of each example
     * @param posClass   positive class (in column at classIndex)
     * @param negClass   negative class (in column at classIndex)
     */
    private void findGSet(StringMatrix sm, SourceCode sc, Text ex, SourceCode ls, int classIndex, String posClass, String negClass) {
        int stepDelay = Timing.MEDIUM.getDelay();
        Timing instanteous = Timing.INSTANTEOUS;

        List<List<String[]>> G = new ArrayList<>();
        int currentGIndex = 0;

        // h0 is the most general hypothesis
        sc.highlight(0);
        ArrayList<String[]> G0 = new ArrayList<>();
        String[] h0 = new String[sm.getNrCols()];
        G0.add(h0);
        ex.setText(String.format("The most general hypothesis is %s.", hToString(h0)), instanteous, instanteous);
        lang.nextStep(stepDelay);

        // G starts with most general hypothesis: G0 = {h0}
        sc.unhighlight(0);
        sc.highlight(1);
        G.add(G0);
        String gString = gToString(ls, G, currentGIndex);
        ls.addCodeLine(gString, "G" + currentGIndex, 0, instanteous);
        ex.setText(
                String.format("At first the G-Set contains only the most general hypothesis, thus %s.", gString),
                instanteous, instanteous);
        lang.nextStep(stepDelay);

        // for each example e
        sc.unhighlight(1);
        sc.highlight(2);
        ex.setText("For each training example, the set of hypotheses is refined to only cover all positive examples. " +
                "In order to achieve this, hypotheses covering negative examples ar specialized in the least " +
                "possible way while still covering all previously learned positive examples.", instanteous, instanteous);
        lang.nextStep(stepDelay);
        for (int i = 0; i < sm.getNrRows(); i++) {
            currentGIndex++;
            String[] e = new String[sm.getNrCols()];
            for (int j = 0; j < sm.getNrCols(); j++) {
                e[j] = sm.getElement(i, j);
            }
            sm.highlightCellColumnRange(i, 0, sm.getNrCols() - 1, instanteous, instanteous);
            lang.nextStep(stepDelay);

            if (e[classIndex].equals(posClass)) {
                // if e is positive
                sc.highlight(3);
                sm.setGridFillColor(i, classIndex, Color.GREEN, instanteous, instanteous);
                ex.setText("The next example to be learned is positive.", instanteous, instanteous);
                lang.nextStep(stepDelay);

                // remove all h in G that do not cover e
                sc.highlight(4);
                ls.highlight(currentGIndex - 1);
                ex.setText("Since the example is positive, all hypothesis in G have to cover it. " +
                                "All hypothesis in G not covering the example are removed.",
                        instanteous, instanteous);
                lang.nextStep(stepDelay);
                List<String[]> newG = new ArrayList<>();
                hLoop:
                for (String[] h : G.get(currentGIndex - 1)) {
                    for (int colIndex = 0; colIndex < sm.getNrCols(); colIndex++) {
                        if ((colIndex < classIndex && h[colIndex] != null && !h[colIndex].equals(e[colIndex]))
                                || (colIndex > classIndex && h[colIndex - 1] != null && !h[colIndex - 1].equals(e[colIndex]))) {
                            // h does not cover positive e --> do not add it to the next G set
                            ex.setText(
                                    String.format("%s does not cover the positive example. It will be removed from G.", hToString(h)),
                                    instanteous, instanteous);
                            lang.nextStep(stepDelay);
                            continue hLoop;
                        }
                    }
                    // h covers positive e --> add it to the next G set
                    ex.setText(String.format("%s covers the positive example. It will be kept in G.", hToString(h)),
                            instanteous, instanteous);
                    lang.nextStep(stepDelay);
                    newG.add(h);
                }
                G.add(newG);
                sc.unhighlight(3);
                sc.unhighlight(4);
                ls.unhighlight(currentGIndex - 1);
                lang.nextStep(stepDelay);
            } else if (e[classIndex].equals(negClass)) {
                // if e is negative
                sc.highlight(5);
                sm.setGridFillColor(i, classIndex, Color.RED, instanteous, instanteous);
                ex.setText("The next example to be learned is negative.", instanteous, instanteous);
                lang.nextStep(stepDelay);

                // specialize all h in G that cover e
                sc.highlight(6);
                ls.highlight(currentGIndex - 1);
                ex.setText("Since the example is negative, all hypothesis in G must not cover it. " +
                                "All hypothesis in G covering the example are refined.",
                        instanteous, instanteous);
                lang.nextStep(stepDelay);
                List<String[]> newG = new ArrayList<>();
                for (String[] h : G.get(currentGIndex - 1)) {
                    if (!covers(h, e)) {
                        // h does not cover negative e --> add it to the next G set and continue with next h
                        ex.setText(String.format("%s does not cover the negative example. It will be kept in G.", hToString(h)),
                                instanteous, instanteous);
                        lang.nextStep(stepDelay);
                        newG.add(h);
                        continue;
                    }
                    // h covers negative e --> specialize it
                    sc.highlight(7);
                    sc.highlight(8);
                    ex.setText(String.format("%s covers the negative example. It will be refined.", hToString(h)),
                            instanteous, instanteous);
                    lang.nextStep(stepDelay);
                    for (int colIndex = 0; colIndex < sm.getNrCols(); colIndex++) {
                        // for every c in e that is not in h
                        if ((colIndex < classIndex && h[colIndex] == null)
                                || (colIndex > classIndex && h[colIndex - 1] == null)) {
                            ex.setText("For every condition in the example that is not already set in the hypothesis " +
                                            "a new hypothesis is created with that condition.",
                                    instanteous, instanteous);
                            lang.nextStep(stepDelay);

                            String c = e[colIndex];
                            sm.setGridFillColor(i, colIndex, Color.BLUE, instanteous, instanteous);
                            ex.setText(String.format("Condition c='%s'.", c),
                                    instanteous, instanteous);
                            lang.nextStep();

                            // for all cNew that negate c
                            sc.highlight(9);
                            ex.setText(String.format("All conditions c' that negate c='%s' will be used to specialize the hypothesis h='%s'.", c, hToString(h)),
                                    instanteous, instanteous);
                            lang.nextStep(stepDelay);

                            List<String> seenC = new ArrayList<>();
                            seenC.add(c);
                            cLoop:
                            for (int rowIndex = 0; rowIndex < sm.getNrRows(); rowIndex++) {
                                String cNew = sm.getElement(rowIndex, colIndex);
                                if (!seenC.contains(cNew)) {
                                    seenC.add(cNew);
                                    // create specialized hNew
                                    sc.highlight(10);
                                    String[] hNew = h.clone();
                                    hNew[colIndex < classIndex ? colIndex : colIndex - 1] = cNew;
                                    ex.setText(String.format("c'='%s' specializes h='%s' to h'='%s'.", cNew, hToString(h), hToString(hNew)),
                                            instanteous, instanteous);
                                    lang.nextStep(stepDelay);

                                    // if hNew covers all previous positive examples
                                    sc.highlight(11);
                                    lang.nextStep(stepDelay);
                                    for (int k = 0; k < i; k++) {
                                        if (sm.getElement(k, classIndex).equals(posClass)) {
                                            if (!covers(hNew, rowToArray(sm, k))) {
                                                // hNew does not cover a previous positive example
                                                sc.unhighlight(10);
                                                sc.unhighlight(11);
                                                continue cLoop;
                                            }
                                        }
                                    }

                                    // add h to G
                                    sc.highlight(12);
                                    ex.setText(String.format("h'='%s' covers all previous positive examples, thus h' stays in G.", hToString(hNew)),
                                            instanteous, instanteous);
                                    lang.nextStep(stepDelay);
                                    newG.add(hNew);

                                    sc.unhighlight(10);
                                    sc.unhighlight(11);
                                    sc.unhighlight(12);
                                    lang.nextStep(stepDelay);
                                }
                            }
                            sc.unhighlight(9);
                            sm.setGridFillColor(i, colIndex, (Color) sm.getProperties().get("fillColor"), instanteous, instanteous);
                            // gotta re-set some values, since animal always puts the last one on top
                            sm.unhighlightCellColumnRange(i, 0, sm.getNrCols() - 1, instanteous, instanteous);
                            sm.highlightCellColumnRange(i, 0, sm.getNrCols() - 1, instanteous, instanteous);
                            sm.setGridFillColor(i, classIndex, Color.RED, instanteous, instanteous);
                            // done re-setting all the things, let's show this
                            lang.nextStep(stepDelay);
                        }
                    }
                    sc.unhighlight(7);
                    sc.unhighlight(8);
                    ls.unhighlight(currentGIndex - 1);
                    lang.nextStep(stepDelay);
                }
                G.add(newG);
                sc.unhighlight(5);
                sc.unhighlight(6);
                lang.nextStep(stepDelay);
            } else {
                sm.setGridFillColor(i, classIndex, Color.BLUE, instanteous, instanteous);
                String textUnknownClass = String.format("Found unknown class %s at (%d,%d)!", e[classIndex], i, classIndex);
                ex.setText(textUnknownClass, instanteous, instanteous);
                lang.nextStep(stepDelay);
                throw new IllegalArgumentException(textUnknownClass);
            }

            ls.addCodeLine(gToString(ls, G, currentGIndex), "G" + currentGIndex, 0, instanteous);
            ls.highlight("G" + currentGIndex);
            ex.setText(String.format("G%d contains all hypotheses learned on G%d and e='%s'.", currentGIndex, currentGIndex - 1, hToString(e)),
                    instanteous, instanteous);
            lang.nextStep(stepDelay);
            sm.unhighlightCellColumnRange(i, 0, sm.getNrCols() - 1, instanteous, instanteous);
            ls.unhighlight("G" + currentGIndex);
        }
        sc.unhighlight(2);
        lang.nextStep(stepDelay);

        // end
        sc.highlight(13);
        ex.setText(
                String.format("G%d is the learned G-Set containing all most general hypotheses that are correct on the training set.",
                        currentGIndex), instanteous, instanteous);
        lang.nextStep();

        sc.unhighlight(13);
        lang.nextStep(stepDelay);
    }

    private boolean covers(String[] h, String[] e) {
        for (int col = 0; col < e.length; col++) {
            if ((col < classIndex && h[col] != null && !h[col].equals(e[col]))
                    || (col > classIndex && h[col - 1] != null && !h[col - 1].equals(e[col]))) {
                return false;
            }
        }
        return true;
    }

    private String[] rowToArray(StringMatrix sm, int row) {
        String[] array = new String[sm.getNrCols()];
        for (int col = 0; col < array.length; col++) {
            array[col] = sm.getElement(row, col);
        }
        return array;
    }

    private String gToString(SourceCode ls, List<List<String[]>> G, int currentGIndex) {
        StringBuilder hBuilder = new StringBuilder();
        hBuilder.append("G");
        hBuilder.append(currentGIndex);
        hBuilder.append(": {");
        for (int i = 0; i < G.get(currentGIndex).size(); i++) {
            String[] h = G.get(currentGIndex).get(i);
            hBuilder.append(" ");
            hBuilder.append(hToString(h));
            hBuilder.append(" ");
            if (i < (G.get(currentGIndex).size() - 1)) {
                hBuilder.append(",");
            }
        }
        hBuilder.append("}");
        return hBuilder.toString();
    }

    private String hToString(String[] h) {
        StringBuilder hBuilder = new StringBuilder();
        hBuilder.append("<");
        for (int j = 1; j <= (h.length - 1); j++) {
            hBuilder.append(h[j - 1] == null ? "?" : h[j - 1]);
            if (j < (h.length - 1)) {
                hBuilder.append(",");
            }
        }
        hBuilder.append(">");
        return hBuilder.toString();
    }

    public String getName() {
        return GENERATOR_NAME;
    }

    public String getAlgorithmName() {
        return ALGO_NAME;
    }

    public String getAnimationAuthor() {
        return AUTHOR;
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public String getCodeExample() {
        return SOURCE_CODE;
    }

    public String getFileExtension() {
        return FILE_EXTENSION;
    }

    public Locale getContentLocale() {
        return Locale.ENGLISH;
    }

    public GeneratorType getGeneratorType() {
        return new GeneratorType(GeneratorType.GENERATOR_TYPE_MORE);
    }

    public String getOutputLanguage() {
        return Generator.PSEUDO_CODE_OUTPUT;
    }

    @Override
    public boolean validateInput(AnimationPropertiesContainer props, Hashtable<String, Object> primitives)
            throws IllegalArgumentException {
        trainingSet = (String[][]) primitives.get("trainingSet");
        classIndex = (Integer) primitives.get("classIndex");
        posClass = (String) primitives.get("posClass");
        negClass = (String) primitives.get("negClass");
        if (posClass.equals(negClass)) {
            throw new IllegalArgumentException("The positive and negative class may not be equal!");
        }
        if (trainingSet.length == 0 || trainingSet[0].length == 0) {
            throw new IllegalArgumentException("Cannot learn on empty training set!");
        }
        if (classIndex >= trainingSet[0].length) {
            throw new IllegalArgumentException(
                    String.format("Class index (%d) may not be out of bounds (%d)!", classIndex, trainingSet[0].length));
        }
        for (int row = 0; row < trainingSet.length; row++) {
            String[] e = trainingSet[row];
            if (!(e[classIndex].equals(posClass) || e[classIndex].equals(negClass))) {
                throw new IllegalArgumentException(
                        String.format("Found unknown class %s at (%d,%d)!", e[classIndex], row, classIndex));
            }
        }
        return true;
    }
}