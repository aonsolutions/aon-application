import { MATERIAL_ICONS } from "../../environments/environments.js";
import * as MSG from "../../environments/msg.js";
import { PAYROLL } from "../../services/app.js";

export const PAYSHEET = {
  id: 'Paysheet',
  name: MSG.PAYSHEETS,
  icon: 'text_snippet'
};

export const COMPANY_COSTS = {
  id: 'CompanyCosts',
  name: MSG.COMPANY_COSTS,
  icon: MATERIAL_ICONS.ASSIGNMENT
};

export const AON_CCC = {
  name: 'CCC',
  icon: MATERIAL_ICONS.ACCOUNT_BALANCE,
  id: "CCC"
};

export const AON_CERT = {
  name: MSG.CERTIFICATES,
  aonIcon: {
    icon: 'cert',
    color: 'black'
  },
};

export const AON_LABORAL = {
  name: 'Laboral',
  icon: MATERIAL_ICONS.SETTING,
  id: "LABORAL"
};

export const AON_COMUNICA = {
  name: 'Comunic@',
  aonIcon: {
    icon: 'aon_seg_social',
    color: 'black'
  }
}

export const MOVEMENTS = {
  name: 'Movimientos',
  aonIcon: {
    icon: 'aon_seg_social',
    color: 'black'
  }
}

export const SEPA_FILES = {
  id: 'SepaFiles',
  name: MSG.SEPA_FILES,
  icon: 'account_balance'
}

export const AON_CONTRACT = {
  id: 'contractSidenav',
  name: MSG.CONTRACTS,
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
      title: MSG.WORKPLACE,
    },
    {
      type: "select",
      id: "employee",
      name: "employee",
      title: MSG.EMPLOYEE,
      hidden:true
    }
];

export const CONTRACT_OPTIONS = {
  CONTRACT:{
    name: MSG.CONTRACT_PAYROLL,
    title: MSG.CONTRACT_PAYROLL,
    aonIcon: "aon_cto",
    id:"aon_cto",
    permission:true,
    backgroundColor: PAYROLL.color
  },
  TA:{
    name: "Obtener TA",
    title: "Obtener TA",
    aonIcon: "aon_ta",
    id: 'Ta',
    permission:true,
    backgroundColor: PAYROLL.color
  },
  IDC:{
    name: "Obtener IDC",
    title: "Obtener IDC",
    aonIcon: 'aon_idc',
    id: 'Idc',
    permission:true,
    backgroundColor: PAYROLL.color
  },
  DELETE:{
    name: "Anuales",
    title: "Anuales",
    id: 'Delete',
    icon: 'delete_forever',
    permission:true,
    backgroundColor: PAYROLL.color
  }
};

export const ACTION_COMUNICA = {
  INFORMES:{
    name: 'Informes',
    aonIcon: "aon_seg_social",
    id: 'informes',
  },
  COMUNICAR:{
    id: 'Save',
    name: 'Comunicar',
    icon: MATERIAL_ICONS.SEND
  },
  BACK: {
    id: 'Previous',
    name: 'Volver',
    icon: MATERIAL_ICONS.ARROW_BACK
  },
  BAJA:{
    id: 'Baja',
    name: 'Baja',
    icon: MATERIAL_ICONS.CANCEL_SCHEDULE_SEND
  },
  DUPLICATE:{
    id: 'duplicateMov',
    name: 'Copiar datos',
    icon: 'content_copy'
  },
}

export const PayrollOptions = {
  AON_CONTRACT ,COMPANY_COSTS, SEPA_FILES, PAYSHEET, AON_COMUNICA, AON_CERT, AON_CCC, AON_LABORAL, MOVEMENTS
};

export const PAYROLL_VIEWS = {
  AON_LABORAL: "aonLaboral",
  AON_PAYROLL_LIST: "aonPayrollList",
  AON_SEPA_FILES_LIST: "aonSepaFilesList",
  AON_CONTRACT_LIST: "aonContractList",
  AON_MOVEMENTS_LIST: "aonMovementsList",
  AON_CERT: "aonCert",
  AON_CTA_LIST: "aonCtaList",
  AON_ALTA_DIRECTA: "aonAltaDirecta",
  AON_COMPANY_COSTS_LIST: "aon-company-costs-list"
}

export const EXCEPTION_MESSAGE = {
  "CertificateNotFoundException":"Agregue un certificado para conectarse a la Seguridad social",
  "RevokedCertificateException":"El certificado que ésta usted utilizando está revocado"
}

export const APP_PARAMS_PAYROLL = {
  APP_COMUNICA_CONTRACTS: "APP_COMUNICA_CONTRACTS",
  APP_COMUNICA_QUOTE_GROUP: "APP_COMUNICA_QUOTE_GROUP",
  APP_COMUNICA_EMAILS: "APP_COMUNICA_EMAILS"
}