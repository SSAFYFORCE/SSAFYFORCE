import React, {useEffect} from 'react';
import {useAtom} from 'jotai';
import Home from './pages/home';
import {fetchProfileAction} from './store/userStore';
import './styles/global.css';

function App() {
  // 사용자 프로필 자동 로드 액션
  const [, fetchProfile] = useAtom(fetchProfileAction);

  useEffect(() => {
    // 페이지 로드 시 저장된 인증 정보가 있으면 사용자 프로필 가져오기
    fetchProfile();
  }, [fetchProfile]);

  // 여기서는 라우팅을 구현하지 않고 Home 페이지만 렌더링합니다.
  // 실제 프로젝트에서는 React Router 등을 사용하여 라우팅 구현 필요
  return (
    <div className="App">
      <Home />
    </div>
  );
}

export default App;
