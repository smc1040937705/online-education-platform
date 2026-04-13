package com.example.ecommerce.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ecommerce.dto.CreateOrderRequest;
import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.mapper.*;
import com.example.ecommerce.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orderService, "baseMapper", orderMapper);
    }

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private AddressMapper addressMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserCouponMapper userCouponMapper;

    @Mock
    private CouponMapper couponMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_WithValidData_ShouldCreateOrderSuccessfully() {
        Long userId = 1L;
        CreateOrderRequest request = new CreateOrderRequest();
        request.setAddressId(1L);
        request.setCartIds(Arrays.asList(1L, 2L));
        request.setCouponId(null);
        request.setRemark("测试订单");

        Address address = new Address();
        address.setId(1L);
        address.setUserId(1L);
        address.setReceiverName("张三");
        address.setReceiverPhone("13800138000");
        address.setProvince("北京市");
        address.setCity("北京市");
        address.setDistrict("朝阳区");
        address.setDetailAddress("测试街道123号");

        Cart cart1 = new Cart();
        cart1.setId(1L);
        cart1.setUserId(1L);
        cart1.setProductId(101L);
        cart1.setQuantity(2);

        Cart cart2 = new Cart();
        cart2.setId(2L);
        cart2.setUserId(1L);
        cart2.setProductId(102L);
        cart2.setQuantity(1);

        Product product1 = new Product();
        product1.setId(101L);
        product1.setName("商品1");
        product1.setPrice(new BigDecimal("99.99"));
        product1.setStock(100);
        product1.setStatus(1);
        product1.setMainImage("image1.jpg");

        Product product2 = new Product();
        product2.setId(102L);
        product2.setName("商品2");
        product2.setPrice(new BigDecimal("199.99"));
        product2.setStock(50);
        product2.setStatus(1);
        product2.setMainImage("image2.jpg");

        when(addressMapper.selectById(1L)).thenReturn(address);
        when(cartMapper.findByIdsAndUserId("1,2", 1L)).thenReturn(Arrays.asList(cart1, cart2));
        when(productMapper.selectById(101L)).thenReturn(product1);
        when(productMapper.selectById(102L)).thenReturn(product2);
        when(orderMapper.insert(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return 1;
        });

        OrderDTO result = orderService.createOrder(userId, request);

        assertNotNull(result);
        assertNotNull(result.getOrderNo());
        assertEquals(0, result.getStatus());
        assertEquals(new BigDecimal("399.97"), result.getTotalAmount());
        assertEquals(BigDecimal.ZERO, result.getFreightAmount());
        assertEquals(new BigDecimal("399.97"), result.getPayAmount());
        assertEquals("张三", result.getReceiverName());

        verify(orderMapper).insert(any(Order.class));
        verify(orderItemMapper, times(2)).insert(any(OrderItem.class));
        verify(productMapper).deductStock(101L, 2);
        verify(productMapper).deductStock(102L, 1);
        verify(cartMapper).deleteById(1L);
        verify(cartMapper).deleteById(2L);
    }

    @Test
    void createOrder_WithInvalidAddress_ShouldThrowException() {
        Long userId = 1L;
        CreateOrderRequest request = new CreateOrderRequest();
        request.setAddressId(999L);
        request.setCartIds(Arrays.asList(1L));

        when(addressMapper.selectById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.createOrder(userId, request));
        assertEquals("地址不存在", exception.getMessage());
        verify(orderMapper, never()).insert(any(Order.class));
    }

    @Test
    void createOrder_WithWrongUserAddress_ShouldThrowException() {
        Long userId = 1L;
        CreateOrderRequest request = new CreateOrderRequest();
        request.setAddressId(1L);
        request.setCartIds(Arrays.asList(1L));

        Address address = new Address();
        address.setId(1L);
        address.setUserId(2L);

        when(addressMapper.selectById(1L)).thenReturn(address);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.createOrder(userId, request));
        assertEquals("地址不存在", exception.getMessage());
    }

    @Test
    void createOrder_WithEmptyCart_ShouldThrowException() {
        Long userId = 1L;
        CreateOrderRequest request = new CreateOrderRequest();
        request.setAddressId(1L);
        request.setCartIds(Arrays.asList(999L));

        Address address = new Address();
        address.setId(1L);
        address.setUserId(1L);

        when(addressMapper.selectById(1L)).thenReturn(address);
        when(cartMapper.findByIdsAndUserId("999", 1L)).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.createOrder(userId, request));
        assertEquals("购物车为空", exception.getMessage());
    }

    @Test
    void createOrder_WithOffShelfProduct_ShouldThrowException() {
        Long userId = 1L;
        CreateOrderRequest request = new CreateOrderRequest();
        request.setAddressId(1L);
        request.setCartIds(Arrays.asList(1L));

        Address address = new Address();
        address.setId(1L);
        address.setUserId(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setProductId(101L);
        cart.setQuantity(1);

        Product product = new Product();
        product.setId(101L);
        product.setStatus(0);

        when(addressMapper.selectById(1L)).thenReturn(address);
        when(cartMapper.findByIdsAndUserId("1", 1L)).thenReturn(Arrays.asList(cart));
        when(productMapper.selectById(101L)).thenReturn(product);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.createOrder(userId, request));
        assertEquals("商品不存在或已下架: 101", exception.getMessage());
    }

    @Test
    void createOrder_WithInsufficientStock_ShouldThrowException() {
        Long userId = 1L;
        CreateOrderRequest request = new CreateOrderRequest();
        request.setAddressId(1L);
        request.setCartIds(Arrays.asList(1L));

        Address address = new Address();
        address.setId(1L);
        address.setUserId(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setProductId(101L);
        cart.setQuantity(10);

        Product product = new Product();
        product.setId(101L);
        product.setName("限量商品");
        product.setStatus(1);
        product.setStock(5);

        when(addressMapper.selectById(1L)).thenReturn(address);
        when(cartMapper.findByIdsAndUserId("1", 1L)).thenReturn(Arrays.asList(cart));
        when(productMapper.selectById(101L)).thenReturn(product);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.createOrder(userId, request));
        assertEquals("商品库存不足: 限量商品", exception.getMessage());
    }

    @Test
    void createOrder_WithFreeShipping_ShouldNotChargeFreight() {
        Long userId = 1L;
        CreateOrderRequest request = new CreateOrderRequest();
        request.setAddressId(1L);
        request.setCartIds(Arrays.asList(1L));

        Address address = new Address();
        address.setId(1L);
        address.setUserId(1L);
        address.setReceiverName("张三");
        address.setDetailAddress("测试地址");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setProductId(101L);
        cart.setQuantity(2);

        Product product = new Product();
        product.setId(101L);
        product.setName("高价商品");
        product.setPrice(new BigDecimal("100.00"));
        product.setStock(100);
        product.setStatus(1);

        when(addressMapper.selectById(1L)).thenReturn(address);
        when(cartMapper.findByIdsAndUserId("1", 1L)).thenReturn(Arrays.asList(cart));
        when(productMapper.selectById(101L)).thenReturn(product);
        when(orderMapper.insert(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return 1;
        });

        OrderDTO result = orderService.createOrder(userId, request);

        assertNotNull(result);
        assertEquals(new BigDecimal("200.00"), result.getTotalAmount());
        assertEquals(BigDecimal.ZERO, result.getFreightAmount());
    }

    @Test
    void getOrderById_WithValidId_ShouldReturnOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setOrderNo("20240101000001");
        order.setUserId(1L);
        order.setStatus(1);
        order.setPayAmount(new BigDecimal("99.99"));

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        OrderItem item = new OrderItem();
        item.setId(1L);
        item.setOrderId(1L);
        item.setProductName("测试商品");

        when(orderMapper.selectById(1L)).thenReturn(order);
        when(userMapper.selectById(1L)).thenReturn(user);
        when(orderItemMapper.findByOrderId(1L)).thenReturn(Arrays.asList(item));

        OrderDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("20240101000001", result.getOrderNo());
        assertEquals("testuser", result.getUsername());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void getOrderById_WithInvalidId_ShouldThrowException() {
        when(orderMapper.selectById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.getOrderById(999L));
        assertEquals("订单不存在", exception.getMessage());
    }

    @Test
    void getUserOrders_ShouldReturnPagedOrders() {
        Long userId = 1L;
        Page<Order> orderPage = new Page<>(1, 10);

        Order order1 = new Order();
        order1.setId(1L);
        order1.setOrderNo("20240101000001");
        order1.setUserId(1L);
        order1.setStatus(1);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setOrderNo("20240101000002");
        order2.setUserId(1L);
        order2.setStatus(2);

        orderPage.setRecords(Arrays.asList(order1, order2));
        orderPage.setTotal(2);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(orderMapper.findByUserId(any(Page.class), eq(1L))).thenReturn(orderPage);
        when(userMapper.selectById(1L)).thenReturn(user);
        when(orderItemMapper.findByOrderId(anyLong())).thenReturn(Collections.emptyList());

        Page<OrderDTO> result = orderService.getUserOrders(userId, 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertEquals(2, result.getRecords().size());
        assertEquals("20240101000001", result.getRecords().get(0).getOrderNo());
    }

    @Test
    void cancelOrder_WithValidPendingOrder_ShouldCancelSuccessfully() {
        Long orderId = 1L;
        Long userId = 1L;

        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setStatus(0);

        OrderItem item = new OrderItem();
        item.setProductId(101L);
        item.setQuantity(2);

        when(orderMapper.selectById(1L)).thenReturn(order);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);
        when(orderItemMapper.findByOrderId(1L)).thenReturn(Arrays.asList(item));

        boolean result = orderService.cancelOrder(orderId, userId);

        assertTrue(result);
        assertEquals(-1, order.getStatus());
        verify(productMapper).restoreStock(101L, 2);
    }

    @Test
    void cancelOrder_WithNonPendingStatus_ShouldThrowException() {
        Long orderId = 1L;
        Long userId = 1L;

        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setStatus(1);

        when(orderMapper.selectById(1L)).thenReturn(order);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.cancelOrder(orderId, userId));
        assertEquals("订单状态不允许取消", exception.getMessage());
    }

    @Test
    void payOrder_WithValidOrderAndSufficientBalance_ShouldPaySuccessfully() {
        Long orderId = 1L;
        Long userId = 1L;

        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setStatus(0);
        order.setPayAmount(new BigDecimal("99.99"));

        when(orderMapper.selectById(1L)).thenReturn(order);
        when(userMapper.deductBalance(1L, new BigDecimal("99.99"))).thenReturn(1);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        boolean result = orderService.payOrder(orderId, userId);

        assertTrue(result);
        assertEquals(1, order.getStatus());
        assertNotNull(order.getPayTime());
        verify(userMapper).deductBalance(1L, new BigDecimal("99.99"));
    }

    @Test
    void payOrder_WithInsufficientBalance_ShouldThrowException() {
        Long orderId = 1L;
        Long userId = 1L;

        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setStatus(0);
        order.setPayAmount(new BigDecimal("999.99"));

        when(orderMapper.selectById(1L)).thenReturn(order);
        when(userMapper.deductBalance(1L, new BigDecimal("999.99"))).thenReturn(0);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.payOrder(orderId, userId));
        assertEquals("余额不足", exception.getMessage());
        assertEquals(0, order.getStatus());
    }

    @Test
    void payOrder_WithAlreadyPaidOrder_ShouldThrowException() {
        Long orderId = 1L;
        Long userId = 1L;

        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setStatus(1);

        when(orderMapper.selectById(1L)).thenReturn(order);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.payOrder(orderId, userId));
        assertEquals("订单状态不允许支付", exception.getMessage());
    }

    @Test
    void shipOrder_WithPaidOrder_ShouldShipSuccessfully() {
        Long orderId = 1L;

        Order order = new Order();
        order.setId(1L);
        order.setStatus(1);

        when(orderMapper.selectById(1L)).thenReturn(order);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        boolean result = orderService.shipOrder(orderId);

        assertTrue(result);
        assertEquals(2, order.getStatus());
        assertNotNull(order.getShipTime());
    }

    @Test
    void shipOrder_WithUnpaidOrder_ShouldThrowException() {
        Long orderId = 1L;

        Order order = new Order();
        order.setId(1L);
        order.setStatus(0);

        when(orderMapper.selectById(1L)).thenReturn(order);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.shipOrder(orderId));
        assertEquals("订单状态不允许发货", exception.getMessage());
    }

    @Test
    void receiveOrder_WithShippedOrder_ShouldReceiveSuccessfully() {
        Long orderId = 1L;
        Long userId = 1L;

        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setStatus(2);

        when(orderMapper.selectById(1L)).thenReturn(order);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        boolean result = orderService.receiveOrder(orderId, userId);

        assertTrue(result);
        assertEquals(3, order.getStatus());
        assertNotNull(order.getReceiveTime());
    }

    @Test
    void receiveOrder_WithWrongStatus_ShouldThrowException() {
        Long orderId = 1L;
        Long userId = 1L;

        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setStatus(1);

        when(orderMapper.selectById(1L)).thenReturn(order);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.receiveOrder(orderId, userId));
        assertEquals("订单状态不允许收货", exception.getMessage());
    }
}
