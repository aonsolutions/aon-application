import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";

export const PAYSHEET = {
  id: 'Paysheet',
  name: MSG.AON_MSG_PAYSHEETS,
  icon: 'text_snippet'
};

export const COMPANY_COSTS = {
  id: 'CompanyCosts',
  name: MSG.AON_MSG_COMPANY_COSTS,
  icon: 'assignment'
};

export const SEPA_FILES = {
  id: 'SepaFiles',
  name: MSG.AON_MSG_SEPA_FILES,
  icon: 'account_balance'
}

export const PayrollOptions = {
  PAYSHEET, COMPANY_COSTS, SEPA_FILES
};
