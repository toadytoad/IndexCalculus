package client.packet;

import java.io.Serializable;

public class ClientKill implements Serializable {
    private static final ClientKill kill = new ClientKill();

    private ClientKill(){}

    public static ClientKill getKill(){
        return kill;
    }
}
