/*
 * MultilevelQueue.java
 * Andre Challier <andre.challier@stud.tu-darmstadt.de>, Christian Richter <chrisrichter145@gmail.com>, 2017 for the Animal project at TU Darmstadt.
 * Copying this file for educational purposes is permitted without further authorization.
 */
package generators.misc;

import generators.framework.Generator;
import generators.framework.GeneratorType;
import generators.framework.ValidatingGenerator;

import java.util.Locale;

import algoanim.primitives.ArrayMarker;
import algoanim.primitives.SourceCode;
import algoanim.primitives.StringArray;
import algoanim.primitives.StringMatrix;
import algoanim.primitives.Text;
import algoanim.primitives.generators.Language;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import generators.framework.properties.AnimationPropertiesContainer;
import translator.Translator;
import algoanim.animalscript.AnimalScript;
import algoanim.counter.model.TwoValueCounter;
import algoanim.counter.view.TwoValueView;

import java.awt.Color;
import java.awt.Font;

import algoanim.properties.AnimationPropertiesKeys;
import algoanim.properties.ArrayMarkerProperties;
import algoanim.properties.ArrayProperties;
import algoanim.properties.CounterProperties;
import algoanim.properties.MatrixProperties;
import algoanim.properties.SourceCodeProperties;
import algoanim.properties.TextProperties;
import algoanim.util.Coordinates;
import algoanim.util.Node;
import algoanim.util.Offset;
import algoanim.util.TicksTiming;
import algoanim.util.Timing;

public class MultilevelQueue implements ValidatingGenerator {
	
    private Language lang;
    private Color highlightColor;
    private ArrayProperties arg_queueProperties;
    private String[] arg_queues;
    private String[][] arg_processes;
    private MatrixProperties arg_processProperties;
    
    private ArrayMarkerProperties headMarkerProperties;
    private ArrayMarkerProperties tailMarkerProperties;
    
    private String path;
	private Locale loc;
    
    private Translator trl;

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
	 * Pseudocode SourceCode
	 */
	private SourceCode sc;

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
	StringArray[] queueViews;
	
	/**
	 * The Array holding all displayed queueNames
	 */
	Text[] queueNames;
	
	/**
	 * The Array holding counters for all queues
	 */
	TwoValueCounter[] queueCounters;
	
	/**
	 * The Array holding the views for all queue-counters
	 */
	TwoValueView[] queueCounterViews;
	
	/**
	 * The Matrix showing all incoming Processes
	 */
	StringMatrix processMatrix;
	
	/*
	 * Fields for Summary
	 */
	String schedulingOrder;
	int computingSteps;
	int idlingSteps;
	
	private static Font defaultFont =new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	private static Font highlightFont =new Font(Font.SANS_SERIF, Font.BOLD, 12);
	
	/**
	 * default duration for swap processes
	 */
	public final static Timing defaultDuration = new TicksTiming(30);
	
	public MultilevelQueue(String path, Locale loc) {
		this.path = path;
		this.loc = loc;
	}

    public void init(){
        lang = new AnimalScript("Animation of Multilevel Queue", "Andre Challier <andre.challier@stud.tu-darmstadt.de>, Christian Richter <chrisrichter145@gmail.com>", 800, 600);
        lang.setStepMode(true);
        trl = new Translator(path, loc);
    }

