import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { createRestaurant } from '../api.js'

const CATEGORIES = ['한식', '중식', '일식', '양식', '분식', '카페', '기타']

export default function RestaurantForm() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ name: '', category: '한식', address: '밀양시 ', phone: '', description: '' })
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setSubmitting(true)
    setError('')
    try {
      const created = await createRestaurant(form)
      navigate(`/restaurants/${created.id}`)
    } catch (err) {
      setError(err.message)
      setSubmitting(false)
    }
  }

  return (
    <form className="form" onSubmit={submit}>
      <h2>식당 등록</h2>
      <label>식당 이름 *<input required maxLength={100} value={form.name} onChange={set('name')} /></label>
      <label>종류 *
        <select value={form.category} onChange={set('category')}>
          {CATEGORIES.map((c) => <option key={c}>{c}</option>)}
        </select>
      </label>
      <label>주소 *<input required maxLength={200} value={form.address} onChange={set('address')} /></label>
      <label>전화번호<input maxLength={30} value={form.phone} onChange={set('phone')} /></label>
      <label>소개<textarea rows={4} maxLength={1000} value={form.description} onChange={set('description')} /></label>
      {error && <p className="error">{error}</p>}
      <button className="btn" disabled={submitting}>{submitting ? '등록 중...' : '등록하기'}</button>
    </form>
  )
}
