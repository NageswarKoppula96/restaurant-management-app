package com.restaurant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testDatabaseConnection() {
        assertNotNull(dataSource);
        assertNotNull(jdbcTemplate);
        
        try {
            assertTrue(dataSource.getConnection().isValid(5));
            System.out.println("✅ Database connection test passed!");
        } catch (SQLException e) {
            fail("Database connection test failed: " + e.getMessage());
        }
    }

    @Test
    void testSchemaInitialization() {
        // Test if we can connect to the database
        try {
            // Drop the test table if it exists
            try {
                jdbcTemplate.execute("DROP TABLE test_table");
            } catch (Exception e) {
                // Table might not exist, which is fine
            }
            
            // Create a simple table
            jdbcTemplate.execute("CREATE TABLE test_table (id INT PRIMARY KEY, name VARCHAR(50))");
            
            // Insert test data
            jdbcTemplate.update("INSERT INTO test_table (id, name) VALUES (?, ?)", 1, "test");
            
            // Query the data
            String result = jdbcTemplate.queryForObject(
                "SELECT name FROM test_table WHERE id = ?", 
                String.class, 
                1
            );
            
            assertEquals("test", result, "Should be able to insert and query data");
            System.out.println("✅ Schema initialization test passed!");
        } catch (Exception e) {
            fail("Failed to initialize and query test table: " + e.getMessage() + "\n" + 
                 "Caused by: " + (e.getCause() != null ? e.getCause().getMessage() : "No cause"));
        }
    }
}
