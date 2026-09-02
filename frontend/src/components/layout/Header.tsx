import React from 'react';
import { Menu, Bell, RefreshCw } from 'lucide-react';
import { useAuth } from '../../auth/AuthContext';

interface HeaderProps {
  title: string;
  onOpenMobileNav: () => void;
  onRefreshData?: () => void;
  isRefreshing?: boolean;
}

export const Header: React.FC<HeaderProps> = ({
  title,
  onOpenMobileNav,
  onRefreshData,
  isRefreshing = false,
}) => {
  const { user } = useAuth();
  const lastUpdated = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

  return (
    <header className="sticky top-0 z-20 flex h-14 w-full items-center justify-between border-b border-clinical-border bg-white px-4 shadow-sm md:px-6">
      {/* Left: Mobile Toggle & Page Title */}
      <div className="flex items-center gap-3">
        <button
          onClick={onOpenMobileNav}
          className="rounded p-1.5 text-clinical-on-surface-variant hover:bg-clinical-container md:hidden"
          aria-label="Open Navigation Drawer"
        >
          <Menu className="h-5 w-5" />
        </button>
        <div>
          <h1 className="text-base font-bold text-clinical-on-surface tracking-tight md:text-lg">{title}</h1>
        </div>
      </div>

      {/* Right: Operational Status, Refresh, Notifications, User Badge */}
      <div className="flex items-center gap-3">
        {/* Connection & Freshness Status */}
        <div className="hidden items-center gap-2 rounded bg-clinical-container-low px-2.5 py-1 text-xs text-clinical-on-surface-variant md:flex">
          <span className="flex h-2 w-2 relative">
            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-status-ready opacity-75"></span>
            <span className="relative inline-flex rounded-full h-2 w-2 bg-status-ready"></span>
          </span>
          <span className="font-semibold text-clinical-navy">Live Hospital Feed</span>
          <span className="text-gray-400">|</span>
          <span className="font-tabular text-[11px]">Updated {lastUpdated}</span>
        </div>

        {/* Data Refresh Button */}
        {onRefreshData && (
          <button
            onClick={onRefreshData}
            disabled={isRefreshing}
            className="flex items-center gap-1.5 rounded border border-clinical-border bg-white px-2.5 py-1 text-xs font-medium text-clinical-on-surface-variant hover:bg-clinical-container hover:text-clinical-navy transition-colors disabled:opacity-50"
            title="Refresh Operational Data"
          >
            <RefreshCw className={`h-3.5 w-3.5 ${isRefreshing ? 'animate-spin text-status-info' : ''}`} />
            <span className="hidden sm:inline">Refresh</span>
          </button>
        )}

        {/* System Alert Bell */}
        <button
          className="relative rounded border border-clinical-border p-1.5 text-clinical-on-surface-variant hover:bg-clinical-container"
          aria-label="Notifications"
        >
          <Bell className="h-4 w-4" />
          <span className="absolute -top-1 -right-1 flex h-4 w-4 items-center justify-center rounded-full bg-status-critical text-[9px] font-bold text-white">
            2
          </span>
        </button>

        {/* User Badge Mobile/Desktop */}
        <div className="flex items-center gap-2 border-l border-clinical-border pl-3">
          <div className="flex h-7 w-7 items-center justify-center rounded bg-clinical-navy text-xs font-bold text-white shadow-xs">
            {user?.fullName ? user.fullName.charAt(0) : 'U'}
          </div>
          <div className="hidden flex-col md:flex">
            <span className="text-xs font-semibold leading-none text-clinical-on-surface">{user?.fullName}</span>
            <span className="text-[10px] leading-none text-clinical-on-surface-variant font-mono mt-0.5">{user?.role}</span>
          </div>
        </div>
      </div>
    </header>
  );
};
