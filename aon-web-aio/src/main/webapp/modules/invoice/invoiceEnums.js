import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";

export const BillingPeriods = [
  {value: 'NO_PERIOD', name: MSG.NO_PERIOD},
  {value: 'MONTHLY', name: MSG.MONTHLY},
  {value: 'BI_MONTHLY', name: MSG.BI_MONTHLY},
  {value: 'THREE_MONTHLY', name: MSG.THREE_MONTHLY},
  {value: 'SIX_MONTHLY', name: MSG.SIX_MONTHLY},
  {value: 'YEARLY', name: MSG.YEARLY}
];

export const Transactions = [
  {value: 'NAC', name: 'Op. Interiores'},
  {value: 'INTR', name: 'Intracomunitaria'},
  {value: 'EXTR', name: 'Extracomunitaria'},
  {value: 'CCM', name: 'Canarias, Ceuta y Melilla'},
  {value: 'ISP', name: 'I.S.P.'},
];

export const InvoiceStatus = {
  INBOX: 'inbox',
  REJECTED: 'rejected',
  TRASH: 'trash',
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
    id: "PROFESSIONAL",
    name: "Profesional (G/01) - 15%",
    description: "Actividades profesionales con carácter general.",
    percentage: 15.0
  },
  {
    id: "RENTING",
    name: "Arrendamiento - 19%",
    description: "Arrendamiento o subarrendamiento de bienes inmuebles urbanos.",
    percentage: 19.0
  },
   {
    id: "MOVABLE_CAPITAL",
    name: "Cap. Mobiliario (A) - 19%",
    description: "Derivados de la participación en fondos propios de entidades.",
    percentage: 19.0
  },
   {
    id: "FARMER",
    name: "Agrícola (H/01) - 2%",
    description: "Actividades agrícolas y ganaderas en general.",
    percentage: 2.0
  },
   {
    id: "TRANSPORT_OPERATOR",
    name: "Est. Obj. (H/04) - 1%",
    description: "Determinadas actividades empresariales en Estimación Objetiva.",
    percentage: 1.0
  },
  {
    id: 'M190_G_02',
    name: "Profesional (G/02) - 7%",
    description: "Determinadas actividades profesionales (recaudadores municipales, mediadores de seguros ...)",
    percentage: 7.0
  },
  {
    id: 'M190_G_03',
    name: "Profesional (G/03) - 7%",
    description: "Profesionales de nuevo inicio.",
    percentage: 7.0
  },
  {
    id: 'M190_H_02',
    name: "Agrícola (H/02) - 1%",
    description: "Actividades de engorde de porcino y avicultura.",
    percentage: 1.0
  },
  {
    id: 'M190_H_03',
    name: "Forestales (H/03) - 2%",
    description: "Actividades forestales.",
    percentage: 2.0
  },
  {
    id: 'M190_I_01',
    name: "Der.Imagen (I/01) - 24%",
    description: "Rendimientos del art. 75.2.b): cesión derecho de imagen",
    percentage: 24.0
  },
   {
    id: 'M190_I_02',
    name: "Otras (I/02) - 19%",
    description: "Rendimientos del art. 75.2.b): resto de conceptos",
    percentage: 19.0
  },
   {
    id: 'M190_J',
    name: "Cesión Der. Imagen(J) - 19%",
    description: "Imputación rentas por cesión derechos imagen",
    percentage: 19.0
  },
   {
    id: 'M190_K_01',
    name: "Premios (K/01) - 19%",
    description: "Premios de juegos, concursos, rifas... sujetos a retención, (K - 01)",
    percentage: 19.0
  },
   {
    id: 'M190_K_03',
    name: "Premios (K/03) - 19%",
    description: "Premios de juegos, concursos, rifas... sujetos a retención, (K - 03)",
    percentage: 19.0
  },
   {
    id: 'M190_K_02',
    name: "Gan. forestal (K/02) - 19%",
    description: "Aprovechamientos forestales en montes públicos.",
    percentage: 19.0
  },
   {
    id: 'M193_C1',
    name: "Cap. Mobiliario (C/1) - 19%",
    description: "Propiedad industrial, prestación de asistencia técnica.",
    percentage: 19.0
  },
   {
    id: 'M193_C2',
    name: "Cap. Mobiliario (C/2) - 15%",
    description: "Propiedad intelectual cuando el contribuyente perceptor no sea el autor.",
    percentage: 15.0
  },
   {
    id: 'M193_C3',
    name: "Cap. Mobiliario (C/3) - 24%",
    description: "Rendimientos derivados de la cesión del derecho de explotación de derechos de imagen siempre que no sean en el desarrollo de una actividad económica",
    percentage: 24.0
  },
   {
    id: 'M190_F_01',
    name: "Trabajo (F/01) - 15%",
    description: "Premios literarios, artísticos o científicos no exentos de IRPF, cuando tengan la consideración de rendimientos del trabajo.",
    percentage: 15.0
  },
  {
    id: 'M190_F_02_1',
    name: "Cursos (F/02) - 15%",
    description: "Cursos, conferencias, seminarios.",
    percentage: 15.0
  },
  {
    id: 'M190_F_02_2',
    name: "Elab. obras (F/02) - 15%",
    description: "Elaboración de obras literarias, artísticas o científicas.",
    percentage: 15.0
  },
  {
    	id: 'M193_C4',
    	name: "Cap. Mobiliario (C/4) - 19%",
    	description: "Arrendamiento y subarrendamiento de bienes muebles, negocios o minas.",
    	percentage: 19.0
  },
	{
	  	id: 'M193_B_01',
	  	name: "Cap. Mobiliario (B/01) - 19%",
	  	description: "Intereses de obligaciones, bonos, certificados de dep\u00F3sito u otros t\u00EDtulos privados.",
	  	percentage: 19.0
	},
	{
	  	id: 'M193_B_02',
	  	name: "Cap. Mobiliario (B/02) - 19%",
	  	description: "Intereses de obligaciones, bonos, c\u00E9dulas, deuda p\u00FAblica u otros t\u00EDtulos p\u00FAblicos.",
	  	percentage: 19.0
	},																																
	{
		id: 'M193_B_03',
		name: "Cap. Mobiliario (B/03) - 19%",
		description: "Intereses de pr\u00E9stamos no bancarios.",
		percentage: 19.0
	},
	{
		id: 'M193_B_04',
		name: "Cap. Mobiliario (B/04) - 19%",
		description: "Rendimientos o rentas que disfruten de un r\u00E9gimen transitorio de beneficios en operaciones financieras a que se refiere la D.T. sexta de la Ley 27/2014, de 27 de noviembre, del I.S.",
		percentage: 19.0
	},
	{
		id: 'M193_B_05',
		name: "Cap. Mobiliario (B/05) - 19%",
		description: "Rendimientos o rentas satisfechos por una entidad financiera como consecuencia de la transmisión, cesión o transferencia, total o parcial, de un crédito titularidad de aquélla.",
		percentage: 19.0
		},
	{
		id: 'M193_B_06',
		name: "Cap. Mobiliario (B/06) - 19%",
		description: "Otros rendimientos de capital mobiliario o rentas no incluidos en los dígitos anteriores.",
		percentage: 19.0
	},
	{
		id: 'M193_B_07',
		name: "Cap. Mobiliario (B/07) - 19%",
		description: "Rendimientos exentos.",
		percentage: 19.0
	},
	{
		id: 'M193_D_01',
		name: "Cap. Mobiliario (D/01) - 19%",
		description: "Intereses de obligaciones, bonos, certificados de dep\u00F3sito u otros t\u00EDtulos privados.",
		percentage: 19.0
	},
	{
		id: 'M193_D_02',
		name: "Cap. Mobiliario (D/02) - 19%",
		description: "Intereses de obligaciones, bonos, c\u00E9dulas, deuda p\u00FAblica u otros t\u00EDtulos p\u00FAblicos.",
		percentage: 19.0
	},
	{
		id: 'M193_D_03',
		name: "Cap. Mobiliario (D/03) - 19%",
		description: "Intereses de pr\u00E9stamos no bancarios.",
		percentage: 19.0
	},
	{
		id: 'M193_D_04',
		name: "Cap. Mobiliario (D/04) - 19%",
		description: "Rendimientos o rentas que disfruten de un r\u00E9gimen transitorio de beneficios en operaciones financieras a que se refiere la D.T. sexta de la Ley 27/2014, de 27 de noviembre, del I.S.",
		percentage: 19.0
	},
	{
		id: 'M193_D_05',
		name: "Cap. Mobiliario (D/05) - 19%",
		description: "Rendimientos o rentas satisfechos por una entidad financiera como consecuencia de la transmisión, cesión o transferencia, total o parcial, de un crédito titularidad de aquélla.",
		percentage: 19.0
	},
	{
		id: 'M193_D_06',
		name: "Cap. Mobiliario (D/06) - 19%",
		description: "Otros rendimientos de capital mobiliario o rentas no incluidos en los dígitos anteriores.",
		percentage: 19.0
	},
	{
		id: 'M193_D_07',
		name: "Cap. Mobiliario (D/07) - 19%",
		description: "Rendimientos exentos.",
		percentage: 19.0
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
  {value:7.5, name:'7,5%'},
  {value:5.0, name:'5%'},
  {value:4.0, name:'4%'},
  {value:2.0, name:'2%'},
  {value:0.0, name:'0%'}
];

