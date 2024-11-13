import * as APP from  "aonsolutions/services/app.js";
import { MATERIAL_ICONS, MSG, CONSTANT, AON_ICONS } from "aonsolutions/environments/environments.js";


export const NEW = {
	home : true,
	title: MSG.NEW,
	description: MSG.NEW,
	app: CONSTANT.NEW,
	symbol: MATERIAL_ICONS.ADD_CIRCLE_OUTLINE,
};

export const NOTIFICATION = {
	app: "notification",
	symbol: MATERIAL_ICONS.NOTIFICATIONS,
	title: MSG.NOTIFICATIONS,
	subtitle: "Notification",
}

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

export const AON_CLASSIC = {
	app: CONSTANT.AON_APPLICATION,
	title: MSG.CLASSIC_VIEW,
	description: MSG.CLASSIC_VIEW,
	logo: "../assets/aon.png",
};

export const NEW_APPS = {
	app: CONSTANT.APPS,
	title: MSG.APPLICATIONS,
	description: MSG.APPLICATIONS,
	symbol: MATERIAL_ICONS.APPS
}

export const AON_APPS = [ 
	APP.AON_SOLUTIONS, 
	APP.BIDOQ, 
	APP.SELFCONTA, 
	APP.AON_SALTRA,
];

export const MENU_APPS = [
  HOME,
  NEW_APPS,
  NEW,
  APP.INVOICE,
  APP.DOCUMENTAL,
  APP.ACCOUNTING,
  APP.FISCAL,
  APP.PAYROLL,
  APP.COMUNICA,
  APP.TIMECONTROL,
  APP.NOTES,
  APP.MESSENGER,
  APP.WAREHOUSE,
  AON_CLASSIC
];

export const DESKTOP_APPS = [
  APP.INVOICE,
  APP.DOCUMENTAL,
  APP.ACCOUNTING,
  APP.FISCAL,
  APP.PAYROLL,
  APP.TIMECONTROL,
  APP.NOTES,
  APP.MESSENGER,
  AON_CLASSIC
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
	symbol: MATERIAL_ICONS.HANDSHAKE,
	title: MSG.COMMERCIAL,
	description: MSG.COMMERCIAL,
	subtitle: "Comercial",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavCommercialButton",
	apps: [],
	price: " ",
};

export const MANAGEMENT_MENU = {
	app: "managementMenu",
	symbol: MATERIAL_ICONS.MONITORING,
	title: MSG.MANAGEMENT,
	description: MSG.MANAGEMENT,
	subtitle: "Management",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavManagementButton",
	apps: [],
	price: " ",
};

export const TREASURY_MENU = {
	app: "treasuryMenu",
	symbol: MATERIAL_ICONS.ACCOUNT_BALANCE,
	title: MSG.TREASURY,
	description: MSG.TREASURY,
	subtitle: "Treasury",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavTreasuryButton",
	apps: [],
	price: " ",
};

export const WAREHOUSE_MENU = {
	app: "warehouseMenu",
	symbol: MATERIAL_ICONS.TROLLEY,
	title: MSG.WAREHOUSE,
	description: MSG.WAREHOUSE,
	subtitle: "Warehouse",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavWarehouseButton",
	apps: [],
	price: " ",
};

export const GROUPWARE_MENU = {
	app: "groupwareMenu",
	symbol: MATERIAL_ICONS.NOTE_STACK,
	title: MSG.GROUPWARE,
	description: MSG.GROUPWARE,
	subtitle: "Groupware",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavGroupwareButton",
	apps: [],
	price: " ",
};

export const ACCOUNTING_MENU = {
	app: "accountingMenu",
	symbol: MATERIAL_ICONS.CALCULATE,
	title: MSG.ACCOUNTING,
	description: MSG.ACCOUNTING,
	subtitle: "Accounting",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavAccountingButton",
	apps: [],
	price: " ",
};

export const FISCAL_MENU = {
	app: "fiscalMenu",
	symbol: MATERIAL_ICONS.EURO_SYMBOL,
	title: MSG.FISCAL,
	description: MSG.FISCAL,
	subtitle: "Fiscal",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavFiscalButton",
	apps: [],
	price: " ",
};

export const PAYROLL_MENU = {
	app: "payrollMenu",
	symbol: "group",
	title: MSG.PAYROLL,
	description: MSG.PAYROLL,
	subtitle: "Payroll",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavPayrollButton",
	apps: [],
	price: " ",
};

export const MARKETING_MENU = {
	app: "marketingMenu",
	symbol: "ads_click",
	title: MSG.MARKETING,
	description: MSG.MARKETING,
	subtitle: "Marketing",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavMarketingButton",
	apps: [],
	price: " ",
};

export const CONFIGURATION_MENU = {
	app: "configurationMenu",
	symbol: "construction",
	title: MSG.CONFIGURATION,
	description: MSG.CONFIGURATION,
	subtitle: "Configuration",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavConfigurationButton",
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
	CONFIGURATION_MENU,
];

export default Apps;
