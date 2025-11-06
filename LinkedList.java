
public class LinkedList<T> {
    private Node<T> head;
    private Node<T> current;
    private int size;


    public LinkedList() {
        this.head = null;
        this.current = null;
        this.size = 0;
    }


    public void add(T data) {
        Node<T> newNode = new Node<>(data);

        if (head == null) {

            head = newNode;
        } else {

            Node<T> temp = head;
            while (temp.getNext() != null) {
                temp = temp.getNext();
            }

            temp.setNext(newNode);
        }
        size++;
    }


    public int getSize() {
        return size;
    }


    public void resetCurrent() {
        current = head;
    }


    public boolean hasNext() {
        return current != null;
    }


    public Node<T> getNext() {
        if (!hasNext()) {
            return null;
        }
        Node<T> temp = current;
        current = current.getNext();
        return temp;
    }
}