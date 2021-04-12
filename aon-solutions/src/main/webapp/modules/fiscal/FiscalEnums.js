
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

const TAX_MODEL_NUMBER  = {
  "IVA":"303",
  "111":"111",
  "115":"115",
  "123":"123",
  "130":"130",
  "131":"131", 
  "202":"202"
};

const TAX_MODEL_TEXT = {
  "303":"IVA",
  "111":"IRPF Trabajo y Profesionales",
  "115":"IRPF Alquileres", 
  "123":"Rend. Capital Mobiliario", 
  "130":"IRPF Pago Fraccionado (ED)", 
  "131":"IRPF Pago Fraccionado (EO)",
  "202":"Imp. Sociedades. Pago Fraccionado",
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
  ALAVA:"Araba/Alava",
  BIZKAIA:"Bizkaia",
  GIPUZKOA:"Gipuzkoa",
  NAVARRA:"Navarra",
  COMMON_TERRITORY:"Territorio Común",
  UNKNOWN:"Otro"
}

const TAX_STATUS = {
  PENDING:"Pendiente",
  FINISHED:"Finalizado",
  BATCHED:"En Lote",
  BLOCKED:"Bloqueado",
  SENT:"Presentado",
  MISSING:"Desconocido",
  CUSTOMER_CHECK:"Envio a cliente",
}

const TAX_TYPE = {
  NEGATIVE:"Negativa, cero ó sin. act.",//  Nada
  DEPOSIT:"Ingreso",    // BANCO NRC
  BANK:"Domiciliación", // BANCO 
  DEPOSIT_CCT:"Ingreso a anotar en CCT", // Nada
  TO_DEDUCE:"A deducir",  //  Nada
  COMPENSATE:"A compensar", //  Nada
  PAYBACK:"A devolver", // Banco
  PAYBACK_CCT:"Devolución a anotar en CCT"  // Nada
}

export const TAX_ENUMS = {
  TAX_MODEL_NUMBER, TAX_MODEL_TEXT, TAX_PERIOD, TAX_ADMIN, TAX_STATUS, TAX_TYPE
}