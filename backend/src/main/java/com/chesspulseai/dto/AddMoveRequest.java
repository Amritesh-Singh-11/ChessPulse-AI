package com.chesspulseai.dto; import jakarta.validation.constraints.*; public record AddMoveRequest(@NotBlank @Pattern(regexp="^[a-h][1-8][a-h][1-8][qrbnQRBN]?$") String uciMove){}
