


import java.util.*;

public class SRTFScheduler {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Prompt user for the number of processes
        System.out.print("Enter the number of processes: ");
        int n = sc.nextInt();

        // Create an array to store processes
        Process[] processes = new Process[n];

        // Prompt user for arrival and burst times
        for (int i = 0; i < n; i++) {
            System.out.print("Enter arrival time for P" + (i + 1) + ": ");
            int arrivalTime = sc.nextInt();
            System.out.print("Enter burst time for P" + (i + 1) + ": ");
            int burstTime = sc.nextInt();
            processes[i] = new Process(i + 1, arrivalTime, burstTime);
        }

        // Display the input in the specified format
        System.out.println("\nNumber of processes= " + n + " (" + getProcessNames(n) + ")");
        System.out.println("Arrival times and burst times as follows:");
        for (Process p : processes) {
            System.out.println("P" + p.id + ": Arrival time = " + p.arrivalTime + ", Burst time = " + p.burstTime + " ms");
        }

        // Sort processes by arrival time
        Arrays.sort(processes, Comparator.comparingInt(p -> p.arrivalTime));

        // Simulate SRTF scheduling
        simulateSRTF(processes, n);
    }

    // Helper method to generate process names (P1, P2, ..., Pn)
    private static String getProcessNames(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            sb.append("P").append(i);
            if (i < n) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private static void simulateSRTF(Process[] processes, int n) {
        int currentTime = 0;
        int completedProcesses = 0;
        int contextSwitchTime = 1; // 1 ms for context switch
        int totalCPUTime = 0;
        int totalIdleTime = 0;

        // Priority queue to select the process with the shortest remaining time
        PriorityQueue<Process> queue = new PriorityQueue<>(
                Comparator.comparingInt(p -> p.remainingTime)
        );

        System.out.println("\nScheduling Algorithm: Shortest Remaining Time First");
        System.out.println("Context Switch: " + contextSwitchTime + " ms");
        System.out.println("Time\tProcess/CS");

        while (completedProcesses < n) {
            // Add processes that have arrived by the current time
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && p.remainingTime > 0 && !queue.contains(p)) {
                    queue.add(p);
                }
            }

            if (queue.isEmpty()) {
                // No processes are ready, CPU is idle
                totalIdleTime++;
                currentTime++;
                continue;
            }

            // Get the process with the shortest remaining time
            Process currentProcess = queue.poll();

            // If this is the first time the process is running, set its start time
            if (currentProcess.startTime == -1) {
                currentProcess.startTime = currentTime;
            }

            // Execute the process for 1 unit of time
            currentProcess.remainingTime--;
            totalCPUTime++;
            currentTime++;

            // Print the current time and process
            System.out.println((currentTime - 1) + "-" + currentTime + "\tP" + currentProcess.id);

            // If the process has completed
            if (currentProcess.remainingTime == 0) {
                currentProcess.finishTime = currentTime;
                completedProcesses++;

                // Add context switch time if there are more processes to run
                if (completedProcesses < n) {
                    currentTime += contextSwitchTime;
                    totalIdleTime += contextSwitchTime;
                    System.out.println((currentTime - contextSwitchTime) + "-" + currentTime + "\tCS");
                }
            }
        }

        // Calculate performance metrics
        int totalTurnaroundTime = 0;
        int totalWaitingTime = 0;

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
        System.out.println("Average Turnaround Time: " + avgTurnaroundTime);
        System.out.println("Average Waiting Time: " + avgWaitingTime);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtilization);
    }
}