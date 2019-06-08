package com.leyou.order.dto;

import com.leyou.common.dto.CartDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    @NotNull
    private Long addressId; // 收获人地址id
    @NotNull
    private Integer paymentType;// 付款类型
    @NotNull
    private List<CartDto> carts;// 订单详情

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public List<CartDto> getCarts() {
        return carts;
    }

    public void setCarts(List<CartDto> carts) {
        this.carts = carts;
    }
}