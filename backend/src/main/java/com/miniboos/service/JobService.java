package com.miniboos.service;

import com.miniboos.common.BizException;
import com.miniboos.dto.JobVO;
import com.miniboos.dto.PageVO;
import com.miniboos.dto.req.BizReq;
import com.miniboos.entity.Company;
import com.miniboos.entity.Job;
import com.miniboos.entity.Resume;
import com.miniboos.mapper.CompanyMapper;
import com.miniboos.mapper.JobMapper;
import com.miniboos.mapper.ResumeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobMapper jobMapper;
    private final CompanyMapper companyMapper;
    private final ResumeMapper resumeMapper;
    private final DictService dictService;

    /** C端职位列表：未筛选时按当前牛人意向打分排序（有简历才有的待遇） */
    public PageVO<JobVO> listPublic(String category, String city, String kw, int page, int size, Long userId) {
        String expectCategory = null, expectCity = null;
        Integer expectSalaryMin = null, expectSalaryMax = null;
        Resume resume = resumeMapper.findByUserId(userId);
        if (resume != null) {
            expectCategory = resume.getExpectCategory();
            expectCity = resume.getExpectCity();
            expectSalaryMin = resume.getExpectSalaryMin();
            expectSalaryMax = resume.getExpectSalaryMax();
        }
        int offset = (page - 1) * size;
        List<JobVO> list = jobMapper.listPublic(category, city, kw, offset, size,
                expectCategory, expectCity, expectSalaryMin, expectSalaryMax);
        // 联表已带label，列表不暴露photo等大字段（JobVO本就没有）
        long total = jobMapper.countPublic(category, city, kw);
        return PageVO.of(list, total, page, size);
    }

    public JobVO detail(Long id) {
        Job job = jobMapper.findById(id);
        if (job == null) return null;
        Company company = companyMapper.findById(job.getCompanyId());
        if (!"ACTIVE".equals(job.getStatus()) || company == null || !"APPROVED".equals(company.getStatus())) {
            return null; // 未上架/企业未过审=对牛人不存在
        }
        // 复用listPublic的单条查询代价高，直接手拼VO
        JobVO vo = new JobVO();
        vo.setId(job.getId());
        vo.setTitle(job.getTitle());
        vo.setCategory(job.getCategory());
        vo.setCity(job.getCity());
        vo.setEducation(job.getEducation());
        vo.setSalaryMin(job.getSalaryMin());
        vo.setSalaryMax(job.getSalaryMax());
        vo.setDescription(job.getDescription());
        vo.setStatus(job.getStatus());
        vo.setCompanyId(company.getId());
        vo.setCompanyName(company.getName());
        translateLabels(vo, company);
        return vo;
    }

    private void translateLabels(JobVO vo, Company company) {
        for (var d : dictService.enabledByType("category")) {
            if (d.getCode().equals(vo.getCategory())) vo.setCategoryLabel(d.getLabel());
        }
        for (var d : dictService.enabledByType("city")) {
            if (d.getCode().equals(vo.getCity())) vo.setCityLabel(vo.getCityLabel() == null ? d.getLabel() : vo.getCityLabel());
        }
        if (vo.getCityLabel() == null) vo.setCityLabel(vo.getCity());
        if (vo.getCategoryLabel() == null) vo.setCategoryLabel(vo.getCategory());
        if (vo.getEducation() != null) {
            for (var d : dictService.enabledByType("education")) {
                if (d.getCode().equals(vo.getEducation())) vo.setEducationLabel(d.getLabel());
            }
            if (vo.getEducationLabel() == null) vo.setEducationLabel(vo.getEducation());
        }
        for (var d : dictService.enabledByType("industry")) {
            if (d.getCode().equals(company.getIndustry())) vo.setCompanyIndustryLabel(d.getLabel());
        }
        if (vo.getCompanyIndustryLabel() == null) vo.setCompanyIndustryLabel(company.getIndustry());
    }

    public void publish(Long hrUserId, BizReq.Job req) {
        Company company = companyMapper.findByHrUserId(hrUserId);
        if (company == null || !"APPROVED".equals(company.getStatus())) {
            throw BizException.forbidden("企业认证通过后才能发布职位");
        }
        dictService.checkCodeUsable("category", req.getCategory());
        dictService.checkCodeUsable("city", req.getCity());
        dictService.checkCodeUsable("education", req.getEducation());
        Job job = new Job();
        job.setCompanyId(company.getId());
        job.setTitle(req.getTitle());
        job.setCategory(req.getCategory());
        job.setCity(req.getCity());
        job.setEducation(req.getEducation());
        job.setSalaryMin(req.getSalaryMin());
        job.setSalaryMax(req.getSalaryMax());
        job.setDescription(req.getDescription());
        jobMapper.insert(job); // 初始PENDING，待Admin审核
    }

    public List<Job> myJobs(Long hrUserId) {
        Company company = companyMapper.findByHrUserId(hrUserId);
        if (company == null) return List.of();
        return jobMapper.listByCompany(company.getId());
    }

    /** HR上架/下架：只能动自己的职位，且只在ACTIVE/OFF之间切换 */
    private static final Set<String> HR_SWITCHABLE = Set.of("ACTIVE", "OFF");

    public void switchStatus(Long hrUserId, Long jobId, String target) {
        if (!HR_SWITCHABLE.contains(target)) {
            throw BizException.badRequest("HR只能上架/下架");
        }
        Job job = ownedJob(hrUserId, jobId);
        if (!HR_SWITCHABLE.contains(job.getStatus())) {
            throw BizException.badRequest("当前状态不允许此操作（待审/被驳回的职位请联系平台）");
        }
        jobMapper.updateStatus(jobId, target);
    }

    public Job ownedJob(Long hrUserId, Long jobId) {
        Job job = jobMapper.findById(jobId);
        if (job == null) throw BizException.notFound("职位不存在");
        Company company = companyMapper.findById(job.getCompanyId());
        if (company == null || !company.getHrUserId().equals(hrUserId)) {
            throw BizException.forbidden("只能操作自己企业的职位");
        }
        return job;
    }
}
