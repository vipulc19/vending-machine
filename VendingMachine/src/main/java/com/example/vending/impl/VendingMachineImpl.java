package com.example.vending.impl;

import com.example.vending.entities.Coin;
import com.example.vending.entities.Item;
import com.example.vending.entities.VendingMachine;
import com.example.vending.exception.NotFullPaidException;
import com.example.vending.exception.NotSufficientChangeException;
import com.example.vending.exception.SoldOutException;
import javafx.util.Pair;
import com.example.vending.entities.Inventory;

import java.util.ArrayList;
import java.util.List;

public class VendingMachineImpl implements VendingMachine {

    //Generics
    private final Inventory<Coin> coinInventory = new Inventory<>();
    private final Inventory<Item> itemInventory = new Inventory<>();

    private long totalSales;
    private Item currentItem;
    private long currentBalance;

    public VendingMachineImpl() {
        initialize();
    }

    private void initialize() {

        //initialize machine with 5 coins of each denomination
        //and 5 cans of each Item
        for (Coin c : Coin.values())
            coinInventory.put(c, 5);

        for (Item i : Item.values())
            itemInventory.put(i, 5);
    }

    @Override
    public long selectItemAndGetPrice(Item item) {

        if (itemInventory.hasItem(item)) {
            currentItem = item;
            return currentItem.getPrice();
        }
        throw new SoldOutException("Sold Out, Please buy another item");
    }

    @Override
    public void insertCoin(Coin coin) {

        currentBalance += coin.getDenomination();
        coinInventory.add(coin);
    }

    @Override
    public Pair<Item, List<Coin>> collectItemAndChange() {

        Item item = collectItem();

        totalSales = totalSales + currentItem.getPrice();
        List<Coin> change = collectChange();

        return new Pair<>(item, change);
    }

    public Item collectItem() {

        if (isFullPaid()) {
            if (hasSufficientChange()) {
                itemInventory.deductQuantity(currentItem);
                return currentItem;
            }
            throw new NotSufficientChangeException("Not Sufficient change in Inventory");
        }
        long remainingBalance = currentItem.getPrice() - currentBalance;
        throw new NotFullPaidException("Price not full paid, remaining : " + remainingBalance);
    }

    private boolean isFullPaid() {
        return currentBalance >= currentItem.getPrice();
    }


    private boolean hasSufficientChange() {
        return hasSufficientChangeForAmount(currentBalance - currentItem.getPrice());
    }

    private boolean hasSufficientChangeForAmount(long amount) {

        try {
            getChange(amount);
        } catch (NotSufficientChangeException nsce) {
            System.out.println(nsce.getMessage());
            return false;
        }
        return true;
    }

    private List<Coin> getChange(long amount) {

        List<Coin> changes = new ArrayList<>();

        if (amount > 0) {

            long balance = amount;

            while (balance > 0) {

                if (balance >= Coin.QUARTER.getDenomination() && coinInventory.hasItem(Coin.QUARTER)) {
                    changes.add(Coin.QUARTER);
                    balance -= Coin.QUARTER.getDenomination();
                } else if (balance >= Coin.DIME.getDenomination() && coinInventory.hasItem(Coin.DIME)) {
                    changes.add(Coin.DIME);
                    balance = balance - Coin.DIME.getDenomination();

                } else if (balance >= Coin.NICKLE.getDenomination() && coinInventory.hasItem(Coin.NICKLE)) {
                    changes.add(Coin.NICKLE);
                    balance = balance - Coin.NICKLE.getDenomination();

                } else if (balance >= Coin.PENNY.getDenomination() && coinInventory.hasItem(Coin.PENNY)) {
                    changes.add(Coin.PENNY);
                    balance = balance - Coin.PENNY.getDenomination();

                } else {
                    throw new NotSufficientChangeException("NotSufficientChange, Please try another product");
                }
            }
        }

        return changes;
    }

    private List<Coin> collectChange() throws NotSufficientChangeException {
        long changeAmount = currentBalance - currentItem.getPrice();
        List<Coin> change = getChange(changeAmount);
        updateCoinInventory(change);
        currentBalance = 0;
        currentItem = null;
        return change;
    }

    private void updateCoinInventory(List<Coin> change) {
        for (Coin c : change) {
            coinInventory.deductQuantity(c);
        }
    }

    @Override
    public List<Coin> refund() {
        List<Coin> refund = getChange(currentBalance);
        updateCoinInventory(refund);
        currentBalance = 0;
        currentItem = null;
        return refund;
    }


    @Override
    public void reset() {
        coinInventory.clear();
        itemInventory.clear();
        totalSales = 0;
        currentItem = null;
        currentBalance = 0;
    }

    public void printStats() {
        System.out.println("Total Sales : " + totalSales);
        System.out.println("Current Item Inventory : " + itemInventory);
        System.out.println("Current Coin Inventory : " + coinInventory);
    }

    public long getTotalSales() {
        return totalSales;
    }
}
