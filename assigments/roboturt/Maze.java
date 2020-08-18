import java.util.HashSet;
import java.util.ArrayList;
import java.util.Random;

import java.awt.Graphics;
import java.awt.BasicStroke;
import java.awt.Graphics2D;

public class Maze {
    private boolean[][] horzWalls;
    private boolean[][] vertWalls;
    private int size;

    public Maze(int size) {
        this.size = size;
        horzWalls = new boolean[size-1][size];
        vertWalls = new boolean[size-1][size];

        for(int i = 0; i < size - 1; i++) {
            for(int j = 0; j < size; j++) {
                horzWalls[i][j] = true;
                vertWalls[i][j] = true;
            }
        }

        HashSet<Integer> visited = new HashSet<Integer>();
        visited.add(0);

        while(visited.size() < size*size) {
            int v = 0;
            while(visited.contains(v))
                v++;
            
            var path = lerw(v, visited, size);
            for(int i = 0; i < path.size()-1; i++) {
                visited.add(path.get(i));
                this.removeWall(path.get(i), path.get(i+1));
            }
        }
    }   

    private ArrayList<Integer> lerw(int v, HashSet<Integer> visited, int size) {
        var path = new ArrayList<Integer>();
        path.add(v);

        Random random = new Random();

        while(!visited.contains(v)) {
            var neighbors = new ArrayList<Integer>();
            if(v % size != 0)
                neighbors.add(v-1);
            if(v % size != size-1)
                neighbors.add(v+1);
            if(v >= size)
                neighbors.add(v-size);
            if(v < size*(size-1))
                neighbors.add(v+size);
            
            v = neighbors.get(random.nextInt(neighbors.size()));
            path.add(v);
        }

        return le(path);
    }

    private ArrayList<Integer> le(ArrayList<Integer> path) {
        ArrayList<Integer> lep = new ArrayList<Integer>();
        lep.add(path.get(0));
        int next = path.get(1);
        while(lep.get(lep.size()-1) != path.get(path.size()-1)) {
            int i = path.size()-1;
            while(path.get(i) != next)
                i--;
            lep.add(path.get(i));
            if(i < path.size() - 1)
                next = path.get(i+1);
        }
        return lep;
    }

    private void removeWall(int i1, int i2) {
        int col = Math.min(i1,i2) % size;
        int row = Math.min(i1,i2) / size;
        if(Math.abs(i1-i2) == 1)
            vertWalls[col][row] = false;
        else
            horzWalls[row][col] = false;
    }

    public int getWidth() {
        return size;
    }

    public int getHeight() {
        return size;
    }

    public boolean hasWall(int x, int y, int angle) {
        if(y == -1 && x == 0)
            return false;
        if(angle % 360 == 0)
            if(y == size - 1)
                return x != size - 1;
            else
                return horzWalls[y][x];
        else if(angle % 360 == 90)
            return x == 0 || vertWalls[x-1][y];
        else if(angle % 360 == 180)
            if(y == 0)
                return x != 0;
            else
                return horzWalls[y-1][x];
        else
            return x == size - 1 || vertWalls[x][y];
    }

    public void draw(Graphics g, int x, int y, int width, int height) {
        int wallWidth = width/this.size;
        int wallHeight = height/this.size;

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(wallWidth/10));
        for(int i = 0; i < size - 1; i++) {
            for(int j = 0; j < size; j++) {
                if(vertWalls[i][j])
                    g2.drawLine(x + (i+1)*wallWidth, y + j*wallHeight, x + (i+1) * wallWidth, y + (j+1)*wallHeight);
                if(horzWalls[i][j])
                    g2.drawLine(x + j*wallWidth, y + (i+1)*wallHeight, x + (j+1)*wallWidth, y + (i+1)*wallHeight);
            }
        }

        g2.drawLine(x, y, x, y + height);
        g2.drawLine(x + width, y, x + width, y + height);
        g2.drawLine(x, y, x + width, y);
        g2.drawLine(x, y + height, x + width, y + height);
    }
}