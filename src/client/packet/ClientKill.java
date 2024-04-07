package client.packet;

public class ClientKill {
    private static final ClientKill kill = new ClientKill();

    private ClientKill(){}

    public static ClientKill getKill(){
        return kill;
    }
}
