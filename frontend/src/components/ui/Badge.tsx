import React from 'react';

export type BadgeVariant =
  | 'critical'
  | 'warning'
  | 'ready'
  | 'info'
  | 'neutral'
  | 'isolation'
  | 'icu';

interface BadgeProps {
  children: React.ReactNode;
  variant?: BadgeVariant;
  className?: string;
}

export const Badge: React.FC<BadgeProps> = ({ children, variant = 'neutral', className = '' }) => {
  const variantStyles: Record<BadgeVariant, string> = {
    critical: 'bg-status-critical-bg text-status-critical border-status-critical-border',
    warning: 'bg-status-warning-bg text-status-warning border-status-warning-border',
    ready: 'bg-status-ready-bg text-status-ready border-status-ready-border',
    info: 'bg-status-info-bg text-status-info border-status-info-border',
    neutral: 'bg-clinical-container text-clinical-on-surface-variant border-clinical-outline-variant',
    isolation: 'bg-purple-50 text-purple-700 border-purple-200',
    icu: 'bg-rose-50 text-rose-800 border-rose-200 font-bold',
  };

  return (
    <span
      className={`inline-flex items-center rounded border px-2 py-0.5 text-[11px] font-semibold uppercase tracking-wider ${variantStyles[variant]} ${className}`}
    >
      {children}
    </span>
  );
};
