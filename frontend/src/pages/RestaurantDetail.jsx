import { useCallback, useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { createReview, deleteReview, getRestaurant } from '../api.js'
import Stars from '../components/Stars.jsx'

export default function RestaurantDetail() {
  const { id } = useParams()
  const [data, setData] = useState(null)
  const [error, setError] = useState('')
  const [form, setForm] = useState({ author: '', password: '', rating: 5, content: '' })
  const [formError, setFormError] = useState('')

  const load = useCallback(() => {
    getRestaurant(id).then(setData).catch((e) => setError(e.message))
  }, [id])

  useEffect(() => { load() }, [load])

  const submit = async (e) => {
    e.preventDefault()
    setFormError('')
    try {
      await createReview(id, { ...form, rating: Number(form.rating) })
      setForm({ author: '', password: '', rating: 5, content: '' })
      load()
    } catch (err) {
      setFormError(err.message)
    }
  }

  const remove = async (reviewId) => {
    const password = window.prompt('리뷰 작성 시 입력한 비밀번호를 입력하세요.')
    if (!password) return
    try {
      await deleteReview(reviewId, password)
      load()
    } catch (err) {
      alert(err.message)
    }
  }

  if (error) return <p className="error">{error}</p>
  if (!data) return <p className="muted">불러오는 중...</p>

  return (
    <>
      <Link to="/" className="muted">← 목록으로</Link>
      <section className="detail">
        <h2>{data.name} <span className="tag">{data.category}</span></h2>
        <p>📍 {data.address}</p>
        {data.phone && <p>📞 {data.phone}</p>}
        {data.description && <p className="desc">{data.description}</p>}
        <p>
          <Stars value={data.averageRating} />{' '}
          <span className="muted">{data.averageRating ? data.averageRating.toFixed(1) : '-'} · 리뷰 {data.reviews.length}</span>
        </p>
      </section>

      <section>
        <h3>리뷰 작성</h3>
        <form className="form" onSubmit={submit}>
          <div className="row">
            <input required maxLength={50} placeholder="닉네임" value={form.author}
                   onChange={(e) => setForm({ ...form, author: e.target.value })} />
            <input required type="password" minLength={4} maxLength={50} placeholder="삭제용 비밀번호 (4자 이상)"
                   value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
            <select value={form.rating} onChange={(e) => setForm({ ...form, rating: e.target.value })}>
              {[5, 4, 3, 2, 1].map((n) => <option key={n} value={n}>{'★'.repeat(n)} ({n})</option>)}
            </select>
          </div>
          <textarea required rows={3} maxLength={1000} placeholder="리뷰 내용" value={form.content}
                    onChange={(e) => setForm({ ...form, content: e.target.value })} />
          {formError && <p className="error">{formError}</p>}
          <button className="btn">리뷰 등록</button>
        </form>
      </section>

      <section>
        <h3>리뷰 {data.reviews.length}개</h3>
        {data.reviews.length === 0 && <p className="muted">아직 리뷰가 없습니다.</p>}
        <ul className="reviews">
          {data.reviews.map((r) => (
            <li key={r.id}>
              <div className="review-head">
                <strong>{r.author}</strong> <Stars value={r.rating} />
                <span className="muted">{new Date(r.createdAt).toLocaleDateString('ko-KR')}</span>
                <button className="link-btn" onClick={() => remove(r.id)}>삭제</button>
              </div>
              <p>{r.content}</p>
            </li>
          ))}
        </ul>
      </section>
    </>
  )
}
