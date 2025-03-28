package utils;
import java.awt.image.BufferedImage;

public class ErrorMetrics {
    public enum ErrorMethod {
        VARIANCE,
        MEAN_ABSOLUTE_DEVIATION,
        MAX_PIXEL_DIFFERENCE,
        ENTROPY,
        SSIM
    }

    private static double calculateVariance(BufferedImage image, int x, int y, int width, int height) {
        double totalVariance = 0.0;
        for (int channel = 0; channel < 3; channel++) {
            totalVariance += calculateChannelVariance(image, x, y, width, height, channel);
        }

        return totalVariance / 3.0;
    }

    private static double calculateChannelVariance(BufferedImage image, int x, int y, int width, int height, int channel) {
        double mean = calculateMean(image, x, y, width, height, channel);
        double channelVariance = 0.0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = image.getRGB(x + i, y + j);
                int pixelValue = getColorChannel(rgb, channel);
                channelVariance += Math.pow(pixelValue - mean, 2);
            }
        }

        return channelVariance / (width * height);
    }

    private static double calculateMean(BufferedImage image, int x, int y, int width, int height, int channel) {
        double sum = 0.0;
        int pixelCount = width * height;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = image.getRGB(x + i, y + j);
                sum += getColorChannel(rgb, channel);
            }
        }

        return sum / pixelCount;
    }

    private static int getColorChannel(int rgb, int channel) {
        // [ Alpha (8 bit) | Red (8 bit) | Green (8 bit) | Blue (8 bit) ]
        return switch (channel) {
            case 0 -> (rgb >> 16) & 0xFF; // Red
            case 1 -> (rgb >> 8) & 0xFF; // Green
            case 2 -> rgb & 0xFF; // Blue
            default -> throw new IllegalArgumentException("Invalid channel");
        };
    }

    private static double calculateMeanAbsoluteDeviation(BufferedImage image, int x, int y, int width, int height) {
        double totalMAD = 0.0;
        for (int channel = 0; channel < 3; channel++) {
            double mean = calculateMean(image, x, y, width, height, channel);
            double channelMAD = 0.0;

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int rgb = image.getRGB(x + i, y + j);
                    int pixelValue = getColorChannel(rgb, channel);
                    channelMAD += Math.abs(pixelValue - mean);
                }
            }

            totalMAD += channelMAD / (width * height);
        }

        return totalMAD / 3.0;
    }

    private static double calculateMaxPixelDifference(BufferedImage image, int x, int y, int width, int height) {
        double totalMaxDiff = 0.0;
        for (int channel = 0; channel < 3; channel++) {
            int maxValue = 0;
            int minValue = 255;

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int rgb = image.getRGB(x + i, y + j);
                    int pixelValue = getColorChannel(rgb, channel);

                    maxValue = Math.max(maxValue, pixelValue);
                    minValue = Math.min(minValue, pixelValue);
                }
            }

            totalMaxDiff += (maxValue - minValue);
        }

        return totalMaxDiff / 3.0;
    }

    private static double calculateEntropy(BufferedImage image, int x, int y, int width, int height) {
        double totalEntropy = 0.0;

        for (int channel = 0; channel < 3; channel++) {
            int[] histogram = new int[256];

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int rgb = image.getRGB(x + i, y + j);
                    int pixelValue = getColorChannel(rgb, channel);
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

    public static double calculateSSIM(BufferedImage original, BufferedImage compressed, int x, int y, int width,int height) {
        // weightR, weightG, weightB not fixed
        double weightR = 0.2126;
        double weightG = 0.7152;
        double weightB = 0.0722;
        double ssimR = calculateChannelSSIM(original, compressed, x, y, width, height, 0);
        double ssimG = calculateChannelSSIM(original, compressed, x, y, width, height, 1);
        double ssimB = calculateChannelSSIM(original, compressed, x, y, width, height, 2);

        return weightR * ssimR + weightG * ssimG + weightB * ssimB;
    }

    private static double calculateChannelSSIM(BufferedImage original, BufferedImage compressed, int x, int y, int width, int height, int channel) {
        double meanOriginal = calculateMean(original, x, y, width, height, channel);
        double meanCompressed = calculateMean(compressed, x, y, width, height, channel);
        double varianceOriginal = calculateChannelVariance(original, x, y, width, height, channel);
        double varianceCompressed = calculateChannelVariance(compressed, x, y, width, height, channel);
        
        // Constants for stability (Not fixed)
        double C1 = 0.01 * 255;
        double C2 = 0.03 * 255;

        double ssimC = ((2 * meanOriginal * meanCompressed + C1) * (2 * calculateCovariance(original, compressed, x, y, width, height, channel) + C2)) / ((meanOriginal * meanOriginal + meanCompressed * meanCompressed + C1) * (varianceOriginal + varianceCompressed + C2));
        
        return ssimC;
    }

    private static double calculateCovariance(BufferedImage img1, BufferedImage img2, int x, int y, int width, int height, int channel) {
        double meanImg1 = calculateMean(img1, x, y, width, height, channel);
        double meanImg2 = calculateMean(img2, x, y, width, height, channel);

        double covariance = 0.0;
        int pixelCount = width * height;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb1 = img1.getRGB(x + i, y + j);
                int rgb2 = img2.getRGB(x + i, y + j);

                double pixel1 = getColorChannel(rgb1, channel);
                double pixel2 = getColorChannel(rgb2, channel);

                covariance += (pixel1 - meanImg1) * (pixel2 - meanImg2);
            }
        }

        return covariance / pixelCount;
    }
    
    public static double calculateError(BufferedImage original, BufferedImage compressed, int x, int y, int width, int height, ErrorMethod method) {
        return switch (method) {
            case VARIANCE -> calculateVariance(original, x, y, width, height);
            case MEAN_ABSOLUTE_DEVIATION -> calculateMeanAbsoluteDeviation(original, x, y, width, height);
            case MAX_PIXEL_DIFFERENCE -> calculateMaxPixelDifference(original, x, y, width, height);
            case ENTROPY -> calculateEntropy(original, x, y, width, height);
            case SSIM -> 1.0 - calculateSSIM(original, compressed, x, y, width, height);
            default -> throw new IllegalArgumentException("Invalid error method");
        };
    }
}