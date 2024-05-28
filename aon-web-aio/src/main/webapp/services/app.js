import * as APP from  "aonsolutions/services/app.js";
import { MATERIAL_ICONS, MSG, CONSTANT, AON_ICONS } from "aonsolutions/environments/environments.js";


export const HOME = {
	home : true,
	title: MSG.HOME,
	app: CONSTANT.HOME,
	symbol: MATERIAL_ICONS.HOME,
};

export const APPS = {
	home : true,
	app: CONSTANT.APPS,
	title: MSG.APPLICATIONS,
	symbol: MATERIAL_ICONS.APPS,
};

export const APPLICATIONS = {
	home : true,
	app: CONSTANT.APPLICATIONS,
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
  APP.DOCUMENTAL,
  APP.ACCOUNTING,
  APP.FISCAL,
  APP.PAYROLL,
  APP.INVOICE,
  APP.TIMECONTROL,
  APP.NOTES,
];



export const Apps = APP.Apps;
export const AuxApps = APP.AuxApps;
export const MenuApps = APP.MenuApps;

export const HomeApps = {
	HOME,
	APPS,
	APPLICATIONS
};


export const COMERCIAL_MENU = {
	app: "comercial",
	icon: AON_ICONS.AON_KIT_DIGITAL,
	symbol: MATERIAL_ICONS.HANDSHAKE,
	title: MSG.COMMERCIAL,
	subtitle: "Comercial",
	color: "var(--aonBlue)",
	apps: [],
	price: " ",
};

export const MANAGEMENT_MENU = {
	app: "management",
	icon: AON_ICONS.AON_KIT_DIGITAL,
	symbol: MATERIAL_ICONS.MONITORING,
	title: MSG.MANAGEMENT,
	subtitle: "Management",
	color: "var(--aonBlue)",
	apps: [],
	price: " ",
};

export const TREASURY_MENU = {
	app: "treasury",
	icon: AON_ICONS.AON_KIT_DIGITAL,
	symbol: MATERIAL_ICONS.ACCOUNT_BALANCE,
	title: MSG.TREASURY,
	subtitle: "Treasury",
	color: "var(--aonBlue)",
	apps: [],
	price: " ",
};

export const WAREHOUSE_MENU = {
	app: "warehouse",
	icon: AON_ICONS.AON_KIT_DIGITAL,
	symbol: MATERIAL_ICONS.TROLLEY,
	title: MSG.WAREHOUSE,
	subtitle: "Warehouse",
	color: "var(--aonBlue)",
	apps: [],
	price: " ",
};

export const GROUPWARE_MENU = {
	app: "groupware",
	icon: AON_ICONS.AON_KIT_DIGITAL,
	symbol: MATERIAL_ICONS.NOTE_STACK,
	title: MSG.GROUPWARE,
	subtitle: "Groupware",
	color: "var(--aonBlue)",
	apps: [],
	price: " ",
};

export const ACCOUNTING_MENU = {
	app: "accountingMenu",
	icon: AON_ICONS.AON_KIT_DIGITAL,
	symbol: MATERIAL_ICONS.CALCULATE,
	title: MSG.ACCOUNTING,
	subtitle: "Accounting",
	color: "var(--aonBlue)",
	apps: [],
	price: " ",
};

export const FISCAL_MENU = {
	app: "fiscal",
	icon: AON_ICONS.AON_KIT_DIGITAL,
	symbol: MATERIAL_ICONS.EURO_SYMBOL,
	title: MSG.FISCAL,
	subtitle: "Fiscal",
	color: "var(--aonBlue)",
	apps: [],
	price: " ",
};

export const PAYROLL_MENU = {
	app: "payroll",
	icon: AON_ICONS.AON_KIT_DIGITAL,
	symbol: MATERIAL_ICONS.GROUPS,
	title: MSG.PAYROLL,
	subtitle: "Payroll",
	color: "var(--aonBlue)",
	apps: [],
	price: " ",
};

export const MARKETING_MENU = {
	app: "marketing",
	icon: AON_ICONS.AON_KIT_DIGITAL,
	symbol: MATERIAL_ICONS.CAMPAIGN,
	title: MSG.MARKETING,
	subtitle: "Marketing",
	color: "var(--aonBlue)",
	apps: [],
	price: " ",
};


export const TOP_MENU_APPS = [
	APP.OFFICE,
	APP.GARAGE,
	APP.ACADEMY,
	APP.COMMERCE,

	COMERCIAL_MENU,
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
