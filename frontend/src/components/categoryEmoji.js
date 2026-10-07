const EMOJI = { 한식: '🍲', 중식: '🥟', 일식: '🍣', 양식: '🍝', 분식: '🍢', 카페: '☕', 기타: '🍽️' }

export const emojiOf = (category) => EMOJI[category] || '🍽️'
