'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'

export default function AdminCheck() {
  const [input, setInput] = useState('')
  const [error, setError] = useState('')
  const router = useRouter()

  const handleCheck = () => {
    if (input.trim().toLowerCase() === 'admin') {
      router.push('/admin')
    } else {
      setError('권한이 없습니다.')
    }
  }

  return (
    <main className="mt-4 grid place-items-center p-4">
      <h1 className="text-2xl font-bold mb-3 text-amber-900 border-b-2 pb-2">
        관리자 확인
      </h1>
      <input
        type="text"
        value={input}
        onChange={(e) => setInput(e.target.value)}
        placeholder="관리자 키워드 입력"
        className="w-full max-w-xs border p-2 mb-2 rounded"
      />
      <button
        onClick={handleCheck}
        className="bg-amber-500 hover:bg-amber-600 text-white px-4 py-2 rounded w-full max-w-xs"
      >
        확인
      </button>
      {error && <p className="text-red-500 mt-2">{error}</p>}
    </main>
  )
}
