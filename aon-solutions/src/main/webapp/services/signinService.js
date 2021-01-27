import { post, get } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getTimeControl = (data) => get(`${API_URL}/timecontrol`, data);
export const saveTimeControl = (data) => post(`${API_URL}/timecontrol`, data);

export const getSigninStatus = () => {
  let date = new Date();
  let started = localStorage.getItem("aon-signin-started");
  let time = localStorage.getItem("aon-signin-time") || 0;
  if (started) {
    let startedDate = new Date(Number(started));
    if (date.getDate() !== startedDate.getDate()) {
      startedDate = new Date(
        date.getFullYear(),
        date.getMonth(),
        date.getDate(),
        0,
        0,
        0,
        0
      );
      started = startedDate.getTime();
      localStorage.setItem("aon-signin-started", started);
    }
    time = Number(time) + (date.getTime() - Number(started));
  }
  let signin = {
    status: localStorage.getItem("aon-signin-status") || "out",
    time: time,
    started: started,
  };
  return new Promise((resolve, reject) => {
    resolve(signin);
  });
};

export const updateSigninStatus = (data) => {
  let date = new Date();
  localStorage.setItem("aon-signin-status", data.status);
  if (data.status !== "in") {
    let started = localStorage.getItem("aon-signin-started");
    let time = localStorage.getItem("aon-signin-time") || 0;
    time = Number(time) + (date.getTime() - Number(started));
    localStorage.setItem("aon-signin-time", time);
    localStorage.removeItem("aon-signin-started");
  } else {
    localStorage.setItem("aon-signin-started", date.getTime());
  }
};

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

// export const getTimeControlList = (data) => get(`${API_URL}/timecontrol/list`, data);
//delete test
export const getTimeControlList = (data) =>
  new Promise((resolve) => {
    let jsonValues = [
      {
        id: 1,
        name: "NOMBRE",
        last_name1: "APELLIDO",
        time: "16116",
        taskHolderId: "Y11111X",
        status: "in",
        location: "VITORIA",
        department: "aplicaciones",
        date: new Date(),
      },
      {
        id: 2,
        name: "NOMBRE",
        last_name1: "APELLIDO",
        time: "16116",
        taskHolderId: "Y2222X",
        status: "out",
        location: "MADRID",
        department: "aplicaciones",
        date: new Date(),
      },
      {
        id: 3,
        name: "NOMBRE",
        last_name1: "APELLIDO",
        time: "16116",
        taskHolderId: "Y3333X",
        status: "pause",
        location: "BARCELONA",
        department: "Jornada Parcial",
        date: new Date(),
      },
      {
        id: 4,
        name: "NOMBRE",
        last_name1: "APELLIDO",
        time: "16116",
        taskHolderId: "Y5555X",
        status: "in",
        location: "VITORIA",
        department: "aplicaciones",
        date: new Date(),
      },
      {
        id: 5,
        name: "NOMBRE",
        last_name1: "APELLIDO",
        time: "16116",
        taskHolderId: "Y6666X",
        status: "pause",
        location: "MADRID",
        department: "aplicaciones",
        date: new Date(),
      },
    ];

    resolve(jsonValues);
  });

// export const getTaskHolderTimeControl = (data) => get(`${API_URL}/timecontrol/list-holder`, data);

export const getTaskHolderTimeControl = (data) =>
  new Promise((resolve) => {
    let jsonValues = [
      {
        id: 1,
        name: "NOMBRE",
        last_name1: "APELLIDO",
        time: "16116",
        taskHolderId: "Y11111X",
        status: "in",
        location: "VITORIA",
        department: "aplicaciones",
        date: new Date(),
      },
      {
        id: 2,
        name: "NOMBRE",
        last_name1: "APELLIDO",
        time: "16116",
        taskHolderId: "Y2222X",
        status: "out",
        location: "MADRID",
        department: "aplicaciones",
        date: new Date(),
      },
      {
        id: 3,
        name: "NOMBRE",
        last_name1: "APELLIDO",
        time: "16116",
        taskHolderId: "Y3333X",
        status: "pause",
        location: "BARCELONA",
        department: "Jornada Parcial",
        date: new Date(),
      },
      {
        id: 4,
        name: "NOMBRE",
        last_name1: "APELLIDO",
        time: "16116",
        taskHolderId: "Y5555X",
        status: "in",
        location: "VITORIA",
        department: "aplicaciones",
        date: new Date(),
      },
      {
        id: 5,
        name: "NOMBRE",
        last_name1: "APELLIDO",
        time: "16116",
        taskHolderId: "Y6666X",
        status: "pause",
        location: "MADRID",
        department: "aplicaciones",
        date: new Date(),
      },
    ];
    let filters = jsonValues
      .filter((f) => f.taskHolderId.indexOf(data.taskHolderId) >= 0)
      .map((m) => {
        return { ...m };
      });

    resolve(filters);
  });
