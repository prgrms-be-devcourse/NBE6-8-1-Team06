'use client'

import { useEffect, useState } from 'react'
import { Product, ApiResponse } from '../../types/index'
import Link from 'next/link'
import HeaderTitle from '../components/HeaderTitle'

export default function HomePage() {
  const [products, setProducts] = useState<Product[]>([])
  const [errorMessage, setErrorMessage] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetch('http://localhost:8080/api/v1/products')
      .then(async (res) => {
        if (!res.ok) throw new Error('상품 목록을 불러오는 데 실패했습니다.')

        const data: ApiResponse<Product[]> = await res.json()

        if (data.resultCode !== '200') {
          throw new Error(data.msg || '알 수 없는 오류가 발생했습니다.')
        }

        return data.data
      })
      .then((products) => {
        setProducts(products)
        setErrorMessage(null)
      })
      .catch((err) => {
        console.error(err)
        setErrorMessage(err.message)
      })
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <p className="text-center mt-10">로딩중...</p>

  if (errorMessage)
    return (
      <div className="text-center mt-10">
        <p className="text-red-600 font-semibold mb-4">{errorMessage}</p>
        <button
          onClick={() => window.location.reload()}
          className="bg-amber-600 hover:bg-amber-700 text-white font-semibold py-2 px-4 rounded"
        >
          새로고침
        </button>
      </div>
    )

  return (
    <main className="grid place-items-center relative">
      <Link
        href="/admin/login"
        className="absolute right-4 top-4 hover:text-amber-700"
      >
        👤
      </Link>

      <HeaderTitle />
      <p className="text-gray-600 mb-3">상품을 소개합니다.</p>

      <div className="w-full max-w-2xl">
        <h4 className="text-2xl font-bold mb-4 text-amber-900 bg-amber-200 p-4 rounded-xl text-center">
          상품 목록
        </h4>
      </div>

      <ul className="w-full max-w-2xl grid grid-cols-2 gap-4">
        {products.map((product) => (
          <li
            key={product.id}
            className="border p-4 rounded shadow hover:shadow-md transition flex flex-col min-h-30"
          >
            <Link href={`/products/${product.id}`} className="block">
              {/* 이미지 url 추가  */}
              <div className="aspect-square w-full mb-3 overflow-hidden rounded-lg bg-gray-200">
                <img
                  src={product.imageUrl || '/cafe_coffee_bag.png'}
                  alt={product.name}
                  className="object-contain w-full h-full"
                />
              </div>

              <h2 className="text-xl font-semibold">{product.name}</h2>
              <p className="text-sm text-gray-600">{product.description.split('\n')[0]}</p>
              <p className="mt-2 font-bold">{product.price}원</p>
            </Link>
          </li>
        ))}
      </ul>
    </main>
  )
}
