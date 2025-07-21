'use client'

import { useEffect, useState } from 'react'
import { apiFetch } from '../../../libs/apiFetch'
import { Product, ApiResponse, ProductForm } from '../../../../types/index'
import {useRouter} from "next/navigation";

const DEFAULT_IMAGE = '/cafe_coffee_bag.png'

const initialForm: ProductForm = {
  id: undefined,        // 수정 시에만 사용, 신규 등록이면 undefined
  name: '',
  description: '',
  price: 0,
  imageUrl: '',
}

export default function AdminProductPage() {
  const [products, setProducts] = useState<Product[]>([])
  const [productForm, setProductForm] = useState<ProductForm>(initialForm)
  const [isEdit, setIsEdit] = useState(false)
  const router = useRouter()


  const fetchProducts = async () => {
    try {
      const res = await apiFetch<ApiResponse<Product[]>>('http://localhost:8080/api/v1/products')
      setProducts(res.data)
    } catch (err) {
      console.error('상품 목록 불러오기 실패:', err)
    }
  }

  useEffect(() => {
    fetchProducts()
  }, [])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target
    setProductForm((prev) => ({
      ...prev,
      [name]: name === 'price' ? Number(value) : value,
    }))
  }

  const handleSubmit = async () => {
    const payload = {
      ...productForm,
      imgUrl: productForm.imageUrl?.trim() || DEFAULT_IMAGE,
    }

    const url = isEdit
      ? `http://localhost:8080/api/v1/products/${productForm.id}`
      : `http://localhost:8080/api/v1/products`

    const method = isEdit ? 'PUT' : 'POST'

    try {
      await apiFetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      })

      alert(isEdit ? '수정 완료!' : '등록 완료!')
      setProductForm(initialForm)
      setIsEdit(false)
      fetchProducts()
    } catch (err) {
      console.error(err)
      alert('요청 실패')
    }
  }

  const handleEdit = (product: Product) => {
    setProductForm(product)
    setIsEdit(true)
  }

const handleDelete = async (id: number) => {
  if (!confirm('정말 삭제하시겠습니까?')) return;

  try {
    const res = await fetch(`http://localhost:8080/api/v1/products/${id}`, { method: 'DELETE' });
    if (!res.ok) throw new Error('삭제 실패');
    alert('삭제 성공');
    location.reload();
  } catch (error) {
    alert('삭제 실패: ' + (error as Error).message);
  }
};




  return (
    <div className="max-w-4xl mx-auto p-6">
      <h1 className="text-2xl font-bold text-amber-900 mb-4">상품 관리</h1>

      {/* 상품 폼 */}
      <div className="border p-4 rounded mb-6 bg-amber-50">
        <h2 className="text-lg font-semibold mb-2">{isEdit ? '상품 수정' : '상품 등록'}</h2>
        <input
          name="name"
          value={productForm.name}
          onChange={handleChange}
          placeholder="상품명"
          className="border p-2 w-full mb-2 rounded"
        />
        <input
          name="price"
          type="number"
          value={productForm.price}
          onChange={handleChange}
          placeholder="가격"
          className="border p-2 w-full mb-2 rounded"
        />
        <input
          name="imageUrl"
          value={productForm.imageUrl ?? ''}
          onChange={handleChange}
          placeholder="이미지 URL (선택)"
          className="border p-2 w-full mb-2 rounded"
        />
        <textarea
          name="description"
          value={productForm.description}
          onChange={handleChange}
          placeholder="설명"
          className="border p-2 w-full mb-2 rounded"
        />
        <button
          onClick={handleSubmit}
          className="bg-amber-600 text-white px-4 py-2 rounded hover:bg-amber-700"
        >
          {isEdit ? '수정' : '등록'}
        </button>
      </div>

      {/* 상품 목록 */}
      <ul className="grid grid-cols-4 gap-4 font-semibold text-center border-b pb-2 mb-2">
        <li>ID</li>
        <li>상품명</li>
        <li>가격</li>
        <li>관리</li>
      </ul>
      {products.map((product) => (
        <ul
          key={product.id}
          className="grid grid-cols-4 gap-4 text-center items-center border-b py-2"
        >
          <li>{product.id}</li>
          <li>{product.name}</li>
          <li>{product.price.toLocaleString()}원</li>
          <li className="flex gap-2 justify-center">
            <button
              onClick={() => handleEdit(product)}
              className="bg-blue-500 text-white px-2 py-1 rounded"
            >
              수정
            </button>
            <button
              onClick={() => handleDelete(product.id)}
              className="bg-red-500 text-white px-2 py-1 rounded"
            >
              삭제
            </button>
          </li>
        </ul>
      ))}

      <div className="mt-3">
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
    </div>
  )
}
