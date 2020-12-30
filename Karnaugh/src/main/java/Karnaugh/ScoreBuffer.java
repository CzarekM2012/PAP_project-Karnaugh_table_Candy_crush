package Karnaugh;

class ScoreBufferNode{
    public int value;
    public ScoreBufferNode next;

    public ScoreBufferNode(int value){
        this.value = value;
        this.next = null;
    }
};


class ScoreBuffer{
    /*FIFO buffer for holding gained score values, head points at the oldest entry
    popping happens when entries == maxEntries and a new value is entered      */

    public ScoreBufferNode head;
    public ScoreBufferNode tail;
    public int maxEntries;
    public int entries;

    public ScoreBuffer(int maxEntries){
        this.head = null;
        this.tail = null;
        this.maxEntries = maxEntries;
        this.entries = 0;
    }


    public void push_score(int value){
        if(this.head == null){
            this.head = new ScoreBufferNode(value);
            this.tail = head;
            entries = 1;
        }

        else if(entries < maxEntries){
            this.tail.next = new ScoreBufferNode(value);
            this.tail = this.tail.next;
            entries += 1;
        }

        else if(entries == maxEntries){
            this.pop_head();
            this.tail.next = new ScoreBufferNode(value);
            this.tail = this.tail.next;
        }
    }

    private int pop_head(){
        int temp = this.head.value;
        this.head = this.head.next;
        
        return temp;
    }

    public void changeMaxEntries(int newMax){
        for(int i = 0; i < (entries - newMax); i++){
            this.pop_head();
        }
    }

    public void printBuffer(){
        ScoreBufferNode currNode = head;

        while(currNode != null){
            System.out.println(currNode.value);
            currNode = currNode.next;
        }
    }

    public int[] getValuesArray(){
        int[] arr = new int[entries];
        ScoreBufferNode currNode = this.head;
        for(int i = 0; i < entries; i++){
            arr[i] = currNode.value;
            currNode = currNode.next;
        }
        return arr;
    }
}
