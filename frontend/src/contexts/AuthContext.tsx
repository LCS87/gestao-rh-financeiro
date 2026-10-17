import React, { createContext, useContext, useReducer, useEffect } from 'react';
import { api } from '../lib/api';

// Types
interface User {
  id: string;
  username: string;
  level: 'ti' | 'admin' | 'supervisor' | 'user' | 'financeiro' | 'rh';
  name: string;
}

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}

interface AuthContextType extends AuthState {
  login: (username: string, password: string) => Promise<boolean>;
  logout: () => void;
  hasPermission: (requiredLevel: 'ti' | 'admin' | 'supervisor' | 'user' | 'financeiro' | 'rh') => boolean;
}

// Reducer
type AuthAction =
  | { type: 'LOGIN_START' }
  | { type: 'LOGIN_SUCCESS'; payload: User }
  | { type: 'LOGIN_FAILURE' }
  | { type: 'LOGOUT' };

function authReducer(state: AuthState, action: AuthAction): AuthState {
  switch (action.type) {
    case 'LOGIN_START':
      return { ...state, isLoading: true };
    case 'LOGIN_SUCCESS':
      return { user: action.payload, isAuthenticated: true, isLoading: false };
    case 'LOGIN_FAILURE':
    case 'LOGOUT':
      return { user: null, isAuthenticated: false, isLoading: false };
    default:
      return state;
  }
}

// Map backend roles to frontend levels
function roleToLevel(roles: string[] | string): 'ti' | 'admin' | 'supervisor' | 'user' | 'financeiro' | 'rh' {
  const r = Array.isArray(roles) ? roles : [roles];
  if (r.some((x) => /ROLE_TI|ROLE_SUPER_ADMIN/i.test(x))) return 'ti';
  if (r.some((x) => /ROLE_ADMIN/i.test(x))) return 'admin';
  if (r.some((x) => /ROLE_FINANCEIRO/i.test(x))) return 'financeiro';
  if (r.some((x) => /ROLE_RH/i.test(x))) return 'rh';
  if (r.some((x) => /ROLE_SUPERVISOR/i.test(x))) return 'supervisor';
  return 'user';
}

// Context
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Provider
export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [state, dispatch] = useReducer(authReducer, {
    user: null,
    isAuthenticated: false,
    isLoading: true,
  });

  // Restore session from localStorage
  useEffect(() => {
    const storedUser = localStorage.getItem('auth_user');
    const token = localStorage.getItem('accessToken');
    if (storedUser && token) {
      try {
        const user = JSON.parse(storedUser);
        dispatch({ type: 'LOGIN_SUCCESS', payload: user });
      } catch {
        localStorage.removeItem('auth_user');
        dispatch({ type: 'LOGIN_FAILURE' });
      }
    } else {
      dispatch({ type: 'LOGIN_FAILURE' });
    }
  }, []);

  const login = async (username: string, password: string): Promise<boolean> => {
    dispatch({ type: 'LOGIN_START' });

    try {
      const { data } = await api.post('/auth/login', { username, password });

      // Store JWT tokens
      localStorage.setItem('accessToken', data.accessToken);
      localStorage.setItem('refreshToken', data.refreshToken);

      // Decode user info from token payload (JWT base64)
      let userInfo: User;
      try {
        const payload = JSON.parse(atob(data.accessToken.split('.')[1]));
        userInfo = {
          id: payload.sub || payload.id || username,
          username: payload.username || username,
          level: roleToLevel(payload.roles || payload.authorities || payload.role || []),
          name: payload.name || payload.nome || username,
        };
      } catch {
        // Fallback if token decode fails
        userInfo = { id: username, username, level: 'user', name: username };
      }

      localStorage.setItem('auth_user', JSON.stringify(userInfo));
      dispatch({ type: 'LOGIN_SUCCESS', payload: userInfo });
      return true;
    } catch {
      dispatch({ type: 'LOGIN_FAILURE' });
      return false;
    }
  };

  const logout = () => {
    localStorage.removeItem('auth_user');
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    dispatch({ type: 'LOGOUT' });
  };

  const hasPermission = (requiredLevel: 'ti' | 'admin' | 'supervisor' | 'user' | 'financeiro' | 'rh'): boolean => {
    if (!state.user) return false;
    // simplificação progressiva de níveis para acesso 
    const levels = { ti: 6, admin: 5, financeiro: 4, rh: 3, supervisor: 2, user: 1 };
    return levels[state.user.level] >= levels[requiredLevel];
  };

  return (
    <AuthContext.Provider value={{ ...state, login, logout, hasPermission }}>
      {children}
    </AuthContext.Provider>
  );
}

// Hook
export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
