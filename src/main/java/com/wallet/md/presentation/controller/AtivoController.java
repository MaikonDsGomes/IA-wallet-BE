package com.wallet.md.presentation.controller;

import com.wallet.md.application.dto.response.ApiResponse;
import com.wallet.md.application.dto.response.ativo.AtivoResponse;
import com.wallet.md.application.dto.response.ativo.CotacaoResponse;
import com.wallet.md.application.usecase.ativo.GetAtivosPorTicket;
import com.wallet.md.domain.model.enums.TipoAtivo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ativos")
@RequiredArgsConstructor
public class AtivoController {

    private final GetAtivosPorTicket getAtivosPorTicket;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AtivoResponse>>> listar(
            @RequestParam(required = false) TipoAtivo tipo,
            @RequestParam(required = false) String busca) {
        return ResponseEntity.ok(ApiResponse.success(getAtivosPorTicket.listar(tipo, busca)));
    }

    @GetMapping("/{simbolo}")
    public ResponseEntity<ApiResponse<AtivoResponse>> buscarPorSimbolo(
            @PathVariable String simbolo) {
        return ResponseEntity.ok(ApiResponse.success(getAtivosPorTicket.buscarPorCodigo(simbolo)));
    }

    @GetMapping("/{simbolo}/cotacoes")
    public ResponseEntity<ApiResponse<List<CotacaoResponse>>> listarCotacoes(
            @PathVariable String simbolo) {
        return ResponseEntity.ok(ApiResponse.success(getAtivosPorTicket.listarCotacoes(simbolo)));
    }
}
