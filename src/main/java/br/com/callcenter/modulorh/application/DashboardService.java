package br.com.callcenter.modulorh.application;

import br.com.callcenter.modulorh.domain.entity.Funcionario;
import br.com.callcenter.modulorh.infrastructure.repository.DemissaoRepository;
import br.com.callcenter.modulorh.infrastructure.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final FuncionarioRepository funcionarioRepository;
    private final DemissaoRepository demissaoRepository;
    private final DocumentoService documentoService;

    public DashboardService(FuncionarioRepository funcionarioRepository,
                            DemissaoRepository demissaoRepository,
                            DocumentoService documentoService) {
        this.funcionarioRepository = funcionarioRepository;
        this.demissaoRepository = demissaoRepository;
        this.documentoService = documentoService;
    }

    public Map<String, Object> obterIndicadores() {
        Map<String, Object> map = new HashMap<>();
        List<Funcionario> todos = funcionarioRepository.findAll();

        long ativos = todos.stream().filter(f -> f.getStatus() == Funcionario.StatusFuncionario.ATIVO).count();
        long inativos = todos.stream().filter(f -> f.getStatus() == Funcionario.StatusFuncionario.INATIVO).count();
        long ferias = todos.stream().filter(f -> f.getStatus() == Funcionario.StatusFuncionario.FERIAS).count();
        long afastados = todos.stream().filter(f -> f.getStatus() == Funcionario.StatusFuncionario.AFASTADO).count();

        map.put("headcount", (int) ativos);
        map.put("totalFuncionarios", todos.size());
        map.put("inativos", (int) inativos);
        map.put("emFerias", (int) ferias);
        map.put("afastados", (int) afastados);
        map.put("demissoesMes", demissaoRepository.count());
        map.put("documentosVencidos", documentoService.buscarDocumentosVencidos().size());
        map.put("documentosAVencer", documentoService.buscarDocumentosAVencer(30).size());

        return map;
    }
}
