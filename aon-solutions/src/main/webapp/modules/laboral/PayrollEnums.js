// import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
// import * as MATERIAL_ICONS from "../../environments/materialIcons.js";

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

export const AON_CCC = {
  name: 'CCC',
  icon: 'account_balance',
  id: "CCC"
};

export const AON_CERT = {
  name: 'Certificados',
  aonIcon: {
    icon: 'cert',
    color: 'black'
  },
};

export const AON_COMUNICA = {
  name: 'Comunica',
  aonIcon: {
    icon: 'aon_seg_social',
    color: 'black'
  }
}

export const SEPA_FILES = {
  id: 'SepaFiles',
  name: MSG.AON_MSG_SEPA_FILES,
  icon: 'account_balance'
}

export const AON_CONTRACT = {
  id: 'contract',
  name: 'Contratos',
  aonIcon: {
    icon: 'contract',
    color: 'black'
  },
}

export const PAYROLL_FILTER = [
    {
      type: "select",
      id: "workplace",
      name: "workplace",
      title: "Centro de trabajo",
    },
    {
      type: "select",
      id: "employee",
      name: "employee",
      title: "Trabajador",
      hidden:true
    }
];

export const CONTRACT_OPTIONS = {
  CONTRACT:{
    name: "Contrato",
    aonIcon: "aon_cto",
  },
  TA:{
    name: "Obtener TA",
    aonIcon: "aon_ta",
  },
  IDC:{
    name: 'Obtener IDC',
    aonIcon: 'aon_idc',
  }
};

export const PayrollOptions = {
  AON_CONTRACT ,COMPANY_COSTS, SEPA_FILES, PAYSHEET, AON_COMUNICA, AON_CERT, AON_CCC
};

export const PAYROLL_VIEWS = {
  AON_PAYROLL_LIST: "aonPayrollList",
  AON_SEPA_FILES_LIST: "aonSepaFilesList",
  AON_CONTRACT_LIST: "aonContractList",
  AON_MOVEMENTS: "aonMovements",
  AON_MOVEMENTS_LIST: "aonMovementsList",
  AON_CERT: "aonCert",
  AON_CTA_LIST: "aonCtaList",
  AON_ALTA_DIRECT: "aonAltaDirecta"
}