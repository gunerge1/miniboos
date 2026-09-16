#!/bin/bash
# miniboos 接口冒烟测试 v2
# v1教训：Git Bash的Windows curl把命令行中文按GBK发送→后端UTF-8解析炸（Invalid UTF-8 start byte）
# v2对策：所有payload写入UTF-8临时文件，curl用 -d @file 发送（与真实浏览器行为一致）
cd "$(dirname "$0")/.." || exit 1
set -a; source .env; set +a
BASE=http://localhost:8080/api
P=/tmp/miniboos-payload.json
PASS=0; FAIL=0

# 封装：方法 URL [JSON] [AUTH_HEADER]
req() {
  local method=$1 url=$2 json=$3 auth=$4
  local args=(-s -X "$method" "$url" -H 'Content-Type: application/json')
  [ -n "$auth" ] && args+=(-H "$auth")
  if [ -n "$json" ]; then printf '%s' "$json" > "$P"; args+=(-d @"$P"); fi
  curl "${args[@]}"
}

# 提取JSON里第一个数值（字段名是$1；-a治中文流二进制误判，+治空匹配吐空行）
first_num() { grep -ao "\"$1\":[0-9]*" | head -1 | grep -oE '[0-9]+'; }

check() {
  if echo "$3" | grep -q "$2"; then PASS=$((PASS+1)); echo "✅ $1"
  else FAIL=$((FAIL+1)); echo "❌ $1 | 期望含[$2] 实际: $(echo "$3" | head -c 300)"; fi
}

echo "===== 1.健康检查 ====="
R=$(curl -s $BASE/health); check "health UP" '"status":"UP"' "$R"

