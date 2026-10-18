import { http, HttpResponse } from 'msw';

export const API_BASE = '/api';

const regrasFixtures = [
  {
    id: 'r1',
    nome: 'Padrão Vendas',
    descricao: 'Comissão padrão sobre vendas',
    tipoComissao: 'PORCENTAGEM_VENDA',
    porcentagem: 5,
    valorFixo: null,
    valorMinimo: null,
    valorMaximo: null,
    aceleradorPercentual: 20,
    metaMinimaPercentual: 100,
    aplicarPorDepartamento: false,
    ativo: true,
  },
];

export const handlers = [
  http.get(`${API_BASE}/comissoes/regras`, () => HttpResponse.json(regrasFixtures)),

  http.get(`${API_BASE}/comissoes/apuracao`, () =>
    HttpResponse.json({
      mesReferencia: '2026-10',
      totalComissoes: 0,
      itens: [],
    }),
  ),

  http.post(`${API_BASE}/comissoes/regras`, async ({ request }) => {
    const body = (await request.json()) as Record<string, unknown>;
    return HttpResponse.json({ ...regrasFixtures[0], ...body, id: 'r-new' }, { status: 201 });
  }),

  http.post(`${API_BASE}/comissoes/regras/:id/simular`, async ({ request }) => {
    const body = (await request.json()) as { valorVenda?: number };
    // 5% de comissao + acelerador de 20% quando a meta e batida
    const valor = ((Number(body.valorVenda) || 0) * 0.05 * 1.2).toFixed(2);
    return HttpResponse.json({ valorComissao: Number(valor) });
  }),
];
