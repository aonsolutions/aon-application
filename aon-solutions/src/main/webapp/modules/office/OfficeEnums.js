import { MATERIAL_ICONS, MSG } from "../../environments/environments.js";



const AON_CUSTOMER = {
    id: 'sideNavcustomer',
    name: MSG.CUSTOMERS,
    icon: MATERIAL_ICONS.CONTACT_PAGE
}
  
const AON_TASK_HOLDER = {
    id: 'sideNavTaskHolder',
    name: "Operarios",
    icon: MATERIAL_ICONS.PEOPLE
}
  

const OfficeViews = {
    AON_CUSTOMER: "aonCustomer",
    AON_CUSTOMER_LIST: "aonCustomerList",
    AON_OFFICE_PANEL: "aonOfficePanel",
    AON_TASK_HOLDER: "aonTaskHolder",
    AON_TASK_HOLDER_LIST: "aonTaskHolderList",
}


const OfficeOptions = {
    AON_CUSTOMER,
    AON_TASK_HOLDER
};

export const OfficeEnums = {
    OfficeViews,
    OfficeOptions
}

