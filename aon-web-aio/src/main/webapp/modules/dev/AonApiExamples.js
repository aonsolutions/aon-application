import { CONSTANT, MATERIAL_ICONS, MSG } from "../../environments/environments.js"

  export const INVOICE = {
    id: 1,
    domain: 1,
    type: 'emitida',
    serie: '2022',
    number: 1,
    reference: '2022/000001',
    date: '2022-11-04',
    transaction: 'NAC',
    category: '700000000',
    total: 12.1,
    comments: '',
    remarks: '',
    sender: {document: "", name: "", address: {country: "ES", address: "", zip: "", city: "", province: ""}},
    receiver: {document: "", name: "", address: {country: "ES", address: "", zip: "", city: "", province: ""}},
    service: false,
    vatAccrualPayment: false,
    withholding: false,
    surcharge: false,
    taxes: [
      {
        tax: "IVA",
        type: "IVA",
        percentage: 21,
        base: 10,
        quota: 2.1,
        surcharge: 0, 
        surcharge_quota: 0
      }
    ],
    details: [
      {
        description: "concepto 1", 
        quantity: 1, 
        price: 10.00,
        discount: 0, 
        amount: 10,
        category: "700000000",
        percentage: 21,
        prepayment: false,
        quantity: 1,
        quota: 2.1,
        surcharge: 0.0,
        surcharge_quota: 0.0,
        withholding: false,
        withholding_quota: 0.0,
        workplace: 1514
      }
    ],
    finances: [
      {
        due_date: "2022-11-03T11:05:57.888Z",
        paymethod: "118",
        amount: 12.1,
        iban: "",
        bank_account: ""
      }
    ]
  };