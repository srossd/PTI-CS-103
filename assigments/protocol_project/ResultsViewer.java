import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.*;

import javafx.application.Platform;
import javafx.animation.AnimationTimer;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javax.swing.JFrame;

public class ResultsViewer extends JFrame {

    private static final long serialVersionUID = 2047524583583651892L;

    private JLabel receivedImageLabel;
    private byte[] receivedImagePixels;
    private byte[] sentImagePixels;

    private int width;
    private int height;

    private XYChart.Series<Number,Number> sentSeries = new XYChart.Series<>();
    private XYChart.Series<Number,Number> receivedSeries = new XYChart.Series<>();

    
    private ConcurrentLinkedQueue<Number> sentTime = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Number> sentNumber = new ConcurrentLinkedQueue<>();

    private ConcurrentLinkedQueue<Number> receivedTime = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Number> receivedNumber = new ConcurrentLinkedQueue<>();

    private JLabel time;
    private JLabel accuracy;

    public ResultsViewer() {
        setTitle("Network Viewer");
        setSize(1700, 900);
        setLocationRelativeTo(null);

        sentSeries.setName("Packets Sent");
        receivedSeries.setName("Packets Received");
        
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void setImage(String imagePath) {
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.setBackground(Color.DARK_GRAY);

        JPanel imagePane = new JPanel();
        imagePane.setBackground(Color.DARK_GRAY);

        try {
            sentImagePixels = Loader.readImage(imagePath).getBytes("ISO-8859-1");
        }
        catch(UnsupportedEncodingException e) {
            System.out.println("ISO-8859-1 encoding failed");
        }

        ImageIcon sentImageIcon = new ImageIcon(imagePath);
        Image image = sentImageIcon.getImage();
        Image newimg = image.getScaledInstance(800, 600,  java.awt.Image.SCALE_SMOOTH);
        sentImageIcon = new ImageIcon(newimg);
        JLabel label = new JLabel(sentImageIcon);
        imagePane.add(label);

        width = image.getWidth(null);
        height = image.getHeight(null);

        BufferedImage receivedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        receivedImagePixels = ((DataBufferByte) receivedImage.getRaster().getDataBuffer()).getData();
        ImageIcon receivedImageIcon = new ImageIcon(scale(receivedImage, 800, 600));
        receivedImageLabel = new JLabel(receivedImageIcon);
        imagePane.add(receivedImageLabel);

        pane.add(imagePane);

        JPanel statsPane = new JPanel();
        statsPane.setSize(1600, 200);
        statsPane.setBackground(Color.DARK_GRAY);

        final JFXPanel packetPanel = new JFXPanel();
        statsPane.add(packetPanel);

        JPanel reportPanel = new JPanel();
        ((FlowLayout)reportPanel.getLayout()).setHgap(30);
        reportPanel.setBackground(Color.DARK_GRAY);
        reportPanel.setSize(400, 200);

        JPanel timeReport = new JPanel();
        timeReport.setBackground(Color.DARK_GRAY);
        timeReport.setLayout(new BoxLayout(timeReport, BoxLayout.Y_AXIS));
        JLabel timeTitle = new JLabel("Time");
        timeTitle.setForeground(Color.WHITE);
        timeTitle.setFont(new Font(timeTitle.getFont().getName(), Font.PLAIN, 35));
        timeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeReport.add(timeTitle);
        time = new JLabel("?");
        time.setForeground(Color.WHITE);
        time.setFont(new Font(time.getFont().getName(), Font.PLAIN, 50));
        time.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeReport.add(time);
        timeReport.setSize(200, 200);
        reportPanel.add(timeReport);
        
        JPanel accuracyReport = new JPanel();
        accuracyReport.setBackground(Color.DARK_GRAY);
        accuracyReport.setLayout(new BoxLayout(accuracyReport, BoxLayout.Y_AXIS));
        JLabel accuracyTitle = new JLabel("Accuracy");
        accuracyTitle.setForeground(Color.WHITE);
        accuracyTitle.setFont(new Font(accuracyTitle.getFont().getName(), Font.PLAIN, 35));
        accuracyTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        accuracyReport.add(accuracyTitle);
        accuracy = new JLabel("?");
        accuracy.setForeground(Color.WHITE);
        accuracy.setFont(new Font(accuracy.getFont().getName(), Font.PLAIN, 50));
        accuracy.setAlignmentX(Component.CENTER_ALIGNMENT);
        accuracyReport.add(accuracy);
        accuracyReport.setSize(200, 200);
        reportPanel.add(accuracyReport);

        statsPane.add(reportPanel);

        final JFXPanel eccPanel = new JFXPanel();
        statsPane.add(eccPanel);

        pane.add(statsPane);

        Runnable update = new Runnable() {
            @Override
            public void run() {
                redrawReceivedImage();
            }
        };
        
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(update, 0, 100, TimeUnit.MILLISECONDS);
        
        Platform.runLater(() -> { initFX(packetPanel, eccPanel); });

        getContentPane().add(pane);
    }

    private void initFX(JFXPanel packetPanel, JFXPanel eccPanel) {
        Scene packetScene = createPacketScene(sentSeries, receivedSeries);
        packetPanel.setScene(packetScene);

        // Scene receivedScene = createScene(receivedSeries);
        // receivedPanel.setScene(receivedScene);

        prepareTimeline();
    }

    public void addSentValue(double time, int sent) {
        sentTime.add(time);
        sentNumber.add(sent);
    }

    public void addReceivedValue(double time, int received) {
        receivedTime.add(time);
        receivedNumber.add(received);
    }

    private void addDataToSeries() {
        while(!sentNumber.isEmpty()) {
            sentSeries.getData().add(new XYChart.Data<>(sentTime.remove(), sentNumber.remove()));
        }
        
        while(!receivedNumber.isEmpty()) {
            receivedSeries.getData().add(new XYChart.Data<>(receivedTime.remove(), receivedNumber.remove()));
        }
    }

    private void prepareTimeline() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                addDataToSeries();
            }
        }.start();
    }

    private Scene createPacketScene(XYChart.Series<Number,Number> sentSeries, XYChart.Series<Number,Number> receivedSeries) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Time (s)");
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(10);
        xAxis.setTickUnit(1);
        xAxis.setMinorTickCount(0);

        yAxis.setLabel("Packets");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(4000);
        yAxis.setTickUnit(500);
        yAxis.setMinorTickCount(0);

        final AreaChart<Number,Number> areaChart = new AreaChart<Number,Number>(xAxis,yAxis);
                
        areaChart.getData().add(sentSeries);
        areaChart.getData().add(receivedSeries);
        
        Scene scene  = new Scene(areaChart,600,200);
        scene.getStylesheets().add("chart.css");
        return scene;
    }

    private BufferedImage scale(BufferedImage original, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(original, 0, 0, width, height, null);
        graphics2D.dispose();

        return scaledImage;
    }

    private void redrawReceivedImage() {
        BufferedImage receivedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        receivedImage.setData(Raster.createRaster(receivedImage.getSampleModel(), new DataBufferByte(receivedImagePixels, receivedImagePixels.length), new Point() ) );
        ImageIcon receivedImageIcon = new ImageIcon(scale(receivedImage, 800, 600));
        receivedImageLabel.setIcon(receivedImageIcon);
    }

    public void receive(int offset, String packet) {
        try {
            byte[] packetPixels = packet.getBytes("ISO-8859-1");
            for(int i = 0; i < packetPixels.length; i++) {
                receivedImagePixels[offset+i] = packetPixels[i];
            }
        }
        catch(UnsupportedEncodingException e) {
            System.out.println("ISO-8859-1 encoding failed");
        }
    }

    public void setTime(double seconds) {
        time.setText(String.format("%.1fs",seconds));
    }

    public void computeAccuracy() {
        int correct = 0;
        for(int i = 0; i < sentImagePixels.length; i++)
            if(sentImagePixels[i] == receivedImagePixels[i])
                correct++;
        
        double acc = ((double)correct)/sentImagePixels.length;
        accuracy.setText(String.format("%.1f%%", 100*acc));
    }
}