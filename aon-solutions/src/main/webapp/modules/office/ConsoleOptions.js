import { CONSTANT, MATERIAL_ICONS, MSG } from "../../environments/environments.js"

export const LINK_DOMAINS = {
  id: CONSTANT.LINK_DOMAINS.initCap(),
  name: MSG.LINK_DOMAINS,
  icon: MATERIAL_ICONS.DATASET_LINKED
}

export const BOOKING_PANEL = {
  id: CONSTANT.BOOKING_PANEL.initCap(),
  name: MSG.BOOKING_PANEL,
  icon: MATERIAL_ICONS.THUNDERSTORM
}

export const ConsoleSidenav = {
  CONSOLE: {
    id: CONSTANT.CONSOLE.initCap(),
    name: MSG.CONSOLE.toUpperCase(),
    options: [LINK_DOMAINS, BOOKING_PANEL]
  }
}
