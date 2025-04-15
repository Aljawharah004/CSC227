package assignment2;

import java.util.ArrayList;
import java.util.Scanner;

class MemoryBlock {
    int size;
    int startAddress;
    int endAddress;
    boolean isAllocated;
    String processID;
    int internalFragmentation;

    // Constructor
    public MemoryBlock(int size, int startAddress) {
        this.size = size;
        this.startAddress = startAddress;
        this.endAddress = startAddress + size - 1; // Calculate end address based on size
        this.isAllocated = false;
        this.processID = "Null";
        this.internalFragmentation = 0;
    }
}

public class MemoryManagement {
    private static ArrayList<MemoryBlock> memoryBlocks = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static int allocationStrategy;

    public static void main(String[] args) {
        initializeMemory();
        printMemoryStatus();
        runSimulation();
    }

    private static void initializeMemory() {
        System.out.print("Enter the total number of blocks: ");
        int numBlocks = scanner.nextInt();

        System.out.println("Enter the size of each block in KB:");
        int startAddress = 0;
        for (int i = 0; i < numBlocks; i++) {
            int size = scanner.nextInt();
            memoryBlocks.add(new MemoryBlock(size, startAddress));
            startAddress += size;
        }

        System.out.print("Enter allocation strategy (1 for first-fit, 2 for best-fit, 3 for worst-fit): ");
        allocationStrategy = scanner.nextInt();

        System.out.println("Memory blocks are created...");
    }

    private static void runSimulation() {
        while (true) {
            System.out.println("============================================");
            System.out.println("1) Allocate memory blocks");
            System.out.println("2) Deallocate memory blocks");
            System.out.println("3) Print report");
            System.out.println("4) Exit");
            System.out.println("============================================");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    allocateMemory();
                    break;
                case 2:
                    deallocateMemory();
                    break;
                case 3:
                    printMemoryStatus();
                    break;
                case 4:
                    System.out.println("Exiting program...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

private static void allocateMemory() {
    System.out.print("Enter process ID and size of process: ");
    String processID = scanner.next();
    int processSize = scanner.nextInt();

    // Check if the process ID already exists in memory
    for (MemoryBlock block : memoryBlocks) {
        if (block.isAllocated && block.processID.equalsIgnoreCase(processID)) {
            System.out.println("Error: Process ID " + processID + " already allocated. Please use a unique Process ID.");
            return; // Exit the method without allocating memory
        }
    }

    MemoryBlock selectedBlock = null;

    // Debugging: Print current memory blocks and allocation strategy
    System.out.println("Allocating memory using strategy: " + (allocationStrategy == 1 ? "First-Fit" : (allocationStrategy == 2 ? "Best-Fit" : "Worst-Fit")));

    // Apply First-Fit Allocation strategy
    if (allocationStrategy == 1) {
        for (MemoryBlock block : memoryBlocks) {
            System.out.println("Checking block (Start-End: " + block.startAddress + "-" + block.endAddress + ", Size: " + block.size + ", Allocated: " + block.isAllocated + ")");
            if (!block.isAllocated && block.size >= processSize) {
                selectedBlock = block; // Found a block that fits
                break; // No need to continue searching once a valid block is found
            }
        }
    } else if (allocationStrategy == 2) { // Best-Fit Allocation strategy
        int minSize = Integer.MAX_VALUE;
        for (MemoryBlock block : memoryBlocks) {
            System.out.println("Checking block (Start-End: " + block.startAddress + "-" + block.endAddress + ", Size: " + block.size + ", Allocated: " + block.isAllocated + ")");
            if (!block.isAllocated && block.size >= processSize && block.size < minSize) {
                minSize = block.size;
                selectedBlock = block; // Found the smallest block that fits
            }
        }
    } else if (allocationStrategy == 3) { // Worst-Fit Allocation strategy
        int maxSize = -1;
        for (MemoryBlock block : memoryBlocks) {
            System.out.println("Checking block (Start-End: " + block.startAddress + "-" + block.endAddress + ", Size: " + block.size + ", Allocated: " + block.isAllocated + ")");
            if (!block.isAllocated && block.size >= processSize && block.size > maxSize) {
                maxSize = block.size;
                selectedBlock = block; // Found the largest block that fits
            }
        }
    }

    // If a valid block was found, allocate memory
    if (selectedBlock != null) {
        selectedBlock.isAllocated = true;
        selectedBlock.processID = processID;
        selectedBlock.internalFragmentation = selectedBlock.size - processSize;
        System.out.println(processID + " allocated at address " + selectedBlock.startAddress + ", and the internal fragmentation is " + selectedBlock.internalFragmentation);
    } else {
        System.out.println("Error: Not enough memory available.");
    }
}

    private static void deallocateMemory() {
        System.out.print("Enter process ID to deallocate: ");
        String processID = scanner.next();

        for (MemoryBlock block : memoryBlocks) {
            if (block.isAllocated && block.processID.equalsIgnoreCase(processID)) {
                block.isAllocated = false;
                block.processID = "Null";
                block.internalFragmentation = 0;
                System.out.println("Process " + processID + " has been deallocated.");
                return;
            }
        }
        System.out.println("Error: Process ID not found.");
    }

    private static void printMemoryStatus() {
        System.out.println("Memory blocks:");
        System.out.println("================================================================================");
        System.out.printf("%-8s %-10s %-15s %-12s %-12s %-20s%n",
                "Block#", "Size(KB)", "Start-End", "Status", "ProcessID", "InternalFragmentation");
        System.out.println("================================================================================");

        for (int i = 0; i < memoryBlocks.size(); i++) {
            MemoryBlock block = memoryBlocks.get(i);
            System.out.printf("%-8d %-10d %-15s %-12s %-12s %-20d%n",
                    i,
                    block.size,
                    block.startAddress + "-" + block.endAddress,
                    block.isAllocated ? "Allocated" : "Free",
                    block.processID,
                    block.internalFragmentation);
        }
        System.out.println("================================================================================");
    }
}