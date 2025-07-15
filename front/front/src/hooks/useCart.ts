import { useState, useEffect } from 'react'

type CartItem = {
  productId: number
  quantity: number
}

export function useCart() {
  const [items, setItems] = useState<CartItem[]>(() => {
    if (typeof window === 'undefined') return []
    const stored = localStorage.getItem('cart')
    return stored ? JSON.parse(stored) : []
  })

  useEffect(() => {
    localStorage.setItem('cart', JSON.stringify(items))
  }, [items])

  const addToCart = (productId: number, quantity: number) => {
    setItems((prev) => {
      const found = prev.find((item) => item.productId === productId)
      if (found) {
        return prev.map((item) =>
          item.productId === productId
            ? { ...item, quantity: item.quantity + quantity }
            : item
        )
      }
      return [...prev, { productId, quantity }]
    })
  }

  const getTotalCount = () => items.reduce((acc, item) => acc + item.quantity, 0)
  const getItemTypeCount = () => items.length

  return { items, addToCart, getTotalCount, getItemTypeCount }
}
