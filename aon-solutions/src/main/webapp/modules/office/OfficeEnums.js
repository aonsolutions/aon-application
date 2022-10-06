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
    AON_CUSTOMER: "aonCustomerOffice",
    AON_CUSTOMER_LIST: "aonCustomerListOffice",
    AON_OFFICE_PANEL: "aonOfficePanel",
    AON_TASK_HOLDER: "aonTaskHolder",
    AON_TASK_HOLDER_LIST: "aonTaskHolderList",
}

const OfficeSidenav = {
    ADD_FOLDER: {
        name: "Agregar expediente",
        icon: "create_new_folder",
        id: "create_new_folder",
    },
}



const OfficeOptions = {
    AON_CUSTOMER,
    AON_TASK_HOLDER
};

export const OfficeEnums = {
    OfficeViews,
    OfficeOptions,
    OfficeSidenav
}

