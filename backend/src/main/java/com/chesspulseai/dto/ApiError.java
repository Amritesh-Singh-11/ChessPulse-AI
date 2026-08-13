package com.chesspulseai.dto; import java.time.Instant; import java.util.*; public record ApiError(Instant timestamp,int status,String code,String message,Map<String,String> fieldErrors){}

