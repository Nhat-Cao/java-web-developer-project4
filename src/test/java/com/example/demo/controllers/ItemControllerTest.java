package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
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

public class ItemControllerTest  {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void get_items_happy_path() throws Exception {
        Item item = new Item();
        item.setName("testItem");
        item.setPrice(new BigDecimal(2.99));
        item.setDescription("This is a testItem description");

        List<Item> expectedItems = new ArrayList<>();
        for (int i=0; i < 2; i++) {
            item.setId((long) i);
            expectedItems.add(item);
        }

        when(itemRepository.findAll()).thenReturn(expectedItems);

        final ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());

        List<Item> actualItems = response.getBody();
        assertNotNull(actualItems);
        assertEquals(expectedItems, actualItems);
    }

    @Test
    public void get_item_by_id_happy_path() throws Exception {
        Item item = new Item();
        item.setId((long) 0);
        item.setName("testItem");
        item.setPrice(new BigDecimal(2.99));
        item.setDescription("This is a testItem description");

        when(itemRepository.findById((long) 0)).thenReturn(java.util.Optional.of(item));

        final ResponseEntity<Item> response = itemController.getItemById((long) 0);

        // EMG - Assertions are made
        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());

        Item actualItem = response.getBody();
        assertNotNull(actualItem);
        assertEquals(item.getId(), actualItem.getId());
        assertEquals(item.getName(), actualItem.getName());
        assertEquals(item.getPrice(), actualItem.getPrice());
        assertEquals(item.getDescription(), actualItem.getDescription());
    }

    @Test
    public void get_items_by_name_happy_path() throws Exception {
        Item item = new Item();
        item.setName("testItem");
        item.setPrice(new BigDecimal(2.99));
        item.setDescription("This is a testItem description");

        List<Item> expectedItems = new ArrayList<>();
        for (int i=0; i < 2; i++) {
            item.setId((long) i);
            expectedItems.add(item);
        }

        when(itemRepository.findByName("testItem")).thenReturn(expectedItems);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName("testItem");

        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());

        List<Item> actualItems = response.getBody();
        assertNotNull(actualItems);
        assertEquals(expectedItems, actualItems);
    }

    @Test
    public void get_items_by_name_empty() throws Exception {

        when(itemRepository.findByName("testItem")).thenReturn(null);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName("testItem");

        assertNotNull(response);
        assertEquals(404,response.getStatusCodeValue());
    }

}
