'use client'

import { use } from 'react'
import { useEffect, useState } from 'react'
import { Product } from '../../../../types/index'
import HeaderTitle from '../../../components/HeaderTitle'
import CartIcon from '../../../components/CartIcon'
import { useRouter } from 'next/navigation'
import { useCart } from '../../../hooks/useCart'


/*

type Params = {
  params: {
       productId: string
     }
}
ProductDetailPage({ params }: Params) : 이 함수의 인자로 params 라는 객체가 들어오고
 타입을 params로 명시해서 params 안에 어떤 값들이 들어오는 지 타입스크립트가 알 수 있음
-> 하지만 next.js 14버전부터는 구조 변경 떄문에 에러 메시지 주는 중 -> 변경

*/
export default function ProductDetailPage({ params }: { params: Promise<{ productId: string }> }) {
  const { productId } = use(params)
  const [product, setProduct] = useState<Product | null>(null)
  const [quantity, setQuantity] = useState(1)
  const [errorMessage, setErrorMessage] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  const router = useRouter()
  const { addToCart } = useCart()

  useEffect(() => {
    async function fetchProduct() {
      try {
        const res = await fetch(`http://localhost:8080/api/v1/products/${productId}`)
        if (!res.ok) {
          if (res.status === 404) throw new Error('해당 상품을 찾을 수 없습니다.')
          if (res.status === 500) throw new Error('서버에 문제가 발생했습니다.')
          throw new Error('알 수 없는 오류가 발생했습니다.')
        }
        const json = await res.json()
        if (json.resultCode !== '200') throw new Error(json.msg || '알 수 없는 오류가 발생했습니다.')
        setProduct(json.data)
        setErrorMessage(null)
      } catch (error: any) {
        setErrorMessage(error.message)
        setProduct(null)
      } finally {
        setLoading(false)
      }
    }
    fetchProduct()
  }, [productId])

  if (loading) return <p>로딩중...</p>

  if (errorMessage)
    return (
      <div className="text-center mt-10">
        <p className="text-red-600 font-semibold mb-4">{errorMessage}</p>
        <button
          onClick={() => router.back()}
          className="mr-3 bg-gray-300 hover:bg-gray-400 text-gray-800 font-semibold py-2 px-4 rounded"
        >
          🔙 뒤로가기
        </button>
        <button
          onClick={() => router.push('/')}
          className="bg-amber-600 hover:bg-amber-700 text-white font-semibold py-2 px-4 rounded"
        >
          🏠 홈으로 이동
        </button>
      </div>
    )

  if (!product) return null

  return (
    <main className="pt-6">
      <div className="grid place-items-center">
        <HeaderTitle />
      </div>

      <div className="max-w-2xl mx-auto">
        <div className="w-full max-w-2xl">
          <h4 className="text-xl font-bold mb-4 text-amber-900 bg-amber-200 p-1 rounded-xl text-center">
            상품 소개
          </h4>
        </div>

        <div className="w-full max-w-2xl flex items-center justify-between mb-4">
          <h5 className="text-xl font-bold p-1 rounded-xl text-left ml-auto flex-grow">
            주문하기
          </h5>
          <CartIcon />
        </div>

        <div className="flex gap-6 border p-6 rounded shadow hover:shadow-md transition w-full max-w-2xl">
          <div className="flex-1">
            <h1 className="text-3xl font-bold mb-4">{product.name}</h1>
            <p className="mb-2 text-gray-700 whitespace-pre-wrap">{product.description}</p>
            <p className="text-xl font-semibold">{product.price}원</p>
          </div>

          <div className="flex flex-col justify-between items-end">
            <div className="flex items-center gap-2 mb-4">
              <button
                onClick={() => setQuantity((prev) => Math.max(prev - 1, 1))}
                className="px-3 py-1 bg-gray-300 rounded"
              >
                -
              </button>
              <span className="min-w-[30px] text-center font-semibold">{quantity}</span>
              <button
                onClick={() => setQuantity((prev) => prev + 1)}
                className="px-3 py-1 bg-gray-300 rounded"
              >
                +
              </button>
            </div>

            <button
              className="bg-amber-700 text-white font-semibold py-2 px-4 rounded hover:bg-amber-800 transition"
              onClick={() => {
                if (!product) return
                addToCart(product.id, quantity, product.name, product.price)
                const confirmed = window.confirm(
                  `${product.name} ${quantity}개가 장바구니에 추가되었습니다!\n장바구니로 이동하시겠습니까?`
                )
                if (confirmed) router.push('/cart')
                else window.location.reload()
              }}
            >
              🛒 장바구니 담기
            </button>
          </div>
        </div>

        <button
          onClick={() => router.back()}
          className="bg-amber-700 text-white font-semibold py-2 px-4 rounded hover:bg-amber-800 transition mt-6"
        >
          ◀️ ️뒤로가기
        </button>
      </div>
    </main>
  )
}

