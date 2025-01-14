import { CONSTANT, MATERIAL_ICONS, MSG } from "../../environments/environments.js"

export const EXAMPLE = {
  id: CONSTANT.EXAMPLE.initCap(),
  name: MSG.EXAMPLE,
  icon: MATERIAL_ICONS.PERSON
}

export const ExampleSidenav = {
  EXAMPLE: {
    id: CONSTANT.EXAMPLE.initCap(),
    name: MSG.EXAMPLE.toUpperCase(),
    options: [EXAMPLE]
  }
}