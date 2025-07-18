'use client'

import { useCart } from '@/hooks/useCart'
import { useRouter } from 'next/navigation'
import HeaderTitle from '@/components/HeaderTitle'
import Link from 'next/link'
import { useEffect, useState } from 'react'  //장바구니에 담긴 상품 이름이 다를 때 사용하기 위해
import { Product, ApiResponse } from '../../../types/index'

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

  //장바구니랑 db이름 다를때
  const [latestProducts, setLatestProducts] = useState<Product[]>([])
  useEffect(() => {
    fetch('http://localhost:8080/api/v1/products')
      .then(res => res.json())
      .then((resJson: ApiResponse<Product[]>) => {
        setLatestProducts(resJson.data)
      })
      .catch(err => console.error('최신 상품 불러오기 실패:', err))
  }, [])


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

               {/*product 변경 시 */}
              {items.map(item => {
                const latest = latestProducts.find(p => p.id === item.id);
                const isNameChanged = latest && latest.name !== item.name;

                {/* 장바구니 item 하나씩 돌아가면서 최신 상품 데이터랑 이름 비교하여 다르면 글자 회색으로 변경
                    ${조건 ? '클래스 A' : ''}' = 템플릿 리터럴 문법 -> 자바스크립트 표현식 쓸 수 있음
                */}
                return (
                  <li
                    key={item.id}
                    className={`flex border-b border-amber-300 hover:bg-amber-100 transition p-3 items-center ${
                      isNameChanged ? 'text-gray-400' : ''
                    }`}
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
                        className="bg-amber-700 text-white rounded-lg px-3 py-1 mx-2 hover:bg-amber-800 transition"
                      >
                        -
                      </button>
                      <span className="inline-block w-8 text-center">{item.quantity}</span>
                      <button
                        onClick={() => increaseQuantity(item.id, item.quantity)}
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
                      >
                        ×
                      </button>
                    </span>
                  </li>
                );
              })}

              <li className="flex font-bold p-3 border-t-2 border-amber-400">
                <span className="flex-grow"></span>
                <span className="w-32 text-right">총 합계:</span>
                <span className="w-32 text-right">{total.toLocaleString()}원</span>
                <span className="w-16"></span>
              </li>
            </ul>

            <div className="text-center mt-5 font-bold bg-amber-100 py-2 px-10 rounded">
              “당일 오후 2시 이후의 주문은 다음날 배송을 시작합니다.”
            </div>

            <div className="flex justify-between mt-3">
              <button
                onClick={() => router.back()}
                className="bg-amber-700 text-white font-semibold py-2 px-4 rounded hover:bg-amber-800 transition"
              >
                ◀️ 뒤로가기
              </button>

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
    );
  }