export const TaxIVAAgriPercentage = [
  {value:21.0, name:'21%'},
  {value:12.0, name:'12%'},
  {value:10.5, name:'10,5%'},
  {value:10.0, name:'10%'},
  {value:7.5, name:'7,5%'},
  {value:5.0, name:'5%'},
  {value:4.0, name:'4%'},
  {value:2.0, name:'2%'},
  {value:0.0, name:'0%'}
];

export const TaxVatREPercentage = [
  {value:21.0, name:'21%+5,2%'},
  {value:10.0, name:'10%+1,4%'},
  {value:7.5, name:'7.5%+1%'},
  {value:5.0, name:'5%+0,6%'},
  {value:4.0, name:'4%+0,5%'},
  {value:2.0, name:'2%+0,26%'},
  {value:0.0, name:'0%'}
];

export const TaxVatIGICPercentage = [
  {value:0.0, name:'0%'},
  {value:1.0, name:'1%'},
  {value:3.0, name:'3%'},
  {value:7.0, name:'7%'},
  {value:9.0, name:'9%'},
  {value:9.5, name:'9,5%'},
  {value:13.5, name:'13,5%'},
  {value:20.0, name:'20%'},
  {value:35.0, name:'35%'},
];

export const TaxIRPFPercentage = [
  {value:24.0, name:'24%'},
  {value:19.0, name:'19%'},
  {value:15.0, name:'15%'},
  {value:7.0, name:'7%'},
  {value:2.0, name:'2%'},
  {value:1.0, name:'1%'}
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

export const getVats = (administration, agri) => {
  if(administration && "CANARIAS" == administration) {
    return TaxVatIGICPercentage;
  } else return agri ? TaxIVAAgriPercentage : TaxIVAPercentage;
}

export const getVatLabel = (administration) => {
  if(administration && "CANARIAS" == administration) {
    return '%IGIC';
  } else return '%IVA';
}


export const getSurchargeByVat = (vat) => {
  if(vat == 21.0) {
    return 5.2;
  } else if(vat == 10.0) {
    return 1.4;
  } else if(vat == 4.0) {
    return 0.5;
  } else if(vat == 5.0) {
    return 0.6;
  } else if(vat == 7.5) {
    return 1;
  } else if(vat == 2.0) {
    return 0.26;
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

export const getTaxTypeName = (type, mobile, administration) => {
  if(TaxType.IVA === type && "CANARIAS" == administration) {
    return 'IGIC';
  } else if(TaxType.IVA === type) {
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

export const getTaxPercentageOption = (type, administration, surcharge, agri) => {
  if(TaxType.IVA === type && "CANARIAS" == administration) {
    return TaxVatIGICPercentage;
  } else if(TaxType.IVA_RE === type || surcharge) {
    return TaxVatREPercentage;
  } else if(TaxType.IVA === type) {
    return agri ? TaxIVAAgriPercentage : TaxIVAPercentage;
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


export const ErrCode = {
	ERR_FILE_TOO_BIG: 'ERR_FILE_TOO_BIG',
	ERR_TOO_MANY_PAGES: 'ERR_TOO_MANY_PAGES',
	ERR_UNSUPPORTED_FILE_FORMAT: 'ERR_UNSUPPORTED_FILE_FORMAT',
	ERR_EMPTY_VALUE: 'ERR_EMPTY_VALUE', 
	ERR_INCORRECT_VALUE: 'ERR_INCORRECT_VALUE', 
	ERR_INVALID_FORMAT: 'ERR_INVALID_FORMAT',
	ERR_LOW_CONFIDENCE: 'ERR_LOW_CONFIDENCE',
	ERR_HANDWRITTEN_DOC: 'ERR_HANDWRITTEN_DOC',
	ERR_INVALID_DOC_TYPE: 'ERR_INVALID_DOC_TYPE',
	ERR_CROPPED_DOC: 'ERR_CROPPED_DOC',
	ERR_MULTI_DOC_PAGE_FOUND: 'ERR_MULTI_DOC_PAGE_FOUND',
	ERR_PAGES_MISSING: 'ERR_PAGES_MISSING',
	ERR_BAD_QUALITY: 'ERR_BAD_QUALITY',
	ERR_DUPLICATED_DOCUMENT: 'ERR_DUPLICATED_DOCUMENT',
	ERR_MISSING_INFO: 'ERR_MISSING_INFO',
	ERR_BREAKDOWN_AMOUNT_MISSMATCH: 'ERR_BREAKDOWN_AMOUNT_MISSMATCH',
	ERR_CLASSIFIER_DISCARD: 'ERR_CLASSIFIER_DISCARD',
	WARN_CLASSIFIER_FORCED_DEFTYPE: 'WARN_CLASSIFIER_FORCED_DEFTYPE',
	ERR_OTHER: 'ERR_OTHER'
};

export const ErrKey = {
	DOMAIN: 'DOMAIN',
	WORKPLACE: 'WORKPLACE',
	TYPE: 'TYPE',
	BASES_QUOTAS: 'BASES_QUOTAS',
	SERIES: 'SERIES',
	NUMBER: 'NUMBER',
	DUPLICATED_SERIES_NUMBER: 'DUPLICATED_SERIES_NUMBER',
	REFERENCE_CODE: 'REFERENCE_CODE',
	DUPLICATED_REFERENCE_CODE: 'DUPLICATED_REFERENCE_CODE',
	TRANSACTION: 'TRANSACTION',
	ISSUE_DATE: 'ISSUE_DATE',
	TAX_DATE: 'TAX_DATE',
	TAX_RATE: 'TAX_RATE',
	TAX_BASE: 'TAX_BASE',
	TAX_QUOTA: 'TAX_QUOTA',
	IRPF_RATE: 'IRPF_RATE',
	IRPF_BASE: 'IRPF_BASE',
	IRPF_QUOTA: 'IRPF_QUOTA',
	SCOPE: 'SCOPE',
	REGISTRY: 'REGISTRY',
	AMBIGUOUS_REGISTRY: 'AMBIGUOUS_REGISTRY',
	RDOCUMENT: 'RDOCUMENT',
	RDOCUMENT_COUNTRY: 'RDOCUMENT_COUNTRY',
	RNAME: 'RNAME',
	ADDRESS: 'ADDRESS',
	DETAIL_DESCRIPTION: 'DETAIL_DESCRIPTION',
	DETAILS: 'DETAILS',
	ACCOUNT_ENTRY: 'ACCOUNT_ENTRY',
	FINANCE_AMOUNT_ZERO: 'FINANCE_AMOUNT_ZERO',
	FINANCE_WRONG_DUE_DATE: 'FINANCE_WRONG_DUE_DATE',
	FINANCE_WRONG_ACCOUNT_BANK: 'FINANCE_WRONG_ACCOUNT_BANK',
	TOTAL: 'TOTAL',
	PAY_METHOD: 'PAY_METHOD'
};
