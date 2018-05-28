package com.thiagojavabr.restresource.resource;

import com.thiagojavabr.restresource.domain.Product;
import com.thiagojavabr.restresource.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductResource {

    @Autowired
    private ProductRepository productRepo;

    @GetMapping
    public Collection<Product> findAll(){
        return productRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id){
        Optional<Product> productOpt = productRepo.findById(id);
        return productOpt.isPresent() ? ResponseEntity.ok(productOpt.get()) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product){
        Product personSaved = productRepo.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(personSaved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product){
        Optional<Product> productOpt = productRepo.findById(id);

        if(!productOpt.isPresent()){
            return new ResponseEntity<Product>(HttpStatus.NOT_FOUND);
        }

        Product productPersisted = productOpt.get();
        BeanUtils.copyProperties(product, productPersisted, "id");

        productRepo.save(productPersisted);
        return ResponseEntity.ok(productPersisted);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        productRepo.deleteById(id);
    }

}
