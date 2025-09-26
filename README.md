# 🍽️ Restaurant Management System

A comprehensive restaurant management application built with Spring Boot that allows you to manage menu items, place orders, track order status, and calculate order amounts. The system uses phone numbers as the primary identifier for customers.

## ✨ Features

- **Menu Management**: View menu items by category, add/edit/delete menu items
- **Order Management**: Place orders, track order status, view order history
- **Customer Management**: Manage customer information using phone numbers
- **Order Processing**: Full order lifecycle management
- **Persistent Storage**: Data persists between application restarts
- **RESTful API**: Well-documented endpoints for integration
- **Swagger UI**: Interactive API documentation

## 🛠 Technology Stack

- **Backend**: Spring Boot 3.1.0
- **Database**: Apache Derby (embedded, persistent)
- **Build Tool**: Maven
- **Java Version**: 17
- **API Documentation**: SpringDoc OpenAPI 3.0

## 🚀 Key Components

- **Persistence Layer**:
  - JPA with Hibernate
  - Automatic schema generation from entities
  - Data initialization with `schema.sql`

- **Service Layer**:
  - Customer Service
  - Menu Service
  - Order Service

- **API Layer**:
  - RESTful endpoints
  - DTOs for request/response
  - Exception handling
  - Input validation

## 📦 Database Configuration

The application uses Apache Derby with the following configuration:
- **Database URL**: `jdbc:derby:directory:./derbydb/restaurantdb;create=true`
- **Database Files**: Stored in `./derbydb` directory
- **Schema**: Automatically initialized from `src/main/resources/schema.sql`
- **Hibernate**: Configured for Derby dialect

### Database Schema

Key tables:
- `customers`: Customer information
- `menu_items`: Available menu items
- `orders`: Order headers
- `order_items`: Individual items in each order
- `order_status_enum`: Order status reference data

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd restaurant-management-app
   ```

2. **Build the application**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   The application will start on port 8083 by default.

4. **Access the application**
   - API Base URL: `http://localhost:8083`
   - Swagger UI: `http://localhost:8083/swagger-ui.html`
   - API Documentation (JSON): `http://localhost:8083/api-docs`

### Running Tests

```bash
mvn test
```

## 🔧 Configuration

