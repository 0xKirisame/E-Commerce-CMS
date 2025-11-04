/**
 * A custom generic singly linked list implementation.
 * Provides methods for adding and iterating through elements.
 * @param <T> The type of data this list will store.
 */
public class LinkedList<T> {
    private Node<T> head;
    private Node<T> current; // Used for iteration
    private int size;

    /**
     * Constructs an empty linked list.
     */
    public LinkedList() {
        this.head = null;
        this.current = null;
        this.size = 0;
    }

    /**
     * Adds a new element to the end of the list.
     * @param data The data to add.
     */
    public void add(T data) {
        Node<T> newNode = new Node<>(data);

        if (head == null) {
            // List is empty, new node is the head
            head = newNode;
        } else {
            // List is not empty, find the last node
            Node<T> temp = head;
            while (temp.getNext() != null) {
                temp = temp.getNext();
            }
            // Point the last node to the new node
            temp.setNext(newNode);
        }
        size++; // Increment the size
    }

    /**
     * Gets the number of elements in the list.
     * @return The size of the list.
     */
    public int getSize() {
        return size;
    }

    /**
     * Resets the iterator to the beginning of the list.
     */
    public void resetCurrent() {
        current = head;
    }

    /**
     * Checks if there are more elements to iterate over.
     * @return true if current is not null, false otherwise.
     */
    public boolean hasNext() {
        return current != null;
    }

    /**
     * Returns the current node and moves the iterator to the next node.
     * @return The current Node, or null if at the end of the list.
     */
    public Node<T> getNext() {
        if (!hasNext()) {
            return null;
        }
        Node<T> temp = current;
        current = current.getNext(); // Move to the next node
        return temp;
    }
}