echo "===== 2.admin登录 ====="
R=$(req POST $BASE/auth/login "{\"phone\":\"10000000000\",\"password\":\"$ADMIN_INIT_PASSWORD\"}")
check "admin登录" '"role":"ADMIN"' "$R"
ADMIN_TOKEN=$(echo "$R" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')
AH="Authorization: Bearer $ADMIN_TOKEN"

echo "===== 3.HR注册+登录 ====="
R=$(req POST $BASE/auth/register '{"phone":"13900000001","password":"hr123456","role":"HR","nickname":"HR小王"}')
check "HR注册" '"code":0' "$R"
R=$(req POST $BASE/auth/login '{"phone":"13900000001","password":"hr123456"}')
check "HR登录" '"role":"HR"' "$R"
HR_TOKEN=$(echo "$R" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')
HH="Authorization: Bearer $HR_TOKEN"

echo "===== 4.HR提交企业认证 ====="
R=$(req POST $BASE/companies '{"name":"棍儿哥科技有限公司","industry":"internet","licenseNo":"91110108MA01TEST"}' "$HH")
check "企业提交" '"code":0' "$R"

echo "===== 5.admin审核企业通过 ====="
R=$(curl -s "$BASE/admin/companies?status=PENDING" -H "$AH")
CID=$(echo "$R" | first_num id)
R=$(req PUT $BASE/admin/companies/$CID/audit '{"result":"APPROVED"}' "$AH")
check "企业过审(companyId=$CID)" '"code":0' "$R"

echo "===== 6.HR发布职位 ====="
R=$(req POST $BASE/jobs '{"title":"Java后端工程师","category":"backend","city":"beijing","education":"bachelor","salaryMin":15,"salaryMax":25,"description":"负责miniboos后端研发，Spring Boot+MySQL"}' "$HH")
check "职位发布(PENDING待审)" '"code":0' "$R"

echo "===== 7.admin审核职位通过 ====="
R=$(curl -s "$BASE/admin/jobs?status=PENDING" -H "$AH")
JID=$(echo "$R" | first_num id)
R=$(req PUT $BASE/admin/jobs/$JID/audit '{"result":"APPROVED"}' "$AH")
check "职位过审(jobId=$JID)" '"code":0' "$R"

echo "===== 8.牛人注册+登录 ====="
R=$(req POST $BASE/auth/register '{"phone":"13900000002","password":"cd123456","role":"CANDIDATE","nickname":"牛人小李"}')
check "牛人注册" '"code":0' "$R"
R=$(req POST $BASE/auth/login '{"phone":"13900000002","password":"cd123456"}')
check "牛人登录" '"role":"CANDIDATE"' "$R"
CD_TOKEN=$(echo "$R" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')
CH="Authorization: Bearer $CD_TOKEN"

echo "===== 9.牛人填简历并发布 ====="
R=$(req PUT $BASE/my/resume '{"name":"李牛人","expectCategory":"backend","expectCity":"beijing","expectSalaryMin":20,"expectSalaryMax":30,"intro":"三年Java经验","published":1}' "$CH")
check "简历保存" '"code":0' "$R"
R=$(req POST $BASE/my/resume/experiences '{"projectName":"电商系统重构","startDate":"2024-01","endDate":"2025-06","description":"主导订单服务重构，QPS提升3倍"}' "$CH")
check "项目经历" '"code":0' "$R"

echo "===== 10.牛人职位列表（意向匹配排序） ====="
R=$(curl -s "$BASE/jobs" -H "$CH")
check "列表含Java职位" 'Java后端工程师' "$R"
check "意向打分字段" 'matchScore' "$R"

echo "===== 11.投递+防重复 ====="
R=$(req POST $BASE/applications "{\"jobId\":$JID}" "$CH")
check "投递成功" '"code":0' "$R"
R=$(req POST $BASE/applications "{\"jobId\":$JID}" "$CH")
check "重复投被拦" '不能重复投' "$R"

echo "===== 12.HR看投递列表 ====="
R=$(curl -s $BASE/my/jobs/$JID/applications -H "$HH")
check "HR见牛人" '李牛人' "$R"
check "匹配打分" 'matchScore' "$R"

echo "===== 13.状态机流转 ====="
AID=$(curl -s $BASE/my/jobs/$JID/applications -H "$HH" | first_num applicationId)
R=$(req PUT $BASE/applications/$AID/status '{"status":"VIEWED"}' "$HH")
check "→VIEWED" '"code":0' "$R"
R=$(req PUT $BASE/applications/$AID/status '{"status":"INTERVIEW"}' "$HH")
check "→INTERVIEW" '"code":0' "$R"
R=$(req PUT $BASE/applications/$AID/status '{"status":"VIEWED"}' "$HH")
check "非法回退被拦" '不允许' "$R"

echo "===== 14.留言（会话双方） ====="
R=$(req POST $BASE/applications/$AID/messages '{"content":"您好，我对这个职位很感兴趣"}' "$CH")
check "牛人发言" '"code":0' "$R"
R=$(req POST $BASE/applications/$AID/messages '{"content":"你好，方便周四下午来面试吗？"}' "$HH")
check "HR回复" '"code":0' "$R"
R=$(curl -s $BASE/applications/$AID/messages -H "$CH")
check "会话可见双方发言" '周四下午' "$R"

echo "===== 15.越权测试 ====="
R=$(curl -s $BASE/admin/stats -H "$CH")
check "牛人进admin被拦(403)" '角色无权访问' "$R"

echo "===== 16.admin看板+用户脱敏 ====="
R=$(curl -s $BASE/admin/stats -H "$AH")
check "看板统计" 'applicationCount' "$R"
R=$(curl -s "$BASE/admin/users?role=CANDIDATE" -H "$AH")
check "手机号脱敏" '\*\*\*\*' "$R"

echo ""
echo "======================================"
echo "冒烟测试结果：通过 $PASS 项 / 失败 $FAIL 项"
echo "======================================"
[ $FAIL -eq 0 ] && echo "🏆 全链路绿灯" || echo "⚠️ 有红灯，看上方❌行"
