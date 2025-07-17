'use client'
import { useEffect, useState } from 'react'
import { useSearchParams } from 'next/navigation'
import { Order, RsData } from '../../../../types/index'
import { apiFetch } from '../../../libs/apiFetch'
import HeaderTitle from "@/components/HeaderTitle";

export default function OrderComplete() {
  const [order, setOrder] = useState<Order | null>(null)
  const [error, setError] = useState<string | null>(null)
  const params = useSearchParams()
  const orderId = params.get('orderId')

  useEffect(() => {
    if (!orderId) {
      setError('주문 ID 없음')
      return
    }
    apiFetch<RsData<Order>>(`http://localhost:8080/orders/${orderId}`)
      .then(res => setOrder(res.data))
      .catch(err => setError(err.message))
  }, [orderId])

  if (error) return <p className="text-red-600 p-4">{error}</p>
  if (!order) return <p className="p-4">불러오는 중...</p>

  return (
    <main className="p-4 max-w-2xl mx-auto">

      <h1 className="text-2xl font-bold mb-4">주문 완료</h1>
      <ul className="border p-4 rounded space-y-2">
        <li><strong>주문 번호:</strong> {order.id}</li>
        <li><strong>이메일:</strong> {order.customerEmail}</li>
        <li><strong>주소:</strong> {order.Address}</li>
        <li><strong>우편번호:</strong> {order.ZipCode}</li>
        <li><strong>총금액:</strong> {order.totalAmount.toLocaleString()}원</li>
        <li><strong>상태:</strong> {order.status}</li>
        <li><strong>주문일시:</strong> {new Date(order.createDate).toLocaleString()}</li>
      </ul>
    </main>
  )
}