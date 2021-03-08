
import { get, openPDF } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getEmployeeSalaries = (data) => get(`${API_URL}/contract/employee/salaries`, data);
export const getEnterpriseSalaries = (data) => get(`${API_URL}/contract/enterprise/salaries`, data);
export const getSalaryPdf = (data) => openPDF(`${API_URL}/contract/salary/pdf`, data);