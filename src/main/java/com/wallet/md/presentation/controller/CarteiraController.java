package com.wallet.md.presentation.controller;

import com.wallet.md.application.dto.request.carteira.DeletePosicaoRequest;
import com.wallet.md.application.dto.request.carteira.PostPosicaoRequest;
import com.wallet.md.application.dto.response.ApiResponse;
import com.wallet.md.application.dto.response.carteira.PosicaoResponse;
import com.wallet.md.application.dto.response.carteira.RecomendacaoResponse;
import com.wallet.md.application.dto.response.carteira.ScoreCarteiraResponse;
import com.wallet.md.application.usecase.carteira.DeletePosicaoUseCase;
import com.wallet.md.application.usecase.carteira.GetRecomendacoesCarteiraUseCase;
import com.wallet.md.application.usecase.carteira.GetPosicoesCarteiraUseCase;
import com.wallet.md.application.usecase.carteira.GetScoreCarteiraUseCase;
import com.wallet.md.application.usecase.carteira.PostPosicaoUseCase;
import com.wallet.md.application.usecase.carteira.SolicitarAtualizacaoRecomendacoesUseCase;
import com.wallet.md.domain.model.enums.TipoAtivo;
import com.wallet.md.presentation.utils.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carteira")
@RequiredArgsConstructor
public class CarteiraController {

    private final GetScoreCarteiraUseCase getScoreCarteiraUseCase;
    private final GetPosicoesCarteiraUseCase getPosicoesCarteiraUseCase;
    private final PostPosicaoUseCase postPosicaoUseCase;
    private final DeletePosicaoUseCase deletePosicaoUseCase;
    private final GetRecomendacoesCarteiraUseCase getRecomendacoesCarteiraUseCase;
    private final SolicitarAtualizacaoRecomendacoesUseCase solicitarAtualizacaoRecomendacoesUseCase;

    @GetMapping("/score")
    public ResponseEntity<ApiResponse<ScoreCarteiraResponse>> getScore(
            @AuthenticationPrincipal UserDetails userDetails) {

        var usuarioId = AuthUtils.extractUserId(userDetails);
        var score = getScoreCarteiraUseCase.getScore(usuarioId);
        return ResponseEntity.ok(ApiResponse.success(score));
    }

    @GetMapping("/recomendacoes")
    public ResponseEntity<ApiResponse<List<RecomendacaoResponse>>> getRecomendacoes(
            @AuthenticationPrincipal UserDetails userDetails) {
        var usuarioId = AuthUtils.extractUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(getRecomendacoesCarteiraUseCase.getRecomendacoes(usuarioId)));
    }

    @PostMapping("/recomendacoes/atualizacao")
    public ResponseEntity<ApiResponse<Void>> solicitarAtualizacao(
            @AuthenticationPrincipal UserDetails userDetails) {
        var usuarioId = AuthUtils.extractUserId(userDetails);
        solicitarAtualizacaoRecomendacoesUseCase.solicitar(usuarioId);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success("Atualização de recomendações agendada", null));
    }

    @PostMapping("/posicoes")
    public ResponseEntity<ApiResponse<Void>> adicionarPosicao(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid PostPosicaoRequest request) {

        var usuarioId = AuthUtils.extractUserId(userDetails);
        postPosicaoUseCase.post(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
    }

    @GetMapping("/posicoes")
    public ResponseEntity<ApiResponse<List<PosicaoResponse>>> getPosicoes(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) TipoAtivo tipo) {

        var usuarioId = AuthUtils.extractUserId(userDetails);
        var posicoes = getPosicoesCarteiraUseCase.getDadosPosicao(usuarioId, tipo);
        return ResponseEntity.ok(ApiResponse.success(posicoes));
    }

    @DeleteMapping("/posicoes")
    public ResponseEntity<ApiResponse<Void>> deletarPosicao(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid DeletePosicaoRequest request) {
        Long usuarioId = AuthUtils.extractUserId(userDetails);
        deletePosicaoUseCase.delete(request, usuarioId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));
    }


}
