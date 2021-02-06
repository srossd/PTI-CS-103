public class User {
    private int ID;
    private Network net;

    public User(int ID, Network net) {
        this.ID = ID;
        this.net = net;

        net.addUser(this);
    }

    public int getID() {
        return ID;
    }

    public void send(String data, User recipient, int packetSize) {
        net.reset(packetSize);
        
        for(int i = 0; i < data.length(); i += packetSize) {
            net.send(recipient.getID(), i, data.substring(i, Math.min(i+packetSize, data.length())));
        }

        net.endOfTransmission(packetSize);
    }
}