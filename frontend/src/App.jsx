import { Link, Route, Routes } from 'react-router-dom'
import RestaurantList from './pages/RestaurantList.jsx'
import RestaurantForm from './pages/RestaurantForm.jsx'
import RestaurantDetail from './pages/RestaurantDetail.jsx'

export default function App() {
  return (
    <>
      <header className="header">
        <Link to="/" className="logo">🍚 밀양 맛집</Link>
        <Link to="/restaurants/new" className="btn">식당 등록</Link>
      </header>
      <main className="container">
        <Routes>
          <Route path="/" element={<RestaurantList />} />
          <Route path="/restaurants/new" element={<RestaurantForm />} />
          <Route path="/restaurants/:id" element={<RestaurantDetail />} />
          <Route path="*" element={<p>페이지를 찾을 수 없습니다.</p>} />
        </Routes>
      </main>
    </>
  )
}
