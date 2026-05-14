import { post, get, remove} from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getAccounting = (data) => post(`${API_URL}/accounting/pyg`, data);
export const getPeriods = (data) => post(`${API_URL}/accounting/periods`, data);
export const getBanks = (data) => get(`${API_URL}/companies/${data.id}/banks`, data);
export const getAccounts = (data) => get(`${API_URL}/accounts`, data);
export const getAccount = (data) => get(`${API_URL}/accounts/${data.id}`, data);
export const getExpenses = (data) => get(`${API_URL}/accounting/expenses`, data);
export const getIncomes = (data) => get(`${API_URL}/accounting/incomes`, data);
export const setIncome = (data) => post(`${API_URL}/accounting/incomes`, data);
export const deleteIncome = (data) => remove(`${API_URL}/accounting/incomes`, data);
export const deleteExpense = (data) => remove(`${API_URL}/accounting/expenses`, data);
export const setExpense = (data) => post(`${API_URL}/accounting/expenses`, data);
export const getAmortizationTypes = (data) => get(`${API_URL}/amortizationtype`, data);
export const getInvoicesCounters = (data) => get(`${API_URL}/accounting/invoicesCounters`, data);

export const PERIOD_FILTER = [
  {
    type: "select",
    id: "year",
    name: "year",
    title: "Ejercicio",
  },
  {
    type: "select",
    id: "show",
    name: "show",
    title: "Mostrar",
  },
  {
    type: "select",
    id: "detail",
    name: "detail",
    title: "Nivel de detalle",
  }
];

export const getPeriodAccounting = (data) =>  [
  {
    name: "Mensual",
    value: "monthly",
  },
  {
    name: "Trimestral",
    value: "quarterly",
  },
  {
    name: "Anual",
    value: "yearly",
  }
];