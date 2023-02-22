import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";
import { IRPF } from "../../environments/msg-en.js";


export const Transactions = [
  {value: 'NAC', name: 'Nacional'},
  {value: 'INTR', name: 'Intracomunitaria'},
  {value: 'EXTR', name: 'Extracomunitaria'},
  {value: 'CCM', name: 'Canarias, Ceuta y Melilla'},
  {value: 'ISP', name: 'I.S.P.'},
];


export const InvoiceStatus = {
  INBOX: 'inbox',
  REJECTED: 'rejected',
  DRAFT: 'draft',
  PENDING: 'pending',
  SCORED: 'scored'
}

export const TaxType = {
  IVA: 'IVA',
  IVA_RE: 'IVA_RE',
  IGIC: 'IGIC',
  IRPF: 'IRPF',
  IRPF_PROF: 'IRPF_PROF',
  IRPF_ALQ: 'IRPF_ALQ',
  IRPF_AGRI: 'IRPF_AGRI'
};

export const WithholdingType = [
  {
    id: CONSTANT.PROFESSIONAL,
    name: "Profesional (G/01)",
    description: "Actividades profesionales con carácter general.",
    percentage: 15.0
  },
  {
    id: CONSTANT.RENTING,
    name: "Arrendamiento",
    description: "Arrendamiento o subarrendamiento de bienes inmuebles urbanos.",
    percentage: 19.0
  },
   {
    id: CONSTANT.MOVABLE_CAPITAL,
    name: "Cap. Mobiliario (A)",
    description: "Derivados de la participación en fondos propios de entidades.",
    percentage: 19.0
  },
   {
    id: CONSTANT.FARMER,
    name: "Agrícola (H/01)",
    description: "Actividades agrícolas y ganaderas en general.",
    percentage: 2.0
  },
   {
    id: CONSTANT.TRANSPORT_OPERATOR,
    name: "Est. Obj. (H/04)",
    description: "Determinadas actividades empresariales en Estimación Objetiva.",
    percentage: 2.0
  },
  {
    id: 'm190g02',
    name: "Profesional (G/02)",
    description: "Determinadas actividades profesionales (recaudadores municipales, mediadores de seguros ...)",
    percentage: 7.0
  },
  {
    id: 'm190g03',
    name: "Profesional (G/03)",
    description: "Profesionales de nuevo inicio.",
    percentage: 7.0
  },
  {
    id: 'm190h02',
    name: "Agrícola (H/02)",
    description: "Actividades de engorde de porcino y avicultura.",
    percentage: 1.0
  },
  {
    id: 'm190h03',
    name: "Forestales (H/03)",
    description: "Actividades forestales.",
    percentage: 2.0
  },
  {
    id: 'm190i01',
    name: "Der.Imagen (I/01)",
    description: "Rendimientos del art. 75.2.b): cesión derecho de imagen",
    percentage: 24.0
  },
   {
    id: 'm190i01',
    name: "Otras (I/02)",
    description: "Rendimientos del art. 75.2.b): resto de conceptos",
    percentage: 19.0
  },
   {
    id: 'm190j',
    name: "Cesión Der. Imagen(J)",
    description: "Imputación rentas por cesión derechos imagen",
    percentage: 19.0
  },
   {
    id: 'm190k01',
    name: "Premios (K/01)",
    description: "Premios de juegos, concursos, rifas... sujetos a retención, (K - 01)",
    percentage: 19.0
  },
   {
    id: 'm190k03',
    name: "Premios (K/03)",
    description: "Premios de juegos, concursos, rifas... sujetos a retención, (K - 03)",
    percentage: 19.0
  },
   {
    id: 'm190k02',
    name: "Gan. forestal (K/02)",
    description: "Aprovechamientos forestales en montes públicos.",
    percentage: 19.0
  },
   {
    id: 'm190c1',
    name: "Cap. Mobiliario (C/1)",
    description: "Propiedad intelectual, industrial, prestación de asistencia técnica.",
    percentage: 19.0
  },
   {
    id: 'm190c2',
    name: "Cap. Mobiliario (C/2)",
    description: "Propiedad intelectual cuando el contribuyente perceptor no sea el autor.",
    percentage: 15.0
  },
   {
    id: 'm190c3',
    name: "Cap. Mobiliario (C/3)",
    description: "Rendimientos derivados de la cesión del derecho de explotación de derechos de imagen siempre que no sean en el desarrollo de una actividad económica",
    percentage: 24.0
  },
   {
    id: 'm190f01',
    name: "Trabajo (F/01)",
    description: "Premios literarios, artísticos o científicos no exentos de IRPF, cuando tengan la consideración de rendimientos del trabajo.",
    percentage: 15.0
  },
  {
    id: 'm190f021',
    name: "Cursos (F/02)",
    description: "Cursos, conferencias, seminarios.",
    percentage: 15.0
  },
  {
    id: 'm190f022',
    name: "Elab. obras (F/02)",
    description: "Elaboración de obras literarias, artísticas o científicas.",
    percentage: 15.0
  }
];


export const TaxVatType = [
  {value: 'IVA', name: 'IVA'},
  {value: 'IVA_RE', name: 'IVA+RE'}//,
  //IGIC: 'IGIC'
];

export const TaxWithholdingType = [
  { value: 'IRPF_PROF', name: 'IRPF PROF.'},
  { value: 'IRPF_ALQ', name: 'IRPF ALQ.'}
];

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

export const TaxRetentionPercentage = [
  {value:19, name:'19%'},
  {value:15.0, name:'15%'},
  {value:7.0, name:'7%'}
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

export const getTaxType = (percentage) => {
  if(percentage == 19.0) {
    return TaxType.IRPF_ALQ;
  } else if(percentage == 15.0 || percentage == 7.0) {
    return TaxType.IRPF_PROF;
  } else if(percentage == 2.0) {
    return TaxType.IRPF_AGRI;
  } else return TaxType.IVA;
}

export const getTaxTypeName = (type, mobile) => {
  if(TaxType.IVA === type) {
    return 'IVA';
  } else if(TaxType.IVA_RE === type) {
    return 'IVA+RE';
  } else if(TaxType.IGIC === type) {
    return 'IGIC';
  } else if(TaxType.IRPF === type) {
    return 'IRPF';
  } else if(TaxType.IRPF_PROF === type) {
    return mobile ? 'IRPF' : 'IRPF PROF.';
  } else if(TaxType.IRPF_ALQ === type) {
    return mobile ? 'IRPF' : 'IRPF ALQ.';
  } else if(TaxType.IRPF_AGRI === type) {
    return mobile ? 'IRPF' : 'IRPF AGRI.';
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
    return TaxRetentionPercentage; // TaxIRPFPROFPercentage;
  } else if(TaxType.IRPF_ALQ === type) {
    return TaxRetentionPercentage; //TaxIRPFALQPercentage;
  } else if(TaxType.IRPF_AGRI === type) {
    return TaxIRPFAGRIPercentage;
  } 
}