import { MATERIAL_ICONS } from "../../environments/environments.js";

const TaskHolderSidenav = {
  ADD: {
    name: "Agregar",
    icon: MATERIAL_ICONS.ADD,
    id: MATERIAL_ICONS.ADD,
  },
  FILTER:{
    name: "Filter",
    icon: "tune",
    id: "filter",
  }
};

const TASK_HOLDER_FILTER = [
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


const TASK_HOLDER_VIEWS = {
  AON_TASK_HOLDER: "aonTaskHolder",
  AON_TASK_HOLDER_LIST: "aonTaskHolderList",
};

export const TaskHolderEnums = {
    TASK_HOLDER_VIEWS,
    TASK_HOLDER_FILTER,
    TaskHolderSidenav
}