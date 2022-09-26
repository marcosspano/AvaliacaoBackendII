package com.dh.msbills.controller;

import com.dh.msbills.models.Bill;
import com.dh.msbills.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/bills")
public class BillController {

    @Autowired
    private BillService service;

    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Bill>> getAll() {
        return ResponseEntity.ok().body(service.getAllBill());
    }

    @GetMapping("/find-all/{customerBill}")
    public List<Bill> getAll(@PathVariable String customerBill) {
        return service.getAllBill(customerBill);
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Bill bill) {
        service.save(bill);
        return new ResponseEntity<>("Dados salvos com sucesso", HttpStatus.CREATED);
    }

}
