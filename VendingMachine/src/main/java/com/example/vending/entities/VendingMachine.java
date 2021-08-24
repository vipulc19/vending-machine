package com.example.vending.entities;

import com.example.vending.entities.Coin;
import com.example.vending.entities.Item;
import javafx.util.Pair;

import java.util.List;

public interface VendingMachine {

    public long selectItemAndGetPrice(Item item);
    public void insertCoin(Coin coin);
    public List<Coin> refund();
    public Pair<Item, List<Coin>> collectItemAndChange();
    public void reset();

}
