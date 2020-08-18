import javax.swing.JPanel;

public class Turtle {
    private int x, y, angle;
    private Maze maze;
    private JPanel panel;

    public Turtle(JPanel panel) {
        this.x = 0;
        this.y = 0;
        this.angle = 0;

        this.panel = panel;

        this.maze = new Maze(10);
    }

    public Turtle(JPanel panel, int size) {
        this.x = 0;
        this.y = 0;
        this.angle = 0;

        this.panel = panel;

        this.maze = new Maze(size);
    }

    public Turtle(JPanel panel, int x, int y, int angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;

        this.panel = panel;

        this.maze = new Maze(10);
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
}