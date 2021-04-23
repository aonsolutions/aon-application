import { post, get, openFile } from "./request.js";
import { API_URL } from "../environments/environments.js";
import { addMonth, formatDateOrigin } from "./utils.js";

// export const saveLocation = (data) => post(`${API_URL}/location`, data);
export const getAccounting = (data) => post(`${API_URL}/accounting/pyg`, data);
export const getPeriods = (data) => post(`${API_URL}/accounting/periods`, data);

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

export const getPeriodAccounting = (data) => {
  let jsonValues = [
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
  return jsonValues;
};
