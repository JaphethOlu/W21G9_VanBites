package com.example.vanbites.entities;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private List<OrderItem> items = new ArrayList<>();
    private double taxRate = .12; // 12% tax estimate
    private double subTotal;
    private double tax;
    private double total;

    public Order(List<OrderItem> items) {
        this.items = items;
        calculateTotalCost();
    }

    public double getSubTotal() {
        return subTotal;
    }

    public double getTax() {
        return tax;
    }

    public double getTotal() {
        return total;
    }

    public void addOrderItem(OrderItem item) {
        items.add(item);
    }

    /**
     * This method is used to calculate the tax, subTotal and total.
     * This calculation is performed on class initialization
     */
    private void calculateTotalCost() {

        double sum = 0;

        for(OrderItem item : items) {
            double itemCost = item.getFood().getPrice();
            double quantity = item.getQuantity();
            sum += itemCost * quantity;
        }

        sum = Math.round(sum * 100.0) / 100.0; // Format to 2 decimal places

        subTotal = sum;
        tax = Math.round((sum * taxRate) * 100.0) / 100.0;
        total = Math.round((sum + tax) * 100.0) / 100.0;
    }

    public int getSize() {
        return items.size();
    }
}
