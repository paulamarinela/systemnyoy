package rq.tech.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rq.tech.models.Customer;
import rq.tech.models.Repair;
import rq.tech.repositories.CustomerRepository;
import rq.tech.repositories.RepairRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final RepairRepository repairRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerByPhone(String phone) {
        return customerRepository.findByContactNumber(phone);
    }

    public List<Customer> searchByName(String name) {
        return customerRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Repair> getRepairHistory(String phone) {
        return repairRepository.findByContactNumber(phone);
    }

    @Transactional
    public void updateCustomerFromRepair(Repair repair) {
        Optional<Customer> customerOpt = customerRepository.findByContactNumber(repair.getContactNumber());
        Customer customer;
        if (customerOpt.isPresent()) {
            customer = customerOpt.get();
            customer.setTotalVisits(customer.getTotalVisits() + 1);
            customer.setLastVisitDate(LocalDateTime.now());
            if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
                customer.setEmail(repair.getCustomerEmail());
            }
        } else {
            customer = Customer.builder()
                    .name(repair.getCustomerName())
                    .contactNumber(repair.getContactNumber())
                    .email(repair.getCustomerEmail())
                    .address(repair.getAddress())
                    .totalVisits(1)
                    .lastVisitDate(LocalDateTime.now())
                    .build();
        }
        customerRepository.save(customer);
    }
}
