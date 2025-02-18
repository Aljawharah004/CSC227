
import java.util.*;

public class SRTFScheduler {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the number of processes: ");
        int n = sc.nextInt();
        Process[] processes = new Process[n];

        for (int i = 0; i < n; i++) {
            System.out.print("Enter arrival time for P" + (i + 1) + ": ");
            int arrivalTime = sc.nextInt();
            System.out.print("Enter burst time for P" + (i + 1) + ": ");
            int burstTime = sc.nextInt();
            processes[i] = new Process(i + 1, arrivalTime, burstTime);
        }

        Arrays.sort(processes, Comparator.comparingInt(p -> p.arrivalTime));
        simulateSRTF(processes, n);
    }

    private static void simulateSRTF(Process[] processes, int n) {
        int currentTime = 0, completedProcesses = 0, contextSwitchTime = 1;
        int totalCPUTime = 0, totalIdleTime = 0;
        PriorityQueue<Process> queue = new PriorityQueue<>(Comparator.comparingInt(p -> p.remainingTime));
        Process lastProcess = null;
        Integer executionStart = null;

        System.out.println("\nScheduling Algorithm: Shortest Remaining Time First");
        System.out.println("Context Switch: " + contextSwitchTime + " ms");
        System.out.println("Time\tProcess/CS");

        while (completedProcesses < n) {
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && p.remainingTime > 0 && !queue.contains(p)) {
                    queue.add(p);
                }
            }

            if (queue.isEmpty()) {
                totalIdleTime++;
                currentTime++;
                continue;
            }

            Process currentProcess = queue.poll();
            if (currentProcess.startTime == -1) {
                currentProcess.startTime = currentTime;
            }

            if (lastProcess != null && lastProcess != currentProcess) {
                if (executionStart != null) {
                    System.out.println(executionStart + "-" + currentTime + "\tP" + lastProcess.id);
                }
                System.out.println(currentTime + "-" + (currentTime + contextSwitchTime) + "\tCS");
                currentTime += contextSwitchTime;
                totalIdleTime += contextSwitchTime;
                executionStart = currentTime;
            }

            if (executionStart == null || lastProcess != currentProcess) {
                executionStart = currentTime;
            }

            currentProcess.remainingTime--;
            totalCPUTime++;
            currentTime++;
            lastProcess = currentProcess;

            if (currentProcess.remainingTime == 0) {
                if (executionStart != null) {
                    System.out.println(executionStart + "-" + currentTime + "\tP" + currentProcess.id);
                }
                currentProcess.finishTime = currentTime;
                completedProcesses++;
                executionStart = null;
            }
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

        System.out.println("\nPerformance Metrics");
        System.out.printf("Average Turnaround Time: %.2f\n", avgTurnaroundTime);
        System.out.printf("Average Waiting Time: %.2f\n", avgWaitingTime);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtilization);
    }
}
