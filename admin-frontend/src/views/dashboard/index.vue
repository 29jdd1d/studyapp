<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon user-icon">
              <el-icon size="40"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.userCount }}</div>
              <div class="stat-label">总用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon resource-icon">
              <el-icon size="40"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.resourceCount }}</div>
              <div class="stat-label">学习资源</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon question-icon">
              <el-icon size="40"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.questionCount }}</div>
              <div class="stat-label">题库题目</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon post-icon">
              <el-icon size="40"><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.postCount }}</div>
              <div class="stat-label">社区帖子</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最近活动</span>
          </template>
          <el-empty v-if="!recentActivities.length" description="暂无数据" />
          <el-timeline v-else>
            <el-timeline-item
              v-for="activity in recentActivities"
              :key="activity.id"
              :timestamp="activity.time"
              placement="top"
            >
              {{ activity.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <span>系统信息</span>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="系统版本">1.0.0</el-descriptions-item>
            <el-descriptions-item label="后端框架">Spring Boot 2.7.14</el-descriptions-item>
            <el-descriptions-item label="前端框架">Vue 3 + Element Plus</el-descriptions-item>
            <el-descriptions-item label="数据库">MySQL 8.0</el-descriptions-item>
            <el-descriptions-item label="缓存">Redis</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Reading, Document, ChatDotRound } from '@element-plus/icons-vue'
import { getStatistics } from '../../api/admin'

const statistics = ref({
  userCount: 0,
  resourceCount: 0,
  questionCount: 0,
  postCount: 0
})

const recentActivities = ref([
  { id: 1, time: '2024-01-15 14:30', content: '新用户注册: user123' },
  { id: 2, time: '2024-01-15 13:20', content: '发布新资源: 高等数学第一章' },
  { id: 3, time: '2024-01-15 12:10', content: '新增题目: 线性代数练习题' },
  { id: 4, time: '2024-01-15 11:00', content: '社区新帖: 考研经验分享' }
])

const loadStatistics = async () => {
  try {
    const response = await getStatistics()
    if (response.data) {
      statistics.value = response.data
    }
  } catch (error) {
    console.error('Failed to load statistics:', error)
    // Use mock data for demonstration
    statistics.value = {
      userCount: 1234,
      resourceCount: 567,
      questionCount: 890,
      postCount: 234
    }
  }
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.user-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.resource-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.question-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.post-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}
</style>
