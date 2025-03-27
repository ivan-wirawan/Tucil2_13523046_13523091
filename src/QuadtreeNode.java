public class QuadtreeNode {
    int x, y, width, height;
    int averageColor;
    QuadtreeNode[] children;

    QuadtreeNode(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.children = new QuadtreeNode[4];
    }
}
