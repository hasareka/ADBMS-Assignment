package com.example.inventorymanagement.controller;

import com.example.inventorymanagement.entity.Product;
import com.example.inventorymanagement.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return service.save(product);
    }

    @PutMapping("/{id}/updateQuantity")
    public Product updateProductQuantity(@PathVariable String id, @RequestParam int quantityChange) {
        Product product = service.findById(id);
        if (product != null) {
            int currentQuantity = product.getQuantity();
            int updatedQuantity = currentQuantity - quantityChange;
            if (updatedQuantity >= 0) {
                product.setQuantity(updatedQuantity);
                return service.update(id, product);
            }
        }
        throw new RuntimeException("Product not found or insufficient quantity.");
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) {
        service.delete(id);
    }
}
