import type { Game, Move, TurningPoint } from './types';

const base = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export class ApiRequestError extends Error {
  constructor(message: string, readonly status: number) { super(message); }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(base + path, { headers: { 'Content-Type': 'application/json' }, ...init });
  if (!response.ok) {
    const body = await response.json().catch(() => null);
    throw new ApiRequestError(body?.message ?? `Request failed (${response.status})`, response.status);
  }
  return response.json() as Promise<T>;
}

export const api = {
  games: () => request<Game[]>('/api/games'),
  create: (whitePlayer: string, blackPlayer: string, playerColor: 'WHITE'|'BLACK') => request<Game>('/api/games', { method: 'POST', body: JSON.stringify({ whitePlayer, blackPlayer, playerColor }) }),
  delete: (id: string) => request<void>(`/api/games/${id}`, { method: 'DELETE' }),
  moves: (id: string) => request<Move[]>(`/api/games/${id}/moves`),
  move: (id: string, uciMove: string) => request<Move>(`/api/games/${id}/moves`, { method: 'POST', body: JSON.stringify({ uciMove }) }),
  points: (id: string) => request<TurningPoint[]>(`/api/games/${id}/turning-points`)
};
