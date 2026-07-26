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

const AON_SELLER_LIST = {
    id: 'sideNavSeller',
    name: "Agentes",
    icon: MATERIAL_ICONS.SUPPORT_AGENT
}

const AON_SELLER_WORKLOAD = {
    id: 'sideNavSellerWorkload',
    name: "Cargas de Trabajo",
    icon: MATERIAL_ICONS.PERSON_PLAY
}

const AON_SELLER_ASSIGN_SCOPE = {
	id: 'sideNavSellerAssignScope',
    name: "Reasignar Ámbitos a Agentes",
    icon: MATERIAL_ICONS.PERSON_PLAY
}

const AON_SCOPE = {
    id: 'sideNavScope',
    name: "Ámbitos",
    icon: MATERIAL_ICONS.ACTIVITY_ZONE
}

const AON_CUSTOMER_STATUS= {
    id: 'sideNavTag',
    name: "Estados Cliente",
    icon: MATERIAL_ICONS.INFO
}

const AON_CUSTOMER_PAYROLL_ACTIVITY = {
	id: 'sideNavCustomerPayrollActivity',
    name: "Actividad Laboral Cliente",
    icon: MATERIAL_ICONS.BROWSE_ACTIVITY
}

const AON_SERVICE = {
    id: 'sideNavService',
    name: 'Servicios Despacho',
    icon: MATERIAL_ICONS.SHOPPING_BAG
}

const AON_SALES_ENTERPRISE = {
    id: 'sideNavSalesEnterprise',
    name: 'Procesar Pedidos',
    icon: MATERIAL_ICONS.EMIT
}

const AON_TARGET_ENTERPRISE = {
    id: 'sideNavTargetEnterprise',
    name: 'Procesar C. Potenciales',
    icon: MATERIAL_ICONS.DOMAIN_ADD
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
    AON_WORKGROUP_LIST,
    AON_SELLER_LIST,
    AON_SELLER_WORKLOAD,
    AON_SELLER_ASSIGN_SCOPE,
    AON_SCOPE,
    AON_CUSTOMER_STATUS,
    AON_CUSTOMER_PAYROLL_ACTIVITY
};

const ServiceOptions = {
    AON_SERVICE,
    AON_SALES_ENTERPRISE,
    AON_TARGET_ENTERPRISE
}


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
    CustomerFilter,
    ServiceOptions
}

