package compression;

import utils.ImageMatrix;

public class QuadtreeNode {
    private int x, y;           
    private int width, height;  
    private ImageMatrix imageMatrix; 
    private QuadtreeNode[] children; 
    private int averageColor;   
    // private double error;       
    private boolean isLeaf;

    public QuadtreeNode(ImageMatrix imageMatrix, int x, int y, int width, int height) {
        this.imageMatrix = imageMatrix;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.children = new QuadtreeNode[4];
        this.isLeaf = true;
        this.averageColor = calculateAverageColor();
    }

    private int calculateAverageColor() {
        return imageMatrix.getAverageColor(x, y, width, height);
    }

    public QuadtreeNode[] getChildren() {
        return children;
    }

    public int getAverageColor() {
        return averageColor;
    }

    // public double getError() {
    //     return error;
    // }

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

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setChildren(QuadtreeNode[] children) {
        this.children = children;
        this.isLeaf = false;
    }

    // public void setError(double error) {
    //     this.error = error;
    // }
}
