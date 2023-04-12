package com.woowahan.recipe.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    private int orderPrice;
    private int count;

    /* 연관관계 메서드 */
    public void addOrder(OrderEntity order) {
        this.order = order;
    }

    /* 생성 메서드 */
    public static OrderItemEntity createOrderItem(ItemEntity item, int count) {
        OrderItemEntity orderItem = OrderItemEntity.builder()
                .item(item)
                .orderPrice(item.getItemPrice())
                .count(count)
                .build();

        item.getStock().decrease(count);
        return orderItem;
    }

    /* 비지니스 로직 */
    public void cancel() {
        getItem().getStock().increase(count);
    }

    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }

}
