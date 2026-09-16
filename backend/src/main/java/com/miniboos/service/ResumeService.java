package com.miniboos.service;

import com.miniboos.common.BizException;
import com.miniboos.dto.ResumeDetailVO;
import com.miniboos.dto.req.BizReq;
import com.miniboos.entity.Resume;
import com.miniboos.entity.ResumeExperience;
import com.miniboos.mapper.ResumeExperienceMapper;
import com.miniboos.mapper.ResumeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {

    /** Base64后27万字符≈200KB原图（技术设计文档：照片≤200KB） */
    private static final int PHOTO_MAX_BASE64_LEN = 280_000;

    private final ResumeMapper resumeMapper;
    private final ResumeExperienceMapper expMapper;
    private final DictService dictService;

    public ResumeDetailVO my(Long userId) {
        Resume resume = resumeMapper.findByUserId(userId);
        if (resume == null) return null;
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

    @Transactional
    public void save(Long userId, BizReq.Resume req) {
        if (req.getPhoto() != null && req.getPhoto().length() > PHOTO_MAX_BASE64_LEN) {
            throw BizException.badRequest("照片过大，请重新选择（≤200KB）");
        }
        dictService.checkCodeUsable("category", req.getExpectCategory());
        dictService.checkCodeUsable("city", req.getExpectCity());
        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setName(req.getName());
        resume.setPhoto(req.getPhoto());
        resume.setExpectCategory(req.getExpectCategory());
        resume.setExpectCity(req.getExpectCity());
        resume.setExpectSalaryMin(req.getExpectSalaryMin());
        resume.setExpectSalaryMax(req.getExpectSalaryMax());
        resume.setIntro(req.getIntro());
        resume.setPublished(req.getPublished() == null ? 0 : req.getPublished());
        if (resumeMapper.findByUserId(userId) == null) {
            resumeMapper.insert(resume);
        } else {
            resumeMapper.update(resume);
        }
    }

    public void addExperience(Long userId, BizReq.Experience req) {
        Resume resume = requireResume(userId);
        ResumeExperience exp = new ResumeExperience();
        copyExperience(exp, req);
        exp.setResumeId(resume.getId());
        expMapper.insert(exp);
    }

    public void updateExperience(Long userId, Long expId, BizReq.Experience req) {
        ResumeExperience exp = ownedExperience(userId, expId);
        copyExperience(exp, req);
        expMapper.update(exp);
    }

    public void deleteExperience(Long userId, Long expId) {
        ownedExperience(userId, expId);
        expMapper.deleteById(expId);
    }

    private void copyExperience(ResumeExperience exp, BizReq.Experience req) {
        exp.setProjectName(req.getProjectName());
        exp.setStartDate(req.getStartDate());
        exp.setEndDate(req.getEndDate());
        exp.setDescription(req.getDescription());
        exp.setSort(req.getSort() == null ? 0 : req.getSort());
    }

    private Resume requireResume(Long userId) {
        Resume resume = resumeMapper.findByUserId(userId);
        if (resume == null) {
            throw BizException.badRequest("请先填写简历基本信息");
        }
        return resume;
    }

    private ResumeExperience ownedExperience(Long userId, Long expId) {
        ResumeExperience exp = expMapper.findById(expId);
        if (exp == null) throw BizException.notFound("项目经历不存在");
        Resume resume = resumeMapper.findByUserId(userId);
        if (resume == null || !resume.getId().equals(exp.getResumeId())) {
            throw BizException.forbidden("只能操作自己简历的经历");
        }
        return exp;
    }
}
