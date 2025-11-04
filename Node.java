/**
 * Represents a single node in a linked list.
 * It holds the data and a reference to the next node.
 * @param <T> The type of data this node will store.
 */
public class Node<T> {
    private T data;
    private Node<T> next;

    /**
     * Constructs a new node with the specified data.
     * @param data The data to be stored in this node.
     */
    public Node(T data) {
        this.data = data;
        this.next = null;
    }

    /**
     * Gets the data stored in this node.
     * @return The data.
     */
    public T getData() {
        return data;
    }

    /**
     * Gets the next node in the list.
     * @return The next Node, or null if this is the last node.
     */
    public Node<T> getNext() {
        return next;
    }

    /**
     * Sets the next node in the list.
     * @param next The node that should come after this one.
     */
    public void setNext(Node<T> next) {
        this.next = next;
    }
}

