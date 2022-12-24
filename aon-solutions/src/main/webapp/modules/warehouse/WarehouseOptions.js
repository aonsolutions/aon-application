import { CONSTANT, MATERIAL_ICONS, MSG } from "../../environments/environments.js"

export const ELABORATION = {
  id: CONSTANT.ELABORATION.initCap(),
  name: MSG.ELABORATION,
  icon: MATERIAL_ICONS.PRECISION_MANUFACTURING
}

export const SALES_PREPARATION = {
  id: CONSTANT.SALES_PREPARATION.initCap(),
  name: MSG.SALES_PREPARATION,
  icon: MATERIAL_ICONS.SHOPPING_BAG
}

export const PACKAGING = {
  id: CONSTANT.PACKAGING.initCap(),
  name: MSG.PACKAGING,
  icon: MATERIAL_ICONS.PALLET
}

export const TAGS = {
  id: CONSTANT.TAGS.initCap(),
  name: MSG.TAGS,
  icon: MATERIAL_ICONS.PRINT
}

export const WarehouseSidenav = {
  WAREHOUSES: {
    id: CONSTANT.WAREHOUSES.initCap(),
    name: MSG.WAREHOUSES.toUpperCase()
  },
  ELABORATION: {
    id: CONSTANT.ELABORATION.initCap(),
    name: MSG.ELABORATION.toUpperCase(),
    options: [ELABORATION, PACKAGING, SALES_PREPARATION, TAGS]
  }
}
