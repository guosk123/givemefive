import { request } from '@/utils/request';

export interface MerchantProduct {
  id: string;
  productName: string;
  purchasePrice: number;
  salePrice: number;
  stockQuantity: number;
  unit: string;
  remark: string;
  grossProfit: number;
  stockCost: number;
  stockRevenue: number;
  createdAt: string;
  updatedAt: string;
}

export interface MerchantProductList {
  products: MerchantProduct[];
  productCount: number;
  stockTotal: number;
  stockCostTotal: number;
  stockRevenueTotal: number;
  grossProfitTotal: number;
}

export interface MerchantProductPayload {
  productName: string;
  purchasePrice: number;
  salePrice: number;
  stockQuantity: number;
  unit: string;
  remark: string;
}

export function fetchMerchantProducts(): Promise<MerchantProductList> {
  return request<MerchantProductList>({
    path: '/api/app/merchant-products',
    method: 'GET'
  });
}

export function createMerchantProduct(payload: MerchantProductPayload): Promise<MerchantProduct> {
  return request<MerchantProduct>({
    path: '/api/app/merchant-products',
    method: 'POST',
    data: payload
  });
}

export function updateMerchantProduct(
  id: string,
  payload: MerchantProductPayload
): Promise<MerchantProduct> {
  return request<MerchantProduct>({
    path: `/api/app/merchant-products/${id}`,
    method: 'PUT',
    data: payload
  });
}

export function deleteMerchantProduct(id: string): Promise<void> {
  return request<void>({
    path: `/api/app/merchant-products/${id}`,
    method: 'DELETE'
  });
}
