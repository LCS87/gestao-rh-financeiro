import React from 'react';
import { TrendingUp } from 'lucide-react';

interface StatCardProps {
  icon: React.ReactNode;
  value: string;
  label: string;
  trend?: string;
  trendColor?: string;
  gradient: string;
  hoverBorder: string;
  hoverShadow?: string;
  hoverBg?: string;
  progress?: number;
}

export function StatCard({
  icon,
  value,
  label,
  trend,
  trendColor,
  gradient,
  hoverBorder,
  hoverShadow = '',
  hoverBg = 'from-transparent',
  progress,
}: StatCardProps) {
  return (
    <div
      className={`group relative overflow-hidden rounded-2xl bg-slate-800/50 backdrop-blur border border-slate-700 p-5 md:p-6 ${hoverBorder} transition-all duration-300 hover:shadow-xl ${hoverShadow}`}
    >
      <div
        className={`absolute inset-0 bg-gradient-to-br ${hoverBg} to-transparent opacity-0 group-hover:opacity-100 transition-opacity`}
      />
      <div className="relative">
        <div className="flex items-center justify-between mb-4">
          <div
            className={`w-12 h-12 rounded-xl bg-gradient-to-br ${gradient} flex items-center justify-center shadow-lg`}
          >
            {icon}
          </div>
          {trend && (
            <span className={`text-xs ${trendColor} flex items-center gap-1`}>
              <TrendingUp className="w-3 h-3" /> {trend}
            </span>
          )}
        </div>
        <div className="text-3xl font-bold text-white">{value}</div>
        <div className="text-sm text-slate-400">{label}</div>
        {typeof progress === 'number' && progress > 0 && (
          <div className="mt-3 h-2 bg-slate-700 rounded-full overflow-hidden">
            <div
              className="h-full bg-gradient-to-r from-blue-500 to-blue-400 rounded-full"
              style={{ width: `${progress}%` }}
            />
          </div>
        )}
      </div>
    </div>
  );
}
