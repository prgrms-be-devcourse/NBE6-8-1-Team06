'use client'

import { useEffect, useState } from 'react'
import { Product } from 'types/product'
import HeaderTitle from '../../../components/HeaderTitle'
import CartIcon from '../../../components/CartIcon'
import { useRouter } from 'next/navigation'
import { useCart } from '../../../hooks/useCart'

type Params = {
  params: {
    productId: string
  }
}

export default function ProductDetailPage({ params }: Params) {
  const { productId } = params
  const [product, setProduct] = useState<Product | null>(null)
  const [quantity, setQuantity] = useState(1)
  const router = useRouter()
  const { addToCart } = useCart()

  useEffect(() => {
    const idNum = Number(productId)
    fetch(`http://localhost:8080/products/${idNum}`)
      .then((res) => {
        if (!res.ok) throw new Error('서버 오류')
        return res.json()
      })
      .then((data: Product) => setProduct(data))
      .catch((err) => {
        console.error('상품 정보를 불러오지 못했습니다:', err)
        setProduct(null)
      })
  }, [productId])

  if (!product) return <p>로딩중...</p>

  return (
    <main className="pt-6 grid place-items-center">
      <HeaderTitle />
      <div className="w-full max-w-2xl">
        <h4 className="text-xl font-bold mb-4 text-amber-900 bg-amber-200 p-1 rounded-xl text-center">
          상품 소개
        </h4>
      </div>
      <CartIcon />
      <div className="flex gap-6 border p-6 rounded shadow hover:shadow-md  transition w-full max-w-2xl">
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
              addToCart(product.productId, quantity)
              const confirmed = window.confirm(`${product.name} ${quantity}개가 장바구니에 추가되었습니다!\n장바구니로 이동하시겠습니까?`)
              if (confirmed) {
                router.push('/cart')
              }
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
    </main>
  )
}
