import * as APP from  "aonsolutions/services/app.js";
import { MATERIAL_ICONS, MSG, CONSTANT, AON_ICONS } from "aonsolutions/environments/environments.js";


export const NEW = {
	home : true,
	title: MSG.NEW,
	description: MSG.NEW,
	app: CONSTANT.NEW,
	symbol: MATERIAL_ICONS.ADD_CIRCLE_OUTLINE,
};

export const HOME = {
	home : true,
	title: MSG.HOME,
	description: MSG.HOME,
	app: CONSTANT.HOME,
	symbol: MATERIAL_ICONS.HOME,
};

export const APPS = {
	home : true,
	app: CONSTANT.APPS,
	title: MSG.APPLICATIONS,
	description: MSG.APPLICATIONS,
	symbol: MATERIAL_ICONS.APPS,
};

export const APPLICATIONS = {
	home : true,
	title: MSG.APPLICATIONS,
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
  NEW,
  APP.INVOICE,
  APP.DOCUMENTAL,
  APP.ACCOUNTING,
  APP.FISCAL,
  APP.PAYROLL,
  APP.TIMECONTROL,
  APP.NOTES,
  APP.MESSENGER
];

export const DESKTOP_APPS = [
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
	style: "aonTopNavCommercialButton",
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
	style: "aonTopNavManagementButton",
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
	style: "aonTopNavTreasuryButton",
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
	style: "aonTopNavWarehouseButton",
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
	style: "aonTopNavGroupwareButton",
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
	style: "aonTopNavAccountingButton",
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
	style: "aonTopNavFiscalButton",
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
	style: "aonTopNavPayrollButton",
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
	style: "aonTopNavMarketingButton",
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
