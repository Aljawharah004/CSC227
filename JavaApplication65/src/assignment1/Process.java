package assignment1;

import java.util.*;

class Process {
    int id, arrivalTime, burstTime, remainingTime, startTime = -1, finishTime;

    Process(int id, int arrivalTime, int burstTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }
}

