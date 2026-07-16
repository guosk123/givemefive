import {
  AccountRecord,
  AccountRecordType,
  createAccountRecord,
  deleteAccountRecord,
  fetchAccountRecords
} from '@/services/accountRecord';

interface AccountRecordView extends AccountRecord {
  typeText: string;
  amountText: string;
  signedAmountText: string;
}

interface AccountPageData {
  records: AccountRecordView[];
  loading: boolean;
  saving: boolean;
  errorMessage: string;
  recordType: AccountRecordType;
  amount: string;
  category: string;
  note: string;
  recordDate: string;
  incomeTotalText: string;
  expenseTotalText: string;
  balanceText: string;
}

function today(): string {
  const date = new Date();
  const pad = (part: number) => part.toString().padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
}

function money(value: number): string {
  return Number(value || 0).toFixed(2);
}

function toRecordView(record: AccountRecord): AccountRecordView {
  const isIncome = record.recordType === 'INCOME';
  return {
    ...record,
    typeText: isIncome ? '收入' : '支出',
    amountText: money(record.amount),
    signedAmountText: `${isIncome ? '+' : '-'}${money(record.amount)}`
  };
}

Page({
  data: {
    records: [],
    loading: false,
    saving: false,
    errorMessage: '',
    recordType: 'EXPENSE',
    amount: '',
    category: '',
    note: '',
    recordDate: today(),
    incomeTotalText: '0.00',
    expenseTotalText: '0.00',
    balanceText: '0.00'
  } as AccountPageData,

  onLoad() {
    this.loadRecords();
  },

  refreshRecords() {
    this.loadRecords();
  },

  switchRecordType(event: WechatMiniprogram.TouchEvent) {
    const recordType = event.currentTarget.dataset.type as AccountRecordType;
    this.setData({ recordType });
  },

  onAmountInput(event: WechatMiniprogram.Input) {
    this.setData({ amount: event.detail.value });
  },

  onCategoryInput(event: WechatMiniprogram.Input) {
    this.setData({ category: event.detail.value });
  },

  onNoteInput(event: WechatMiniprogram.Input) {
    this.setData({ note: event.detail.value });
  },

  onDateChange(event: WechatMiniprogram.PickerChange) {
    this.setData({ recordDate: event.detail.value as string });
  },

  submitRecord() {
    const amount = Number(this.data.amount);
    const category = this.data.category.trim();
    if (!Number.isFinite(amount) || amount <= 0) {
      wx.showToast({ title: '请输入金额', icon: 'none' });
      return;
    }
    if (!category) {
      wx.showToast({ title: '请输入分类', icon: 'none' });
      return;
    }

    this.setData({ saving: true, errorMessage: '' });
    createAccountRecord({
      recordType: this.data.recordType,
      amount,
      category,
      note: this.data.note.trim(),
      recordDate: this.data.recordDate
    })
      .then(() => {
        wx.showToast({ title: '已保存', icon: 'success' });
        this.setData({
          amount: '',
          category: '',
          note: '',
          saving: false
        });
        this.loadRecords();
      })
      .catch((error: Error) => {
        this.setData({
          errorMessage: error.message || '保存失败',
          saving: false
        });
      });
  },

  deleteRecord(event: WechatMiniprogram.TouchEvent) {
    const id = event.currentTarget.dataset.id as string;
    wx.showModal({
      title: '删除记录',
      content: '确认删除这条记录？',
      success: (result) => {
        if (!result.confirm) {
          return;
        }
        deleteAccountRecord(id)
          .then(() => {
            wx.showToast({ title: '已删除', icon: 'success' });
            this.loadRecords();
          })
          .catch((error: Error) => {
            this.setData({ errorMessage: error.message || '删除失败' });
          });
      }
    });
  },

  loadRecords() {
    this.setData({ loading: true, errorMessage: '' });
    fetchAccountRecords()
      .then((result) => {
        this.setData({
          records: result.records.map(toRecordView),
          incomeTotalText: money(result.incomeTotal),
          expenseTotalText: money(result.expenseTotal),
          balanceText: money(result.balance),
          loading: false
        });
      })
      .catch((error: Error) => {
        this.setData({
          errorMessage: error.message || '记录获取失败',
          loading: false
        });
      });
  }
});
