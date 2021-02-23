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
    icon: "add",
    id: "add",
  },
  FILTER:{
    name: "Filter",
    icon: "tune",
    id: "filter",
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
      name: "Ésta semana",
      id: "this_week",
    },
    THIS_MONTH: {
      icon: "today",
      name: "Éste mes",
      id: "this_month",
    },
  },
};

export const PresenceFilterInput = [
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

export const EventListFilterInput = [
  {
    type: "select",
    id: "group",
    name: "group",
    title: "Agrupar por ",
  },
  ...PresenceFilterInput
];
