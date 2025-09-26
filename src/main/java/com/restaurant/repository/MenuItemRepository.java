package com.restaurant.repository;

import com.restaurant.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByCategory(String category);

    List<MenuItem> findByAvailableTrue();

    List<MenuItem> findByCategoryAndAvailableTrue(String category);

    @Query("SELECT DISTINCT m.category FROM MenuItem m ORDER BY m.category")
    List<String> findDistinctCategories();
    
    Optional<MenuItem> findByNameIgnoreCase(String name);
}
