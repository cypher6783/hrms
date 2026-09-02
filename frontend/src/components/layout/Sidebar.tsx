import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  Users,
  UserCheck,
  BedDouble,
  Package,
  Boxes,
  Stethoscope,
  Wrench,
  UserSquare2,
  Sparkles,
  History,
  ShieldAlert,
  LogOut
} from 'lucide-react';
import { useAuth } from '../../auth/AuthContext';

interface SidebarProps {
  onCloseMobile?: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ onCloseMobile }) => {
  const { user, logout } = useAuth();

  const navItems = [
    { label: 'Operations Dashboard', path: '/', icon: LayoutDashboard },
    { label: 'Recommendations', path: '/recommendations', icon: Sparkles, badge: 'AI' },
    { label: 'Patients', path: '/patients', icon: Users },
    { label: 'Admissions', path: '/admissions', icon: UserCheck },
    { label: 'Beds & Wards', path: '/beds', icon: BedDouble },
    { label: 'Resources', path: '/resources', icon: Package },
    { label: 'Inventory', path: '/inventory', icon: Boxes },
    { label: 'Equipment', path: '/equipment', icon: Stethoscope },
    { label: 'Maintenance', path: '/maintenance', icon: Wrench },
    { label: 'Staff & Shifts', path: '/staff', icon: UserSquare2 },
    { label: 'Audit Log', path: '/audit', icon: History },
  ];

  return (
    <aside className="flex h-full w-[280px] flex-col justify-between border-r border-clinical-border bg-clinical-navy text-white">
      {/* Brand Header */}
      <div>
        <div className="flex flex-col gap-1 border-b border-gray-800 p-5">
          <div className="flex items-center gap-2.5">
            <div className="flex h-8 w-8 items-center justify-center rounded bg-red-600 font-bold text-white shadow-sm">
              <ShieldAlert className="h-5 w-5" />
            </div>
            <div>
              <h1 className="text-sm font-bold tracking-tight text-white">BSUTH LASSA</h1>
              <p className="text-[11px] font-semibold tracking-wider text-gray-400 uppercase">Resource Manager</p>
            </div>
          </div>
          <div className="mt-2 inline-flex items-center gap-1.5 rounded bg-gray-900/80 px-2 py-1 text-[11px] text-red-400 border border-red-900/50">
            <span className="h-1.5 w-1.5 rounded-full bg-red-500 animate-pulse"></span>
            Lassa Fever Isolation Unit
          </div>
        </div>

        {/* Navigation Menu */}
        <nav className="flex-1 space-y-1 p-3">
          <p className="px-3 text-[10px] font-bold tracking-widest text-gray-400 uppercase mb-2">Operational Command</p>
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.path}
                to={item.path}
                onClick={onCloseMobile}
                className={({ isActive }) =>
                  `flex items-center justify-between rounded-md px-3 py-2 text-xs font-medium transition-colors ${
                    isActive
                      ? 'bg-gray-800 text-white font-semibold shadow-sm border-l-2 border-status-info'
                      : 'text-gray-300 hover:bg-gray-800/60 hover:text-white'
                  }`
                }
              >
                <div className="flex items-center gap-2.5">
                  <Icon className="h-4 w-4 shrink-0 text-gray-400" />
                  <span>{item.label}</span>
                </div>
                {item.badge && (
                  <span className="rounded bg-status-info/20 px-1.5 py-0.5 text-[10px] font-bold text-status-info border border-status-info/30">
                    {item.badge}
                  </span>
                )}
              </NavLink>
            );
          })}
        </nav>
      </div>

      {/* User Footer */}
      <div className="border-t border-gray-800 p-3">
        <div className="flex items-center justify-between rounded-md bg-gray-900/60 p-2.5">
          <div className="flex items-center gap-2.5 overflow-hidden">
            <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-slate-700 text-xs font-bold text-white">
              {user?.fullName ? user.fullName.charAt(0) : 'U'}
            </div>
            <div className="truncate">
              <p className="truncate text-xs font-semibold text-white">{user?.fullName || 'Clinician'}</p>
              <p className="truncate text-[10px] text-gray-400 font-mono">{user?.role || 'DOCTOR'}</p>
            </div>
          </div>
          <button
            onClick={logout}
            title="Sign out"
            className="rounded p-1 text-gray-400 hover:bg-gray-800 hover:text-white"
          >
            <LogOut className="h-4 w-4" />
          </button>
        </div>
      </div>
    </aside>
  );
};
