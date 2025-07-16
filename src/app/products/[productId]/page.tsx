'use client'

import { useEffect, useState } from 'react'
import { Product } from '../../../../types/product'
import HeaderTitle from '../../../components/HeaderTitle'
import CartIcon from '../../../components/CartIcon'
import { useRouter } from 'next/navigation'
import { useCart } from '../../../hooks/useCart'

type Params = {
  params: {
    productId: string // <-- 여기에 Next.js가 URL의 [productId] 값 넣어줌
  }
}

export default function ProductDetailPage({ params }: Params) {
  const { productId } = params
  const [product, setProduct] = useState<Product | null>(null)
  const [quantity, setQuantity] = useState(1)
  const router = useRouter()
  const { addToCart } = useCart()
  const [errorMessage, setErrorMessage] = useState<string | null>(null)

  useEffect(() => {
    const idNum = Number(productId)

    fetch(`http://localhost:8080/api/v1/products/${idNum}`)
      .then(res => {
        if (!res.ok) {
          if (res.status === 404) throw new Error('해당 상품을 찾을 수 없습니다.')
          else if (res.status === 500) throw new Error('서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.')
          else throw new Error('알 수 없는 오류가 발생했습니다.')
        }
        return res.json()
      })
      .then((data: Product) => {
        setProduct(data)
        setErrorMessage(null)
      })
      .catch(err => {
        console.error('상품 정보 로딩 실패:', err)
        setProduct(null)
        setErrorMessage(err.message)
      })
  }, [productId])

  if (errorMessage) {
    return (
      <div className="text-center mt-10">
        <p className="text-red-600 font-semibold mb-4">{errorMessage}</p>
        <div className="flex justify-center gap-4">
          <button onClick={() => router.back()} className="bg-gray-300 hover:bg-gray-400 text-gray-800 font-semibold py-2 px-4 rounded">
            🔙 뒤로가기
          </button>
          <button onClick={() => router.push('/')} className="bg-amber-600 hover:bg-amber-700 text-white font-semibold py-2 px-4 rounded">
            🏠 홈으로 이동
          </button>
        </div>
      </div>
    )
  }

  if (!product) return <p>로딩중...</p>

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
              장바구니 담기
            </button>
          </div>
        </div>

        <button
          onClick={() => router.back()}
          className="bg-amber-700 text-white font-semibold py-2 px-4 rounded hover:bg-amber-800 transition mt-6"
        >
          뒤로가기
        </button>
      </div>
    </main>
  )
}
