import { CONSTANT, MATERIAL_ICONS, MSG, TAG } from "../../environments/environments";
import Apps from "../../services/app";
import { SIGNIN_VIEWS } from "./signinEnums";
import * as JSF from "../aon-jsf-app.js";

  export const showView = (view) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.showView(view)
  }

  export const taskHolder = () => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    application.setContent(new JSF.AonJsfTaskHolder())
  }

  export const PRESENCE = {
    id: CONSTANT.PRESENCE,
    name: MSG.PRESENCE,
    title: MSG.PRESENCE,
    icon: MATERIAL_ICONS.ACCOUNT_BOX,
    fn: () => showView(SIGNIN_VIEWS.AON_PRESENCE_LIST)
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
    name: "Operarios",
    title: "Operarios",
    icon: MATERIAL_ICONS.PEOPLE,
    fn: () => taskHolder()
  };

  export const TIMECONTROL = {
    id: CONSTANT.TIMECONTROL.initCap(),
    title: MSG.TIMECONTROL,
    name: MSG.TIMECONTROL,
    app: Apps.TIMECONTROL,
    options:[ PRESENCE, LOCATION, TASK_HOLDER]
  }


