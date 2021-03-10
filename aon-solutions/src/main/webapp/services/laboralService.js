
import { get, openPDF } from "./request.js";
import { API_URL } from "../environments/environments.js";
import { addMonth, formatDateOrigin } from "./utils.js";

export const getEmployeeSalaries = (data) => get(`${API_URL}/contract/employee/salaries`, data);
export const getEnterpriseSalaries = (data) => get(`${API_URL}/contract/enterprise/salaries`, data);
export const getAllEmployeesWorkplace = (data) => get(`${API_URL}/contract/employee/workplace`, data);
export const getSalaryPdf = (data) => openPDF(`${API_URL}/contract/salary/pdf`, data);


export const getPeriodLaboral = (data) => {
    const now = new Date();
    return new Promise((resolve) => {
      let jsonValues = [
        {
            name: "Mes actual",
            value: "this_month",
            startDate: formatDateOrigin(new Date(now.getFullYear(), now.getMonth(), 1)),
            endDate: formatDateOrigin(new Date(now.getFullYear(), now.getMonth() + 1, 0))
        },
        {
            name: "Mes anterior",
            value: "last_month",
            startDate: formatDateOrigin(new Date(now.getFullYear(), (now.getMonth() -1), 1)),
            endDate: formatDateOrigin(new Date(now.getFullYear(), (now.getMonth()-1) + 1, 0))
        },
        {
            name: "Último trimeste",
            value: "last_quarterly",
            startDate: formatDateOrigin(now),
            endDate: formatDateOrigin(addMonth(now, -3))
        },
        {
            name: "Último semestre",
            value: "last_semester",
            startDate: formatDateOrigin(now),
            endDate: formatDateOrigin(addMonth(now, -6))
        },
        {
            name: "Año actual",
            value: "this_year",
            startDate: formatDateOrigin(new Date(now.getFullYear(), 0, 1)),
            endDate: formatDateOrigin(new Date(now.getFullYear(), 12, 0))
        },
        {
            name: "Año anterior",
            value: "last_year",
            startDate: formatDateOrigin(new Date(now.getFullYear()-1, 0, 1)),
            endDate:formatDateOrigin(new Date(now.getFullYear()-1, 12, 0))
        },
        {
          name: "Personalizado",
          value: "personalized",
        },
      ];
      if (data) {
        jsonValues = jsonValues.find((f) => f.value.indexOf(data) >= 0);
      }
      resolve(jsonValues);
    });
  }