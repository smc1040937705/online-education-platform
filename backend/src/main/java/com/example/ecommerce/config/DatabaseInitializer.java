package com.example.ecommerce.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            dropAndRecreateTables();
            initializeData();
            log.info("Database initialized successfully");
        } catch (Exception e) {
            log.error("Database initialization failed: {}", e.getMessage(), e);
        }
    }

    private void dropAndRecreateTables() {
        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS enrollment_progress");
            jdbcTemplate.execute("DROP TABLE IF EXISTS enrollments");
            jdbcTemplate.execute("DROP TABLE IF EXISTS wishlists");
            jdbcTemplate.execute("DROP TABLE IF EXISTS chapters");
            jdbcTemplate.execute("DROP TABLE IF EXISTS addresses");
            jdbcTemplate.execute("DROP TABLE IF EXISTS courses");
            jdbcTemplate.execute("DROP TABLE IF EXISTS categories");
            jdbcTemplate.execute("DROP TABLE IF EXISTS users");

            jdbcTemplate.execute("CREATE TABLE users (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, " +
                "email VARCHAR(100) NOT NULL UNIQUE, " +
                "phone VARCHAR(20), " +
                "avatar VARCHAR(255), " +
                "balance DECIMAL(10,2) DEFAULT 0.00, " +
                "status INT DEFAULT 1, " +
                "role INT DEFAULT 0, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "deleted INT DEFAULT 0)");

            jdbcTemplate.execute("CREATE TABLE categories (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(50) NOT NULL, " +
                "description VARCHAR(255), " +
                "parent_id BIGINT DEFAULT 0, " +
                "level INT DEFAULT 1, " +
                "sort_order INT DEFAULT 0, " +
                "icon VARCHAR(255), " +
                "status INT DEFAULT 1, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "deleted INT DEFAULT 0)");

            jdbcTemplate.execute("CREATE TABLE courses (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(200) NOT NULL, " +
                "description TEXT, " +
                "detail TEXT, " +
                "category_id BIGINT NOT NULL, " +
                "price DECIMAL(10,2) NOT NULL, " +
                "original_price DECIMAL(10,2), " +
                "enrollment INT DEFAULT 0, " +
                "students INT DEFAULT 0, " +
                "cover_image VARCHAR(255), " +
                "status INT DEFAULT 1, " +
                "is_hot INT DEFAULT 0, " +
                "is_new INT DEFAULT 0, " +
                "is_recommend INT DEFAULT 0, " +
                "duration INT DEFAULT 0, " +
                "teacher VARCHAR(100), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "deleted INT DEFAULT 0)");

            jdbcTemplate.execute("CREATE TABLE chapters (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "course_id BIGINT NOT NULL, " +
                "name VARCHAR(200) NOT NULL, " +
                "sort_order INT DEFAULT 0, " +
                "duration INT DEFAULT 0, " +
                "video_url VARCHAR(500), " +
                "status INT DEFAULT 1, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "deleted INT DEFAULT 0)");

            jdbcTemplate.execute("CREATE TABLE wishlists (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "course_id BIGINT NOT NULL, " +
                "selected INT DEFAULT 1, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "deleted INT DEFAULT 0)");

            jdbcTemplate.execute("CREATE TABLE addresses (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "receiver_name VARCHAR(50) NOT NULL, " +
                "receiver_phone VARCHAR(20) NOT NULL, " +
                "province VARCHAR(50) NOT NULL, " +
                "city VARCHAR(50) NOT NULL, " +
                "district VARCHAR(50) NOT NULL, " +
                "detail_address VARCHAR(255) NOT NULL, " +
                "zip_code VARCHAR(10), " +
                "is_default INT DEFAULT 0, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "deleted INT DEFAULT 0)");

            jdbcTemplate.execute("CREATE TABLE enrollments (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "enrollment_no VARCHAR(50) NOT NULL UNIQUE, " +
                "user_id BIGINT NOT NULL, " +
                "course_id BIGINT NOT NULL, " +
                "total_amount DECIMAL(10,2) NOT NULL, " +
                "pay_amount DECIMAL(10,2) NOT NULL, " +
                "discount_amount DECIMAL(10,2) DEFAULT 0.00, " +
                "pay_type INT DEFAULT 0, " +
                "status INT DEFAULT 0, " +
                "pay_time TIMESTAMP, " +
                "expire_time TIMESTAMP, " +
                "complete_time TIMESTAMP, " +
                "progress INT DEFAULT 0, " +
                "remark VARCHAR(500), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "deleted INT DEFAULT 0)");

            jdbcTemplate.execute("CREATE TABLE enrollment_progress (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "enrollment_id BIGINT NOT NULL, " +
                "chapter_id BIGINT NOT NULL, " +
                "progress INT DEFAULT 0, " +
                "status INT DEFAULT 0, " +
                "last_watch_time TIMESTAMP, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "deleted INT DEFAULT 0)");

            log.info("Tables recreated successfully");
        } catch (Exception e) {
            log.error("Table recreation failed: {}", e.getMessage());
            throw e;
        }
    }

    private void initializeData() {
        try {
            jdbcTemplate.execute("INSERT INTO categories (name, parent_id, level, sort_order, status) VALUES " +
                "('编程开发', 0, 1, 1, 1), ('人工智能', 0, 1, 2, 1), ('设计创意', 0, 1, 3, 1), " +
                "('Java', 1, 2, 1, 1), ('Python', 1, 2, 2, 1), ('前端开发', 1, 2, 3, 1), " +
                "('机器学习', 2, 2, 1, 1), ('深度学习', 2, 2, 2, 1), ('UI设计', 3, 2, 1, 1), ('平面设计', 3, 2, 2, 1)");

            jdbcTemplate.execute("INSERT INTO courses (name, category_id, price, original_price, enrollment, students, description, cover_image, status, is_hot, is_new, duration, teacher) VALUES " +
                "('Java零基础入门到精通', 4, 99.00, 199.00, 0, 1523, '最适合初学者的Java教程，从零开始学Java', 'https://via.placeholder.com/300x200?text=Java', 1, 1, 1, 1200, '张老师'), " +
                "('Python数据分析实战', 5, 129.00, 299.00, 0, 896, '手把手教你Python数据分析与可视化', 'https://via.placeholder.com/300x200?text=Python', 1, 1, 0, 1500, '李老师'), " +
                "('Vue3企业级项目实战', 6, 149.00, 399.00, 0, 654, 'Vue3+TypeScript+Vite打造企业级项目', 'https://via.placeholder.com/300x200?text=Vue3', 1, 0, 1, 1800, '王老师'), " +
                "('机器学习入门与实践', 7, 199.00, 499.00, 0, 1234, '机器学习基础算法与Python实现', 'https://via.placeholder.com/300x200?text=ML', 1, 1, 0, 2000, '陈老师'), " +
                "('深度学习神经网络', 8, 299.00, 599.00, 0, 567, 'TensorFlow/PyTorch深度学习实战', 'https://via.placeholder.com/300x200?text=DL', 1, 0, 1, 2200, '刘老师'), " +
                "('UI设计精品课程', 9, 79.00, 199.00, 0, 2341, '从入门到进阶的UI设计全栈课程', 'https://via.placeholder.com/300x200?text=UI', 1, 1, 1, 1000, '赵老师')");

            jdbcTemplate.execute("INSERT INTO chapters (course_id, name, sort_order, duration, status) VALUES " +
                "(1, '第一章：Java环境搭建', 1, 600, 1), (1, '第二章：Java基础语法', 2, 900, 1), (1, '第三章：面向对象编程', 3, 1200, 1), " +
                "(2, '第一章：Python入门', 1, 800, 1), (2, '第二章：NumPy数据处理', 2, 1000, 1), (2, '第三章：Pandas数据分析', 3, 1200, 1), " +
                "(3, '第一章：Vue3基础', 1, 700, 1), (3, '第二章：Composition API', 2, 900, 1), (3, '第三章：项目实战', 3, 1500, 1)");

            log.info("Initial data inserted successfully");
        } catch (Exception e) {
            log.warn("Data initialization warning: {}", e.getMessage());
        }
    }
}
