package com.example.vanbites;

import com.example.vanbites.entities.Order;
import com.example.vanbites.entities.Food;
import com.example.vanbites.entities.OrderItem;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderUnitTest {

    // Arrange

    // Initialize food items
    Food bacon = new Food();
    Food pizza = new Food();
    Food wings = new Food();
    Food dumplings = new Food();

    OrderItem baconOrder = new OrderItem(bacon, 2);
    OrderItem pizzaOrder = new OrderItem(pizza, 1);
    OrderItem wingsOrder = new OrderItem(pizza, 1);
    OrderItem dumplingsOrder = new OrderItem(dumplings, 1);

    List<OrderItem> items = new ArrayList<>();

    Order shoppingOrder;

    @Test
    void calculateTotalBill() {
        // Set Price of Food items
        bacon.setPrice(3.27f);
        pizza.setPrice(12.90f);
        wings.setPrice(13.52f);
        dumplings.setPrice(7.60f);

        items.add(baconOrder);
        items.add(pizzaOrder);
        items.add(wingsOrder);
        items.add(dumplingsOrder);


        shoppingOrder = new Order(items);

        assertAll("price of all items",
                () -> assertEquals(4.79, shoppingOrder.getTax()),
                () -> assertEquals(39.94, shoppingOrder.getSubTotal()),
                () -> assertEquals(44.73, shoppingOrder.getTotal())
        );
        
    }

}
