import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import algoanim.exceptions.LineNotExistsException;
import algoanim.primitives.ArrayMarker;
import algoanim.primitives.IntArray;
import algoanim.primitives.SourceCode;
import algoanim.primitives.StringArray;
import algoanim.primitives.StringMatrix;
import algoanim.primitives.Text;
import algoanim.primitives.generators.AnimationType;
import algoanim.primitives.generators.Language;
import algoanim.properties.*;
import algoanim.util.Coordinates;
import algoanim.util.Node;
import algoanim.util.Offset;
import algoanim.util.TicksTiming;
import algoanim.util.Timing;
import extras.lifecycle.common.AnimationStepBean;
import generators.graphics.antialias.Grid;
import generators.maths.grid.GridProperty;

/**
 * @author Andre Challier <andre.challier@stud.tu-darmstadt.de>, Christian
 *         Richter <chrisrichter145@gmail.com>
 * @version 0.1
 */
public class MultiLevelQueueGenerator {

	/**
	 * The concrete language object used for creating output
	 */
	private Language lang;

	/**
	 * The List of queues to schedule.
	 * 
	 * queues.get(0) is list with highest priority, queues.get(1) is the next
	 * highest and so on.
	 */
	private List<Queue> queues;
	/**
	 * The list of incoming processes that has to be scheduled.
	 */
	private List<Process> inc_procs;
	
	/**
	 * The current timeslice of schedulting
	 */
	private int currentTime;
	
	/**
	 * The animal primitive showing the current timeslice of scheduling
	 */
	private Text currentTimeText;

	/**
	 * Default constructor
	 *
	 * @param l
	 *            the conrete language object used for creating output
	 */
	public MultiLevelQueueGenerator(List<Queue> queues, List<Process> inc_procs, Language l) {

		this.queues = queues;
		this.inc_procs = inc_procs;

		// Store the language object
		lang = l;
		// This initializes the step mode. Each pair of subsequent steps has to
		// be divdided by a call of lang.nextStep();
		lang.setStepMode(true);
	}

	private static final String DESCRIPTION = "A Multi Level Queue for scheduling uses a predefined number of levels to"
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
			+ "second queue uses Round-Robin-Scheduling.";

	private static final String SOURCE_CODE = "WHILE sum(proc.work) != 0" + "\n   FOR process IN procList"
			+ "\n     IF process.time == step" + "\n       queue = queueList[process.level]"
			+ "\n       queue.add(process)" + "\n   FOR i FROM 0 TO (queueList.size - 1)"
			+ "\n     IF queueList[i].current() != null" + "\n       queue = queueList[i]" + "\n       BREAK"
			+ "\n   IF queue == null" + "\n     CONTINUE" + "\n   run(queue.current())"
			+ "\n   IF queue.current().work == 0" + "\n     queue.removeCurrent();" + "\n   ELSE"
			+ "\n     if(queue.useRoundRobin)" + "\n       temp = queue.current()" + "\n       queue.removeCurrent()"
			+ "\n       queue.add(temp)";

	/**
	 * default duration for swap processes
	 */
	public final static Timing defaultDuration = new TicksTiming(30);

