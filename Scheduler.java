import java.io.*;
import java.util.*;

public class Scheduler {
	private static BufferedReader br;
	private ArrayList<Process> sList= new ArrayList<Process>();
	private static int[] blocktime;
	private static int[] cputime;
	private static boolean verbose = false;
	
	public static int getNextNum(BufferedReader br) throws IOException{
		int a = 0;
		char temp = 0;
		boolean check = true;
		while (check){
			temp = (char) br.read();
			if (Character.isDigit(temp)) check = false;
		}
		while (Character.isDigit(temp)){
			a = a * 10;
			a += temp - '0';
			temp = (char)br.read();
		}
		return a;
	}
	
	public int randomOS(int R){
		Random random = new Random();
		int num = random.nextInt(R) + 1;
		return num;
	}
	
	public void read(String filename) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(filename));
		int size = br.read() - '0';
		int a,b,c,io;
		for (int i = 0; i < size; i++){
			a = getNextNum(br);
			b = getNextNum(br);
			c = getNextNum(br);
			io = getNextNum(br);
			sList.add(new Process(a,b,c,io));
		}
		System.out.print("the original input is: " + size + "  ");
		for (Process k : sList){
			System.out.print(k.getA() +" " + k.getB() + " " + k.getC() + " " + k.getIO() + "  ");
		}
		System.out.print("\n");
		Collections.sort(sList);
		System.out.print("the sorted input is: " +  size + "  ");
		for (Process k : sList){
			System.out.print(k.getA() +" " + k.getB() + " " + k.getC() + " " + k.getIO() + "  ");
		}
		System.out.print("\n");
		System.out.println();
	}
	
	public void FCFS() throws Exception{
		int cpuBT = 0;
		int time = 0;
		int count = 0;
		double cpuutil = 0,ioutil = 0;
		ArrayList<Process> runTable = new ArrayList<Process>();
		boolean check = true;
		ArrayList<Process> runList = new ArrayList<Process>();
		for(Process k : sList){
			runList.add(k);
		}
		blocktime = new int[sList.size()];
		for (int i = 0; i < runList.size(); i++){
			blocktime[i] = -1;
		}
		if (verbose){
			System.out.print("Before Cycle    " + time + ":");
			for (int i = 0; i < runList.size(); i++){
				System.out.print("unstarted 0" + "\t");	
			}
		}
		System.out.print("\n");
		while (check){
			for (Process k : runList){
				if (k.getA() == time){
					runTable.add(k);
					k.setState(1);
				}
			}
			time ++;
			for (int i = 0; i < blocktime.length; i++){
				if (blocktime[i] == 0){
					runTable.add(runList.get(i));
					blocktime[i] = -1;
					runList.get(i).setState(1);
				}
			}
			if (!runTable.isEmpty()){
				Process temp = runTable.get(0);
				if (temp.getState() == 1){
					int temp1 = randomOS(temp.getB());
					if (temp1 < temp.getC()) cpuBT = temp1;
					else cpuBT = temp.getC();
					temp.setState(2);
				}
				temp.decreaseC();
				cpuutil ++;
			}
			if (verbose){
				System.out.print("Before Cycle    " + time + ":");
				for (Process k: runList){
					if (k.getState() == 0){
						System.out.print("unstarted 0" + "\t");
					}else if(k.getState() == 1){
						System.out.print("ready     0" + "\t");
						k.incWT();
					}else if(k.getState() == 2){
						System.out.print("running   " + cpuBT + "\t");
					}else if(k.getState() == 3){
						System.out.print("blocked   " + blocktime[runList.indexOf(k)] + "\t");
					}else{
						System.out.print("finished  0" + "\t");
					}
					
				}
				System.out.print("\n");
			}
			
			for (int i = 0; i < blocktime.length; i++){
				if (blocktime[i] > 0){
					blocktime[i] --;
					runList.get(i).incIOT();
					ioutil ++;
				}
			}
			if (cpuBT > 0){
				cpuBT --;
				if (cpuBT == 0){
					Process temp = runTable.get(0);
					if (temp.getC() > 0){
						temp.setState(3);
						int temp1 = randomOS(temp.getIO());
						blocktime[runList.indexOf(temp)] = temp1;
						runTable.remove(0);
					}else{
						temp.setft(time);
						temp.setState(4);
						runTable.remove(0);
						count++;
					}
				}
			}
			if (count == runList.size()){
				check = false;
			}
		}
		System.out.println();
		System.out.println("The algorithm used is FIRST COME FIRST SERVED");
		System.out.println();
		double avgWait = 0, avgTurn = 0, max = -1;
		for(Process p:runList){
			if (p.getft() > max) max = p.getft();
			avgTurn += p.getft() - p.getA();
			avgWait += p.getWT();
			System.out.println("Process " + runList.indexOf(p) + ":");
			System.out.println("\t" +"(A,B,C,IO) = (" + p.getA() + "," + p.getB() + "," + p.getintialC() +"," + p.getIO() +")");
			System.out.println("\t" + "Finishing time: " + p.getft());
			System.out.println("\t" + "Turnaround time: " + (p.getft() - p.getA()));
			System.out.println("\t" + "I/O time: " + p.getIOT());
			System.out.println("\t" + "Waiting time: " + p.getWT());
		}
		System.out.println();
		avgWait = avgWait / runList.size();
		avgTurn = avgTurn / runList.size();
		System.out.println("Summary Data: ");
		System.out.println("\t" + "Finishing time: " + max);
		System.out.println("\t" + "CPU Utilization: " + cpuutil/max);
		System.out.println("\t" + "IO Utilization: " + ioutil/max);
		System.out.println("\t" + "Troughput " + (runList.size() / max) * 100 +" processes per hundread cycles");
		System.out.println("\t" + "Average Turnaround time: " + avgTurn);
		System.out.println("\t" + "Average Wait time: " + avgWait);
		System.out.println();
	}
	
	public void RR(){
		int time = 0,count = 0,cpuBT = 0, quantum = 2;
		double cpuutil = 0, ioutil = 0;
		boolean check = true;
		ArrayList<Process> runList = new ArrayList<Process>();
		for(Process k : sList){
			runList.add(k);
		}
		Process running = null;
		ArrayList<Process> readyTable = new ArrayList<Process>();
		blocktime = new int[sList.size()];
		cputime = new int[sList.size()];
		for (int i = 0; i < sList.size(); i++){
			blocktime[i] = -1;
			cputime[i] = 0;
		}
		if (verbose){
			System.out.print("Before Cycle    " + time + ":");
			for (int i = 0; i < runList.size(); i++){
				System.out.print("unstarted 0" + "\t");
			}
		}
		System.out.print("\n");
		while (check){
			for (int i = 0; i < blocktime.length; i++){
				if (blocktime[i] == 0){
					readyTable.add(runList.get(i));
					blocktime[i] = -1;
					runList.get(i).setState(1);
				}
			}
			for (Process k : runList){
				if (k.getA() == time){
					readyTable.add(k);
					k.setState(1);
				}
			}
			time ++;
			if (running != null){
				running.decreaseC();
				cpuutil ++;
			}else if(!readyTable.isEmpty()){
				int min = 99999;
				// the following step choose the earliest arrival process, but the sample output didn't follow this rule
				Process temp = null;
				for (Process p : readyTable){
					if ((runList.indexOf(p)) < min){
						temp = p;
						min = runList.indexOf(p);
					}
				}
				running = readyTable.get(0);
				readyTable.remove(readyTable.indexOf(running));
				running.setState(2);
				if (cputime[runList.indexOf(running)] == 0){
					int temp1 = randomOS(running.getB());
					if (temp1 > running.getC()){
						temp1 = running.getC();
					}
					cputime[runList.indexOf(running)] = temp1;
					if (temp1 > quantum) cpuBT = quantum;
					else cpuBT = temp1;
				}else{
					if (cputime[runList.indexOf(running)] > 2){
						cpuBT = 2;
					}else{
						cpuBT = cputime[runList.indexOf(running)];
					}
				}
				running.decreaseC();
				cpuutil ++;
			}
			if (verbose){
				System.out.print("Before Cycle    " + time + ":");
				for (Process k: runList){
					if (k.getState() == 0){
						System.out.print("unstarted 0" + "\t");
					}else if(k.getState() == 1){
						System.out.print("ready     0" + "\t");
						k.incWT();
					}else if(k.getState() == 2){
						System.out.print("running   " + cpuBT + "\t");
					}else if(k.getState() == 3){
						System.out.print("blocked   " + blocktime[runList.indexOf(k)] + "\t");
					}else{
						System.out.print("finished  0" + "\t");
					}
					
				}
				System.out.print("\n");
			}
			
			for (int i = 0; i < blocktime.length; i++){
				if (blocktime[i] > 0){
					blocktime[i] --;
					runList.get(i).incIOT();
					ioutil ++;
				}
			}
			if (cpuBT > 0){
				cpuBT --;
				cputime[runList.indexOf(running)] --;
				if (cpuBT == 0){
					if (cputime[runList.indexOf(running)] == 0){
						Process temp = running;
						if (temp.getC() > 0){
							temp.setState(3);
							int temp1 = randomOS(temp.getIO());
							blocktime[runList.indexOf(temp)] = temp1;
							running = null;
						}else{
							temp.setft(time);
							temp.setState(4);
							running = null;
							count++;
						}
					}else{
						Process temp = running;
						temp.setState(1);
						running = null;
						readyTable.add(temp);
					}
				}
			}
			if(count == runList.size()) check = false;
		}
		System.out.println();
		System.out.println("The algorithm used is ROUND ROBIN");
		System.out.println();
		double avgWait = 0, avgTurn = 0, max = -1;
		for(Process p:runList){
			if (p.getft() > max) max = p.getft();
			avgTurn += p.getft() - p.getA();
			avgWait += p.getWT();
			System.out.println("Process " + runList.indexOf(p) + ":");
			System.out.println("\t" +"(A,B,C,IO) = (" + p.getA() + "," + p.getB() + "," + p.getintialC() +"," + p.getIO() +")");
			System.out.println("\t" + "Finishing time: " + p.getft());
			System.out.println("\t" + "Turnaround time: " + (p.getft() - p.getA()));
			System.out.println("\t" + "I/O time: " + p.getIOT());
			System.out.println("\t" + "Waiting time: " + p.getWT());
		}
		System.out.println();
		avgWait = avgWait / runList.size();
		avgTurn = avgTurn / runList.size();
		System.out.println("Summary Data: ");
		System.out.println("\t" + "Finishing time: " + max);
		System.out.println("\t" + "CPU Utilization: " + cpuutil/max);
		System.out.println("\t" + "IO Utilization: " + ioutil/max);
		System.out.println("\t" + "Troughput " + (runList.size() / max) * 100 +" processes per hundread cycles");
		System.out.println("\t" + "Average Turnaround time: " + avgTurn);
		System.out.println("\t" + "Average Wait time: " + avgWait);
		System.out.println();
	}
	
	public void UNI(){
		int time = 0,count = 0,cpuBT = 0,blockTime = -1;
		double cpuutil = 0, ioutil = 0;
		boolean check = true;
		ArrayList<Process> runList = new ArrayList<Process>();
		for(Process k : sList){
			runList.add(k);
		}
		ArrayList<Process> runTable = new ArrayList<Process>();
		if (verbose){
			System.out.print("Before Cycle    " + time + ":");
			for (int i = 0; i < runList.size(); i++){
				System.out.print("unstarted 0" + "\t");
			}
		}
		System.out.print("\n");
		while (check){
			for (Process k : runList){
				if (k.getA() == time){
					runTable.add(k);
					k.setState(1);
				}
			}
			time ++;
			if (blockTime == 0){
				blockTime = -1;
				runTable.get(0).setState(1);
			}
			if (!runTable.isEmpty()){
				Process temp = runTable.get(0);
				if (temp.getState() == 1){
					int temp1 = randomOS(temp.getB());
					if (temp1 < temp.getC()) cpuBT = temp1;
					else cpuBT = temp.getC();
					temp.setState(2);
					temp.decreaseC();
					cpuutil ++;
				}else if(temp.getState() == 2){
					temp.decreaseC();
					cpuutil ++;
				}
			}
			if (verbose){
				System.out.print("Before Cycle    " + time + ":");
				for (Process k: runList){
					if (k.getState() == 0){
						System.out.print("unstarted 0" + "\t");
					}else if(k.getState() == 1){
						System.out.print("ready     0" + "\t");
						k.incWT();
					}else if(k.getState() == 2){
						System.out.print("running   " + cpuBT + "\t");
					}else if(k.getState() == 3){
						System.out.print("blocked   " + blockTime + "\t");
					}else{
						System.out.print("finished  0" + "\t");
					}	
				}
				System.out.print("\n");
			}
			
			if (blockTime > 0){
				blockTime --;
				ioutil ++;
				runTable.get(0).incIOT();
			}
			if (cpuBT > 0){
				cpuBT --;
				if (cpuBT == 0){
					Process temp = runTable.get(0);
					if (temp.getC() > 0){
						temp.setState(3);
						blockTime = randomOS(temp.getIO());
					}else{
						temp.setft(time);
						temp.setState(4);
						runTable.remove(0);
						count++;
					}
				}
			}
			if (count == runList.size()) check = false;
		}
		System.out.println();
		System.out.println("The algorithm used is Uniprogrammed");
		System.out.println();
		double avgWait = 0, avgTurn = 0, max = -1;
		for(Process p:runList){
			if (p.getft() > max) max = p.getft();
			avgTurn += p.getft() - p.getA();
			avgWait += p.getWT();
			System.out.println("Process " + runList.indexOf(p) + ":");
			System.out.println("\t" +"(A,B,C,IO) = (" + p.getA() + "," + p.getB() + "," + p.getintialC() +"," + p.getIO() +")");
			System.out.println("\t" + "Finishing time: " + p.getft());
			System.out.println("\t" + "Turnaround time: " + (p.getft() - p.getA()));
			System.out.println("\t" + "I/O time: " + p.getIOT());
			System.out.println("\t" + "Waiting time: " + p.getWT());
		}
		System.out.println();
		avgWait = avgWait / runList.size();
		avgTurn = avgTurn / runList.size();
		System.out.println("Summary Data: ");
		System.out.println("\t" + "Finishing time: " + max);
		System.out.println("\t" + "CPU Utilization: " + cpuutil/max);
		System.out.println("\t" + "IO Utilization: " + ioutil/max);
		System.out.println("\t" + "Troughput " + (runList.size() / max) * 100 +" processes per hundread cycles");
		System.out.println("\t" + "Average Turnaround time: " + avgTurn);
		System.out.println("\t" + "Average Wait time: " + avgWait);
		System.out.println();
	}
	
	public void PSJF(){
		int time = 0,count = 0,cpuBT = 0;
		double cpuutil = 0, ioutil = 0;
		boolean check = true;
		ArrayList<Process> runList = new ArrayList<Process>();
		for(Process k : sList){
			runList.add(k);
		}
		ArrayList<Process> readyTable = new ArrayList<Process>();
		Process running = null;
		blocktime = new int[runList.size()];
		for (int i = 0; i < runList.size(); i++){
			blocktime[i] = -1;
		}
		if (verbose){
			System.out.print("Before Cycle    " + time + ":");
			for (int i = 0; i < runList.size(); i++){
				System.out.print("unstarted 0" + "\t");
			}
		}
		System.out.print("\n");
		while(check){
			for (Process k : runList){
				if (k.getA() == time){
					readyTable.add(k);
					k.setState(1);
				}
			}
			time ++;
			for (int i = 0; i < blocktime.length; i++){
				if (blocktime[i] == 0){
					readyTable.add(runList.get(i));
					runList.get(i).setState(1);
					blocktime[i] = -1;
				}
			}
			if (running != null){
				running.decreaseC();
				cpuutil ++;
			}else if(!readyTable.isEmpty()){
				int min = 99999;
				Process temp = null;
				for (Process p : readyTable){
					if (p.getC() < min){
						temp = p;
						min = temp.getC();
					}
				}
				running = temp;
				readyTable.remove(readyTable.indexOf(running));
				int temp1 = randomOS(running.getB());
				if (temp1 < running.getC()) cpuBT = temp1;
				else cpuBT = running.getC();
				running.setState(2);
				running.decreaseC();
				cpuutil ++;
			}
			if (verbose){
				System.out.print("Before Cycle    " + time + ":");
				for (Process k: runList){
					if (k.getState() == 0){
						System.out.print("unstarted 0" + "\t");
					}else if(k.getState() == 1){
						System.out.print("ready     0" + "\t");
						k.incWT();
					}else if(k.getState() == 2){
						System.out.print("running   " + cpuBT + "\t");
					}else if(k.getState() == 3){
						System.out.print("blocked   " + blocktime[runList.indexOf(k)] + "\t");
					}else{
						System.out.print("finished  0" + "\t");
					}	
				}
				System.out.print("\n");
			}
			
			for (int i = 0; i < blocktime.length; i++){
				if (blocktime[i] > 0){
					blocktime[i] --;
					runList.get(i).incIOT();
					ioutil ++;
				}
			}
			if (cpuBT > 0){
				cpuBT--;
				if (cpuBT == 0){
					if (running.getC() > 0){
						running.setState(3);
						int temp1 = randomOS(running.getIO());
						blocktime[runList.indexOf(running)] = temp1;
					}else{
						running.setft(time);
						running.setState(4);
						count ++;
					}
					running = null;
				}
			}
			if (count == runList.size()) check = false;
		}
		System.out.println();
		System.out.println("The algorithm used is Preemptive Shortest Job First");
		System.out.println();
		double avgWait = 0, avgTurn = 0, max = -1;
		for(Process p:runList){
			if (p.getft() > max) max = p.getft();
			avgTurn += p.getft() - p.getA();
			avgWait += p.getWT();
			System.out.println("Process " + runList.indexOf(p) + ":");
			System.out.println("\t" +"(A,B,C,IO) = (" + p.getA() + "," + p.getB() + "," + p.getintialC() +"," + p.getIO() +")");
			System.out.println("\t" + "Finishing time: " + p.getft());
			System.out.println("\t" + "Turnaround time: " + (p.getft() - p.getA()));
			System.out.println("\t" + "I/O time: " + p.getIOT());
			System.out.println("\t" + "Waiting time: " + p.getWT());
		}
		System.out.println();
		avgWait = avgWait / runList.size();
		avgTurn = avgTurn / runList.size();
		System.out.println("Summary Data: ");
		System.out.println("\t" + "Finishing time: " + max);
		System.out.println("\t" + "CPU Utilization: " + cpuutil/max);
		System.out.println("\t" + "IO Utilization: " + ioutil/max);
		System.out.println("\t" + "Troughput " + (runList.size() / max) * 100 +" processes per hundread cycles");
		System.out.println("\t" + "Average Turnaround time: " + avgTurn);
		System.out.println("\t" + "Average Wait time: " + avgWait);
		System.out.println();
	}
	
	public static void main(String args[]) throws Exception{
		Scheduler s = new Scheduler();
		if (args.length == 2){
			verbose = true;
			s.read(args[1]);
		}else{
			s.read(args[0]);
		}
		System.out.println("Please select scheduling algorithm");
		System.out.println("1.FCFS 2.RR 3.UNI 4.PSJN ");
		Scanner in = new Scanner(System.in);
		String test = in.next();
		if (test.equals("1")) s.FCFS();
		else if (test.equals("2")) s.RR();
		else if (test.equals("3")) s.UNI();
		else if (test.equals("4")) s.PSJF();
		else System.out.println("invalid input");
	}
	
}
