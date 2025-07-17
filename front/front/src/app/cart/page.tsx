'use client'

import { useCart } from '@/hooks/useCart'
import { useRouter } from 'next/navigation'
import HeaderTitle from '@/components/HeaderTitle'
import Link from 'next/link'

export default function CartPage() {
  const { items, updateQuantity, removeFromCart } = useCart()
  const router = useRouter()

  const total = items.reduce((acc, item) => acc + item.price * item.quantity, 0)

 const decreaseQuantity = (id: number, quantity: number) => {
  if (quantity > 1) {
     updateQuantity(id, quantity - 1)
   }
 }

 const increaseQuantity = (id: number, quantity: number) => {
   updateQuantity(id, quantity + 1)
 }


  const removeItem = (id: number) => {
    removeFromCart(id)
  }

  return (
    <main className="max-w-2xl mx-auto p-4">
      <HeaderTitle />
      <h1 className="text-3xl font-bold mb-6 text-amber-900 border-b-2 pb-2">장바구니</h1>

      {items.length === 0 ? (
        <p className="text-gray-600">장바구니가 비어있습니다.</p>
      ) : (
        <>
          <ul className="w-full bg-amber-50 rounded-lg shadow overflow-hidden">
            <li className="flex bg-amber-200 border-b border-amber-300 font-semibold p-3">
              <span className="flex-grow text-left">상품명</span>
              <span className="w-32 text-center">수량</span>
              <span className="w-32 text-right">가격</span>
              <span className="w-16 text-center">삭제</span>
            </li>

            {items.map(item => (
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
                    onClick={() => decreaseQuantity(item.id, item.quantity)}
                    className="bg-amber-700 text-white rounded-lg px-3 py-1 mx-2 hover:bg-amber-800 transition">
                    -
                  </button>
                  <span className="inline-block w-8 text-center">{item.quantity}</span>
                  <button
                    onClick={() => increaseQuantity(item.id, item.quantity)}
                    className="bg-amber-700 text-white rounded-lg px-3 py-1 mx-2 hover:bg-amber-800 transition">
                    +
                  </button>
                </span>

                <span className="w-32 text-right">
                  {(item.price * item.quantity).toLocaleString()}원
                </span>

                <span className="w-16 text-center">
                  <button
                    onClick={() => removeItem(item.id)}
                    className="text-red-600 hover:text-red-800 font-bold" >
                    ×
                  </button>
                </span>
              </li>
            ))}

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
