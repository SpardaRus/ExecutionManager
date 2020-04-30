package com.spardarus.task;


public class Task implements Runnable {

    private long id;

    public Task(long id) {
        this.id = id;
    }

    public void run() {
        System.out.println(getTaskInfo() + " is run");
        if (id == 2 || id == 5) {
            throw new RuntimeException("Test Exception");
        } else {
            System.out.println(getTaskInfo() + " is work");
        }
        System.out.println(getTaskInfo() + " is done");
    }

    private String getTaskInfo() {
        return "" + Thread.currentThread() + " id: " + id;
    }
}
