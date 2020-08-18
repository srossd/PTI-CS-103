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

    public void send(String data, User recipient) {
        for(int i = 0; i < data.length(); i += 1000) {
            net.send(recipient.getID(), i, data.substring(i, Math.min(i+1000, data.length())));
        }

        net.endOfTransmission();
    }

	public void receive(int destinationID, int offset, String packet) {
        return;
	}
}