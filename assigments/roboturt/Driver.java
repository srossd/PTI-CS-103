import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;

public class Driver {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                var panel = new RoboturtPanel();
                panel.setBackground(Color.GREEN.darker());
                var frame = new JFrame("Roboturt");
                frame.setSize(800, 800);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(panel, BorderLayout.CENTER);
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);

                Runnable runnable =
                                new Runnable(){
                                    public void run() {
                                        solveMaze(panel.getTurt());
                                    }
                                };

                Thread thread = new Thread(runnable);
                thread.setPriority(Thread.MIN_PRIORITY);
                thread.start();
            }
            catch(IOException e) {
                System.out.println("Image files not found");
            }
        });
    }

    public static void solveMaze(Turtle turt) {
        while(!turt.atFlag()) {
            if(turt.wallRight() && !turt.wallFront())
                turt.MF();
            else if(!turt.wallRight()) {
                turt.TL();
                turt.TL();
                turt.TL();
                turt.MF();
            }
            else {
                turt.TL();
            }
        }
    }
}