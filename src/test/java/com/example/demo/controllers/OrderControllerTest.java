package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submit_order_username_happy_path() throws Exception {
        User user = new User();
        user.setUsername("test");
        Cart cart = new Cart();
        cart.setId((long) 0);
        cart.setUser(user);
        user.setCart(cart);
        user.setId(0);
        user.setPassword("testPassword");

        Item item = new Item();
        item.setId((long) 0);
        item.setName("testItem");
        item.setPrice(new BigDecimal(2.99));
        item.setDescription("This is a testItem description");

        List<Item> itemsArray = new ArrayList<>();
        for (int i=0; i < 3; i++) {
            itemsArray.add(item);
        }
        cart.setItems(itemsArray);
        cart.setTotal(BigDecimal.valueOf(8.97));
        user.setCart(cart);

        when(userRepository.findByUsername("test")).thenReturn(user);

        final ResponseEntity<UserOrder> response = orderController.submit("test");

        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());

        UserOrder actualUserOrder = response.getBody();
        assertNotNull(actualUserOrder);
        assertEquals(cart.getItems(), actualUserOrder.getItems());
        assertEquals(cart.getUser(), actualUserOrder.getUser());
        assertEquals(cart.getTotal(), actualUserOrder.getTotal());
    }

    @Test
    public void submit_order_username_not_found() throws Exception {
        when(userRepository.findByUsername("test")).thenReturn(null);

        final ResponseEntity<UserOrder> response = orderController.submit("test");

        assertNotNull(response);
        assertEquals(404,response.getStatusCodeValue());
    }

    @Test
    public void get_orders_for_user_happy_path() throws Exception {
        User user = new User();
        user.setUsername("test");
        Cart cart = new Cart();
        cart.setId((long) 0);
        cart.setUser(user);
        user.setCart(cart);
        user.setId(0);
        user.setPassword("testPassword");

        Item item = new Item();
        item.setId((long) 0);
        item.setName("testItem");
        item.setPrice(new BigDecimal(2.99));
        item.setDescription("This is a testItem description");

        List<Item> itemsArray = new ArrayList<>();
        for (int i=0; i < 3; i++) {
            itemsArray.add(item);
        }
        cart.setItems(itemsArray);
        cart.setTotal(BigDecimal.valueOf(8.97));
        user.setCart(cart);

        when(userRepository.findByUsername("test")).thenReturn(user);

        UserOrder order = UserOrder.createFromCart(user.getCart());
        List<UserOrder> expectedUserOrders = new ArrayList<>();
        for (int i=0; i < 2; i++) {
            order.setId((long) i);
            expectedUserOrders.add(order);
        }

        when(orderRepository.findByUser(user)).thenReturn(expectedUserOrders);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");

        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());

        List<UserOrder> actualUserOrders = response.getBody();
        assertNotNull(actualUserOrders);
        assertEquals(expectedUserOrders, actualUserOrders);
    }

    @Test
    public void get_orders_for_user_username_not_found() throws Exception {
        when(userRepository.findByUsername("test")).thenReturn(null);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");

        assertNotNull(response);
        assertEquals(404,response.getStatusCodeValue());
    }

}
