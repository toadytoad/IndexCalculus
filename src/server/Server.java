package server;

import client.packet.ClientKill;
import relations.PartialRelation;
import relations.RelationsTask;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    public Queue<Task> results = new ArrayDeque<>();
    public final Object lock = new Object();
    public final Object clientLock = new Object();
    public static String auth = "mysupersecurepassword";
    private ServerSocket serverSocket;
    public List<ClientHandler> clients;
    BigInteger p, g, h;
    BigInteger[] factorBase;
    public Server(BigInteger p, BigInteger g, BigInteger h, BigInteger b){
        this.p = p;
        this.g = g;
        this.h = h;
        this.clients = new ArrayList<>();
        this.factorBase = getFactorBase(b);
    }
    static BigInteger[] getFactorBase(BigInteger b){
        List<BigInteger> base = new ArrayList<>();
        base.add(new BigInteger("-1"));
        for(BigInteger x = BigInteger.TWO; x.compareTo(b)<0; x = x.add(BigInteger.ONE)){
            if(x.isProbablePrime(50)){
                base.add(x);
            }
        }
        BigInteger[] res = new BigInteger[base.size()];
        for(int i = 0;i<base.size(); i++){
            res[i] = base.get(i);
        }
        return res;
    }
    public void startServer() throws IOException, InterruptedException {
        serverSocket = new ServerSocket(26979);
        ConnectionHandler handler = new ConnectionHandler(this);
        handler.start();
        long start = 0;
        long increment = 100000;
        long numRelations = 0;
        System.out.println("Server starting tasks loop");
        while(numRelations<1000){
            Thread.sleep(100);
            //System.out.println(results.isEmpty());
            if(results.isEmpty()&&!clients.isEmpty()){
                synchronized (clientLock) {
                    for (ClientHandler c : clients) {
                        if (c.tasks.isEmpty()) {
                            System.out.println("Server waiting for lock");
                            synchronized (c.lock) {
                                System.out.println("Server obtained lock");
                                c.tasks.add(new RelationsTask(p, g, factorBase, start, start + increment));
                                start += increment;
                            }
                        }
                    }
                }
            } else if (!results.isEmpty()){
                //TODO actually handle the relations here
                System.out.println("Got a result");
                synchronized (lock) {
                    numRelations += ((RelationsTask) results.poll()).res.fullRelations.size();
                }
                System.out.println(numRelations+" relations.");
            }

        }
        synchronized (clientLock){
            for(ClientHandler c : clients){
                synchronized (c.lock) {
                    c.tasks.clear();
                    c.tasks.add(new KillTask());
                }
            }
        }
        System.out.println("Sent kill tasks");
        while(true){
            Thread.sleep(200);
            synchronized (clientLock){
                if(clients.isEmpty()) break;
            }
        }
        System.out.println("Clients finished");
        serverSocket.close();
        System.out.println("Finished!");
        for(Thread t : Thread.getAllStackTraces().keySet()){
            System.out.println(t);
        }
    }
    static class ConnectionHandler extends Thread{
        Server server;

        public ConnectionHandler(Server server) {
            this.server = server;
        }

        public void run(){
            int id = 0;
            while(!server.serverSocket.isClosed()){
                try {
                    Socket connection = server.serverSocket.accept();
                    synchronized (server.clientLock) {
                        server.clients.add(new ClientHandler(connection, this.server, id));
                        server.clients.get(server.clients.size() - 1).start();
                        System.out.println("Added a new client!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                id++;
            }
        }
    }
}
