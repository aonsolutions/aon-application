import { CONSTANT, MATERIAL_ICONS, MSG } from "../../environments/environments.js"
import { Workgroup } from "../../models/project/Workgroup.js";

export const gwtLoad = (option) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    document.body.classList.add('gwt-Selector');
    GWT.iLoad(option, application.CONTENT);
}

export const customerList = (filter) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildCustomerToolbarOptions();
    parent.buildCustomerList(filter);
}

export const taskHolderList = (filter) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildTaskHolderToolbarOptions();
    parent.buildTaskHolderList(filter);
}

export const workgroup = () => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildWorkgroup();
}

export const linkDomains = () => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildLinkDomainsToolbarOptions();
    parent.buildLinkDomains();
}

export const LINK_DOMAINS = {
    id: CONSTANT.LINK_DOMAINS.initCap(),
    name: MSG.LINK_DOMAINS,
    icon: MATERIAL_ICONS.DATASET_LINKED
}

export const BOOKING_PANEL = {
    id: CONSTANT.BOOKING_PANEL.initCap(),
    name: MSG.BOOKING_PANEL,
    icon: MATERIAL_ICONS.THUNDERSTORM,
    fn: () => gwtLoad(GWT.BOOKING_PANEL)
}

export const AON_SERVICES = {
    id: CONSOLE.AON_SERVICES.initCap(),
    name: MSG.AON_SERVICES,
    icon: MATERIAL_ICONS.SHOPPING_BAG,
    fn: () => gwtLoad(GWT.PRODUCT_MODULE)

}

export const CONSOLE = {
    id: CONSTANT.CONSOLE.initCap(),
    title: MSG.CONSOLE,
    name: MSG.CONSOLE,
    options: [AON_SERVICES, LINK_DOMAINS, BOOKING_PANEL]
}

export const CUSTOMER = {
    id: CONSTANT.CUSTOMER.initCap(),
    name: MSG.CUSTOMERS,
    icon: MATERIAL_ICONS.CONTACT_PAGE,
    fn: () => customerList()
}

const TASK_HOLDER = {
    id: CONSTANT.TASK_HOLDER.initCap(),
    name: "Operarios",
    icon: MATERIAL_ICONS.PEOPLE,
    fn: () => taskHolderList()
}

const WORKGROUP = {
    id: CONSTANT.WORKGROUP.initCap(),
    name: "Grupos de Trabajo",
    icon: MATERIAL_ICONS.GROUPS,
    fn: () => workgroupList()
}

export const OFFICE = {
    id: CONSTANT.OFFICE.initCap(),
    title: MSG.OFFICE,
    name: MSG.OFFICE,
    options: [CUSTOMER, TASK_HOLDER, WORKGROUP]
}


export const getOptions = (beta, sig) => {
    let options = [];
    if(sig) options.push(CONSOLE);
    options.push(OFFICE);
    return options;
}