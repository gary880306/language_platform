import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { cartApi } from '@/api/cart'

export const useCartStore = defineStore('cart', () => {
  const cartId = ref(null)
  const items = ref([])
  const appliedCoupon = ref(null)

  const subtotal = computed(() =>
    items.value.reduce((sum, item) => sum + (item.course?.price ?? 0), 0)
  )

  const discount = computed(() => {
    if (!appliedCoupon.value) return 0
    const { discountType, discountValue } = appliedCoupon.value
    if (discountType === 'PERCENTAGE') {
      return Math.floor(subtotal.value * (discountValue / 100))
    }
    return discountValue
  })

  const total = computed(() => Math.max(0, subtotal.value - discount.value))

  const itemCount = computed(() => items.value.length)

  async function fetchCart() {
    const res = await cartApi.getCart()
    cartId.value = res.data.cartId
    items.value = res.data.items ?? []
  }

  async function addItem(courseId) {
    await cartApi.addToCart(courseId)
    await fetchCart()
  }

  async function removeItem(courseId) {
    await cartApi.removeFromCart(courseId)
    await fetchCart()
  }

  async function applyCoupon(code) {
    const res = await cartApi.applyCoupon(code)
    appliedCoupon.value = res.data
  }

  function removeCoupon() {
    appliedCoupon.value = null
  }

  async function checkout() {
    const couponId = appliedCoupon.value?.couponId ?? null
    const res = await cartApi.checkout(couponId)
    items.value = []
    appliedCoupon.value = null
    return res.data
  }

  return {
    cartId, items, appliedCoupon,
    subtotal, discount, total, itemCount,
    fetchCart, addItem, removeItem,
    applyCoupon, removeCoupon, checkout,
  }
})
