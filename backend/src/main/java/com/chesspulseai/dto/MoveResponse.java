package com.chesspulseai.dto; import com.chesspulseai.enumtype.PlayerColor; public record MoveResponse(int moveNumber,String san,String fen,PlayerColor playerColor,AnalysisResponse analysis){}
