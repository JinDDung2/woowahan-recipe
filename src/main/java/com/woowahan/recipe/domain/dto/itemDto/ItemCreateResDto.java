package com.woowahan.recipe.domain.dto.itemDto;

import com.woowahan.recipe.domain.entity.ItemEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ItemCreateResDto {

    private Long id;
    private String itemName;
    private String message;

    public static ItemCreateResDto from(ItemEntity item) {
        return ItemCreateResDto.builder()
                .id(item.getId())
                .itemName(item.getName())
                .message("상품 등록 완료")
                .build();
    }

}
