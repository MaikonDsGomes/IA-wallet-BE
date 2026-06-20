package com.wallet.md.presentation.controller;

import com.wallet.md.application.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/public")
@Tag(name = "Público", description = "Endpoints públicos sem autenticação")
public class PublicController {

    @GetMapping("/health")
    @Operation(summary = "Health check da aplicação")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        var data = Map.of(
                "status", "UP",
                "service", "InvestWallet API",
                "timestamp", LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/info")
    @Operation(summary = "Informações da API")
    public ResponseEntity<ApiResponse<Map<String, String>>> info() {
        var data = Map.of(
                "name", "InvestWallet API",
                "version", "1.0.0",
                "description", "API REST para sistema financeiro InvestWallet",
                "java", System.getProperty("java.version")
        );
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