Application properties can be configured in `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8083

# Database Configuration
spring.datasource.url=jdbc:derby:directory:./derbydb/restaurantdb;create=true
spring.datasource.driver-class-name=org.apache.derby.jdbc.EmbeddedDriver
spring.datasource.username=app
spring.jpa.database-platform=org.hibernate.dialect.DerbyDialect
spring.jpa.hibernate.ddl-auto=none

# Logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# API Documentation
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

### Database Management

- The database is stored in the `derbydb` directory
- To reset the database, stop the application and delete the `derbydb` directory
- The schema is automatically created from `src/main/resources/schema.sql` on startup

## 🚀 API Endpoints

### Home
- `GET /` - Welcome message and API information

### 🔍 Customer Endpoints
- `POST /api/customers` - Register a new customer
  - Request Body: 
    ```json
    {
      "name": "John Doe",
      "email": "john@example.com",
      "phoneNumber": "+1234567890"
    }
    ```
  - Returns: Created Customer with ID

- `GET /api/customers` - Get all customers
- `GET /api/customers/{id}` - Get customer by ID
- `GET /api/customers/phone/{phoneNumber}` - Find customer by phone number
- `PUT /api/customers/{id}` - Update customer details
- `DELETE /api/customers/{id}` - Delete a customer

### 🍽️ Menu Endpoints
- `GET /api/menu` - Get all menu items
- `GET /api/menu/{id}` - Get menu item by ID
- `GET /api/menu/category/{category}` - Get menu items by category
- `POST /api/menu` - Add a new menu item
  - Request Body:
    ```json
    {
      "name": "Margherita Pizza",
      "description": "Classic pizza with tomato sauce and mozzarella",
      "price": 12.99,
      "category": "PIZZA",
      "available": true
    }
    ```
- `PUT /api/menu/{id}` - Update menu item
- `DELETE /api/menu/{id}` - Delete menu item

### 🛒 Order Endpoints
- `POST /api/orders` - Create a new order
  - Request Body:
    ```json
    {
      "customerId": 1,
      "items": [
        {
          "menuItemId": 1,
          "quantity": 2
        }
      ]
    }
    ```
- `GET /api/orders` - Get all orders
- `GET /api/orders/{id}` - Get order by ID
- `GET /api/orders/customer/{email}` - Get orders by customer email
- `PUT /api/orders/{id}/status` - Update order status
  - Request Body:
    ```json
    {
      "status": "PREPARING"
    }
    ```
- `DELETE /api/orders/{id}` - Cancel an order

### Order Status Flow
Orders go through the following statuses:
1. `PENDING` - Order received
2. `CONFIRMED` - Order confirmed by staff
3. `PREPARING` - Order is being prepared
4. `READY` - Order is ready for pickup/delivery
5. `DELIVERED` - Order has been delivered
6. `CANCELLED` - Order was cancelled

- `GET /api/customers/{id}` - Get customer by ID
  - Returns: Customer details

- `GET /api/customers/email/{email}` - Get customer by email
  - Returns: Customer details

- `GET /api/customers/phone/{phoneNumber}` - Get customer by phone number
  - Returns: Customer details

- `GET /api/customers` - Get all customers
  - Returns: List of all customers

- `PUT /api/customers/{id}` - Update customer information
  - Request Body: Updated Customer object
  - Returns: Updated Customer

- `DELETE /api/customers/{id}` - Delete a customer
  - Returns: 204 No Content on success

- `GET /api/customers/exists/email/{email}` - Check if customer exists by email
  - Returns: boolean
  
- `GET /api/customers/exists/phone/{phoneNumber}` - Check if customer exists by phone number
  - Returns: boolean

### Menu Endpoints
- `GET /api/menu` - Get all menu items
  - Returns: List of all menu items

- `GET /api/menu/categories` - Get all menu categories
  - Returns: List of unique categories

- `GET /api/menu/category/{category}` - Get menu items by category
  - Returns: List of menu items in the specified category

- `GET /api/menu/{id}` - Get menu item by ID
  - Returns: Menu item details

- `POST /api/menu` - Add a new menu item
  - Request Body: MenuItem object (name, description, price, category, available)
  - Returns: Created MenuItem with ID

- `PUT /api/menu/{id}` - Update a menu item
  - Request Body: Updated MenuItem object
  - Returns: Updated MenuItem

- `DELETE /api/menu/{id}` - Delete a menu item
  - Returns: 204 No Content on success

### Order Endpoints
- `POST /api/orders` - Create a new order
  - Request Body: 
    ```json
    {
      "customerPhone": "+1234567890",
      "orderItems": [
        {
          "menuItemId": 1,
          "quantity": 2
        }
      ]
    }
    ```
  - Returns: Created Order with ID and calculated total

- `GET /api/orders/{id}` - Get order by ID
  - Returns: Order details with items

- `GET /api/orders/customer/{email}` - Get orders by customer email
  - Returns: List of customer's orders

- `GET /api/orders` - Get all orders
  - Returns: List of all orders

- `PUT /api/orders/{id}/status` - Update order status
  - Request Body: 
    ```json
    {
      "status": "PREPARING"
    }
    ```
  - Valid statuses: PENDING, PREPARING, READY, COMPLETED, CANCELLED
  - Returns: Updated Order

- `GET /api/orders/status/{status}` - Get orders by status
  - Returns: List of orders with the specified status

- `GET /api/orders/status/{status}/count` - Count orders by status
  - Returns: Number of orders with the specified status

## Sample API Usage

### 1. Get Menu Items
```bash
curl http://localhost:8083/api/menu
```

### 2. Create an Order
```bash
curl -X POST http://localhost:8083/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerPhone": "+1234567890",
    "orderItems": [
      {
        "menuItemId": 1,
        "quantity": 2
      },
      {
        "menuItemId": 3,
        "quantity": 1
      }
    ]
  }'
