import React, { useState } from 'react';
import { Sidebar } from './Sidebar';
import { Header } from './Header';
import { X } from 'lucide-react';

interface AppShellProps {
  children: React.ReactNode;
  title?: string;
  onRefreshData?: () => void;
  isRefreshing?: boolean;
}

export const AppShell: React.FC<AppShellProps> = ({
  children,
  title = 'Operations Dashboard',
  onRefreshData,
  isRefreshing = false,
}) => {
  const [mobileNavOpen, setMobileNavOpen] = useState(false);

  return (
    <div className="flex h-screen w-screen overflow-hidden bg-clinical-surface text-clinical-on-surface">
      {/* Desktop Sidebar (Fixed 280px) */}
      <div className="hidden h-full md:block shrink-0">
        <Sidebar />
      </div>

      {/* Mobile Sidebar Overlay / Drawer */}
      {mobileNavOpen && (
        <div className="fixed inset-0 z-50 flex md:hidden">
          {/* Backdrop */}
          <div
            className="fixed inset-0 bg-black/60 backdrop-blur-xs transition-opacity"
            onClick={() => setMobileNavOpen(false)}
          />
          {/* Drawer Content */}
          <div className="relative flex w-[280px] max-w-[80vw] flex-col bg-clinical-navy shadow-xl">
            <button
              onClick={() => setMobileNavOpen(false)}
              className="absolute top-4 right-3 rounded p-1 text-gray-400 hover:text-white"
              aria-label="Close navigation menu"
            >
              <X className="h-5 w-5" />
            </button>
            <Sidebar onCloseMobile={() => setMobileNavOpen(false)} />
          </div>
        </div>
      )}

      {/* Main Viewport Container */}
      <div className="flex flex-1 flex-col overflow-hidden">
        {/* Header Bar */}
        <Header
          title={title}
          onOpenMobileNav={() => setMobileNavOpen(true)}
          onRefreshData={onRefreshData}
          isRefreshing={isRefreshing}
        />

        {/* Scrollable Page Content */}
        <main className="flex-1 overflow-y-auto p-4 md:p-6 bg-clinical-surface">
          <div className="mx-auto max-w-7xl">
            {children}
          </div>
        </main>
      </div>
    </div>
  );
};
