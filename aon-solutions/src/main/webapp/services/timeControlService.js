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

//
//delete test
let dataJsonTimeControl = [
  {
    time: "16116",
    in_date: new Date(),
    status: "in",
    location: "VITORIA",
    task_holder: {
      id: 1,
      name: "NOMBRE APELLIDO",
    },
  },
  {
    time: "16116",
    in_date: new Date(),
    status: "out",
    location: "MADRID",
    task_holder: {
      id: 2,
      name: "NOMBRE APELLIDO",
    },
  },
  {
    time: "16116",
    in_date: new Date(),
    status: "pause",
    location: "BARCELONA",
    task_holder: {
      id: 3,
      name: "NOMBRE APELLIDO",
    },
  },
  {
    time: "16116",
    in_date: new Date(),
    status: "in",
    location: "MI CASA",
    task_holder: {
      id: 4,
      name: "NOMBRE APELLIDO",
    },
  },
  {
    time: "16116",
    in_date: new Date(),
    status: "out",
    location: "MADRID",
    task_holder: {
      id: 5,
      name: "NOMBRE APELLIDO",
    },
  },
];
// export const getTimeControlList = (data) => get(`${API_URL}/timecontrol/list`, data);
export const getTimeControlList = (data) =>
  new Promise((resolve) => {
    let jsonValues = dataJsonTimeControl;
    resolve(jsonValues);
  });

// export const getTaskHolderTimeControl = (data) => get(`${API_URL}/timecontrol/list-holder`, data);

export const getTaskHolderTimeControl = ({ taskHolderId }) =>
  new Promise((resolve) => {
    let jsonValues = dataJsonTimeControl;
    let filters = jsonValues
      .filter(({ task_holder }) => {
        return task_holder.id.toString().indexOf(taskHolderId) >= 0;
      })
      .map((m) => {
        return { ...m };
      });
    resolve(filters);
  });

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

export const getLocation = (data) =>
  new Promise((resolve) => {
    let jsonValues = [
      {
        id: 1,
        name: "VITORIA",
      },
      {
        id: 2,
        name: "MADRID",
      },
      {
        id: 3,
        name: "BARCELONA",
      },
    ];

    resolve(jsonValues);
  });