```

### 3. Update Order Status
```bash
curl -X PUT http://localhost:8083/api/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "CONFIRMED"}'
```

### 4. Get Orders by Customer
```bash
curl http://localhost:8083/api/orders/customer/john@example.com
```

## 📦 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/restaurant/
│   │       ├── config/         # Configuration classes
│   │       ├── controller/     # REST controllers
│   │       ├── dto/           # Data Transfer Objects
│   │       ├── entity/        # JPA entities
│   │       ├── repository/    # JPA repositories
│   │       ├── service/       # Business logic
│   │       └── RestaurantManagementApplication.java
│   └── resources/
│       ├── application.properties  # Application configuration
│       └── schema.sql             # Database schema
└── test/                        # Test classes
```

## 🔄 Order Status Flow

1. **PENDING** - Order just created, awaiting confirmation
2. **PREPARING** - Order is being prepared
3. **READY** - Order is ready for pickup/delivery
4. **COMPLETED** - Order has been completed
5. **CANCELLED** - Order has been cancelled

## Sample Data

The application comes with pre-loaded sample data:

### Sample Menu Items:
- Margherita Pizza - $12.99 (Pizza)
- Pepperoni Pizza - $14.99 (Pizza)
- Caesar Salad - $8.99 (Salad)
- Grilled Chicken Burger - $11.99 (Burger)
- Chocolate Brownie - $6.99 (Dessert)
- French Fries - $4.99 (Sides)
- Spaghetti Carbonara - $13.99 (Pasta)
- Green Tea - $2.99 (Beverages)

### Sample Customers:
- John Doe - john@example.com
- Jane Smith - jane@example.com

## Database Schema

The application uses the following entities:

- **MenuItem**: Stores menu item information
- **Customer**: Stores customer information
- **Order**: Stores order information with customer relationship
- **OrderItem**: Stores individual items within an order

## Error Handling

The API includes proper error handling for:
- Resource not found (404)
- Invalid requests (400)
- Server errors (500)

## Development

### Project Structure
```
src/main/java/com/restaurant/
├── RestaurantManagementApplication.java  # Main application class
├── config/                              # Configuration classes
│   ├── DataLoader.java                  # Initial data loader
│   └── OpenAPIConfig.java               # OpenAPI/Swagger configuration
├── controller/                          # REST controllers
│   ├── CustomerController.java          # Customer management endpoints
│   ├── HomeController.java              # Root and health check endpoints
│   ├── MenuController.java              # Menu item management endpoints
│   └── OrderController.java             # Order management endpoints
├── entity/                             # JPA entities and DTOs
│   ├── CreateOrderRequest.java         # Request DTO for creating orders
│   ├── Customer.java                   # Customer entity
│   ├── MenuItem.java                   # Menu item entity
│   ├── Order.java                      # Order entity
│   ├── OrderItem.java                  # Order item entity
│   ├── OrderItemRequest.java           # Request DTO for order items
│   ├── OrderStatus.java                # Order status enum
│   └── UpdateStatusRequest.java        # Request DTO for updating order status
├── generator/                          # Custom ID generators
│   └── OrderIdGenerator.java           # Custom ID generator for orders
├── repository/                         # JPA repositories
│   ├── CustomerRepository.java         # Customer data access
│   ├── MenuItemRepository.java         # Menu item data access
│   └── OrderRepository.java            # Order data access
└── service/                            # Business logic services
    ├── CustomerService.java           # Customer business logic
    ├── MenuService.java               # Menu item business logic
    └── OrderService.java              # Order business logic
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.
