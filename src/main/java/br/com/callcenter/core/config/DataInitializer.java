package br.com.callcenter.core.config;

import br.com.callcenter.core.security.Role;
import br.com.callcenter.core.security.Usuario;
import br.com.callcenter.core.security.UsuarioRepository;
import br.com.callcenter.core.repository.ParametroSistemaRepository;
import br.com.callcenter.core.domain.entity.ParametroSistema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    /**
     * Senha padrão do seed. Em produção, defina SEED_DEFAULT_PASSWORD obrigatoriamente
     * (ou use as senhas específicas por papel) — o app recusa defaults em prod.
     */
    @Value("${app.seed.default-password:#{null}}")
    private String seedDefaultPassword;

    @Value("${app.seed.rh-password:#{null}}")
    private String seedRhPassword;

    @Value("${app.seed.financeiro-password:#{null}}")
    private String seedFinanceiroPassword;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    public CommandLineRunner createDefaultUsers(UsuarioRepository usuarioRepository,
                                                PasswordEncoder passwordEncoder,
                                                br.com.callcenter.modulorh.infrastructure.repository.DepartamentoRepository deptRepository,
                                                ParametroSistemaRepository parametroRepository) {
        return args -> {
            // 0. Inicializar Parâmetros do Sistema (Chaves Padrões)
            if (parametroRepository.count() == 0) {
                // Tema
                ParametroSistema theme = new ParametroSistema();
                theme.setChave("theme");
                theme.setValor("Dark Mode (Padrão)");
                theme.setDescricao("Tema padrão do sistema");
                parametroRepository.save(theme);

                // Meta Global
                ParametroSistema globalGoal = new ParametroSistema();
                globalGoal.setChave("globalGoal");
                globalGoal.setValor("150000");
                globalGoal.setDescricao("Meta de Vendas Diária Global");
                parametroRepository.save(globalGoal);
                
                // Allow Late Sales
                ParametroSistema lateSales = new ParametroSistema();
                lateSales.setChave("allowLateSales");
                lateSales.setValor("false");
                lateSales.setDescricao("Permitir Vendas Retroativas");
                parametroRepository.save(lateSales);
                
                log.info("Parâmetros do sistema iniciais criados");
            }

            // 1. Criar Departamentos Predefinidos
            String[] depts = {"Comercial", "TI", "BKO", "Renovação", "Pos-Venda", "Auditoria", "RH", "Financeiro", "Logistica"};
            for (String nome : depts) {
                if (!deptRepository.findByNome(nome).isPresent()) {
                    br.com.callcenter.modulorh.domain.entity.Departamento d = new br.com.callcenter.modulorh.domain.entity.Departamento();
                    d.setNome(nome);
                    d.setDescricao("Departamento de " + nome);
                    deptRepository.save(d);
                    log.info("Departamento '{}' criado", nome);
                }
            }

            // 2. Senha do seed: default apenas em dev; obrigatoria em producao
            String defaultPassword = seedDefaultPassword != null ? seedDefaultPassword : "admin123";
            if (isProducao() && seedDefaultPassword == null) {
                throw new IllegalStateException(
                    "Em producao e obrigatorio definir app.seed.default-password (SEED_DEFAULT_PASSWORD). "
                    + "Seed com senha padrao 'admin123' nao e permitido.");
            }

            // 3. Criar TI (Controle Total)
            seedUser(usuarioRepository, passwordEncoder, "ti", Role.ROLE_TI, defaultPassword);

            // 4. Criar Admins
            seedUser(usuarioRepository, passwordEncoder, "admin", Role.ROLE_ADMIN, defaultPassword);
            seedUser(usuarioRepository, passwordEncoder, "admin2", Role.ROLE_ADMIN, defaultPassword);

            // 5. Criar Supervisor
            seedUser(usuarioRepository, passwordEncoder, "supervisor", Role.ROLE_SUPERVISOR, defaultPassword);

            // 6. Papeis de negocio RH e FINANCEIRO (faltavam no seed original)
            seedUser(usuarioRepository, passwordEncoder, "rh", Role.ROLE_RH,
                    seedRhPassword != null ? seedRhPassword : defaultPassword);
            seedUser(usuarioRepository, passwordEncoder, "financeiro", Role.ROLE_FINANCEIRO,
                    seedFinanceiroPassword != null ? seedFinanceiroPassword : defaultPassword);

            // 7. Criar Usuarios Comuns
            for (int i = 1; i <= 7; i++) {
                seedUser(usuarioRepository, passwordEncoder, "user" + i, Role.ROLE_VISUALIZADOR, defaultPassword);
            }
        };
    }

    private void seedUser(UsuarioRepository repository, PasswordEncoder encoder, String username, Role role, String password) {
        repository.findByUsername(username).ifPresentOrElse(
            user -> {
                boolean changed = false;
                if (user.getRole() != role) {
                    user.setRole(role);
                    changed = true;
                }
                // Em dev, reseta a senha para garantir acesso; em producao nunca reseta senhas existentes
                if (!isProducao()) {
                    user.setPassword(encoder.encode(password));
                    changed = true;
                }
                
                if (changed) {
                    repository.save(user);
                    log.info("Usuário '{}' atualizado (Role: {})", username, role);
                }
            },
            () -> {
                Usuario user = Usuario.builder()
                        .username(username)
                        .password(encoder.encode(password))
                        .role(role)
                        .enabled(true)
                        .build();
                repository.save(user);
                log.info("Usuário '{}' criado com role {}", username, role);
            }
        );
    }

    private boolean isProducao() {
        return "prod".equalsIgnoreCase(activeProfile) || "production".equalsIgnoreCase(activeProfile);
    }
}
