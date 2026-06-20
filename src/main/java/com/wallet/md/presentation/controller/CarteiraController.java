package com.wallet.md.presentation.controller;

import com.wallet.md.application.dto.response.ApiResponse;
import com.wallet.md.application.dto.response.carteira.ScoreCarteiraResponse;
import com.wallet.md.application.usecase.carteira.GetScoreCarteiraUseCase;
import com.wallet.md.presentation.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/carteira")
@RequiredArgsConstructor
public class CarteiraController {

    private final GetScoreCarteiraUseCase getScoreCarteiraUseCase;

    @GetMapping("/score")
    public ResponseEntity<ApiResponse<ScoreCarteiraResponse>> getScore(
            @AuthenticationPrincipal UserDetails userDetails) {

        var usuarioId = AuthUtils.extractUserId(userDetails);
        var score = getScoreCarteiraUseCase.getScore(usuarioId);
        return ResponseEntity.ok(ApiResponse.success(score));
    }
}
