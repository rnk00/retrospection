<template>
  <div class="login-page">
    <div class="login-card">
      <h1 class="logo">DOT</h1>
      <p class="subtitle">나의 하루를 기록하는 회고 캘린더</p>

      <div class="login-buttons">
        <a href="/oauth2/authorization/kakao" class="btn btn-kakao">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 3C6.477 3 2 6.477 2 10.8c0 2.736 1.668 5.14 4.186 6.587L5.07 21l4.868-2.56c.667.093 1.35.143 2.062.143 5.523 0 10-3.477 10-7.783C22 6.477 17.523 3 12 3z"/>
          </svg>
          카카오로 로그인
        </a>

        <a href="/oauth2/authorization/github" class="btn btn-github">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.477 2 2 6.477 2 12c0 4.418 2.865 8.166 6.839 9.489.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12c0-5.523-4.477-10-10-10z"/>
          </svg>
          GitHub으로 로그인
        </a>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ede9fe 0%, #dbeafe 100%);
}

.login-card {
  background: white;
  border-radius: 20px;
  padding: 48px 40px;
  width: 100%;
  max-width: 380px;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0,0,0,0.1);
}

.logo {
  font-size: 3rem;
  font-weight: 800;
  color: var(--color-primary);
  letter-spacing: 4px;
  margin-bottom: 8px;
}

.subtitle {
  color: var(--color-text-secondary);
  font-size: 0.95rem;
  margin-bottom: 40px;
}

.login-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 14px;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 600;
  text-decoration: none;
  transition: opacity 0.2s, transform 0.1s;
}

.btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.btn-kakao {
  background: #FEE500;
  color: #3A1D1D;
}

.btn-github {
  background: #24292e;
  color: white;
}
</style>
