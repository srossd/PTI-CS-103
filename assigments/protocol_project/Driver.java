import java.awt.event.*;
import java.util.concurrent.*;

public class Driver {

    public static User alice, bob;
    public static String data;
    public static void main(String[] args) {
        String filename = "images/starry_night.jpg"; // Image to transmit

        Network net = new Network(); // Create a network
        ResultsViewer results = new ResultsViewer(); // Create a window for monitoring transmissions
        net.useViewer(results); // attach the viewer to the network
        results.setImage(filename); // display the unaltered image in the viewer
        results.setAction(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                final ExecutorService executorService = Executors.newSingleThreadExecutor();

                executorService.execute(new Runnable() {
                    public void run() {
                        start();
                    }
                });
            }
        }); // tell the window what to do when the Start button is pressed

        alice = new User(1, net); // Create Alice and add her to net as user 1
        bob = new User(2, net); // Create Bob and add him to net as user 2

        data = Loader.readImage(filename); // Load image and store as a string
    }

    public static void start() {
        for(int ps = 1000; ps <= 5000; ps += 1000)
            alice.send(data, bob, ps); // Alice transmits the data to Bob
    }
}