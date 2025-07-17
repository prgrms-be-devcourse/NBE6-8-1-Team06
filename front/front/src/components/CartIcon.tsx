'use client'

import Link from 'next/link'
import { useCart } from '../hooks/useCart'

export default function CartIcon() {
  const { getItemTypeCount } = useCart()
  const count = getItemTypeCount()

  return (
    <span className="flex items-center mr-4 relative">
      <Link href="/cart">
        <span className="text-2xl">🛒</span>
        {count > 0 && (
          <span className="absolute -top-1 -right-2 bg-red-500 text-white text-xs px-1 rounded-full">
            {count}
          </span>
        )}
      </Link>
    </span>
  )
}
