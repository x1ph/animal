/*
 * test.java
 * Andre Challier <andre.challier@stud.tu-darmstadt.de>, Christian Richter <chrisrichter145@gmail.com>, 2017 for the Animal project at TU Darmstadt.
 * Copying this file for educational purposes is permitted without further authorization.
 */
package generators.sorting;

import generators.framework.Generator;
import generators.framework.GeneratorType;
import java.util.Locale;
import algoanim.primitives.generators.Language;
import java.util.Hashtable;
import generators.framework.properties.AnimationPropertiesContainer;
import algoanim.animalscript.AnimalScript;
import algoanim.properties.CircleProperties;

public class test implements Generator {
    private Language lang;

    public void init(){
        lang = new AnimalScript("Local Outlier Factor", "Andre Challier <andre.challier@stud.tu-darmstadt.de>, Christian Richter <chrisrichter145@gmail.com>", 800, 600);
    }

    public String generate(AnimationPropertiesContainer props,Hashtable<String, Object> primitives) {
        CircleProperties circle = (CircleProperties)props.getPropertiesByName("circle");
        
        return lang.toString();
    }

    public String getName() {
        return "Local Outlier Factor";
    }

    public String getAlgorithmName() {
        return "Local Outlier Factor";
    }

    public String getAnimationAuthor() {
        return "Andre Challier <andre.challier@stud.tu-darmstadt.de>, Christian Richter <chrisrichter145@gmail.com>";
    }

    public String getDescription(){
        return "A Description";
    }

    public String getCodeExample(){
        return "FOO()"
 +"\n"
 +"BAR()";
    }

    public String getFileExtension(){
        return "asu";
    }

    public Locale getContentLocale() {
        return Locale.ENGLISH;
    }

    public GeneratorType getGeneratorType() {
        return new GeneratorType(GeneratorType.GENERATOR_TYPE_SORT);
    }

    public String getOutputLanguage() {
        return Generator.JAVA_OUTPUT;
    }

}