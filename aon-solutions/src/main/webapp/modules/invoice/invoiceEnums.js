import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";
import { IRPF } from "../../environments/msg-en.js";


export const TaxType = {
  IVA: 'IVA',
  IVA_RE: 'IVA_RE',
  IGIC: 'IGIC',
  IRPF: 'IRPF',
  IRPF_PROF: 'IRPF_PROF',
  IRPF_ALQ: 'IRPF_ALQ',
  IRPF_AGRI: 'IRPF_AGRI'
};

export const TaxVatType = [
  {value: 'IVA', name: 'IVA'},
  {value: 'IVA_RE', name: 'IVA+RE'}//,
  //IGIC: 'IGIC'
];

export const TaxWithholdingType = [
  { value: 'IRPF_PROF', name: 'IRPF PROF.'},
  { value: 'IRPF_ALQ', name: 'IRPF ALQ.'}
]

export const TaxIVAPercentage = [
  {value:21.0, name:'21%'},
  {value:10.0, name:'10%'},
  {value:4.0, name:'4%'},
  {value:0.0, name:'0%'}
];

export const TaxVATPercentage = [
  {value:21.0, name:'21%'},
  {value:10.0, name:'10%'},
  {value:4.0, name:'4%'},
  {value:0.0, name:'0%'}
];

export const TaxVatREPercentage = [
  {value:21.0, name:'21%+5,2%'},
  {value:10.0, name:'10%+1,4%'},
  {value:4.0, name:'4%+0,5%'},
  {value:0.0, name:'0%'}
];

export const TaxVatIGICPercentage = [];

export const TaxIRPFPercentage = [
  {value:19, name:'19%'},
  {value:15.0, name:'15%'},
  {value:7.0, name:'7%'},
  {value:2.0, name:'2%'}
];

export const TaxIRPFPROFPercentage = [
  {value:15.0, name:'15%'},
  {value:7.0, name:'7%'}
];

export const TaxIRPFALQPercentage = [
  {value:19.0, name:'19%'}
];

export const TaxIRPFAGRIPercentage = [
  {value:2.0, name:'2%'}
];


export const getSurchargeByVat = (vat) => {
  if(vat == 21.0) {
    return 5.2;
  } else if(vat == 10.0) {
    return 1.4;
  } else if(vat == 4.0) {
    return 0.5;
  } else return 0.0;
};

export const getTaxTypeName = (type) => {
  if(TaxType.IVA === type) {
    return 'IVA';
  } else if(TaxType.IVA_RE === type) {
    return 'IVA+RE';
  } else if(TaxType.IGIC === type) {
    return 'IGIC';
  } else if(TaxType.IRPF === type) {
    return 'IRPF';
  } else if(TaxType.IRPF_PROF === type) {
    return 'IRPF PROF.';
  } else if(TaxType.IRPF_ALQ === type) {
    return 'IRPF ALQ.';
  } else if(TaxType.IRPF_AGRI === type) {
    return 'IRPF AGRI.';
  } 
}

export const getTaxPercentageOption = (type) => {
  if(TaxType.IVA === type) {
    return TaxIVAPercentage;
  } else if(TaxType.IVA_RE === type) {
    return TaxVatREPercentage;
  } else if(TaxType.IGIC === type) {
    return TaxVatIGICPercentage;
  } else if(TaxType.IRPF === type) {
    return TaxIRPFPercentage;
  } else if(TaxType.IRPF_PROF === type) {
    return TaxIRPFPROFPercentage;
  } else if(TaxType.IRPF_ALQ === type) {
    return TaxIRPFALQPercentage;
  } else if(TaxType.IRPF_AGRI === type) {
    return TaxIRPFAGRIPercentage;
  } 
}