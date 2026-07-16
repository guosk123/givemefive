import {
  MerchantProduct,
  createMerchantProduct,
  deleteMerchantProduct,
  fetchMerchantProducts,
  updateMerchantProduct
} from '@/services/merchantProduct';

interface MerchantProductView extends MerchantProduct {
  purchasePriceText: string;
  salePriceText: string;
  grossProfitText: string;
  stockCostText: string;
  stockRevenueText: string;
}

interface MerchantPageData {
  products: MerchantProductView[];
  loading: boolean;
  saving: boolean;
  errorMessage: string;
  editingProductId: string;
  productName: string;
  purchasePrice: string;
  salePrice: string;
  stockQuantity: string;
  unit: string;
  remark: string;
  productCountText: string;
  stockTotalText: string;
  stockCostTotalText: string;
  stockRevenueTotalText: string;
  grossProfitTotalText: string;
  formTitle: string;
  saveButtonText: string;
}

function money(value: number): string {
  return Number(value || 0).toFixed(2);
}

function toProductView(product: MerchantProduct): MerchantProductView {
  return {
    ...product,
    purchasePriceText: money(product.purchasePrice),
    salePriceText: money(product.salePrice),
    grossProfitText: money(product.grossProfit),
    stockCostText: money(product.stockCost),
    stockRevenueText: money(product.stockRevenue)
  };
}

Page({
  data: {
    products: [],
    loading: false,
    saving: false,
    errorMessage: '',
    editingProductId: '',
    productName: '',
    purchasePrice: '',
    salePrice: '',
    stockQuantity: '',
    unit: '件',
    remark: '',
    productCountText: '0',
    stockTotalText: '0',
    stockCostTotalText: '0.00',
    stockRevenueTotalText: '0.00',
    grossProfitTotalText: '0.00',
    formTitle: '新增商品',
    saveButtonText: '保存商品'
  } as MerchantPageData,

  onLoad() {
    this.loadProducts();
  },

  refreshProducts() {
    this.loadProducts();
  },

  onProductNameInput(event: WechatMiniprogram.Input) {
    this.setData({ productName: event.detail.value });
  },

  onPurchasePriceInput(event: WechatMiniprogram.Input) {
    this.setData({ purchasePrice: event.detail.value });
  },

  onSalePriceInput(event: WechatMiniprogram.Input) {
    this.setData({ salePrice: event.detail.value });
  },

  onStockQuantityInput(event: WechatMiniprogram.Input) {
    this.setData({ stockQuantity: event.detail.value });
  },

  onUnitInput(event: WechatMiniprogram.Input) {
    this.setData({ unit: event.detail.value });
  },

  onRemarkInput(event: WechatMiniprogram.Input) {
    this.setData({ remark: event.detail.value });
  },

  submitProduct() {
    const productName = this.data.productName.trim();
    const purchasePrice = Number(this.data.purchasePrice);
    const salePrice = Number(this.data.salePrice);
    const stockQuantity = Number(this.data.stockQuantity);

    if (!productName) {
      wx.showToast({ title: '请输入商品名', icon: 'none' });
      return;
    }
    if (!Number.isFinite(purchasePrice) || purchasePrice < 0) {
      wx.showToast({ title: '请输入进价', icon: 'none' });
      return;
    }
    if (!Number.isFinite(salePrice) || salePrice < 0) {
      wx.showToast({ title: '请输入售价', icon: 'none' });
      return;
    }
    if (!Number.isInteger(stockQuantity) || stockQuantity < 0) {
      wx.showToast({ title: '请输入库存', icon: 'none' });
      return;
    }

    const payload = {
      productName,
      purchasePrice,
      salePrice,
      stockQuantity,
      unit: this.data.unit.trim() || '件',
      remark: this.data.remark.trim()
    };
    const save = this.data.editingProductId
      ? updateMerchantProduct(this.data.editingProductId, payload)
      : createMerchantProduct(payload);

    this.setData({ saving: true, errorMessage: '' });
    save
      .then(() => {
        wx.showToast({ title: '已保存', icon: 'success' });
        this.resetForm();
        this.loadProducts();
      })
      .catch((error: Error) => {
        this.setData({
          saving: false,
          errorMessage: error.message || '保存失败'
        });
      });
  },

  editProduct(event: WechatMiniprogram.TouchEvent) {
    const id = event.currentTarget.dataset.id as string;
    const product = this.data.products.find((item) => item.id === id);
    if (!product) {
      return;
    }
    this.setData({
      editingProductId: product.id,
      productName: product.productName,
      purchasePrice: money(product.purchasePrice),
      salePrice: money(product.salePrice),
      stockQuantity: product.stockQuantity.toString(),
      unit: product.unit || '件',
      remark: product.remark || '',
      formTitle: '编辑商品',
      saveButtonText: '更新商品'
    });
  },

  cancelEdit() {
    this.resetForm();
  },

  deleteProduct(event: WechatMiniprogram.TouchEvent) {
    const id = event.currentTarget.dataset.id as string;
    wx.showModal({
      title: '删除商品',
      content: '确认删除这个商品？',
      success: (result) => {
        if (!result.confirm) {
          return;
        }
        deleteMerchantProduct(id)
          .then(() => {
            wx.showToast({ title: '已删除', icon: 'success' });
            if (this.data.editingProductId === id) {
              this.resetForm();
            }
            this.loadProducts();
          })
          .catch((error: Error) => {
            this.setData({ errorMessage: error.message || '删除失败' });
          });
      }
    });
  },

  loadProducts() {
    this.setData({ loading: true, errorMessage: '' });
    fetchMerchantProducts()
      .then((result) => {
        this.setData({
          products: result.products.map(toProductView),
          productCountText: result.productCount.toString(),
          stockTotalText: result.stockTotal.toString(),
          stockCostTotalText: money(result.stockCostTotal),
          stockRevenueTotalText: money(result.stockRevenueTotal),
          grossProfitTotalText: money(result.grossProfitTotal),
          loading: false
        });
      })
      .catch((error: Error) => {
        this.setData({
          loading: false,
          errorMessage: error.message || '商品获取失败'
        });
      });
  },

  resetForm() {
    this.setData({
      saving: false,
      editingProductId: '',
      productName: '',
      purchasePrice: '',
      salePrice: '',
      stockQuantity: '',
      unit: '件',
      remark: '',
      formTitle: '新增商品',
      saveButtonText: '保存商品'
    });
  }
});
