import Link from "next/link";

export default function AdminMain() {
  return (
    <main className="max-w-3xl mx-auto p-8">
      <h1 className="text-3xl font-bold mb-10 text-amber-900 text-center  border-b-2 pb-2">관리자 페이지</h1>
      <nav className="flex flex-row gap-8 justify-center">
        <Link
          href="/admin/products"
          className="bg-amber-700 text-white py-4 px-10 rounded-xl shadow-md hover:bg-amber-800 transition"
        >
          상품 관리
        </Link>
        <Link
          href="/admin/orders"
          className="bg-amber-700 text-white py-4 px-10 rounded-xl shadow-md hover:bg-amber-800 transition"
        >
          주문 관리
        </Link>
      </nav>
    </main>
  );
}