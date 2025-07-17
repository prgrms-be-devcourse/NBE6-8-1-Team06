import { useEffect, useState } from 'react'

export type CartItem = {
  id: number
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
    id: number,
    quantity: number,
    name: string,
    price: number
  ) => {
    setItems((prevItems) => {
      const existing = prevItems.find((item) => item.id === id)
      let updated: CartItem[]

      if (existing) {
        updated = prevItems.map((item) =>
          item.id === id
            ? { ...item, quantity: item.quantity + quantity }
            : item
        )
      } else {
        updated = [...prevItems, { id, quantity, name, price }]
      }

      localStorage.setItem('cart', JSON.stringify(updated))
      return updated
    })
  }

  const clearCart = () => {
    setItems([])
    localStorage.removeItem('cart')
  }

  const getItemTypeCount = () => items.length

  const removeFromCart = (id: number) => {
    const updated = items.filter(item => item.id !== id)
    setItems(updated)
    localStorage.setItem('cart', JSON.stringify(updated))
  }

  const updateQuantity = (id: number, quantity: number) => {
    setItems((prevItems) => {
      const updated = prevItems.map((item) =>
        item.id === id
          ? { ...item, quantity: Math.max(1, quantity) }
          : item
      )
      localStorage.setItem('cart', JSON.stringify(updated))
      return updated
    })
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
