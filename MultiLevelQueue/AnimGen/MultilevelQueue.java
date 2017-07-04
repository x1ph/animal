/*
 * MultilevelQueue.java
 * Andre Challier <andre.challier@stud.tu-darmstadt.de>, Christian Richter <chrisrichter145@gmail.com>, 2017 for the Animal project at TU Darmstadt.
 * Copying this file for educational purposes is permitted without further authorization.
 */
package generators.misc;

import generators.framework.Generator;
import generators.framework.GeneratorType;
import java.util.Locale;
import algoanim.primitives.generators.Language;
import java.util.Hashtable;
import generators.framework.properties.AnimationPropertiesContainer;
import algoanim.animalscript.AnimalScript;
import java.awt.Color;
import algoanim.properties.ArrayProperties;
import algoanim.properties.MatrixProperties;

public class MultilevelQueue implements Generator {
    private Language lang;
    private Color color;
    private ArrayProperties array;
    private String[] stringArray;
    private String[][] stringMatrix;
    private MatrixProperties matrix;

    public void init(){
        lang = new AnimalScript("Animation of Multilevel Queue", "Andre Challier <andre.challier@stud.tu-darmstadt.de>, Christian Richter <chrisrichter145@gmail.com>", 800, 600);
    }

    public String generate(AnimationPropertiesContainer props,Hashtable<String, Object> primitives) {
        color = (Color)primitives.get("color");
        array = (ArrayProperties)props.getPropertiesByName("array");
        stringArray = (String[])primitives.get("stringArray");
        stringMatrix = (String[][])primitives.get("stringMatrix");
        matrix = (MatrixProperties)props.getPropertiesByName("matrix");
        
        return lang.toString();
    }

    public String getName() {
        return "Animation of Multilevel Queue";
    }

    public String getAlgorithmName() {
        return "Multilevel Queue";
    }

    public String getAnimationAuthor() {
        return "Andre Challier <andre.challier@stud.tu-darmstadt.de>, Christian Richter <chrisrichter145@gmail.com>";
    }

    public String getDescription(){
        return "A Multi Level Queue for scheduling uses a predefined number of levels to"
 +"\n"
 +"schedule processes. Processes get assigned to a particular level at insert."
 +"\n"
 +"The processes in queues of higher level will then be executed first, lower level"
 +"\n"
 +"queues will be executed when all higher level queues are empty. Each queue is"
 +"\n"
 +"free to use its own scheduling, thus adding greater flexibility then merely"
 +"\n"
 +"having multiple levels in a queue."
 +"\n"
 +"\n"
 +"In this scenario each process has a tima of arrival (process.time), and a number"
 +"\n"
 +"of execution timeslides (process.work). The algorithm loops until all processes"
 +"\n"
 +"are done. To schedule the processes the algorithm first adds all arriving"
 +"\n"
 +"processes to the queues, then picks the non-empty queue with the highest level"
 +"\n"
 +"and executes the upcoming process."
 +"\n"
 +"\n"
 +"The first queue in this example uses First-Come-First-Serve-Scheduling while the"
 +"\n"
 +"second queue uses Round-Robin-Scheduling.";
    }

    public String getCodeExample(){
        return "WHILE sum(proc.work) != 0"
 +"\n"
 +"	FOR process IN procList"
 +"\n"
 +"		IF process.time == time"
 +"\n"
 +"			queue = queueList[process.level]"
 +"\n"
 +"			queue.add(process)"
 +"\n"
 +"	FOR i FROM 0 TO (queueList.size - 1)"
 +"\n"
 +"		IF queueList[i].current() != null"
 +"\n"
 +"			queue = queueList[i]"
 +"\n"
 +"			BREAK"
 +"\n"
 +"		IF queue == null"
 +"\n"
 +"			time++"
 +"\n"
 +"			CONTINUE"
 +"\n"
 +"		run(queue.current())"
 +"\n"
 +"		IF queue.current().work == 0"
 +"\n"
 +"			queue.removeCurrent()"
 +"\n"
 +"		ELSE"
 +"\n"
 +"			if(queue.useRoundRobin)"
 +"\n"
 +"				temp = queue.current()"
 +"\n"
 +"				queue.removeCurrent()"
 +"\n"
 +"				queue.add(temp)";
    }

    public String getFileExtension(){
        return "asu";
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

}