import { post, get } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getTimeControl = (data) => get(`${API_URL}/timecontrol`, data);
export const saveTimeControl = (data) => post(`${API_URL}/timecontrol`, data);

export const getStatus = (data) =>
  new Promise((resolve) => {
    let jsonValues = [
      {
        name: "Entrada",
        value: "in",
      },
      {
        name: "Salida",
        value: "out",
      },
      {
        name: "Pausa",
        value: "pause",
      },
    ];
    if (data) {
      jsonValues = jsonValues.find((f) => f.value.indexOf(data) >= 0);
    }
    resolve(jsonValues);
  });

export const getTimeControlList = (data) => get(`${API_URL}/timecontrol/list`, data);

export const getTaskHolderTimeControl = (data) => get(`${API_URL}/timecontrol/list-holder`, data);

export const getGroups = (data) =>
  new Promise((resolve) => {
    let jsonValues = [
      {
        name: "DIA",
        value: "DAY",
      },
      {
        name: "SEMANA",
        value: "WEEK",
      },
      {
        name: "MES",
        value: "MONTH",
      },
      {
        name: "AÑO",
        value: "YEAR",
      },
    ];

    resolve(jsonValues);
});
