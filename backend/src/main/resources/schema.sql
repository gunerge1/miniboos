-- miniboos 建表脚本（技术设计文档3.2数据字典·8张表）
-- 幂等：IF NOT EXISTS，重启不炸库

CREATE TABLE IF NOT EXISTS users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone         VARCHAR(20)  NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    role          VARCHAR(20)  NOT NULL COMMENT 'CANDIDATE/HR/ADMIN（系统词汇，永不入字典）',
    nickname      VARCHAR(50),
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1正常/0禁用',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS companies (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    hr_user_id    BIGINT       NOT NULL,
    name          VARCHAR(100) NOT NULL,
    industry      VARCHAR(30)  NOT NULL COMMENT '行业（字典industry的code）',
    license_no    VARCHAR(50),
    status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED（状态机不入字典）',
    reject_reason VARCHAR(200),
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_hr (hr_user_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS jobs (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id  BIGINT       NOT NULL,
    title       VARCHAR(100) NOT NULL,
    category    VARCHAR(30)  NOT NULL COMMENT '岗位方向（字典category的code）',
    city        VARCHAR(30)  NOT NULL COMMENT '字典city的code',
    education   VARCHAR(20)  COMMENT '学历要求（字典education的code，v1.1）',
    salary_min  INT,
    salary_max  INT COMMENT 'K/月',
    description TEXT,
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/ACTIVE/OFF/REJECTED（状态机）',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_list (category, city, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS resumes (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id            BIGINT      NOT NULL,
    name               VARCHAR(50),
    photo              TEXT COMMENT 'Base64（前端压缩≤200KB，仅详情拉取；真实形态=对象存储+URL，触发日迁移）',
    expect_category    VARCHAR(30) COMMENT '期望方向（字典category的code，意向匹配原料）',
    expect_city        VARCHAR(30),
    expect_salary_min  INT,
    expect_salary_max  INT,
    intro              TEXT,
    published          TINYINT     NOT NULL DEFAULT 0 COMMENT '1已发布（投递门槛）',
    created_at         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS resume_experiences (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    resume_id    BIGINT       NOT NULL,
    project_name VARCHAR(100) NOT NULL,
    start_date   VARCHAR(10) COMMENT 'YYYY-MM',
    end_date     VARCHAR(10) COMMENT '空=至今',
    description  TEXT,
    sort         INT          NOT NULL DEFAULT 0,
    KEY idx_resume (resume_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS applications (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id       BIGINT      NOT NULL,
    user_id      BIGINT      NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED' COMMENT 'SUBMITTED/VIEWED/INTERVIEW/OFFER/REJECTED（状态机）',
    viewed_at    DATETIME COMMENT '以下时间戳=终局数据分析钩子（PRD：投递到offer周期）',
    interview_at DATETIME,
    offer_at     DATETIME,
    rejected_at  DATETIME,
    created_at   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_no_repeat (job_id, user_id) COMMENT '同一职位不可重复投（PRD验收要点）',
    KEY idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS messages (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id  BIGINT        NOT NULL COMMENT '一次投递=一个会话（PRD留言模型）',
    sender_id       BIGINT        NOT NULL,
    content         VARCHAR(1000) NOT NULL,
    is_read         TINYINT       NOT NULL DEFAULT 0 COMMENT '接收方已读标记',
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_conversation (application_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS dict_items (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    dict_type  VARCHAR(30) NOT NULL COMMENT 'category/industry/city/education（运营词汇）',
    code       VARCHAR(50) NOT NULL COMMENT '存储值（稳定不改）',
    label      VARCHAR(50) NOT NULL COMMENT '显示名（运营可改，存量无感）',
    sort       INT         NOT NULL DEFAULT 0,
    status     TINYINT     NOT NULL DEFAULT 1 COMMENT '1启用/0停用',
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_type_code (dict_type, code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
