'use client'

import { useEffect, useState } from 'react'
import {useRouter} from "next/navigation";
type OrderItem = {
  productId: number
  productName: string
  quantity: number
}

type Order = {
  orderId: number
  customerEmail: string
  shippingAddress: string
  shippingZipCode: string
  totalPrice: number
  status: string
  items: OrderItem[]
}

export default function AdminOrderPage() {
  const [orders, setOrders] = useState<Order[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const router = useRouter()
  useEffect(() => {
    fetchOrders()
  }, [])

  async function fetchOrders() {
    setLoading(true)
    setError(null)
    try {
      const res = await fetch('http://localhost:8080/api/v1/orders')
      if (!res.ok) throw new Error('주문 목록 불러오기 실패')
      const json = await res.json()
      setOrders(json.data)
    } catch (e: any) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  async function updateOrderStatus(orderId: number, newStatus: string) {
    try {
      // 현재 orders 상태에서 바꿀 주문 찾기
      const orderToUpdate = orders.find(o => o.orderId === orderId);
      if (!orderToUpdate) {
        alert('해당 주문을 찾을 수 없습니다.');
        return;
      }

      // 서버가 요구하는 전체 데이터 형식 맞춰서 body 생성
      const body = {
        shippingAddress: orderToUpdate.shippingAddress,
        shippingZipCode: orderToUpdate.shippingZipCode,
        status: newStatus,
        items: orderToUpdate.items.map(item => ({
          productId: item.productId,
          quantity: item.quantity,
        })),
      };

      const res = await fetch(`http://localhost:8080/api/v1/orders/${orderId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      });
      if (!res.ok) throw new Error('주문 상태 변경 실패');

      await fetchOrders(); // 변경 후 최신 주문 목록 다시 가져오기
    } catch (e: any) {
      alert(e.message);
    }
  }


  return (
    <main className="p-6 max-w-6xl mx-auto">
      <h1 className="text-3xl font-bold mb-6">주문 관리</h1>

      {loading && <p>로딩 중...</p>}
      {error && <p className="text-red-600">{error}</p>}

      <table className="w-full border-collapse border border-gray-300">
        <thead>
          <tr className="bg-amber-200">
            <th className="border border-gray-300 p-2">주문 ID</th>
            <th className="border border-gray-300 p-2">고객 이메일</th>
            <th className="border border-gray-300 p-2">배송지</th>
            <th className="border border-gray-300 p-2">우편번호</th>
            <th className="border border-gray-300 p-2">총 금액</th>
            <th className="border border-gray-300 p-2">상태</th>
            <th className="border border-gray-300 p-2">상품 목록</th>
            <th className="border border-gray-300 p-2">상태 변경</th>
          </tr>
        </thead>
        <tbody>
          {orders.map((order) => (
            <tr key={order.orderId} className="text-center border border-gray-300">
              <td className="border border-gray-300 p-2">{order.orderId}</td>
              <td className="border border-gray-300 p-2">{order.customerEmail}</td>
              <td className="border border-gray-300 p-2">{order.shippingAddress}</td>
              <td className="border border-gray-300 p-2">{order.shippingZipCode}</td>
              <td className="border border-gray-300 p-2">{order.totalPrice.toLocaleString()}원</td>
              <td className="border border-gray-300 p-2 font-semibold">{order.status}</td>
              <td className="border border-gray-300 p-2 text-left">
                <ul>
                  {order.items.map((item) => (
                    <li key={item.productId}>
                      {item.productName} x {item.quantity}
                    </li>
                  ))}
                </ul>
              </td>
              <td className="border border-gray-300 p-2">
                <select
                  value={order.status}
                  onChange={(e) => updateOrderStatus(order.orderId, e.target.value)}
                  className="border border-gray-400 rounded px-2 py-1"
                >
                  <option value="PENDING">PENDING</option>
                  <option value="PAID">PAID</option>
                  <option value="CANCELED">CANCELED</option>
                  {/* 상태변하게 해둠  */}
                </select>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

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
    </main>
  )
}
