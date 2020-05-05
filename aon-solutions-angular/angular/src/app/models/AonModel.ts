/*
-------------------------------------------------------------------
  _______ ______ _____ _____   __  __  ____  _____  ______ _
 |__   __|  ____|  __ \_   _| |  \/  |/ __ \|  __ \|  ____| |
    | |  | |__  | |  | || |   | \  / | |  | | |  | | |__  | |
    | |  |  __| | |  | || |   | |\/| | |  | | |  | |  __| | |
    | |  | |____| |__| || |_  | |  | | |__| | |__| | |____| |____
    |_|  |______|_____/_____| |_|  |_|\____/|_____/|______|______|
-------------------------------------------------------------------
*/

export enum InvoiceType {
  PURCHASE = 'PURCHASE',
  SALES = 'SALES',
  EXPENSES = 'EXPENSES',
  UNDEDUCTIBLE = 'UNDEDUCTIBLE',
}

export enum InvoiceStatus {
  PENDING = 'PENDING',
  SCORED = 'SCORED',
  REFUSED = "REFUSED",
  TRASH = "TRASH"
}

export enum InvoiceHistoryAction {
  create = 'create',
  status = 'status',
  update = 'update',
}

export enum InvoiceTransaction {
  NATIONAL = 'NATIONAL',
  INTRACOMMUNITY = 'INTRACOMMUNITY',
  EXTRACOMMUNITY = 'EXTRACOMMUNITY',
  CAN_CEU_MEL = 'CAN_CEU_MEL',
  OTHER_ISP = 'OTHER_ISP',
}

export enum TaxType {
  VAT = 'VAT',
  RETENTION = 'RETENTION',
}

export enum PayMethod {
  CASH_BASIS = 'CASH_BASIS',
  NEGOTIABLE_DOCUMENT = 'NEGOTIABLE_DOCUMENT',
  DEBIT_CARD = 'DEBIT_CARD',
  CREDIT_CARD = 'CREDIT_CARD',
  CHEQUE = 'CHEQUE',
  BANK_TRANSFER = 'BANK_TRANSFER',
  OTHER = 'OTHER'
}

export enum InvoiceCategory {
  C7000 = '700.0',
  C7050 = '705.0',
  C6000 = '600.0',
  C6070 = '607.0',
  C6210 = '621.0',
  C6220 = '622.0',
  C6230 = '623.0',
  C6240 = '624.0',
  C6250 = '625.0',
  C6260 = '626.0',
  C6270 = '627.0',
  C6280 = '628.0',
  C6290 = '629.0',
  C6291 = '629.1',
  C6292 = '629.2',
  C6293 = '629.3',
  C6294 = '629.4',
  C6295 = '629.5',
  C6296 = '629.6',
  C6297 = '629.7',
  C6298 = '629.8',
  C6299 = '629.9',
}

export interface InvoiceTax {
  tax: TaxType;
  base: number;
  percentage: number;
  quota: number;
  surcharge?: number;
  surcharge_quota?: number;
}

export interface InvoiceHistory {
  action: InvoiceHistoryAction;
  date: Date;
  user: string;
}

export interface InvoiceDetail {
  id?: number;
  description?: string;
  quantity?: number;
  price?: number;
  discount?: number;
  vat?: number;
  surcharge?: number;
  amount?: number;
  prepayment?: boolean;
  withholding?:boolean;
  retention?:number;
}

export interface Finance {
  id?: number;
  due_date?: Date;
  amount?: number;
  pay_method?: PayMethod;
  iban?: string;
}

export interface Account {
  id?: number;
  code?: string;
  description?: string;
}

export interface InvoiceTemplate extends Invoice {
  _address: string;
  _nif: string;
  _name: string;
}

export interface Invoice {
  id?: number;
  domain?: string;
  type?: InvoiceType;
  series?: string;
  number?: number;
  reference?: string;
  date?: Date;
  transaction?: InvoiceTransaction;
  category?: InvoiceCategory;
  total?: number;
  sender?: Registry;
  receiver?: Registry;
  details?: InvoiceDetail[];
  taxes?: InvoiceTax[];
  finances?: Finance[];
  file?: InvoiceFile;
  status: InvoiceStatus;
  verified?: boolean;
  oldStatus?: InvoiceStatus;
  source?: string;
  email?: TediEmailInfo;
  comments?: Comment[];
  create_user?: string;
  create_date?: Date;
  modification_user?: string;
  modification_date?: Date;
}

