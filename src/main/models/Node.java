package main.models;

import java.util.Objects;

public class Node {
    public Task task;
    public Node next;
    public Node prev;

    public Node(Node prev, Task task, Node next) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node node)) return false;
        return Objects.equals(task, node.task);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(task);
    }
}
