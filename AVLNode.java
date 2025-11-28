/**
 * Time Complexity: O(1)
 * Space Complexity: O(1)
 */
public class AVLNode<K extends Comparable<K>, T> {
    private K key;
    private T data;
    private AVLNode<K, T> left;
    private AVLNode<K, T> right;
    private int height;

    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public AVLNode(K key, T data) {
        this.key = key;
        this.data = data;
        this.left = null;
        this.right = null;
        this.height = 1;
    }

    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public K getKey() {
        return key;
    }

    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public void setKey(K key) {
        this.key = key;
    }

    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public T getData() {
        return data;
    }

    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public AVLNode<K, T> getLeft() {
        return left;
    }

    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public void setLeft(AVLNode<K, T> left) {
        this.left = left;
    }

    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public AVLNode<K, T> getRight() {
        return right;
    }

    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public void setRight(AVLNode<K, T> right) {
        this.right = right;
    }

    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public int getHeight() {
        return height;
    }

    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public void setHeight(int height) {
        this.height = height;
    }
}
