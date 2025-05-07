import React from 'react';
import {createRoot} from 'react-dom/client';
import App from './App';

// 루트 요소 가져오기
const rootElement = document.getElementById('root');

// React 19의 createRoot API 사용
const root = createRoot(rootElement);

// 앱 렌더링
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
