export interface Product {
  id: number
  createDate: string
  modifyDate: string
  name: string
  description: string
  price: number
}

export interface CartItem {
  id: number
  quantity: number
}

export type CartItemDetailed = Product & { quantity: number }

export interface OrderItem {
  id: number
  quantity: number
}

export interface OrderRequest {
  customerName: string
  customerEmail: string
  address: string
  postalCode: string
  orderItems: OrderItem[]
  totalPrice: number
  status: string
}

export type Order = {
  id: number
  customerEmail: string
  Address: string
  ZipCode: string
  totalAmount: number
  status: string
  createDate: string
}

export interface RsData<T> {
  resultCode: string
  msg: string
  data: T
}
