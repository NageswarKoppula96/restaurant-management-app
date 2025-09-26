package com.restaurant.service;

import com.restaurant.entity.MenuItem;
import com.restaurant.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    @Autowired
    public MenuService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findByAvailableTrue();
    }

    public List<MenuItem> getMenuItemsByCategory(String category) {
        return menuItemRepository.findByCategoryAndAvailableTrue(category);
    }

    public List<String> getAllCategories() {
        return menuItemRepository.findDistinctCategories();
    }
    
    public Optional<MenuItem> getMenuItemByName(String name) {
        return menuItemRepository.findByNameIgnoreCase(name);
    }

    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .filter(MenuItem::getAvailable);
    }

    public MenuItem saveMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }
}
