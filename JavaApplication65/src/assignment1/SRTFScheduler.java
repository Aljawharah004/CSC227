package assignment1;


import assignment1.Process;
import assignment1.Event;
import java.util.*;

public class SRTFScheduler { 
    
    public static void main(String[] args) {
        // Create scanner object for user input
        Scanner sc = new Scanner(System.in);
        
        // Get number of processes from the user
        System.out.print("Enter the number of processes: ");
        int n = sc.nextInt();
        Process[] processes = new Process[n];

        // Collect arrival and burst times for each process
        for (int i = 0; i < n; i++) {
            System.out.print("Enter arrival time for P" + (i + 1) + ": ");
            int arrivalTime = sc.nextInt();
            System.out.print("Enter burst time for P" + (i + 1) + ": ");
            int burstTime = sc.nextInt();
            processes[i] = new Process(i + 1, arrivalTime, burstTime);
        }

        // Sort processes by arrival time
        Arrays.sort(processes, Comparator.comparingInt(p -> p.arrivalTime));

        // Print out the number of processes and their arrival and burst times
        System.out.println("\nNumber of processes = " + n + " (P1, P2, P3, P4)");
        System.out.println("Arrival times and burst times as follows:");
        for (Process p : processes) {
            System.out.println("P" + p.id + ": Arrival time = " + p.arrivalTime + ", Burst time = " + p.burstTime + " ms");
        }

        // Start the simulation for SRTF scheduling
        simulateSRTF(processes, n);
    }

    private static void simulateSRTF(Process[] processes, int n) {
        PriorityQueue<Event> eventQueue = new PriorityQueue<>(Comparator.comparingInt(e -> e.time));
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(
            Comparator.comparingInt((Process p) -> p.remainingTime)
                      .thenComparingInt(p -> p.arrivalTime)
                      .thenComparingInt(p -> p.id)
        );

        int currentTime = 0, completedProcesses = 0, contextSwitchTime = 1;
        int totalCPUTime = 0, totalIdleTime = 0;
        Process lastProcess = null;
        Integer executionStart = null;

        // Initialize event queue with arrival events
        for (Process p : processes) {
            eventQueue.add(new Event(p.arrivalTime, "ARRIVAL", p));
        }

        System.out.println("\nScheduling Algorithm: Shortest remaining time first");
        System.out.println("Context Switch: " + contextSwitchTime + " ms");
        System.out.println("Time\tProcess/CS");

        while (completedProcesses < n) {
            // Add newly arrived processes to the ready queue
            while (!eventQueue.isEmpty() && eventQueue.peek().time <= currentTime) {
                Event event = eventQueue.poll();
                if ("ARRIVAL".equals(event.type)) {
                    readyQueue.add(event.process);
                }
            }

            if (readyQueue.isEmpty()) {
                System.out.println(currentTime + "-" + (currentTime + 1) + "\tIdle");
                totalIdleTime++;
                currentTime++;
                continue;
            }

            Process currentProcess = readyQueue.poll();

            // Set start time if this is the first time the process is executed
            if (currentProcess.startTime == -1) {
                currentProcess.startTime = currentTime;
            }

            // Handle context switch if switching processes
            if (lastProcess != null && lastProcess != currentProcess) {
                if (executionStart != null) {
                    System.out.println(executionStart + "-" + currentTime + "\tP" + lastProcess.id);
                }
                System.out.println(currentTime + "-" + (currentTime + contextSwitchTime) + "\tCS");
                currentTime += contextSwitchTime;
                totalIdleTime += contextSwitchTime;

                // Re-check for newly arrived processes during the context switch
                while (!eventQueue.isEmpty() && eventQueue.peek().time <= currentTime) {
                    Event event = eventQueue.poll();
                    if ("ARRIVAL".equals(event.type)) {
                        readyQueue.add(event.process);
                    }
                }

                // If a new process with a shorter remaining time has arrived, preempt the current process
                if (!readyQueue.isEmpty() && readyQueue.peek().remainingTime < currentProcess.remainingTime) {
                    readyQueue.add(currentProcess); // Re-add the current process to the queue
                    currentProcess = readyQueue.poll(); // Get the new shortest process
                    System.out.println(currentTime + "-" + (currentTime + contextSwitchTime) + "\tCS");
                    currentTime += contextSwitchTime;
                    totalIdleTime += contextSwitchTime;
                }

                executionStart = currentTime;
            }

            if (executionStart == null || lastProcess != currentProcess) {
                executionStart = currentTime;
            }

            // Execute the process for 1ms
            currentProcess.remainingTime--;
            totalCPUTime++;
            currentTime++;

            // If the process finishes, log its completion
            if (currentProcess.remainingTime == 0) {
                if (executionStart != null) {
                    System.out.println(executionStart + "-" + currentTime + "\tP" + currentProcess.id);
                }
                currentProcess.finishTime = currentTime;
                completedProcesses++;
                executionStart = null;
            } else {
                // Re-add the process to the queue if it hasn't finished
                readyQueue.add(currentProcess);
            }

            lastProcess = currentProcess;
        }

        calculatePerformanceMetrics(processes, n, totalCPUTime, totalIdleTime);
    }

    private static void calculatePerformanceMetrics(Process[] processes, int n, int totalCPUTime, int totalIdleTime) {
        int totalTurnaroundTime = 0, totalWaitingTime = 0;
        for (Process p : processes) {
            int turnaroundTime = p.finishTime - p.arrivalTime;
            int waitingTime = turnaroundTime - p.burstTime;
            totalTurnaroundTime += turnaroundTime;
            totalWaitingTime += waitingTime;
        }

        double avgTurnaroundTime = (double) totalTurnaroundTime / n;
        double avgWaitingTime = (double) totalWaitingTime / n;
        double cpuUtilization = (double) totalCPUTime / (totalCPUTime + totalIdleTime) * 100;

        // Print performance metrics
        System.out.println("\nPerformance Metrics");
        System.out.printf("Average Turnaround Time: %.2f\n", avgTurnaroundTime);
        System.out.printf("Average Waiting Time: %.2f\n", avgWaitingTime);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtilization);
    }
}