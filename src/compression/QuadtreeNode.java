package compression;

import java.awt.image.BufferedImage;

public class QuadtreeNode {
    private int x, y;           
    private int width, height;  
    private BufferedImage block; 
    private QuadtreeNode[] children; 
    private int averageColor;   
    private double error;       
    private boolean isLeaf;

    public QuadtreeNode(BufferedImage block, int x, int y, int width, int height) {
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
        long sumR = 0, sumG = 0, sumB = 0;
        int pixelCount = width * height;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = block.getRGB(x + i, y + j);
                sumR += (rgb >> 16) & 0xFF;
                sumG += (rgb >> 8) & 0xFF;
                sumB += rgb & 0xFF;
            }
        }

        int avgR = (int) (sumR / pixelCount);
        int avgG = (int) (sumG / pixelCount);
        int avgB = (int) (sumB / pixelCount);

        return (avgR << 16) | (avgG << 8) | avgB;
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
