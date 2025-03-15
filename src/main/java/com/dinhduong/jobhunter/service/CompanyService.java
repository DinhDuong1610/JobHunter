package com.dinhduong.jobhunter.service;

import java.util.List;
import java.util.Optional;

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

    public Company fetchCompanyById(long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if (companyOptional.isPresent()) {
            return companyOptional.get();
        }
        return null;
    }

    public void handleDeleteCompany(long id) {
        this.companyRepository.deleteById(id);
    }

    public Company handleUpdateCompany(Company reqCompany) {
        Company currentCompany = fetchCompanyById(reqCompany.getId());
        if (currentCompany != null) {
            currentCompany.setName(reqCompany.getName());
            currentCompany.setAddress(reqCompany.getAddress());
            currentCompany.setDescription(reqCompany.getDescription());
            currentCompany.setLogo(reqCompany.getLogo());
            return this.companyRepository.save(currentCompany);
        }
        return null;
    }

}
