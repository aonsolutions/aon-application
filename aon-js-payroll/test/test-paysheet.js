var fs = require('fs');
var payroll = require('../lib/payroll');

payrollJSON = {
  logo : './aon.png',
  enterprise : {
    name : 'PRUEBAS LABORAL',
    address : 'Calle Duque de Wellington, 52 (01010)', //Calle, numero, cp
    locality : 'Vitoria-Gasteiz',
    cif : '5859585859M',
    ccc : 0202020202020202
  },
  employee : {
    fullname : 'VALDEPEÑAS DEL POZO, SERGIO',
    nif : '47227931-F',
    ss : 011004767999,
    professional_group : 'Director',
    quote_group : '01',
    seniority_date : '01/07/2010',
  },
  settlement : {
    start_date : '01/04/2016',
    end_date : '30/04/2016',
    total_days: 30
  },
  accruals : [
    {
      accrual_name : "01. Percepciones salariales",
      types : [
        {
          type_expression : "Salario base anual",
          code : 0001,
          type_value : 295.9
        },
        {
          type_expression : "Salario base anual",
          code : 0001,
          type_value : 295.9
        },
        {
          type_expression : "Salario base anual",
          code : 0001,
          type_value : 295.9
        }
      ]
    },
    {
      accrual_name : "02. Horas extraordinarias",
      types : [
        {
          type_expression : "Horas extras mes de Junio",
          code : 0002,
          type_value : 19
        }
      ]
    },
    {
      accrual_name : "04. Pagas extraordinarias prorrateo",
      types : [
        {
          type_expression : "Paga extraordinaria Junio",
          code : 0004,
          type_value : 24.66
        },
        {
          type_expression : "Paga extraordinaria Navidad",
          code : 0004,
          type_value : 24.66
        }
      ]
    },
    {
      accrual_name : "15. Retribuciones en especie",
      types : [
        {
          type_expression : "Móvil de empresa",
          code : 0015,
          type_value : 499.99
        }
      ]
    },
    {
      accrual_name : "38. Primas de seguro",
      types : [
        {
          type_expression : "Prima seguro trabajador",
          code : 0038,
          type_value : 25.3
        }
      ]
    },
    {
      accrual_name : "42. Gastos de locomocion y estancia",
      types : [
        {
          type_expression : "kilometraje en vehículo propio",
          code : 0042,
          type_value : 100
        },
        {
          type_expression : "dietas viaje a Madid",
          code : 0042,
          type_value : 44.1
        }
      ]
    }
  ],
  total_accrual : 345.21,
  deductions : [
    {
      name : "Contingencias comunes",
      percent : 4.7,
      value : 16.77
    },
    {
      name : "Desempleo",
      percent : 1.55,
      value : 5.53
    },
    {
      name : "Formación profesional",
      percent : 0.1,
      value : 0.36
    },
    {
      name : "Fuerza mayor o estructurales (HE)",
      percent: 0,
      value : 0
    },
    {
      name : "No estructurales (HE)",
      percent : 0,
      value : 0
    },
    {
      name : "Dinerario",
      percent : 33,
      value : 5.5
    },
    {
      name : "Especie",
      percent : 66.3,
      value : 10
    },
    {
      name : "Anticipos",
      types : [
        {
          name : "Anticipos 1",
          value : 50.05
        },
        {
          name : "Anticipos 2",
          value : 8.9
        }
      ]
    },
    {
      name : "Valor de los productos recibidos en especie",
      types : [
        {
          name : "Especie 1",
          value : 99.98
        },
        {
          name : "Especie 2",
          value : 5.21
        }
      ]
    },
    {
      name : "Otras deducciones",
      types : [
        {
          name : "Deducción 1",
          value : 12
        },
        {
          name : "Deducción 2",
          value : 31.12
        }
      ]
    }
  ],
  total_contributions : 22.65,
  total_deductions : 22.65,
  liquid_perceive : 322.56,
  signature_logo : './aon.png',
  footer_ss_quotation : {
    common_contingency : {
      monthly_remuneration : 345.21,
      extraordinary_pay_packet : 0,
      base : 356.72,
      type_percent : 23.6,
      company_input : 84.19
    },
    professional_contingency : {
      base : 356.72,
      type_percent_at : 1.35,
      company_input_at : 4.82,
      type_percent_unemployment : 5.5,
      company_input_unemployment : 19.62,
      type_percent_professional_formation : 0.6,
      company_input_professional_formation : 2.14,
      type_percent_salary_warranty : 0.2,
      company_input_salary_warranty : 0.71
    },
    aditional_quotation : {
      base_overwhelming_force : 0,
      company_input_overwhelming_force : 0,
      base_non_structural : 0,
      company_input_non_structural : 0
    },
    base_irpf : 15.5
  }
};

// payroll.standardPayroll(payrollJSON, process.stdout);
//payroll.newStandardPayroll(payrollJSON, process.stdout);
// payroll.standardTwoColumnsPayroll(payrollJSON, process.stdout);
// payroll.salaryRecibeCRA(payrollJSON, process.stdout);
 payroll.salaryRecibe(payrollJSON, process.stdout);
