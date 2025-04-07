import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE_BOLD = "\u001B[1;37m";
    private static final String WHITE_BG = "\u001B[47m";
    private static final String BLACK = "\u001B[30m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            printLogo();
            System.out.println(WHITE_BOLD + "=== Quadtree Image Compression CLI ===" + RESET);
            System.out.println(CYAN + "Transform your images with efficient quadtree compression" + RESET);
            System.out.println();

            String inputPath;
            while (true) {
                System.out.print(WHITE_BOLD + "📁 Absolute path of input image: " + RESET);
                inputPath = scanner.nextLine().trim();
                if (IO.isValidImagePath(inputPath)) {
                    System.out.println(GREEN + "✅ Valid image found!" + RESET);
                    break;
                }
                System.out.println(RED + "❌ Invalid image path or format." + RESET);
            }

            int errorMethod = 0;
            while (true) {
                System.out.println("\n" + WHITE_BOLD + "📊 Error Measurement Method:" + RESET);
                System.out.println(BLUE + "1. " + RESET + "Variance " + YELLOW + "(0 - 65025)" + RESET);
                System.out.println(BLUE + "2. " + RESET + "Mean Absolute Deviation " + YELLOW + "(0 - 255)" + RESET);
                System.out.println(BLUE + "3. " + RESET + "Max Pixel Difference " + YELLOW + "(0 - 255)" + RESET);
                System.out.println(BLUE + "4. " + RESET + "Entropy " + YELLOW + "(0.0 - ~8.0)" + RESET);
                System.out.println(BLUE + "5. " + RESET + "SSIM " + YELLOW + "(0.0 - 1.0)" + RESET);
                System.out.print(WHITE_BOLD + "Choose method (1-5): " + RESET);
                try {
                    errorMethod = Integer.parseInt(scanner.nextLine());
                    if (errorMethod >= 1 && errorMethod <= 5) {
                        System.out.println(GREEN + "✅ Method selected: " + getMethodName(errorMethod) + RESET);
                        break;
                    }
                } catch (Exception ignored) {
                }
                System.out.println(RED + "❌ Please select a valid number (1-5)." + RESET);
            }

            ImageMatrix compressedRef = null;
            if (errorMethod == 5) {
                while (true) {
                    System.out.print(
                            WHITE_BOLD + "📁 Enter absolute path of compressed reference image for SSIM: " + RESET);
                    String refPath = scanner.nextLine().trim();
                    if (IO.isValidImagePath(refPath)) {
                        compressedRef = IO.readImage(refPath);
                        System.out.println(GREEN + "✅ Reference image loaded!" + RESET);
                        break;
                    }
                    System.out.println(RED + "❌ Invalid image path or format." + RESET);
                }
            }

            double threshold;
            while (true) {
                System.out.print(
                        WHITE_BOLD + "🔍 Enter threshold " + YELLOW + getThresholdRange(errorMethod) + RESET + ": ");
                try {
                    threshold = Double.parseDouble(scanner.nextLine());
                    if (IO.isValidThreshold(threshold, errorMethod)) {
                        System.out.println(GREEN + "✅ Threshold set!" + RESET);
                        break;
                    }
                } catch (Exception ignored) {
                }
                System.out.println(RED + "❌ Threshold out of range for selected method." + RESET);
            }

            int minBlock;
            while (true) {
                System.out.print(WHITE_BOLD + "📏 Minimum block size: " + RESET);
                try {
                    minBlock = Integer.parseInt(scanner.nextLine());
                    if (minBlock > 0) {
                        System.out.println(GREEN + "✅ Minimum block size set to " + minBlock + RESET);
                        break;
                    }
                } catch (Exception ignored) {
                }
                System.out.println(RED + "❌ Must be a positive integer." + RESET);
            }

            double compressionTarget;
            while (true) {
                System.out
                        .print(WHITE_BOLD + "🎯 Target compression percentage " + YELLOW + "(0.0-1.0)" + RESET + ": ");
                try {
                    compressionTarget = Double.parseDouble(scanner.nextLine());
                    if (compressionTarget >= 0.0 && compressionTarget <= 1.0) {
                        if (compressionTarget == 0.0) {
                            System.out.println(BLUE + "ℹ️ Target compression disabled" + RESET);
                        } else {
                            System.out.println(GREEN + "✅ Target set to " + (compressionTarget * 100) + "%" + RESET);
                        }
                        break;
                    }
                } catch (Exception ignored) {
                }
                System.out.println(RED + "❌ Must be between 0.0 and 1.0." + RESET);
            }

            String outputPath;
            while (true) {
                System.out.print(WHITE_BOLD + "💾 Absolute path for compressed image: " + RESET);
                outputPath = scanner.nextLine().trim();

                if (outputPath.equals(inputPath)) {
                    System.out.println(RED + "❌ Output path cannot be the same as input path." + RESET);
                    continue;
                }

                File outputFile = new File(outputPath);
                File outputDir = outputFile.getParentFile();
                if (outputDir != null && !outputDir.exists()) {
                    System.out.println(RED + "❌ Output directory doesn't exist: " + outputDir.getAbsolutePath() + RESET);
                    continue;
                }

                if (outputFile.exists()) {
                    System.out.println(RED + "❌ Output file already exists: " + outputPath + RESET);
                    continue;
                }

                if (IO.hasSameExtension(inputPath, outputPath)) {
                    System.out.println(GREEN + "✅ Output path set!" + RESET);
                    break;
                }
                System.out.println(RED + "❌ Output file must have the same extension as input." + RESET);
            }

            boolean saveGif = false;
            String gifPath = null;
            while (true) {
                System.out.print(
                        WHITE_BOLD + "🎬 Do you want to save compression GIF? " + YELLOW + "(y/n)" + RESET + ": ");
                String gifChoice = scanner.nextLine().trim().toLowerCase();
                if (gifChoice.equals("y")) {
                    saveGif = true;
                    while (true) {
                        System.out.print(WHITE_BOLD + "📁 Enter absolute path to save GIF: " + RESET);
                        gifPath = scanner.nextLine().trim();

                        if (gifPath.equals(inputPath) || gifPath.equals(outputPath)) {
                            System.out.println(RED + "❌ GIF path cannot be the same as input or output path." + RESET);
                            continue;
                        }

                        File gifFile = new File(gifPath);
                        File gifDir = gifFile.getParentFile();
                        if (gifDir != null && !gifDir.exists()) {
                            System.out.println(RED + "❌ GIF directory doesn't exist: " + gifDir.getAbsolutePath() + RESET);
                            continue;
                        }

                        if (gifFile.exists()) {
                            System.out.println(RED + "❌ GIF file already exists: " + gifPath + RESET);
                            continue;
                        }

                        if (gifPath.toLowerCase().endsWith(".gif")) {
                            System.out.println(GREEN + "✅ GIF path set!" + RESET);
                            break;
                        }
                        System.out.println(RED + "❌ Output file must have a .gif extension." + RESET);
                    }
                    break;
                } else if (gifChoice.equals("n")) {
                    saveGif = false;
                    break;
                }
                System.out.println(RED + "❌ Please type 'y' or 'n'." + RESET);
            }

            System.out.println("\n" + PURPLE + "⚙️  Processing image compression..." + RESET);
            printProgressBar(0);

            long startTime = System.nanoTime();

            ImageMatrix original = IO.readImage(inputPath);
            int width = original.getWidth();
            int height = original.getHeight();
            printProgressBar(20);

            QuadtreeCompression compressor = new QuadtreeCompression(
                    original, compressedRef, errorMethod, threshold, minBlock, compressionTarget);
            printProgressBar(40);

            QuadtreeNode root = compressor.compress();
            printProgressBar(70);

            ImageMatrix compressed = IO.reconstructImageFromQuadtree(root, width, height);
            IO.writeCompressedImage(compressed, outputPath);
            printProgressBar(90);

            if (saveGif && gifPath != null) {
                System.out.println(BLUE + "\n🎬 Generating compression GIF..." + RESET);
                List<BufferedImage> frames = IO.reconstructGIFFromQuadtree(root, width, height,
                        compressor.getTreeDepth());
                IO.createCompressionGif(frames, gifPath, 500);
            }
            printProgressBar(100);

            long endTime = System.nanoTime();
            double executionTime = (endTime - startTime) / 1_000_000.0;

            System.out.println("\n\n" + WHITE_BOLD + "=== 📊 Compression Results ===" + RESET);
            System.out.println("┌─────────────────────────┬─────────────────────────┐");
            System.out.printf("│ %-23s │ " + CYAN + "%-23s" + RESET + " │%n", "Execution Time",
                    String.format("%.4f ms", executionTime));
            System.out.println("├─────────────────────────┼─────────────────────────┤");
            System.out.printf("│ %-23s │ " + YELLOW + "%-23s" + RESET + " │%n", "Original Size",
                    String.format("%d bytes", IO.calcFileSize(inputPath)));
            System.out.println("├─────────────────────────┼─────────────────────────┤");
            System.out.printf("│ %-23s │ " + GREEN + "%-23s" + RESET + " │%n", "Compressed Size",
                    String.format("%d bytes", IO.calcFileSize(outputPath)));
            System.out.println("├─────────────────────────┼─────────────────────────┤");
            double compressionPercentage = IO.calcCompressionPercentage(inputPath, outputPath);
            String compressionColor = compressionPercentage > 50 ? GREEN : (compressionPercentage > 25 ? YELLOW : RED);
            System.out.printf("│ %-23s │ " + compressionColor + "%-23s" + RESET + " │%n", "Compression Percentage",
                    String.format("%.2f%%", compressionPercentage));
            System.out.println("├─────────────────────────┼─────────────────────────┤");
            System.out.printf("│ %-23s │ " + BLUE + "%-23s" + RESET + " │%n", "Tree Depth",
                    String.format("%d", compressor.getTreeDepth()));
            System.out.println("├─────────────────────────┼─────────────────────────┤");
            System.out.printf("│ %-23s │ " + BLUE + "%-23s" + RESET + " │%n", "Total Nodes",
                    String.format("%d", compressor.getNodeCount()));
            System.out.println("└─────────────────────────┴─────────────────────────┘");

            System.out.println(WHITE_BOLD + "\n📄 Output Files:" + RESET);
            System.out.println(GREEN + "✅ Compressed Image: " + RESET + outputPath);
            if (saveGif && gifPath != null) {
                System.out.println(GREEN + "✅ Compression GIF: " + RESET + gifPath);
            }

            System.out.println("\n" + PURPLE + "Thank you for using Quadtree Image Compression!" + RESET);

        } catch (IOException e) {
            System.err.println(RED + "❌ Error processing image: " + e.getMessage() + RESET);
        } finally {
            scanner.close();
        }
    }

    private static String getMethodName(int method) {
        switch (method) {
            case 1:
                return "Variance";
            case 2:
                return "Mean Absolute Deviation";
            case 3:
                return "Max Pixel Difference";
            case 4:
                return "Entropy";
            case 5:
                return "SSIM";
            default:
                return "Unknown";
        }
    }

    private static String getThresholdRange(int method) {
        switch (method) {
            case 1:
                return "(0-65025)";
            case 2:
            case 3:
                return "(0-255)";
            case 4:
                return "(0.0-8.0)";
            case 5:
                return "(0.0-1.0)";
            default:
                return "";
        }
    }

    private static void printProgressBar(int percent) {
        final int width = 50;
        int completed = width * percent / 100;

        System.out.print("\r" + WHITE_BOLD + "[");
        for (int i = 0; i < width; i++) {
            if (i < completed) {
                System.out.print(GREEN + "█" + RESET);
            } else {
                System.out.print(" ");
            }
        }
        System.out.print(WHITE_BOLD + "] " + percent + "%" + RESET);
        System.out.flush();
    }

    private static void printLogo() {
        System.out.println(CYAN + "   ____                 _ _                  " + RESET);
        System.out.println(CYAN + "  / __ \\               | | |                 " + RESET);
        System.out.println(CYAN + " | |  | |_   _  __ _  _| | |_ _ __ ___  ___  " + RESET);
        System.out.println(CYAN + " | |  | | | | |/ _` |/ _` | __| '__/ _ \\/ _ \\ " + RESET);
        System.out.println(CYAN + " | |__| | |_| | (_| | (_| | |_| | |  __/  __/" + RESET);
        System.out.println(CYAN + "  \\___\\_\\\\__,_|\\__,_|\\__,_|\\__|_|  \\___|\\___|" + RESET);
        System.out.println();
    }
}