public class Process {
	public String name;
	public Queue queue;
	public int computingTime;
	public int arrival;
	
	public Process(String name, Queue queue, int computingTime, int arrival) {
		super();
		this.name = name;
		this.queue = queue;
		this.computingTime = computingTime;
		this.arrival = arrival;
	}
}
