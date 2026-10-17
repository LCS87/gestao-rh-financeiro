package br.com.callcenter.core.aspect;

import br.com.callcenter.core.annotation.Audited;
import br.com.callcenter.core.domain.entity.AuditoriaLog;
import br.com.callcenter.core.repository.AuditoriaLogRepository;
import br.com.callcenter.core.security.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditoriaAspect {

    private final AuditoriaLogRepository auditoriaLogRepository;

    @AfterReturning(pointcut = "@annotation(audited)", returning = "result")
    public void logAfter(JoinPoint joinPoint, Audited audited, Object result) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usuarioNome = "Sistema (Anonimo)";
            java.util.UUID usuarioId = null;

            if (authentication != null && authentication.getPrincipal() instanceof Usuario principal) {
                usuarioNome = principal.getUsername();
                usuarioId = principal.getId();
            }

            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                    .currentRequestAttributes()).getRequest();
            String ipAddress = request.getRemoteAddr();

            // Pega o parametro ID, se houver na rota
            String registroId = Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg instanceof java.util.UUID || arg instanceof String || arg instanceof Long)
                .map(Object::toString)
                .findFirst()
                .orElse("ID_NAO_IDENTIFICADO");

            String detalhes = "Metodo: " + joinPoint.getSignature().getName() + " -> " + request.getRequestURI();

            AuditoriaLog logEntry = AuditoriaLog.builder()
                    .usuarioId(usuarioId)
                    .usuarioNome(usuarioNome)
                    .acao(audited.acao())
                    .tabela(audited.tabela())
                    .registroId(registroId)
                    .ipAddress(ipAddress)
                    .detalhes(detalhes)
                    .build();

            auditoriaLogRepository.save(logEntry);
            log.info("Auditoria gravada com sucesso: [{}] em {}", audited.acao(), audited.tabela());

        } catch (Exception e) {
            log.error("Erro ao tentar gravar a Auditoria do AOP", e);
        }
    }
}
