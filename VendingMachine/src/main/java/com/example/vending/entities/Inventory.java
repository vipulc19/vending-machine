package com.example.vending.entities;

import java.util.HashMap;
import java.util.Map;

public class Inventory<T> {

    private Map<T, Integer> inventory = new HashMap<>();

    public int getQuantity(T item) {
        Integer quantity = inventory.get(item);
        return quantity == null ? 0 : quantity;
    }

    public void add(T item) {
        int count = inventory.getOrDefault(item, 0);
        inventory.put(item, count + 1);
    }

    public void deductQuantity(T item) {
        if (hasItem(item)) {
            int count = inventory.get(item);
            inventory.put(item, count - 1);
        }
    }

    public void clear() {
        inventory.clear();
    }

    public void put(T item, int quantity) {
        inventory.put(item, quantity);
    }

    public boolean hasItem(T item) {
        return getQuantity(item) > 0;
    }

}
