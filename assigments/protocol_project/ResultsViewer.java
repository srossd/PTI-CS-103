import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.*;

public class ResultsViewer extends JFrame {

    private static final long serialVersionUID = 2047524583583651892L;

    private JLabel receivedImageLabel;
    private byte[] receivedImagePixels;
    private byte[] sentImagePixels;

    private int width;
    private int height;

    private JLabel time;
    private JLabel accuracy;

    private JButton start;

    private JTable table;
    private int row;
    private double sec, acc;

    public ResultsViewer() {
        setTitle("Network Viewer");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void setImage(String imagePath) {
        JPanel superpane = new JPanel();
        superpane.setLayout(new BoxLayout(superpane, BoxLayout.X_AXIS));

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
        width = image.getWidth(null);
        height = image.getHeight(null);

        BufferedImage receivedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        receivedImagePixels = ((DataBufferByte) receivedImage.getRaster().getDataBuffer()).getData();
        ImageIcon receivedImageIcon = new ImageIcon(scale(receivedImage, 800, 600));
        receivedImageLabel = new JLabel(receivedImageIcon);
        imagePane.add(receivedImageLabel);

        pane.add(imagePane);

        JPanel statsPane = new JPanel();
        statsPane.setSize(1000, 200);
        statsPane.setBackground(Color.DARK_GRAY);

        JPanel reportPanel = new JPanel();
        ((FlowLayout)reportPanel.getLayout()).setHgap(60);
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

        pane.add(statsPane);

        start = new JButton("Start");
        start.setAlignmentX(JButton.CENTER_ALIGNMENT);
        pane.add(start);

        Runnable update = new Runnable() {
            @Override
            public void run() {
                redrawReceivedImage();
            }
        };
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(update, 0, 100, TimeUnit.MILLISECONDS);

        superpane.add(pane);

        String[] columns = {"Packet Size", "Time (s)", "Accuracy (%)"};
        
        DefaultTableModel tableModel = new DefaultTableModel(new Double[50][3], columns) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
               //all cells false
               return false;
            }
        };

        table = new JTable(new Double[50][3], columns);
        table.setModel(tableModel);
        new ExcelAdapter(table);

        JScrollPane data = new JScrollPane(table);
        superpane.add(data);

        getContentPane().add(superpane);
        setSize(1100, 900);
        setLocationRelativeTo(null);
        
        setVisible(true);
    }

    public void setAction(ActionListener al) {
        start.addActionListener(al);
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
            // redrawReceivedImage();
        }
        catch(UnsupportedEncodingException e) {
            System.out.println("ISO-8859-1 encoding failed");
        }
    }

    public void setTime(double seconds) {
        sec = seconds;
        time.setText(String.format("%.1fs",seconds));
    }

    public void computeAccuracy() {
        int correct = 0;
        for(int i = 0; i < sentImagePixels.length; i++)
            if(sentImagePixels[i] == receivedImagePixels[i])
                correct++;
        
        acc = ((double)correct)/sentImagePixels.length;
        accuracy.setText(String.format("%.1f%%", 100*acc));
    }

    public void logData(int packetSize) {
        table.setValueAt(packetSize, row, 0);
        table.setValueAt(sec, row, 1);
        table.setValueAt(Math.round(1000*acc)/10., row, 2);
        row++;
    }

    public void resetImage(int packetSize) {
        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        start.setText("Testing packet size of "+packetSize);
        start.setEnabled(false);

        BufferedImage receivedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        receivedImagePixels = ((DataBufferByte) receivedImage.getRaster().getDataBuffer()).getData();
        // ImageIcon receivedImageIcon = new ImageIcon(scale(receivedImage, 800, 600));
        // receivedImageLabel = new JLabel(receivedImageIcon);
    }
}