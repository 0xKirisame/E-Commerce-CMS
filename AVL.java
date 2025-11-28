import java.util.ArrayList;
import java.util.List;

/**
 * Time Complexity: O(1) for initialization
 * Space Complexity: O(N) to store N elements
 */
public class AVL<K extends Comparable<K>, T> {
    private AVLNode<K, T> root;
    private int size;
    // Iterator state
    private List<AVLNode<K, T>> traversalList;
    private int currentIndex;

    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public AVL() {
        this.root = null;
        this.size = 0;
    }

    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    private int height(AVLNode<K, T> N) {
        if (N == null)
            return 0;
        return N.getHeight();
    }

    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    private int max(int a, int b) {
        return (a > b) ? a : b;
    }

    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    private AVLNode<K, T> rightRotate(AVLNode<K, T> y) {
        AVLNode<K, T> x = y.getLeft();
        AVLNode<K, T> T2 = x.getRight();

        // Perform rotation
        x.setRight(y);
        y.setLeft(T2);

        // Update heights
        y.setHeight(max(height(y.getLeft()), height(y.getRight())) + 1);
        x.setHeight(max(height(x.getLeft()), height(x.getRight())) + 1);

        // Return new root
        return x;
    }

    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    private AVLNode<K, T> leftRotate(AVLNode<K, T> x) {
        AVLNode<K, T> y = x.getRight();
        AVLNode<K, T> T2 = y.getLeft();

        // Perform rotation
        y.setLeft(x);
        x.setRight(T2);

        // Update heights
        x.setHeight(max(height(x.getLeft()), height(x.getRight())) + 1);
        y.setHeight(max(height(y.getLeft()), height(y.getRight())) + 1);

        // Return new root
        return y;
    }

    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    private int getBalance(AVLNode<K, T> N) {
        if (N == null)
            return 0;
        return height(N.getLeft()) - height(N.getRight());
    }

    /**
     * Time Complexity: O(log N)
     * Space Complexity: O(log N) (recursion stack)
     */
    public void insert(K key, T data) {
        root = insertRec(root, key, data);
    }

    private AVLNode<K, T> insertRec(AVLNode<K, T> node, K key, T data) {
        /* 1. Perform the normal BST insertion */
        if (node == null) {
            size++;
            return new AVLNode<>(key, data);
        }

        if (key.compareTo(node.getKey()) < 0)
            node.setLeft(insertRec(node.getLeft(), key, data));
        else if (key.compareTo(node.getKey()) > 0)
            node.setRight(insertRec(node.getRight(), key, data));
        else {
            // Duplicate keys not allowed, update data
            node.setData(data);
            return node;
        }

        /* 2. Update height of this ancestor node */
        node.setHeight(1 + max(height(node.getLeft()), height(node.getRight())));

        /* 3. Get the balance factor of this ancestor node to check whether
           this node became unbalanced */
        int balance = getBalance(node);

        // If this node becomes unbalanced, then there are 4 cases

        // Left Left Case
        if (balance > 1 && key.compareTo(node.getLeft().getKey()) < 0)
            return rightRotate(node);

        // Right Right Case
        if (balance < -1 && key.compareTo(node.getRight().getKey()) > 0)
            return leftRotate(node);

        // Left Right Case
        if (balance > 1 && key.compareTo(node.getLeft().getKey()) > 0) {
            node.setLeft(leftRotate(node.getLeft()));
            return rightRotate(node);
        }

        // Right Left Case
        if (balance < -1 && key.compareTo(node.getRight().getKey()) < 0) {
            node.setRight(rightRotate(node.getRight()));
            return leftRotate(node);
        }

        /* return the (unchanged) node pointer */
        return node;
    }

    /**
     * Time Complexity: O(log N)
     * Space Complexity: O(log N) (recursion stack)
     */
    public T search(K key) {
        AVLNode<K, T> result = searchRec(root, key);
        return (result != null) ? result.getData() : null;
    }

    private AVLNode<K, T> searchRec(AVLNode<K, T> root, K key) {
        if (root == null || root.getKey().equals(key)) {
            return root;
        }

        if (key.compareTo(root.getKey()) < 0) {
            return searchRec(root.getLeft(), key);
        }

        return searchRec(root.getRight(), key);
    }

    /**
     * Time Complexity: O(N)
     * Space Complexity: O(N) (result list + recursion stack)
     */
    public List<T> inOrderTraversal() {
        List<T> result = new ArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    private void inOrderRec(AVLNode<K, T> root, List<T> result) {
        if (root != null) {
            inOrderRec(root.getLeft(), result);
            result.add(root.getData());
            inOrderRec(root.getRight(), result);
        }
    }

    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public int getSize() {
        return size;
    }

    // Iterator-like methods to support the existing code structure if needed
    // Although inOrderTraversal returns a list, some parts of the code might rely on resetCurrent/hasNext/getNext pattern
    // I will implement them using the list for compatibility
    
    public void resetCurrent() {
        traversalList = new ArrayList<>();
        collectNodes(root, traversalList);
        currentIndex = 0;
    }

    private void collectNodes(AVLNode<K, T> node, List<AVLNode<K, T>> list) {
        if (node != null) {
            collectNodes(node.getLeft(), list);
            list.add(node);
            collectNodes(node.getRight(), list);
        }
    }

    public boolean hasNext() {
        return traversalList != null && currentIndex < traversalList.size();
    }

    public AVLNode<K, T> getNext() {
        if (!hasNext()) return null;
        return traversalList.get(currentIndex++);
    }
}
