import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.io.File;
import java.io.IOException;

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

    public Turtle getTurt() {
        return turt;
    }

    private int convertCoordinate(int x, int max, int start, int length) {
        return start + (int)(((x+0.5)*length)/max);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        turt.getMaze().draw(g, mazeStartX, mazeStartY, mazeWidth, mazeHeight);

        double cellWidth = Math.abs(mazeWidth/turt.getMaze().getWidth());
        double cellHeight = Math.abs(mazeHeight/turt.getMaze().getHeight());
        AffineTransform tx = AffineTransform.getScaleInstance(0.8*cellWidth/turtImg.getWidth(), 0.8*cellHeight/turtImg.getHeight());
        tx.rotate(Math.toRadians(-turt.getAngle()), turtImg.getWidth()/2, turtImg.getHeight()/2);
        AffineTransformOp opTurt = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

        int turtxpixel = convertCoordinate(turt.getX(), turt.getMaze().getWidth(), mazeStartX, mazeWidth) - (int)(0.4*cellWidth);
        int turtypixel = convertCoordinate(turt.getY(), turt.getMaze().getHeight(), mazeStartY, mazeHeight) - (int)(0.4*cellHeight);

        double flagscale = 0.8*Math.min(cellWidth/flagImg.getWidth(), cellHeight/flagImg.getHeight());
        tx.setToScale(flagscale, flagscale);
        AffineTransformOp opFlag = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

        int flagxpixel = convertCoordinate(turt.getMaze().getWidth() - 1, turt.getMaze().getWidth(), mazeStartX, mazeWidth) - (int)(0.4*cellWidth);
        int flagypixel = convertCoordinate(turt.getMaze().getHeight() - 1, turt.getMaze().getHeight(), mazeStartY, mazeHeight) - (int)(0.4*cellHeight);

        g.drawImage(opFlag.filter(flagImg, null), flagxpixel, flagypixel, null);
        g.drawImage(opTurt.filter(turtImg, null), turtxpixel, turtypixel, null);
        
    }
}