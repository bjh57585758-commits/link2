import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { listRestaurants } from '../api.js'
import Stars from '../components/Stars.jsx'
import { emojiOf } from '../components/categoryEmoji.js'

export default function RestaurantList() {
  const [items, setItems] = useState([])
  const [keyword, setKeyword] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const fetchList = (kw) =>
    listRestaurants(kw)
      .then(setItems)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))

  useEffect(() => { fetchList('') }, [])

  const search = (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    fetchList(keyword)
  }

  return (
    <>
      <section className="hero">
        <h1>밀양의 맛집을 찾아보세요</h1>
        <p>직접 다녀온 식당을 등록하고, 솔직한 리뷰를 남겨 보세요.</p>
        <form className="search" onSubmit={search}>
          <input value={keyword} onChange={(e) => setKeyword(e.target.value)} placeholder="식당 이름 또는 종류 검색 (예: 국밥, 한식)" />
          <button className="btn">검색</button>
        </form>
      </section>

      {loading && <p className="notice">불러오는 중... 서버가 잠들어 있으면 최대 1분 걸릴 수 있어요.</p>}
      {error && <p className="notice error">{error}</p>}
      {!loading && !error && items.length === 0 && (
        <div className="empty">
          <div className="empty-icon">🍚</div>
          <p>등록된 식당이 없습니다. 첫 식당을 등록해 보세요!</p>
          <Link to="/restaurants/new" className="btn">식당 등록하기</Link>
        </div>
      )}
      {!loading && !error && items.length > 0 && <p className="count">식당 {items.length}곳</p>}
      <ul className="cards">
        {items.map((r) => (
          <li key={r.id}>
            <Link to={`/restaurants/${r.id}`} className="card">
              <div className="card-icon">{emojiOf(r.category)}</div>
              <div className="card-body">
                <h3>{r.name} <span className="tag">{r.category}</span></h3>
                <p className="muted">📍 {r.address}</p>
                <p className="rating">
                  <Stars value={r.averageRating} />
                  <span className="muted">
                    {r.averageRating ? r.averageRating.toFixed(1) : '평가 없음'} · 리뷰 {r.reviewCount}개
                  </span>
                </p>
              </div>
            </Link>
          </li>
        ))}
      </ul>
    </>
  )
}
