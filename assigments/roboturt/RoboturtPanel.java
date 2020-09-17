import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

 public class RoboturtPanel extends JPanel {
    private static final long serialVersionUID = 7148504528835036003L;

    private Turtle turt;

    private int mazeStartX, mazeStartY, mazeWidth, mazeHeight;

    private BufferedImage turtImg, flagImg;

    public RoboturtPanel() throws IOException {
        this.turt = new Turtle(this, 10);
        
        this.mazeStartX = 100;
        this.mazeStartY = 700;
        this.mazeWidth = 600;
        this.mazeHeight = -600;

        turtImg = ImageIO.read(new File("turtle.png"));
        flagImg = ImageIO.read(new File("flags.png"));
    }

    public RoboturtPanel(int size) throws IOException {
        this.turt = new Turtle(this, size);
        
        this.mazeStartX = 100;
        this.mazeStartY = 700;
        this.mazeWidth = 600;
        this.mazeHeight = -600;

        turtImg = ImageIO.read(new File("turtle.png"));
        flagImg = ImageIO.read(new File("flags.png"));
    }

    public Turtle getTurt() {
        return turt;
    }

    private int convertCoordinate(int x, int max, int start, int length) {
        return start + (int)(((x+0.5)*length)/max);
    }

    private void drawImg(BufferedImage img, Graphics g, int x, int y, double angle, double cw, double ch) {
        int xpix = convertCoordinate(x, turt.getMaze().getWidth(), mazeStartX, mazeWidth) - (int)(0.4*cw);
        int ypix = convertCoordinate(y, turt.getMaze().getHeight(), mazeStartY, mazeHeight) - (int)(0.4*ch);

        double scale = 0.8*Math.min(cw/img.getWidth(), ch/img.getHeight());
        AffineTransform tx = AffineTransform.getScaleInstance(scale, scale);
        tx.rotate(Math.toRadians(-angle), img.getWidth()/2, img.getHeight()/2);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

        g.drawImage(op.filter(img, null), xpix, ypix, null);
    }

    private void drawString(String s, Graphics g, int x, int y, double cw, double ch) {
        int xpix = convertCoordinate(x, turt.getMaze().getWidth(), mazeStartX, mazeWidth);
        int ypix = convertCoordinate(y, turt.getMaze().getHeight(), mazeStartY, mazeHeight);

        FontMetrics metrics = g.getFontMetrics(g.getFont());

        g.drawString(s, xpix - metrics.stringWidth(s)/2, ypix + metrics.getHeight()/2);
    }

    private void drawLine(Graphics g, int x1, int y1, int x2, int y2) {
        int x1pix = convertCoordinate(x1, turt.getMaze().getWidth(), mazeStartX, mazeWidth);
        int y1pix = convertCoordinate(y1, turt.getMaze().getHeight(), mazeStartY, mazeHeight);
        int x2pix = convertCoordinate(x2, turt.getMaze().getWidth(), mazeStartX, mazeWidth);
        int y2pix = convertCoordinate(y2, turt.getMaze().getHeight(), mazeStartY, mazeHeight);

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(5));
        g2.setColor(Color.RED);
        g.drawLine(x1pix, y1pix, x2pix, y2pix);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        turt.getMaze().draw(g, mazeStartX, mazeStartY, mazeWidth, mazeHeight);

        double cellWidth = Math.abs(mazeWidth/turt.getMaze().getWidth());
        double cellHeight = Math.abs(mazeHeight/turt.getMaze().getHeight());

        if(turt.numbers) {
            g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 300/turt.getMaze().getWidth())); 
            for(int i = 0; i < turt.getMaze().getHeight(); i++) {
                for(int j = 0; j < turt.getMaze().getWidth(); j++) {
                    if(turt.counters[i][j] != 0)
                        drawString(turt.counters[i][j]+"", g, i, j, cellWidth, cellHeight);
                }
            }
        }

        if(turt.path) {
            ArrayList<Integer> path = turt.getPath();

            for(int i = 0; i < path.size() - 1; i++) {
                int x1 = path.get(i) % turt.getMaze().getWidth();
                int y1 = path.get(i) / turt.getMaze().getWidth();
                int x2 = path.get(i+1) % turt.getMaze().getWidth();
                int y2 = path.get(i+1) / turt.getMaze().getWidth();

                drawLine(g, x1, y1, x2, y2);
            }
        }
        
        drawImg(flagImg, g, turt.getMaze().getWidth() - 1, turt.getMaze().getHeight() - 1, 0, cellWidth, cellHeight);
        drawImg(turtImg, g, turt.getX(), turt.getY(), turt.getAngle(), cellWidth, cellHeight);
        
    }
}