import java.util.LinkedList;

public class Queue {
	
	public String name;
	public LinkedList<Process> procs;
	public boolean useRoundRobin;
	
	public Queue(String name, boolean useRoundRobin) {
		this.name = name;
		this.useRoundRobin = useRoundRobin;
		procs = new LinkedList<Process>();
	}
}
