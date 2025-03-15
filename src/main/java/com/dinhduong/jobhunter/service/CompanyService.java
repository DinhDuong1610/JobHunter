package com.dinhduong.jobhunter.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dinhduong.jobhunter.domain.Company;
import com.dinhduong.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company hanleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public List<Company> fetchAllCompanies() {
        return this.companyRepository.findAll();
    }

}
