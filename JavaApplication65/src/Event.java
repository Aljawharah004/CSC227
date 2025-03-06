class Event {
        int time;
        String type;
        Process process;

        public Event(int time, String type, Process process) {
            this.time = time;
            this.type = type;
            this.process = process;
        }
    }