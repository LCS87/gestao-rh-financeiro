import React from 'react';
import { ArrowUpDown, ArrowUp, ArrowDown } from 'lucide-react';

type SortDirection = 'asc' | 'desc' | null;

interface SortState<K extends string> {
  column: K | null;
  direction: SortDirection;
}

export function useSortableTable<T, K extends string>(
  items: T[],
  accessor: (item: T, key: K) => string | number | undefined
) {
  const [sort, setSort] = React.useState<SortState<K>>({ column: null, direction: null });

  const toggleSort = (column: K) => {
    setSort((prev) => {
      if (prev.column !== column) return { column, direction: 'asc' };
      if (prev.direction === 'asc') return { column, direction: 'desc' };
      return { column: null, direction: null }; // 3rd click resets
    });
  };

  const sorted = React.useMemo(() => {
    if (!sort.column || !sort.direction) return items;
    const col = sort.column;
    const dir = sort.direction === 'asc' ? 1 : -1;
    return [...items].sort((a, b) => {
      const va = accessor(a, col) ?? '';
      const vb = accessor(b, col) ?? '';
      if (typeof va === 'number' && typeof vb === 'number') return (va - vb) * dir;
      return String(va).localeCompare(String(vb), 'pt-BR', { sensitivity: 'base' }) * dir;
    });
  }, [items, sort, accessor]);

  return { sorted, sort, toggleSort };
}

// ── Sortable header cell ───────────────────────────

interface SortableThProps<K extends string> {
  column: K;
  label: string;
  currentSort: SortState<K>;
  onSort: (col: K) => void;
  className?: string;
}

export function SortableTh<K extends string>({
  column,
  label,
  currentSort,
  onSort,
  className = '',
}: SortableThProps<K>) {
  const isActive = currentSort.column === column;
  return (
    <th className={`px-4 py-3 text-slate-400 font-medium select-none ${className}`}>
      <button
        onClick={() => onSort(column)}
        className="flex items-center gap-1 hover:text-white transition-colors group"
        aria-label={`Ordenar por ${label}`}
      >
        {label}
        <span className="inline-flex text-slate-500 group-hover:text-slate-300">
          {isActive && currentSort.direction === 'asc' ? (
            <ArrowUp className="w-3.5 h-3.5 text-blue-400" />
          ) : isActive && currentSort.direction === 'desc' ? (
            <ArrowDown className="w-3.5 h-3.5 text-blue-400" />
          ) : (
            <ArrowUpDown className="w-3.5 h-3.5" />
          )}
        </span>
      </button>
    </th>
  );
}
