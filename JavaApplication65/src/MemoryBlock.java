class MemoryBlock {
    int size;
    int startAddress;
    int endAddress;
    boolean isAllocated;
    String processID;
    int internalFragmentation;

    public MemoryBlock(int size, int startAddress) {
        this.size = size;
        this.startAddress = startAddress;
        this.endAddress = startAddress + size - 1;
        this.isAllocated = false;
        this.processID = "Null";
        this.internalFragmentation = 0;
    }
}