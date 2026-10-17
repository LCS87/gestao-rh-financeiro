import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/Input';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/card';
import { useAuth } from '../contexts/AuthContext';
import { Building2 } from 'lucide-react';

// Schema de validação do login
const loginSchema = z.object({
  username: z.string().min(1, 'Usuário é obrigatório'),
  password: z.string().min(1, 'Senha é obrigatória'),
});

type LoginFormData = z.infer<typeof loginSchema>;

export function LoginPage() {
  const navigate = useNavigate();
  const { login, isLoading } = useAuth();

  const {
    register,
    handleSubmit,
    formState: { errors },
    setError,
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormData) => {
    try {
      const success = await login(data.username, data.password);
      if (success) {
        navigate('/dashboard');
      } else {
        setError('root', { message: 'Usuário ou senha incorretos' });
      }
    } catch (error) {
      setError('root', { message: 'Erro ao fazer login' });
    }
  };

  return (
    <div className="min-h-screen bg-slate-900 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <Card>
          <CardHeader>
            <CardTitle className="text-center">
              <div className="flex items-center justify-center gap-2 text-3xl font-bold text-white mb-2">
                <Building2 className="w-8 h-8 text-blue-400" />
                Gestão RH-Financeiro
              </div>
              <p className="text-slate-300 text-lg">Faça login para acessar o sistema</p>
            </CardTitle>
          </CardHeader>

          <CardContent>
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
              <Input
                label="Usuário"
                {...register('username')}
                error={errors.username?.message}
                placeholder="Digite seu usuário"
                disabled={isLoading}
              />

              <Input
                label="Senha"
                type="password"
                {...register('password')}
                error={errors.password?.message}
                placeholder="Digite sua senha"
                disabled={isLoading}
              />

              {errors.root && (
                <div className="bg-red-600 bg-opacity-20 border border-red-600 rounded p-3">
                  <p className="text-red-400 text-sm">{errors.root.message}</p>
                </div>
              )}

              <Button
                type="submit"
                variant="default"
                size="lg"
                disabled={isLoading}
                className="w-full"
              >
                {isLoading ? 'Entrando...' : 'Entrar'}
              </Button>
            </form>

          </CardContent>
        </Card>
      </div>
    </div>
  );
}
