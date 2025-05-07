import typography from '@tailwindcss/typography';

/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/*.{js,ts,jsx,tsx}', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    screens: {
      mobile: {max: '640px'},
      tablet: {min: '641px', max: '1279px'},
      desktop: {min: '1280px'},
    },
    fontFamily: {
      notoKR: 'Noto Sans KR',
      pretendard: 'Pretendard',
      SFMono: 'SF Mono',
    },
    extend: {
      colors: {
        primary: {
          100: '#c2c5d0',
          200: '#868ca1',
          300: '#686f89',
          400: '#4a5272',
          500: '#2C365B',
          600: '#222946',
          700: '#1f2641',
          800: '#181d32',
        },
        secondary: '#707A9F',
        'black-navy': '#1A2036',
        gray: {
          100: '#eeeeee',
          200: '#f5f5f5',
          250: '#f3f3f3',
          300: '#e3e3e3',
          400: '#fafafb',
          500: '#ebecef',
          600: '#cccccc',
          700: '#919191',
          800: '#a5a5a5',
          900: '#515151',
        },
        background: {
          blue: '#F5F7FA',
          navy: '#EFF2FB',
          bnavy: '#D1D5E8',
        },
        state: {
          warning: '#EF4545',
          like: '#FF6024',
          success: '#00C49F',
        },
      },
    },
  },
  plugins: [typography],
};