    public String generate(AnimationPropertiesContainer props,Hashtable<String, Object> primitives) {
        
    	arg_queues = (String[])primitives.get("Queues");
        arg_processes = (String[][])primitives.get("Processes");
    	
    	highlightColor = (Color)primitives.get("HighlightColor");
        arg_queueProperties = (ArrayProperties)props.getPropertiesByName("QueueTable");
        arg_processProperties = (MatrixProperties)props.getPropertiesByName("ProcessTable");
        
		arg_processProperties.set(AnimationPropertiesKeys.GRID_STYLE_PROPERTY, "table");
		headMarkerProperties = new ArrayMarkerProperties();
		tailMarkerProperties = new ArrayMarkerProperties();
		headMarkerProperties.set(AnimationPropertiesKeys.LABEL_PROPERTY, "head");
		tailMarkerProperties.set(AnimationPropertiesKeys.LABEL_PROPERTY, "tail");
		tailMarkerProperties.set(AnimationPropertiesKeys.SHORT_MARKER_PROPERTY, true);
        
        queues = new LinkedList<Queue>();
        inc_procs = new LinkedList<Process>();
        
        // set initial variables
        
        schedulingOrder = new String();
        computingSteps = 0;
        idlingSteps = 0;
        currentTime = 0;
        
        // draw description
        
        TextProperties titleProps = new TextProperties();
		titleProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		title = lang.newText(new Coordinates(30,30), trl.translateMessage("DESC_TITLE"), "desc", null, titleProps);


		Text desc1 =lang.newText(new Offset(0, 20, title, AnimalScript.DIRECTION_SW), trl.translateMessage("DESC_1"), "desc", null );
        Text desc2 =lang.newText(new Offset(0, 20, desc1, AnimalScript.DIRECTION_SW), trl.translateMessage("DESC_2"), "desc", null );
        Text desc3 =lang.newText(new Offset(0, 20, desc2, AnimalScript.DIRECTION_SW), trl.translateMessage("DESC_3"), "desc", null );
        Text desc4 =lang.newText(new Offset(0, 20, desc3, AnimalScript.DIRECTION_SW),trl.translateMessage("DESC_4"), "desc", null );
        Text desc5 =lang.newText(new Offset(0, 20, desc4, AnimalScript.DIRECTION_SW), trl.translateMessage("DESC_5"), "desc", null );
        Text desc6 =lang.newText(new Offset(0, 20, desc5, AnimalScript.DIRECTION_SW), trl.translateMessage("DESC_6"), "desc", null );

        Text desc7 =lang.newText(new Offset(0, 30, desc6, AnimalScript.DIRECTION_SW), trl.translateMessage("DESC_7"), "desc", null );
        Text desc8 =lang.newText(new Offset(0, 20, desc7, AnimalScript.DIRECTION_SW), trl.translateMessage("DESC_8"), "desc", null );
        Text desc9 =lang.newText(new Offset(0, 20, desc8, AnimalScript.DIRECTION_SW), trl.translateMessage("DESC_9"), "desc", null );
        Text desc10 =lang.newText(new Offset(0, 20, desc9, AnimalScript.DIRECTION_SW), trl.translateMessage("DESC_10"), "desc", null );

        Text desc11 =lang.newText(new Offset(0, 30, desc10, AnimalScript.DIRECTION_SW), trl.translateMessage("DESC_11"), "desc", null );
        Text desc12 =lang.newText(new Offset(0, 20, desc11, AnimalScript.DIRECTION_SW), trl.translateMessage("DESC_12"), "desc", null );

        lang.nextStep();
        
        //hide description
        
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
		
		// first, set the visual properties for the source code
		SourceCodeProperties scProps = new SourceCodeProperties();
		scProps.set(AnimationPropertiesKeys.CONTEXTCOLOR_PROPERTY, Color.GRAY);
		scProps.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.GRAY);
		scProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.MONOSPACED, Font.PLAIN, 12));
		
		scProps.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, highlightColor);
		
		// now, create the source code entity
		sc = lang.newSourceCode(new Offset(0, 20, title, AnimalScript.DIRECTION_SW), "sourceCode", null, scProps);
		
		sc.addCodeLine(trl.translateMessage("SRC_0"), null, 0, null);	// line 0
		sc.addCodeLine(trl.translateMessage("SRC_1"), null, 1, null);	// line 1
		sc.addCodeLine(trl.translateMessage("SRC_2"), null, 2, null);	// line 2
		sc.addCodeLine(trl.translateMessage("SRC_3"), null, 3, null);	// line 3
		sc.addCodeLine(trl.translateMessage("SRC_4"), null, 3, null);	// line 4
		sc.addCodeLine(trl.translateMessage("SRC_5"), null, 1, null);	// line 5
		sc.addCodeLine(trl.translateMessage("SRC_6"), null, 2, null);	// line 6
		sc.addCodeLine(trl.translateMessage("SRC_7"), null, 3, null);	// line 7
		sc.addCodeLine(trl.translateMessage("SRC_8"), null, 3, null);	// line 8
		sc.addCodeLine(trl.translateMessage("SRC_9"), null, 1, null); // line 9
		sc.addCodeLine(trl.translateMessage("SRC_10"), null, 2, null); // line 10
		sc.addCodeLine(trl.translateMessage("SRC_11"), null, 2, null); // line 11
		sc.addCodeLine(trl.translateMessage("SRC_12"), null, 1, null); // line 12
		sc.addCodeLine(trl.translateMessage("SRC_13"), null, 1, null); // line 13
		sc.addCodeLine(trl.translateMessage("SRC_14"), null, 2, null); // line 14
		sc.addCodeLine(trl.translateMessage("SRC_15"), null, 1, null); // line 15
		sc.addCodeLine(trl.translateMessage("SRC_16"), null, 2, null); // line 16
		sc.addCodeLine(trl.translateMessage("SRC_17"), null, 3, null); // line 17
		sc.addCodeLine(trl.translateMessage("SRC_18"), null, 3, null); // line 18
		sc.addCodeLine(trl.translateMessage("SRC_19"), null, 3, null); // line 19
		
		// init current processing time
		currentTime = 0;
		TextProperties tp = new TextProperties();
		currentTimeText = lang.newText(new Offset(20, 0, sc, AnimalScript.DIRECTION_NE), trl.translateMessage("CURR_TIME") + currentTime, "curr_time", null, tp);
		
		// init processes
		String[][] procMatrix = new String[arg_processes.length + 1][4];
		procMatrix[0][0] = trl.translateMessage("ID");
		procMatrix[0][1] = trl.translateMessage("QUEUE");
		procMatrix[0][2] = trl.translateMessage("WORK");
		procMatrix[0][3] = trl.translateMessage("TIME");
		
		for(int i = 0; i < arg_processes.length; i++) {
			String[] arg_process = arg_processes[i];
        	String name = arg_process[0];
        	int queue = Integer.parseInt(arg_process[1]);
        	int work = Integer.parseInt(arg_process[2]);
        	int arrival = Integer.parseInt(arg_process[3]);
        	inc_procs.add(new Process(name, queue, work, arrival, i+1));
        	procMatrix[i + 1][0] = inc_procs.get(i).name;
			procMatrix[i + 1][1] = "Level " + inc_procs.get(i).queue;
			procMatrix[i + 1][2] = Integer.toString(inc_procs.get(i).work);
			procMatrix[i + 1][3] = Integer.toString(inc_procs.get(i).arrival);
        }
		
		processMatrix = lang.newStringMatrix(new Offset(0, 20, currentTimeText, AnimalScript.DIRECTION_SW), procMatrix, "inc_proc", null, arg_processProperties);
		
		// init Hint-Text
		hint = lang.newText(new Offset(20, -30, processMatrix, AnimalScript.DIRECTION_NE), "", "hint", null, tp);
		// init Queues
		
		queueViews = new StringArray[arg_queues.length];
	    queueNames = new Text[arg_queues.length];
	    queueCounters = new TwoValueCounter[arg_queues.length];
	    queueCounterViews = new TwoValueView[arg_queues.length];
	    TextProperties queueNameTextProp = new TextProperties();
	    queueNameTextProp.set(AnimationPropertiesKeys.FONT_PROPERTY, defaultFont);
	    CounterProperties queueCounterProperties = new CounterProperties();
        
        for(int i = 0; i < arg_queues.length; i++) {
        	// get queue strategy
        	String queueStrat = arg_queues[i];
        	boolean RR = queueStrat.equals("RR");
        	
        	// build queue name
        	String queueName = "Level " + i;
        	
        	// build initial empty String Array
        	String[] queueStat = new String[inc_procs.size()];
	    	for(int j = 0; j < inc_procs.size(); j++) {
	    		queueStat[j] = new String(" ");
	    	}
        	
        	// find anchor
        	Node anchor;
	    	if(i == 0) {
	    		anchor = new Offset(20, 50, processMatrix, AnimalScript.DIRECTION_NE);
	    	}else {
	    		anchor = new Offset(0, 100, queueNames[i-1], AnimalScript.DIRECTION_SW);
	    	}
	    	
	    	// draw label
	    	queueNames[i] = lang.newText(
	    			anchor, 
	    			RR ? queueName + " (RR)": queueName + " (FIFO)", 
	    			"queuetext_" + i, 
	    			null, 
	    			queueNameTextProp);
	    	
	    	// draw StringArray
	    	queueViews[i] = lang.newStringArray(
	    			new Offset(20, 0, queueNames[i], AnimalScript.DIRECTION_NE), 
	    			queueStat, 
	    			"queue_" + i, 
	    			null, 
	    			arg_queueProperties);
	    	
	    	// init access counter
	    	queueCounters[i] = lang.newCounter(queueViews[i]);
	    	
	    	// draw access counter
	    	queueCounterViews[i] = lang.newCounterView(
	    			queueCounters[i], 
	    			new Offset(20, 0, queueViews[i], 
	    			AnimalScript.DIRECTION_NE), 
	    			queueCounterProperties, true, true);
        	queues.add(
        			new Queue("Level " + queues.size(), 
        			RR, 
        			arg_processes.length, 
        			queueViews[i],
        			queueNames[i]));
        }
		
		lang.nextStep();
        
        schedule();
        summarize();
        
        return lang.toString();
    }
    
	@Override
	public boolean validateInput(AnimationPropertiesContainer props, Hashtable<String, Object> primitives)
			throws IllegalArgumentException {
		arg_queues = (String[])primitives.get("Queues");
        arg_processes = (String[][])primitives.get("Processes");
    	
    	highlightColor = (Color)primitives.get("HighlightColor");
        arg_queueProperties = (ArrayProperties)props.getPropertiesByName("QueueTable");
        arg_processProperties = (MatrixProperties)props.getPropertiesByName("ProcessTable");
        
        if(arg_queues == null || arg_queues.length == 0) {
        	throw new IllegalArgumentException("Cannot start without Queues.");
        }
        
        if(arg_processes == null || arg_processes.length == 0) {
        	throw new IllegalArgumentException("Cannot start without Processes.");
        }
        
        if(arg_processes[0].length != 4) {
        	throw new IllegalArgumentException("Processes MUST have 4 Fields (Name, Queue-ID, Work, Arrival-Time)");
        }
        
        int queuecnt = -1;
        
        for(String s: arg_queues) {
        	if(s == null) {
        		throw new IllegalArgumentException("Cannot init queue without scheduling strategy");
        	}
        	if(!(s.equals("FIFO") || s.equals("RR"))) {
        		throw new IllegalArgumentException("Cannot init queue with scheduling strategy '" + s + "'");
        	}
        	queuecnt++;
        }
        
        for(int i = 0; i < arg_processes.length; i++) {
        	String[] curr = arg_processes[i];
        	if(curr[0] == null) {
        		throw new IllegalArgumentException("Cannot schedule process with empty name (process: " + i + ")");
        	}
        	
        	if(curr[1] == null) {
        		throw new IllegalArgumentException("Cannot schedule process without queue (process: " + i + ")");
        	}
        	try {
				int queue = Integer.parseInt(curr[1]);
				if(queue < 0 || queue > queuecnt) {
					throw new IllegalArgumentException("Process has invalid queue-id: " + curr[1] + " Must be between 0 and " + queuecnt);
				}
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Process has malformed queue-id: " + curr[1]);
			}
        	
        	if(curr[2] == null) {
        		throw new IllegalArgumentException("Cannot schedule process without workload (process: " + i + ")");
        	}
        	try {
				Integer.parseInt(curr[2]);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Process has malformed workload: " + curr[2]);
			}
        	
        	if(curr[3] == null) {
        		throw new IllegalArgumentException("Cannot schedule process without arrival time (process: " + i + ")");
        	}
        	try {
				Integer.parseInt(curr[3]);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Process has malformed arrival time: " + curr[2]);
			}
        	
        }
        return true;
	}
	
	public void schedule() {
		sc.highlight(0);
		while (sumOfWork() != 0) {
			highlightProcessCol(2);
			hint.setText(trl.translateMessage("REMAINING_WORK"), null, defaultDuration);
			lang.nextStep();
			unhighlightProcessCol(2);
			hint.setText(trl.translateMessage("INCOMING_PROCESS"), null, defaultDuration);
			
			sc.highlight(1);
			highlightProcessCol(0);
			lang.nextStep();
			for(Process p : inc_procs) {
				hint.setText(trl.translateMessage("PROCESS") + " " + p.name + " " + trl.translateMessage("STARTS_AT") + " " + p.arrival, null, defaultDuration);
				sc.highlight(2);
				processMatrix.highlightCell(p.row, 3, null, null);
				lang.nextStep();
				if(p.arrival == currentTime && p.work > 0) {
					sc.highlight(3);
					sc.highlight(4);
					Queue q = queues.get(p.queue);
					hint.setText(trl.translateMessage("PROCESS_LEVEL_IS") + " " + q.name, null, defaultDuration);
					q.add(p);
					sc.unhighlight(3);
					sc.unhighlight(4);
				}
				sc.unhighlight(2);
				processMatrix.unhighlightCell(p.row, 3, null, null);
			}
			sc.unhighlight(1);
			unhighlightProcessCol(0);
			
			Queue queue = null;
			
			sc.highlight(5);
			hint.setText(trl.translateMessage("SEARCH_FOR_QUEUE_TO_SCHEDULE"), null, defaultDuration);
			lang.nextStep();
			for(Queue q : queues) {
				q.highlight();
				q.unhighlightTail();
				sc.highlight(6);
				hint.setText(trl.translateMessage("CHECK_QUEUE") + " " + q.name, null, defaultDuration);
				lang.nextStep();
				if(!q.isEmpty()) {
					sc.highlight(7);
					sc.highlight(8);
					q.unhighlightTail();
					hint.setText(trl.translateMessage("QUEUE_LC") + " " + q.name + " " + trl.translateMessage("HAS_PENDING_WORK"), null, defaultDuration);
					lang.nextStep();
					sc.unhighlight(6);
					sc.unhighlight(7);
					sc.unhighlight(8);
					queue = q;
					break;
				}
				q.unhighlightTail();
				q.unhighlight();
				sc.unhighlight(6);
			}
			sc.unhighlight(5);
			
			sc.highlight(9);
			hint.setText(trl.translateMessage("CHECK_FOR_QUEUE_TO_SCHEDULE"), null, defaultDuration);
			lang.nextStep();
			if(queue == null) {
				sc.highlight(10);
				sc.highlight(11);
				idlingSteps++;
				schedulingOrder += "-";
				incCurrentTime();
				hint.setText(trl.translateMessage("NO_QUEUE_HAS_PENDING_WORK"), null, defaultDuration);
				lang.nextStep();
				sc.unhighlight(9);
				sc.unhighlight(10);
				sc.unhighlight(11);
				continue;
			}
			sc.unhighlight(9);
			
			sc.highlight(12);
			queue.highlightTail();
			Process first = queue.first();
			first.run();
			hint.setText(trl.translateMessage("SCHEDULE_PROCESS") + " " + first.name + " " + trl.translateMessage("FROM_QUEUE") + " " + queue.name, null, defaultDuration);
			lang.nextStep();
			
			sc.unhighlight(12);
			
			sc.highlight(13);
			hint.setText(trl.translateMessage("CHECK_IF_WORK_LEFT"), null, defaultDuration);
			lang.nextStep();
			first.unhighlightWork();
			if(first.work == 0) {
				sc.highlight(14);
				hint.setText(trl.translateMessage("PROCESS_HAS_NO_WORK_LEFT_REMOVE"), null, defaultDuration);
				lang.nextStep();
				queue.removeTail();
				queue.unhighlight();;
				sc.unhighlight(14);
			}else {
				sc.highlight(15);
				sc.highlight(16);
				hint.setText(trl.translateMessage("PROCESS_HAS_WORK_LEFT_RESCHEDULE"), null, defaultDuration);
				lang.nextStep();
				if(queue.useRoundRobin && queue.numberOfProcesses > 1) {
					sc.highlight(17);
					hint.setText(trl.translateMessage("TEMP_ROUND_ROBIN"), null, defaultDuration);
					Process temp = first;
					lang.nextStep();
					sc.unhighlight(17);
					sc.highlight(18);
					queue.removeTail();
					sc.unhighlight(18);
					sc.highlight(19);
					queue.add(temp);
					sc.unhighlight(19);
					queue.unhighlight();
				}else {
					queue.unhighlight();
					queue.unhighlightTail();
				}
				sc.unhighlight(15);
				sc.unhighlight(16);
			}
			incCurrentTime();
			sc.unhighlight(13);
		}
		sc.unhighlight(0);
		hint.setText(trl.translateMessage("NO_PROCESS_HAS_PENDING_WORK"), null, defaultDuration);
		lang.nextStep();
	}
	
	private void summarize() {
		lang.hideAllPrimitivesExcept(title);
		lang.newText(new Coordinates(30,  70),
				trl.translateMessage(
						"THIS_MLQ_SCHED") 
						+ " " 
						+ (idlingSteps + computingSteps) 
						+ " " 
						+ trl.translateMessage("TIME_SLOTS"),
				"summ_1", 
				null);
		lang.newText(new Coordinates(30,  90),
				trl.translateMessage(
						"OF_COMP_TIME_FOR") 
						+ " " 
						+ inc_procs.size() 
						+ " "
						+ trl.translateMessage("PROCESSES_IN")
						+ " "
						+ queues.size() 
						+ " "
						+ trl.translateMessage("QUEUES"),
				"summ_2", 
				null);
		lang.newText(new Coordinates(30,  130),
				trl.translateMessage("WORK_TIME") + ": " + computingSteps,
				"summ_3", 
				null);
		lang.newText(new Coordinates(30,  150),
				trl.translateMessage("IDLE_TIME") + ": " + idlingSteps,
				"summ_4", 
				null);
		lang.newText(new Coordinates(30,  170),
				trl.translateMessage("SCHEDULING_ORDER") + ": " + schedulingOrder,
				"summ_5", 
				null);
		lang.nextStep();
		
	}
	
	private void unhighlightProcessCol(int col) {
		for(int i = 1; i < processMatrix.getNrRows(); i++) {
			processMatrix.unhighlightCell(i, col, null, null);
		}
	}
	
	private void highlightProcessCol(int col) {
		for(int i = 1; i < processMatrix.getNrRows(); i++) {
			processMatrix.highlightCell(i, col, null, null);
		}
	}
	
	private void incCurrentTime(){
		currentTime++;
		currentTimeText.setText(trl.translateMessage("CURR_TIME") + ": " + currentTime, null, defaultDuration);
	}

	public int sumOfWork() {
		int sum = 0;
		for (Process p : inc_procs) {
			sum += p.work;
		}
		return sum;
	}

    public String getName() {
        return "Multilevel Queue";
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
 +"		IF queueList[i].first() != null"
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
 +"		run(queue.first())"
 +"\n"
 +"		IF queue.first().work == 0"
 +"\n"
 +"			queue.removeFirst()"
 +"\n"
 +"		ELSE"
 +"\n"
 +"			if(queue.useRoundRobin && queue.size > 1)"
 +"\n"
 +"				temp = queue.first()"
 +"\n"
 +"				queue.removeFirst()"
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
    
	/**
	 * A Process is an object that arrives at a given time in a predefined queue
	 * to schedule the given amount of work. 
	 */
	public class Process {
		/** The name of this process. */
		public String name;
		/** Queue-ID of this process. */
		public int queue;
		/** The number of time-slices this process needs */
		public int work;
		/** The number of steps after which this process arrives */
		public int arrival;
		/** The row in process matrix of this process */
		public int row;
		/**
		 * Constructs a new Process.
		 * 
		 * @param	name		Name of the process.
		 * @param	queue		Predefined queue of this process
		 * @param	work		The number of time slices, this process needs
		 * 						to be scheduled
		 * @param	arrival		The number of time slices after which the
		 * 						process arrives.
		 * @param col 
		 */
		public Process(String name, int queue, int work, int arrival, int col) {
			super();
			this.name = name;
			this.queue = queue;
			this.work = work;
			this.arrival = arrival;
			this.row = col;
		}
		
		public void highlightWork() {
			processMatrix.highlightCell(row, 2, null, null);
		}
		
		public void unhighlightWork() {
			processMatrix.unhighlightCell(row, 2, null, null);
		}
		
		public void run() {
			work--;
			schedulingOrder += name;
			computingSteps++;
			highlightWork();
			processMatrix.put(row,2,Integer.toString(work),null,null);
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
		private Process[] procs;
		/** true if this queue uses RoundRobin-Scheduling */
		public boolean useRoundRobin;
		/** pointer to the queues head */
		private int head;
		/** pointer to the queues tail */
		private int tail;
		/** number of processes in buffer */
		private int numberOfProcesses;
		/** view of this queue */
		public StringArray view;
		/** Label of this Queue */
		public Text label;
		/** headmarker */
		public ArrayMarker headMarker;
		/** tailmarker */
		public ArrayMarker tailMarker;
		
		/**
		 * Constructs a new Queue.
		 * 
		 * @param	name	The name of the Queue.
		 * @param	useRR	Specifies if this queue schedules in
		 * 					RoundRobin-mode.
		 */
		public Queue(String name, boolean useRR, int size, StringArray view, Text label) {
			this.name = name;
			this.useRoundRobin = useRR;
			this.view = view;
			this.label = label;
			procs = new Process[size];
			head = 0;
			headMarker = lang.newArrayMarker(view, head, "head", null, headMarkerProperties);
			tail = 0;
			tailMarker = lang.newArrayMarker(view, tail, "tail", null, tailMarkerProperties);
			numberOfProcesses = 0;
		}
		
		public int size() {
			return procs.length;
		}
		
		public int numberOfProcesses() {
			return numberOfProcesses;
		}
		
		public void removeTail() {
			hint.setText(trl.translateMessage("REMOVE_PROCESS") + " " + procs[tail].name + " " + trl.translateMessage("FROM_QUEUE") + " " + name, null, defaultDuration);
			procs[tail] = null;
			view.put(tail, " ", null, null);
			int oldTail = tail;
			tail = (tail + 1) % procs.length;
			tailMarker.move(tail, null, null);
			numberOfProcesses--;
			lang.nextStep();
			view.unhighlightCell(oldTail, null, null);
		}
		
		public Process first() {
			// random access for access counter
			view.getData(tail);
			return procs[tail];
		}
		
		public void add(Process p) {
			procs[head] = p;
			view.highlightCell(head, null, null);
			view.put(head, p.name, null, null);
			hint.setText(trl.translateMessage("ADD_PROCESS") + " " + p.name + " " + trl.translateMessage("TO_QUEUE") + " " + name, null, defaultDuration);
			int oldHead = head;
			head = (head + 1) % procs.length;
			headMarker.move(head, null, null);
			numberOfProcesses++;
			lang.nextStep();
			view.unhighlightCell(oldHead, null, null);
		}
		
		public boolean isEmpty() {
			return numberOfProcesses == 0;
		}
		
		public void highlight() {
			label.setFont(highlightFont, null, null);
		}
		
		public void unhighlight() {
			label.setFont(defaultFont, null, null);
		}
		
		public void highlightTail() {
			view.highlightCell(tail, null, null);
		}
		
		public void unhighlightTail() {
			view.unhighlightCell(tail, null, null);
		}
	}

}