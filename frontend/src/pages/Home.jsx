import React from 'react';
import Header from '../components/layout/Header';
import Button from '../components/common/Button';

/**
 * 홈 페이지 컴포넌트
 */
function Home() {
  return (
    <div className="min-h-screen bg-gray-50">
      <Header />

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        {/* 히어로 섹션 */}
        <div className="text-center py-16">
          <h1 className="text-4xl font-extrabold text-gray-900 sm:text-5xl sm:tracking-tight lg:text-6xl">
            환영합니다!
          </h1>
          <p className="mt-5 max-w-xl mx-auto text-xl text-primary-700">
            새로운 React 프로젝트를 시작합니다. 이 페이지는 홈 페이지의 예시입니다.
          </p>
          <div className="mt-8 flex justify-center">
            <Button size="lg">시작하기</Button>
            <Button variant="outline" size="lg" className="ml-4">
              더 알아보기
            </Button>
          </div>
        </div>

        {/* 특징 섹션 */}
        <div className="mt-24">
          <h2 className="text-2xl font-bold text-gray-900 text-center">주요 기능</h2>
          <div className="mt-10 grid grid-cols-1 gap-10 sm:grid-cols-2 lg:grid-cols-3">
            {/* 특징 카드 1 */}
            <div className="bg-white overflow-hidden shadow rounded-lg">
              <div className="px-4 py-5 sm:p-6">
                <h3 className="text-lg font-medium text-gray-900">기능 1</h3>
                <p className="mt-2 text-base text-primary-700">이 기능은 사용자에게 다양한 이점을 제공합니다.</p>
              </div>
            </div>

            {/* 특징 카드 2 */}
            <div className="bg-white overflow-hidden shadow rounded-lg">
              <div className="px-4 py-5 sm:p-6">
                <h3 className="text-lg font-medium text-gray-900">기능 2</h3>
                <p className="mt-2 text-base text-primary-700">이 기능은 작업 효율성을 크게 향상시킵니다.</p>
              </div>
            </div>

            {/* 특징 카드 3 */}
            <div className="bg-white overflow-hidden shadow rounded-lg">
              <div className="px-4 py-5 sm:p-6">
                <h3 className="text-lg font-medium text-gray-900">기능 3</h3>
                <p className="mt-2 text-base text-primary-700">이 기능은 놀라운 사용자 경험을 제공합니다.</p>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}

export default Home;
