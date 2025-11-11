import { CONSTANT, MSG } from "../../environments/environments.js"

export const DELIVERY_SEARCH_OPTIONS = [
    {
      type: CONSTANT.NEW_DATE,
      name: "startDate",
      id: "startDate",
      title: MSG.FROM,
    },
    {
      type: CONSTANT.NEW_DATE,
      name: "endDate",
      id: "endDate",
      title: MSG.TO,
    },
    {
      type: CONSTANT.SELECT,
      name: "status",
      id: "status",
      title: MSG.STATUS
    },
  ];