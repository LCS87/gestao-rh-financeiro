import React from 'react';
import { cn } from '../../utils/cn';

interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  label?: string;
  error?: string;
  helperText?: string;
}

export function Select({
  label,
  error,
  helperText,
  className,
  id,
  children,
  ...props
}: SelectProps) {
  const selectId = id || `select-${Math.random().toString(36).substr(2, 9)}`;

  return (
    <div className="space-y-2">
      {label && (
        <label htmlFor={selectId} className="block text-sm font-medium text-slate-300">
          {label}
        </label>
      )}

      <select
        id={selectId}
        className={cn(
          'w-full px-3 py-2 bg-slate-700 text-white rounded border border-slate-600',
          'focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500',
          'disabled:opacity-50 disabled:cursor-not-allowed',
          error && 'border-red-500 focus:border-red-500 focus:ring-red-500',
          className,
        )}
        {...props}
      >
        {children}
      </select>

      {error && <p className="text-sm text-red-400">{error}</p>}
      {helperText && !error && <p className="text-sm text-slate-400">{helperText}</p>}
    </div>
  );
}
