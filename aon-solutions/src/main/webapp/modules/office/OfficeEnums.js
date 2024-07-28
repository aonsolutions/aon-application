import { AonCheckbox } from "../../components/aon-checkbox.js";
import { AON_WORKGROUP } from "../../environments/aonTag.js";
import { CONSTANT, MATERIAL_ICONS, MSG } from "../../environments/environments.js";


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

const AON_WORKGROUP_LIST = {
    id: 'sideNavWorkgroup',
    name: "Grupos de Trabajo",
    icon: MATERIAL_ICONS.GROUPS
}


const OfficeViews = {
    AON_CUSTOMER: "aonCustomerOffice",
    AON_CUSTOMER_LIST: "aonCustomerListOffice",
    AON_OFFICE_PANEL: "aonOfficePanel",
    AON_TASK_HOLDER: "aonTaskHolder",
    AON_TASK_HOLDER_LIST: "aonTaskHolderList",
    AON_WORKGROUP_LIST : "aonWorkgroupList"
}

const OfficeSidenav = {
    MORE_VERT:{
        name: MSG.OPTIONS,
        icon: MATERIAL_ICONS.MORE_VERT,
        id: "new_options", 
    }
    // ADD_FOLDER: {
    //     name: "Agregar expediente",
    //     icon: "create_new_folder",
    //     id: "create_new_folder",
    // },
}


const OfficeOptions = {
    AON_CUSTOMER,
    AON_TASK_HOLDER,
    AON_WORKGROUP_LIST
};


const getButtonsStatus = () => {
    const id = "htmlElementCustom";
    let div = document.getElementById(id) || document.createElement("div");
    div.id = id;
    div.style.display = "flex";
    div.style.columnGap = "10px";
    div.innerHTML ="";

    let active =  new AonCheckbox();
    active.id = "active";
    active.name = "active";
    active.description = MSG.ACTIVE;
    div.appendChild(active);

    let inactive =  new AonCheckbox();
    inactive.id = "inactive";
    inactive.name = "inactive";
    inactive.description = MSG.INACTIVE;
    div.appendChild(inactive);

    let blocked =  new AonCheckbox();
    blocked.id = "blocked";
    blocked.name = "blocked";
    blocked.description = "Bloqueado";
    div.appendChild(blocked);

    return div;
}

const CustomerFilter = [
    {
        type: CONSTANT.SELECT,
        id: "scope",
        name: "scope",
        title: MSG.SCOPE,
        autocomplete: true,
        default:true,
        emptyclear:true
    },
    {
        type:CONSTANT.SELECT,
        id: "projectType",
        name: "projectType",
        title: "Tipo de expediente",
        autocomplete: true,
        default:true,
        emptyclear:true
    },
    {
        type:CONSTANT.SELECT,
        id: "rrelationship",
        name: "rrelationship",
        title: "Vinculo",
        autocomplete: true,
        default:true,
        emptyclear:true
    },
    {
        type: CONSTANT.SELECT,
        id: "type",
        name: "type",
        title: MSG.TYPE,
        autocomplete: true,
        default:true,
        emptyclear:true
    },
    {
        type: CONSTANT.HTML_ELEMENT,
        id: CONSTANT.HTML_ELEMENT,
        element: getButtonsStatus()
    }
];

export const OfficeEnums = {
    OfficeViews,
    OfficeOptions,
    OfficeSidenav,
    CustomerFilter
}

