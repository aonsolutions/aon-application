export const DataAttachSource = {
  QUALITY: 0,
  INVOICE: 1,
  FBATCH: 2,
  PRODUCTION: 3,
  DELIVERY: 4,
  SII: 5,
  SERES: 6,
  INGENET: 7,
  MOD303: 8,
  MOD111: 9,
  MOD115: 10,
  MOD123: 11,
  MOD130: 12,
  MOD131: 13,
  MOD390: 14,
  IMPORTATION: 15,
  SISTEMA_RED: 16,
  INVOICE_PRINT_CONFIGURATION: 17,
  TBAI: 18,
  MOD202: 19,
  MOD190: 20,
  LROE: 21,
  MOD180: 22,
  MOD193: 23,
  MOD184: 24,
  MOD347: 25,
  MOD349: 26,
  MOD200: 27,
  MOD369: 28,
  VERIFACTU: 29,
  SIF: 30,
  NO_VERIFACTU: 31, 
  FACTURAE: 32,
  MOD421: 33,
};

DataAttachSource.getValueByName = function (name) {
  const key = Object.keys(DataAttachSource).find(k => k.includes(name));
  if (!key) {
    return null;
  }
  return DataAttachSource[key];
};