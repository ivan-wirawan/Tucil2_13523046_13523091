package utils;

import javax.imageio.ImageIO;

import compression.QuadtreeNode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class IO {
    public static ImageMatrix readImage(String imagePath) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
        return new ImageMatrix(bufferedImage);
    }

    public static void writeCompressedImage(ImageMatrix image, String outputPath) throws IOException {
        BufferedImage bufferedImage = image.toBufferedImage();
        File outputFile = new File(outputPath);
        String format = outputPath.substring(outputPath.lastIndexOf('.') + 1);
        ImageIO.write(bufferedImage, format, outputFile);
    }

    public static ImageMatrix reconstructImageFromQuadtree(QuadtreeNode root, int width, int height) {
        ImageMatrix reconstructedImage = new ImageMatrix(width, height);
        reconstructNode(root, reconstructedImage);
        return reconstructedImage;
    }

    private static void reconstructNode(QuadtreeNode node, ImageMatrix image) {
        if (node.isLeaf()) {
            int avgColor = node.getAverageColor();
            for (int x = node.getX(); x < node.getX() + node.getWidth(); x++) {
                for (int y = node.getY(); y < node.getY() + node.getHeight(); y++) {
                    int r = (avgColor >> 16) & 0xFF;
                    int g = (avgColor >> 8) & 0xFF;
                    int b = avgColor & 0xFF;
                    
                    image.setPixel(0, x, y, r);
                    image.setPixel(1, x, y, g);
                    image.setPixel(2, x, y, b);
                }
            }
        } else {
            for (QuadtreeNode child : node.getChildren()) {
                reconstructNode(child, image);
            }
        }
    }

    public static double calcCompressionPercentage(String originalPath, String compressedPath) {
        File originalFile = new File(originalPath);
        File compressedFile = new File(compressedPath);
    
        long originalSize = originalFile.length();      
        long compressedSize = compressedFile.length();
    
        if (originalSize == 0) {
            throw new IllegalArgumentException("Ukuran file asli tidak boleh 0");
        }
    
        double compressionRatio = 1.0 - ((double) compressedSize / originalSize);
        return compressionRatio * 100;
    }
    
    // // Bonus: Create GIF visualization of compression process
    // public static void createCompressionGif(List<BufferedImage> frames, String outputPath) throws IOException {
    //     
    // }
}