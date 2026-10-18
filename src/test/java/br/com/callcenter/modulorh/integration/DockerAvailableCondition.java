package br.com.callcenter.modulorh.integration;

import java.util.logging.Logger;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.DockerClientFactory;

/**
 * Pula os testes de integracao quando nao ha Docker disponivel no ambiente,
 * evitando falhas em maquinas sem daemon (ex.: builds locais minimos).
 */
public class DockerAvailableCondition implements ExecutionCondition {

    private static final Logger LOG = Logger.getLogger(DockerAvailableCondition.class.getName());
    private static Boolean cached;

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if (cached == null) {
            try {
                cached = DockerClientFactory.instance().isDockerAvailable();
            } catch (Throwable t) {
                cached = false;
            }
        }
        if (Boolean.TRUE.equals(cached)) {
            return ConditionEvaluationResult.enabled("Docker disponivel");
        }
        LOG.info("Docker indisponivel - pulando teste de integracao " + context.getDisplayName());
        return ConditionEvaluationResult.disabled("Docker indisponivel");
    }
}
