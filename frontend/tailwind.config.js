/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        clinical: {
          surface: '#fcf8fa',
          'surface-dim': '#dcd9db',
          'surface-bright': '#fcf8fa',
          'container-lowest': '#ffffff',
          'container-low': '#f6f3f5',
          container: '#f0edef',
          'container-high': '#eae7e9',
          'container-highest': '#e4e2e4',
          'on-surface': '#1b1b1d',
          'on-surface-variant': '#45464d',
          outline: '#76777d',
          'outline-variant': '#c6c6cd',
          navy: '#0f172a',
          'navy-dark': '#131b2e',
          slate: '#515f74',
          'slate-light': '#d5e3fd',
          border: '#e2e8f0',
        },
        status: {
          critical: '#ef4444',
          'critical-bg': '#fef2f2',
          'critical-border': '#fecaca',
          warning: '#f59e0b',
          'warning-bg': '#fffbeb',
          'warning-border': '#fde68a',
          ready: '#10b981',
          'ready-bg': '#ecfdf5',
          'ready-border': '#a7f3d0',
          info: '#0ea5e9',
          'info-bg': '#f0f9ff',
          'info-border': '#bae6fd',
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'sans-serif'],
      },
      boxShadow: {
        clinical: '0px 4px 6px -1px rgba(15, 23, 42, 0.08)',
        modal: '0px 10px 15px -3px rgba(15, 23, 42, 0.12)',
      },
      borderRadius: {
        sm: '0.125rem',
        DEFAULT: '0.25rem',
        md: '0.375rem',
        lg: '0.5rem',
        xl: '0.75rem',
      }
    },
  },
  plugins: [],
}
