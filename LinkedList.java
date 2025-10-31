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
            current = head;
        } else {
            current.setNext(newNode);
            current = newNode;
        }
        size++;
    }

    public int getSize() {
        return size;
    }

    public Node<T> getHead() {
        return head;
    }

    public Node<T> getCurrent() {
        return current;
    }

    public void resetCurrent() {
        current = head;
    }

    public boolean hasNext() {
        return current != null && current.getNext() != null;
    }

    public Node<T> getNext() {
        if (current != null) {
            Node<T> temp = current;
            current = current.getNext();
            return temp;
        }
        return null;
    }

    public void remove(T data) {
        if (head == null) return;

        if (head.getData().equals(data)) {
            head = head.getNext();
            size--;
            return;
        }

        Node<T> temp = head;
        while (temp.getNext() != null) {
            if (temp.getNext().getData().equals(data)) {
                temp.setNext(temp.getNext().getNext());
                size--;
                return;
            }
            temp = temp.getNext();
        }
    }

    public Node<T> searchById(int id) {
        Node<T> temp = head;
        while (temp != null) {
            if (temp.getData() instanceof Products) {
                Products prod = (Products) temp.getData();
                if (prod.getProductId() == id) {
                    return temp;
                }
            }
            temp = temp.getNext();
        }
        return null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node<T> temp = head;
        while (temp != null) {
            sb.append(temp.getData().toString());
            if (temp.getNext() != null) sb.append(" -> ");
            temp = temp.getNext();
        }
        return sb.toString();
    }

    public void next(Node<T> node) {
        if (hasNext()) {
            current = current.getNext();
        }
    }
}
