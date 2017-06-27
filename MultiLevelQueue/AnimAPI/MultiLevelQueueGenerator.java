import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import algoanim.exceptions.LineNotExistsException;
import algoanim.primitives.*;
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
	 * The Array holding all displayed queues
	 */
	StringMatrix[] arrays;
	
	/**
	 * The Matrix showing all incoming Processes
	 */
	StringMatrix sm;

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
		Text title = lang.newText(new Coordinates(30,30), "Animation of Multilevel Queue", "desc", null);


		Text desc1 =lang.newText(new Coordinates(30,70), "A Multi Level Queue for scheduling uses a predefined number of levels to", "desc", null );
        Text desc2 =lang.newText(new Coordinates(30,90), "schedule processes. Processes get assigned to a particular level at insert.", "desc", null );
        Text desc3 =lang.newText(new Coordinates(30,110), "The processes in queues of higher level will then be executed first, lower level", "desc", null );
        Text desc4 =lang.newText(new Coordinates(30,130), "queues will be executed when all higher level queues are empty. Each queue is", "desc", null );
        Text desc5 =lang.newText(new Coordinates(30,150), "free to use its own scheduling, thus adding greater flexibility then merely", "desc", null );
        Text desc6 =lang.newText(new Coordinates(30,170), "having multiple levels in a queue.", "desc", null );

        Text desc7 =lang.newText(new Coordinates(30,210), "In this scenario each process has a tima of arrival (process.time), and a number", "desc", null );
        Text desc8 =lang.newText(new Coordinates(30,230), "of execution timeslides (process.work). The algorithm loops until all processes", "desc", null );
        Text desc9 =lang.newText(new Coordinates(30,250), "are done. To schedule the processes the algorithm first adds all arriving", "desc", null );
        Text desc10 =lang.newText(new Coordinates(30,270), "processes to the queues, then picks the non-empty queue with the highest level", "desc", null );

        Text desc11 =lang.newText(new Coordinates(30,310), "The first queue in this example uses First-Come-First-Serve-Scheduling while the", "desc", null );
        Text desc12 =lang.newText(new Coordinates(30,330), "second queue uses Round-Robin-Scheduling.", "desc", null );

        lang.nextStep();

		//hide intro Text
		desc1.hide();
        desc2.hide();
        desc3.hide();
        desc4.hide();
        desc5.hide();
        desc6.hide();
        desc7.hide();
        desc8.hide();
        desc9.hide();
        desc10.hide();
        desc11.hide();
        desc12.hide();

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
		matrixProps.set(AnimationPropertiesKeys.ELEMHIGHLIGHT_PROPERTY, Color.BLACK);
		matrixProps.set(AnimationPropertiesKeys.CELLHIGHLIGHT_PROPERTY,
	        Color.GREEN);
		matrixProps.set(AnimationPropertiesKeys.GRID_STYLE_PROPERTY, "table");

	    // now, create the IntArray object, linked to the properties
	    sm = lang.newStringMatrix(new Coordinates(400, 10), procMatrix, "inc_proc", null, matrixProps);
	    
	    
	    arrays = new StringMatrix[queues.size()];
	   
	    
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
		SourceCode sc = lang.newSourceCode(new Coordinates(30, 70), "sourceCode", null, scProps);

		sc.addCodeLine("WHILE sum(proc.work) != 0", null, 0, null);				// line 0
		sc.addCodeLine("FOR process IN procList", null, 1, null);				// line 1
		sc.addCodeLine("IF process.time == time", null, 2, null);				// line 2
		sc.addCodeLine("queue = queueList[process.level]", null, 3, null);		// line 3
		sc.addCodeLine("queue.add(process)", null, 3, null);					// line 4
		sc.addCodeLine("FOR i FROM 0 TO (queueList.size - 1)", null, 0, null);	// line 5
		sc.addCodeLine("IF queueList[i].current() != null", null, 1, null);		// line 6
		sc.addCodeLine("queue = queueList[i]", null, 2, null);					// line 7
		sc.addCodeLine("BREAK", null, 2, null);									// line 8
		sc.addCodeLine("IF queue == null", null, 0, null);						// line 9
		sc.addCodeLine("time++", null, 1, null);								// line 10
		sc.addCodeLine("CONTINUE", null, 1, null);								// line 11
		sc.addCodeLine("run(queue.current())", null, 0, null);					// line 12
		sc.addCodeLine("IF queue.current().work == 0", null, 0, null);			// line 13
		sc.addCodeLine("queue.removeCurrent();", null, 1, null);				// line 14
		sc.addCodeLine("ELSE", null, 0, null);									// line 15
		sc.addCodeLine("if(queue.useRoundRobin)", null, 1, null);				// line 16
		sc.addCodeLine("temp = queue.current()", null, 2, null);				// line 17
		sc.addCodeLine("queue.removeCurrent()", null, 2, null);					// line 18
		sc.addCodeLine("queue.add(temp)", null, 2, null);						// line 19
		
		currentTimeText = lang.newText(new Coordinates(600, 10), "Current Time: 0", "curr_time", null);
		currentTime = 0;
		
		currentTime = 0;
		TextProperties tp = new TextProperties();
		currentTimeText = lang.newText(new Coordinates(600, 10), "Current Time: " + currentTime, "curr_time", null, tp);
		
		lang.nextStep();
		sc.highlight(0);
		lang.nextStep();
		while ((!inc_procs.isEmpty()) && (sumOfWork() != 0)) {
			highlightProcessCol(2);
			lang.nextStep();
			
			unhighlightProcessCol(2);
			sc.highlight(1);
			highlightProcessCol(0);
			lang.nextStep();
			// enqueue arriving processes
			for (Process p : inc_procs) {
				sm.highlightCell(inc_procs.indexOf(p) + 1, 3, null, null);
				sc.highlight(2);
				lang.nextStep();
				if (p.arrival == currentTime) {
					sc.highlight(3);
					sc.highlight(4);
					addToQueue(p.queue, p);
					sc.unhighlight(3);
					sc.unhighlight(4);
				}
				sc.unhighlight(2);
				sm.unhighlightCell(inc_procs.indexOf(p) + 1, 3, null, null);
			}
			sc.unhighlight(1);
			unhighlightProcessCol(0);

			// schedule Process of queue with highest priority
			Queue currentQueue = null;
			sc.highlight(5);
			lang.nextStep();
			for (int i = 0; i < queues.size(); i++) {
				currentQueue = queues.get(i);
				highlightQueue(currentQueue);
				sc.highlight(6);
				highlightQueueHead(currentQueue);
				lang.nextStep();
				if (currentQueue.procs.size() > 0) {
					sc.highlight(7);
					sc.highlight(8);
					lang.nextStep();
					break;
				}
				unhighlightQueueHead(currentQueue);
				unhighlightQueue(currentQueue);
				currentQueue = null;
			}
			sc.unhighlight(5);
			sc.unhighlight(6);
			sc.unhighlight(7);
			sc.unhighlight(8);
			sc.highlight(9);
			lang.nextStep();
			if (currentQueue == null) {
				// Zur zeit keine Arbeit in den Queues
				incCurrentTime();
				sc.highlight(10);
				lang.nextStep();
				sc.unhighlight(10);
				sc.highlight(11);
				lang.nextStep();
				sc.unhighlight(11);
			} else {
				Process currentProc = currentQueue.procs.get(0);
				currentProc.computingTime--;
				if (currentProc.computingTime == 0) {
					currentQueue.procs.remove(0);
				} else {

				}
				unhighlightQueue(currentQueue);
			}
			sc.unhighlight(9);
		}
		sc.highlight(0);
		highlightProcessCol(2);
	}
	
	private void highlightProcessCol(int col) {
		for(int i = 0; i < sm.getNrRows(); i++) {
			sm.highlightCell(i, col, null, null);
		}
	}
	
	private void highlightQueue(Queue q) {
		arrays[queues.indexOf(q)].highlightCell(0, 0, null, null);
	}
	
	private void unhighlightQueue(Queue q) {
		arrays[queues.indexOf(q)].unhighlightCell(0, 0, null, null);
	}
	
	private void highlightQueueHead(Queue q) {
		StringMatrix current = arrays[queues.indexOf(q)];
		current.highlightCell(0, current.getNrCols() -1, null, null);
	}
	
	private void unhighlightQueueHead(Queue q) {
		StringMatrix current = arrays[queues.indexOf(q)];
		current.unhighlightCell(0, current.getNrCols() -1, null, null);
	}
	
	private void unhighlightProcessCol(int col) {
		for(int i = 0; i < sm.getNrRows(); i++) {
			sm.unhighlightCell(i, col, null, null);
		}
	}
	
	private void incCurrentTime(){
		currentTime++;
		currentTimeText.setText("Current Time: " + currentTime, null, defaultDuration);
	}

	public void addToQueue(Queue q, Process p) {
		q.procs.add(p);
		StringMatrix currentQueue = arrays[queues.indexOf(q)];
		currentQueue.put(0, 
				(currentQueue.getNrCols() - 1) - q.procs.indexOf(p), 
				p.name,
				null,
				null);
		currentQueue.highlightCell(0, (currentQueue.getNrCols() - 1) - q.procs.indexOf(p), null, null);
		lang.nextStep();
		currentQueue.unhighlightCell(0, (currentQueue.getNrCols() - 1) - q.procs.indexOf(p), null, null);
		
	}

	public void removeFromQueue(Process p) {
		p.queue.procs.remove(0);
		StringMatrix currentQueue = arrays[queues.indexOf(p.queue)];
		for(int i = currentQueue.getNrCols() - 1; i > 1; i--) {
			currentQueue.put(0, i, currentQueue.getElement(0, i-1), null, null);
		}

	}

	public void reschedule(Process p) {
		Queue q = p.queue;
		if (q.useRoundRobin) {
			removeFromQueue(p);
			addToQueue(q, p);
		} else {
			// Bei FCFS keine aktion n�tig.
		}
	}

	public int sumOfWork() {
		int sum = 0;
		for (Process p : inc_procs) {
			sum += p.computingTime;
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
		return "MultiLevelQueue";
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
