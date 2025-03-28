
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import javax.imageio.ImageIO;



public class Input
{
    public String sourcePath;
    public int variance;
    public int threshold;
    public int minimumSize;
    public double target;
    public String resultPath;
    public String gifPath;
    public BufferedImage image;
    public int width;
    public int height;
    public int[][] pixelMatrix;

    public static void main(String[] args) {
        Input obj = new Input(); // Create an instance to access class variables

        System.out.println("Silahkan masukkan alamat gambar yang akan dikompresi!");
        Scanner input = new Scanner(System.in);
        obj.sourcePath = input.nextLine();

        if (!Files.exists(Paths.get(obj.sourcePath)) || !Files.isReadable(Paths.get(obj.sourcePath))) {
            System.out.println("File tidak ditemukan atau tidak bisa dibaca.");
            input.close();
            return;
        }

        try {
            // Assign to the instance variable instead of creating a new local one
            obj.image = ImageIO.read(new File(obj.sourcePath));
            obj.width = obj.image.getWidth();
            obj.height = obj.image.getHeight();

            // Convert to Matrix
            obj.pixelMatrix = new int[obj.height][obj.width];
            for (int y = 0; y < obj.height; y++) {
                for (int x = 0; x < obj.width; x++) {
                    obj.pixelMatrix[y][x] = obj.image.getRGB(x, y);
                }
            }

            System.out.println("Gambar berhasil dikonversi ke matriks!");
        } catch (IOException e) {
            System.out.println("Gagal membaca gambar: " + e.getMessage());
            input.close();
        }

        try {
            System.out.println("Silahkan masukkan nilai variansi yang diinginkan!");
            obj.variance = Integer.parseInt(input.nextLine());

            System.out.println("Silahkan masukkan nilai threshold yang diinginkan!");
            obj.threshold = Integer.parseInt(input.nextLine());

            System.out.println("Silahkan masukkan nilai minimum blok yang diinginkan!");
            obj.minimumSize = Integer.parseInt(input.nextLine());

            System.out.println("Silahkan masukkan nilai target kompresi yang diinginkan! (Jika ingin menonaktifkan, silahkan isi dengan '0')");
            obj.target = Double.parseDouble(input.nextLine());

            System.out.println("Silahkan masukkan alamat hasil gambar yang telah dikompresi!");
            obj.resultPath = input.nextLine();

            System.out.println("Silahkan masukkan alamat hasil gif dari gambar!");
            obj.gifPath = input.nextLine();
        } catch (NumberFormatException e)
        {
            System.out.println("Input tidak valid! Harap masukkan angka untuk variansi, threshold, minimumSize, dan target.");
            input.close();
            return;
        }
        input.close();
        System.out.println("don");
    }

}