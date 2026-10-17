import { useQuery } from '@tanstack/react-query';
import { BookOpen, Clock, Heart, MessageCircle, Plus, Shield, Trophy, X } from 'lucide-react';
import { useState } from 'react';
import CrudLayout from '../components/CrudLayout';
import { useAuth } from '../contexts/AuthContext';
import { api } from '../lib/api';

interface Postagem {
  id: string;
  autorNome: string;
  conteudo: string;
  tipo: string;
  visibilidade: string;
  fixada: boolean;
  ativa: boolean;
  comentarios: number;
  curtidas: number;
  dataPublicacao: string;
}

interface Enquete {
  id: string;
  pergunta: string;
  autorNome: string;
  anonima: boolean;
  expiracao: string;
  votos: number;
}

interface Reconhecimento {
  id: string;
  remetenteNome: string;
  destinatarioNome: string;
  tipo: string;
  mensagem: string;
  publico: boolean;
  data: string;
}

export default function Mural() {
  const { hasPermission } = useAuth();
  const [tab, setTab] = useState<'postagens' | 'enquetes' | 'reconhecimentos'>('postagens');
  const [showNovaPostagemModal, setShowNovaPostagemModal] = useState(false);

  const { data: postagens } = useQuery({
    queryKey: ['mural', 'postagens'],
    queryFn: async () => {
      const { data } = await api.get<Postagem[]>('/api/engajamento/postagens');
      return data;
    },
  });

  const { data: enquetes } = useQuery({
    queryKey: ['mural', 'enquetes'],
    queryFn: async () => {
      const { data } = await api.get<Enquete[]>('/api/engajamento/enquetes');
      return data;
    },
  });

  const { data: reconhecimentos } = useQuery({
    queryKey: ['mural', 'reconhecimentos'],
    queryFn: async () => {
      const { data } = await api.get<Reconhecimento[]>('/api/engajamento/reconhecimentos');
      return data;
    },
  });

  if (!hasPermission('user')) {
    return (
      <div className="flex items-center justify-center p-12">
        <div className="text-center space-y-4">
          <Shield className="w-16 h-16 text-slate-600 mx-auto" />
          <h2 className="text-2xl font-bold text-white">Acesso Restrito</h2>
          <p className="text-slate-400">Esta área requer privilégios de usuário.</p>
        </div>
      </div>
    );
  }

  return (
    <CrudLayout title="Mural Digital">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-6">
        <div className="flex gap-2">
          <button
            onClick={() => setTab('postagens')}
            className={`px-4 py-2 rounded-lg ${tab === 'postagens'
              ? 'bg-blue-500 text-white'
              : 'bg-slate-800 text-slate-400 hover:bg-slate-700'
              }`}
          >
            <BookOpen className="w-4 h-4 inline mr-2" />
            Postagens
          </button>
          <button
            onClick={() => setTab('enquetes')}
            className={`px-4 py-2 rounded-lg ${tab === 'enquetes'
              ? 'bg-purple-500 text-white'
              : 'bg-slate-800 text-slate-400 hover:bg-slate-700'
              }`}
          >
            <Trophy className="w-4 h-4 inline mr-2" />
            Enquetes
          </button>
          <button
            onClick={() => setTab('reconhecimentos')}
            className={`px-4 py-2 rounded-lg ${tab === 'reconhecimentos'
              ? 'bg-pink-500 text-white'
              : 'bg-slate-800 text-slate-400 hover:bg-slate-700'
              }`}
          >
            <Trophy className="w-4 h-4 inline mr-2" />
            Reconhecimentos
          </button>
        </div>
        <button
          onClick={() => setShowNovaPostagemModal(true)}
          className="px-4 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 hover:from-blue-600 hover:to-indigo-700 text-white rounded-xl font-medium flex items-center justify-center gap-2 shadow-lg shadow-blue-500/20 transition-all"
        >
          <Plus className="w-4 h-4" /> Nova Postagem
        </button>
      </div>

      <div className="bg-slate-800 rounded-2xl border border-slate-700 overflow-hidden shadow-xl">
        <div className="p-6 border-b border-slate-700 bg-slate-800/50">
          <h3 className="text-lg font-bold text-white mb-4">
            {tab === 'postagens' && 'Postagens Recentes'}
            {tab === 'enquetes' && 'Enquetes Ativas'}
            {tab === 'reconhecimentos' && 'Reconhecimentos Públicos'}
          </h3>

          {tab === 'postagens' && (
            <div className="space-y-4">
              {postagens?.map((post) => (
                <div key={post.id} className="bg-slate-700/30 rounded-xl p-4 border border-slate-600/30 hover:border-blue-500/30 transition-all">
                  <div className="flex justify-between items-start mb-2">
                    <div className="flex items-center gap-2">
                      <div className="w-10 h-10 rounded-full bg-blue-500/20 flex items-center justify-center text-blue-400 font-bold">
                        {post.autorNome.charAt(0)}
                      </div>
                      <div>
                        <div className="font-bold text-white text-sm">{post.autorNome}</div>
                        <div className="text-xs text-slate-500 flex items-center gap-1">
                          <Clock className="w-3 h-3" />
                          {new Date(post.dataPublicacao).toLocaleDateString('pt-BR')}
                        </div>
                      </div>
                    </div>
                    <span className="text-xs font-bold text-slate-400 uppercase">{post.tipo}</span>
                  </div>
                  <div className="text-slate-300 text-sm mb-3">{post.conteudo}</div>
                  <div className="flex items-center gap-4 text-sm text-slate-400">
                    <button className="flex items-center gap-1 hover:text-blue-400 transition-colors">
                      <Heart className="w-4 h-4" /> {post.curtidas} curtidas
                    </button>
                    <button className="flex items-center gap-1 hover:text-blue-400 transition-colors">
                      <MessageCircle className="w-4 h-4" /> {post.comentarios} comentários
                    </button>
                  </div>
                </div>
              ))}
              {postagens?.length === 0 && (
                <div className="text-center py-8 text-slate-500">
                  Nenhuma postagem encontrada.
                </div>
              )}
            </div>
          )}

          {tab === 'enquetes' && (
            <div className="space-y-4">
              {enquetes?.map((enquete) => (
                <div key={enquete.id} className="bg-slate-700/30 rounded-xl p-4 border border-slate-600/30 hover:border-purple-500/30 transition-all">
                  <div className="flex justify-between items-start mb-2">
                    <div className="flex items-center gap-2">
                      <div className="w-8 h-8 rounded-full bg-purple-500/20 flex items-center justify-center text-purple-400 font-bold">
                        {enquete.autorNome.charAt(0)}
                      </div>
                      <div>
                        <div className="font-bold text-white text-sm">{enquete.autorNome}</div>
                        <div className="text-xs text-slate-500">Enquete{enquete.anonima ? ' (Anônima)' : ''}</div>
                      </div>
                    </div>
                  </div>
                  <div className="text-slate-300 text-sm mb-3">{enquete.pergunta}</div>
                  <div className="text-xs text-slate-500">
                    Total de votos: {enquete.votos}
                  </div>
                </div>
              ))}
              {enquetes?.length === 0 && (
                <div className="text-center py-8 text-slate-500">
                  Nenhuma enquete encontrada.
                </div>
              )}
            </div>
          )}

          {tab === 'reconhecimentos' && (
            <div className="space-y-4">
              {reconhecimentos?.map((rec) => (
                <div key={rec.id} className="bg-slate-700/30 rounded-xl p-4 border border-slate-600/30 hover:border-pink-500/30 transition-all">
                  <div className="flex items-center gap-2 mb-2">
                    <Trophy className="w-5 h-5 text-pink-400" />
                    <span className="font-bold text-white text-sm">
                      {rec.remetenteNome} reconheceu {rec.destinatarioNome}
                    </span>
                  </div>
                  <div className="text-slate-300 text-sm mb-2">"{rec.mensagem}"</div>
                  <div className="text-xs text-slate-500">
                    {rec.tipo} • {rec.publico ? 'Público' : 'Privado'}
                  </div>
                </div>
              ))}
              {reconhecimentos?.length === 0 && (
                <div className="text-center py-8 text-slate-500">
                  Nenhum reconhecimento encontrado.
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      {/* Modal Nova Postagem */}
      {showNovaPostagemModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
          <div className="bg-slate-800 rounded-2xl border border-slate-700 shadow-2xl w-full max-w-lg overflow-hidden flex flex-col">
            <div className="flex justify-between items-center p-6 border-b border-slate-700">
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <BookOpen className="w-5 h-5 text-blue-400" /> Nova Postagem
              </h3>
              <button onClick={() => setShowNovaPostagemModal(false)} className="text-slate-400 hover:text-white" type="button">
                <X className="w-5 h-5" />
              </button>
            </div>
            <form className="p-6 flex flex-col gap-4" onSubmit={(e) => e.preventDefault()}>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Tipo de Postagem</label>
                <select className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-blue-500">
                  <option value="PUBLICACAO">Publicação Normal</option>
                  <option value="ANUNCIO">Anúncio Importante</option>
                  <option value="NOTICIA">Notícia da Empresa</option>
                  <option value="EVENTO">Evento</option>
                  <option value="DICA">Dica</option>
                  <option value="RECONHECIMENTO">Reconhecimento</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Conteúdo</label>
                <textarea required rows={4} placeholder="Escreva sua mensagem..." className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-blue-500" />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Visibilidade</label>
                <select className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-blue-500">
                  <option value="PUBLICO">Público (Todos)</option>
                  <option value="DEPARTAMENTO">Departamento</option>
                  <option value="EQUIPE">Equipe</option>
                </select>
              </div>
              <button type="submit" className="mt-4 w-full py-2.5 rounded-lg font-bold text-white bg-blue-500 hover:bg-blue-600 transition-colors flex justify-center items-center gap-2">
                Publicar
              </button>
            </form>
          </div>
        </div>
      )}
    </CrudLayout>
  );
}
