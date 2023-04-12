package com.woowahan.recipe.domain.entity;


import com.woowahan.recipe.exception.AppException;
import com.woowahan.recipe.exception.ErrorCode;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@SQLDelete(sql = "UPDATE item_entity SET deleted_date = current_timestamp WHERE item_id = ?")
@Where(clause = "deleted_date is null")
public class ItemEntity extends BaseEntity{

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemImagePath;
    @NotBlank
    @Column(name = "item_name")
    private String name;
    @NotNull
    private Integer itemPrice;
    @NotNull
    private Integer itemStock;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "stock_id")
    private StockEntity stock;

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<RecipeItemEntity> recipes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private SellerEntity seller;

    /* 아이템 수정 */
    public void update(String itemImagePath, String itemName, Integer itemPrice, Integer itemStock, SellerEntity seller) {
        this.itemImagePath = itemImagePath;
        this.name = itemName;
        this.itemPrice = itemPrice;
        this.itemStock = itemStock;
        this.seller = seller;
    }

    /* 연관관계 메서드 */
    public void addItem() {};

    /* 비지니스 로직 */
    public void decreaseStock(int quantity) {
         int restStock = this.itemStock -= quantity;
         if (restStock < 0) {
             throw new AppException(ErrorCode.NOT_ENOUGH_STOCK, ErrorCode.NOT_ENOUGH_STOCK.getMessage());
         }
        this.itemStock = restStock;
    }

    // item 등록 시 해당 상품을 판매하는 판매자 등록
    public void createSeller(SellerEntity seller) {
        this.seller = seller;
    }
}
