import { request } from '@/utils/request';

export type AccountRecordType = 'INCOME' | 'EXPENSE';

export interface AccountRecord {
  id: string;
  recordType: AccountRecordType;
  amount: number;
  category: string;
  note: string;
  recordDate: string;
  createdAt: string;
  updatedAt: string;
}

export interface AccountRecordList {
  records: AccountRecord[];
  incomeTotal: number;
  expenseTotal: number;
  balance: number;
}

export interface CreateAccountRecordPayload {
  recordType: AccountRecordType;
  amount: number;
  category: string;
  note: string;
  recordDate: string;
}

export function fetchAccountRecords(): Promise<AccountRecordList> {
  return request<AccountRecordList>({
    path: '/api/app/account-records',
    method: 'GET'
  });
}

export function createAccountRecord(payload: CreateAccountRecordPayload): Promise<AccountRecord> {
  return request<AccountRecord>({
    path: '/api/app/account-records',
    method: 'POST',
    data: payload
  });
}

export function deleteAccountRecord(id: string): Promise<void> {
  return request<void>({
    path: `/api/app/account-records/${id}`,
    method: 'DELETE'
  });
}
