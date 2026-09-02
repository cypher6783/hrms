import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from './AuthContext';

interface ProtectedRouteProps {
  children: React.ReactNode;
  allowedRoles?: string[];
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children, allowedRoles }) => {
  const { isAuthenticated, isLoading, hasRole } = useAuth();
  const location = useLocation();

  if (isLoading) {
    return (
      <div className="flex h-screen w-screen items-center justify-center bg-clinical-surface">
        <div className="flex flex-col items-center gap-3">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-clinical-navy border-t-transparent"></div>
          <p className="text-sm font-medium text-clinical-on-surface-variant">Verifying security context...</p>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (allowedRoles && !hasRole(allowedRoles)) {
    return (
      <div className="flex h-screen w-screen flex-col items-center justify-center bg-clinical-surface p-6 text-center">
        <div className="rounded-lg border border-status-critical-border bg-status-critical-bg p-8 max-w-md shadow-clinical">
          <h2 className="text-xl font-semibold text-status-critical mb-2">Permission Denied</h2>
          <p className="text-sm text-clinical-on-surface-variant mb-4">
            Your assigned role does not have authorization to access this operational workflow.
          </p>
          <a
            href="/"
            className="inline-flex items-center justify-center rounded-md bg-clinical-navy px-4 py-2 text-sm font-medium text-white shadow hover:bg-opacity-90"
          >
            Return to Dashboard
          </a>
        </div>
      </div>
    );
  }

  return <>{children}</>;
};
