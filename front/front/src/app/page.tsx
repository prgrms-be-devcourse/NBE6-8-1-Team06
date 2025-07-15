'use client'

import { useEffect, useState } from 'react'
import { Product } from 'types/product'
import Link from 'next/link'
import HeaderTitle from '../components/HeaderTitle'



export default function HomePage() {
 const [products, setProducts] = useState<Product[]>([])

  useEffect(() => {
    fetch('http://localhost:8080/products')
      .then((res) => res.json())
      .then((data: Product[]) => setProducts(data))
      .catch((err) => console.error('상품 불러오기 실패:', err))
  }, [])

  return (
    <main className="pt-6 grid place-items-center" >
     <HeaderTitle />
     <p className="text-gray-600 mb-3"> 상품을 소개합니다. </p>


      <div className="w-full max-w-2xl">
        <h4 className="text-2xl font-bold mb-4 text-amber-900 bg-amber-200 p-4 rounded-xl text-center">
          상품 목록
        </h4>
      </div>

     <ul className="w-full max-w-2xl grid grid-cols-2 gap-4">
       {products.map((product) => (
         <li key={product.productId} className="border p-4 rounded shadow hover:shadow-md  transition flex flex-col min-h-30">
           <Link href={`/products/${product.productId}`} className="block">
             <h2 className="text-xl font-semibold">{product.name}</h2>
             <p className="text-sm text-gray-600">  {product.description.split('\n')[0]}</p>
             <p className="mt-2 font-bold">{product.price}원</p>
           </Link>
         </li>
       ))}
     </ul>

    </main>
  )
}
