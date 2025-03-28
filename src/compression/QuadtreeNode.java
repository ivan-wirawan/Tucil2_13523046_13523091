package compression;

import utils.ImageMatrix;

public class QuadtreeNode {
    private int x, y;           
    private int width, height;  
    private ImageMatrix block; 
    private QuadtreeNode[] children; 
    private int averageColor;   
    private double error;       
    private boolean isLeaf;

    public QuadtreeNode(ImageMatrix block, int x, int y, int width, int height) {
        this.block = block;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.children = new QuadtreeNode[4];
        this.isLeaf = true;
        this.averageColor = calculateAverageColor();
    }

    private int calculateAverageColor() {
        return block.getAverageColor(x, y, width, height);
    }

    public QuadtreeNode[] getChildren() {
        return children;
    }

    public void setChildren(QuadtreeNode[] children) {
        this.children = children;
        this.isLeaf = false;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public int getAverageColor() {
        return averageColor;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
