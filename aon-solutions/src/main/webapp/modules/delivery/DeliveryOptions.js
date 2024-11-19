import { CONSTANT, MSG } from "../../environments/environments.js"

export const DELIVERY_SEARCH_OPTIONS = [
    {
      type: CONSTANT.DATE,
      name: "startDate",
      id: "startDate",
      title: MSG.FROM,
    },
    {
      type: CONSTANT.DATE,
      name: "endDate",
      id: "endDate",
      title: MSG.TO,
    },
    {
      type: CONSTANT.SELECT,
      name: "status",
      id: "status",
      title: MSG.STATUS,
      options: JSON.stringify([
        { name: "-", value: undefined },
        { name: MSG.PENDING, value: "PENDING" },
        { name: MSG.INVOICED, value: "INVOICED" },
        { name: MSG.IN_PREPARATION, value: "IN_PREPARATION" },
      ])
    },
  ];