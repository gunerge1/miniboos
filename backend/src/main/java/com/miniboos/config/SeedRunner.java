package com.miniboos.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 种子数据：admin账号 + 4类字典初始值（技术设计文档3.3初始化数据）
 * 幂等设计：查空才插，重启不重复
 * admin初始密码来自环境变量ADMIN_INIT_PASSWORD（.env/Render），不入代码
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SeedRunner implements ApplicationRunner {

    private final JdbcTemplate jdbc;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public void run(ApplicationArguments args) {
        seedAdmin();
        seedDicts();
    }

    private void seedAdmin() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE role='ADMIN'", Integer.class);
        if (count != null && count > 0) {
            return;
        }
        String pwd = System.getenv("ADMIN_INIT_PASSWORD");
        if (pwd == null || pwd.isBlank()) {
            pwd = "miniboos-admin-2026"; // 兜底默认，仅本机dev；生产必须配环境变量
            log.warn("ADMIN_INIT_PASSWORD未配置，使用默认初始密码（仅限本机dev！）");
        }
        jdbc.update("INSERT INTO users(phone, password_hash, role, nickname) VALUES(?,?,?,?)",
                "10000000000", encoder.encode(pwd), "ADMIN", "平台管理员");
        log.info("种子：ADMIN账号已创建");
    }

    private void seedDicts() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM dict_items", Integer.class);
        if (count != null && count > 0) {
            return;
        }
        List<Object[]> rows = List.of(
                // category 岗位方向
                row("category", "backend", "后端开发", 1), row("category", "frontend", "前端开发", 2),
                row("category", "mobile", "移动开发", 3), row("category", "qa", "测试", 4),
                row("category", "pm", "产品经理", 5), row("category", "ops", "运营", 6),
                row("category", "design", "设计", 7), row("category", "other", "其他", 8),
                // industry 行业
                row("industry", "internet", "互联网", 1), row("industry", "finance", "金融", 2),
                row("industry", "education", "教育", 3), row("industry", "manufacture", "制造", 4),
                row("industry", "medical", "医疗", 5), row("industry", "retail", "零售", 6),
                row("industry", "other_ind", "其他", 7),
                // city 开通城市
                row("city", "beijing", "北京", 1), row("city", "shanghai", "上海", 2),
                row("city", "shenzhen", "深圳", 3), row("city", "guangzhou", "广州", 4),
                row("city", "hangzhou", "杭州", 5), row("city", "chengdu", "成都", 6),
                row("city", "wuhan", "武汉", 7), row("city", "xian", "西安", 8),
                // education 学历（jobs.education引用，CEO拍板v1.1）
                row("education", "any", "学历不限", 1), row("education", "college", "大专", 2),
                row("education", "bachelor", "本科", 3), row("education", "master", "硕士", 4),
                row("education", "doctor", "博士", 5)
        );
        jdbc.batchUpdate("INSERT INTO dict_items(dict_type, code, label, sort) VALUES(?,?,?,?)", rows);
        log.info("种子：4类字典26条初始值已入库");
    }

    private Object[] row(String type, String code, String label, int sort) {
        return new Object[]{type, code, label, sort};
    }
}
