package br.com.callcenter.modulorh.application;

import br.com.callcenter.modulorh.domain.entity.RegistroPonto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.StringJoiner;

/**
 * Gera o Arquivo Eletrônico de Ponto (AFD/AFDT) no layout texto da
 * Portaria 671/2021 MTE (anexo I - formato "texto"), permitindo
 * homologação com relógios de ponto físicos ou aplicativos SREP.
 *
 * Layout por linha (registro tipo "P"):
 *   P + dataDDMMYYYY + horaHHMMSS(entrada) + CR(limite) + nome + ...
 * Aqui utilizamos o layout simplificado amplamente aceito:
 *   P|data|funcionario|nome|matricula|CNPJ|entrada|intervaloIni|intervaloFim|saida|observacoes
 */
@Service
public class AfdService {

    private static final String TIPO_REGISTRO = "P";
    private static final String SEPARADOR = "|";

    /**
     * Monta o conteúdo AFD para um funcionário e período.
     * @param cnpjEmpresas CNPJ da empresa (sem máscara) informado pelo RH.
     */
    public String gerarAfd(List<RegistroPonto> registros, String nomeFuncionario,
                           String matricula, String cnpjEmpresa) {
        StringBuilder sb = new StringBuilder();
        // Header do arquivo (identificação do empregador)
        sb.append("A").append(SEPARADOR)
          .append("gestao-rh-financeiro").append(SEPARADOR)
          .append(cnpjEmpresa != null ? cnpjEmpresa.replaceAll("\\D", "") : "").append(SEPARADOR)
          .append(nomeFuncionario).append("\n");

        for (RegistroPonto rp : registros) {
            StringJoiner linha = new StringJoiner(SEPARADOR);
            linha.add(TIPO_REGISTRO);
            linha.add(formatData(rp.getData()));
            linha.add(formatHora(rp.getHoraEntrada()));
            linha.add(formatHora(rp.getHoraIntervaloInicio()));
            linha.add(formatHora(rp.getHoraIntervaloFim()));
            linha.add(formatHora(rp.getHoraSaida()));
            linha.add(matricula != null ? matricula : rp.getFuncionarioId().toString());
            linha.add(normalizarObservacao(rp.getObservacoes()));
            sb.append(linha).append("\n");
        }

        // Trailer: total de marcações
        long marcacoes = registros.stream()
                .mapToLong(r -> (r.getHoraEntrada() != null ? 1 : 0)
                        + (r.getHoraIntervaloInicio() != null ? 1 : 0)
                        + (r.getHoraIntervaloFim() != null ? 1 : 0)
                        + (r.getHoraSaida() != null ? 1 : 0))
                .sum();
        sb.append("T").append(SEPARADOR).append(marcacoes).append("\n");
        return sb.toString();
    }

    private String formatData(LocalDate data) {
        return data == null ? "" : String.format("%02d%02d%04d",
                data.getDayOfMonth(), data.getMonthValue(), data.getYear());
    }

    private String formatHora(java.time.LocalTime hora) {
        return hora == null ? "" : String.format("%02d%02d%02d",
                hora.getHour(), hora.getMinute(), hora.getSecond());
    }

    private String normalizarObservacao(String obs) {
        if (obs == null) {
            return "";
        }
        return obs.replace(SEPARADOR, "/").replace("\n", " ").trim();
    }
}
