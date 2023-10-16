import { CONSTANT, MATERIAL_ICONS, MSG } from "../../environments/environments.js"
import { CONSOLE } from "../../services/app.js"

export const LINK_DOMAINS = {
  id: CONSTANT.LINK_DOMAINS.initCap(),
  name: MSG.LINK_DOMAINS,
  icon: MATERIAL_ICONS.DATASET_LINKED
}

export const ConsoleSidenav = {
  UTILITIES: {
    id: CONSTANT.UTILITIES.initCap(),
    name: MSG.UTILITIES.toUpperCase(),
    app: CONSOLE,
    options: [LINK_DOMAINS]
  }
}
