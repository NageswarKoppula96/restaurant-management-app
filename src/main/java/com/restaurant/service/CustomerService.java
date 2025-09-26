package com.restaurant.service;

import com.restaurant.entity.Customer;
import com.restaurant.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }
    
    public Optional<Customer> getCustomerByPhone(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }
    
    public boolean existsByPhoneNumber(String phoneNumber) {
        return customerRepository.existsByPhoneNumber(phoneNumber);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}
