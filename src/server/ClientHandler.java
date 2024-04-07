package server;

import client.packet.ClientHello;
import client.packet.ClientKill;

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
            if(available>0){
                if(!tasks.isEmpty()){
                    System.out.println("Waiting for lock");
                    synchronized (lock) {
                        System.out.println("Sending task");
                        writeObject(tasks.poll());
                    }
                    available--;
                }
            }
            try {
                if(in.available()>0){ //TODO add an exit sequence here.
                    Object o = readObject();
                    if(o instanceof ClientKill){
                        synchronized (server.clientLock){
                            server.clients.remove(this);
                        }
                        break;
                    }
                    else if(o instanceof Task) {
                        synchronized (server.lock) {
                            server.results.add((Task) o);
                            available += 1;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        


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
