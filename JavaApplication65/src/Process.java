import java.util.*;

class Process {
    int id, arrivalTime, burstTime, remainingTime, startTime, finishTime;

    public Process(int id, int arrivalTime, int burstTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.startTime = -1;
        this.finishTime = -1;
    }
}
