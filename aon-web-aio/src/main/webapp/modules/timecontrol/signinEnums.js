import { MATERIAL_ICONS } from "../../environments/environments.js";

export const SigninSidenav = {
  PRESENCE: {
    icon: "account_box",
    name: "Presencia",
    id: "presence",
  },
  LOCATION: {
    icon: "location_on",
    name: "Ubicaciones",
    id: "location",
  },
  ADD: {
    name: "Agregar",
    icon: MATERIAL_ICONS.ADD,
    id: MATERIAL_ICONS.ADD,
  },
  FILTER:{
    name: "Filter",
    icon: "tune",
    id: "filter",
  },
  MORE:{
    name: "Ver",
    icon: MATERIAL_ICONS.MORE_VERT,
    id: MATERIAL_ICONS.MORE_VERT,
  },
  SYNCHRONIZE:{
    name: "SYNCHRONIZE",
    icon: "autorenew",
    id: "SYNCHRONIZE",
  },
  EXCEL:{
    name: "Excel",
    aonIcon: "aon_excel",
    id: "excel",
  },
  PERIOD: {
    TODAY: {
      icon: "today",
      name: "Hoy",
      id: "today",
    },
    YESTERDAY: {
      icon: "today",
      name: "Ayer",
      id: "yesterday",
    },
    THIS_WEEK: {
      icon: "today",
      name: "Semana actual",
      id: "this_week",
    },
    LAST_WEEK: {
      icon: "today",
      name: "Semana pasada",
      id: "last_week",
    },
    THIS_MONTH: {
      icon: "today",
      name: "Mes actual",
      id: "this_month",
    },
  },
};

export const PRESENCE_FILTER = [
  {
    type: "select",
    id: "period",
    name: "period",
    title: "Período",
  },
  {
    type: "date",
    name: "startDate",
    id: "startDate",
    title: "Desde",
  },
  {
    type: "date",
    name: "endDate",
    id: "endDate",
    title: "Hasta",
  },
];

export const EVENT_LIST_FILTER = [
  {
    type: "select",
    id: "group",
    name: "group",
    title: "Agrupar por ",
  },
  ...PRESENCE_FILTER
];


export const SIGNIN_VIEWS = {
  AON_SIGN: "aonSign",
  AON_SIGNIN: "aonSignin",
  AON_PRESENCE_LIST:"aonPresenceList",
  AON_EVENT_LIST:"aonEventList",
  AON_EVENT_DETAIL_LIST:"aonEventDetailList",
  AON_EVENT_ADD:"aonEventAdd",
  AON_LOCATION_LIST:"aonLocationList",
  AON_LOCATION_ADD:"aonLocationAdd",
  AON_STATISTICS: "aonStatistics"
};

export const iconAddLocation = "add_location";