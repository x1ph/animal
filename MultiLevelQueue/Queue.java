
public class Queue {
	
	public int size;
	public int level;
	public Process[] processes;
	public int numberOfProcesses;
	public boolean useRoundRobin;
	
	public Queue(int size, int level, boolean useRoundRobin) {
		this.size = size;
		this.level = level;
		this.useRoundRobin = useRoundRobin;
		this.processes = new Process[size];
		this.numberOfProcesses = 0;
	}
}
