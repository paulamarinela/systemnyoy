package rq.tech.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rq.tech.models.Customer;
import rq.tech.models.Repair;
import rq.tech.services.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Customer>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(customerService.searchByName(name));
    }

    @GetMapping("/{phone}/history")
    public ResponseEntity<List<Repair>> getRepairHistory(@PathVariable String phone) {
        return ResponseEntity.ok(customerService.getRepairHistory(phone));
    }
}
