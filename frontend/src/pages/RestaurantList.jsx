import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { listRestaurants } from '../api.js'
import Stars from '../components/Stars.jsx'

export default function RestaurantList() {
  const [items, setItems] = useState([])
  const [keyword, setKeyword] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = (kw) => {
    setLoading(true)
    setError('')
    listRestaurants(kw)
      .then(setItems)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => { load('') }, [])

  return (
    <>
      <form className="search" onSubmit={(e) => { e.preventDefault(); load(keyword) }}>
        <input value={keyword} onChange={(e) => setKeyword(e.target.value)} placeholder="식당 이름 또는 종류 검색" />
        <button className="btn">검색</button>
      </form>
      {loading && <p className="muted">불러오는 중... (서버가 잠들어 있으면 최대 1분 걸릴 수 있어요)</p>}
      {error && <p className="error">{error}</p>}
      {!loading && !error && items.length === 0 && <p className="muted">등록된 식당이 없습니다. 첫 식당을 등록해 보세요!</p>}
      <ul className="cards">
        {items.map((r) => (
          <li key={r.id}>
            <Link to={`/restaurants/${r.id}`} className="card">
              <h3>{r.name} <span className="tag">{r.category}</span></h3>
              <p className="muted">{r.address}</p>
              <p>
                <Stars value={r.averageRating} />{' '}
                <span className="muted">
                  {r.averageRating ? r.averageRating.toFixed(1) : '-'} · 리뷰 {r.reviewCount}
                </span>
              </p>
            </Link>
          </li>
        ))}
      </ul>
    </>
  )
}
