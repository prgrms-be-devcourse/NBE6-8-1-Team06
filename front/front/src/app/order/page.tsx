'use client'

import { useState, useEffect } from 'react'
import { useCart } from '../../hooks/useCart'
import { useRouter } from 'next/navigation'
import { OrderRequest, ApiResponse, Product, Order } from '../../../types/index'
import { apiFetch } from '../../libs/apiFetch'
import HeaderTitle from '@/components/HeaderTitle'

type CartItemDetailed = Product & { quantity: number }

export default function OrderPage() {
  const { items, clearCart } = useCart()
  const [cartItems, setCartItems] = useState<CartItemDetailed[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const [email, setEmail] = useState('')
  const [address, setAddress] = useState('')
  const [zipCode, setZipCode] = useState('')
  const router = useRouter()

  useEffect(() => {
    if (items.length === 0) {
      setCartItems([])
      return
    }

    setLoading(true)
    setError(null)

    fetch('http://localhost:8080/api/v1/products')
      .then((res) => {
        if (!res.ok) throw new Error('상품 정보를 불러오는 데 실패했습니다.')
        return res.json()
      })
      .then((resJson: ApiResponse<Product[]>) => {
        const allProducts = resJson.data
        const detailed = items
          .map(({ id, quantity }) => {
            const product = allProducts.find((p) => p.id === id)
            if (!product) return null
            return { ...product, quantity }
          })
          .filter(Boolean) as CartItemDetailed[]

        setCartItems(detailed)
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [items])

  const total = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0)

  const handleSubmit = async () => {
    if (!email.trim() || !address.trim() || !zipCode.trim()) {
      alert('모든 정보를 입력하세요.')
      return
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(email)) {
      alert('올바른 이메일 주소를 입력해주세요.')
      return
    }

    if (cartItems.length === 0) {
      alert('장바구니에 상품이 없습니다.')
      return
    }

    const payload: OrderRequest = {
        customerEmail: email,
      shippingAddress: address,
      shippingZipCode: zipCode,
      items: cartItems.map(i => ({
        productId: i.id,
        quantity: i.quantity,
      })),
    }

    try {
      setLoading(true)

      const res = await apiFetch<ApiResponse<Order>>(
        'http://localhost:8080/api/v1/orders',
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload),
        }
      )

      const orderId = res.data.orderId

      const confirmed = window.confirm('결제를 진행하시겠습니까?')

      const paymentEndpoint = confirmed
        ? `/api/v1/orders/${orderId}/payment/complete`
        : `/api/v1/orders/${orderId}/payment/cancel`

      await apiFetch(`http://localhost:8080${paymentEndpoint}`, {
        method: 'PUT',
      })

      clearCart()
      setEmail('')
      setAddress('')
      setZipCode('')

      if (confirmed) {
        router.push(`/order/complete?orderId=${orderId}`)
      } else {
        alert('결제가 취소되었습니다.')
        router.push('/')
      }
    } catch (e: any) {
      alert(e?.message || '주문 처리 중 오류가 발생했습니다.')
    } finally {
      setLoading(false)
    }
  }

  if (loading) return <p>로딩 중...</p>
  if (error) return <p className="text-red-600">에러: {error}</p>
  if (cartItems.length === 0)
    return (
      <main className="p-4 max-w-2xl mx-auto text-center">
        <HeaderTitle />
        <p>장바구니가 비어있습니다.</p>
        <button className="mt-4 bg-amber-700 text-white py-2 px-4 rounded" onClick={() => router.push('/')}>상품 목록으로 돌아가기</button>
      </main>
    )

  return (
    <main className="p-4 max-w-2xl mx-auto">
      <HeaderTitle />
      <h1 className="text-2xl font-bold mb-3 text-amber-900 border-b-2 pb-2">📃 주문서</h1>

      <ul className="text-amber-600 border-b-2 mb-4">
        {cartItems.map((item) => (
          <li key={item.id} className="flex justify-between items-center mb-2 text-black">
                <span className="flex-1 font-bold">{item.name}</span>
                <span className="w-33 text-center">{item.quantity}개 × {item.price.toLocaleString()}원</span>
                <span className="w-25 text-right font-semibold">{(item.price * item.quantity).toLocaleString()}원</span>
          </li>
        ))}
      </ul>

      <div className="text-right font-bold mt-4 mb-2">총 결제 금액: {total.toLocaleString()}원</div>
      <div className="text-amber-900 border-b-2 mb-4"></div>
      <h1 className="text-2xl font-bold mb-3 text-amber-900">🎁 배송지 작성</h1>

          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="이메일" className="w-full border p-2 mb-2 rounded" />
          <input type="text" value={address} onChange={(e) => setAddress(e.target.value)} placeholder="주소" className="w-full border p-2 mb-2 rounded" />
          <input type="text" value={zipCode} onChange={(e) => setZipCode(e.target.value)} placeholder="우편번호" className="w-full border p-2 mb-4 rounded" />

      <button onClick={handleSubmit} disabled={loading} className="w-full bg-amber-700 text-white py-2 rounded hover:bg-amber-800 transition">
        {loading ? '처리중...' : '주문하기'}
      </button>

      <div className="flex justify-between mt-6">
        <button onClick={() => router.back()} className="bg-gray-300 text-gray-800 font-semibold py-2 px-4 rounded hover:bg-gray-400 transition">
          ◀️ 뒤로가기
        </button>
        <button onClick={() => router.push('/')} className="bg-gray-300 text-gray-800 font-semibold px-5 py-2 rounded hover:bg-gray-400 transition">
          🏠 홈으로 돌아가기
        </button>
      </div>
    </main>
  )
}
