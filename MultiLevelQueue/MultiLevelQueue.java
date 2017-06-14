
public class MultiLevelQueue {

	private Process[] processes;
	private Queue[] queues;

	public MultiLevelQueue(Queue[] queues, Process[] processes) {
		this.processes = processes;
		this.queues = queues;
	}

	public static void main(String args[]) {
		Process[] myProcesses = new Process[3];
		myProcesses[0] = new Process("A", 1, 3, 0);
		myProcesses[1] = new Process("B", 0, 1, 1);
		myProcesses[2] = new Process("C", 1, 1, 2);
		Queue[] myQueues = new Queue[2];
		myQueues[0] = new Queue(5, 0, false);
		myQueues[1] = new Queue(5, 1, true);

		MultiLevelQueue mlq = new MultiLevelQueue(myQueues, myProcesses);
		mlq.schedule();
	}

	public void schedule() {
		int step = 0;
		while(hasComputationsLeft(processes)) {
			System.out.println("Starting Step " + step);
			// get arriving process
			System.out.println("Suche ankommende Prozesse");
			for(Process p: processes) {

				if(p.arrival == step) {
					Queue current = queues[p.level];
					if(current.numberOfProcesses == current.size) {
						System.out.println("Prozess " + p.name + "kommt an, aber queue ist voll, incrementiere arrival");
						p.arrival++;
					}else {
						System.out.println("Füge Prozess " + p.name + " zur Queue " + p.level + " hinzu");
						current.processes[current.numberOfProcesses] = p;
						current.numberOfProcesses++;
					}
				}
			}

			// compute Process of queue with highest priority
			Queue current = null;
			for(int i = 0; i < queues.length; i++) {

				if(queues[i].numberOfProcesses > 0) {
					System.out.println("Level " + i + " Queue ist nicht leer");
					current = queues[i];
					break;
				}
			}
			if(current == null) {
				System.out.println("Alle Queues sind leer, nichts zu bearbeiten");
			}else {
				// compute first process of queue
				System.out.println("Prozess " + current.processes[0].name + " wird ausgeführt");
				current.processes[0].computingTime--;
				// reschedule process or dismiss if done
				if(current.processes[0].computingTime == 0) {
					System.out.println("Prozess " + current.processes[0].name + " ist fertig.");
					for(int i = 0; i <= (current.numberOfProcesses - 1); i++) {
						if(i == current.size - 1) {
							current.processes[i] = null;
						}else {
							current.processes[i] = current.processes[i+1];
						}
					}
					current.numberOfProcesses--;
				}else {
					if(current.useRoundRobin) {
						Process temp = current.processes[0];
						for(int i = 0; i <= (current.numberOfProcesses - 1); i++) {
							if(i == current.size - 1) {
								current.processes[i] = null;
							}else {
								current.processes[i] = current.processes[i+1];
							}
						}
						current.processes[current.numberOfProcesses-1] = temp;
					}else {
						//NOP
					}
				}
			}
			System.out.println("Step " + step + " fertig. Status:");
			for(Process p : processes) {
				System.out.println("Process " + p.name + " level:" + p.level + " ct:" + p.computingTime + " arrival:" + p.arrival);
			}
			for(Queue q : queues) {
				System.out.print("Level " + q.level + " Queue: [");
				for(int i = 0; i < q.size; i++) {
					String name = "";
					if(q.processes[i] == null) {
						name = " ";
					}else {
						name = q.processes[i].name + " (" + q.processes[i].computingTime + ")";
					}
					if(i == q.size - 1) {
						System.out.println(name + "]");
					}else {
						System.out.print(name + "|");
					}
				}
			}
			step++;
			if(step == 100) break;

		}
	}

	public boolean hasComputationsLeft(Process[] processes) {
		for(int i = 0; i < processes.length; i++) {
			if(processes[i].computingTime > 0) {
				return true;
			}
		}
		return false;
	}
}
