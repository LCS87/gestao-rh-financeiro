import React, { createContext, useContext, useState, useCallback, useRef } from 'react';
import { AlertTriangle, X } from 'lucide-react';
import { AccessibleModal } from './AccessibleModal';

interface ConfirmOptions {
  title?: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  variant?: 'danger' | 'warning' | 'default';
}

interface ConfirmContextType {
  confirm: (options: ConfirmOptions) => Promise<boolean>;
}

const ConfirmContext = createContext<ConfirmContextType | undefined>(undefined);

export function ConfirmProvider({ children }: { children: React.ReactNode }) {
  const [open, setOpen] = useState(false);
  const [options, setOptions] = useState<ConfirmOptions | null>(null);
  const resolveRef = useRef<((value: boolean) => void) | null>(null);

  const confirm = useCallback((opts: ConfirmOptions): Promise<boolean> => {
    setOptions(opts);
    setOpen(true);
    return new Promise<boolean>((resolve) => {
      resolveRef.current = resolve;
    });
  }, []);

  const handleResponse = (value: boolean) => {
    setOpen(false);
    resolveRef.current?.(value);
    resolveRef.current = null;
  };

  const variantStyles = {
    danger: {
      icon: 'bg-red-500/20 text-red-400',
      button: 'bg-red-500 hover:bg-red-600 text-white',
    },
    warning: {
      icon: 'bg-yellow-500/20 text-yellow-400',
      button: 'bg-yellow-500 hover:bg-yellow-600 text-black',
    },
    default: {
      icon: 'bg-blue-500/20 text-blue-400',
      button: 'bg-blue-500 hover:bg-blue-600 text-white',
    },
  };

  const styles = variantStyles[options?.variant || 'default'];

  return (
    <ConfirmContext.Provider value={{ confirm }}>
      {children}

      <AccessibleModal
        open={open && !!options}
        onClose={() => handleResponse(false)}
        ariaLabel={options?.title || 'Confirmar ação'}
        className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-[110] p-4"
      >
        {options && (
          <div className="bg-slate-800 rounded-2xl max-w-sm w-full border border-slate-700 shadow-2xl animate-scale-in">
            {/* Header */}
            <div className="flex items-start gap-4 p-6 pb-2">
              <div className={`w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0 ${styles.icon}`}>
                <AlertTriangle className="w-5 h-5" />
              </div>
              <div className="flex-1 min-w-0">
                <h3 className="text-lg font-semibold text-white">
                  {options.title || 'Confirmar ação'}
                </h3>
                <p className="text-sm text-slate-400 mt-1">{options.message}</p>
              </div>
              <button
                onClick={() => handleResponse(false)}
                className="p-1 text-slate-400 hover:text-white rounded-lg hover:bg-white/10 transition-colors flex-shrink-0"
                aria-label="Fechar"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            {/* Actions */}
            <div className="flex gap-3 p-6 pt-4 justify-end">
              <button
                onClick={() => handleResponse(false)}
                className="px-4 py-2 rounded-xl text-sm font-medium text-slate-300 bg-slate-700 hover:bg-slate-600 transition-colors"
              >
                {options.cancelText || 'Cancelar'}
              </button>
              <button
                onClick={() => handleResponse(true)}
                className={`px-4 py-2 rounded-xl text-sm font-medium transition-colors ${styles.button}`}
              >
                {options.confirmText || 'Confirmar'}
              </button>
            </div>
          </div>
        )}
      </AccessibleModal>
    </ConfirmContext.Provider>
  );
}

export function useConfirm() {
  const context = useContext(ConfirmContext);
  if (!context) {
    throw new Error('useConfirm must be used within a ConfirmProvider');
  }
  return context.confirm;
}
