const BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080'

async function request(path, options = {}) {
  const res = await fetch(`${BASE}/api${path}`, {
    ...options,
    headers: { 'Content-Type': 'application/json', ...options.headers },
  })
  if (!res.ok) {
    const body = await res.json().catch(() => ({}))
    throw new Error(body.message || `요청 실패 (${res.status})`)
  }
  return res.status === 204 ? null : res.json()
}

export const listRestaurants = (keyword = '') =>
  request(`/restaurants${keyword ? `?keyword=${encodeURIComponent(keyword)}` : ''}`)

export const getRestaurant = (id) => request(`/restaurants/${id}`)

export const createRestaurant = (data) =>
  request('/restaurants', { method: 'POST', body: JSON.stringify(data) })

export const createReview = (id, data) =>
  request(`/restaurants/${id}/reviews`, { method: 'POST', body: JSON.stringify(data) })

export const deleteReview = (reviewId, password) =>
  request(`/reviews/${reviewId}`, { method: 'DELETE', headers: { 'X-Review-Password': password } })
