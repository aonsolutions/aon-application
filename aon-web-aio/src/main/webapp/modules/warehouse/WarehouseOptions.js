import { CONSTANT, MATERIAL_ICONS, MSG } from "../../environments/environments.js"
import { WAREHOUSE } from "../../services/app.js"

export const INVENTORY = {
  id: CONSTANT.INVENTORY.initCap(),
  name: MSG.INVENTORY,
  icon: MATERIAL_ICONS.INVENTORY
}

export const ELABORATION = {
  id: CONSTANT.ELABORATION.initCap(),
  name: MSG.ELABORATION,
}

export const SALES_PREPARATION = {
  id: CONSTANT.SALES_PREPARATION.initCap(),
  name: MSG.SALES_PREPARATION,
  icon: MATERIAL_ICONS.SHOPPING_BAG
}

export const DELIVERY = {
  id: CONSTANT.DELIVERY.initCap(),
  name: MSG.DELIVERY,
}

export const PACKAGING = {
  id: CONSTANT.PACKAGING.initCap(),
  name: MSG.PACKAGING,
}

export const TAGS = {
  id: CONSTANT.TAGS.initCap(),
  name: MSG.TAGS,
}

export const CARRIER = {
  id: CONSTANT.CARRIER.initCap(),
  name: MSG.CARRIERS,
}

export const PRODUCT = {
  id: CONSTANT.PRODUCT.initCap(),
  name: MSG.PRODUCTS,
}

export const PACKAGE = {
  id: CONSTANT.PACKAGE.initCap(),
  name: MSG.PACKAGES,
}

export const WarehouseSidenav = {
  WAREHOUSES: {
    id: CONSTANT.WAREHOUSES.initCap(),
    name: MSG.WAREHOUSES.toUpperCase(),
    app: WAREHOUSE
  },
  ELABORATION: {
    id: CONSTANT.ELABORATION.initCap(),
    name: MSG.ELABORATION.toUpperCase(),
    app: WAREHOUSE,
    options: [ELABORATION, PACKAGING, DELIVERY, INVENTORY, TAGS]
  },
  OTHER: {
    id: CONSTANT.OTHER.initCap(),
    name: MSG.OTHERS.toUpperCase(),
    app: WAREHOUSE,
    options: [PRODUCT, PACKAGE, CARRIER]
  }
}
export const WarehouseOptions = {
  ELABORATION: {
    id: CONSTANT.ELABORATION.initCap(),
    name: MSG.ELABORATION.toUpperCase(),
    app: WAREHOUSE,
    options: [ELABORATION, PACKAGING, SALES_PREPARATION, DELIVERY, INVENTORY, TAGS]
  }
}
