import { useEffect, useState } from 'react'
import { apiFetch } from '../libs/apiFetch'

export type OrderItem = {
  product_id: number
  quantity: number
}

export type Order = {
  orderId: number
  customer_email: string
  shipping_address: string
  shipping_zip_code: string
  total_amount: number
  order_items: OrderItem[]
  status: string
  created_at: string
}

export function useOrders() {
  const [orders, setOrders] = useState<Order[]>([])
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    apiFetch<Order[]>('http://localhost:8080/api/orders')
      .then(setOrders)
      .catch(err => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  return { orders, error, loading }
}

export async function fetchOrderById(orderId: number): Promise<Order> {
  return apiFetch<Order>(`http://localhost:8080/orders/${orderId}`)
}
