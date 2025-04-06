import compression.QuadtreeCompression;
import compression.QuadtreeNode;
import utils.ErrorMetrics.ErrorMethod;
import utils.IO;
import utils.ImageMatrix;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // User inputs
            System.out.print("Enter absolute path of input image: ");
            String inputPath = scanner.nextLine();

            // Error method selection
            System.out.println("Select Error Measurement Method:");
            for (int i = 0; i < ErrorMethod.values().length; i++) {
                System.out.printf("%d. %s\n", i + 1, ErrorMethod.values()[i]);
            }
            System.out.print("Enter method number: ");
            int methodChoice = scanner.nextInt() - 1;
            ErrorMethod errorMethod = ErrorMethod.values()[methodChoice];

            // Prepare compressed image reference for SSIM
            ImageMatrix compressedImageRef = null;
            if (errorMethod == ErrorMethod.SSIM) {
                System.out.print("Enter absolute path of compressed reference image for SSIM: ");
                scanner.nextLine(); // Consume newline
                String compressedRefPath = scanner.nextLine();
                compressedImageRef = IO.readImage(compressedRefPath);
            }

            // Threshold input
            System.out.print("Enter error threshold: ");
            double threshold = scanner.nextDouble();

            // Minimum block size
            System.out.print("Enter minimum block size: ");
            int minimumBlockSize = scanner.nextInt();

            // Compression percentage target
            System.out.print("Enter target compression percentage (0 to disable): ");
            double targetCompression = scanner.nextDouble();

            // Output path
            System.out.print("Enter absolute path for compressed image: ");
            scanner.nextLine(); // Consume newline
            String outputPath = scanner.nextLine();

            // Start timing
            long startTime = System.nanoTime();

            // Read input image
            ImageMatrix originalImage = IO.readImage(inputPath);

            // Perform compression
            QuadtreeCompression compressor = new QuadtreeCompression(
                originalImage, 
                compressedImageRef, 
                errorMethod, 
                threshold, 
                minimumBlockSize, 
                targetCompression
            );

            // Compress image
            QuadtreeNode root = compressor.compress();

            // Reconstruct compressed image
            ImageMatrix compressedImage = IO.reconstructImageFromQuadtree(
                root, 
                originalImage.getWidth(), 
                originalImage.getHeight()
            );

            // End timing
            long endTime = System.nanoTime();
            double executionTime = (endTime - startTime) / 1_000_000_000.0;

            // Write compressed image
            IO.writeCompressedImage(compressedImage, outputPath);

            // Output results
            System.out.printf("Execution Time: %.4f seconds\n", executionTime);
            System.out.printf("Original Image Size: %d bytes\n", originalImage.getWidth() * originalImage.getHeight() * 3);
            System.out.printf("Compressed Image Size: %d bytes\n", compressedImage.getWidth() * compressedImage.getHeight() * 3);
            
            double compressionPercentage = IO.calcCompressionPercentage(inputPath, outputPath);
            System.out.printf("Compression Percentage: %.2f%%\n", compressionPercentage);
            
            System.out.printf("Tree Depth: %d\n", compressor.getTreeDepth());
            System.out.printf("Node Count: %d\n", compressor.getNodeCount());

        } catch (IOException e) {
            System.err.println("Error processing image: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}