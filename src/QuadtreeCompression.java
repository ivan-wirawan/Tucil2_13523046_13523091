import java.io.File;

public class QuadtreeCompression {
    private ImageMatrix originalImage;
    private double threshold;
    private int minimumBlockSize;
    private int errorMethod;
    private double targetCompressionPercentage;
    String inputPath;

    public QuadtreeCompression(ImageMatrix originalImage, int errorMethod,
            double threshold, int minimumBlockSize, double targetCompressionPercentage, String inputPath) {
        this.originalImage = originalImage;
        this.errorMethod = errorMethod;
        this.threshold = threshold;
        this.minimumBlockSize = minimumBlockSize;
        this.targetCompressionPercentage = targetCompressionPercentage;
        this.inputPath = inputPath;
    }

    public QuadtreeNode compress() {
        // Bonus: target Compression Percentage
        boolean useMinimumBlockSize = true;
        if (targetCompressionPercentage > 0) {
            this.threshold = findOptimalThreshold();
            useMinimumBlockSize = false;
        }

        return buildQuadtree(originalImage, 0, 0, originalImage.getWidth(), originalImage.getHeight(),
                0, this.threshold, useMinimumBlockSize);
    }

    private QuadtreeNode buildQuadtree(ImageMatrix originalImage, int x, int y, int width,
            int height, int currentDepth, double threshold, boolean useMinimumBlockSize) {
        QuadtreeNode node = new QuadtreeNode(originalImage, x, y, width, height);

        if (width <= 1 || height <= 1) {
            return node;
        }

        double error = ErrorMetrics.calculateError(originalImage, x, y, width, height, errorMethod);

        int halfWidth = width / 2;
        int halfHeight = height / 2;

        boolean shouldDivide;

        if (useMinimumBlockSize) {
            shouldDivide = (error > threshold) && (width * height >= minimumBlockSize)
                    && (halfWidth * halfHeight >= minimumBlockSize);
        } else {
            shouldDivide = (error > threshold);
        }

        if (shouldDivide) {
            QuadtreeNode[] children = new QuadtreeNode[4];

            // Top-left
            children[0] = buildQuadtree(originalImage, x, y, halfWidth, halfHeight, currentDepth + 1,
                    threshold, useMinimumBlockSize);

            // Top-right
            children[1] = buildQuadtree(originalImage, x + halfWidth, y, width - halfWidth, halfHeight,
                    currentDepth + 1, threshold, useMinimumBlockSize);

            // Bottom-left
            children[2] = buildQuadtree(originalImage, x, y + halfHeight, halfWidth,
                    height - halfHeight, currentDepth + 1, threshold, useMinimumBlockSize);

            // Bottom-right
            children[3] = buildQuadtree(originalImage, x + halfWidth, y + halfHeight,
                    width - halfWidth, height - halfHeight, currentDepth + 1, threshold, useMinimumBlockSize);

            node.setChildren(children);
        }

        return node;
    }

    private double findOptimalThreshold() {
        double minThreshold = 0.0;
        double maxThreshold;

        if (errorMethod == 1) { // Variance
            maxThreshold = 16256.25;
        } else if (errorMethod == 2) { // Mean Absolute Deviation
            maxThreshold = 127.5;
        } else if (errorMethod == 3) { // Max Pixel Difference
            maxThreshold = 255.0;
        } else if (errorMethod == 4) { // Entropy
            maxThreshold = 8.0;
        } else if (errorMethod == 5) { // SSIM
            maxThreshold = 1.0;
        } else {
            maxThreshold = 255.0;
        }

        double currentThreshold;
        double currentCompression;
        double epsilon = 0.001;
        int maxIterations = 50;
        int iterations = 0;

        String originalExtension = inputPath.substring(inputPath.lastIndexOf('.'));
        String tempDirPath = "test/temp";
        String tempPath = tempDirPath + "/temp" + originalExtension;

        File tempDir = new File(tempDirPath);
        if (!tempDir.exists()) {
            if (!tempDir.mkdirs()) {
                System.err.println("Gagal membuat direktori: " + tempDirPath);
                return maxThreshold / 2;
            }
        }

        File tempFile = new File(tempPath);

        try {
            while (minThreshold < maxThreshold && iterations < maxIterations) {
                iterations++;
                currentThreshold = (minThreshold + maxThreshold) / 2;

                QuadtreeNode root = buildQuadtree(originalImage, 0, 0,
                        originalImage.getWidth(), originalImage.getHeight(), 0, currentThreshold, false);

                ImageMatrix reconstructedImage = IO.reconstructImageFromQuadtree(root,
                        originalImage.getWidth(), originalImage.getHeight());

                IO.writeCompressedImage(reconstructedImage, tempPath);

                currentCompression = IO.calcCompressionPercentage(inputPath, tempPath);

                if (Math.abs(currentCompression - targetCompressionPercentage) < epsilon) {
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }
                    return currentThreshold;
                }

                if (currentCompression > targetCompressionPercentage) {
                    maxThreshold = currentThreshold;
                } else {
                    minThreshold = currentThreshold;
                }
            }

            double finalThreshold = (minThreshold + maxThreshold) / 2;

            if (tempFile.exists()) {
                tempFile.delete();
            }

            return finalThreshold;
        } catch (Exception e) {
            System.err.println("Error during threshold optimization: " + e.getMessage());
            e.printStackTrace();

            if (tempFile.exists()) {
                tempFile.delete();
            }

            return (minThreshold + maxThreshold) / 2;
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    public int calculateTreeDepth(QuadtreeNode root) {
        if (root == null) {
            return -1;
        }

        if (root.getChildren() == null) {
            return 0;
        }

        int topLeftDepth = calculateTreeDepth(root.getChildren()[0]);
        int topRightDepth = calculateTreeDepth(root.getChildren()[1]);
        int bottomLeftDepth = calculateTreeDepth(root.getChildren()[2]);
        int bottomRightDepth = calculateTreeDepth(root.getChildren()[3]);

        int maxDepth = topLeftDepth;
        if (topRightDepth > maxDepth)
            maxDepth = topRightDepth;
        if (bottomLeftDepth > maxDepth)
            maxDepth = bottomLeftDepth;
        if (bottomRightDepth > maxDepth)
            maxDepth = bottomRightDepth;

        return maxDepth + 1;
    }

    public int calculateNodeCount(QuadtreeNode root) {
        if (root == null) {
            return 0;
        }

        int count = 1;

        if (root.getChildren() != null) {
            int topLeftCount = calculateNodeCount(root.getChildren()[0]);
            int topRightCount = calculateNodeCount(root.getChildren()[1]);
            int bottomLeftCount = calculateNodeCount(root.getChildren()[2]);
            int bottomRightCount = calculateNodeCount(root.getChildren()[3]);

            count += topLeftCount + topRightCount + bottomLeftCount + bottomRightCount;
        }

        return count;
    }

    public double getThreshold() {
        return threshold;
    }
}