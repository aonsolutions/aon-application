import * as APP from  "aonsolutions/services/app.js";
import { MATERIAL_ICONS, MSG, CONSTANT, AON_ICONS } from "aonsolutions/environments/environments.js";


export const HOME = {
	home : true,
	title: MSG.HOME,
	app: CONSTANT.HOME,
	color: "var(--aonGrayHeaderButtonsColor)", 
	symbol: MATERIAL_ICONS.HOME,
};

export const APPS = {
	home : true,
	app: CONSTANT.APPS,
	title: MSG.APPLICATIONS,
	color: "var(--aonGrayHeaderButtonsColor)",
	symbol: MATERIAL_ICONS.APPS,
};

export const APPLICATIONS = {
	home : true,
	app: CONSTANT.APPLICATIONS,
	color: "var(--aonGrayHeaderButtonsColor)",
	symbol: MATERIAL_ICONS.APPLICATIONS,
};

export const AON_APPS = [ 
	APP.AON_SOLUTIONS, 
	APP.BIDOQ, 
	APP.SELFCONTA, 
	APP.AON_SALTRA,
];

export const MENU_APPS = [
  HOME,
  APP.INVOICE,
  APP.DOCUMENTAL,
  APP.ACCOUNTING,
  APP.FISCAL,
  APP.PAYROLL,
  APP.TIMECONTROL,
  APP.NOTES,
  APP.MESSENGER
];



export const Apps = APP.Apps;
export const AuxApps = APP.AuxApps;
export const MenuApps = APP.MenuApps;

export const HomeApps = {
	HOME,
	APPS,
	APPLICATIONS
};


export const COMMERCIAL_MENU = {
	app: "comercialMenu",
	icon: AON_ICONS.AON_KIT_DIGITAL,
	symbol: MATERIAL_ICONS.HANDSHAKE,
	title: MSG.COMMERCIAL,
	subtitle: "Comercial",
	color: "var(--aonTopMenuAvailable)",
	apps: [],
	price: " ",
};

export const MANAGEMENT_MENU = {
	app: "managementMenu",
	icon: AON_ICONS.AON_KIT_DIGITAL,
	symbol: MATERIAL_ICONS.MONITORING,
	title: MSG.MANAGEMENT,
	subtitle: "Management",
	color: "var(--aonTopMenuAvailable)",
	apps: [],
	price: " ",
};

export const TREASURY_MENU = {
	app: "treasuryMenu",
	icon: AON_ICONS.AON_KIT_DIGITAL,
	symbol: MATERIAL_ICONS.ACCOUNT_BALANCE,
	title: MSG.TREASURY,
	subtitle: "Treasury",
	color: "var(--aonTopMenuAvailable)",
	apps: [],
	price: " ",
};

export const WAREHOUSE_MENU = {
	app: "warehouseMenu",
	icon: AON_ICONS.AON_KIT_DIGITAL,
	symbol: MATERIAL_ICONS.TROLLEY,
	title: MSG.WAREHOUSE,
	subtitle: "Warehouse",
	color: "var(--aonTopMenuAvailable)",
	apps: [],
	price: " ",
};

export const GROUPWARE_MENU = {
	app: "groupwareMenu",
	icon: AON_ICONS.AON_KIT_DIGITAL,
	symbol: MATERIAL_ICONS.NOTE_STACK,
	title: MSG.GROUPWARE,
	subtitle: "Groupware",
	color: "var(--aonTopMenuAvailable)",
	apps: [],
	price: " ",
};

export const ACCOUNTING_MENU = {
	app: "accountingMenu",
	icon: AON_ICONS.AON_KIT_DIGITAL,
	symbol: MATERIAL_ICONS.CALCULATE,
	title: MSG.ACCOUNTING,
	subtitle: "Accounting",
	color: "var(--aonTopMenuAvailable)",
	apps: [],
	price: " ",
};

export const FISCAL_MENU = {
	app: "fiscalMenu",
	icon: AON_ICONS.AON_KIT_DIGITAL,
	symbol: MATERIAL_ICONS.EURO_SYMBOL,
	title: MSG.FISCAL,
	subtitle: "Fiscal",
	color: "var(--aonTopMenuAvailable)",
	apps: [],
	price: " ",
};

export const PAYROLL_MENU = {
	app: "payrollMenu",
	icon: AON_ICONS.AON_KIT_DIGITAL,
	symbol: MATERIAL_ICONS.GROUPS,
	title: MSG.PAYROLL,
	subtitle: "Payroll",
	color: "var(--aonTopMenuAvailable)",
	apps: [],
	price: " ",
};

export const MARKETING_MENU = {
	app: "marketingMenu",
	icon: AON_ICONS.AON_KIT_DIGITAL,
	symbol: MATERIAL_ICONS.CAMPAIGN,
	title: MSG.MARKETING,
	subtitle: "Marketing",
	color: "var(--aonTopMenuAvailable)",
	apps: [],
	price: " ",
};


export const TOP_MENU_APPS = [
	APP.OFFICE,
	APP.GARAGE,
	APP.ACADEMY,
	APP.COMMERCE,

	COMMERCIAL_MENU,
	MANAGEMENT_MENU,
	TREASURY_MENU,
	WAREHOUSE_MENU,
	GROUPWARE_MENU,
	ACCOUNTING_MENU,
	FISCAL_MENU,
	PAYROLL_MENU,
	MARKETING_MENU,
];

export default Apps;
