import React from 'react';

export const Table: React.FC<{ children: React.ReactNode; className?: string }> = ({
  children,
  className = '',
}) => (
  <div className="w-full overflow-x-auto rounded border border-clinical-border bg-white shadow-xs">
    <table className={`w-full text-left text-xs border-collapse ${className}`}>{children}</table>
  </div>
);

export const TableHeader: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <thead className="border-b border-clinical-border bg-clinical-container-low text-[11px] font-bold text-clinical-on-surface-variant uppercase tracking-wider">
    {children}
  </thead>
);

export const TableRow: React.FC<{ children: React.ReactNode; className?: string; onClick?: () => void }> = ({
  children,
  className = '',
  onClick,
}) => (
  <tr
    onClick={onClick}
    className={`border-b border-clinical-border/60 hover:bg-clinical-container-low/80 transition-colors ${
      onClick ? 'cursor-pointer' : ''
    } ${className}`}
  >
    {children}
  </tr>
);

export const TableHead: React.FC<{ children: React.ReactNode; className?: string }> = ({
  children,
  className = '',
}) => <th className={`px-3 py-2.5 font-bold ${className}`}>{children}</th>;

export const TableCell: React.FC<{ children: React.ReactNode; className?: string }> = ({
  children,
  className = '',
}) => <td className={`px-3 py-2.5 text-clinical-on-surface ${className}`}>{children}</td>;
