export interface OrderItem {
  productId: number
  productName: string
  quantity: number
}

export interface OrderData {
  orderId: number
  customerEmail: string
  shippingAddress: string
  shippingZipCode: string
  totalPrice: number
  items: OrderItem[]
}

export type Order = OrderData

export interface ApiResponse<T> {
  resultCode: string
  msg: string
  data: T
}


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

export interface OrderResponse {
  resultCode: string
  msg: string
  data: Order
}

export interface OrderRequest {
  customerEmail: string
  shippingAddress: string
  shippingZipCode: string
  items: {
    productId: number
    quantity: number
  }[]

}
