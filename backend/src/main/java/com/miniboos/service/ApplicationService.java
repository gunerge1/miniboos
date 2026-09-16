package com.miniboos.service;

import com.miniboos.common.BizException;
import com.miniboos.dto.ApplicationVO;
import com.miniboos.dto.CandidateVO;
import com.miniboos.dto.ResumeDetailVO;
import com.miniboos.entity.Application;
import com.miniboos.entity.Company;
import com.miniboos.entity.Job;
import com.miniboos.entity.Resume;
import com.miniboos.mapper.ApplicationMapper;
import com.miniboos.mapper.CompanyMapper;
import com.miniboos.mapper.JobMapper;
import com.miniboos.mapper.ResumeExperienceMapper;
import com.miniboos.mapper.ResumeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    /** 投递状态机（系统词汇，永不入字典——技术设计文档字典三原则） */
    private static final Map<String, Set<String>> TRANSITIONS = Map.of(
            "SUBMITTED", Set.of("VIEWED", "INTERVIEW", "OFFER", "REJECTED"),
            "VIEWED", Set.of("INTERVIEW", "OFFER", "REJECTED"),
            "INTERVIEW", Set.of("OFFER", "REJECTED"),
            "OFFER", Set.of(),
            "REJECTED", Set.of());

    private final ApplicationMapper applicationMapper;
    private final JobMapper jobMapper;
    private final CompanyMapper companyMapper;
    private final ResumeMapper resumeMapper;
    private final ResumeExperienceMapper expMapper;
    private final JobService jobService;

    public void apply(Long userId, Long jobId) {
        Resume resume = resumeMapper.findByUserId(userId);
        if (resume == null || resume.getPublished() != 1) {
            throw BizException.badRequest("请先完善并发布简历，再投递（投递门槛）");
        }
        Job job = jobMapper.findById(jobId);
        if (job == null || !"ACTIVE".equals(job.getStatus())) {
            throw BizException.notFound("职位不存在或已下架");
        }
        Company company = companyMapper.findById(job.getCompanyId());
        if (company == null || !"APPROVED".equals(company.getStatus())) {
            throw BizException.notFound("职位不存在或已下架");
        }
        if (applicationMapper.countByJobAndUser(jobId, userId) > 0) {
            throw BizException.conflict("你已投递过该职位，不能重复投");
        }
        Application app = new Application();
        app.setJobId(jobId);
        app.setUserId(userId);
        applicationMapper.insert(app);
    }

    public List<ApplicationVO> my(Long userId) {
        return applicationMapper.listByUser(userId);
    }

    public List<CandidateVO> candidatesOfJob(Long hrUserId, Long jobId) {
        jobService.ownedJob(hrUserId, jobId); // 归属校验
        return applicationMapper.listCandidatesByJob(jobId);
    }

    public void updateStatus(Long hrUserId, Long appId, String target) {
        Application app = applicationMapper.findById(appId);
        if (app == null) throw BizException.notFound("投递记录不存在");
        jobService.ownedJob(hrUserId, app.getJobId()); // HR只能动自己职位的投递
        Set<String> allowed = TRANSITIONS.get(app.getStatus());
        if (allowed == null || !allowed.contains(target)) {
            throw BizException.badRequest("状态不允许从 " + app.getStatus() + " 流转到 " + target);
        }
        switch (target) {
            case "VIEWED" -> applicationMapper.markViewed(appId);
            case "INTERVIEW" -> applicationMapper.markInterview(appId);
            case "OFFER" -> applicationMapper.markOffer(appId);
            case "REJECTED" -> applicationMapper.markRejected(appId);
            default -> throw BizException.badRequest("未知状态");
        }
    }

    /** 会话参与方校验：牛人本人 or 该职位所属企业的HR（留言接口共用） */
    public Application findAsParticipant(Long appId, Long userId) {
        Application app = applicationMapper.findById(appId);
        if (app == null) throw BizException.notFound("会话不存在");
        if (app.getUserId().equals(userId)) return app;
        Job job = jobMapper.findById(app.getJobId());
        Company company = job == null ? null : companyMapper.findById(job.getCompanyId());
        if (company != null && company.getHrUserId().equals(userId)) return app;
        throw BizException.forbidden("只有会话双方能查看");
    }

    /** HR查看投递者的完整简历（A类缺口补齐：HR核心动作"查简历"） */
    public ResumeDetailVO resumeOfApplication(Long hrUserId, Long appId) {
        Application app = findAsParticipant(appId, hrUserId); // 复用归属校验
        Resume resume = resumeMapper.findByUserId(app.getUserId());
        if (resume == null) throw BizException.notFound("该牛人尚未填写简历");
        ResumeDetailVO vo = new ResumeDetailVO();
        vo.setId(resume.getId());
        vo.setName(resume.getName());
        vo.setPhoto(resume.getPhoto());
        vo.setExpectCategory(resume.getExpectCategory());
        vo.setExpectCity(resume.getExpectCity());
        vo.setExpectSalaryMin(resume.getExpectSalaryMin());
        vo.setExpectSalaryMax(resume.getExpectSalaryMax());
        vo.setIntro(resume.getIntro());
        vo.setPublished(resume.getPublished());
        vo.setExperiences(expMapper.listByResumeId(resume.getId()));
        return vo;
    }
}
