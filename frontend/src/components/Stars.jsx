export default function Stars({ value }) {
  const n = Math.round(value || 0)
  return <span className="stars" aria-label={`별점 ${n}점`}>{'★'.repeat(n)}{'☆'.repeat(5 - n)}</span>
}
