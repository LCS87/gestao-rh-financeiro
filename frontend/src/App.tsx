import { Navigate, Route, Routes } from 'react-router-dom';
import { ConfirmProvider } from './components/ConfirmDialog';
import { ErrorBoundary } from './components/ErrorBoundary';
import Layout from './components/Layout';
import ProtectedRoute from './components/ProtectedRoute';
import { ToastProvider } from './components/Toast';
import { AuthProvider } from './contexts/AuthContext';
import { QueryProvider } from './hooks/useApi';
import { AdmissoesDemissoes } from './pages/AdmissoesDemissoes';
import Avaliacoes from './pages/Avaliacoes';
import Beneficios from './pages/Beneficios';
import { Escalas } from './pages/Escalas';
import Cargos from './pages/Cargos';
import DashboardPage from './pages/Dashboard';
import Departamentos from './pages/Departamentos';
import Documentos from './pages/Documentos';
import { Ferias } from './pages/Ferias';
import { FluxoCaixa } from './pages/FluxoCaixa';
import { FuncionariosPage } from './pages/Funcionarios';
import { Holerites } from './pages/Holerites';
import { LoginPage } from './pages/LoginPage';
import Mural from './pages/Mural';
import { Ponto } from './pages/Ponto';
import { Comissoes } from './pages/Comissoes';
import { Relatorios } from './pages/Relatorios';
import SettingsPage from './pages/Settings';
import { VendasPage } from './pages/Vendas';

function App() {
  return (
    <ErrorBoundary>
      <QueryProvider>
        <AuthProvider>
          <ToastProvider>
            <ConfirmProvider>
              <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/" element={<Navigate to="/dashboard" replace />} />
                <Route
                  path="/dashboard"
                  element={
                    <ProtectedRoute>
                      <Layout>
                        <DashboardPage />
                      </Layout>
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/employees"
                  element={
                    <ProtectedRoute requiredLevel="admin">
                      <Layout>
                        <FuncionariosPage />
                      </Layout>
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/sales"
                  element={
                    <ProtectedRoute requiredLevel="user">
                      <Layout>
                        <VendasPage />
                      </Layout>
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/departamentos"
                  element={
                    <ProtectedRoute requiredLevel="admin">
                      <Layout>
                        <Departamentos />
                      </Layout>
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/cargos"
                  element={
                    <ProtectedRoute requiredLevel="admin">
                      <Layout>
                        <Cargos />
                      </Layout>
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/fluxo-caixa"
                  element={
                    <ProtectedRoute requiredLevel="financeiro">
                      <Layout>
                        <FluxoCaixa />
                      </Layout>
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/holerites"
                  element={
                    <ProtectedRoute requiredLevel="rh">
                      <Layout>
                        <Holerites />
                      </Layout>
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/ferias"
                  element={
                    <ProtectedRoute requiredLevel="rh">
                      <Layout>
                        <Ferias />
                      </Layout>
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/avaliacoes"
                  element={
                    <ProtectedRoute requiredLevel="rh">
                      <Layout>
                        <Avaliacoes />
                      </Layout>
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/mural"
                  element={
                    <ProtectedRoute requiredLevel="user">
                      <Layout>
                        <Mural />
                      </Layout>
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/beneficios"
                  element={
                    <ProtectedRoute requiredLevel="rh">
                      <Layout>
                        <Beneficios />
                      </Layout>
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/documentos"
                  element={
                    <ProtectedRoute requiredLevel="rh">
                      <Layout>
                        <Documentos />
                      </Layout>
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/ponto"
                  element={
                    <ProtectedRoute requiredLevel="supervisor">
                      <Layout>
                        <Ponto />
                      </Layout>
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/escalas"
                  element={
                    <ProtectedRoute requiredLevel="supervisor">
                      <Layout>
                        <Escalas />
                      </Layout>
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/admissoes-demissoes"
                  element={
                    <ProtectedRoute requiredLevel="rh">
                      <Layout>
                        <AdmissoesDemissoes />
                      </Layout>
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/comissoes"
                  element={
                    <ProtectedRoute requiredLevel="supervisor">
                      <Layout>
                        <Comissoes />
                      </Layout>
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/relatorios"
                  element={
                    <ProtectedRoute requiredLevel="financeiro">
                      <Layout>
                        <Relatorios />
                      </Layout>
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/settings"
                  element={
                    <ProtectedRoute requiredLevel="ti">
                      <Layout>
                        <SettingsPage />
                      </Layout>
                    </ProtectedRoute>
                  }
                />
                <Route path="*" element={<Navigate to="/dashboard" replace />} />
              </Routes>
            </ConfirmProvider>
          </ToastProvider>
        </AuthProvider>
      </QueryProvider>
    </ErrorBoundary>
  );
}

export default App;
