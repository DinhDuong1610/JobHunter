package com.dinhduong.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dinhduong.jobhunter.domain.Company;
import com.dinhduong.jobhunter.domain.User;
import com.dinhduong.jobhunter.domain.response.ResultPaginationDTO;
import com.dinhduong.jobhunter.repository.CompanyRepository;
import com.dinhduong.jobhunter.repository.UserRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company hanleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO fetchAllCompanies(Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageCompany.getTotalPages());
        mt.setTotal(pageCompany.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageCompany.getContent());

        return rs;
    }

    public Company fetchCompanyById(long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if (companyOptional.isPresent()) {
            return companyOptional.get();
        }
        return null;
    }

    public void handleDeleteCompany(long id) {
        Optional<Company> comOptional = this.companyRepository.findById(id);
        if (comOptional.isPresent()) {
            Company company = comOptional.get();
            List<User> users = this.userRepository.findByCompany(company);
            this.userRepository.deleteAll(users);
        }
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
