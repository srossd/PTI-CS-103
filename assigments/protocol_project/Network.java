import java.util.ArrayList;
import java.util.concurrent.*;

public class Network {
    private int meanLatency;
    private double errorRate;
    private double dropRate;

    private int sent = 0;
    private int received = 0;
    private double started;

    private ArrayList<User> users;

    private ResultsViewer viewer;

    ExecutorService pool;

    public Network() {
        this.meanLatency = 100; // milliseconds
        this.errorRate = 0.12; // probability of bit flip
        this.dropRate = 0.0001; // drop probability per character in packet

        this.users = new ArrayList<User>();

        this.pool = Executors.newFixedThreadPool(50, new ThreadFactory(){
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setPriority(Thread.MIN_PRIORITY);
                return thread;
            }
        });
    }

    public Network(int meanLatency, double errorRate, double dropRate) {
        this.meanLatency = meanLatency;
        this.errorRate = errorRate;
        this.dropRate = dropRate;

        this.users = new ArrayList<User>();
    }

    private char garble(char ch) {
        for(int i = 0; i < 8; i++)
            if(Math.random() < errorRate)
                ch ^= (1 << i);
        return ch;
    }

    private String garble(String s) {
        char[] garbled = new char[s.length()];
        for(int i = 0; i < s.length(); i++)
            garbled[i] = garble(s.charAt(i));
        return new String(garbled);
    }

    private boolean dropped(String packet) {
        return Math.random() < packet.length() * dropRate;
    }

    public void useViewer(ResultsViewer viewer) {
        this.viewer = viewer;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void send(int destinationID, int offset, String packet) {
        Runnable sender = () -> { 
            sent++;
            if(sent == 1)
                started = System.currentTimeMillis();
            viewer.addSentValue((System.currentTimeMillis() - started)/1000, sent);
            try {
                Thread.sleep(meanLatency);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            String garbledPacket = garble(packet);

            if(dropped(packet))
                return;

            viewer.receive(offset, garbledPacket);
            for(User user : users) {
                user.receive(destinationID, offset, garbledPacket);
            }
            received++;
            viewer.addReceivedValue((System.currentTimeMillis() - started)/1000, received);
        };

        pool.execute(sender);
    }

    public void endOfTransmission() {
        pool.shutdown();
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            viewer.setTime((System.currentTimeMillis() - started)/1000);
            viewer.computeAccuracy();
        }
        catch(InterruptedException e) {
            System.out.println("Program halted before statistics computed");
        }
    }
}