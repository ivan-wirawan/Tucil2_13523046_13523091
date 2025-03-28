package compression;

import utils.ErrorMetrics;
import utils.ErrorMetrics.ErrorMethod;

import utils.ImageMatrix;

public class QuadtreeCompression {
    private ImageMatrix originalImage;
    private ImageMatrix compressedImage;
    private double threshold;
    private int minimumBlockSize;
    private ErrorMethod errorMethod;
    private double targetCompressionPercentage;
    private int treeDepth;
    private int nodeCount;

    public QuadtreeCompression(ImageMatrix originalImage, ImageMatrix compressedImage, ErrorMethod errorMethod, double threshold, int minimumBlockSize, double targetCompressionPercentage) {
        this.originalImage = originalImage;
        this.compressedImage = compressedImage;
        this.errorMethod = errorMethod;
        this.threshold = threshold;
        this.minimumBlockSize = minimumBlockSize;
        this.targetCompressionPercentage = targetCompressionPercentage;
        this.treeDepth = 0;
        this.nodeCount = 0;
    }

    public QuadtreeNode compress() {
        if (targetCompressionPercentage > 0) {
            this.threshold = findOptimalThreshold();
        }

        if (errorMethod == ErrorMethod.SSIM && compressedImage == null) {
            throw new IllegalArgumentException("Compressed image is required for SSIM method");
        }

        return buildQuadtree(originalImage, compressedImage, 0, 0, originalImage.getWidth(), originalImage.getHeight(), 0);
    }

    private QuadtreeNode buildQuadtree(ImageMatrix originalImage, ImageMatrix compressedImage, int x, int y, int width, int height, int currentDepth) {
        treeDepth = Math.max(treeDepth, currentDepth);
        
        QuadtreeNode node = new QuadtreeNode(originalImage, x, y, width, height);
        nodeCount++;

        double error = ErrorMetrics.calculateError(originalImage, compressedImage, x, y, width, height, errorMethod);
        
        node.setError(error);

        boolean shouldDivide = shouldDivideBlock(width, height, error);

        if (shouldDivide) {
            QuadtreeNode[] children = new QuadtreeNode[4];
            int halfWidth = width / 2;
            int halfHeight = height / 2;

            // Top-left
            children[0] = buildQuadtree(originalImage, compressedImage, x, y, halfWidth, halfHeight, currentDepth + 1);
            
            // Top-right
            children[1] = buildQuadtree(originalImage, compressedImage, x + halfWidth, y, width - halfWidth, halfHeight, currentDepth + 1);
            
            // Bottom-left
            children[2] = buildQuadtree(originalImage, compressedImage, x, y + halfHeight, halfWidth, height - halfHeight, currentDepth + 1);
            
            // Bottom-right
            children[3] = buildQuadtree(originalImage, compressedImage, x + halfWidth, y + halfHeight, width - halfWidth, height - halfHeight, currentDepth + 1);

            node.setChildren(children);
        }

        return node;
    }

    private boolean shouldDivideBlock(int width, int height, double error) {
        if (width <= minimumBlockSize || height <= minimumBlockSize) {
            return false;
        }

        return error > threshold;
    }

    // Not Fixed
    private double findOptimalThreshold() {
        double minThreshold = 0;
        double maxThreshold = 255;
        double currentThreshold = (minThreshold + maxThreshold) / 2;
        double precision = 0.0001; // adjust precision
    
        while (Math.abs(maxThreshold - minThreshold) > precision) {
            treeDepth = 0;
            nodeCount = 0;
    
            compress();
    
            double compressionPercentage = calculateCompressionPercentage();
    
            if (compressionPercentage > targetCompressionPercentage) {
                minThreshold = currentThreshold;
            } else {
                maxThreshold = currentThreshold;
            }
    
            currentThreshold = (minThreshold + maxThreshold) / 2;
        }
    
        return currentThreshold;
    }

    // Not Fixed
    private double calculateCompressionPercentage() {
        double originalNodeCount = originalImage.getWidth() * originalImage.getHeight();
        return 1.0 - (nodeCount / originalNodeCount);
    }

    public int getTreeDepth() {
        return treeDepth;
    }

    public int getNodeCount() {
        return nodeCount;
    }
}