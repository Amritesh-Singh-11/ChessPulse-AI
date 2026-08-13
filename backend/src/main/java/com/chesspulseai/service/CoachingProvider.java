package com.chesspulseai.service;
/** Boundary for optional external coaching providers. Implementations must derive content from supplied engine data. */
public interface CoachingProvider { String coach(String deterministicExplanation, String coachingLevel); }
