package com.miniboos.service;

import com.miniboos.common.BizException;
import com.miniboos.dto.AdminUserVO;
import com.miniboos.dto.PageVO;
import com.miniboos.dto.StatsVO;
import com.miniboos.dto.req.AdminReq;
import com.miniboos.entity.Company;
import com.miniboos.entity.Job;
import com.miniboos.entity.User;
import com.miniboos.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CompanyMapper companyMapper;
    private final JobMapper jobMapper;
    private final UserMapper userMapper;
    private final ApplicationMapper applicationMapper;
    private final ResumeMapper resumeMapper;
    private final JdbcTemplate jdbc;

    // ---------- 企业审核 ----------
    public PageVO<Company> companies(String status, int page, int size) {
        int offset = (page - 1) * size;
        return PageVO.of(companyMapper.listByStatus(status, offset, size),
                companyMapper.countByStatus(status), page, size);
    }

    public void auditCompany(Long id, AdminReq.Audit req) {
        Company company = companyMapper.findById(id);
        if (company == null) throw BizException.notFound("企业不存在");
        if ("REJECTED".equals(req.getResult()) && (req.getReason() == null || req.getReason().isBlank())) {
            throw BizException.badRequest("驳回必须填写理由");
        }
        companyMapper.updateAudit(id, req.getResult(),
                "REJECTED".equals(req.getResult()) ? req.getReason() : null);
    }

    // ---------- 职位审核 ----------
    public PageVO<Job> jobs(String status, int page, int size) {
        int offset = (page - 1) * size;
        return PageVO.of(jobMapper.listByStatus(status, offset, size),
                jobMapper.countByStatus(status), page, size);
    }

    public void auditJob(Long id, AdminReq.Audit req) {
        Job job = jobMapper.findById(id);
        if (job == null) throw BizException.notFound("职位不存在");
        if ("REJECTED".equals(req.getResult()) && (req.getReason() == null || req.getReason().isBlank())) {
            throw BizException.badRequest("驳回必须填写理由");
        }
        // 审核通过=ACTIVE（上架），驳回=REJECTED
        jobMapper.updateStatus(id, "APPROVED".equals(req.getResult()) ? "ACTIVE" : "REJECTED");
    }

    // ---------- 用户管理（JdbcTemplate参数化?占位符，杜绝拼接） ----------
    public PageVO<AdminUserVO> users(String role, String kw, int page, int size) {
        int offset = (page - 1) * size;
        StringBuilder where = new StringBuilder("WHERE role != 'ADMIN' ");
        Object[] args = new Object[4];
        int argCount = 0;
        if (role != null && !role.isBlank()) {
            where.append("AND role=? ");
            args[argCount++] = role;
        }
        if (kw != null && !kw.isBlank()) {
            where.append("AND (nickname LIKE ? OR phone LIKE ?) ");
            String like = "%" + kw + "%";
            args[argCount++] = like;
            args[argCount++] = like;
        }
        List<AdminUserVO> list = jdbc.query(
                "SELECT id, phone, role, nickname, status, created_at FROM users " + where
                        + "ORDER BY created_at DESC LIMIT " + offset + ", " + size,
                ps -> {
                    for (int i = 0; i < argCount; i++) {
                        ps.setObject(i + 1, args[i]);
                    }
                },
                (rs, i) -> {
                    AdminUserVO vo = new AdminUserVO();
                    vo.setId(rs.getLong("id"));
                    String phone = rs.getString("phone");
                    vo.setPhoneMasked(phone != null && phone.length() == 11
                            ? phone.substring(0, 3) + "****" + phone.substring(7) : phone);
                    vo.setRole(rs.getString("role"));
                    vo.setNickname(rs.getString("nickname"));
                    vo.setStatus(rs.getInt("status"));
                    vo.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return vo;
                });
        Long total = jdbc.queryForObject("SELECT COUNT(*) FROM users " + where, Long.class, trimArgs(args, argCount));
        return PageVO.of(list, total == null ? 0 : total, page, size);
    }

    private Object[] trimArgs(Object[] args, int count) {
        Object[] result = new Object[count];
        System.arraycopy(args, 0, result, 0, count);
        return result;
    }

    public void updateUserStatus(Long id, int status) {
        User user = userMapper.findById(id);
        if (user == null) throw BizException.notFound("用户不存在");
        if ("ADMIN".equals(user.getRole())) throw BizException.forbidden("不能对管理员执行此操作");
        userMapper.updateStatus(id, status);
    }

    // ---------- 数据看板 ----------
    public StatsVO stats() {
        StatsVO vo = new StatsVO();
        vo.setCompanyCount(companyMapper.countByStatus(null));
        vo.setJobCount(jobMapper.countAll());
        vo.setApplicationCount(applicationMapper.countAll());
        vo.setResumeCount(resumeMapper.countPublished());
        vo.setCandidateCount(userMapper.countCandidates());
        return vo;
    }
}
