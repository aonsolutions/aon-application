import { CONSTANT, MATERIAL_ICONS, MSG, TAG } from "../../environments/environments";
import Apps from "../../services/app";
import { SIGNIN_VIEWS, SigninSidenav } from "./signinEnums";
import * as JSF from "../aon-jsf-app.js";
import * as GWT from '../../gwt/gwt.js';

  export const showView = (view) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.showView(view)
  }

  export const taskHolder = () => {
	let application = document.querySelector(TAG.AON_APPLICATION);
    application.removeToolbarOptions();
    GWT.iLoad(GWT.TASK_HOLDER_MODULE, application.CONTENT);
  }
  
  export const workplacesCalendar = () => {
	let application = document.querySelector(TAG.AON_APPLICATION);
    application.removeToolbarOptions();
    GWT.iLoad(GWT.WORKPLACES_CALENDAR, application.CONTENT);
  }

  export const PRESENCE = {
    id: CONSTANT.PRESENCE,
    name: MSG.PRESENCE,
    title: MSG.PRESENCE,
    icon: MATERIAL_ICONS.ACCOUNT_BOX,
    fn: () => showView(SIGNIN_VIEWS.AON_PRESENCE_LIST)
  };
  
  export const AGENDA = {
    id: 'timecontrol-agenda',
    name: MSG.AGENDA,
    title: MSG.AGENDA,
    icon: MATERIAL_ICONS.CALENDAR_VIEW_DAY,
    fn: () => showView(SIGNIN_VIEWS.AON_TIMECONTROL_AGENDA)
  };

  export const LOCATION = {
    id: CONSTANT.LOCATION,
    name: MSG.LOCATIONS,
    title: MSG.LOCATIONS,
    icon: MATERIAL_ICONS.LOCATION_ON,
    fn: () => showView(SIGNIN_VIEWS.AON_LOCATION_LIST)
  };

  export const TASK_HOLDER = {
    id: CONSTANT.TASK_HOLDER,
    name: MSG.OPERATORS,
    title: MSG.OPERATORS,
    icon: MATERIAL_ICONS.PEOPLE,
    fn: () => taskHolder()
  };

  export const WORKPLACES_CALENDAR = {
    id: CONSTANT.WORKPLACES_CALENDAR,
    name: MSG.CALENDAR,
    title: MSG.CALENDAR,
    icon: MATERIAL_ICONS.CALENDAR_TODAY,
    fn: () => workplacesCalendar()
  };

  export const TIMECONTROL = {
    id: CONSTANT.TIMECONTROL.initCap(),
    title: MSG.TIMECONTROL,
    name: MSG.TIMECONTROL,
    app: Apps.TIMECONTROL,
    options:[ PRESENCE, LOCATION, TASK_HOLDER, WORKPLACES_CALENDAR]
  }


