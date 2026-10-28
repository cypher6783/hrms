import React from 'react';

interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
  className?: string;
  statusBarColor?: 'critical' | 'warning' | 'ready' | 'info' | 'none';
}

export const Card: React.FC<CardProps> = ({
  children,
  className = '',
  statusBarColor = 'none',
  ...props
}) => {
  const statusBarClasses = {
    critical: 'border-l-4 border-l-status-critical',
    warning: 'border-l-4 border-l-status-warning',
    ready: 'border-l-4 border-l-status-ready',
    info: 'border-l-4 border-l-status-info',
    none: '',
  };

  return (
    <div
      className={`rounded-md border border-clinical-border bg-white shadow-clinical transition-shadow ${statusBarClasses[statusBarColor]} ${className}`}
      {...props}
    >
      {children}
    </div>
  );
};

export const CardHeader: React.FC<{ children: React.ReactNode; className?: string }> = ({
  children,
  className = '',
}) => (
  <div className={`flex items-center justify-between border-b border-clinical-border px-4 py-3 ${className}`}>
    {children}
  </div>
);

export const CardTitle: React.FC<{ children: React.ReactNode; className?: string }> = ({
  children,
  className = '',
}) => (
  <h3 className={`text-xs font-bold tracking-wider text-clinical-on-surface uppercase ${className}`}>
    {children}
  </h3>
);

export const CardContent: React.FC<{ children: React.ReactNode; className?: string }> = ({
  children,
  className = '',
}) => <div className={`p-4 ${className}`}>{children}</div>;
