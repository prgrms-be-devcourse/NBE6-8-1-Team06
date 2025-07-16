'use client'

import Link from 'next/link'

export default function HeaderTitle() {
  return (
    <Link href="/" className="w-full max-w-2xl">
      <h1 className="text-3xl font-bold mb-4 text-white/80 bg-amber-800/70 p-4 rounded-xl text-center">
        ☕ Grids & Circles 커피 ☕
      </h1>
    </Link>
  )
}
