// 색상 테마별 점수 색상 정의
const themes = {
  default: [
    '', // 0번 인덱스 사용 안 함
    '#fee2e2', '#fed7aa', '#fef9c3', '#d9f99d', '#bbf7d0',
    '#6ee7b7', '#34d399', '#10b981', '#059669', '#047857'
  ],
  ocean: [
    '',
    '#e0f2fe', '#bae6fd', '#7dd3fc', '#38bdf8', '#0ea5e9',
    '#0284c7', '#0369a1', '#075985', '#0c4a6e', '#082f49'
  ],
  sunset: [
    '',
    '#fff1f2', '#ffe4e6', '#fecdd3', '#fda4af', '#fb7185',
    '#f43f5e', '#e11d48', '#be123c', '#9f1239', '#881337'
  ],
  purple: [
    '',
    '#f5f3ff', '#ede9fe', '#ddd6fe', '#c4b5fd', '#a78bfa',
    '#8b5cf6', '#7c3aed', '#6d28d9', '#5b21b6', '#4c1d95'
  ]
}

export const colorThemes = [
  { value: 'default', label: '기본 (초록)', preview: '#10b981' },
  { value: 'ocean', label: '오션 (파랑)', preview: '#0ea5e9' },
  { value: 'sunset', label: '선셋 (빨강)', preview: '#f43f5e' },
  { value: 'purple', label: '퍼플', preview: '#8b5cf6' }
]

export function getScoreColor(score, theme = 'default') {
  const palette = themes[theme] || themes.default
  const idx = Math.min(Math.max(score, 1), 10)
  return palette[idx]
}
