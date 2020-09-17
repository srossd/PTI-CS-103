import java.util.ArrayList;

import javax.lang.model.util.ElementScanner6;
import javax.swing.JPanel;

public class Turtle {
    private int x, y, angle;
    private Maze maze;
    private JPanel panel;

    public int[][] counters;

    public boolean numbers = true, path = false;

    public Turtle(JPanel panel) {
        this.x = 0;
        this.y = 0;
        this.angle = 0;

        this.panel = panel;

        this.maze = new Maze(10);
        this.counters = new int[10][10];
    }

    public Turtle(JPanel panel, int size) {
        this.x = 0;
        this.y = 0;
        this.angle = 0;

        this.panel = panel;

        this.maze = new Maze(size);
        this.counters = new int[size][size];
    }

    public Turtle(JPanel panel, int x, int y, int angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;

        this.panel = panel;

        this.maze = new Maze(10);
        this.counters = new int[10][10];
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getAngle() {
        return angle;
    }

    public Maze getMaze() {
        return maze;
    }

    public boolean wallFront() {
        return maze.hasWall(x, y, angle);
    }

    public boolean wallRight() {
        return maze.hasWall(x, y, angle + 270);
    }

    public boolean wallBehind() {
        return maze.hasWall(x, y, angle + 180);
    }

    public boolean wallLeft() {
        return maze.hasWall(x, y, angle + 90);
    }

    public boolean atFlag() {
        return (x == maze.getWidth() - 1) && (y == maze.getHeight() - 1);
    }

    public void MF() {
        if (wallFront()) {
            System.out.println("I can't move forward, I have a wall in front of me!");
            System.exit(0);
        }
        int oldy = y;
        int oldx = x;
        switch ((angle / 90) % 4) {
            case 0:
                y++;
                break;
            case 1:
                x--;
                break;
            case 2:
                y--;
                break;
            case 3:
                x++;
                break;
        }
        if (x < 0 || x >= maze.getWidth() || y < 0 || y >= maze.getHeight()) {
            System.out.println("I can't move forward, I have a wall in front of me!");
            System.exit(0);
            x = oldx;
            y = oldy;
        }
        panel.repaint(0, 0, panel.getWidth(), panel.getHeight());
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void TL() {
        angle += 90;
        panel.repaint(0, 0, panel.getWidth(), panel.getHeight());
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void hideNumbers() {
        numbers = false;
        panel.repaint(0, 0, panel.getWidth(), panel.getHeight());
    }

    public void drawPath() {
        path = true;
        panel.repaint(0, 0, panel.getWidth(), panel.getHeight());
    }

    public ArrayList<Integer> getPath() {
        ArrayList<Integer> coords = new ArrayList<Integer>();

        coords.add(0);
        int last = 0;
        int temp = counters[getMaze().getWidth() - 1][getMaze().getHeight()-1];
        counters[getMaze().getWidth() - 1][getMaze().getHeight()-1] = getMaze().getHeight() * getMaze().getWidth();
        while(last != maze.getHeight()*maze.getWidth() - 1 && coords.size() < getMaze().getHeight() * getMaze().getWidth()) {
            int x = last % maze.getWidth();
            int y = last / maze.getWidth();

            int up = y == maze.getHeight() - 1 || maze.hasWall(x, y, 0) ? 0 : counters[x][y+1];
            int down = y == 0 || maze.hasWall(x, y, 180)  ? 0 : counters[x][y-1];
            int right = x == maze.getWidth() - 1 || maze.hasWall(x, y, 270)  ? 0 : counters[x+1][y];
            int left = x == 0 || maze.hasWall(x, y, 90) ? 0 : counters[x-1][y];

            if(up > down && up > right && up > left)
                last += maze.getWidth();
            else if(down > right && down > left)
                last -= maze.getWidth();
            else if(right > left)
                last += 1;
            else
                last -= 1;
            
            coords.add(last);
        }
        counters[getMaze().getWidth() - 1][getMaze().getHeight()-1] = temp;

        if(coords.get(coords.size() - 1) != maze.getHeight()*maze.getWidth() - 1)
            System.out.println("There seems to be something wrong with the counters array.");

        return coords;
    }
}