import { post, get, remove, openFile } from "./request.js";
import { API_URL } from "../environments/environments.js";
import { formatDateOrigin} from "./utils.js";

export const getTimeControl = (data) => get(`${API_URL}/timecontrol`, data);
export const saveTimeControl = (data) => post(`${API_URL}/timecontrol`, data);
export const saveTimeControlDetail = (data) => post(`${API_URL}/timecontrol/save`, data);
export const deleteTimeControl = (data) => remove(`${API_URL}/timecontrol`, data);
export const getTimeControlDetail = (data) => get(`${API_URL}/timecontrol/list-holder-detail`, data);

export const getTimeControlExcel = (data) => openFile(`${API_URL}/timecontrol/excel`, data);

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


export const getTimeControlList = (data) =>
  get(`${API_URL}/timecontrol/list`, data);

export const getTaskHolderTimeControl = (data) =>
  get(`${API_URL}/timecontrol/list-holder`, data);

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
    if (data) {
      jsonValues = jsonValues.find((f) => f.value.indexOf(data) >= 0);
    }
    resolve(jsonValues);
  });

export const getWeekDayObj = () =>  ({
    now: new Date(),
    dayWeekFirst: new Date().getFirstDayOfWeek().getDate(),
    dayWeekLast: new Date().getLastDayOfWeek().getDate()
});

export const getPeriod = (data) => {
  const {now, dayWeekFirst, dayWeekLast} = getWeekDayObj(); 
  const year = now.getFullYear();
  const month = now.getMonth();
  let jsonValues = [
    {
      name: "Hoy",
      value: "today",
      startDate: formatDateOrigin(now),
      endDate: formatDateOrigin(now)
    },
    {
      name: "Ayer",
      value: "yesterday",
      startDate: formatDateOrigin( new Date().addDay(-1)),
      endDate: formatDateOrigin(new Date().addDay(-1))
    },
    {
      name: "Semana actual",
      value: "this_week",
      startDate: formatDateOrigin( new Date().setDate(dayWeekFirst) ),
      endDate: formatDateOrigin( new Date().setDate(dayWeekLast) )
    },
    {
      name: "Semana anterior",
      value: "last_week",
      startDate: formatDateOrigin(  new Date().setDate(dayWeekFirst -7)  ),
      endDate: formatDateOrigin( new Date().setDate(dayWeekLast -7) )
    },
    {
      name: "Mes actual",
      value: "this_month",
      startDate: formatDateOrigin(new Date(year, month, 1)),
      endDate: formatDateOrigin(new Date(year, month + 1, 0))
    },
    {
      name: "Mes anterior",
      value: "last_month",
      startDate: formatDateOrigin(new Date(year, (month -1), 1)),
      endDate: formatDateOrigin(new Date(year, (month-1) + 1, 0))
    },
    {
      name: "Año actual",
      value: "this_year",
      startDate: formatDateOrigin(new Date(year, 0, 1)),
      endDate: formatDateOrigin(new Date(year, 12, 0))
    },
    {
      name: "Año anterior",
      value: "last_year",
      startDate: formatDateOrigin(new Date(year-1, 0, 1)),
      endDate:formatDateOrigin(new Date(year-1, 12, 0))
    },
    {
      name: "Personalizado",
      value: "personalized",
    },
  ];
  if (data) {
    jsonValues = jsonValues.find((f) => f.value.indexOf(data) >= 0);
  }
    return jsonValues
}
