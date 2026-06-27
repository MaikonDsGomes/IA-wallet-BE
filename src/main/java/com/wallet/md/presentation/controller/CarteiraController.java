package com.wallet.md.presentation.controller;

import com.wallet.md.application.dto.request.carteira.AdicionarPosicaoRequest;
import com.wallet.md.application.dto.response.ApiResponse;
import com.wallet.md.application.dto.response.carteira.PosicaoResponse;
import com.wallet.md.application.dto.response.carteira.ScoreCarteiraResponse;
import com.wallet.md.application.usecase.carteira.GetPosicoesCarteiraUseCase;
import com.wallet.md.application.usecase.carteira.GetScoreCarteiraUseCase;
import com.wallet.md.application.usecase.carteira.PostPosicaoUseCase;
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

    @GetMapping("/score")
    public ResponseEntity<ApiResponse<ScoreCarteiraResponse>> getScore(
            @AuthenticationPrincipal UserDetails userDetails) {

        var usuarioId = AuthUtils.extractUserId(userDetails);
        var score = getScoreCarteiraUseCase.getScore(usuarioId);
        return ResponseEntity.ok(ApiResponse.success(score));
    }

    @PostMapping("/posicoes")
    public ResponseEntity<ApiResponse<Void>> adicionarPosicao(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid AdicionarPosicaoRequest request) {

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


}
