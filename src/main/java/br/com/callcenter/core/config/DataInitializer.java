package br.com.callcenter.core.config;

import br.com.callcenter.core.security.Role;
import br.com.callcenter.core.security.Usuario;
import br.com.callcenter.core.security.UsuarioRepository;
import br.com.callcenter.core.repository.ParametroSistemaRepository;
import br.com.callcenter.core.domain.entity.ParametroSistema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

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

            // 2. Criar TI (Controle Total)
            seedUser(usuarioRepository, passwordEncoder, "ti", Role.ROLE_TI, "admin123");

            // 3. Criar Admins
            seedUser(usuarioRepository, passwordEncoder, "admin", Role.ROLE_ADMIN, "admin123");
            seedUser(usuarioRepository, passwordEncoder, "admin2", Role.ROLE_ADMIN, "admin123");

            // 4. Criar Supervisor
            seedUser(usuarioRepository, passwordEncoder, "supervisor", Role.ROLE_SUPERVISOR, "admin123");

            // 5. Criar Usuários Comuns
            for (int i = 1; i <= 7; i++) {
                seedUser(usuarioRepository, passwordEncoder, "user" + i, Role.ROLE_VISUALIZADOR, "admin123");
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
                // Forçar reset de senha para garantir acesso nos testes
                String encodedPassword = encoder.encode(password);
                user.setPassword(encodedPassword);
                changed = true;
                
                if (changed) {
                    repository.save(user);
                    log.info("Usuário '{}' atualizado (Role: {}, Senha resetada)", username, role);
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
}

