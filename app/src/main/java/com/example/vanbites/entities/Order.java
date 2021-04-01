package com.example.vanbites.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private List<OrderItem> items;
    private double taxRate = .12; // 12% tax estimate
    private BigDecimal tax;
    private BigDecimal total;

    public Order(List<OrderItem> items) {
        this.items = items;
    }

    public double getTax() {
        return tax.doubleValue();
    }

    public double getTotal() {
        return total.doubleValue();
    }

    /**
     * This method is used to calculate the tax, subTotal and total.
     */
    public double calculateTotalCost() {

        BigDecimal sum = new BigDecimal("0");

        for(OrderItem item : items) {
            sum = sum.add(item.getCostInBigDecimal());
        }

        tax = sum.multiply(BigDecimal.valueOf(taxRate));
        total = sum.add(tax).setScale(2, RoundingMode.HALF_UP);

        return total.doubleValue();
    }
}
