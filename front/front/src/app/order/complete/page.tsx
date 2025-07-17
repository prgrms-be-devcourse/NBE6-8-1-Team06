'use client'

import { useEffect, useState } from 'react'
import { useRouter, useSearchParams } from 'next/navigation'
import { OrderData, ApiResponse } from '../../../../types/index'
import { apiFetch } from '../../../libs/apiFetch'
import HeaderTitle from '@/components/HeaderTitle'

export default function OrderComplete() {
  const [order, setOrder] = useState<OrderData & { createDate?: string } | null>(null)
  const [error, setError] = useState<string | null>(null)
  const params = useSearchParams()
  const orderId = params.get('orderId')
  const router = useRouter()

  useEffect(() => {
    if (!orderId) {
      setError('주문 ID가 없습니다.')
      return
    }

    apiFetch<ApiResponse<OrderData & { createDate?: string }>>(`http://localhost:8080/api/v1/orders/${orderId}`)
      .then(res => setOrder(res.data))
      .catch(err => setError(`주문 정보를 불러오지 못했습니다: ${err.message}`))
  }, [orderId])

  if (error) return <p className="p-4 text-red-500">{error}</p>
  if (!order) return <p className="p-4">주문 정보를 불러오는 중...</p>

  // 배송 안내 메시지 생성
  const SHIPPING_CUTOFF_HOUR = 14
  const orderTime = order.createDate ? new Date(order.createDate) : new Date()
  const orderHour = orderTime.getHours()
  const shippingMessage =
    orderHour < SHIPPING_CUTOFF_HOUR
      ? '당일 오후 2시 이전 주문이므로 오늘 배송을 시작합니다.'
      : '당일 오후 2시 이후 주문이므로 다음날 배송을 시작합니다.'

  return (
    <main className="p-4 max-w-2xl mx-auto">
      <HeaderTitle />
      <h2 className="text-xl font-bold mb-4">🎉 주문이 완료되었습니다! </h2>

      <ul className="border p-4 rounded bg-white shadow space-y-2 text-gray-700">
        <li>
          <strong className="inline-block min-w-[120px] text-right pr-2">주문 번호 :</strong>
          {order.orderId}
        </li>
        <li>
          <strong className="inline-block min-w-[120px] text-right pr-2">이메일 :</strong>
          {order.customerEmail}
        </li>
        <li>
          <strong className="inline-block min-w-[120px] text-right pr-2">주소 :</strong>
          {order.shippingAddress}
        </li>
        <li>
          <strong className="inline-block min-w-[120px] text-right pr-2">우편번호 :</strong>
          {order.shippingZipCode}
        </li>
        <li>
          <strong className="inline-block min-w-[120px] text-right pr-2">총 금액 :</strong>
          {order.totalPrice.toLocaleString()}원
        </li>
      </ul>

      {/* 배송 안내 문구 */}
      <p className="mt-4 text-amber-700 font-semibold">{shippingMessage}</p>

      <h3 className="mt-6 mb-2 font-semibold text-lg">주문 상품 목록</h3>
      <ul className="space-y-2">
        {order.items.map((item) => (
          <li key={item.productId} className="p-3 bg-gray-100 rounded">
            {item.productName} - {item.quantity}개
          </li>
        ))}
      </ul>

      <div className="flex justify-between mt-6">
        <button
          className="bg-gray-300 text-gray-800 font-semibold rounded-md px-5 py-2 hover:bg-gray-400 transition"
          onClick={() => router.push('/')}
        >
          🏠 홈으로 돌아가기
        </button>
      </div>
    </main>
  )
}
