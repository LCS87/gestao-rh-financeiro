import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { api } from '../lib/api';
import { useEmployees, useUploadDocumento } from '../hooks/useApi';
import { useToast } from '../components/Toast';
import CrudLayout from '../components/CrudLayout';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { AlertTriangle, FileWarning, Plus, X, Upload } from 'lucide-react';

interface Documento {
  id: string;
  funcionarioId: string;
  tipoDocumento: string;
  nomeArquivo: string;
  dataValidade?: string;
  status: string;
}

export default function Documentos() {
  const [tab, setTab] = useState<'todos' | 'vencidos' | 'avencer'>('todos');
  const [showModal, setShowModal] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [uploadData, setUploadData] = useState({
    funcionarioId: '',
    tipoDocumento: 'CONTRATO_TRABALHO',
    dataValidade: '',
  });

  const { showToast } = useToast();
  const uploadDoc = useUploadDocumento();
  const { data: employees } = useEmployees();

  const handleUpload = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedFile || !uploadData.funcionarioId) return;

    const formData = new FormData();
    formData.append('funcionarioId', uploadData.funcionarioId);
    formData.append('tipoDocumento', uploadData.tipoDocumento);
    formData.append('arquivo', selectedFile);
    if (uploadData.dataValidade) {
        formData.append('dataValidade', uploadData.dataValidade);
    }

    try {
      await uploadDoc.mutateAsync(formData);
      showToast('Documento enviado com sucesso!', 'success');
      setShowModal(false);
      setUploadData({ funcionarioId: '', tipoDocumento: 'CONTRATO_TRABALHO', dataValidade: '' });
      setSelectedFile(null);
    } catch {
      showToast('Erro ao realizar upload.', 'error');
    }
  };

  const { data: todos = [], isLoading: loadingTodos } = useQuery({
    queryKey: ['documentos'],
    queryFn: async () => {
      const { data } = await api.get<Documento[]>('/rh/documentos');
      return data;
    },
  });

  const { data: vencidos = [] } = useQuery({
    queryKey: ['documentos', 'vencidos'],
    queryFn: async () => {
      const { data } = await api.get<Documento[]>('/rh/documentos/vencidos');
      return data;
    },
    enabled: tab === 'vencidos',
  });

  const { data: aVencer = [] } = useQuery({
    queryKey: ['documentos', 'avencer'],
    queryFn: async () => {
      const { data } = await api.get<Documento[]>('/rh/documentos/a-vencer?dias=30');
      return data;
    },
    enabled: tab === 'avencer',
  });

  const items = tab === 'vencidos' ? vencidos : tab === 'avencer' ? aVencer : todos;
  const isLoading = tab === 'todos' ? loadingTodos : false;

  return (
    <CrudLayout title="Documentos">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-6">
        <div className="flex gap-2">
        <button
          onClick={() => setTab('todos')}
          className={`px-4 py-2 rounded-lg ${
            tab === 'todos'
              ? 'bg-sky-500 text-white'
              : 'bg-slate-800 text-slate-400 hover:bg-slate-700'
          }`}
        >
          Todos
        </button>
        <button
          onClick={() => setTab('avencer')}
          className={`flex items-center gap-2 px-4 py-2 rounded-lg ${
            tab === 'avencer'
              ? 'bg-amber-500/30 text-amber-400'
              : 'bg-slate-800 text-slate-400 hover:bg-slate-700'
          }`}
        >
          <AlertTriangle className="w-4 h-4" /> A vencer (30 dias)
        </button>
        <button
          onClick={() => setTab('vencidos')}
          className={`flex items-center gap-2 px-4 py-2 rounded-lg ${
            tab === 'vencidos'
              ? 'bg-red-500/30 text-red-400'
              : 'bg-slate-800 text-slate-400 hover:bg-slate-700'
          }`}
        >
          <FileWarning className="w-4 h-4" /> Vencidos
        </button>
      </div>
      <button 
        onClick={() => setShowModal(true)}
        className="px-4 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 hover:from-blue-600 hover:to-indigo-700 text-white rounded-xl font-medium flex items-center justify-center gap-2 shadow-lg shadow-blue-500/20 transition-all border border-blue-500/50"
      >
        <Plus className="w-4 h-4" /> Novo Termo/Anexo
      </button>
    </div>
      <div className="rounded-xl border border-slate-700 overflow-hidden">
        <table className="w-full text-left">
          <thead className="bg-slate-800">
            <tr>
              <th className="px-4 py-3 text-slate-400 font-medium">Funcionário ID</th>
              <th className="px-4 py-3 text-slate-400 font-medium">Tipo</th>
              <th className="px-4 py-3 text-slate-400 font-medium">Arquivo</th>
              <th className="px-4 py-3 text-slate-400 font-medium">Validade</th>
              <th className="px-4 py-3 text-slate-400 font-medium">Status</th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.id} className="border-t border-slate-700 hover:bg-slate-800/50">
                <td className="px-4 py-3 text-white font-mono text-sm">{item.funcionarioId}</td>
                <td className="px-4 py-3 text-slate-400">{item.tipoDocumento}</td>
                <td className="px-4 py-3 text-slate-400">{item.nomeArquivo}</td>
                <td className="px-4 py-3 text-slate-400">
                  {item.dataValidade
                    ? format(new Date(item.dataValidade), 'dd/MM/yyyy', { locale: ptBR })
                    : '-'}
                </td>
                <td className="px-4 py-3">
                  <span
                    className={`px-2 py-1 rounded text-xs ${
                      item.status === 'VENCIDO'
                        ? 'bg-red-500/20 text-red-400'
                        : item.status === 'A_VENCER'
                          ? 'bg-amber-500/20 text-amber-400'
                          : 'bg-emerald-500/20 text-emerald-400'
                    }`}
                  >
                    {item.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {items.length === 0 && !isLoading && (
          <div className="py-12 text-center text-slate-500">Nenhum documento encontrado.</div>
        )}
      </div>

      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
          <div className="bg-slate-800 rounded-2xl border border-slate-700 shadow-2xl w-full max-w-md overflow-hidden flex flex-col">
            <div className="flex justify-between items-center p-6 border-b border-slate-700">
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                 <Upload className="w-5 h-5 text-blue-400" /> Upload de Documento
              </h3>
              <button onClick={() => setShowModal(false)} className="text-slate-400 hover:text-white transition-colors" type="button">
                <X className="w-5 h-5" />
              </button>
            </div>
            <form className="p-6 flex flex-col gap-4" onSubmit={handleUpload}>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Funcionário</label>
                <select required value={uploadData.funcionarioId} onChange={e => setUploadData({...uploadData, funcionarioId: e.target.value})} className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-blue-500">
                  <option value="" disabled>Selecione um colaborador</option>
                  {employees?.map(emp => (
                    <option key={emp.id} value={emp.id}>{emp.nome}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Tipo de Documento</label>
                <select required value={uploadData.tipoDocumento} onChange={e => setUploadData({...uploadData, tipoDocumento: e.target.value})} className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-blue-500">
                   <option value="RG">RG / CPF</option>
                   <option value="CNH">CNH</option>
                   <option value="COMPROVANTE_RESIDENCIA">Comprovante de Residência</option>
                   <option value="CONTRATO_TRABALHO">Contrato de Trabalho / Estágio</option>
                   <option value="CERTIFICADO_CURSO">Certificado / Treinamento</option>
                   <option value="ASO">ASO / Exame Médico</option>
                   <option value="OUTROS">Outros</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Validade (Opcional)</label>
                <input type="date" value={uploadData.dataValidade} onChange={e => setUploadData({...uploadData, dataValidade: e.target.value})} className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-blue-500 [color-scheme:dark]" />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Arquivo PDF/Imagem</label>
                <input required type="file" onChange={e => setSelectedFile(e.target.files?.[0] || null)} className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-sm text-slate-300 file:mr-4 file:py-2 file:px-4 file:rounded-xl file:border-0 file:text-sm file:font-bold file:bg-blue-500/20 file:text-blue-400 hover:file:bg-blue-500/30 transition-all focus:outline-none focus:border-blue-500" />
              </div>
              <button type="submit" disabled={uploadDoc.isPending} className="mt-4 w-full py-2.5 rounded-lg font-bold text-white bg-blue-500 hover:bg-blue-600 transition-colors flex justify-center items-center gap-2 disabled:opacity-50">
                {uploadDoc.isPending ? 'Enviando...' : 'Anexar Documento'}
              </button>
            </form>
          </div>
        </div>
      )}
    </CrudLayout>
  );
}
