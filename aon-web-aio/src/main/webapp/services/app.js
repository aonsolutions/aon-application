import * as APP from  "aonsolutions/services/app.js";
import { MATERIAL_ICONS, MSG, CONSTANT, AON_ICONS, COLORS } from "aonsolutions/environments/environments.js";


export const HOME = {
	title: MSG.HOME,
	app: CONSTANT.HOME,
	symbol: MATERIAL_ICONS.HOME,
};

export const APPS = {
	app: CONSTANT.APPS,
	title: MSG.APPLICATIONS,
	symbol: MATERIAL_ICONS.APPS,
};

export const APPLICATIONS = {
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
  APPLICATIONS,
  HOME,
  APPS,
  APP.CALENDAR,
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

export default Apps;