package server;


import java.io.Serializable;

public abstract class Task implements Serializable {
    public abstract void runTask();
}
