<h1 align="center">Tugas Kecil 2 IF2211 Strategi Algoritma</h1>
<h2 align="center">Semester II tahun 2024/2025</h2>
<h2 align="center">Kompresi Gambar dan Pembuatan GIF Dengan Metode Quadtree</h2>

## Table of Contents
- [Description](#description)
- [Program Structure](#program-structure)
- [Requirements & Installation](#requirements--installation)
- [Compilation](#compilation)
- [How to Use](#how-to-use)
- [Author](#author)
- [References](#references)

## Description
Repositori ini berisikan program yang telah dibuat guna memenuhi penilaian mata kuliah Strategi Algoritma IF2211. Program ini ditujukan untuk melakukan kompresi gambar dengan metode Quadtree. Adapun metode perhitungan error yang telah diimplementasikan adalah berbasis variansi, Mean Absolute Deviation (MAD), entropi, Structural Similarity Index (SSIM), dan Max Pixel Difference. Selain itu, program juga dapat memberikan visualisasi kompresi melalui file dengan format GIF.  Bahasa pemrograman yang digunakan dalam perangkat lunak ini adalah Java.

## Program Structure
📁 root  
├── 📁 bin  
│   ├── 📄 ErrorMetrics.class  
│   ├── 📄 IO.class  
│   ├── 📄 ImageMatrix.class  
│   ├── 📄 Main.class  
│   ├── 📄 QuadtreeCompression.class  
│   ├── 📄 QuadtreeNode.class  
│   └── 📄 UI.class  
├── 📁 doc  
│   └── 📄 Tucil2_13523046_13523091  
├── 📁 src  
│   ├── 📄 ErrorMetrics.java  
│   ├── 📄 IO.java  
│   ├── 📄 ImageMatrix.java  
│   ├── 📄 Main.java  
│   ├── 📄 QuadtreeCompression.java  
│   ├── 📄 QuadtreeNode.java  
│   └── 📄 UI.java  
├── 📁 test  
│   ├── 📁 input  
│   └── 📁 output  
│       ├── 📁 gif  
│       ├── 📁 image  
├── 📄 README.md  

## Requirements & Installation
1. Install Java Development Kit (JDK) 17 or later
2. Clone the repository
    ```bash
    git clone https://github.com/ivan-wirawan/Tucil2_13523046_13523091.git
    ```
3. Navigate to the project directory
    ```bash
    cd Tucil2_13523046_13523091
    ```

## Compilation

```bash
javac -d bin src/*.java
```

## How to Use

```bash
java -cp bin Main
```

## Author
| **NIM**  | **Nama Anggota**               | **Github** |
| -------- | ------------------------------ | ---------- |
| 13523046 | Ivan Wirawan                   | [ivan-wirawan](https://github.com/ivan-wirawan) |
| 13523091 | Carlo Angkisan                 | [carllix](https://github.com/carllix) | 

## References
Munir, Rinaldi. 2025. “Algoritma Divide and Conquer (Bagian 1)” (https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/07-Algoritma-Divide-and-Conquer-(2025)-Bagian1.pdf, diakses 8 April 2025)  
Munir, Rinaldi. 2025. “Algoritma Divide and Conquer (Bagian 2)” (https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/05-Algoritma-Greedy-(2025)-Bag2.pdf, diakses 8 April 2025)  
Munir, Rinaldi. 2025. “Algoritma Divide and Conquer (Bagian 3)” (https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/09-Algoritma-Divide-and-Conquer-(2025)-Bagian3.pdf, diakses 8 April 2025)  
Munir, Rinaldi. 2025. “Algoritma Divide and Conquer (Bagian 4)” (https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2024-2025/10-Algoritma-Divide-and-Conquer-(2025)-Bagian4.pdf, diakses 8 April 2025)  