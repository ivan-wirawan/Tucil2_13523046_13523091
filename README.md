<h1 align="center">Tugas Kecil 2 IF2211 Strategi Algoritma</h1>
<h2 align="center">Semester II tahun 2024/2025</h2>
<h2 align="center">Kompresi Gambar dan Pembuatan GIF Dengan Metode Quadtree</h2>

<p align="center">
  <img src="doc/itb.gif" alt="Quadtree Compression GIF" width="500"/>
</p>

## Table of Contents
- [Description](#description)
- [Program Structure](#program-structure)
- [Requirements & Installation](#requirements--installation)
- [Compilation](#compilation)
- [How to Use](#how-to-use)
- [Author](#author)
- [References](#references)

## Description
This repository contains a Java-based application developed as part of the IF2211 Algorithm Strategy course. The project focuses on image compression using the Quadtree method, a divide-and-conquer approach that recursively splits images into smaller blocks based on color similarity.

To determine block homogeneity, the program implements five error metrics:
- Variance
- Mean Absolute Deviation (MAD)
- Max Pixel Difference
- Entropy
- Structural Similarity Index (SSIM)

In addition to compressing images, the program also supports generating **GIF visualizations** that illustrate the compression process in action. This allows users to better understand how the quadtree structure adapts to different image regions. The program is written entirely in Java, emphasizing efficiency and clarity in algorithm design.

## Program Structure
```
├── README.md
├── bin
│   ├── ErrorMetrics.class
│   ├── IO.class
│   ├── ImageMatrix.class
│   ├── Main.class
│   ├── QuadtreeCompression.class
│   ├── QuadtreeNode.class
│   └── UI.class
├── doc
│   └── Tucil2_13523046_13523091.pdf
├── src
│   ├── ErrorMetrics.java
│   ├── IO.java
│   ├── ImageMatrix.java
│   ├── Main.java
│   ├── QuadtreeCompression.java
│   ├── QuadtreeNode.java
│   └── UI.java
└── test
    ├── input
    └── output
```
- **bin** : contains Java executable .class files compiled from the source code in the src folder.
- **src** : contains the main program's source code files (.java).
- **doc** : contains the assignment report and program documentation.
- **test** : contains the test results included in the report.

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

## Compiling

```bash
javac -d bin src/*.java
```

## How to Use

```bash
java -cp bin Main
```

You can also watch the video below to learn how to use the program :
[![How to Use Program](https://img.youtube.com/vi/GmKJs3wif9k/0.jpg)](https://youtu.be/GmKJs3wif9k)

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