public class Driver {
    public static void main(String[] args) {
        String filename = "images/starry_night.jpg"; // Image to transmit

        Network net = new Network(); // Create a network
        ResultsViewer results = new ResultsViewer(); // Create a window for monitoring transmissions
        net.useViewer(results); // attach the viewer to the network
        results.setImage(filename); // display the unaltered image in the viewer

        User alice = new User(1, net); // Create Alice and add her to net as user 1
        User bob = new User(2, net); // Create Bob and add him to net as user 2

        String data = Loader.readImage(filename); // Load image and store as a string
        
        alice.send(data, bob); // Alice transmits the data to Bob
    }
}