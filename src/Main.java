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
    private static final String WHITE = "\u001B[1;37m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            UI.printLogo();
            UI.printHeader();
            String inputPath;
            while (true) {
                System.out.print(WHITE + "\n 📁 Absolute path of input image: " + RESET);
                inputPath = scanner.nextLine().trim();
                if (IO.isValidImagePath(inputPath)) {
                    System.out.println(GREEN + " ✅ Valid image found!" + RESET);
                    break;
                }
                System.out.println(RED + " ❌ Invalid image path or format." + RESET);
            }

            int errorMethod = 0;
            while (true) {
                UI.printErrorMethodInfo();

                System.out.print(WHITE + "\n ✏️  Choose method (1-4): " + RESET);

                try {
                    errorMethod = Integer.parseInt(scanner.nextLine());
                    if (errorMethod >= 1 && errorMethod <= 4) {
                        System.out.println(GREEN + " ✅ Method selected: " + UI.getMethodName(errorMethod) + RESET);
                        break;
                    }
                } catch (Exception ignored) {
                }

                System.out.println(RED + " ❌ Please select a valid number (1-4)." + RESET);
            }

            ImageMatrix compressedRef = null;
            if (errorMethod == 5) {
                while (true) {
                    System.out.print(
                            WHITE + "\n 📁 Enter absolute path of compressed reference image for SSIM: " + RESET);
                    String refPath = scanner.nextLine().trim();
                    if (IO.isValidImagePath(refPath)) {
                        compressedRef = IO.readImage(refPath);
                        System.out.println(GREEN + " ✅ Reference image loaded!" + RESET);
                        break;
                    }
                    System.out.println(RED + " ❌ Invalid image path or format." + RESET);
                }
            }

            double threshold;
            while (true) {
                System.out.print(
                        WHITE + "\n 🔍 Enter threshold " + YELLOW + UI.getThresholdRange(errorMethod) + RESET + ": ");
                try {
                    threshold = Double.parseDouble(scanner.nextLine());
                    if (IO.isValidThreshold(threshold, errorMethod)) {
                        System.out.println(GREEN + " ✅ Threshold set!" + RESET);
                        break;
                    }
                } catch (Exception ignored) {
                }
                System.out.println(RED + " ❌ Threshold out of range for selected method." + RESET);
            }

            int minBlock;
            while (true) {
                System.out.print(WHITE + "\n 📏 Minimum block size: " + RESET);
                try {
                    minBlock = Integer.parseInt(scanner.nextLine());
                    if (minBlock > 0) {
                        System.out.println(GREEN + " ✅ Minimum block size set to " + minBlock + RESET);
                        break;
                    }
                } catch (Exception ignored) {
                }
                System.out.println(RED + " ❌ Must be a positive integer." + RESET);
            }

            double compressionTarget;
            while (true) {
                System.out.print(WHITE + "\n 🎯 Target compression percentage " + YELLOW + "(0.0-1.0)" + RESET + ": ");
                try {
                    compressionTarget = Double.parseDouble(scanner.nextLine());
                    if (compressionTarget >= 0.0 && compressionTarget <= 1.0) {
                        if (compressionTarget == 0.0) {
                            System.out.println(BLUE + " ℹ️ Target compression disabled" + RESET);
                            break;
                        } else {
                            // System.out.println(GREEN + " ✅ Target set to " + (compressionTarget * 100) + "%" + RESET);
                            System.out.println(BLUE + " 😔 Sorry, the target compression percentage feature is not available yet" + RESET);
                        }
                        // break;
                    }
                } catch (Exception ignored) {
                }
                // System.out.println(RED + " ❌ Must be between 0.0 and 1.0." + RESET);
                System.out.println(RED + " ❌ Must be 0.0." + RESET);
            }

            String outputPath;
            while (true) {
                System.out.print(WHITE + "\n 💾 Absolute path for compressed image: " + RESET);
                outputPath = scanner.nextLine().trim();

                if (outputPath.equals(inputPath)) {
                    System.out.println(RED + " ❌ Output path cannot be the same as input path." + RESET);
                    continue;
                }

                File outputFile = new File(outputPath);
                File outputDir = outputFile.getParentFile();
                if (outputDir != null && !outputDir.exists()) {
                    System.out
                            .println(RED + " ❌ Output directory doesn't exist: " + outputDir.getAbsolutePath() + RESET);
                    continue;
                }

                if (outputFile.exists()) {
                    System.out.println(RED + " ❌ Output file already exists: " + outputPath + RESET);
                    continue;
                }

                if (IO.hasSameExtension(inputPath, outputPath)) {
                    System.out.println(GREEN + " ✅ Output path set!" + RESET);
                    break;
                }
                System.out.println(RED + " ❌ Output file must have the same extension as input." + RESET);
            }

            boolean saveGif = false;
            String gifPath = null;
            while (true) {
                System.out.print(WHITE + "\n 🎬 Do you want to save compression GIF? " + YELLOW + "(y/n)" + RESET + ": ");
                String gifChoice = scanner.nextLine().trim().toLowerCase();
                if (gifChoice.equals("y")) {
                    saveGif = true;
                    while (true) {
                        System.out.print(WHITE + "\n 📁 Enter absolute path to save GIF: " + RESET);
                        gifPath = scanner.nextLine().trim();

                        if (gifPath.equals(inputPath) || gifPath.equals(outputPath)) {
                            System.out.println(RED + " ❌ GIF path cannot be the same as input or output path." + RESET);
                            continue;
                        }

                        File gifFile = new File(gifPath);
                        File gifDir = gifFile.getParentFile();
                        if (gifDir != null && !gifDir.exists()) {
                            System.out.println(
                                    RED + " ❌ GIF directory doesn't exist: " + gifDir.getAbsolutePath() + RESET);
                            continue;
                        }

                        if (gifFile.exists()) {
                            System.out.println(RED + " ❌ GIF file already exists: " + gifPath + RESET);
                            continue;
                        }

                        if (gifPath.toLowerCase().endsWith(".gif")) {
                            System.out.println(GREEN + " ✅ GIF path set!" + RESET);
                            break;
                        }
                        System.out.println(RED + " ❌ Output file must have a .gif extension." + RESET);
                    }
                    break;
                } else if (gifChoice.equals("n")) {
                    saveGif = false;
                    break;
                }
                System.out.println(RED + " ❌ Please type 'y' or 'n'." + RESET);
            }

            System.out.println("\n" + PURPLE + "\n ⚙️  Processing image compression..." + RESET);
            UI.printProgressBar(0);

            long startTime = System.nanoTime();

            ImageMatrix original = IO.readImage(inputPath);
            int width = original.getWidth();
            int height = original.getHeight();
            UI.printProgressBar(20);

            QuadtreeCompression compressor = new QuadtreeCompression(
                    original, compressedRef, errorMethod, threshold, minBlock, compressionTarget);
            UI.printProgressBar(40);
            

            QuadtreeNode root = compressor.compress();
            UI.printProgressBar(70);

            int treeDepth = compressor.calculateTreeDepth(root);
            int nodeCount = compressor.calculateNodeCount(root);

            UI.printProgressBar(80);

            ImageMatrix compressed = IO.reconstructImageFromQuadtree(root, width, height);
            IO.writeCompressedImage(compressed, outputPath);
            UI.printProgressBar(90);
            
            if (saveGif && gifPath != null) {
                System.out.println(BLUE + "\n\n 🎬 Generating compression GIF..." + RESET);
                List<BufferedImage> frames = IO.reconstructGIFFromQuadtree(root, width, height,
                        treeDepth);
                IO.createCompressionGif(frames, gifPath, 500);
            }
            UI.printProgressBar(100);

            long endTime = System.nanoTime();
            double executionTime = (endTime - startTime) / 1_000_000.0;
            double compressionPercentage = IO.calcCompressionPercentage(inputPath, outputPath);

            UI.printResultMenu(treeDepth, nodeCount, executionTime, compressionPercentage, inputPath, outputPath, gifPath, saveGif);
            System.out.println("\n" + PURPLE + "Thank you for using Quadtree Image Compression!" + RESET);

        } catch (IOException e) {
            System.err.println(RED + " ❌ Error processing image: " + e.getMessage() + RESET);
        } finally {
            scanner.close();
        }
    }
}