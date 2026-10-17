package br.com.callcenter.modulorh.api;

import br.com.callcenter.modulorh.application.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/rh/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/indicadores")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> indicadores() {
        return ResponseEntity.ok(dashboardService.obterIndicadores());
    }
}
