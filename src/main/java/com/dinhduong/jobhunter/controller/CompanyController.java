package com.dinhduong.jobhunter.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dinhduong.jobhunter.domain.Company;
import com.dinhduong.jobhunter.service.CompanyService;

import jakarta.validation.Valid;

@RestController
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company reqCompany) {
        Company newCompany = this.companyService.hanleCreateCompany(reqCompany);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }

    @GetMapping("/companies")
    public ResponseEntity<List<Company>> getAllCompanies() {
        return ResponseEntity.ok(this.companyService.fetchAllCompanies());
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable("id") long id) {
        return ResponseEntity.ok(this.companyService.fetchCompanyById(id));
    }

}
