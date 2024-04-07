import relations.Relation;
import relations.RelationsTask;
import server.Server;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        BigInteger p = new BigInteger("2395675847");
        BigInteger g = new BigInteger("1282476314");
        BigInteger[] factorBase = {new BigInteger("-1"), new BigInteger("2"), new BigInteger("3"), new BigInteger("5")};
        long start = 1;
        long end = 100;
        Server server = new Server(p, g, g, new BigInteger("58"));
        server.startServer();

    }
}
