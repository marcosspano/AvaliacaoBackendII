package com.dh.msbills.service;

import com.dh.msbills.models.Bill;
import com.dh.msbills.repositories.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository repository;

    public List<Bill> getAllBill() {
        return repository.findAll();
    }

    public List<Bill> getAllBill(String customerBill) {
        return repository.findAllByCustomerBill(customerBill).orElseThrow(() -> new RuntimeException("Erro ao buscar " +
                "faturas."));
    }

    public void save(Bill bill) {
        repository.save(bill);
    }

}
