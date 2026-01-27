import { CONSTANT, MSG, TAG } from "../../environments/environments";
import Apps from "../../services/app";
import { SIGNIN_VIEWS, SigninSidenav } from "./signinEnums";
import * as JSF from "../aon-jsf-app.js";

  export const showView = (view) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.showView(view);
  }

  export const taskHolder = () => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    application.removeToolbarOptions();
    application.setContent(new JSF.AonJsfTaskHolder())
  }

  export const PRESENCE = {
    id: CONSTANT.PRESENCE,
    name: MSG.PRESENCE,
    title: MSG.PRESENCE,
    fn: () => {
      let tc = document.querySelector("aon-timecontrol");
      tc.setDataFilter({period: SigninSidenav.PERIOD.TODAY.id});
    }
  };

  export const LOCATION = {
    id: CONSTANT.LOCATION,
    name: MSG.LOCATIONS,
    title: MSG.LOCATIONS,
    fn: () => showView(SIGNIN_VIEWS.AON_LOCATION_LIST)
  };

  export const TASK_HOLDER = {
    id: CONSTANT.TASK_HOLDER,
    name: "Operarios",
    title: "Operarios",
    fn: () => taskHolder()
  };

  export const TIMECONTROL_SIDENAV = {
    id: CONSTANT.TIMECONTROL.initCap(),
    title: MSG.TIMECONTROL,
    name: MSG.TIMECONTROL,
    app: Apps.TIMECONTROL,
    options:[ PRESENCE, LOCATION, TASK_HOLDER]
  };
