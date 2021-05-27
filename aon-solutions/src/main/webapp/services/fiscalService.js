import { get, post } from "./request.js";
import { API_URL } from "../environments/environments.js";

// export const getModelsFiscal = (data) =>
//   new Promise((resolve) => {
//     resolve([
//       {
//         id: "301",
//         domain: "3305",
//         administration: "ALAVA",
//         model: "IVA",
//         year: "2021",
//         period: "M01",
//         status: "PENDING",
//         complementary: false,
//         replacement: false,
//         document: "777777777",
//         name: "DECLARANTE_NAME",
//         surname: "DECLARANTE_SURNAME",
//         type: "DEPOSIT",
//         result: 1231.23,
//         iban: "12312389893774766883",
//         nrc: "",
//       },
//       {
//         id: "302",
//         domain: "3305",
//         administration: "COMMON_TERRITORY",
//         model: "202",
//         year: "2020",
//         period: "T1",
//         status: "FINISHED",
//         complementary: false,
//         replacement: false,
//         document: "888888",
//         name: "DECLARANTE_NAME",
//         surname: "DECLARANTE_SURNAME",
//         type: "BANK",
//         result: 6551.84,
//         iban: "9999123",
//         nrc: "",
//       },
//       {
//         id: "303",
//         domain: "3305",
//         administration: "GIPUZKOA",
//         model: "130",
//         year: "2020",
//         period: "T2",
//         status: "SENT",
//         complementary: false,
//         replacement: false,
//         document: "77777",
//         name: "DECLARANTE_NAME",
//         surname: "DECLARANTE_SURNAME",
//         type: "NEGATIVE",
//         result: 2523.84,
//         iban: "9999123",
//         nrc: "",
//       },
//     ]);
//   });

export const getModelsFiscal = (data) => get(`${API_URL}/fiscal/models`, data);

export const setModelStatus = (data) =>
  post(`${API_URL}/fiscal/markAsFinished`, data);
