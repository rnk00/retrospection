<template>
  <div class="retro-page">
    <!-- 헤더 -->
    <header class="header">
      <button class="btn-back" @click="router.push('/calendar')">← 캘린더</button>
      <h2 class="date-title">{{ route.params.date }}</h2>
      <button v-if="retrospect?.id" class="btn-delete" @click="deleteRetro">삭제</button>
      <div v-else class="header-spacer" />
    </header>

    <main class="main">
      <!-- 점수 -->
      <div class="score-section">
        <label class="section-label">오늘의 점수 (1~10)</label>
        <div class="score-buttons">
          <button
            v-for="n in 10"
            :key="n"
            class="score-btn"
            :class="{ active: form.score === n }"
            :style="form.score === n ? { background: getScoreColor(n), color: n >= 7 ? '#fff' : '#111' } : {}"
            @click="form.score = n"
          >{{ n }}</button>
        </div>
      </div>

      <!-- KPT 폼 -->
      <div class="kpt-section">
        <div v-for="field in kptFields" :key="field.key" class="field-block">
          <div class="field-header">
            <label class="section-label">
              <span class="field-tag" :class="field.key">{{ field.tag }}</span>
              {{ field.label }}
            </label>
            <button class="btn-guide" @click="openGuide(field.key)">도움말</button>
          </div>
          <textarea
            v-model="form[field.key]"
            :placeholder="field.placeholder"
            rows="4"
          />
        </div>
      </div>

      <!-- AI 버튼 -->
      <div class="ai-section">
        <button class="btn-ai" @click="suggestTry" :disabled="aiLoading">
          {{ aiLoading ? 'AI 분석 중...' : '✨ AI로 Try 제안받기' }}
        </button>
        <div v-if="aiSuggestion" class="ai-result">
          <p class="ai-result-label">AI 제안</p>
          <p class="ai-result-text">{{ aiSuggestion }}</p>
          <button class="btn-apply" @click="applyAISuggestion">이 내용으로 Try에 적용</button>
        </div>
      </div>

      <!-- 저장 -->
      <button class="btn-save" @click="save" :disabled="saving">
        {{ saving ? '저장 중...' : (retrospect?.id ? '수정하기' : '저장하기') }}
      </button>
    </main>

    <!-- 가이드 모달 -->
    <div v-if="guideModal.show" class="modal-overlay" @click.self="guideModal.show = false">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ guideModal.title }} 작성 가이드</h3>
          <button @click="guideModal.show = false">✕</button>
        </div>
        <div class="modal-body">
          <p v-if="guideModal.loading" class="loading-text">가이드 불러오는 중...</p>
          <p v-else class="guide-text">{{ guideModal.content }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { retrospectApi } from '@/api/retrospect'
import { aiApi } from '@/api/ai'
import { getScoreColor } from '@/utils/scoreColor'

const router = useRouter()
const route = useRoute()

const retrospect = ref(null)
const saving = ref(false)
const aiLoading = ref(false)
const aiSuggestion = ref('')

const form = reactive({
  score: 5,
  keep: '',
  problem: '',
  tryContent: ''
})

const guideModal = reactive({
  show: false,
  title: '',
  content: '',
  loading: false
})

const kptFields = [
  {
    key: 'keep',
    tag: 'K',
    label: 'Keep — 잘한 점, 계속할 것',
    placeholder: '오늘 잘한 점이나 유지하고 싶은 것을 적어보세요.'
  },
  {
    key: 'problem',
    tag: 'P',
    label: 'Problem — 문제점, 개선할 점',
    placeholder: '아쉬웠던 점이나 개선이 필요한 것을 적어보세요.'
  },
  {
    key: 'tryContent',
    tag: 'T',
    label: 'Try — 다음에 시도할 것',
    placeholder: '다음에 시도해볼 구체적인 행동을 적어보세요.'
  }
]

async function loadRetro() {
  try {
    const data = await retrospectApi.getByDate(route.params.date)
    if (data) {
      retrospect.value = data
      form.score = data.score
      form.keep = data.keep || ''
      form.problem = data.problem || ''
      form.tryContent = data.tryContent || ''
    }
  } catch (e) {
    if (e.response?.status !== 404) console.error(e)
  }
}

async function save() {
  saving.value = true
  try {
    const payload = {
      date: route.params.date,
      keep: form.keep,
      problem: form.problem,
      tryContent: form.tryContent,
      score: form.score
    }
    if (retrospect.value?.id) {
      await retrospectApi.update(retrospect.value.id, payload)
    } else {
      await retrospectApi.create(payload)
    }
    router.push('/calendar')
  } catch (e) {
    alert('저장에 실패했습니다.')
  } finally {
    saving.value = false
  }
}

async function deleteRetro() {
  if (!confirm('회고를 삭제할까요?')) return
  await retrospectApi.delete(retrospect.value.id)
  router.push('/calendar')
}

async function suggestTry() {
  if (!form.keep && !form.problem) {
    alert('Keep 또는 Problem을 먼저 작성해주세요.')
    return
  }
  aiLoading.value = true
  aiSuggestion.value = ''
  try {
    const res = await aiApi.suggestTry(form.keep, form.problem)
    aiSuggestion.value = res.suggestion
  } catch {
    alert('AI 서비스 오류가 발생했습니다.')
  } finally {
    aiLoading.value = false
  }
}

function applyAISuggestion() {
  form.tryContent = aiSuggestion.value
  aiSuggestion.value = ''
}

async function openGuide(field) {
  const titles = { keep: 'Keep', problem: 'Problem', tryContent: 'Try' }
  guideModal.title = titles[field]
  guideModal.show = true
  guideModal.loading = true
  guideModal.content = ''
  try {
    const apiField = field === 'tryContent' ? 'try' : field
    const res = await aiApi.getGuide(apiField)
    guideModal.content = res.guide
  } catch {
    guideModal.content = '가이드를 불러오지 못했습니다.'
  } finally {
    guideModal.loading = false
  }
}

onMounted(loadRetro)
</script>

<style scoped>
.retro-page {
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

.btn-back {
  background: none;
  color: var(--color-primary);
  font-size: 0.95rem;
  font-weight: 600;
}

.date-title {
  font-size: 1.1rem;
  font-weight: 700;
}

.btn-delete {
  background: none;
  color: #ef4444;
  font-size: 0.875rem;
  padding: 6px 12px;
  border-radius: 6px;
  border: 1px solid #fecaca;
}

.header-spacer {
  width: 48px;
}

.main {
  max-width: 680px;
  margin: 0 auto;
  padding: 24px 16px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.section-label {
  font-weight: 700;
  font-size: 0.9rem;
  color: var(--color-text);
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.score-buttons {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.score-btn {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  border: 2px solid var(--color-border);
  background: white;
  font-weight: 700;
  font-size: 0.875rem;
  transition: all 0.15s;
}

.score-btn:hover {
  border-color: var(--color-primary);
}

.score-btn.active {
  border-color: transparent;
  transform: scale(1.1);
}

.kpt-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.field-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.field-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 800;
  color: white;
}

.field-tag.keep { background: #10b981; }
.field-tag.problem { background: #f59e0b; }
.field-tag.tryContent { background: #6366f1; }

.btn-guide {
  background: none;
  font-size: 0.8rem;
  color: var(--color-text-secondary);
  border: 1px solid var(--color-border);
  padding: 4px 10px;
  border-radius: 6px;
}

textarea {
  resize: vertical;
  min-height: 100px;
  line-height: 1.6;
  font-size: 0.95rem;
}

.ai-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.btn-ai {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: white;
  padding: 14px;
  border-radius: 10px;
  font-weight: 700;
  font-size: 0.95rem;
  transition: opacity 0.2s;
}

.btn-ai:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.ai-result {
  background: #f5f3ff;
  border: 1px solid #ddd6fe;
  border-radius: 10px;
  padding: 16px;
}

.ai-result-label {
  font-size: 0.8rem;
  font-weight: 700;
  color: #7c3aed;
  margin-bottom: 8px;
}

.ai-result-text {
  font-size: 0.9rem;
  line-height: 1.7;
  white-space: pre-wrap;
  color: var(--color-text);
}

.btn-apply {
  margin-top: 12px;
  background: #7c3aed;
  color: white;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 600;
}

.btn-save {
  background: var(--color-primary);
  color: white;
  padding: 16px;
  border-radius: 10px;
  font-weight: 700;
  font-size: 1rem;
  transition: opacity 0.2s;
}

.btn-save:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 모달 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  padding: 16px;
}

.modal {
  background: white;
  border-radius: 16px;
  width: 100%;
  max-width: 520px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid var(--color-border);
}

.modal-header h3 {
  font-size: 1rem;
  font-weight: 700;
}

.modal-header button {
  background: none;
  font-size: 1.2rem;
  color: var(--color-text-secondary);
}

.modal-body {
  padding: 20px 24px;
}

.guide-text {
  font-size: 0.9rem;
  line-height: 1.8;
  white-space: pre-wrap;
  color: var(--color-text);
}

.loading-text {
  color: var(--color-text-secondary);
  font-size: 0.9rem;
}
</style>
