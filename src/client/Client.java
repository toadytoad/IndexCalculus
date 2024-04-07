package client;

import client.packet.ClientHello;
import client.packet.ClientKill;
import client.packet.KillAcknowledge;
import relations.RelationsTask;
import server.KillTask;
import server.Task;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Client {
    static ThreadPoolExecutor pool;
    static final Object lock = new Object();
    static final Object outputLock = new Object();
    public static boolean open;
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket clientSocket = new Socket("127.0.0.1", 26979);
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        ClientHello hello = new ClientHello("mysupersecurepassword", 8);
        out.writeObject(hello);
        open = true;
        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);


        for(;;) {
            Task task;
            task = (Task) (in.readObject());
            if(task instanceof KillTask){
                System.out.println("Received Kill Task!");
                break;
            } else if (task instanceof RelationsTask) {
                synchronized (lock){
                    pool.execute(new ClientChild(task, out, clientSocket));
                }
            }
        }
        open = false;
        System.out.println("Sending kill");
        synchronized (outputLock) {
            out.writeObject(ClientKill.getKill());
        }
        System.out.println("Freed client outputLock");
        System.out.println(in.readObject());
        System.out.println("Recieved KillAck");
        out.close();

        pool.close();
    }
}
