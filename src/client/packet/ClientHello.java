package client.packet;

public class ClientHello {
    public String auth;
    public int cores;

    public ClientHello(String auth, int cores) {
        this.auth = auth;
        this.cores = cores;
    }
}
