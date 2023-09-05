package com.example.orderplacement.controller;

import com.example.orderplacement.dto.ProductDTO;
import com.example.orderplacement.dto.UserDTO;
import com.example.orderplacement.entity.Order;
import com.example.orderplacement.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        // Validate User
        UserDTO user = restTemplate.getForObject("http://localhost:8080/api/users/" + order.getUserId(), UserDTO.class);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Validate Product
        ProductDTO product = restTemplate.getForObject("http://localhost:8081/products/" + order.getProductId(), ProductDTO.class);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        // Save the order in the Order Placement Microservice's database
        Order savedOrder = orderService.saveOrder(order);

        // Update the quantity in the Inventory Management Microservice
        int updatedQuantity = order.getQuantity(); // Get the quantity from the order
        String productId = order.getProductId(); // Get the product ID from the order

        // Make an HTTP request to the Inventory Management Microservice to update the product quantity
        restTemplate.put("http://localhost:8081/products/" + productId + "/updateQuantity?quantityChange=" + updatedQuantity, null);

        return savedOrder;
    }


    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.findAllOrders();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.findOrderById(id);
    }

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order order) {
        return orderService.updateOrder(id, order);
    }

    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Long id) {
        return orderService.deleteOrder(id);
    }
}
