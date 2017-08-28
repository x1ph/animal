import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import algoanim.primitives.*;
import algoanim.primitives.Text;
import algoanim.primitives.generators.AnimationType;
import algoanim.primitives.generators.Language;
import algoanim.properties.*;
import algoanim.util.Coordinates;
import algoanim.util.TicksTiming;
import algoanim.util.Timing;
import translator.Translator;
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
	 * The Translator Object for internationalization
	 */
	private Translator trans;

	private String DESCRIPTION;

	private String SOURCE_CODE;

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
	 * Title showing on every page
	 */
	private Text title;

	/**
	 * The current timeslice of schedulting
	 */
	private int currentTime;

	/**
	 * The animal primitive showing the current timeslice of scheduling
	 */
	private Text currentTimeText;

	/**
	 * The animal primitive showing the current execution hint
	 */
	private Text hint;

	/**
	 * The Array holding all displayed queues
	 */
	StringMatrix[] arrays;

	/**
	 * The Array holding all displayed queueNames
	 */
	Text[] queueNames;

	/**
	 * The Matrix showing all incoming Processes
	 */
	StringMatrix sm;

	/*
	 * Fields for Summary
	 */
	String schedulingOrder;
	int computingSteps;
	int idlingSteps;

	private static Font defaultFont =new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	private static Font highlightFont =new Font(Font.SANS_SERIF, Font.BOLD, 12);

	/**
	 * Default constructor
	 *
	 * @param l
	 *            the conrete language object used for creating output
	 */
	public MultiLevelQueueGenerator(Language l) {

		// Store the language object
		lang = l;
		// init Translator Object
		trans = new Translator("locale/mlq", Locale.US);
		DESCRIPTION = trans.translateMessage("DESCRIPTION");
		SOURCE_CODE = trans.translateMessage("SOURCE_CODE");

		// This initializes the step mode. Each pair of subsequent steps has to
		// be divdided by a call of lang.nextStep();
		lang.setStepMode(true);
		schedulingOrder = new String();
		idlingSteps = 0;
		computingSteps = 0;
	}

	/**
	 * Set the list of queues this MultiLevelQueue uses.
	 *
	 * @param	queues	Queues to schedule processes.
	 */
	public void setQueues(List<Queue> queues) {
		this.queues = queues;
	}

	/**
	 * Set the processes that this MLQ will schedule.
	 *
	 * @param	procs	Processes to schedule.
	 */
	public void setProcesses(List<Process> procs){
		this.inc_procs = procs;
	}

	/**
	 * default duration for swap processes
	 */
	public final static Timing defaultDuration = new TicksTiming(30);

	/**
	 * Schedule the Processes and Queues of this instance of MultiLevelQueue
	 * (see main())
	 */
	public void schedule() {
		TextProperties titleProps = new TextProperties();
		titleProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		title = lang.newText(new Coordinates(30,30), trans.translateMessage("DESC_TITLE"), "desc", null, titleProps);


		Text desc1 =lang.newText(new Coordinates(30,70), trans.translateMessage("DESC_1"), "desc", null );
        Text desc2 =lang.newText(new Coordinates(30,90), trans.translateMessage("DESC_2"), "desc", null );
        Text desc3 =lang.newText(new Coordinates(30,110), trans.translateMessage("DESC_3"), "desc", null );
        Text desc4 =lang.newText(new Coordinates(30,130),trans.translateMessage("DESC_4"), "desc", null );
        Text desc5 =lang.newText(new Coordinates(30,150), trans.translateMessage("DESC_5"), "desc", null );
        Text desc6 =lang.newText(new Coordinates(30,170), trans.translateMessage("DESC_6"), "desc", null );

        Text desc7 =lang.newText(new Coordinates(30,210), trans.translateMessage("DESC_7"), "desc", null );
        Text desc8 =lang.newText(new Coordinates(30,230), trans.translateMessage("DESC_8"), "desc", null );
        Text desc9 =lang.newText(new Coordinates(30,250), trans.translateMessage("DESC_9"), "desc", null );
        Text desc10 =lang.newText(new Coordinates(30,270), trans.translateMessage("DESC_10"), "desc", null );

        Text desc11 =lang.newText(new Coordinates(30,310), trans.translateMessage("DESC_11"), "desc", null );
        Text desc12 =lang.newText(new Coordinates(30,330), trans.translateMessage("DESC_12"), "desc", null );

        lang.nextStep();
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
		procMatrix[0][0] = trans.translateMessage("ID");
		procMatrix[0][1] = trans.translateMessage("QUEUE");
		procMatrix[0][2] = trans.translateMessage("WORK");
		procMatrix[0][3] = trans.translateMessage("TIME");
		for (int i = 0; i < inc_procs.size(); i++) {
			procMatrix[i + 1][0] = inc_procs.get(i).name;
			procMatrix[i + 1][1] = inc_procs.get(i).queue.name;
			procMatrix[i + 1][2] = Integer.toString(inc_procs.get(i).work);
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
		matrixProps.set(AnimationPropertiesKeys.GRID_HIGHLIGHT_BORDER_COLOR_PROPERTY, Color.BLACK);
		matrixProps.set(AnimationPropertiesKeys.GRID_STYLE_PROPERTY, "table");

	    // now, create the IntArray object, linked to the properties
	    sm = lang.newStringMatrix(new Coordinates(400, 100), procMatrix, "inc_proc", null, matrixProps);

	    arrays = new StringMatrix[queues.size()];
	    queueNames = new Text[queues.size()];
	    TextProperties queueNameTextProp = new TextProperties();
	    queueNameTextProp.set(AnimationPropertiesKeys.FONT_PROPERTY, defaultFont);

	    for(int i = 0; i < queues.size(); i++) {
	    	String[][] queueStat = new String[1][inc_procs.size()];
	    	for(int j = 0; j < inc_procs.size(); j++) {
	    		queueStat[0][j] = new String(" ");
	    	}
	    	String queueName = queues.get(i).name;
	    	if(queues.get(i).useRoundRobin) {
	    		queueName = queueName + " (RoundRobin)";
	    	}else {
	    		queueName = queueName + " (FIFO)";
	    	}
	    	queueNames[i] = lang.newText(new Coordinates(400, 130 + inc_procs.size() * 30 + i*50), queueName, "queuetext_" + i, null, queueNameTextProp);
	    	arrays[i] = lang.newStringMatrix(new Coordinates(400, 150 + inc_procs.size() * 30 + i * 50), queueStat, "queue_" + i, null, matrixProps);
	    }

		// first, set the visual properties for the source code
		SourceCodeProperties scProps = new SourceCodeProperties();
		scProps.set(AnimationPropertiesKeys.CONTEXTCOLOR_PROPERTY, Color.GRAY);
		scProps.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.GRAY);
		scProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.MONOSPACED, Font.PLAIN, 12));

		scProps.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.BLUE);

		// now, create the source code entity
		SourceCode sc = lang.newSourceCode(new Coordinates(30, 70), "sourceCode", null, scProps);

		sc.addCodeLine(trans.translateMessage("SRC_0"), null, 0, null);	// line 0
		sc.addCodeLine(trans.translateMessage("SRC_1"), null, 1, null);	// line 1
		sc.addCodeLine(trans.translateMessage("SRC_2"), null, 2, null);	// line 2
		sc.addCodeLine(trans.translateMessage("SRC_3"), null, 3, null);	// line 3
		sc.addCodeLine(trans.translateMessage("SRC_4"), null, 3, null);	// line 4
		sc.addCodeLine(trans.translateMessage("SRC_5"), null, 1, null);	// line 5
		sc.addCodeLine(trans.translateMessage("SRC_6"), null, 2, null);	// line 6
		sc.addCodeLine(trans.translateMessage("SRC_7"), null, 3, null);	// line 7
		sc.addCodeLine(trans.translateMessage("SRC_8"), null, 3, null);	// line 8
		sc.addCodeLine(trans.translateMessage("SRC_9"), null, 1, null); // line 9
		sc.addCodeLine(trans.translateMessage("SRC_10"), null, 2, null); // line 10
		sc.addCodeLine(trans.translateMessage("SRC_11"), null, 2, null); // line 11
		sc.addCodeLine(trans.translateMessage("SRC_12"), null, 1, null); // line 12
		sc.addCodeLine(trans.translateMessage("SRC_13"), null, 1, null); // line 13
		sc.addCodeLine(trans.translateMessage("SRC_14"), null, 2, null); // line 14
		sc.addCodeLine(trans.translateMessage("SRC_15"), null, 1, null); // line 15
		sc.addCodeLine(trans.translateMessage("SRC_16"), null, 2, null); // line 16
		sc.addCodeLine(trans.translateMessage("SRC_17"), null, 3, null); // line 17
		sc.addCodeLine(trans.translateMessage("SRC_18"), null, 3, null); // line 18
		sc.addCodeLine(trans.translateMessage("SRC_19"), null, 3, null); // line 19

		currentTime = 0;
		TextProperties tp = new TextProperties();
		currentTimeText = lang.newText(new Coordinates(400, 70), trans.translateMessage("CURR_TIME") + currentTime, "curr_time", null, tp);

		hint = lang.newText(new Coordinates(600, 150), "", "hint", null, tp);

		lang.nextStep();
		sc.highlight(0);
		while (sumOfWork() != 0) {
			highlightProcessCol(2);
			hint.setText(trans.translateMessage("REMAINING_WORK"), null, defaultDuration);
			lang.nextStep();
			unhighlightProcessCol(2);
			hint.setText(trans.translateMessage("INCOMING_PROCESS"), null, defaultDuration);

			sc.highlight(1);
			highlightProcessCol(0);
			lang.nextStep();
			for(Process p : inc_procs) {
				hint.setText(trans.translateMessage("PROCESS") + " " + p.name + " " + trans.translateMessage("STARTS_AT") + " " + p.arrival, null, defaultDuration);
				sc.highlight(2);
				sm.highlightCell(inc_procs.indexOf(p)+1, 3, null, null);
				lang.nextStep();
				if(p.arrival == currentTime && p.work > 0) {
					sc.highlight(3);
					sc.highlight(4);
					hint.setText(trans.translateMessage("PROCESS_LEVEL_IS") + " " + p.queue.name, null, defaultDuration);
					addToQueue(p.queue, p);
					sc.unhighlight(3);
					sc.unhighlight(4);
				}
				sc.unhighlight(2);
				sm.unhighlightCell(inc_procs.indexOf(p)+1, 3, null, null);
			}
			sc.unhighlight(1);
			unhighlightProcessCol(0);

			Queue queue = null;

			sc.highlight(5);
			hint.setText(trans.translateMessage("SEARCH_FOR_QUEUE_TO_SCHEDULE"), null, defaultDuration);
			lang.nextStep();
			for(Queue q : queues) {
				highlightQueue(q);
				highlightQueueHead(q);
				sc.highlight(6);
				hint.setText(trans.translateMessage("CHECK_QUEUE") + " " + q.name, null, defaultDuration);
				lang.nextStep();
				if(!q.procs.isEmpty()) {
					sc.highlight(7);
					sc.highlight(8);
					unhighlightQueueHead(q);
					hint.setText(trans.translateMessage("QUEUE_LC") + " " + q.name + " " + trans.translateMessage("HAS_PENDING_WORK"), null, defaultDuration);
					lang.nextStep();
					sc.unhighlight(6);
					sc.unhighlight(7);
					sc.unhighlight(8);
					queue = q;
					break;
				}
				unhighlightQueueHead(q);
				unhighlightQueue(q);
				sc.unhighlight(6);
			}
			sc.unhighlight(5);

			sc.highlight(9);
			hint.setText(trans.translateMessage("CHECK_FOR_QUEUE_TO_SCHEDULE"), null, defaultDuration);
			lang.nextStep();
			if(queue == null) {
				sc.highlight(10);
				sc.highlight(11);
				idlingSteps++;
				schedulingOrder += "-";
				incCurrentTime();
				hint.setText(trans.translateMessage("NO_QUEUE_HAS_PENDING_WORK"), null, defaultDuration);
				lang.nextStep();
				sc.unhighlight(9);
				sc.unhighlight(10);
				sc.unhighlight(11);
				continue;
			}
			sc.unhighlight(9);

			sc.highlight(12);
			sm.setGridHighlightFillColor(inc_procs.indexOf(queue.procs.getFirst()) +1, 2, Color.YELLOW, null, null);
			sm.highlightCell(inc_procs.indexOf(queue.procs.getFirst()) +1, 2, null, null);
			highlightQueueHead(queue);
			queue.procs.getFirst().work--;
			computingSteps++;
			schedulingOrder += queue.procs.getFirst().name;
			sm.put(inc_procs.indexOf(
					queue.procs.getFirst()) +1,
					2,
					Integer.toString(queue.procs.getFirst().work),
					null,
					null);
			hint.setText(trans.translateMessage("SCHEDULE_PROCESS") + " " + queue.procs.getFirst().name + " " + trans.translateMessage("FROM_QUEUE") + " " + queue.name, null, defaultDuration);
			lang.nextStep();

			sc.unhighlight(12);

			sc.highlight(13);
			hint.setText(trans.translateMessage("CHECK_IF_WORK_LEFT"), null, defaultDuration);
			lang.nextStep();
			sm.setGridHighlightFillColor(inc_procs.indexOf(queue.procs.getFirst()) +1, 2, Color.GREEN, null, null);
			sm.unhighlightCell(inc_procs.indexOf(queue.procs.getFirst()) +1, 2, null, null);
			if(queue.procs.getFirst().work == 0) {
				sc.highlight(14);
				hint.setText(trans.translateMessage("PROCESS_HAS_NO_WORK_LEFT_REMOVE"), null, defaultDuration);
				lang.nextStep();
				removeFromQueue(queue.procs.getFirst());
				unhighlightQueue(queue);
				sc.unhighlight(14);
			}else {
				sc.highlight(15);
				sc.highlight(16);
				hint.setText(trans.translateMessage("PROCESS_HAS_WORK_LEFT_RESCHEDULE"), null, defaultDuration);
				lang.nextStep();
				if(queue.useRoundRobin) {
					sc.highlight(17);
					sc.highlight(18);
					sc.highlight(19);
					hint.setText(trans.translateMessage("RESCHEDULE_ROUND_ROBIN"), null, defaultDuration);
					lang.nextStep();
					reschedule(queue.procs.getFirst());
					unhighlightQueue(queue);
					sc.unhighlight(17);
					sc.unhighlight(18);
					sc.unhighlight(19);
				}else {
					unhighlightQueue(queue);
					unhighlightQueueHead(queue);
				}
				sc.unhighlight(15);
				sc.unhighlight(16);
			}
			incCurrentTime();
			sc.unhighlight(13);
		}
		sc.unhighlight(0);
		hint.setText(trans.translateMessage("NO_PROCESS_HAS_PENDING_WORK"), null, defaultDuration);
		lang.nextStep();
	}

	private void summarize() {
		lang.hideAllPrimitivesExcept(title);
		lang.newText(new Coordinates(30,  70),
				trans.translateMessage(
						"THIS_MLQ_SCHED")
						+ " "
						+ (idlingSteps + computingSteps)
						+ " "
						+ trans.translateMessage("TIME_SLOTS"),
				"summ_1",
				null);
		lang.newText(new Coordinates(30,  90),
				trans.translateMessage(
						"OF_COMP_TIME_FOR")
						+ " "
						+ inc_procs.size()
						+ " "
						+ trans.translateMessage("PROCESSES_IN")
						+ " "
						+ queues.size()
						+ " "
						+ trans.translateMessage("QUEUES"),
				"summ_2",
				null);
		lang.newText(new Coordinates(30,  130),
				trans.translateMessage("WORK_TIME") + ": " + computingSteps,
				"summ_3",
				null);
		lang.newText(new Coordinates(30,  150),
				trans.translateMessage("IDLE_TIME") + ": " + idlingSteps,
				"summ_4",
				null);
		lang.newText(new Coordinates(30,  170),
				trans.translateMessage("SCHEDULING_ORDER") + ": " + schedulingOrder,
				"summ_5",
				null);
		lang.nextStep();

	}

	private void highlightQueue(Queue q) {
		queueNames[queues.indexOf(q)].setFont(highlightFont, null, null);
	}

	private void unhighlightQueue(Queue q) {
		queueNames[queues.indexOf(q)].setFont(defaultFont, null, null);
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
		for(int i = 1; i < sm.getNrRows(); i++) {
			sm.unhighlightCell(i, col, null, null);
		}
	}

	private void highlightProcessCol(int col) {
		for(int i = 1; i < sm.getNrRows(); i++) {
			sm.highlightCell(i, col, null, null);
		}
	}

	private void incCurrentTime(){
		currentTime++;
		currentTimeText.setText(trans.translateMessage("CURR_TIME") + ": " + currentTime, null, defaultDuration);
	}

	public void addToQueue(Queue q, Process p) {
		q.procs.add(p);
		hint.setText(trans.translateMessage("ADD_PROCESS") + " " + p.name + " " + trans.translateMessage("TO_QUEUE") + " " + q.name, null, defaultDuration);
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
		for(int i = currentQueue.getNrCols() - 1; i > 0; i--) {
			currentQueue.put(0, i, currentQueue.getElement(0, i-1), null, null);
		}
		unhighlightQueueHead(p.queue);
		hint.setText(trans.translateMessage("REMOVE_PROCESS") + " " + p.name + " " + trans.translateMessage("FROM_QUEUE") + " " + p.queue.name, null, defaultDuration);
		lang.nextStep();

	}

	public void reschedule(Process p) {
		removeFromQueue(p);
		addToQueue(p.queue, p);
	}

	public int sumOfWork() {
		int sum = 0;
		for (Process p : inc_procs) {
			sum += p.work;
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

		/*
		 * ERSTELLEN DER OBJEKTE F�R DIE VISUALISIERUNG
		 *
		 * Die Visualisierung verwendet die Prozesse in inc_processes und die Queues aus queues
		 * f�r die Darstellung. Hier wird eine bestimmte Anzahl an Queues und Prozessen erstellt,
		 * die zuf�llige Werte f�r die computation Time und arrival Time (bei Prozessen) sowie die
		 * verwendete scheduling Strategie (bei Queues) gew�hlt werden.
		 *
		 * Die Queues und Prozesse k�nnen nat�rlich auch manuell erstellt werden.
		 */
		int numberOfQueues = 3;
		int numberOfProcesses = 5;

		MultiLevelQueueGenerator mlqg = new MultiLevelQueueGenerator(l);

		for(int i = 0; i < numberOfQueues; i++) {
			Queue q = mlqg.new Queue("Level " + i, Math.random() < 0.5);
			queues.add(q);
		}

		for(int i = 0; i < numberOfProcesses; i++) {
			Process proc = mlqg.new Process("" + (char)(65 + i), queues.get((int)(Math.random() * (queues.size() - 1))), 1 + (int)(Math.random() * 5), (int)(Math.random() * 10));
			inc_procs.add(proc);
		}

		mlqg.setQueues(queues);
		mlqg.setProcesses(inc_procs);

		/* start scheduling */
		mlqg.schedule();
		/* summarize */
		mlqg.summarize();
		/* print animalscript */
		String out = l.toString();
		System.out.println(out);
		/* write animalscript to file */
		try {
			FileWriter fw = new FileWriter(new File("MultiLevelQueue.asu"), false);
			fw.write(out);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * A Process is an object that arrives at a given time in a predefined queue
	 * to schedule the given amount of work.
	 */
	public class Process {
		/** The name of this process. */
		public String name;
		/** Predefined queue of this process. */
		public Queue queue;
		/** The number of time-slices this process needs */
		public int work;
		/** The number of steps after which this process arrives */
		public int arrival;
		/**
		 * Constructs a new Process.
		 *
		 * @param	name		Name of the process.
		 * @param	queue		Predefined queue of this process
		 * @param	work		The number of time slices, this process needs
		 * 						to be scheduled
		 * @param	arrival		The number of time slices after which the
		 * 						process arrives.
		 */
		public Process(String name, Queue queue, int work, int arrival) {
			super();
			this.name = name;
			this.queue = queue;
			this.work = work;
			this.arrival = arrival;
		}
	}

	/**
	 * A Queue has a name and a list of enqueued processes. A Queue can be
	 * scheduled in FIFO-Mode (useRoundRobin: false) or in RoundRobin-Mode
	 * (useRoundRobin: true).
	 */
	public class Queue {

		/** This Queues Name */
		public String name;
		/** The list of processes in this queue */
		public LinkedList<Process> procs;
		/** true if this queue uses RoundRobin-Scheduling */
		public boolean useRoundRobin;

		/**
		 * Constructs a new Queue.
		 *
		 * @param	name	The name of the Queue.
		 * @param	useRR	Specifies if this queue schedules in
		 * 					RoundRobin-mode.
		 */
		public Queue(String name, boolean useRR) {
			this.name = name;
			this.useRoundRobin = useRR;
			procs = new LinkedList<Process>();
		}
	}
}
