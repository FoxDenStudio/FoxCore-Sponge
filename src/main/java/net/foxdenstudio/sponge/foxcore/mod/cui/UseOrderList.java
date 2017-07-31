package net.foxdenstudio.sponge.foxcore.mod.cui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class UseOrderList<T> {

    private final List<T> itemList;

    public UseOrderList() {
        this.itemList = new ArrayList<>();
    }

    public T use(T item) {
        if (this.itemList.contains(item)) {
            this.itemList.remove(item);
        }
        this.itemList.add(0, item);
        return item;
    }

    public boolean remove(T item) {
        return this.itemList.remove(item);
    }

    public Stream<T> stream() {
        return this.itemList.stream();
    }

    List<T> getItems() {
        return this.itemList;
    }

    public Stream<T> reverseStream() {
        List<T> temp = new ArrayList<>();
        this.itemList.forEach(t -> temp.add(0, t));
        return temp.stream();
    }
}
