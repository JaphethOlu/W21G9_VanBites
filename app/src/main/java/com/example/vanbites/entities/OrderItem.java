package com.example.vanbites.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OrderItem {
    private Food food;
    private int quantity;

    public OrderItem(Food food, int quantity) {
        this.food = food;
        this.quantity = quantity;
    }

    public Food getFood() {
        return food;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void incrementQuantity() {
        quantity++;
    }

    public void decrementQuantity() {
        if(quantity >= 0) {
            quantity--;
        }
    }

    public double getCost() {
        BigDecimal price = BigDecimal.valueOf(food.getPrice());
        BigDecimal quant = BigDecimal.valueOf(quantity);
        BigDecimal total = price.multiply(quant);
        total.setScale(2);
        return total.doubleValue();
    }
}
