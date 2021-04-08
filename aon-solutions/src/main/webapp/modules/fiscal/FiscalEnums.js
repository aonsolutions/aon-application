
export const AON_TAX = {
    name: 'Impuestos',
    icon: 'assignment',
    id: 'TAX'
};

export const FiscalOptions = {
    AON_TAX
  };


  export const FISCAL_VIEWS = {
    AON_TAX:"aonTax",
  }



export const DATA_TEST =[
  {
    id:"301",
    domain:"3305",
    administration: "ALAVA",
    model: "IVA",
    year:"2021",
    period: "M01",
    status:"PENDING",
    complementary: false,
    replacement: false,
    document:"777777777",
    name:"DECLARANTE_NAME",
    surname:"DECLARANTE_SURNAME",
    type:"NEGATIVE",
    result: 1231.23,
    iban: "12312389893774766883"
  },
  {
    id:"302",
    domain:"3305",
    administration: "COMMON_TERRITORY",
    model: "202",
    year:"2020",
    period: "T1",
    status:"FINISHED",
    complementary: false,
    replacement: false,
    document:"888888",
    name:"DECLARANTE_NAME",
    surname:"DECLARANTE_SURNAME",
    type:"NEGATIVE",
    result: 6551.84,
    iban: "12312389893774766883"
  }
];

const TAX_MODEL  = {
  "IVA":"303",
  "111":"111",
  "115":"115",
  "123":"123",
  "130":"130",
  "131":"131",
  "202":"202",
};

const TAX_PERIOD  = {
  "M01":"Enero",
  "M02":"Febrero",
  "M03":"Marzo",
  "M04":"Abril",
  "M05":"Mayo",
  "M06":"Junio",
  "M07":"Julio",
  "M08":"Agosto",
  "M09":"Septiembre",
  "M10":"Octubre",
  "M11":"Noviembre",
  "M12":"Diciembre",
  "T1":"1º Trim.",
  "T2":"2º Trim.",
  "T3":"3º Trim.",
  "T4":"4º Trim.",
  "YEAR":"Anual",
};

const TAX_ADMIN = {
  "ALAVA":"Araba/Alava",
  "BIZKAIA":"Bizkaia",
  "GIPUZKOA":"Gipuzkoa",
  "NAVARRA":"Navarra",
  "COMMON_TERRITORY":"Territorio Común",
  "UNKNOWN":"Otro"
}

const TAX_STATUS = {
  "PENDING":"Pendiente",
  "FINISHED":"Finalizado",
  "BATCHED":"En Lote",
  "BLOCKED":"Bloqueado",
  "SENT":"Presentado",
  "MISSING":"Desconocido",
  "CUSTOMER_CHECK":"Envio a cliente",
}

const TAX_TYPE = {
  "NEGATIVE":"Negativa, cero ó sin. act.",
  "DEPOSIT":"Ingreso",
  "BANK":"Domiciliación",
  "DEPOSIT_CCT":"Ingreso a anotar en CCT",
  "TO_DEDUCE":"A deducir",
  "COMPENSATE":"A compensar",
  "PAYBACK":"A devolver",
  "PAYBACK_CCT":"Devolución a anotar en CCT"
}

export const TAX_ENUMS = {
  TAX_MODEL, TAX_PERIOD, TAX_ADMIN, TAX_STATUS, TAX_TYPE
}