package server;

import client.packet.ClientHello;
import client.packet.ClientKill;
import client.packet.KillAcknowledge;

import java.io.*;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Queue;

public class ClientHandler extends Thread{
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    public final Object lock;
    public Queue<Task> tasks;
    public boolean inUse;
    public int workers;
    public int available;
    private final Server server;
    public final int id;

    public ClientHandler(Socket socket, Server server, int id) {
        this.socket = socket;
        this.lock = new Object();
        this.inUse = false;
        this.tasks = new ArrayDeque<>();
        this.server = server;
        this.id = id;
    }

    public void run(){
        System.out.println("Started ClientHandler "+id);
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Obtained out");
        try {
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ClientHello hello = (ClientHello)readObject();
        assert hello != null;
        if(!hello.auth.equals(Server.auth)){
            System.out.println("Authentication Failed");
            return;
        }
        workers = hello.cores;
        System.out.println("Obtained params");
        available = workers;
        while(true){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(available>0&&!tasks.isEmpty()){
                    System.out.println("Waiting for lock");
                    synchronized (lock) {
                        System.out.println("Sending task");
                        System.out.println(tasks.peek().getClass().toString());
                        writeObject(tasks.poll());
                    }
                    available--;
            } else {

                Object o = readObject();
                System.out.println(o.getClass().toString());
                if (o instanceof ClientKill) {
                    System.out.println("Killing!");
                    synchronized (server.clientLock) {
                        server.clients.remove(this);
                    }
                    try {
                        out.writeObject(new KillAcknowledge());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                } else if (o instanceof Task) {
                    System.out.println("Recieved Task on client " + id);
                    synchronized (server.lock) {
                        server.results.add((Task) o);
                        available += 1;
                    }
                    System.out.println("Freed server lock");
                }

            }
        }

        System.out.println("Finished Client "+id);
    }

    private Object readObject(){
        try {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }
    private void writeObject(Object o){
        try {
            out.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
