import { useCallback, useEffect, useState } from 'react'
import { answerFaq, createFaq, deleteFaq, listFaqs } from '../api.js'

const EMPTY = { author: '', password: '', title: '', question: '' }

export default function Faq() {
  const [items, setItems] = useState(null)
  const [error, setError] = useState('')
  const [form, setForm] = useState(EMPTY)
  const [formError, setFormError] = useState('')

  const load = useCallback(() => {
    listFaqs().then(setItems).catch((e) => setError(e.message))
  }, [])

  useEffect(() => { load() }, [load])

  const submit = async (e) => {
    e.preventDefault()
    setFormError('')
    try {
      await createFaq(form)
      setForm(EMPTY)
      load()
    } catch (err) {
      setFormError(err.message)
    }
  }

  const remove = async (id) => {
    const password = window.prompt('질문 작성 시 입력한 비밀번호(또는 관리자 비밀번호)를 입력하세요.')
    if (!password) return
    try {
      await deleteFaq(id, password)
      load()
    } catch (err) {
      alert(err.message)
    }
  }

  const answer = async (item) => {
    const adminPassword = window.prompt('관리자 비밀번호를 입력하세요.')
    if (!adminPassword) return
    const text = window.prompt('답변을 입력하세요.', item.answer || '')
    if (!text || !text.trim()) return
    try {
      await answerFaq(item.id, text, adminPassword)
      load()
    } catch (err) {
      alert(err.message)
    }
  }

  return (
    <>
      <section className="hero">
        <h1>🐂 한우소달구지 FAQ</h1>
        <p>궁금한 점을 남겨주세요. 답변은 관리자가 등록합니다.</p>
      </section>

      <section>
        <h3 className="section-title">질문하기</h3>
        <form className="form" onSubmit={submit}>
          <div className="row faq-row">
            <input required maxLength={50} placeholder="닉네임" value={form.author}
                   onChange={(e) => setForm({ ...form, author: e.target.value })} />
            <input required type="password" minLength={4} maxLength={50} placeholder="삭제용 비밀번호 (4자 이상)"
                   value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
          </div>
          <input required maxLength={100} placeholder="제목" value={form.title}
                 onChange={(e) => setForm({ ...form, title: e.target.value })} />
          <textarea required rows={3} maxLength={1000} placeholder="질문 내용" value={form.question}
                    onChange={(e) => setForm({ ...form, question: e.target.value })} />
          {formError && <p className="error">{formError}</p>}
          <button className="btn">질문 등록</button>
        </form>
      </section>

      <section>
        <h3 className="section-title">질문 {items ? items.length : 0}개</h3>
        {error && <p className="error">{error}</p>}
        {!items && !error && <p className="muted">불러오는 중...</p>}
        {items && items.length === 0 && <p className="muted">아직 등록된 질문이 없습니다.</p>}
        <ul className="reviews">
          {items && items.map((f) => (
            <li key={f.id}>
              <div className="review-head">
                <strong>Q. {f.title}</strong>
                <span className="tag">{f.answer ? '답변완료' : '답변대기'}</span>
                <button className="link-btn" onClick={() => remove(f.id)}>삭제</button>
              </div>
              <p className="muted">{f.author} · {new Date(f.createdAt).toLocaleDateString('ko-KR')}</p>
              <p>{f.question}</p>
              {f.answer && (
                <p className="desc faq-answer"><strong>A.</strong> {f.answer}</p>
              )}
              <button className="link-btn faq-admin" onClick={() => answer(f)}>
                {f.answer ? '답변 수정 (관리자)' : '답변 달기 (관리자)'}
              </button>
            </li>
          ))}
        </ul>
      </section>
    </>
  )
}
