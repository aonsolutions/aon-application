import { MSG } from '../environments/environments.js'; 
import * as GWT from '../gwt/gwt.js';

export const PAYMETHODS = {
    description: MSG.PAYMETHODS,
    title: MSG.PAYMETHODS,
    action: () => GWT.iLoad(GWT.PAY_METHOD)
    // action: () => this.rootPanel(new JSF.AonJsfPayMethod())
};