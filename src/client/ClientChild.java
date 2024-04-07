package client;

import server.Task;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ClientChild implements Runnable{
    public ObjectOutputStream out;
    public Task task;
    public Socket socket;

    public ClientChild(Task t, ObjectOutputStream out, Socket socket){
        this.task = t;
        this.out = out;
        this.socket=socket;
    }

    @Override
    public void run() {
        System.out.println("Running task");
        task.runTask();
        System.out.println("Ran task");
        System.out.println("Child requesting lock");
        synchronized (Client.outputLock){
            try {
                System.out.println("Sending task results");
                if(Client.open) {
                    out.writeObject(task);
                    System.out.println("Sent task results");
                } else {
                    System.out.println("Socket is closed");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Child freed lock");
    }
}
