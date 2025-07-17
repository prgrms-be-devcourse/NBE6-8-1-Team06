'use client'

import { useEffect, useState } from 'react'
import { apiFetch } from '../../libs/apiFetch'
import HeaderTitle from '@/components/HeaderTitle'

type Product = {
  id: number
  name: string
  description: string
  price: number
}

type OrderItem = {
  productId: number
  productName: string
  quantity: number
}

type OrderData = {
  orderId: number
  customerEmail: string
  shippingAddress: string
  shippingZipCode: string
  totalPrice: number
  status: string
  items: OrderItem[]
}

export default function AdminPage() {
  // 상품 관련 상태
  const [products, setProducts] = useState<Product[]>([])
  const [productForm, setProductForm] = useState<{ id?: number; name: string; description: string; price: string }>({
    name: '',
    description: '',
    price: '',
  })
  const [productLoading, setProductLoading] = useState(false)
  const [productError, setProductError] = useState<string | null>(null)
  const [editingProductId, setEditingProductId] = useState<number | null>(null)

  // 주문 관련 상태
  const [orders, setOrders] = useState<OrderData[]>([])
  const [orderLoading, setOrderLoading] = useState(false)
  const [orderError, setOrderError] = useState<string | null>(null)

  // 상품 목록 가져오기
  const fetchProducts = async () => {
    setProductLoading(true)
    setProductError(null)
    try {
      const res = await apiFetch<{ resultCode: string; msg: string; data: Product[] }>('http://localhost:8080/api/v1/products')
      if (res.resultCode !== '200') throw new Error(res.msg || '상품 목록을 불러올 수 없습니다.')
      setProducts(res.data)
    } catch (e: any) {
      setProductError(e.message)
    } finally {
      setProductLoading(false)
    }
  }

  // 주문 목록 가져오기
  const fetchOrders = async () => {
    setOrderLoading(true)
    setOrderError(null)
    try {
      const res = await apiFetch<{ resultCode: string; msg: string; data: OrderData[] }>('http://localhost:8080/api/v1/delivery')
      if (res.resultCode !== '200') throw new Error(res.msg || '주문 목록을 불러올 수 없습니다.')
      setOrders(res.data)
    } catch (e: any) {
      setOrderError(e.message)
    } finally {
      setOrderLoading(false)
    }
  }

  useEffect(() => {
    fetchProducts()
    fetchOrders()
  }, [])

  // 상품 폼 입력 핸들러
  const onChangeProductForm = (field: string, value: string) => {
    setProductForm((prev) => ({ ...prev, [field]: value }))
  }

  // 상품 등록 또는 수정 처리
  const handleProductSubmit = async () => {
    const { id, name, description, price } = productForm
    if (!name.trim() || !description.trim() || !price.trim()) {
      alert('모든 상품 정보를 입력해주세요.')
      return
    }
    if (isNaN(Number(price)) || Number(price) < 0) {
      alert('가격은 0 이상의 숫자여야 합니다.')
      return
    }

    try {
      setProductLoading(true)

      if (editingProductId) {
        // 수정
        const res = await apiFetch<{ resultCode: string; msg: string; data: Product }>(
          `http://localhost:8080/api/v1/products/${editingProductId}`,
          {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              name,
              description,
              price: Number(price),
            }),
          }
        )
        if (res.resultCode !== '200') throw new Error(res.msg || '상품 수정 실패')
        alert('상품이 수정되었습니다.')
      } else {
        // 등록
        const res = await apiFetch<{ resultCode: string; msg: string; data: Product }>(
          `http://localhost:8080/api/v1/products`,
          {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              name,
              description,
              price: Number(price),
            }),
          }
        )
        if (res.resultCode !== '200') throw new Error(res.msg || '상품 등록 실패')
        alert('상품이 등록되었습니다.')
      }
      setProductForm({ name: '', description: '', price: '' })
      setEditingProductId(null)
      fetchProducts()
    } catch (e: any) {
      alert(e.message || '오류가 발생했습니다.')
    } finally {
      setProductLoading(false)
    }
  }

  // 상품 삭제 처리
  const handleDeleteProduct = async (id: number) => {
    if (!window.confirm('정말 이 상품을 삭제하시겠습니까?')) return
    try {
      setProductLoading(true)
      const res = await apiFetch<{ resultCode: string; msg: string }>(`http://localhost:8080/api/v1/products/${id}`, {
        method: 'DELETE',
      })
      if (res.resultCode !== '200') throw new Error(res.msg || '상품 삭제 실패')
      alert('상품이 삭제되었습니다.')
      fetchProducts()
    } catch (e: any) {
      alert(e.message || '오류가 발생했습니다.')
    } finally {
      setProductLoading(false)
    }
  }

  // 상품 수정 시작
  const startEditProduct = (product: Product) => {
    setEditingProductId(product.id)
    setProductForm({
      name: product.name,
      description: product.description,
      price: product.price.toString(),
    })
  }

  // 주문 상태 변경
  const handleChangeOrderStatus = async (orderId: number, newStatus: string) => {
    if (!window.confirm(`주문 ${orderId} 상태를 '${newStatus}' 로 변경하시겠습니까?`)) return
    try {
      setOrderLoading(true)
      const res = await apiFetch<{ resultCode: string; msg: string }>(`http://localhost:8080/api/v1/delivery/${orderId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status: newStatus }),
      })
      if (res.resultCode !== '200') throw new Error(res.msg || '상태 변경 실패')
      alert('주문 상태가 변경되었습니다.')
      fetchOrders()
    } catch (e: any) {
      alert(e.message || '오류가 발생했습니다.')
    } finally {
      setOrderLoading(false)
    }
  }

  return (
    <main className="max-w-4xl mx-auto p-4">
      <HeaderTitle />
      <h1 className="text-3xl font-bold text-amber-900 mb-6 border-b-2 pb-2">관리자 페이지</h1>

      {/* 상품 관리 섹션 */}
      <section className="mb-10">
        <h2 className="text-2xl font-semibold mb-4 border-b pb-2">상품 등록 / 수정</h2>
        <div className="grid grid-cols-1 gap-3 max-w-xl">
          <input
            type="text"
            placeholder="상품명"
            value={productForm.name}
            onChange={(e) => onChangeProductForm('name', e.target.value)}
            className="border p-2 rounded"
          />
          <textarea
            placeholder="설명"
            value={productForm.description}
            onChange={(e) => onChangeProductForm('description', e.target.value)}
            className="border p-2 rounded resize-none"
            rows={3}
          />
          <input
            type="number"
            placeholder="가격"
            value={productForm.price}
            onChange={(e) => onChangeProductForm('price', e.target.value)}
            className="border p-2 rounded"
            min={0}
          />
          <div>
            <button
              onClick={handleProductSubmit}
              disabled={productLoading}
              className="bg-amber-700 text-white px-4 py-2 rounded hover:bg-amber-800 transition"
            >
              {editingProductId ? '수정하기' : '등록하기'}
            </button>
            {editingProductId && (
              <button
                onClick={() => {
                  setEditingProductId(null)
                  setProductForm({ name: '', description: '', price: '' })
                }}
                className="ml-3 px-4 py-2 border rounded hover:bg-gray-100 transition"
              >
                취소
              </button>
            )}
          </div>
          {productError && <p className="text-red-600">{productError}</p>}
        </div>

        {/* 상품 목록 */}
        <div className="mt-8">
          <h3 className="text-xl font-semibold mb-2">등록된 상품 목록</h3>
          {productLoading && <p>상품 불러오는 중...</p>}
          {productError && <p className="text-red-600">{productError}</p>}
          <ul className="divide-y divide-gray-300">
            {products.map((product) => (
              <li key={product.id} className="flex justify-between items-center py-2">
                <div>
                  <strong>{product.name}</strong> ({product.price.toLocaleString()}원)
                </div>
                <div className="space-x-2">
                  <button
                    onClick={() => startEditProduct(product)}
                    className="text-amber-700 hover:underline"
                  >
                    수정
                  </button>
                  <button
                    onClick={() => handleDeleteProduct(product.id)}
                    className="text-red-600 hover:underline"
                  >
                    삭제
                  </button>
                </div>
              </li>
            ))}
          </ul>
        </div>
      </section>

      {/* 주문 및 배송 관리 섹션 */}
      <section>
        <h2 className="text-2xl font-semibold mb-4 border-b pb-2">주문 / 배송 관리</h2>
        {orderLoading && <p>주문 목록 불러오는 중...</p>}
        {orderError && <p className="text-red-600">{orderError}</p>}

        <ul className="space-y-4 max-w-3xl">
          {orders.map((order) => (
            <li key={order.orderId} className="border p-4 rounded shadow bg-white">
              <div className="mb-2">
                <strong>주문번호:</strong> {order.orderId} <br />
                <strong>이메일:</strong> {order.customerEmail} <br />
                <strong>주소:</strong> {order.shippingAddress} ({order.shippingZipCode}) <br />
                <strong>총 금액:</strong> {order.totalPrice.toLocaleString()}원 <br />
                <strong>상태:</strong>{' '}
                <select
                  value={order.status}
                  onChange={(e) => handleChangeOrderStatus(order.orderId, e.target.value)}
                  className="border rounded px-2 py-1"
                  disabled={orderLoading}
                >
                  <option value="PAID">결제완료</option>
                  <option value="SHIPPED">배송중</option>
                  <option value="DELIVERED">배송완료</option>
                  <option value="CANCELLED">취소</option>
                </select>
              </div>
              <div>
                <strong>주문 상품 목록:</strong>
                <ul className="mt-1 list-disc list-inside text-gray-700">
                  {order.items.map((item) => (
                    <li key={item.productId}>
                      {item.productName} × {item.quantity}개
                    </li>
                  ))}
                </ul>
              </div>
            </li>
          ))}
        </ul>
      </section>
    </main>
  )
}
