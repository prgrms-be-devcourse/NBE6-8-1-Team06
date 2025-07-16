// hooks/useCart.ts
import { useEffect, useState } from 'react'

export type CartItem = {
  productId: number
  quantity: number
  name: string
  price: number
}

export function useCart() {
  const [items, setItems] = useState<CartItem[]>([])

  useEffect(() => {
    const stored = localStorage.getItem('cart')
    if (stored) setItems(JSON.parse(stored))
  }, [])

  const addToCart = (
    productId: number,
    quantity: number,
    name: string,
    price: number
  ) => {
    const updated = [...items]
    const existing = updated.find((item) => item.productId === productId)

    if (existing) {
      existing.quantity += quantity
    } else {
      updated.push({ productId, quantity, name, price })
    }

    setItems(updated)
    localStorage.setItem('cart', JSON.stringify(updated))
  }

  const clearCart = () => {
    setItems([])
    localStorage.removeItem('cart')
  }

  const getItemTypeCount = () => items.length

  const removeFromCart = (productId: number) => {
    const updated = items.filter(item => item.productId !== productId)
    setItems(updated)
    localStorage.setItem('cart', JSON.stringify(updated))
  }

  const updateQuantity = (productId: number, quantity: number) => {
    const updated = items.map((item) =>
      item.productId === productId
        ? { ...item, quantity: Math.max(1, quantity) }
        : item
    )
    setItems(updated)
    localStorage.setItem('cart', JSON.stringify(updated))
  }

  return {
    items,
    addToCart,
    getItemTypeCount,
    clearCart,
    updateQuantity,
    removeFromCart,
  }
}
