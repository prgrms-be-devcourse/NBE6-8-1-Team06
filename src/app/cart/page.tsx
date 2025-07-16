'use client'

import { useCart } from '@/hooks/useCart'
import { useEffect, useState } from 'react'
import { Product } from '../../../types/product'
import Link from 'next/link'
import HeaderTitle from '@/components/HeaderTitle'
import { useRouter } from 'next/navigation'

export default function CartPage() {
  const { items, updateQuantity, removeFromCart } = useCart()
  const [cartItems, setCartItems] = useState<(Product & { quantity: number })[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const router = useRouter()

  useEffect(() => {
    if (items.length === 0) {
      setCartItems([])
      return
    }

    setLoading(true)
    setError(null)

    Promise.all(
      items.map(({ productId, quantity }) =>
        fetch(`http://localhost:8080/api/v1/products/${productId}`)
          .then(res => {
            if (!res.ok) throw new Error(`상품 조회 실패 (ID: ${productId})`)
            return res.json()
          })
          .then((product: Product) => ({ ...product, quantity }))
      )
    )
      .then(results => {
        setCartItems(results)
        setLoading(false)
      })
      .catch(err => {
        setError(err.message || '상품 정보를 불러오는데 실패했습니다.')
        setLoading(false)
      })
  }, [items])

  const total = cartItems.reduce((acc, item) => acc + item.price * item.quantity, 0)

  // 삭제 함수 (removeFromCart 래핑)
  const removeItem = (productId: number) => {
    removeFromCart(productId)
  }

  if (loading) return <p className="text-center mt-10">장바구니 로딩중...</p>
  if (error) return <p className="text-center mt-10 text-red-600">{error}</p>

  return (
    <main className="max-w-3xl mx-auto p-4">
      <HeaderTitle />
      <h1 className="text-3xl font-bold mb-6 text-amber-900 border-b-2 pb-2">장바구니</h1>

      {cartItems.length === 0 ? (
        <p className="text-gray-600">장바구니가 비어있습니다.</p>
      ) : (
        <>
          <ul className="w-full bg-amber-50 rounded-lg shadow overflow-hidden">
            {/* 헤더 */}
            <li className="flex bg-amber-200 border-b border-amber-300 font-semibold p-3">
              <span className="flex-grow text-left">상품명</span>
              <span className="w-32 text-center">수량</span>
              <span className="w-32 text-right">가격</span>
              <span className="w-16 text-center">삭제</span>
            </li>

            {/* 아이템 목록 */}
            {cartItems.map(item => (
              <li
                key={item.id}
                className="flex border-b border-amber-300 hover:bg-amber-100 transition p-3 items-center"
              >
                <Link
                  href={`/products/${item.id}`}
                  className="flex-grow font-semibold hover:underline"
                >
                  {item.name}
                </Link>

                <span className="w-32 text-center">
                  <button
                    onClick={() => updateQuantity(item.id, item.quantity - 1)}
                    className="bg-amber-700 text-white rounded-lg px-3 py-1 mx-2 hover:bg-amber-800 transition"
                  >
                    -
                  </button>
                  <span className="inline-block w-8 text-center">{item.quantity}</span>
                  <button
                    onClick={() => updateQuantity(item.id, item.quantity + 1)}
                    className="bg-amber-700 text-white rounded-lg px-3 py-1 mx-2 hover:bg-amber-800 transition"
                  >
                    +
                  </button>
                </span>

                <span className="w-32 text-right">
                  {(item.price * item.quantity).toLocaleString()}원
                </span>

                <span className="w-16 text-center">
                  <button
                    onClick={() => removeItem(item.id)}
                    className="text-red-600 hover:text-red-800 font-bold"
                    aria-label={`${item.name} 삭제`}
                  >
                    ×
                  </button>
                </span>
              </li>
            ))}

            {/* 총 합계 */}
            <li className="flex font-bold p-3 border-t-2 border-amber-400">
              <span className="flex-grow"></span>
              <span className="w-32 text-right">총 합계:</span>
              <span className="w-32 text-right">{total.toLocaleString()}원</span>
              <span className="w-16"></span>
            </li>
          </ul>

          <div className="text-right mt-6">
            <button
              className="bg-amber-700 text-white rounded-md px-5 py-2 hover:bg-amber-800 transition"
              onClick={() => router.push('/order')}
            >
              주문하기
            </button>
          </div>
        </>
      )}

      <div className="mt-8 text-center">
        <button
          className="bg-gray-300 hover:bg-gray-400 text-gray-800 font-semibold py-2 px-6 rounded"
          onClick={() => router.push('/')}
        >
          🏠 홈으로 돌아가기
        </button>
      </div>
    </main>
  )
}
