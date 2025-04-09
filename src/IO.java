import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

public class IO {
    public static ImageMatrix readImage(String imagePath) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
        return new ImageMatrix(bufferedImage);
    }

    public static long calcFileSize(String imagePath) {
        File file = new File(imagePath);
        return file.length();
    }

    public static double calcCompressionPercentage(String originalPath, String compressedPath) {
        long originalSize = calcFileSize(originalPath);
        long compressedSize = calcFileSize(compressedPath);

        if (originalSize == 0) {
            throw new IllegalArgumentException("[ERROR] : Original file size cannot be zero.");
        }

        double compressionRatio = 1.0 - ((double) compressedSize / originalSize);
        return compressionRatio * 100;
    }

    // Output Image
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
            // Top-left
            reconstructNode(node.getChildren()[0], image);
            
            // Top-right
            reconstructNode(node.getChildren()[1], image);
            
            // Bottom-left
            reconstructNode(node.getChildren()[2], image);
            
            // Bottom-right
            reconstructNode(node.getChildren()[3], image);
        }
    }

    public static void writeCompressedImage(ImageMatrix image, String outputPath) throws IOException {
        BufferedImage bufferedImage = image.toBufferedImage();
        File outputFile = new File(outputPath);
        String format = outputPath.substring(outputPath.lastIndexOf('.') + 1);
        ImageIO.write(bufferedImage, format, outputFile);
    }

    // Output GIF
    public static List<BufferedImage> reconstructGIFFromQuadtree(QuadtreeNode root, int width, int height, int maxDepth) {
        List<BufferedImage> frames = new ArrayList<>();

        for (int i = 0; i <= maxDepth; i++) {
            ImageMatrix frameImage = new ImageMatrix(width, height);
            IO.reconstructNodeForGIF(root, frameImage, 0, i);
            frames.add(frameImage.toBufferedImage());
        }

        return frames;
    }

    public static void reconstructNodeForGIF(QuadtreeNode node, ImageMatrix image, int currentDepth, int maxDepth) {
        if (node.isLeaf() || currentDepth >= maxDepth) {
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
            // Top-left
            reconstructNodeForGIF(node.getChildren()[0], image, currentDepth + 1, maxDepth);
            
            // Top-right
            reconstructNodeForGIF(node.getChildren()[1], image, currentDepth + 1, maxDepth);
            
            // Bottom-left
            reconstructNodeForGIF(node.getChildren()[2], image, currentDepth + 1, maxDepth);
            
            // Bottom-right
            reconstructNodeForGIF(node.getChildren()[3], image, currentDepth + 1, maxDepth);
        }
    }

    public static void createCompressionGif(List<BufferedImage> frames, String outputPath, int delayTime) throws IOException {
        ImageWriter gifWriter = ImageIO.getImageWritersBySuffix("gif").next();
        ImageWriteParam imageWriteParam = gifWriter.getDefaultWriteParam();
        ImageOutputStream output = ImageIO.createImageOutputStream(new File(outputPath));
        gifWriter.setOutput(output);

        gifWriter.prepareWriteSequence(null);

        for (int i = 0; i < frames.size(); i++) {
            BufferedImage frame = frames.get(i);
            IIOMetadata metadata = gifWriter.getDefaultImageMetadata(new ImageTypeSpecifier(frame), imageWriteParam);
            String metaFormat = metadata.getNativeMetadataFormatName();
            IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormat);
            IIOMetadataNode graphicsControlExtensionNode = new IIOMetadataNode("GraphicControlExtension");
            graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
            graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
            graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
            graphicsControlExtensionNode.setAttribute("delayTime", String.valueOf(delayTime / 10));
            graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");
            root.appendChild(graphicsControlExtensionNode);

            // Loop forever
            if (i == 0) {
                IIOMetadataNode appExtensions = new IIOMetadataNode("ApplicationExtensions");
                IIOMetadataNode appExtension = new IIOMetadataNode("ApplicationExtension");
                appExtension.setAttribute("applicationID", "NETSCAPE");
                appExtension.setAttribute("authenticationCode", "2.0");

                byte[] loopForever = new byte[] { 0x1, 0x0, 0x0 };
                appExtension.setUserObject(loopForever);
                appExtensions.appendChild(appExtension);
                root.appendChild(appExtensions);
            }

            metadata.setFromTree(metaFormat, root);
            gifWriter.writeToSequence(new IIOImage(frame, null, metadata), imageWriteParam);
        }

        gifWriter.endWriteSequence();
        output.close();
    }

    // Validation IO
    public static boolean isValidImagePath(String path) {
        String ext = getExtension(path).toLowerCase();
        if (!ext.equals("jpg") && !ext.equals("jpeg") && !ext.equals("png")) {
            System.out.println(" ❌ Invalid image format! Allowed: .jpg, .jpeg, .png");
            return false;
        }

        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            System.out.println(" ❌ File does not exist.");
            return false;
        }
    
        return true;
    }
    

    private static String getExtension(String path) {
        int i = path.lastIndexOf('.');
        return (i > 0) ? path.substring(i + 1).toLowerCase() : "";
    }

    public static boolean hasSameExtension(String path1, String path2) {
        return getExtension(path1).equals(getExtension(path2));
    }

    public static boolean isValidThreshold(double value, int method) {
        if (method == 1) {
            return value >= 0 && value <= 16256.25;
        } else if (method == 2) {
            return value >= 0 && value <= 127.5;
        } else if (method == 3) {
            return value >= 0 && value <= 255;
        } else if (method == 4) {
            return value >= 0 && value <= 8;
        } else if (method == 5) {
            return value >= 0 && value <= 1;
        } else {
            return false;
        }
    }
}