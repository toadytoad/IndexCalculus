package client;

import client.packet.ClientHello;
import client.packet.ClientKill;
import relations.RelationsTask;
import server.KillTask;
import server.Task;

import java.io.*;
import java.net.Socket;

public class Client {


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket clientSocket = new Socket("127.0.0.1", 26979);
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        ClientHello hello = new ClientHello("mysupersecurepassword", 1);
        out.writeObject(hello);
        for(;;) {
            Task task = (Task) (in.readObject());
            if(task instanceof KillTask){
                break;
            } else if (task instanceof RelationsTask) {
                task.runTask();
                System.out.println("Finished Task");
                System.out.println(((RelationsTask)task).res.fullRelations.size());
                out.writeObject(task);
                out.flush();
            }
        }
        out.writeObject(ClientKill.getKill());

    }
}
