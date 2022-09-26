package com.dh.msusers.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bills {

    private String idBill;

    private String customerBill;

    private String productBill;

    private Double totalPrice;

}
