public class UI {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[1;37m";

    public static void printLogo() {
        System.out.println(CYAN + "  ┌─────────────────────────────────────────────┐" + RESET);
        System.out.println(CYAN + "  │                                             │" + RESET);
        System.out.println(CYAN + "  │    " + PURPLE + "██████  " + GREEN + "██    " + BLUE + "██  " + RED + " █████  "+ YELLOW + "████████       " + CYAN + "│" + RESET);
        System.out.println(CYAN + "  │    " + PURPLE + "██  ██  " + GREEN + "██    " + BLUE + "██  " + RED + "██   ██ "+ YELLOW + "██     ██      " + CYAN + "│" + RESET);
        System.out.println(CYAN + "  │    " + PURPLE + "██  ██  " + GREEN + "██    " + BLUE + "██  " + RED + "███████ "+ YELLOW + "███     ███    " + CYAN + "│" + RESET);
        System.out.println(CYAN + "  │    " + PURPLE + "██  ██  " + GREEN + "██    " + BLUE + "██  " + RED + "██   ██ "+ YELLOW + " ██    ██      " + CYAN + "│" + RESET);
        System.out.println(CYAN + "  │    " + PURPLE + "██████  " + GREEN + "██████ " + BLUE + "███ " + RED + "██   ██ "+ YELLOW + "███████       " + CYAN + "│" + RESET);
        System.out.println(CYAN + "  │    " + PURPLE + "     ██  " + CYAN + "                                │" + RESET);
        System.out.println(CYAN + "  │                                             │" + RESET);
        System.out.println(CYAN + "  │    " + PURPLE + "████████ " + GREEN + "████   " + BLUE + "██████ " + RED+ "██████            " + CYAN + "│" + RESET);
        System.out.println(CYAN + "  │       " + PURPLE + "██    " + GREEN + "██  ██ " + BLUE + "██     " + RED+ "██                " + CYAN + "│" + RESET);
        System.out.println(CYAN + "  │       " + PURPLE + "██    " + GREEN + "█████  " + BLUE + "████   " + RED+ "█████             " + CYAN + "│" + RESET);
        System.out.println(CYAN + "  │       " + PURPLE + "██    " + GREEN + "██  ██ " + BLUE + "██     " + RED+ "██                " + CYAN + "│" + RESET);
        System.out.println(CYAN + "  │       " + PURPLE + "██    " + GREEN + "██  ██ " + BLUE + "██████ " + RED+ "██████            " + CYAN + "│" + RESET);
        System.out.println(CYAN + "  │                                             │" + RESET);
        System.out.println(CYAN + "  │            " + YELLOW + "[ IMAGE COMPRESSION ]" + CYAN + "            │" + RESET);
        System.out.println(CYAN + "  └─────────────────────────────────────────────┘" + RESET);
    }

    public static void printHeader() {
        System.out.println(CYAN + "╔════════════════════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + "║" + WHITE+ "                      QUADTREE IMAGE COMPRESSION TOOL                   " + CYAN + "║" + RESET);
        System.out.println(CYAN + "╚════════════════════════════════════════════════════════════════════════╝" + RESET);
    }

    public static void printProgressBar(int percent) {
        final int width = 40;
        int completed = width * percent / 100;
        String[] spinChars = new String[] { "⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏" };
        String spinChar = spinChars[(percent / 5) % spinChars.length];

        StringBuilder bar = new StringBuilder();
        bar.append("\r").append(BLUE).append(spinChar).append(" ").append(WHITE).append("[");

        for (int i = 0; i < width; i++) {
            if (i < completed) {
                bar.append(GREEN).append("■").append(RESET);
            } else {
                bar.append(" ");
            }
        }

        bar.append(WHITE).append("] ").append(CYAN).append(String.format("%3d%%", percent)).append(RESET);
        System.out.print(bar);
        System.out.flush();
    }

    public static void printErrorMethodInfo() {
        System.out.println("\n" + CYAN+ "╔════════════════════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + "║                      " + WHITE + "📊 ERROR MEASUREMENT METHOD" + CYAN+ "                       ║" + RESET);
        System.out.println(CYAN + "╠════════════════════════════════════════════════════════════════════════╣" + RESET);
        System.out.println(CYAN + "║  " + BLUE + "1. " + RESET + "Variance                         " + YELLOW+ "(0 - 16256.25)" + CYAN + "                    ║" + RESET);
        System.out.println(CYAN + "║  " + BLUE + "2. " + RESET + "Mean Absolute Deviation          " + YELLOW+ "(0 - 127.5)" + CYAN + "                       ║" + RESET);
        System.out.println(CYAN + "║  " + BLUE + "3. " + RESET + "Max Pixel Difference             " + YELLOW+ "(0 - 255)" + CYAN + "                         ║" + RESET);
        System.out.println(CYAN + "║  " + BLUE + "4. " + RESET + "Entropy                          " + YELLOW+ "(0 - 8)" + CYAN + "                           ║" + RESET);
        System.out.println(CYAN + "╚════════════════════════════════════════════════════════════════════════╝" + RESET);
    }

    public static void printResultMenu(int treeDepth, int nodeCount,double executionTime, double compressionPercentage, String inputPath, String outputPath, String gifPath, boolean saveGif, double compressionTarget, double threshold) {
        String compressionColor = compressionPercentage > 50 ? GREEN : (compressionPercentage > 25 ? YELLOW : RED);
        
        System.out.println("\n\n" + PURPLE + "╔═══════════════════════════════════════════╗" + RESET);
        System.out.println(PURPLE + "║" + WHITE + "           COMPRESSION RESULTS             " + PURPLE + "║" + RESET);
        System.out.println(PURPLE + "╠═══════════════════════╦═══════════════════╣" + RESET);
        System.out.printf(PURPLE + "║" + RESET + " %-22s" + PURPLE + "║" + CYAN + " %-18s" + PURPLE + "║%n" + RESET,"Execution Time", String.format("%.4f ms", executionTime));
        System.out.println(PURPLE + "╠═══════════════════════╬═══════════════════╣" + RESET);
        System.out.printf(PURPLE + "║" + RESET + " %-22s" + PURPLE + "║" + YELLOW + " %-18s" + PURPLE + "║%n" + RESET,"Original Size", String.format("%d bytes", IO.calcFileSize(inputPath)));
        System.out.println(PURPLE + "╠═══════════════════════╬═══════════════════╣" + RESET);
        System.out.printf(PURPLE + "║" + RESET + " %-22s" + PURPLE + "║" + GREEN + " %-18s" + PURPLE + "║%n" + RESET,"Compressed Size", String.format("%d bytes", IO.calcFileSize(outputPath)));
        System.out.println(PURPLE + "╠═══════════════════════╬═══════════════════╣" + RESET);
        System.out.printf(PURPLE + "║" + RESET + " %-22s" + PURPLE + "║" + compressionColor + " %-18s" + PURPLE + "║%n"+ RESET,"Compression Rate", String.format("%.4f%%", compressionPercentage));
        System.out.println(PURPLE + "╠═══════════════════════╬═══════════════════╣" + RESET);
        System.out.printf(PURPLE + "║" + RESET + " %-22s" + PURPLE + "║" + BLUE + " %-18s" + PURPLE + "║%n" + RESET,"Tree Depth", String.format("%d", treeDepth));
        System.out.println(PURPLE + "╠═══════════════════════╬═══════════════════╣" + RESET);
        System.out.printf(PURPLE + "║" + RESET + " %-22s" + PURPLE + "║" + BLUE + " %-18s" + PURPLE + "║%n" + RESET,"Total Nodes", String.format("%d", nodeCount));
        System.out.println(PURPLE + "╠═══════════════════════╬═══════════════════╣" + RESET);

        if (compressionTarget != 0){
            System.out.printf(PURPLE + "║" + RESET + " %-22s" + PURPLE + "║" + BLUE + " %-18s" + PURPLE + "║%n" + RESET,"Optimal Threshold", String.format("%.4f", threshold));
        }
        
        System.out.println(PURPLE + "╚═══════════════════════╩═══════════════════╝" + RESET);
        System.out.println(WHITE + "\n📄 Output Files:" + RESET);
        System.out.println(GREEN + " ✅ Compressed Image: " + RESET + outputPath);
        if (saveGif && gifPath != null) {
            System.out.println(GREEN + " ✅ Compression GIF: " + RESET + gifPath);
        }
    }
    public static String getThresholdRange(int method) {
        switch (method) {
            case 1:
                return "(0-16256.25)";
            case 2:
                return "(0-127.5)";
            case 3:
                return "(0-255)";
            case 4:
                return "(0.0-8.0)";
            // case 5:
            //     return "(0.0-1.0)";
            default:
                return "";
        }
    }

    public static String getMethodName(int method) {
        switch (method) {
            case 1:
                return "Variance";
            case 2:
                return "Mean Absolute Deviation";
            case 3:
                return "Max Pixel Difference";
            case 4:
                return "Entropy";
            // case 5:
            //     return "SSIM";
            default:
                return "Unknown";
        }
    }
}
