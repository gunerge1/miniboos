#!/bin/bash
# miniboos dev启动脚本（Git Bash）：载入.env密钥 → 起后端
# 用法：./scripts/dev-up.sh
cd "$(dirname "$0")/../backend" || exit 1
set -a; source ../.env; set +a
export SPRING_PROFILES_ACTIVE=dev
echo "后端启动中：http://localhost:8080 （Ctrl+C停止）"
mvn spring-boot:run
