import java.awt.image.BufferedImage;

public class ImageMatrix {
    private int[][][] pixels;
    private int width;
    private int height;

    public ImageMatrix(BufferedImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.pixels = new int[3][width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                pixels[0][x][y] = (rgb >> 16) & 0xFF;
                pixels[1][x][y] = (rgb >> 8) & 0xFF;
                pixels[2][x][y] = rgb & 0xFF;
            }
        }
    }

    public ImageMatrix(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new int[3][width][height];
    }

    public BufferedImage toBufferedImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int r = pixels[0][x][y];
                int g = pixels[1][x][y];
                int b = pixels[2][x][y];
                int rgb = (r << 16) | (g << 8) | b;
                image.setRGB(x, y, rgb);
            }
        }
        
        return image;
    }

    public int[][][] getPixels() {
        return pixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPixel(int channel, int x, int y) {
        return pixels[channel][x][y];
    }

    public void setPixel(int channel, int x, int y, int value) {
        pixels[channel][x][y] = value;
    }

    public int getAverageColor(int x, int y, int blockWidth, int blockHeight) {
        long sumR = 0, sumG = 0, sumB = 0;
        int pixelCount = blockWidth * blockHeight;

        for (int i = 0; i < blockWidth; i++) {
            for (int j = 0; j < blockHeight; j++) {
                sumR += pixels[0][x + i][y + j];
                sumG += pixels[1][x + i][y + j];
                sumB += pixels[2][x + i][y + j];
            }
        }

        int avgR = (int) (sumR / pixelCount);
        int avgG = (int) (sumG / pixelCount);
        int avgB = (int) (sumB / pixelCount);

        return (avgR << 16) | (avgG << 8) | avgB;
    }
}