	/**
	 * Schedule the Processes and Queues of this instance of MultiLevelQueue
	 * (see main())
	 */
	public void schedule() {
		
		// Create Data Array for Matrix Representation
		String[][] procMatrix = new String[inc_procs.size() + 1][4];
		procMatrix[0][0] = "ID";
		procMatrix[0][1] = "QUEUE";
		procMatrix[0][2] = "WORK";
		procMatrix[0][3] = "TIME";
		for (int i = 0; i < inc_procs.size(); i++) {
			procMatrix[i + 1][0] = inc_procs.get(i).name;
			procMatrix[i + 1][1] = inc_procs.get(i).queue.name;
			procMatrix[i + 1][2] = Integer.toString(inc_procs.get(i).computingTime);
			procMatrix[i + 1][3] = Integer.toString(inc_procs.get(i).arrival);
		}

	    // first, set the visual properties (somewhat similar to CSS)
		MatrixProperties matrixProps = new MatrixProperties();
		matrixProps.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLACK);
		matrixProps.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.WHITE);
		matrixProps.set(AnimationPropertiesKeys.FILLED_PROPERTY, Boolean.TRUE);
		matrixProps.set(AnimationPropertiesKeys.ELEMENTCOLOR_PROPERTY, Color.BLACK);
		matrixProps.set(AnimationPropertiesKeys.ELEMHIGHLIGHT_PROPERTY, Color.RED);
		matrixProps.set(AnimationPropertiesKeys.CELLHIGHLIGHT_PROPERTY,
	        Color.YELLOW);
		matrixProps.set(AnimationPropertiesKeys.GRID_STYLE_PROPERTY, "table");

	    // now, create the IntArray object, linked to the properties
	    StringMatrix sm = lang.newStringMatrix(new Coordinates(400, 10), procMatrix, "inc_proc", null, matrixProps);
	    
	    
	    StringMatrix[] arrays = new StringMatrix[queues.size()];
	   
	    
	    for(int i = 0; i < queues.size(); i++) {
	    	String[][] queueStat = new String[1][inc_procs.size() + 1];
	    	queueStat[0][0] = new String(queues.get(i).name);
	    	
	    	for(int j = 1; j < inc_procs.size() + 1; j++) {
	    		queueStat[0][j] = new String(" ");
	    	}
	    	arrays[i] = lang.newStringMatrix(new Coordinates(400, 50 + inc_procs.size() * 30 + i * 50), queueStat, "queue_" + i, null, matrixProps);
	    }
		
		// first, set the visual properties for the source code
		SourceCodeProperties scProps = new SourceCodeProperties();
		scProps.set(AnimationPropertiesKeys.CONTEXTCOLOR_PROPERTY, Color.BLUE);
		scProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font("Monospaced", Font.PLAIN, 12));

		scProps.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.RED);
		scProps.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLACK);

		// now, create the source code entity
		SourceCode sc = lang.newSourceCode(new Coordinates(10, 10), "sourceCode", null, scProps);

		sc.addCodeLine("WHILE sum(proc.work) != 0", null, 0, null);
		sc.addCodeLine("FOR process IN procList", null, 1, null);
		sc.addCodeLine("IF process.time == time", null, 2, null);
		sc.addCodeLine("queue = queueList[process.level]", null, 3, null);
		sc.addCodeLine("queue.add(process)", null, 3, null);
		sc.addCodeLine("FOR i FROM 0 TO (queueList.size - 1)", null, 0, null);
		sc.addCodeLine("IF queueList[i].current() != null", null, 1, null);
		sc.addCodeLine("queue = queueList[i]", null, 2, null);
		sc.addCodeLine("BREAK", null, 2, null);
		sc.addCodeLine("IF queue == null", null, 0, null);
		sc.addCodeLine("time++", null, 1, null);
		sc.addCodeLine("CONTINUE", null, 1, null);
		sc.addCodeLine("run(queue.current())", null, 0, null);
		sc.addCodeLine("IF queue.current().work == 0", null, 0, null);
		sc.addCodeLine("queue.removeCurrent();", null, 1, null);
		sc.addCodeLine("ELSE", null, 0, null);
		sc.addCodeLine("if(queue.useRoundRobin)", null, 1, null);
		sc.addCodeLine("temp = queue.current()", null, 2, null);
		sc.addCodeLine("queue.removeCurrent()", null, 2, null);
		sc.addCodeLine("queue.add(temp)", null, 2, null);
		
		currentTimeText = lang.newText(new Coordinates(600, 10), "Current Time: 0", "curr_time", null);
		setCurrentTime(0);
		
		lang.nextStep();
		
		

		while (!inc_procs.isEmpty() && sumOfWork() != 0) {

			// enqueue arriving processes
			for (Process p : inc_procs) {
				if (p.arrival == currentTime) {
					addToQueue(p.queue, p);
				}
			}

			// schedule Process of queue with highest priority
			Queue currentQueue = null;
			for (int i = 0; i < queues.size(); i++) {
				currentQueue = queues.get(i);
				if (currentQueue.procs.size() > 0) {
					break;
				}
				currentQueue = null;
			}
			if (currentQueue == null) {
				// Zur zeit keine Arbeit in den Queues
			} else {
				Process currentProc = currentQueue.procs.get(0);
				currentProc.computingTime--;
				if (currentProc.computingTime == 0) {
					currentQueue.procs.remove(0);
				} else {

				}
			}
			setCurrentTime(currentTime + 1);;
		}
	}
	
	private void setCurrentTime(int time){
		currentTime = time;
		currentTimeText.setText("Current Time: " + time, null, defaultDuration);
	}

	public void addToQueue(Queue q, Process p) {
		q.procs.add(p);
	}

	public void removeFromQueue(Process p) {
		p.queue.procs.remove(0);

	}

	public void reschedule(Process p) {
		Queue q = p.queue;
		if (q.useRoundRobin) {
			q.procs.remove(0);
			q.procs.add(p);
		} else {
			// Bei FCFS keine aktion nötig.
		}
	}

	public int sumOfWork() {
		int sum = 0;
		for (Queue q : queues) {
			for (Process p : q.procs) {
				sum += p.computingTime;
			}
		}
		return sum;
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
		Language l = Language.getLanguageInstance(AnimationType.ANIMALSCRIPT, "Multi Level Queue",
				"Andre Challier, Christian Richter", 640, 480);

		LinkedList<Process> inc_procs = new LinkedList<Process>();
		LinkedList<Queue> queues = new LinkedList<Queue>();

		Queue level0 = new Queue("Level 0", false);
		Queue level1 = new Queue("Level 1", true);

		queues.add(level0);
		queues.add(level1);

		Process proc1 = new Process("A", level1, 3, 0);
		Process proc2 = new Process("B", level0, 1, 1);
		Process proc3 = new Process("C", level1, 1, 2);

		inc_procs.add(proc1);
		inc_procs.add(proc2);
		inc_procs.add(proc3);

		MultiLevelQueueGenerator mlqg = new MultiLevelQueueGenerator(queues, inc_procs, l);
		mlqg.schedule();
		String out = l.toString();
		System.out.println(out);
		try {
			FileWriter fw = new FileWriter(new File("MultiLevelQueue.asu"), false);
			fw.write(out);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
