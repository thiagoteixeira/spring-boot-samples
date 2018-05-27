package com.thiagojavabr.repositoryrestresource.repository;


import com.thiagojavabr.repositoryrestresource.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
}
