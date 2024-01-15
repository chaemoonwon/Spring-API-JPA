package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;


    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }

        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
            // Entity 조회
            List < Order > orders = orderRepository.findAll(new OrderSearch());
            // Dto로 변환해서 출력
            return orders.stream()
                    .map(o -> new OrderDto(o))
                .collect(toList());
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3() {
        List<Order> orders = orderRepository.findAllWithItem();

        for (Order order : orders) {
            System.out.println("order = " + order + " id = " + order.getId());
        }

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto((Order) o))
                .collect(toList());
        return result;
    }


    // .ToOne 관계는 row수를 증가시키지 않으므로 페이징 쿼리에 영향을 주지 않는다.
    // 페이징을 가능하게 하는 장점이 있음
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> orderV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit)
    {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset,limit);

//        for (Order order : orders) {
//            System.out.println("order = " + order + " id = " + order.getId());
//        }

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto((Order) o))
                .collect(toList());
        return result;
    }


    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDto();
    }
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByOto_optimization();
    }

    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flat = orderQueryRepository.findAllByDto_flat();


        return flat.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());

    }





    @Data
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        // OrderItem의 Entity의 모든 정보를 외부로 노출하는 것을 막기 위해
        // 필요한 정보 만을 노출하는 Dto를 따로 생성해서 전달해 주어야 함.
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {

            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
//            order.getOrderItems().stream().forEach(o -> o.getItem().getName()); //이와 같은 방법은 Entity의 모든 정보를 노출할 위험이 있음
//            orderItems = order.getOrderItems();

            orderItems = order.getOrderItems()
                    .stream().map(orderItem -> new OrderItemDto(orderItem))
                    .collect(toList());
        }
    }




    @Data
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {

            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getItem().getPrice();
            count = orderItem.getCount();
        }
    }


}
