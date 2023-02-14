package com.woowahan.recipe.domain.dto.sellerDto;

import com.woowahan.recipe.domain.UserRole;
import com.woowahan.recipe.domain.entity.SellerEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerResponse {
    private String sellerName;
    private String companyName;
    private String address;
    private String email;
    private String phoneNum;
    private String businessRegNum;
    private UserRole status;
    private String message;


    public static SellerResponse toSellerResponse(SellerEntity seller) {
        return SellerResponse.builder()
                .sellerName(seller.getSellerName())
                .companyName(seller.getCompanyName())
                .address(seller.getAddress())
                .email(seller.getEmail())
                .phoneNum(seller.getPhoneNum())
                .businessRegNum(seller.getBusinessRegNum())
                .status(seller.getUserRole())
                .message("수정이 완료되었습니다.")
                .build();
    }
}


