package com.baeldung.springboothsqldb.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.baeldung.springboothsqldb.application.entities.Customer;
import com.baeldung.springboothsqldb.application.repositories.CustomerRepository;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

	@Override
	public void run(String... args) throws Exception {
		customerRepository.save(new Customer("Bob", "bob@domain.com"));
	    Customer customer = customerRepository.findById(1L).orElseGet(() 
	      -> new Customer("john", "john@domain.com"));

        customerRepository.save(new Customer("Julie", "julie@domain.com"));
	}
}