export interface Comment {
  data?: Date;
  comment?: string;
}

export interface Address {
  id?: number;
  address?: string;
  city?: string;
  province?: string;
  postal_code?: string;
  country?: string;
}

export interface Registry {
  id?: number;
  document?: string;
  document_country?: string;
  name?: string;
  address?: Address;
}

export interface InvoiceFile {
  url?: string;
  thumb_url?: string;
  content_type?: string;
}

export interface Insight {
  references?: string[];
}

export enum PermissionTagType {
  ADMIN = 'admin',
  TICKET = 'invoice_ticket',
  ISSUED = 'invoice_issued',
  RECEIVED = 'invoice_received',
  COMPANY = 'company',
  USER = 'user',
}

export interface Permission {
  name: string;
  tag: PermissionTagType;
  reader: boolean;
  writer: boolean;
}

export interface AonConfig {
  domain: string;
  user: string;
  password: string;
}

export interface User {
  email: string;
  active?: boolean;
  phone?: string;
  name?: string;
  surname?: string;
  document?: string;
  password?: string;
  actual_company?: string;
  users?: string[];
  aon?: AonConfig;
  permissions?: Permission[];
  root?: boolean;
  admin?: boolean;
  gestor?: boolean;
  company?: string;
}

export interface PrinterConfiguration {
  header: number;
  footer: number;
  background?: string;
  adjustment: boolean;
  detailed: boolean;
}

export interface Company {
  id: number;
  domain: string;
  document: string;
  name: string;
  active: boolean;
  alias?: string;
  plan?: TediPlan;
  iban?: string;
  bic?: string;
  address?: Address;
  printer_configuration?: PrinterConfiguration;
  users?: string[];
  aon?: AonConfig;
  contract?: Evicertia;
}

export interface TediPlan {
  plan: TediPlanEnum;
  period: TediPlanPeriod;
  promo?: string;
}

export enum TediPlanEnum {
  PLAN101 = '+101',
  PLAN10 = '10',
  PLAN25 = '25',
  PLAN50 = '50',
  PLANPYME = 'PyME',
  PLAN100 = '100',
}

export enum TediPlanPeriod {
  ANUAL = 'A',
  MENSUAL = 'M',
}

export interface Evicertia {
  uniqueId: string;
}

export interface TediSendInvoicesInfo {
  company: Company;
  to: string;
  invoices: Invoice[];
}

export interface TediFile {
  content: string | Buffer;
  contentType: string;
  contentEncoding?: string;
}

export interface TediEmailInfo {
  id: string | undefined;
  from: string[];
  fileName: string;
}
export interface TediImportInvoicesInfo extends TediFile {
  company?: string;
  source?: string;
  uuid?: string;
  name?: string;
  invoice?: Invoice;
  type?: InvoiceType;
  email?: TediEmailInfo;
  companies?: Company[];
  status?: InvoiceStatus;
}

export interface GServiceAccount {
  auth_provider_x509_cert_url: string;
  auth_uri: string;
  client_email: string;
  client_id: string;
  client_x509_cert_url: string;
  private_key: string;
  private_key_id: string;
  project_id: string;
  subject: string;
  token_uri: string;
  type: string;
}

export interface Oauth2Token {
  access_token: string;
  expiry_date: number;
  refresh_token: string;
  scope: string;
  token_type: string;
}

export interface AutoMLInvoiceTables {
  amounts: AutoMLInvoiceAmount[];
}

export interface InsightInvoice {
  dates: Date[];
  amounts: number[];
  references: string[];
  taxTypes: TaxType[];
}

export interface AutoMLInvoiceAmount {
  id: number;
  x: number;
  y: number;
  max: number;
  count: number;
  height: number;
  amount: number;
  target_IVA_score?: number;
  target_BASE_score?: number;
  target_TOTAL_score?: number;
  target_undefined_score?: number;
}
