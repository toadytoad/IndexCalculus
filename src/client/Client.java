package client;

import relations.RelationsTask;

import java.io.*;
import java.net.Socket;

public class Client {


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket clientSocket = new Socket("127.0.0.1", 26979);
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        assert in.readLine().equals("Hello");
        out.write("mysupersecurepassword\n1\n");
        out.flush();
        ObjectInputStream stream = new ObjectInputStream(clientSocket.getInputStream());
        stream.skip(6);
        RelationsTask task = (RelationsTask)(new ObjectInputStream(clientSocket.getInputStream()).readObject());
        task.runTask();

    }
}
