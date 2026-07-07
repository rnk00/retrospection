<template>
  <div class="calendar-page">
    <!-- 헤더 -->
    <header class="header">
      <h1 class="logo">DOT</h1>
      <div class="header-right">
        <span class="user-name">{{ authStore.user?.nickname }}</span>
        <button class="btn-logout" @click="logout">로그아웃</button>
      </div>
    </header>

    <!-- 캘린더 -->
    <main class="main">
      <!-- 월 네비게이션 -->
      <div class="month-nav">
        <button @click="prevMonth">‹</button>
        <h2>{{ currentYear }}년 {{ currentMonth }}월</h2>
        <button @click="nextMonth">›</button>
      </div>

      <!-- 색상 테마 선택 -->
      <div class="theme-selector">
        <span class="theme-label">색상 테마</span>
        <button
          v-for="theme in colorThemes"
          :key="theme.value"
          class="theme-btn"
          :class="{ active: selectedTheme === theme.value }"
          :style="{ background: theme.preview }"
          @click="selectedTheme = theme.value"
          :title="theme.label"
        />
      </div>

      <!-- 캘린더 그리드 -->
      <div class="calendar-grid">
        <!-- 요일 헤더 -->
        <div v-for="day in weekDays" :key="day" class="weekday-header">{{ day }}</div>

        <!-- 빈 칸 -->
        <div v-for="n in firstDayOffset" :key="`empty-${n}`" class="day-cell empty" />

        <!-- 날짜 -->
        <div
          v-for="day in daysInMonth"
          :key="day"
          class="day-cell"
          :class="{ today: isToday(day), 'has-retro': getDayData(day) }"
          :style="getDayCellStyle(day)"
          @click="goToDate(day)"
        >
          <span class="day-num">{{ day }}</span>
          <span v-if="getDayData(day)" class="day-score">{{ getDayData(day).score }}</span>
        </div>
      </div>

      <!-- 점수 범례 -->
      <div class="legend">
        <span class="legend-label">점수</span>
        <div class="legend-items">
          <div
            v-for="n in 10"
            :key="n"
            class="legend-item"
            :style="{ background: getScoreColor(n, selectedTheme) }"
          >{{ n }}</div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { retrospectApi } from '@/api/retrospect'
import { getScoreColor, colorThemes } from '@/utils/scoreColor'

const router = useRouter()
const authStore = useAuthStore()

const today = new Date()
const currentYear = ref(today.getFullYear())
const currentMonth = ref(today.getMonth() + 1)
const selectedTheme = ref('default')
const calendarData = ref([])

const weekDays = ['일', '월', '화', '수', '목', '금', '토']

const daysInMonth = computed(() => {
  return new Date(currentYear.value, currentMonth.value, 0).getDate()
})

const firstDayOffset = computed(() => {
  return new Date(currentYear.value, currentMonth.value - 1, 1).getDay()
})

const dayMap = computed(() => {
  const map = {}
  calendarData.value.forEach(item => {
    const day = new Date(item.date).getDate()
    map[day] = item
  })
  return map
})

function getDayData(day) {
  return dayMap.value[day] || null
}

function getDayCellStyle(day) {
  const data = getDayData(day)
  if (!data) return {}
  return {
    background: getScoreColor(data.score, selectedTheme.value),
    color: data.score >= 7 ? '#fff' : '#111'
  }
}

function isToday(day) {
  return (
    day === today.getDate() &&
    currentMonth.value === today.getMonth() + 1 &&
    currentYear.value === today.getFullYear()
  )
}

function goToDate(day) {
  const dateStr = `${currentYear.value}-${String(currentMonth.value).padStart(2, '0')}-${String(day).padStart(2, '0')}`
  router.push(`/retrospect/${dateStr}`)
}

function prevMonth() {
  if (currentMonth.value === 1) {
    currentMonth.value = 12
    currentYear.value--
  } else {
    currentMonth.value--
  }
}

function nextMonth() {
  if (currentMonth.value === 12) {
    currentMonth.value = 1
    currentYear.value++
  } else {
    currentMonth.value++
  }
}

function logout() {
  authStore.logout()
  router.push('/login')
}

async function loadCalendar() {
  try {
    calendarData.value = await retrospectApi.getCalendar(currentYear.value, currentMonth.value)
  } catch (e) {
    console.error('캘린더 로딩 실패', e)
  }
}

watch([currentYear, currentMonth], loadCalendar)
onMounted(loadCalendar)
</script>

<style scoped>
.calendar-page {
  min-height: 100vh;
  background: var(--color-bg);
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: white;
  border-bottom: 1px solid var(--color-border);
}

.logo {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--color-primary);
  letter-spacing: 3px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-name {
  font-weight: 600;
  color: var(--color-text);
}

.btn-logout {
  background: none;
  color: var(--color-text-secondary);
  font-size: 0.875rem;
  padding: 6px 12px;
  border-radius: 6px;
  border: 1px solid var(--color-border);
}

.btn-logout:hover {
  background: var(--color-bg);
}

.main {
  max-width: 720px;
  margin: 0 auto;
  padding: 24px 16px;
}

.month-nav {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  margin-bottom: 20px;
}

.month-nav h2 {
  font-size: 1.25rem;
  font-weight: 700;
}

.month-nav button {
  background: none;
  font-size: 1.5rem;
  color: var(--color-primary);
  padding: 4px 12px;
  border-radius: 6px;
  border: 1px solid var(--color-border);
}

.theme-selector {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.theme-label {
  font-size: 0.875rem;
  color: var(--color-text-secondary);
}

.theme-btn {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: 2px solid transparent;
  transition: border-color 0.2s, transform 0.1s;
}

.theme-btn.active {
  border-color: var(--color-primary);
  transform: scale(1.2);
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.weekday-header {
  text-align: center;
  font-size: 0.8rem;
  font-weight: 600;
  color: var(--color-text-secondary);
  padding: 8px 0;
}

.day-cell {
  aspect-ratio: 1;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  background: white;
  border: 1px solid var(--color-border);
  transition: transform 0.1s, box-shadow 0.1s;
  position: relative;
}

.day-cell:not(.empty):hover {
  transform: scale(1.04);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.day-cell.empty {
  background: transparent;
  border-color: transparent;
  cursor: default;
}

.day-cell.today .day-num {
  color: var(--color-primary);
  font-weight: 800;
}

.day-num {
  font-size: 0.875rem;
  font-weight: 500;
}

.day-score {
  font-size: 0.7rem;
  font-weight: 700;
  margin-top: 2px;
}

.legend {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
}

.legend-label {
  font-size: 0.8rem;
  color: var(--color-text-secondary);
}

.legend-items {
  display: flex;
  gap: 4px;
}

.legend-item {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.7rem;
  font-weight: 700;
  color: #333;
}
</style>
