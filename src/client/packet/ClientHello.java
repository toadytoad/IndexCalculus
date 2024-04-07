package client.packet;

import java.io.Serializable;

public class ClientHello implements Serializable {
    public String auth;
    public int cores;

    public ClientHello(String auth, int cores) {
        this.auth = auth;
        this.cores = cores;
    }
}
