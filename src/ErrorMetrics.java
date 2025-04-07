

public class ErrorMetrics {
    private static double calculateVariance(ImageMatrix image, int x, int y, int width, int height) {
        double totalVariance = 0.0;
        for (int channel = 0; channel < 3; channel++) {
            totalVariance += calculateChannelVariance(image, x, y, width, height, channel);
        }

        return totalVariance / 3.0;
    }

    private static double calculateChannelVariance(ImageMatrix image, int x, int y, int width, int height, int channel) {
        double mean = calculateMean(image, x, y, width, height, channel);
        double channelVariance = 0.0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixelValue = image.getPixel(channel, x + i, y + j);
                channelVariance += Math.pow(pixelValue - mean, 2);
            }
        }

        return channelVariance / (width * height);
    }

    private static double calculateMean(ImageMatrix image, int x, int y, int width, int height, int channel) {
        double sum = 0.0;
        int pixelCount = width * height;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                sum += image.getPixel(channel, x + i, y + j);
            }
        }

        return sum / pixelCount;
    }

    private static double calculateMeanAbsoluteDeviation(ImageMatrix image, int x, int y, int width, int height) {
        double totalMAD = 0.0;
        for (int channel = 0; channel < 3; channel++) {
            double mean = calculateMean(image, x, y, width, height, channel);
            double channelMAD = 0.0;

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int pixelValue = image.getPixel(channel, x + i, y + j);
                    channelMAD += Math.abs(pixelValue - mean);
                }
            }

            totalMAD += channelMAD / (width * height);
        }

        return totalMAD / 3.0;
    }

    private static double calculateMaxPixelDifference(ImageMatrix image, int x, int y, int width, int height) {
        double totalMaxDiff = 0.0;
        for (int channel = 0; channel < 3; channel++) {
            int maxValue = 0;
            int minValue = 255;

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int pixelValue = image.getPixel(channel, x + i, y + j);

                    if (pixelValue > maxValue) {
                        maxValue = pixelValue;
                    }
                    if (pixelValue < minValue) {
                        minValue = pixelValue;
                    }                    
                }

            }

            totalMaxDiff += (maxValue - minValue);
        }

        return totalMaxDiff / 3.0;
    }

    private static double calculateEntropy(ImageMatrix image, int x, int y, int width, int height) {
        double totalEntropy = 0.0;

        for (int channel = 0; channel < 3; channel++) {
            int[] histogram = new int[256];

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int pixelValue = image.getPixel(channel, x + i, y + j);
                    histogram[pixelValue]++;
                }
            }

            double channelEntropy = 0.0;
            int totalPixels = width * height;

            for (int count : histogram) {
                if (count > 0) {
                    double probability = (double) count / totalPixels;
                    channelEntropy -= probability * (Math.log(probability) / Math.log(2));
                }
            }

            totalEntropy += channelEntropy;
        }

        return totalEntropy / 3.0;
    }

    // Not Fixed
    public static double calculateSSIM(ImageMatrix original, ImageMatrix compressed, int x, int y, int width, int height) {
        // weightR, weightG, weightB not fixed
        double weightR = 0.2126;
        double weightG = 0.7152;
        double weightB = 0.0722;
        double ssimR = calculateChannelSSIM(original, compressed, x, y, width, height, 0);
        double ssimG = calculateChannelSSIM(original, compressed, x, y, width, height, 1);
        double ssimB = calculateChannelSSIM(original, compressed, x, y, width, height, 2);

        return weightR * ssimR + weightG * ssimG + weightB * ssimB;
    }

    private static double calculateChannelSSIM(ImageMatrix original, ImageMatrix compressed, int x, int y, int width, int height, int channel) {
        double meanOriginal = calculateMean(original, x, y, width, height, channel);
        double meanCompressed = calculateMean(compressed, x, y, width, height, channel);
        double varianceOriginal = calculateChannelVariance(original, x, y, width, height, channel);
        double varianceCompressed = calculateChannelVariance(compressed, x, y, width, height, channel);

        // Constants for stability (Not fixed)
        double C1 = 0.01 * 255;
        double C2 = 0.03 * 255;

        double ssimC = ((2 * meanOriginal * meanCompressed + C1)
                * (2 * calculateCovariance(original, compressed, x, y, width, height, channel) + C2))
                / ((meanOriginal * meanOriginal + meanCompressed * meanCompressed + C1)
                        * (varianceOriginal + varianceCompressed + C2));

        return ssimC;
    }

    private static double calculateCovariance(ImageMatrix img1, ImageMatrix img2, int x, int y, int width, int height, int channel) {
        double meanImg1 = calculateMean(img1, x, y, width, height, channel);
        double meanImg2 = calculateMean(img2, x, y, width, height, channel);

        double covariance = 0.0;
        int pixelCount = width * height;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double pixel1 = img1.getPixel(channel, x + i, y + j);
                double pixel2 = img2.getPixel(channel, x + i, y + j);

                covariance += (pixel1 - meanImg1) * (pixel2 - meanImg2);
            }
        }

        return covariance / pixelCount;
    }

    public static double calculateError(ImageMatrix original, ImageMatrix compressed, int x, int y, int width, int height, int errorMethod) {
        // 1: Variance
        // 2: Mean Absolute Deviation
        // 3: Max Pixel Difference
        // 4: Entropy
        // 5: SSIM (Not Fixed)

        double error = 0.0;
        if (errorMethod == 1){
            error = calculateVariance(original, x, y, width, height);
        } else if (errorMethod == 2){
            error = calculateMeanAbsoluteDeviation(original, x, y, width, height);
        } else if (errorMethod == 3){
            error = calculateMaxPixelDifference(original, x, y, width, height);
        } else if (errorMethod == 4){
            error = calculateEntropy(original, x, y, width, height);
        } else if (errorMethod == 5){
            error = 1.0 - calculateSSIM(original, compressed, x, y, width, height);
        } else {
            throw new IllegalArgumentException("Invalid error method");
        }

        return error;
    }
}