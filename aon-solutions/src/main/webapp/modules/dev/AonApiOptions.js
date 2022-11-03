import { AON_ICONS, CONSTANT, MATERIAL_ICONS, MSG } from "../../environments/environments.js"

export const INVOICES = {
  id: CONSTANT.INVOICE.initCap(),
  name: MSG.INVOICES,
  aonIcon: {
    icon:  AON_ICONS.AON_INVOICE,
    color: '#4472C4'
  }
}

export const GET_INVOICES = {
  id: CONSTANT.GET_INVOICES.initCap(),
  name: MSG.GET_INVOICES,
  icon: MATERIAL_ICONS.LIST
}

export const GET_INVOICE = {
  id: CONSTANT.GET_INVOICE.initCap(),
  name: MSG.GET_INVOICE,
  icon: ''
}

export const CREATE_INVOICE = {
  id: CONSTANT.CREATE_INVOICE.initCap(),
  name: MSG.CREATE_INVOICE,
  icon: MATERIAL_ICONS.ADD
}

export const UPDATE_INVOICE = {
  id: CONSTANT.UPDATE_INVOICE.initCap(),
  name: MSG.UPDATE_INVOICE,
  icon: MATERIAL_ICONS.EDIT
}

export const DELETE_INVOICE = {
  id: CONSTANT.DELETE_INVOICE.initCap(),
  name: MSG.DELETE_INVOICE,
  icon: MATERIAL_ICONS.DELETE
}
  
export const INVOICE_OBJECT = {
  id: CONSTANT.INVOICE_OBJECT.initCap(),
  name: MSG.INVOICE_OBJECT,
  icon: MATERIAL_ICONS.DATA_OBJECT
}