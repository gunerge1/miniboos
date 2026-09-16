package com.miniboos.service;

import com.miniboos.common.BizException;
import com.miniboos.dto.req.BizReq;
import com.miniboos.entity.Company;
import com.miniboos.mapper.CompanyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyMapper companyMapper;
    private final DictService dictService;

    public void submit(Long hrUserId, BizReq.Company req) {
        if (companyMapper.findByHrUserId(hrUserId) != null) {
            throw BizException.conflict("你已提交过企业认证（一人一企业）");
        }
        dictService.checkCodeUsable("industry", req.getIndustry());
        Company company = new Company();
        company.setHrUserId(hrUserId);
        company.setName(req.getName());
        company.setIndustry(req.getIndustry());
        company.setLicenseNo(req.getLicenseNo());
        companyMapper.insert(company);
    }

    public Company myCompany(Long hrUserId) {
        return companyMapper.findByHrUserId(hrUserId);
    }
}
