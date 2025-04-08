public class QuadtreeCompression {
    private ImageMatrix originalImage;
    private ImageMatrix compressedImage;
    private double threshold;
    private int minimumBlockSize;
    private int errorMethod;
    private double targetCompressionPercentage; //Bonus

    public QuadtreeCompression(ImageMatrix originalImage, ImageMatrix compressedImage, int errorMethod, double threshold, int minimumBlockSize, double targetCompressionPercentage) {
        this.originalImage = originalImage;
        this.compressedImage = compressedImage;
        this.errorMethod = errorMethod;
        this.threshold = threshold;
        this.minimumBlockSize = minimumBlockSize;
        this.targetCompressionPercentage = targetCompressionPercentage;
    }

    public QuadtreeNode compress() {
        // // Bonus: target Compression Percentage (Not Fixed)
        // if (targetCompressionPercentage > 0) {
        //     this.threshold = findOptimalThreshold();
        // }

        // // Bonus: SSIM (Not Fixed)
        // if (errorMethod == 5 && compressedImage == null) {
        //     throw new IllegalArgumentException("[ERROR] : Compressed image is required for SSIM method");
        // }

        return buildQuadtree(originalImage, compressedImage, 0, 0, originalImage.getWidth(), originalImage.getHeight(), 0, this.threshold);
    }

    private QuadtreeNode buildQuadtree(ImageMatrix originalImage, ImageMatrix compressedImage, int x, int y, int width, int height, int currentDepth, double threshold) {
        QuadtreeNode node = new QuadtreeNode(originalImage, x, y, width, height);

        double error = ErrorMetrics.calculateError(originalImage, compressedImage, x, y, width, height, errorMethod);
        
        int halfWidth = width / 2;
        int halfHeight = height / 2;

        if ((error > threshold) && (width * height >= minimumBlockSize) && (halfWidth * halfHeight >= minimumBlockSize)) {
            QuadtreeNode[] children = new QuadtreeNode[4];
            

            // Top-left
            children[0] = buildQuadtree(originalImage, compressedImage, x, y, halfWidth, halfHeight, currentDepth + 1, threshold);
            
            // Top-right
            children[1] = buildQuadtree(originalImage, compressedImage, x + halfWidth, y, width - halfWidth, halfHeight, currentDepth + 1, threshold);
            
            // Bottom-left
            children[2] = buildQuadtree(originalImage, compressedImage, x, y + halfHeight, halfWidth, height - halfHeight, currentDepth + 1, threshold);
            
            // Bottom-right
            children[3] = buildQuadtree(originalImage, compressedImage, x + halfWidth, y + halfHeight, width - halfWidth, height - halfHeight, currentDepth + 1, threshold);

            node.setChildren(children);
        }

        return node;
    }

    // private double findOptimalThreshold() {
    //     // 1: Variance
    //     // 2: Mean Absolute Deviation
    //     // 3: Max Pixel Difference
    //     // 4: Entropy
    //     // 5: SSIM (Not Fixed)

    //     // Range Method Variance = 0 - 16256.25
    //     // Range Method Mean Absolute Deviation = 0 - 127.5
    //     // Range Method Max Pixel Difference = 0 - 255
    //     // Range Method Entropy = 0 - 8
    //     // Range Method SSIM = 0 - 1
    // }

    // private double calculateCompressionPercentage() {
    //     // Rumus
    //     // Persentase kompresi= 1 - ukuran file gambar asli / ukuran file gambar kompresi
    // }

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
        if (topRightDepth > maxDepth) maxDepth = topRightDepth;
        if (bottomLeftDepth > maxDepth) maxDepth = bottomLeftDepth;
        if (bottomRightDepth > maxDepth) maxDepth = bottomRightDepth;
        
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
}