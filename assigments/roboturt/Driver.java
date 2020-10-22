import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class Driver {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                var panel = new RoboturtPanel(10);
                panel.setBackground(Color.GREEN.darker());

                var button = new JButton("Start");
                button.addActionListener(new ActionListener() { 
                    public void actionPerformed(ActionEvent e) { 
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
                  } );

                var frame = new JFrame("Roboturt");
                frame.setSize(800, 800);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.getContentPane().add(panel);
                frame.getContentPane().add(button, BorderLayout.SOUTH);
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);
            }
            catch(IOException e) {
                System.out.println("Image files not found");
            }
        });
    }

    public static void turnRight(Turtle turt) {
        for(int i = 0; i < 3; i++)
            turt.TL();
    }

    public static void solveMaze(Turtle turt) {
        int steps = 1;
        while(!turt.atFlag()) {
            if(!turt.wallRight()) {
                if(turt.counters[turt.getX()][turt.getY()] == 0)
                    turt.counters[turt.getX()][turt.getY()] = steps++;
                turnRight(turt);
                turt.MF();
            }
            else if(!turt.wallFront()) {
                if(turt.counters[turt.getX()][turt.getY()] == 0)
                    turt.counters[turt.getX()][turt.getY()] = steps++;
                turt.MF();
            }
            else {
                turt.TL();
            }
        }

        turt.drawPath();
        turt.hideNumbers();
    }
}