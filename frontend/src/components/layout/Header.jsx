import React from 'react';
import {Menu} from 'lucide-react';
import Button from '../common/Button';

/**
 * 애플리케이션 상단 헤더 컴포넌트
 */
function Header() {
  const [isMenuOpen, setIsMenuOpen] = React.useState(false);

  const toggleMenu = () => {
    setIsMenuOpen((prev) => !prev);
  };

  return (
    <header className="bg-white shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16 items-center">
          {/* 로고 */}
          <div className="flex-shrink-0 flex items-center">
            <h1 className="text-xl font-bold text-blue-600">애플리케이션 이름</h1>
          </div>

          {/* 데스크탑 메뉴 */}
          <div className="hidden md:block">
            <div className="ml-10 flex items-center space-x-4">
              <a href="#" className="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md font-medium">
                홈
              </a>
              <a href="#" className="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md font-medium">
                기능
              </a>
              <a href="#" className="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md font-medium">
                대시보드
              </a>
              <Button size="sm">로그인</Button>
            </div>
          </div>

          {/* 모바일 메뉴 버튼 */}
          <div className="md:hidden">
            <button className="text-gray-500 hover:text-gray-700 focus:outline-none" onClick={toggleMenu}>
              <Menu size={24} />
            </button>
          </div>
        </div>
      </div>

      {/* 모바일 메뉴 */}
      {isMenuOpen && (
        <div className="md:hidden">
          <div className="px-2 pt-2 pb-3 space-y-1 sm:px-3">
            <a href="#" className="block text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md font-medium">
              홈
            </a>
            <a href="#" className="block text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md font-medium">
              기능
            </a>
            <a href="#" className="block text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md font-medium">
              대시보드
            </a>
            <Button fullWidth>로그인</Button>
          </div>
        </div>
      )}
    </header>
  );
}

export default Header;
