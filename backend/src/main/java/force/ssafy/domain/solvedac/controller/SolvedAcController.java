package force.ssafy.domain.solvedac.controller;

import force.ssafy.domain.solvedac.dto.request.VerificationCodeDto;
import force.ssafy.domain.solvedac.dto.response.VerificationCodeResponseDto;
import force.ssafy.domain.solvedac.dto.response.VerificationResultDto;
import force.ssafy.domain.solvedac.service.SolvedAcService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/solved-ac")
@RequiredArgsConstructor
public class SolvedAcController {

    private final SolvedAcService solvedAcService;

    /**
     * solved.ac 인증코드 발급 엔드포인트
     */
    @PostMapping("/verification-code")
    public ResponseEntity<VerificationCodeResponseDto> generateVerificationCode(
            @Valid @RequestBody VerificationCodeDto request) {
        VerificationCodeResponseDto response =
                solvedAcService.generateVerificationCode(request.getSolvedAcId());
        return ResponseEntity.ok(response);
    }

    /**
     * solved.ac 인증코드 검증 엔드포인트
     */
    @GetMapping("/verify/{solvedAcId}")
    public ResponseEntity<VerificationResultDto> verifyCode(
            @PathVariable String solvedAcId) {
        VerificationResultDto result = solvedAcService.verifyCode(solvedAcId);
        return ResponseEntity.ok(result);
    }
}