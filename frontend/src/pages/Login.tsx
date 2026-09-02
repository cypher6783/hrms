import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { ShieldAlert, Lock, User as UserIcon, AlertTriangle } from 'lucide-react';
import { Button } from '../components/ui/Button';

export const Login: React.FC = () => {
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('password123');
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const from = (location.state as any)?.from?.pathname || '/';

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setIsLoading(true);

    try {
      await login({ username, password });
      navigate(from, { replace: true });
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Authentication failed. Please verify credentials.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen w-screen items-center justify-center bg-clinical-surface px-4 py-8">
      <div className="w-full max-w-md space-y-6">
        {/* Brand Banner */}
        <div className="text-center">
          <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-lg bg-clinical-navy text-white shadow-md mb-3">
            <ShieldAlert className="h-7 w-7 text-red-500" />
          </div>
          <h1 className="text-xl font-bold tracking-tight text-clinical-navy">BSUTH LASSA</h1>
          <p className="text-xs font-bold tracking-widest text-clinical-on-surface-variant uppercase mt-0.5">
            Resource Allocation Manager
          </p>
          <div className="mt-2 inline-flex items-center gap-1.5 rounded bg-red-50 px-2.5 py-1 text-[11px] font-semibold text-red-700 border border-red-200">
            <span className="h-1.5 w-1.5 rounded-full bg-red-600 animate-pulse"></span>
            Lassa Fever Isolation Command Unit
          </div>
        </div>

        {/* Security Warning Box */}
        <div className="rounded-md border border-clinical-border bg-white p-6 shadow-modal">
          <form onSubmit={handleSubmit} className="space-y-4">
            {error && (
              <div className="flex items-start gap-2.5 rounded border border-status-critical-border bg-status-critical-bg p-3 text-xs text-status-critical">
                <AlertTriangle className="h-4 w-4 shrink-0 mt-0.5" />
                <div>
                  <p className="font-semibold">Authentication Error</p>
                  <p className="mt-0.5 text-clinical-on-surface-variant">{error}</p>
                </div>
              </div>
            )}

            <div>
              <label className="block text-xs font-semibold text-clinical-on-surface uppercase mb-1">
                Username / Clinical ID
              </label>
              <div className="relative">
                <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3 text-gray-400">
                  <UserIcon className="h-4 w-4" />
                </div>
                <input
                  type="text"
                  required
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="w-full rounded border border-clinical-border bg-white py-2 pl-9 pr-3 text-xs font-medium text-clinical-on-surface focus:border-status-info focus:outline-none focus:ring-1 focus:ring-status-info"
                  placeholder="Enter authorized username"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-clinical-on-surface uppercase mb-1">
                Password
              </label>
              <div className="relative">
                <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3 text-gray-400">
                  <Lock className="h-4 w-4" />
                </div>
                <input
                  type="password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full rounded border border-clinical-border bg-white py-2 pl-9 pr-3 text-xs font-medium text-clinical-on-surface focus:border-status-info focus:outline-none focus:ring-1 focus:ring-status-info"
                  placeholder="Enter password"
                />
              </div>
            </div>

            <Button type="submit" isLoading={isLoading} className="w-full text-xs font-bold uppercase tracking-wider py-2">
              Sign In to Command Center
            </Button>
          </form>

          <div className="mt-4 border-t border-clinical-border pt-3 text-center text-[11px] text-clinical-on-surface-variant">
            Authorized Personnel Only — BSUTH Clinical Operations
          </div>
        </div>
      </div>
    </div>
  );
};
