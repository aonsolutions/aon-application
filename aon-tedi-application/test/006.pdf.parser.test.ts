// tslint:disable: no-unused-expression
import { expect } from 'chai';
// import fileType from 'file-type';
import fs = require('fs');
// import readChunk from 'read-chunk';
import readline = require('readline');
// import { from } from 'rxjs';
// import { filter, map, mergeMap } from 'rxjs/operators';
// import unzip from 'unzip';
import { InvoiceStatus, TaxType } from '../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../src/tedi-pdf-parser/TediPdfParser';
import { AutoMLTest } from './006.pdf.parser/automl.test';
import { AmazonTest } from './006.pdf.parser/amazon.test';
import { BipDriveTest } from './006.pdf.parser/bip_drive.test';
import { CaixaBankViaTest } from './006.pdf.parser/caixa-bank-viat.test';
import { EndesaTest } from './006.pdf.parser/endesa.test';
import { EuskaltelTest } from './006.pdf.parser/euskaltel.test';
import { IberdrolaClientesTest } from './006.pdf.parser/iberdrola-clientes.test';
import { IberdrolaCurTest } from './006.pdf.parser/iberdrola-cur.test';
import { IonosTest } from './006.pdf.parser/ionos.test';
import { MovistarFijoTest } from './006.pdf.parser/movistar-fijo.test';
import { MovistarFusionTest } from './006.pdf.parser/movistar-fusion.test';
import { MovistarMovilTest } from './006.pdf.parser/movistar-movil.test';
import { NaturgyTest } from './006.pdf.parser/naturgy.test';
import { OrangeTest } from './006.pdf.parser/orange.test';
import { SecuritasTest } from './006.pdf.parser/securitas.test';
import { TAPOTest } from './006.pdf.parser/tapo.test';
import { CaixaBankSolredTest, MakroTest, RenfeTest } from './006.pdf.parser/TediPdfParserTests';
import { VodafoneTest } from './006.pdf.parser/vodafone.test';

describe('TEDI PDF PARSER TESTS', () => {
  before(done => {
    const lineReader = readline.createInterface({
      input: fs.createReadStream('./test/resources/006.lotso.txt'),
    });

    lineReader.on('line', line => {
      // tslint:disable-next-line: no-console
      console.log(line);
    });
    lineReader.on('close', () => {
      done();
    });
  });
});

BipDriveTest.bipDriveTest();
CaixaBankViaTest.viatTest();
CaixaBankSolredTest.solredTest();
IonosTest.ionosTest();
MakroTest.makroTest();
RenfeTest.renfeTest();
TAPOTest.tapoTest();
NaturgyTest.naturgyTest();
OrangeTest.orangeTest();
MovistarFijoTest.movistarFijoTest();
MovistarMovilTest.movistarMovilTest();
MovistarFusionTest.movistarFusionTest();
IberdrolaClientesTest.iberdrolaClienteTest();
IberdrolaCurTest.iberdrolaCurTest();
AmazonTest.amazonTest();
SecuritasTest.securitasTest();
VodafoneTest.vodafoneTest();
EuskaltelTest.euskaltelTest();
EndesaTest.endesaTest();
AutoMLTest.automlTest();




describe('TEDI PDF PARSER TEST [TU ASESORIA ON INVOICES]', () => {
  it('PARSE TU_ASESORIA INVOICE [TUASESORIA.pdf]', done => {
    const filename = 'test/resources/TUASESORIA.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('18701520');
          expect(invoice.date).eql(new Date(2019, 0, 2, 0, 0));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('DOMINIQUE BRABANT');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('Ctra. de Cerro Alarcón, 9C, 28210 Valdemorillo, Madrid');
            }
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('ESX5738371D');
          }

          //expect(invoice.details).to.be.an('array').not.to.empty;
          // if (invoice.details) {
          //   expect(invoice.details.length).eq(1);
          //   expect(invoice.taxes).to.be.an('array').not.to.empty;
          //   if (invoice.taxes) {
          //     expect(invoice.taxes[0].tax).eq(TaxType.IVA);
          //     expect(invoice.taxes[0].quota).eq(6.3);
          //     expect(invoice.taxes[0].base).eq(30.0);
          //     expect(invoice.taxes[0].percentage).eq(21.0);
          //   }
          //   expect(invoice.details[0].description).eq('SERVICIO FISCAL CONTABLE');
          //   expect(invoice.details[0].price).eq(30.0);
          //   expect(invoice.details[0].quantity).eq(1);
          //   expect(invoice.details[0].base).eq(30.0);
          // }
          expect(invoice.total).eq(36.3);
        }
      },
      error => {
        done(error);
      },
      () => {
        done();
      },
    );
  });

  it('PARSE TU_ASESORIA INVOICE [TUASESORIA2.pdf]', done => {
    const filename = 'test/resources/TUASESORIA2.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('18701909');
          expect(invoice.date).eql(new Date(2019, 1, 1, 0, 0));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('IRENE ESPINOLA BUSQUI');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('C/ BALTARGA 7 LOCAL 1, 08207 SABADELL, Barcelona');
            }
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('ES47183579E');
          }

          //expect(invoice.details).to.be.an('array').not.to.empty;
          // if (invoice.details) {
          //   expect(invoice.details[0].description).eq('SERVICIO FISCAL CONTABLE');
          //   expect(invoice.details[0].price).eq(50.0);
          //   expect(invoice.details[0].quantity).eq(1);
          //   expect(invoice.details[0].base).eq(50.0);
          //
          //   expect(invoice.details[1].description).eq('SERVICIO LABORAL');
          //   expect(invoice.details[1].price).eq(50.0);
          //   expect(invoice.details[1].quantity).eq(1);
          //   expect(invoice.details[1].base).eq(50.0);
          //
          //   expect(invoice.taxes).to.be.an('array').not.to.empty;
          //   if (invoice.taxes) {
          //     expect(invoice.taxes[0].tax).eq(TaxType.IVA);
          //     expect(invoice.taxes[0].quota).eq(21.0);
          //     expect(invoice.taxes[0].base).eq(100.0);
          //     expect(invoice.taxes[0].percentage).eq(21.0);
          //   }
          // }
          expect(invoice.total).eq(121.0);
        }
      },
      error => {
        done(error);
      },
      () => {
        done();
      },
    );
  });

  it('PARSE TU_ASESORIA INVOICE [TUASESORIA3.pdf]', done => {
    const filename = 'test/resources/TUASESORIA3.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('18702774');
          expect(invoice.date).eql(new Date(2019, 4, 1, 0, 0));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('DOMINIQUE BRABANT');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('Ctra. de Cerro Alarcón, 9C, 28210 Valdemorillo, Madrid');
            }
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('ESX5738371D');
          }

          //expect(invoice.details).to.be.an('array').not.to.empty;
          // if (invoice.details) {
          //   expect(invoice.details[0].description).eq('SERVICIO FISCAL CONTABLE');
          //   expect(invoice.details[0].price).eq(30.0);
          //   expect(invoice.details[0].quantity).eq(1);
          //   expect(invoice.details[0].base).eq(30.0);
          //
          //   expect(invoice.taxes).to.be.an('array').not.to.empty;
          //   if (invoice.taxes) {
          //     expect(invoice.taxes[0].tax).eq(TaxType.IVA);
          //     expect(invoice.taxes[0].quota).eq(6.3);
          //     expect(invoice.taxes[0].base).eq(30.0);
          //     expect(invoice.taxes[0].percentage).eq(21.0);
          //   }
          // }
          expect(invoice.total).eq(36.3);
        }
      },
      error => {
        done(error);
      },
      () => {
        done();
      },
    );
  });
});

describe('TEDI PDF PARSER TEST [NATURGY INVOICES]', () => {
  it('PARSE NATURGY INVOICE [Naturgy_I.pdf]', done => {
    const filename = 'test/resources/Naturgy_I.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('RE18326000307639');
          expect(invoice.date).eql(new Date(2018, 10, 16, 0, 0));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('AON SOLUTIONS S.L.');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('DUQUE DE WELLINGTON 0052');
            }
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('B01487271');
          }
          expect(invoice.finances).not.to.be.undefined;
          expect(invoice.finances).to.be.an('array').not.to.empty;
          if (invoice.finances) {
            expect(invoice.finances[0].iban).eq('ES96 0075 4626 4106 0005 ****');
            expect(invoice.finances[0].due_date).eql(new Date(2018, 10, 23, 0, 0));
          }
          //expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            expect(invoice.details.length).eq(3);
            expect(invoice.taxes).to.be.an('array').not.to.empty;
            if (invoice.taxes) {
              expect(invoice.taxes[0].tax).eq(TaxType.IVA);
              expect(invoice.taxes[0].quota).eq(74.6);
              expect(invoice.taxes[0].base).eq(355.26);
              expect(invoice.taxes[0].percentage).eq(21);
              expect(invoice.details[0].vat).eq(invoice.taxes[0].percentage);
              expect(invoice.details[1].vat).eq(invoice.taxes[0].percentage);
              expect(invoice.details[2].vat).eq(invoice.taxes[0].percentage);
            }
            expect(invoice.details[0].description).eq('Consumos');
            expect(invoice.details[0].price).eq(331.96);
            expect(invoice.details[0].quantity).eq(1);
            expect(invoice.details[0].base).eq(331.96);

            expect(invoice.details[1].description).eq('Impuesto electricidad');
            expect(invoice.details[1].price).eq(0.0511269632);
            expect(invoice.details[1].quantity).eq(331.96);
            expect(invoice.details[1].base).eq(16.97);

            expect(invoice.details[2].description).eq('Alquiler de contador');
            expect(invoice.details[2].price).eq(0.197813);
            expect(invoice.details[2].quantity).eq(32);
            expect(invoice.details[2].base).eq(6.33);
          }
          expect(invoice.total).eq(429.86);
        }
      },
      error => {
        done(error);
      },
      () => {
        done();
      },
    );
  });

  it('PARSE NATURGY INVOICE [Naturgy_II.pdf]', done => {
    const filename = 'test/resources/Naturgy_II.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('RE18326000307640');
          expect(invoice.date).eql(new Date(2018, 10, 16, 0, 0));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('AON SOLUTIONS S.L.');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('DUQUE DE WELLINGTON 0052');
            }
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('B01487271');
          }
          expect(invoice.finances).not.to.be.undefined;
          expect(invoice.finances).to.be.an('array').not.to.empty;
          if (invoice.finances) {
            expect(invoice.finances[0].iban).eq('ES96 0075 4626 4106 0005 ****');
            expect(invoice.finances[0].due_date).eql(new Date(2018, 10, 23, 0, 0));
          }
          const expected = [
            {
              description: 'Consumo electricidad punta',
              quantity: 216,
              price: 0.122197,
              base: 26.39,
              discount: 0,
              vat: 21,
            },
            {
              description: 'Consumo electricidad llano',
              quantity: 621,
              price: 0.108427,
              base: 67.33,
              discount: 0,
              vat: 21,
            },
            {
              description: 'Consumo electricidad valle',
              quantity: 168,
              price: 0.085109,
              base: 14.3,
              discount: 0,
              vat: 21,
            },
            {
              description: 'Término potencia punta (33,660 kW)',
              quantity: 29,
              price: 0.111586,
              base: 108.92,
              discount: 0,
              vat: 21,
            },
            {
              description: 'Término potencia llano (33,660 kW)',
              quantity: 29,
              price: 0.066952,
              base: 65.35,
              discount: 0,
              vat: 21,
            },
            {
              description: 'Término potencia valle (33,660 kW)',
              quantity: 29,
              price: 0.044634,
              base: 43.57,
              discount: 0,
              vat: 21,
            },
            {
              description: 'Impuesto electricidad',
              price: 0.0511269632,
              quantity: 325.86,
              base: 16.66,
              vat: 21,
            },
            {
              description: 'Alquiler de contador',
              price: 0.197931,
              quantity: 29,
              base: 5.74,
              vat: 21,
            },
          ];
          //expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].quota).eq(73.13);
            expect(invoice.taxes[0].base).eq(348.26);
            expect(invoice.taxes[0].percentage).eq(21);
          }
          expect(invoice.total).eq(421.39);
        }
      },
      error => {
        done(error);
      },
      () => {
        done();
      },
    );
  });

  it('PARSE NATURGY INVOICE [Naturgy_Negativa_I.pdf]', done => {
    const filename = 'test/resources/Naturgy_Negativa_I.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('SU18320502428463');
          expect(invoice.date).eql(new Date(2018, 10, 8, 0, 0));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('AON SOLUTIONS S.L.');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('DUQUE DE WELLINGTON 0052');
            }
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('B01487271');
          }
          expect(invoice.finances).not.to.be.undefined;
          expect(invoice.finances).to.be.an('array').not.to.empty;
          if (invoice.finances) {
            expect(invoice.finances[0].iban).eq('ES96 0075 4626 4106 0005 ****');
            expect(invoice.finances[0].due_date).eql(new Date(2018, 9, 18, 0, 0));
          }
          const expected = [
            {
              description: 'Consumo electricidad punta',
              quantity: 216,
              price: 0.124274,
              base: -26.84,
              discount: 0,
              vat: 21,
            },
            {
              description: 'Consumo electricidad llano',
              quantity: 621,
              price: 0.11027,
              base: -68.48,
              discount: 0,
              vat: 21,
            },
            {
              description: 'Consumo electricidad valle',
              quantity: 168,
              price: 0.086556,
              base: -14.54,
              discount: 0,
              vat: 21,
            },
            {
              description: 'Término potencia punta (33,660 kW)',
              quantity: 29,
              price: 0.113483,
              base: -110.78,
              discount: 0,
              vat: 21,
            },
            {
              description: 'Término potencia llano (33,660 kW)',
              quantity: 29,
              price: 0.06809,
              base: -66.47,
              discount: 0,
              vat: 21,
            },
            {
              description: 'Término potencia valle (33,660 kW)',
              quantity: 29,
              price: 0.045393,
              base: -44.31,
              discount: 0,
              vat: 21,
            },
            {
              description: 'Impuesto electricidad',
              price: 0.0511269632,
              quantity: 331.42,
              base: -16.94,
              vat: 21,
            },
            {
              description: 'Alquiler de contador',
              price: 0.197931,
              quantity: 29,
              base: -5.74,
              vat: 21,
            },
          ];
          //expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].quota).eq(-74.36);
            expect(invoice.taxes[0].base).eq(-354.1);
            expect(invoice.taxes[0].percentage).eq(21);
          }
          expect(invoice.total).eq(-428.46);
        }
      },
      error => {
        done(error);
      },
      () => {
        done();
      },
    );
  });
});

describe('TEDI PDF PARSER TEST [IBERDROLA CLIENTES INVOICES]', () => {
  it('PARSE IBERDROLA INVOICE [Iberdrola_Clientes_I.pdf]', done => {
    const filename = 'test/resources/Iberdrola_Clientes_I.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('21181119010327055');
          expect(invoice.date).eql(new Date(2018, 10, 19, 0, 0));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('IN - BLAN MAQUINARIA DE HOSTELERIA, S.L.');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('B47434253');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('C/ COBALTO, 21, Bajo 1 47012 VALLADOLID');
            }
          }
          expect(invoice.finances).not.to.be.undefined;
          expect(invoice.finances).to.be.an('array').not.to.empty;
          if (invoice.finances) {
            expect(invoice.finances[0].iban).eq('ES41 0049 6064 3129 1603 ****');
            expect(invoice.finances[0].due_date).eql(new Date(2018, 10, 19, 0, 0));
            expect(invoice.finances[0].amount).eq(585.87);
          }
          const expected = [
            {
              description: 'ENERGÍA',
              quantity: 1,
              price: 477.66,
              discount: 0,
              base: 477.66,
              vat: 21,
            },
            {
              description: 'SERVICIOS Y OTROS CONCEPTOS',
              quantity: 1,
              price: 6.53,
              discount: 0,
              base: 6.53,
              vat: 21,
            },
          ];
//          expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
              expect(invoice.details[i].vat).eq(expected[i].vat);
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].quota).eq(101.68);
            expect(invoice.taxes[0].base).eq(484.19);
            expect(invoice.taxes[0].percentage).eq(21);
          }
          expect(invoice.total).eq(585.87);
        }
      },
      error => {
        done(error);
      },
      () => {
        done();
      },
    );
  });

  it('PARSE IBERDROLA INVOICE [Iberdrola_Clientes_II.pdf]', done => {
    const filename = 'test/resources/Iberdrola_Clientes_II.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('21181119010327056');
          expect(invoice.date).eql(new Date(2018, 10, 19, 0, 0));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('IN - BLAN MAQUINARIA DE HOSTELERIA, S.L.');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('B47434253');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('C/ COBALTO, 21 47012 VALLADOLID');
            }
          }
          expect(invoice.finances).not.to.be.undefined;
          expect(invoice.finances).to.be.an('array').not.to.empty;
          if (invoice.finances) {
            expect(invoice.finances[0].iban).eq('ES41 0049 6064 3129 1603 ****');
            expect(invoice.finances[0].due_date).eql(new Date(2018, 10, 19, 0, 0));
            expect(invoice.finances[0].amount).eq(835.95);
          }
          const expected = [
            {
              description: 'ENERGÍA',
              quantity: 1,
              price: 684.34,
              discount: 0,
              base: 684.34,
              vat: 21,
            },
            {
              description: 'SERVICIOS Y OTROS CONCEPTOS',
              quantity: 1,
              price: 6.53,
              discount: 0,
              base: 6.53,
              vat: 21,
            },
          ];
      //  expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
              expect(invoice.details[i].vat).eq(expected[i].vat);
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].quota).eq(145.08);
            expect(invoice.taxes[0].base).eq(690.87);
            expect(invoice.taxes[0].percentage).eq(21);
          }
          expect(invoice.total).eq(835.95);
        }
      },
      error => {
        done(error);
      },
      () => {
        done();
      },
    );
  });
});

// describe('TEDI PDF PARSER TEST [IBERDROLA CUR (COM. ULT. RECURSO) INVOICES]', () => {
//   it('PARSE IBERDROLA INVOICE [Iberdrola_CUR_I.pdf]', done => {
//     const filename = 'test/resources/Iberdrola_CUR_I.pdf';
//     TediPdfParser.extractFromFile(filename).subscribe(
//       invoice => {
//         expect(invoice).not.to.be.null;
//         expect(invoice).not.to.be.undefined;
//         if (invoice) {
//           expect(invoice.status).eq(InvoiceStatus.inbox);
//           expect(invoice.reference).eq('09190305010085331');
//           expect(invoice.date).eql(new Date(2019, 2, 5, 0, 0));
//           expect(invoice.receiver).not.to.be.undefined;
//           if (invoice.receiver) {
//             expect(invoice.receiver.name).eq('EUGENIO CASTELLANO HURTADO');
//             expect(invoice.receiver.document_country).eq('ES');
//             expect(invoice.receiver.document).eq('44671367P');
//             expect(invoice.receiver.address).not.to.be.undefined;
//             if (invoice.receiver.address) {
//               expect(invoice.receiver.address.address).eq('C/ URSULETA, 2, Bajo 1 01240 ALEGRIA DULANTZI');
//             }
//           }
//           expect(invoice.finances).not.to.be.undefined;
//           expect(invoice.finances).to.be.an('array').not.to.empty;
//           if (invoice.finances) {
//             // expect(invoice.finances[0].iban).eq('ES41 0049 6064 3129 1603 ****');
//             expect(invoice.finances[0].due_date).eql(new Date(2019, 2, 13, 0, 0));
//             expect(invoice.finances[0].amount).eq(12.1);
//           }
//           const expected = [
//             {
//               description: 'Por potencia contratada',
//               quantity: 1,
//               price: 4.74,
//               discount: 0,
//               base: 4.74,
//               vat: 21,
//             },
//             {
//               description: 'Por energía consumida',
//               quantity: 1,
//               price: 4.55,
//               discount: 0,
//               base: 4.55,
//               vat: 21,
//             },
//             {
//               description: 'Por energía consumida',
//               quantity: 1,
//               price: 0.47,
//               discount: 0,
//               base: 0.47,
//               vat: 21,
//             },
//             {
//               description: 'Alquiler equipos medida y control',
//               quantity: 1,
//               price: 0.24,
//               discount: 0,
//               base: 0.24,
//               vat: 21,
//             },
//           ];
//         //  expect(invoice.details).to.be.an('array').not.to.empty;
//           if (invoice.details) {
//             for (let i = 0; i < invoice.details.length; i++) {
//               expect(invoice.details[i].description).eq(expected[i].description);
//               expect(invoice.details[i].price).eq(expected[i].price);
//               expect(invoice.details[i].quantity).eq(expected[i].quantity);
//               expect(invoice.details[i].base).eq(expected[i].base);
//               expect(invoice.details[i].vat).eq(expected[i].vat);
//             }
//           }
//           expect(invoice.taxes).to.be.an('array').not.to.empty;
//           if (invoice.taxes) {
//             expect(invoice.taxes[0].tax).eq(TaxType.IVA);
//             expect(invoice.taxes[0].quota).eq(2.1);
//             expect(invoice.taxes[0].base).eq(10);
//             expect(invoice.taxes[0].percentage).eq(21);
//           }
//           expect(invoice.total).eq(12.1);
//         }
//       },
//       error => {
//         done(error);
//       },
//       () => {
//         done();
//       },
//     );
//   });
// });

describe('TEDI PDF PARSER TEST [ORANGE INVOICES]', () => {
  it('PARSE ORANGE INVOICE [Orange_I.pdf]', done => {
    const filename = 'test/resources/Orange_I.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('E1AL00023624-0918');
          expect(invoice.date).eql(new Date(2018, 8, 5, 0, 0));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('AON SOLUTIONS S L');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('B01487271');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('CALLE DUQUE DE WELLINGTON 52 BJ, 01010 VITORIA-GASTEIZ, ALAVA');
            }
          }
          expect(invoice.finances).not.to.be.undefined;
          expect(invoice.finances).to.be.an('array').not.to.empty;
          if (invoice.finances) {
            expect(invoice.finances[0].iban).eq('0075 4626 41 06000*****');
            expect(invoice.finances[0].due_date).eql(new Date(2018, 8, 6, 0, 0));
            expect(invoice.finances[0].amount).eq(247.61);
          }
          const expected = [
            {
              price: 86.11,
              quantity: 1,
              base: 86.11,
              discount: 0,
              description: 'Pack Love Negocio',
            },
            {
              price: 11.53,
              quantity: 1,
              base: 11.53,
              discount: 0,
              description: 'Love Negocio Total',
            },
            {
              price: 129.47,
              quantity: 1,
              base: 129.47,
              discount: 0,
              description: 'Compra de dispositivos a plazos',
            },
            {
              price: -10,
              quantity: 1,
              base: -10,
              discount: 0,
              description: 'Descuentos y promociones',
            },
          ];
          //expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].quota).eq(20.5);
            expect(invoice.taxes[0].base).eq(97.64);
            expect(invoice.taxes[0].percentage).eq(21);
          }
          expect(invoice.total).eq(247.61);
        }
      },
      error => {
        done(error);
      },
      () => {
        done();
      },
    );
  });

  it('PARSE ORANGE INVOICE [Orange_II.pdf]', done => {
    const filename = 'test/resources/Orange_II.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('E1AL00023966-1018');
          expect(invoice.date).eql(new Date(2018, 9, 5, 0, 0));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('AON SOLUTIONS S L');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('B01487271');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('CALLE DUQUE DE WELLINGTON 52 BJ, 01010 VITORIA-GASTEIZ, ALAVA');
            }
          }
          expect(invoice.finances).not.to.be.undefined;
          expect(invoice.finances).to.be.an('array').not.to.empty;
          if (invoice.finances) {
            expect(invoice.finances[0].iban).eq('0075 4626 41 06000*****');
            expect(invoice.finances[0].due_date).eql(new Date(2018, 9, 6, 0, 0));
            expect(invoice.finances[0].amount).eq(151.71);
          }
          const expected = [
            {
              price: 79.22,
              quantity: 1,
              base: 79.22,
              discount: 0,
              description: 'Pack Love Negocio',
            },
            {
              price: 14.16,
              quantity: 1,
              base: 14.16,
              discount: 0,
              description: 'Love Negocio Total',
            },
            {
              price: 38.72,
              quantity: 1,
              base: 38.72,
              discount: 0,
              description: 'Compra de dispositivos a plazos',
            },
          ];
          //expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].quota).eq(19.61);
            expect(invoice.taxes[0].base).eq(93.38);
            expect(invoice.taxes[0].percentage).eq(21);
          }
          expect(invoice.total).eq(151.71);
        }
      },
      error => {
        done(error);
      },
      () => {
        done();
      },
    );
  });

  it('PARSE ORANGE INVOICE [Orange_III.pdf]', done => {
    const filename = 'test/resources/Orange_III.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('E1AL00024204-1118');
          expect(invoice.date).eql(new Date(2018, 10, 5, 0, 0));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('AON SOLUTIONS S L');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('B01487271');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('CALLE DUQUE DE WELLINGTON 52 BJ, 01010 VITORIA-GASTEIZ, ALAVA');
            }
          }
          expect(invoice.finances).not.to.be.undefined;
          expect(invoice.finances).to.be.an('array').not.to.empty;
          if (invoice.finances) {
            expect(invoice.finances[0].iban).eq('0075 4626 41 06000*****');
            expect(invoice.finances[0].due_date).eql(new Date(2018, 10, 6, 0, 0));
            expect(invoice.finances[0].amount).eq(146.62);
          }
          const expected = [
            {
              price: 77.64,
              quantity: 1,
              base: 77.64,
              discount: 0,
              description: 'Pack Love Negocio',
            },
            {
              price: 11.53,
              quantity: 1,
              base: 11.53,
              discount: 0,
              description: 'Love Negocio Total',
            },
            {
              price: 38.72,
              quantity: 1,
              base: 38.72,
              discount: 0,
              description: 'Compra de dispositivos a plazos',
            },
          ];
          //expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].quota).eq(18.73);
            expect(invoice.taxes[0].base).eq(89.17);
            expect(invoice.taxes[0].percentage).eq(21);
          }
          expect(invoice.total).eq(146.62);
        }
      },
      error => {
        done(error);
      },
      () => {
        done();
      },
    );
  });
});

describe('TEDI PDF PARSER TEST [MOVISTAR FIJO INVOICES]', () => {
  it('PARSE MOVISTAR FIJO INVOICE [Movistar_Fijo_I.pdf]', done => {
    const filename = 'test/resources/Movistar_Fijo_I.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('TA5ZH0179306');
          expect(invoice.date).eql(new Date(2018, 10, 19, 0, 0));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('UDAPA SDAD.COOP.');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('00F01131978');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('Calle Paduleta Kalea, 1 Bajo');
              expect(invoice.receiver.address.postal_code).eq('01015');
              expect(invoice.receiver.address.city).eq('Jundiz');
            }
          }
          expect(invoice.finances).not.to.be.undefined;
          expect(invoice.finances).to.be.an('array').not.to.empty;
          if (invoice.finances) {
            expect(invoice.finances[0].due_date).eql(new Date(2018, 10, 19, 0, 0));
            expect(invoice.finances[0].amount).eq(20.66);
          }
          const expected = [
            {
              price: 14.379,
              quantity: 1,
              base: 14.379,
              discount: 0,
              description: 'Cuotas Mensuales',
            },
            {
              price: 2.697,
              quantity: 1,
              base: 2.697,
              discount: 0,
              description: 'Consumos',
            },
          ];
          //expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].base).eq(17.076);
            expect(invoice.taxes[0].percentage).eq(21);
            expect(invoice.taxes[0].quota).eq(3.586);
          }
          expect(invoice.total).eq(20.66);
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });
});

describe('TEDI PDF PARSER TEST [MOVISTAR FUSION INVOICES]', () => {
  it('PARSE MOVISTAR FUSION  INVOICE [Movistar_Fusion_I.pdf]', done => {
    const filename = 'test/resources/Movistar_Fusion_I.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('TA60G0026117');
          expect(invoice.date).eql(new Date(2018, 10, 1, 0, 0));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('AON SOLUTIONS S.L.U.');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('00B01487271');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('Calle Duque de Wellington, 52 Bajo');
              expect(invoice.receiver.address.postal_code).eq('01010');
              expect(invoice.receiver.address.city).eq('Vitoria-Gasteiz');
            }
          }
          expect(invoice.finances).not.to.be.undefined;
          expect(invoice.finances).to.be.an('array').not.to.empty;
          if (invoice.finances) {
            expect(invoice.finances[0].due_date).eql(new Date(2018, 10, 1, 0, 0));
            expect(invoice.finances[0].amount).eq(121.59);
          }
          const expected = [
            {
              price: 72.0,
              quantity: 1,
              base: 72.0,
              discount: 0,
              description: 'Cuotas Mensuales',
            },
            {
              price: 7.8265,
              quantity: 1,
              base: 7.8265,
              discount: 0,
              description: 'Otros Conceptos',
            },
          ];
          //expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].base).eq(79.8265);
            expect(invoice.taxes[0].percentage).eq(21);
            expect(invoice.taxes[0].quota).eq(16.7636);
          }
          expect(invoice.total).eq(121.59);
        }
      },
      error => {
        done(error);
      },
      () => {
        done();
      },
    );
  });

  it('PARSE MOVISTAR FUSION  INVOICE [Movistar_Fusion_II.pdf]', done => {
    const filename = 'test/resources/Movistar_Fusion_II.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('TA60F0026654');
          expect(invoice.date).eql(new Date(2018, 9, 1, 0, 0));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('AON SOLUTIONS S.L.U.');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('00B01487271');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('Calle Duque de Wellington, 52 Bajo');
              expect(invoice.receiver.address.postal_code).eq('01010');
              expect(invoice.receiver.address.city).eq('Vitoria-Gasteiz');
            }
          }
          expect(invoice.finances).not.to.be.undefined;
          expect(invoice.finances).to.be.an('array').not.to.empty;
          if (invoice.finances) {
            expect(invoice.finances[0].due_date).eql(new Date(2018, 9, 1, 0, 0));
            expect(invoice.finances[0].amount).eq(87.99);
          }
          const expected = [
            {
              price: 72.0,
              quantity: 1,
              base: 72.0,
              discount: 0,
              description: 'Cuotas Mensuales',
            },
            {
              price: 0.72,
              quantity: 1,
              base: 0.72,
              discount: 0,
              description: 'Otros Conceptos',
            },
          ];
        //  expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].base).eq(72.72);
            expect(invoice.taxes[0].percentage).eq(21);
            expect(invoice.taxes[0].quota).eq(15.2712);
          }
          expect(invoice.total).eq(87.99);
        }
      },
      error => {
        done(error);
      },
      () => {
        done();
      },
    );
  });

  it('PARSE MOVISTAR FUSION  INVOICE [Movistar_Fusion_III.pdf]', done => {
    const filename = 'test/resources/Movistar_Fusion_III.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('TA60F0248732');
          expect(invoice.date).eql(new Date(2018, 9, 1, 0, 0));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('Business Process Management Systems');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('00B01398387');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('Calle Orio Kalea, 6 1º D');
              expect(invoice.receiver.address.postal_code).eq('01010');
              expect(invoice.receiver.address.city).eq('Vitoria-Gasteiz');
            }
          }
          expect(invoice.finances).not.to.be.undefined;
          expect(invoice.finances).to.be.an('array').not.to.empty;
          if (invoice.finances) {
            expect(invoice.finances[0].due_date).eql(new Date(2018, 9, 1, 0, 0));
            expect(invoice.finances[0].amount).eq(89.12);
          }
          const expected = [
            {
              price: 73.6529,
              quantity: 1,
              base: 73.6529,
              discount: 0,
              description: 'Cuotas Mensuales',
            },
          ];
        //  expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].base).eq(73.6529);
            expect(invoice.taxes[0].percentage).eq(21);
            expect(invoice.taxes[0].quota).eq(15.4671);
          }
          expect(invoice.total).eq(89.12);
        }
      },
      error => {
        done(error);
      },
      () => {
        done();
      },
    );
  });

  it('PARSE MOVISTAR FUSION  INVOICE [Movistar_Fusion_IV.pdf]', done => {
    const filename = 'test/resources/Movistar_Fusion_IV.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('TA60C0288630');
          expect(invoice.date).eql(new Date(2018, 6, 1, 0, 0));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('Business Process Management Systems');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('00B01398387');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('Calle Orio Kalea, 6 1º D');
              expect(invoice.receiver.address.postal_code).eq('01010');
              expect(invoice.receiver.address.city).eq('Vitoria-Gasteiz');
            }
          }
          expect(invoice.finances).not.to.be.undefined;
          expect(invoice.finances).to.be.an('array').not.to.empty;
          if (invoice.finances) {
            expect(invoice.finances[0].due_date).eql(new Date(2018, 6, 1, 0, 0));
            expect(invoice.finances[0].amount).eq(84.58);
          }
          const expected = [
            {
              price: 69.4214,
              quantity: 1,
              base: 69.4214,
              discount: 0,
              description: 'Cuotas Mensuales',
            },
            {
              price: 0.48,
              quantity: 1,
              base: 0.48,
              discount: 0,
              description: 'Otros Conceptos',
            },
          ];
          //expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].base).eq(69.9014);
            expect(invoice.taxes[0].percentage).eq(21);
            expect(invoice.taxes[0].quota).eq(14.6793);
          }
          expect(invoice.total).eq(84.58);
        }
      },
      error => {
        done(error);
      },
      () => {
        done();
      },
    );
  });

  it('PARSE MOVISTAR FUSION  INVOICE [Movistar_Fusion_V.pdf]', done => {
    const filename = 'test/resources/Movistar_Fusion_V.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('TA60E0275244');
          expect(invoice.date).eql(new Date(2018, 8, 1, 0, 0));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('Business Process Management Systems');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('00B01398387');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('Calle Orio Kalea, 6 1º D');
              expect(invoice.receiver.address.postal_code).eq('01010');
              expect(invoice.receiver.address.city).eq('Vitoria-Gasteiz');
            }
          }
          expect(invoice.finances).not.to.be.undefined;
          expect(invoice.finances).to.be.an('array').not.to.empty;
          if (invoice.finances) {
            expect(invoice.finances[0].due_date).eql(new Date(2018, 8, 1, 0, 0));
            expect(invoice.finances[0].amount).eq(100.18);
          }
          const expected = [
            {
              price: 73.6529,
              quantity: 1,
              base: 73.6529,
              discount: 0,
              description: 'Cuotas Mensuales',
            },
            {
              price: 9.141,
              quantity: 1,
              base: 9.141,
              discount: 0,
              description: 'Otros Conceptos',
            },
          ];
          //expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].base).eq(82.7939);
            expect(invoice.taxes[0].percentage).eq(21);
            expect(invoice.taxes[0].quota).eq(17.3867);
          }
          expect(invoice.total).eq(100.18);
        }
      },
      error => {
        done(error);
      },
      () => {
        done();
      },
    );
  });
});

describe('TEDI PDF PARSER TEST [ENDESA INVOICES]', () => {
  it('PARSE ENDESA INVOICE [Endesa_I.pdf]', done => {
    const filename = 'test/resources/Endesa_I.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });
});

describe('TEDI PDF PARSER TEST [UNKNOWN INVOICES]', () => {
  it('PARSE UNKNOWN INVOICE [Unknown.pdf]', done => {
    const filename = 'test/resources/Unknown.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });
});

// ============== TU ASESORIA PERSONAL ONLINE TEST (TAPO) ================ //
describe('TEDI PDF PARSER TEST [TU ASESORIA ONLINE]', () => {
  it('PARSE TAPO INVOICE [ TU_ASESORIA_PERSONAL_ONLINE_1.pdf ]', done => {
    const filename = 'test/resources/TU_ASESORIA_PERSONAL_ONLINE_1.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        // console.log( JSON.stringify( invoice , null , 1 ) )
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          //    expect(invoice.reference).eq('B87266656');
          expect(invoice.date).eql(new Date(2019, 2, 1));
          expect(invoice.receiver).not.that.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('MARIA JENNIFER VIQUE QUINDE');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('ES52908166R');
            expect(invoice.receiver.address).not.to.undefined;
            if (invoice.receiver.address) {
              // expect(invoice.receiver.address.address).eq('PASEO DE LOS ARTILLEROS 6');
              // expect(invoice.receiver.address.postal_code).eq('28032');
              // expect(invoice.receiver.address.city).eq('Madrid');
            }
          }
          const expected = [
            {
              price: 30,
              quantity: 1,
              base: 30,
              discount: 0,
              description: 'SERVICIO FISCAL CONTABLE',
            },
            {
              price: 24,
              quantity: 1,
              base: 24,
              discount: 0,
              description: 'SERVICIO LABORAL',
            },
            {
              price: -12,
              quantity: 1,
              base: -12,
              discount: 0,
              description: 'Regularización Laboral Enero',
            },
          ];
          //expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].base).eq(42.0);
            expect(invoice.taxes[0].percentage).eq(21.0);
            expect(invoice.taxes[0].quota).eq(8.82);
          }
          expect(invoice.total).eq(50.82);
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });

  it('PARSE TAPO INVOICE [ TU_ASESORIA_PERSONAL_ONLINE_2.pdf ]', done => {
    const filename = 'test/resources/TU_ASESORIA_PERSONAL_ONLINE_2.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        // console.log( JSON.stringify( invoice , null , 1 ) )
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          // expect(invoice.reference).eq('B87266656');
          expect(invoice.date).eql(new Date(2019, 3, 1));
          expect(invoice.receiver).not.that.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('REFORMAS E INSTALACIONES ');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('ESB90127861');
            expect(invoice.receiver.address).not.to.undefined;
            if (invoice.receiver.address) {
              // expect(invoice.receiver.address.address).eq('CALLE RAMBLA DE ALMERIA 13 1ºC');
              // expect(invoice.receiver.address.postal_code).eq('41014');
              // expect(invoice.receiver.address.city).eq('Sevilla');
            }
          }
          const expected = [
            {
              price: 60,
              quantity: 1,
              base: 60,
              discount: 0,
              description: 'PYME',
            },
            {
              price: 24,
              quantity: 1,
              base: 24,
              discount: 0,
              description: 'SERVICIO LABORAL',
            },
          ];
          //expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].base).eq(84.0);
            expect(invoice.taxes[0].percentage).eq(21.0);
            expect(invoice.taxes[0].quota).eq(17.64);
          }
          expect(invoice.total).eq(101.64);
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });

  it('PARSE TAPO INVOICE [ TU_ASESORIA_PERSONAL_ONLINE_3.pdf ]', done => {
    const filename = 'test/resources/TU_ASESORIA_PERSONAL_ONLINE_3.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        // console.log( JSON.stringify( invoice , null , 1 ) )
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          // expect(invoice.reference).eq('B87266656');
          expect(invoice.date).eql(new Date(2019, 0, 2));
          expect(invoice.receiver).not.that.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('GLORIA MAGDALENA RIOS VILLACIS');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('ES51549801Q');
            expect(invoice.receiver.address).not.to.undefined;
            if (invoice.receiver.address) {
              // expect(invoice.receiver.address.address).eq('CL SINESIO DELGADO 3 BJ 2');
              // expect(invoice.receiver.address.postal_code).eq('28029');
              // expect(invoice.receiver.address.city).eq('Madrid');
            }
          }
          const expected = [
            {
              price: 30,
              quantity: 1,
              base: 30,
              discount: 0,
              description: 'SERVICIO FISCAL CONTABLE',
            },
          ];
          //expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].base).eq(30.0);
            expect(invoice.taxes[0].percentage).eq(21.0);
            expect(invoice.taxes[0].quota).eq(6.3);
          }
          expect(invoice.total).eq(36.3);
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });

  it('PARSE TAPO INVOICE [ TU_ASESORIA_PERSONAL_ONLINE_4.pdf ]', done => {
    const filename = 'test/resources/TU_ASESORIA_PERSONAL_ONLINE_4.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        // console.log( JSON.stringify( invoice , null , 1 ) )
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          // expect(invoice.reference).eq('B87266656');
          expect(invoice.date).eql(new Date(2019, 4, 1));
          expect(invoice.receiver).not.that.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('HERNAN MARIANO ORIOLI BISSO');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('ES45197300T');
            expect(invoice.receiver.address).not.to.undefined;
            if (invoice.receiver.address) {
              // expect(invoice.receiver.address.address).eq('CALLE PIGNATELLI NUMERO 13');
              // expect(invoice.receiver.address.postal_code).eq('50690');
              // expect(invoice.receiver.address.city).eq('Zaragoza');
            }
          }
          const expected = [
            {
              price: 30,
              quantity: 1,
              base: 30,
              discount: 0,
              description: 'SERVICIO FISCAL CONTABLE',
            },
          ];
        //  expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].base).eq(30.0);
            expect(invoice.taxes[0].percentage).eq(21.0);
            expect(invoice.taxes[0].quota).eq(6.3);
          }
          expect(invoice.total).eq(36.3);
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });

  it('PARSE TAPO INVOICE [ TU_ASESORIA_PERSONAL_ONLINE_5.pdf ]', done => {
    const filename = 'test/resources/TU_ASESORIA_PERSONAL_ONLINE_5.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        // console.log( JSON.stringify( invoice , null , 1 ) )
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          // expect(invoice.reference).eq('B87266656');
          expect(invoice.date).eql(new Date(2019, 4, 1));
          expect(invoice.receiver).not.that.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('PEDRO GIL CUEVAS');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('ES80058404G');
            expect(invoice.receiver.address).not.to.undefined;
            if (invoice.receiver.address) {
              // expect(invoice.receiver.address.address).eq('CALLE ZAMORA 6 PISO 5ºB');
              // expect(invoice.receiver.address.postal_code).eq('28922');
              // expect(invoice.receiver.address.city).eq('Madrid');
            }
          }
          const expected = [
            {
              price: 30,
              quantity: 1,
              base: 30,
              discount: 0,
              description: 'SERVICIO FISCAL CONTABLE',
            },
            {
              price: 12,
              quantity: 1,
              base: 12,
              discount: 0,
              description: 'SERVICIO LABORAL',
            },
          ];
        //  expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].base).eq(42.0);
            expect(invoice.taxes[0].percentage).eq(21.0);
            expect(invoice.taxes[0].quota).eq(8.82);
          }
          expect(invoice.total).eq(50.82);
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });

  it('PARSE TAPO INVOICE [ TU_ASESORIA_PERSONAL_ONLINE_6.pdf ]', done => {
    const filename = 'test/resources/TU_ASESORIA_PERSONAL_ONLINE_6.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        // console.log( JSON.stringify( invoice , null , 1 ) )
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          // expect(invoice.reference).eq('B87266656');
          expect(invoice.date).eql(new Date(2019, 4, 1));
          expect(invoice.receiver).not.that.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('EMILIO JOSE LOPEZ MOYA');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('ES05203793C');
            expect(invoice.receiver.address).not.to.undefined;
            if (invoice.receiver.address) {
              // expect(invoice.receiver.address.address).eq('CL PLAZA PINTOR LUCAS Nº 2, Dº');
              // expect(invoice.receiver.address.postal_code).eq('28026');
              // expect(invoice.receiver.address.city).eq('Madrid');
            }
          }
          const expected = [
            {
              price: 30,
              quantity: 1,
              base: 30,
              discount: 0,
              description: 'SERVICIO FISCAL CONTABLE',
            },
          ];
          //expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].base).eq(30.0);
            expect(invoice.taxes[0].percentage).eq(21.0);
            expect(invoice.taxes[0].quota).eq(6.3);
          }
          expect(invoice.total).eq(36.3);
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });
});
// ============== FIN TU ASESORIA PERSONAL ONLINE TEST (TAPO) ================ //

// ============== AMAZON TEST  ================ //
describe('TEDI PDF PARSER TEST [ AMAZON ]', () => {
  it('PARSE AMAZON INVOICE [ AMAZON_2.pdf ]', done => {
    const filename = 'test/resources/AMAZON_2.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        // console.log( JSON.stringify( invoice , null , 1 ) )
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('W0184081H');
          expect(invoice.date).eql(new Date(2019, 1, 18));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('JORGE MONTES BENITO');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('CONVENTO 19');
              expect(invoice.receiver.address.postal_code).eq('37210');
              expect(invoice.receiver.address.city).eq('SALAMANCA');
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].base).eq(48.66);
            expect(invoice.taxes[0].percentage).eq(21.0);
            expect(invoice.taxes[0].quota).eq(10.22);
          }
          const expected = [
            {
              price: 32.9,
              quantity: 1,
              base: 27.19,
              discount: 0,
              description:
                'TP-Link TL-WA901ND - Punto de acceso inalámbrico/Extensor de red WiFi (N450 Mbps, 3 Antenas, Power over Ethernet, WPS), blanco',
            },
            {
              price: 16.99,
              quantity: 1,
              base: 14.04,
              discount: 0,
              description:
                'TP-Link TL-SG105 Switch Gigabit Conmutador de red 5 puertos para sobremesas, 1000Mbps, Acero inoxidable, IGMP Snooping, QoS',
            },
            {
              price: 8.99,
              quantity: 1,
              base: 7.43,
              discount: 0,
              description: 'Memoria Flash USB SanDisk Cruzer Blade 16 GB con USB 2.0 Pack Triple',
            },
          ];
          //expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.total).eq(58.88);
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });

  it('PARSE AMAZON INVOICE [ AMAZON_3.pdf ]', done => {
    const filename = 'test/resources/AMAZON_3.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        // console.log( JSON.stringify( invoice , null , 1 ) )
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('W0184081H');
          expect(invoice.date).eql(new Date(2019, 3, 26));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('Grupo Tecnologico ARBINOVA SL');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('ESB32440927');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('Calle Valdomiño nº1');
              expect(invoice.receiver.address.postal_code).eq('32890');
              expect(invoice.receiver.address.city).eq('orense');
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].base).eq(26.2);
            expect(invoice.taxes[0].percentage).eq(21.0);
            expect(invoice.taxes[0].quota).eq(5.5);
          }
          const expected = [
            {
              price: 31.7,
              quantity: 1,
              base: 26.2,
              discount: 0,
              description: 'triplast 13 x 19 pulgadas – Bolsa de plástico para envíos postales gris (200 unidades)',
            },
          ];
          //expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.total).eq(31.7);
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });

  it('PARSE AMAZON INVOICE [ AMAZON_1.pdf ]', done => {
    const filename = 'test/resources/AMAZON_1.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        // console.log( JSON.stringify( invoice , null , 1 ) )
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('W0184081H');
          expect(invoice.date).eql(new Date(2019, 0, 10));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('Alejandra Jimenez González');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('C/Santísima Trinidad, 30, planta 7,puerta 8');
              expect(invoice.receiver.address.postal_code).eq('28010');
              expect(invoice.receiver.address.city).eq('Madrid');
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].base).eq(11.57);
            expect(invoice.taxes[0].percentage).eq(21.0);
            expect(invoice.taxes[0].quota).eq(2.43);
          }
          const expected = [
            {
              price: 14.0,
              quantity: 1,
              base: 11.57,
              discount: 0,
              description: 'Agenda 2019 semana vista apaisada español',
            },
          ];
        //  expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.total).eq(14.0);
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });

  it('PARSE AMAZON INVOICE [ AMAZON_4.pdf ]', done => {
    const filename = 'test/resources/AMAZON_4.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        // console.log( JSON.stringify( invoice , null , 1 ) )
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('W0184081H');
          expect(invoice.date).eql(new Date(2019, 0, 8));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('Santiago Soria Lopez');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('22578233F');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('Avenida Doctor Waksman 35, 6º, B');
              expect(invoice.receiver.address.postal_code).eq('46006');
              expect(invoice.receiver.address.city).eq('Valencia');
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].base).eq(214.73);
            expect(invoice.taxes[0].percentage).eq(21.0);
            expect(invoice.taxes[0].quota).eq(45.09);
          }
          const expected = [
            {
              price: 70.9,
              quantity: 1,
              base: 58.6,
              discount: 0,
              description: 'Crucial CT2K4G4DFS824A - Kit de Memoria RAM de 8 GB (4 GB x 2, DDR4, 2400 MT/s, PC4-19200, SR x8, DIMM, 288-Pin)',
            },
            {
              price: 188.92,
              quantity: 1,
              base: 156.1,
              discount: 0,
              description: 'Intel Pentium Core i5-6600 - Microprocesador (3.30 GHz 6M Box Skylake) Color Plata',
            },
          ];
        //  expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.total).eq(259.82);
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });

  it('PARSE AMAZON INVOICE [ AMAZON_5.pdf ]', done => {
    const filename = 'test/resources/AMAZON_5.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        // console.log( JSON.stringify( invoice , null , 1 ) )
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('W0184081H');
          expect(invoice.date).eql(new Date(2019, 1, 9));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('SOCRAM SERVICIOS DE LIMPIEZA S.L.');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('B67349274');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('Carrer Sant Joan Bosco 36 Ecochelimpio');
              expect(invoice.receiver.address.postal_code).eq('08830');
              expect(invoice.receiver.address.city).eq('Barcelona');
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].base).eq(11.12);
            expect(invoice.taxes[0].percentage).eq(21.0);
            expect(invoice.taxes[0].quota).eq(2.33);
          }
          const expected = [
            {
              price: 13.45,
              quantity: 1,
              base: 11.12,
              discount: 0,
              description: 'GreenBlue - GB104 Temporizador digital, planificador semanal, DIN 16A, Carril DIN, panel de control',
            },
          ];
        //  expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.total).eq(13.45);
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });

  it('PARSE AMAZON INVOICE [ AMAZON_6.pdf ]', done => {
    const filename = 'test/resources/AMAZON_6.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        // console.log( JSON.stringify( invoice , null , 1 ) )
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('W0184081H');
          expect(invoice.date).eql(new Date(2018, 11, 28));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('MULTISERVEI ALT CAMP S.L');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('B55719207');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('Calle Crespia 87');
              expect(invoice.receiver.address.postal_code).eq('43811');
              expect(invoice.receiver.address.city).eq('Tarragona');
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].base).eq(16.45);
            expect(invoice.taxes[0].percentage).eq(21.0);
            expect(invoice.taxes[0].quota).eq(3.45);
          }
          const expected = [
            {
              price: 19.9,
              quantity: 1,
              base: 16.45,
              discount: 0,
              description: 'Philips Barbero MG3730/15 - Recortador de Barba y Precisión 8 en 1, Cuchillas autoafilables, Incluye Funda de Viaje',
            },
          ];
          //expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.total).eq(19.9);
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });

  it('PARSE AMAZON INVOICE [ AMAZON_7.pdf ]', done => {
    const filename = 'test/resources/AMAZON_7.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('W0184081H');
          // console.log("FECHA =========> " + invoice.date );
          expect(invoice.date).eql(new Date(2019, 3, 16));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('Irene Vehí Pomés');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('77961911F');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('C/ Miquel Rosset, 35 at 2');
              expect(invoice.receiver.address.postal_code).eq('17488');
              expect(invoice.receiver.address.city).eq('Girona');
            }
          }
          expect(invoice.taxes).to.be.an('array').not.to.empty;
          if (invoice.taxes) {
            expect(invoice.taxes[0].tax).eq(TaxType.IVA);
            expect(invoice.taxes[0].base).eq(17.26);
            expect(invoice.taxes[0].percentage).eq(10.0);
            expect(invoice.taxes[0].quota).eq(1.72);
          }
          const expected = [
            {
              price: 9.49,
              quantity: 1,
              base: 8.627,
              discount: 0,
              description: 'Purina Gourmet Gold Mousse comida para gatos con Buey 24 x 85 g',
            },
            {
              price: 9.49,
              quantity: 1,
              base: 8.627,
              discount: 0,
              description: 'Purina Gourmet Gold Mousse comida para gatos con Pato y Espinacas 24 x 85 g',
            },
          ];
          //expect(invoice.details).to.be.an('array').not.to.empty;
          if (invoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
              expect(invoice.details[i].description).eq(expected[i].description);
              expect(invoice.details[i].price).eq(expected[i].price);
              expect(invoice.details[i].quantity).eq(expected[i].quantity);
              expect(invoice.details[i].base).eq(expected[i].base);
            }
          }
          expect(invoice.total).eq(18.98);
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });
});

// ============== FIN AMAZON  ============== //

// ============== MOVISTAR MOVIL TEST  ================ //
describe('TEDI PDF PARSER TEST [ MOVISTAR MOVIL ]', () => {
  it('PARSE MOVISTAR MOVIL INVOICE [ MOVISTAR MOVIL_1.pdf ]', done => {
    const filename = 'test/resources/MOVISTAR_MOVIL_1.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('28-B9M0-012276');
          expect(invoice.date).eql(new Date(2019, 1, 1));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('UDAPA, S.COOP.');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('F01131978');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('CL ARRIURDINA N 6 P.I. JUNDIZ');
              expect(invoice.receiver.address.postal_code).eq('01015');
              expect(invoice.receiver.address.city).eq('VITORIA-GASTEIZ');
            }
            expect(invoice.finances).not.to.be.undefined;
            expect(invoice.finances).to.be.an('array').not.to.empty;
            if (invoice.finances) {
              expect(invoice.finances[0].due_date).eql(new Date(2019, 1, 1, 0, 0));
              expect(invoice.finances[0].amount).eq(198.33);
            }
            expect(invoice.taxes).to.be.an('array').not.to.empty;
            if (invoice.taxes) {
              expect(invoice.taxes[0].tax).eq(TaxType.IVA);
              expect(invoice.taxes[0].base).eq(163.9055);
              expect(invoice.taxes[0].percentage).eq(21.0);
              expect(invoice.taxes[0].quota).eq(34.4202);
            }
            const expected = [
              {
                price: 0.0,
                quantity: 1,
                base: 0.0,
                discount: 0,
                description: 'Datos (18 Dic. 18 a 17 Ene. 19)',
              },
              {
                price: 58.0,
                quantity: 1,
                base: 58.0,
                discount: 0,
                description: 'Cuotas Mensuales',
              },
              {
                price: 121.9925,
                quantity: 1,
                base: 121.9925,
                discount: 0,
                description: 'Llamadas (18 Dic. 18 a 17 Ene. 19)',
              },
              {
                price: -16.087,
                quantity: 1,
                base: -16.087,
                discount: 0,
                description: 'Descuentos por cliente',
              },
            ];
            //expect(invoice.details).to.be.an('array').not.to.empty;
            if (invoice.details) {
              for (let i = 0; i < invoice.details.length; i++) {
                expect(invoice.details[i].description).eq(expected[i].description);
                expect(invoice.details[i].price).eq(expected[i].price);
                expect(invoice.details[i].quantity).eq(expected[i].quantity);
                expect(invoice.details[i].base).eq(expected[i].base);
              }
            }
            expect(invoice.total).eq(198.33);
          }
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });

  it('PARSE MOVISTAR MOVIL INVOICE [ MOVISTAR MOVIL_2.pdf ]', done => {
    const filename = 'test/resources/MOVISTAR_MOVIL_2.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        // console.log( JSON.stringify( invoice , null , 1 ) );
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('28-B9M0-006411');
          expect(invoice.date).eql(new Date(2019, 1, 1));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('PTE SAN MIGUEL SL.');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('B86501004');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('PSO CASTELLANA N 114 ESC 1 PLANTA 2');
              expect(invoice.receiver.address.postal_code).eq('28046');
              expect(invoice.receiver.address.city).eq('MADRID');
            }
            expect(invoice.taxes).to.be.an('array').not.to.empty;
            if (invoice.taxes) {
              expect(invoice.taxes[0].tax).eq(TaxType.IVA);
              expect(invoice.taxes[0].base).eq(258.5152);
              expect(invoice.taxes[0].percentage).eq(21.0);
              expect(invoice.taxes[0].quota).eq(54.2882);
            }
            const expected = [
              {
                price: 58.3984,
                quantity: 1,
                base: 58.3984,
                discount: 0,
                description: 'Datos (18 Dic. 18 a 17 Ene. 19)',
              },
              {
                price: -125.263,
                quantity: 1,
                base: -125.263,
                discount: 0,
                description: 'Descuentos por cliente',
              },
              {
                price: 244.3798,
                quantity: 1,
                base: 244.3798,
                discount: 0,
                description: 'Llamadas (18 Dic. 18 a 17 Ene. 19)',
              },
              {
                price: 81.0,
                quantity: 1,
                base: 81.0,
                discount: 0,
                description: 'Cuotas Mensuales',
              },
            ];
            //expect(invoice.details).to.be.an('array').not.to.empty;
            if (invoice.details) {
              for (let i = 0; i < invoice.details.length; i++) {
                expect(invoice.details[i].description).eq(expected[i].description);
                expect(invoice.details[i].price).eq(expected[i].price);
                expect(invoice.details[i].quantity).eq(expected[i].quantity);
                expect(invoice.details[i].base).eq(expected[i].base);
              }
            }
            expect(invoice.total).eq(312.8);
          }
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });

  it('PARSE MOVISTAR MOVIL INVOICE [ MOVISTAR MOVIL_3.pdf ]', done => {
    const filename = 'test/resources/MOVISTAR_MOVIL_3.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        // console.log( JSON.stringify( invoice , null , 1 ) );
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('28-B9U1-025414');
          expect(invoice.date).eql(new Date(2019, 1, 1));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('LOCAL PASSWORD S.L.');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('B84129576');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('CL ARLABAN N 7 OFICINA 69');
              expect(invoice.receiver.address.postal_code).eq('28014');
              expect(invoice.receiver.address.city).eq('MADRID');
            }
            expect(invoice.taxes).to.be.an('array').not.to.empty;
            if (invoice.taxes) {
              expect(invoice.taxes[0].tax).eq(TaxType.IVA);
              expect(invoice.taxes[0].base).eq(72.2172);
              expect(invoice.taxes[0].percentage).eq(21.0);
              expect(invoice.taxes[0].quota).eq(15.1656);
            }
            const expected = [
              {
                price: 49.9941,
                quantity: 1,
                base: 49.9941,
                discount: 0,
                description: 'Datos (18 Dic. 18 a 17 Ene. 19)',
              },
              {
                price: 20.0,
                quantity: 1,
                base: 20.0,
                discount: 0,
                description: 'Conceptos fuera de base imponible',
              },
              {
                price: 22.2231,
                quantity: 1,
                base: 22.2231,
                discount: 0,
                description: 'Llamadas (18 Dic. 18 a 17 Ene. 19)',
              },
              {
                price: 0.0,
                quantity: 1,
                base: 0.0,
                discount: 0,
                description: 'Otros conceptos',
              },
            ];
            //expect(invoice.details).to.be.an('array').not.to.empty;
            if (invoice.details) {
              for (let i = 0; i < invoice.details.length; i++) {
                expect(invoice.details[i].description).eq(expected[i].description);
                expect(invoice.details[i].price).eq(expected[i].price);
                expect(invoice.details[i].quantity).eq(expected[i].quantity);
                expect(invoice.details[i].base).eq(expected[i].base);
              }
            }
            expect(invoice.total).eq(107.38);
          }
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });

  it('PARSE MOVISTAR MOVIL INVOICE [ MOVISTAR MOVIL_4.pdf ]', done => {
    const filename = 'test/resources/MOVISTAR_MOVIL_4.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        // console.log( JSON.stringify( invoice , null , 1 ) );
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('28-B9U1-084638');
          expect(invoice.date).eql(new Date(2019, 1, 1));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('INNOVACION COLABORATIVA S.L.');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('B87059358');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('CL ALAMEDA N 22 BAJO');
              expect(invoice.receiver.address.postal_code).eq('28014');
              expect(invoice.receiver.address.city).eq('MADRID');
            }
            expect(invoice.taxes).to.be.an('array').not.to.empty;
            if (invoice.taxes) {
              expect(invoice.taxes[0].tax).eq(TaxType.IVA);
              expect(invoice.taxes[0].base).eq(7.28);
              expect(invoice.taxes[0].percentage).eq(21.0);
              expect(invoice.taxes[0].quota).eq(1.5288);
            }
            const expected = [
              {
                price: 7.28,
                quantity: 1,
                base: 7.28,
                discount: 0,
                description: 'Llamadas (18 Dic. 18 a 17 Ene. 19)',
              },
            ];
            //expect(invoice.details).to.be.an('array').not.to.empty;
            if (invoice.details) {
              for (let i = 0; i < invoice.details.length; i++) {
                expect(invoice.details[i].description).eq(expected[i].description);
                expect(invoice.details[i].price).eq(expected[i].price);
                expect(invoice.details[i].quantity).eq(expected[i].quantity);
                expect(invoice.details[i].base).eq(expected[i].base);
              }
            }
            expect(invoice.total).eq(8.81);
          }
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });

  it('PARSE MOVISTAR MOVIL INVOICE [ MOVISTAR MOVIL_5.pdf ]', done => {
    const filename = 'test/resources/MOVISTAR_MOVIL_5.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        // console.log( JSON.stringify( invoice , null , 1 ) );
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('28-B9U1-109754');
          expect(invoice.date).eql(new Date(2019, 1, 1));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('IDEABLE SOLUTIONS S.L.');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('B95660338');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('CL VIRTUD N 1 BAJO');
              expect(invoice.receiver.address.postal_code).eq('48901');
              expect(invoice.receiver.address.city).eq('BARAKALDO');
            }
            expect(invoice.taxes).to.be.an('array').not.to.empty;
            if (invoice.taxes) {
              expect(invoice.taxes[0].tax).eq(TaxType.IVA);
              expect(invoice.taxes[0].base).eq(0.6256);
              expect(invoice.taxes[0].percentage).eq(21.0);
              expect(invoice.taxes[0].quota).eq(0.1314);
            }
            const expected = [
              {
                price: 3.3431,
                quantity: 1,
                base: 3.3431,
                discount: 0,
                description: 'Llamadas (18 Dic. 18 a 17 Ene. 19)',
              },
              {
                price: -2.7174,
                quantity: 1,
                base: -2.7174,
                discount: 0,
                description: 'Descuentos por plan',
              },
            ];
            //expect(invoice.details).to.be.an('array').not.to.empty;
            if (invoice.details) {
              for (let i = 0; i < invoice.details.length; i++) {
                expect(invoice.details[i].description).eq(expected[i].description);
                expect(invoice.details[i].price).eq(expected[i].price);
                expect(invoice.details[i].quantity).eq(expected[i].quantity);
                expect(invoice.details[i].base).eq(expected[i].base);
              }
            }
            expect(invoice.total).eq(0.76);
          }
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });

  it('PARSE MOVISTAR MOVIL INVOICE [ MOVISTAR MOVIL_6.pdf ]', done => {
    const filename = 'test/resources/MOVISTAR_MOVIL_6.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        // console.log( JSON.stringify( invoice , null , 1 ) );
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('28-B9M0-392876');
          expect(invoice.date).eql(new Date(2019, 1, 1));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('CORINA ELENA LANDA DAVILA');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('49186054W');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('CL BERTRAN N 89 SO 1');
              expect(invoice.receiver.address.postal_code).eq('08023');
              expect(invoice.receiver.address.city).eq('BARCELONA');
            }
            expect(invoice.taxes).to.be.an('array').not.to.empty;
            if (invoice.taxes) {
              expect(invoice.taxes[0].tax).eq(TaxType.IVA);
              expect(invoice.taxes[0].base).eq(2.0621);
              expect(invoice.taxes[0].percentage).eq(21.0);
              expect(invoice.taxes[0].quota).eq(0.433);
            }
            const expected = [
              {
                price: 2.0621,
                quantity: 1,
                base: 2.0621,
                discount: 0,
                description: 'Llamadas (18 Dic. 18 a 17 Ene. 19)',
              },
            ];
            //expect(invoice.details).to.be.an('array').not.to.empty;
            if (invoice.details) {
              for (let i = 0; i < invoice.details.length; i++) {
                expect(invoice.details[i].description).eq(expected[i].description);
                expect(invoice.details[i].price).eq(expected[i].price);
                expect(invoice.details[i].quantity).eq(expected[i].quantity);
                expect(invoice.details[i].base).eq(expected[i].base);
              }
            }
            expect(invoice.total).eq(2.5);
          }
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });
});

// ============== EUSKALTEL TEST  ================ //
describe('TEDI PDF PARSER TEST [ EUSKALTEL ]', () => {
  it('PARSE EUSKALTEL INVOICE [ EUSKALTEL_5.pdf ]', done => {
    const filename = 'test/resources/Euskaltel_5.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('A48766695');
          expect(invoice.date).eql(new Date(2019, 4, 22));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('AITOR ZUBIZARRETA UNCETA');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('15397670K');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('CALLE URKI KURUTZEKUA 004 05 B');
              expect(invoice.receiver.address.postal_code).eq('20600');
              expect(invoice.receiver.address.city).eq('EIBAR');
            }
            expect(invoice.taxes).to.be.an('array').not.to.empty;
            if (invoice.taxes) {
              expect(invoice.taxes[0].tax).eq(TaxType.IVA);
              expect(invoice.taxes[0].base).eq(99.1653);
              expect(invoice.taxes[0].percentage).eq(21.0);
              expect(invoice.taxes[0].quota).eq(20.8247);
            }
            const expected = [
              {
                price: 18.2562,
                quantity: 1,
                base: 18.2562,
                discount: 0,
                description: 'Finkoa/Fijo',
              },
              {
                price: 25.5372,
                quantity: 1,
                base: 25.5372,
                discount: 0,
                description: 'Internet/Internet',
              },
              {
                price: 0.0,
                quantity: 1,
                base: 0.0,
                discount: 0,
                description: 'Televisión/Televisión',
              },
              {
                price: 55.3719,
                quantity: 1,
                base: 55.3719,
                discount: 0,
                description: 'Movil/Móvil',
              },
            ];
            //expect(invoice.details).to.be.an('array').not.to.empty;
            if (invoice.details) {
              for (let i = 0; i < invoice.details.length; i++) {
                expect(invoice.details[i].description).eq(expected[i].description);
                expect(invoice.details[i].price).eq(expected[i].price);
                expect(invoice.details[i].quantity).eq(expected[i].quantity);
                expect(invoice.details[i].base).eq(expected[i].base);
              }
            }
            expect(invoice.total).eq(119.99);
          }
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });

  it('PARSE EUSKALTEL INVOICE [ EUSKALTEL_6.pdf ]', done => {
    const filename = 'test/resources/Euskaltel_6.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('A48766695');
          expect(invoice.date).eql(new Date(2019, 4, 23));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('SANTANA 27, S.L.');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('B95308094');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('CALLE TELLERIA 027 PB');
              expect(invoice.receiver.address.postal_code).eq('48004');
              expect(invoice.receiver.address.city).eq('BILBAO');
            }
            expect(invoice.taxes).to.be.an('array').not.to.empty;
            if (invoice.taxes) {
              expect(invoice.taxes[0].tax).eq(TaxType.IVA);
              expect(invoice.taxes[0].base).eq(129.8359);
              expect(invoice.taxes[0].percentage).eq(21.0);
              expect(invoice.taxes[0].quota).eq(27.2655);
            }
            const expected = [
              {
                price: 2.9766,
                quantity: 1,
                base: 76.2508,
                discount: 76.2508,
                description: 'Finkoa/Fijo',
              },
              {
                price: 30.4959,
                quantity: 1,
                base: 33.0459,
                discount: 2.55,
                description: 'Internet/Internet',
              },
              {
                price: 37.1901,
                quantity: 1,
                base: 37.1901,
                discount: 0,
                description: 'Movil/Móvil',
              },
              {
                price: 59.1733,
                quantity: 1,
                base: 64.0,
                discount: 4.8267,
                description: 'Otros/Otros',
              },
            ];
          //  expect(invoice.details).to.be.an('array').not.to.empty;
            if (invoice.details) {
              for (let i = 0; i < invoice.details.length; i++) {
                expect(invoice.details[i].description).eq(expected[i].description);
                expect(invoice.details[i].price).eq(expected[i].price);
                expect(invoice.details[i].quantity).eq(expected[i].quantity);
                expect(invoice.details[i].base).eq(expected[i].base);
              }
            }
            expect(invoice.total).eq(157.1);
          }
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });

  it('PARSE EUSKALTEL INVOICE [ EUSKALTEL_8.pdf ]', done => {
    const filename = 'test/resources/Euskaltel_8.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('A48766695');
          expect(invoice.date).eql(new Date(2019, 0, 22));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('ERGOSFERA TRAINING AND MANAGERS S.L');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('B95792495');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('PLAZA MOLINAR 006 PB');
              expect(invoice.receiver.address.postal_code).eq('48192');
              expect(invoice.receiver.address.city).eq('GORDEXOLA');
            }
            expect(invoice.taxes).to.be.an('array').not.to.empty;
            if (invoice.taxes) {
              expect(invoice.taxes[0].tax).eq(TaxType.IVA);
              expect(invoice.taxes[0].base).eq(38.5035);
              expect(invoice.taxes[0].percentage).eq(21.0);
              expect(invoice.taxes[0].quota).eq(8.0857);
            }
            const expected = [
              {
                price: 10.0,
                quantity: 1,
                base: 20.7355,
                discount: 10.7355,
                description: 'Finkoa/Fijo',
              },
              {
                price: 25.4545,
                quantity: 1,
                base: 25.4545,
                discount: 0,
                description: 'Internet/Internet',
              },
              {
                price: 3.049,
                quantity: 1,
                base: 15.7025,
                discount: 15.7025,
                description: 'Movil/Móvil',
              },
              {
                price: 0.0,
                quantity: 1,
                base: 0.0,
                discount: 0,
                description: 'Otros/Otros',
              },
            ];
            //expect(invoice.details).to.be.an('array').not.to.empty;
            if (invoice.details) {
              for (let i = 0; i < invoice.details.length; i++) {
                expect(invoice.details[i].description).eq(expected[i].description);
                expect(invoice.details[i].price).eq(expected[i].price);
                expect(invoice.details[i].quantity).eq(expected[i].quantity);
                expect(invoice.details[i].base).eq(expected[i].base);
              }
            }
            expect(invoice.total).eq(46.59);
          }
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });
});

// ============== VODAFONE  TEST  ================ //
// describe('TEDI PDF PARSER TEST [ VODAFONE MOVIL ]', () => {
//   it('PARSE VODAFONE INVOICE [ VODAFONE.pdf ]', done => {
//     const filename = 'test/resources/Vodafone_1.pdf'
//     TediPdfParser.extractFromFile(filename).subscribe(
//       invoice => {
//         console.log(JSON.stringify(invoice, null, 1));
//         expect(invoice).not.to.be.null;
//         expect(invoice).not.to.be.undefined;
//         if (invoice) {
//           expect(invoice.status).eq(InvoiceStatus.inbox);
//           expect(invoice.reference).eq('B-87539284');
//           expect(invoice.date).eql(new Date(2019, 6, 1));
//           expect(invoice.receiver).not.to.be.undefined;
//           if (invoice.receiver) {
//             expect(invoice.receiver.name).eq('AON SOLUTIONS SL');
//             expect(invoice.receiver.document_country).eq('ES');
//             expect(invoice.receiver.document).eq('B01487271');
//             expect(invoice.receiver.address).not.to.be.undefined;
//             if (invoice.receiver.address) {
//               expect(invoice.receiver.address.address).eq('CL DUQUE DE WELLINGTON 52  BA 1');
//               expect(invoice.receiver.address.postal_code).eq('01010');
//               expect(invoice.receiver.address.city).eq('VITORIA-GASTEIZ')
//             }
//             expect(invoice.finances).not.to.be.undefined;
//             expect(invoice.finances).to.be.an('array').not.to.empty;
//             if (invoice.finances) {
//               expect(invoice.finances[0].due_date).eql(new Date(2019, 6, 12, 0, 0));
//               expect(invoice.finances[0].amount).eq(88.32);
//             }
//             expect(invoice.taxes).to.be.an('array').not.to.empty;
//             if (invoice.taxes) {
//               expect(invoice.taxes[0].tax).eq(TaxType.IVA);
//               expect(invoice.taxes[0].base).eq(72.99);
//               expect(invoice.taxes[0].percentage).eq(21.0);
//               expect(invoice.taxes[0].quota).eq(15.33);
//             }
//             const expected = [
//               {
//                 price: 18.4400,
//                 quantity: 1,
//                 base: 18.4400,
//                 discount: 0,
//                 description: 'Servicios',
//               },
//               {
//                 price: 54.5500,
//                 quantity: 1,
//                 base: 54.5500,
//                 discount: 0,
//                 description: 'Cuotas a nivel de cuenta Total',
//               },
//             ];
//             expect(invoice.details).to.be.an('array').not.to.empty;
//             if (invoice.details) {
//               for (let i = 0; i < invoice.details.length; i++) {
//                 expect(invoice.details[i].description).eq(expected[i].description);
//                 expect(invoice.details[i].price).eq(expected[i].price);
//                 expect(invoice.details[i].quantity).eq(expected[i].quantity);
//                 expect(invoice.details[i].base).eq(expected[i].base);
//               }
//             }
//             expect(invoice.total).eq(88.32);
//           }
//         }
//       },
//       error => {
//         expect(error).not.to.be.null;
//         expect(error.code).eq('TediUnknownInvoiceFormat');
//         done();
//       },
//       () => {
//         done();
//       },
//     );
//   });
// });

// ============== SECURITAS  TEST  ================ //
describe('TEDI PDF PARSER TEST [ SECURITAS  ]', () => {
  it('PARSE SECURITAS INVOICE [ SECURITAS.pdf ]', done => {
    const filename = 'test/resources/Securitas_4.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('A26106013');
          // expect(invoice.date).eql(new Date(2019, 0, 1));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('JOSE ANTONIO GOMEZ MARTINEZ');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('25376850F');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('CALLE PONIENTE 8 PUERTA 37');
              expect(invoice.receiver.address.postal_code).eq('46017');
              expect(invoice.receiver.address.city).eq('VALENCIA');
            }
            expect(invoice.taxes).to.be.an('array').not.to.empty;
            if (invoice.taxes) {
              expect(invoice.taxes[0].tax).eq(TaxType.IVA);
              expect(invoice.taxes[0].base).eq(38.34);
              expect(invoice.taxes[0].percentage).eq(21.0);
              expect(invoice.taxes[0].quota).eq(8.05);
            }
            const expected = [
              {
                price: 43.18,
                quantity: 1,
                base: 43.18,
                discount: 0,
                description: 'ALARMA ANTI-INTRUSION',
              },
              {
                price: 1.61,
                quantity: 1,
                base: 1.61,
                discount: 0,
                description: 'DISPOSITIVOS ADICIONALES',
              },
              {
                price: 1.0,
                quantity: 1,
                base: 1.0,
                discount: 0,
                description: 'CUOTA MODULO',
              },
              {
                price: 1.0,
                quantity: 1,
                base: 1.0,
                discount: 0,
                description: 'SERVICIOS CONFORT',
              },
              {
                price: -8.45,
                quantity: 1,
                base: -8.45,
                discount: 0,
                description: 'OFERTA CUOTA FIDELIZACION',
              },
            ];
          //  expect(invoice.details).to.be.an('array').not.to.empty;
            if (invoice.details) {
              for (let i = 0; i < invoice.details.length; i++) {
                expect(invoice.details[i].description).eq(expected[i].description);
                expect(invoice.details[i].price).eq(expected[i].price);
                expect(invoice.details[i].quantity).eq(expected[i].quantity);
                expect(invoice.details[i].base).eq(expected[i].base);
              }
            }
            expect(invoice.total).eq(46.39);
          }
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });

  it('PARSE SECURITAS INVOICE [ SECURITAS.pdf ]', done => {
    const filename = 'test/resources/Securitas_3.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('A26106013');
          // expect(invoice.date).eql(new Date(2019, 0, 1));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('KALDEVI INGENIERIA GERIATRICA S.L');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('B96297403');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('CALLE ACEQUIA DE QUART 7 POLIG,LA PASCUALETA');
              expect(invoice.receiver.address.postal_code).eq('46200');
              expect(invoice.receiver.address.city).eq('PAIPORTA');
            }
            expect(invoice.taxes).to.be.an('array').not.to.empty;
            if (invoice.taxes) {
              expect(invoice.taxes[0].tax).eq(TaxType.IVA);
              expect(invoice.taxes[0].base).eq(41.93);
              expect(invoice.taxes[0].percentage).eq(21.0);
              expect(invoice.taxes[0].quota).eq(8.81);
            }
            const expected = [
              {
                price: 38.93,
                quantity: 1,
                base: 38.93,
                discount: 0,
                description: 'ALARMA ANTI-INTRUSION Y ANTI-INHIBIDORES',
              },
              {
                price: 3.0,
                quantity: 1,
                base: 3.0,
                discount: 0,
                description: 'SERVICIO DE CORTE DE CORRIENTE',
              },
            ];
            //expect(invoice.details).to.be.an('array').not.to.empty;
            if (invoice.details) {
              for (let i = 0; i < invoice.details.length; i++) {
                expect(invoice.details[i].description).eq(expected[i].description);
                expect(invoice.details[i].price).eq(expected[i].price);
                expect(invoice.details[i].quantity).eq(expected[i].quantity);
                expect(invoice.details[i].base).eq(expected[i].base);
              }
            }
            expect(invoice.total).eq(50.74);
          }
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });
});

// ============== ENDESA  TEST  ================ //
describe('TEDI PDF PARSER TEST [ ENDESA MOVIL ]', () => {
  it('PARSE ENDESA INVOICE [ ENDESA.pdf ]', done => {
    const filename = 'test/resources/Endesa_1.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('B82846825');
          expect(invoice.date).eql(new Date(2018, 11, 21));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('GARCIA-LOMAS&AS SL');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('B23741903');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('PARROCO JACINTO MUELA 8 BAJO');
              expect(invoice.receiver.address.postal_code).eq('23200');
              expect(invoice.receiver.address.city).eq('LA CAROLINA');
            }
            expect(invoice.finances).not.to.be.undefined;
            expect(invoice.finances).to.be.an('array').not.to.empty;
            if (invoice.finances) {
              expect(invoice.finances[0].due_date).eql(new Date(2018, 11, 28, 0, 0));
              expect(invoice.finances[0].amount).eq(49.44);
            }
            expect(invoice.taxes).to.be.an('array').not.to.empty;
            if (invoice.taxes) {
              expect(invoice.taxes[0].tax).eq(TaxType.IVA);
              expect(invoice.taxes[0].base).eq(40.86);
              expect(invoice.taxes[0].percentage).eq(21.0);
              expect(invoice.taxes[0].quota).eq(8.58);
            }
            const expected = [
              {
                price: 27.78,
                quantity: 1,
                base: 27.78,
                discount: 0,
                description: 'Por potencia contratada ',
              },
              {
                price: 10.28,
                quantity: 1,
                base: 10.28,
                discount: 0,
                description: 'Por energía consumida ',
              },
              {
                price: 1.95,
                quantity: 1,
                base: 1.95,
                discount: 0,
                description: 'Impuesto electricidad ',
              },
              {
                price: 0.85,
                quantity: 1,
                base: 0.85,
                discount: 0,
                description: 'Alquiler equipos de medida y control ',
              },
            ];
            //expect(invoice.details).to.be.an('array').not.to.empty;
            if (invoice.details) {
              for (let i = 0; i < invoice.details.length; i++) {
                expect(invoice.details[i].description).eq(expected[i].description);
                expect(invoice.details[i].price).eq(expected[i].price);
                expect(invoice.details[i].quantity).eq(expected[i].quantity);
                expect(invoice.details[i].base).eq(expected[i].base);
              }
            }
            expect(invoice.total).eq(49.44);
          }
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });

  it('PARSE ENDESA INVOICE [ ENDESA.pdf ]', done => {
    const filename = 'test/resources/Endesa_4.pdf';
    TediPdfParser.extractFromFile(filename).subscribe(
      invoice => {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
          expect(invoice.status).eq(InvoiceStatus.inbox);
          expect(invoice.reference).eq('A81948077');
          expect(invoice.date).eql(new Date(2019, 0, 18));
          expect(invoice.receiver).not.to.be.undefined;
          if (invoice.receiver) {
            expect(invoice.receiver.name).eq('ARUNDEL S.L');
            expect(invoice.receiver.document_country).eq('ES');
            expect(invoice.receiver.document).eq('B87951026');
            expect(invoice.receiver.address).not.to.be.undefined;
            if (invoice.receiver.address) {
              expect(invoice.receiver.address.address).eq('TRAMONTANA 21-A 3 HUMERA-SOMOSAGUAS-PRADO DEL RE');
              expect(invoice.receiver.address.postal_code).eq('28223');
              // expect( invoice.receiver.address.city).eq( 'LA CAROLINA' )
            }
            expect(invoice.finances).not.to.be.undefined;
            expect(invoice.finances).to.be.an('array').not.to.empty;
            if (invoice.finances) {
              expect(invoice.finances[0].due_date).eql(new Date(2019, 0, 25, 0, 0));
              expect(invoice.finances[0].amount).eq(313.89);
            }
            expect(invoice.taxes).to.be.an('array').not.to.empty;
            if (invoice.taxes) {
              expect(invoice.taxes[0].tax).eq(TaxType.IVA);
              expect(invoice.taxes[0].base).eq(259.41);
              expect(invoice.taxes[0].percentage).eq(21.0);
              expect(invoice.taxes[0].quota).eq(54.48);
            }
            const expected = [
              {
                price: 20.94,
                quantity: 1,
                base: 20.94,
                discount: 0,
                description: 'Fijo',
              },
              {
                price: 241.48,
                quantity: 1,
                base: 241.48,
                discount: 0,
                description: 'Variable',
              },
              {
                price: 13.19,
                quantity: 1,
                base: 13.19,
                discount: 0,
                description: 'Descuentos',
              },
              {
                price: 0.57,
                quantity: 1,
                base: 0.57,
                discount: 0,
                description: 'Otros',
              },
              {
                price: 10.75,
                quantity: 1,
                base: 10.75,
                discount: 0,
                description: 'Impuestos',
              },
            ];
            //expect(invoice.details).to.be.an('array').not.to.empty;
            if (invoice.details) {
              for (let i = 0; i < invoice.details.length; i++) {
                expect(invoice.details[i].description).eq(expected[i].description);
                expect(invoice.details[i].price).eq(expected[i].price);
                expect(invoice.details[i].quantity).eq(expected[i].quantity);
                expect(invoice.details[i].base).eq(expected[i].base);
              }
            }
            expect(invoice.total).eq(313.89);
          }
        }
      },
      error => {
        expect(error).not.to.be.null;
        expect(error.code).eq('TediUnknownInvoiceFormat');
        done();
      },
      () => {
        done();
      },
    );
  });
});
describe('Invoice insight', () => {
  describe('PDF Parser', () => {
    // const TELEFONICA_INVOICES = {
    //   TA6550172734: 112.66,
    //   TA6640137838: 207.55,
    //   TA62E0079291: 207.54,
    //   TA6640046438: 227.21,
    //   TA6470004324: 97.56,
    //   TA6470004325: 83.17,
    //   TA6470010207: 63.4,
    //   TA6470010208: 60.93,
    //   TA6470010206: 46.88,
    //   TA6470004323: 96.65,
    //   AA5ZX0594898: 32.3,
    //   TA66W0042967: 65.27,
    //   TA66W0042968: 32.3,
    //   TA62I0043246: 62.52,
    //   TA66C0049989: 73.4,
    //   TA62I0043247: 32.3,
    //   TA65J0011223: 97.1,
    //   TA66B0103260: 79.82,
    //   TA6470018316: 102.82,
    //   TA62F0105565: 77.5,
    //   TA6470018317: 97.53,
    //   TA6470018318: 94.03,
    //   XXXXXXXXXXXX: 85.37,
    //   TA66B0107028: 76.35,
    //   TA6640078824: 207.25,
    //   TA65J0293057: 1001.05,
    //   TA62B0335336: 95.0,
    //   TA6640086679: 75.02,
    //   TA6640083384: 320.15,
    //   AA62E0311811: 92.35,
    //   TA6290173751: 60.01,
    //   TA6330011590: 84.96,
    //   TA6290173750: 20.65,
    //   TA6290173749: 106.69,
    //   TA6290173748: 112.66,
    //   TA6290031131: 3.39,
    //   TA62E0139591: 281.39,
    //   TA62E0046463: 226.9,
    //   TA5ZJ0174433: 57.4,
    //   TA66B0051335: 72.72,
    //   TA6460010477: 67.43,
    //   TA6460010478: 44.34,
    //   TA6240010506: 65.4,
    //   TA6460010479: 60.93,
    //   TA6240010507: 32.3,
    //   TA62B0027871: 95.29,
    //   TA62F0109412: 73.4,
    //   TA62B0288861: 1000.06,
    //   TA6460018850: 113.99,
    //   TA6460018851: 96.64,
    //   TA6460018852: 93.06,
    //   TA6460004494: 115.35,
    //   TA6460004496: 86.84,
    //   TA6460004495: 96.27,
    //   TA62E0087086: 75.02,
    //   NE62F0000115: -14.56,
    //   TA5ZJ0031273: 3.39,
    //   TA62E0083768: 322.45,
    //   TA6240004506: 97.76,
    //   TA62E0171406: 148.83,
    // };
    // it('Telefonica', done => {
    //   fs.createReadStream('test/resources/telefonica.zip')
    //     .pipe(unzip.Extract({ path: 'test/pdfs' }))
    //     .on('close', () =>
    //       fs.readdir('./test/pdfs/telefonica', (err, files: string[]) => {
    //         if (err) {
    //           return done('Unable to scan directory: ' + err);
    //         }
    //         from(files)
    //           .pipe(map(file => `./test/pdfs/telefonica/${file}`))
    //           .pipe(
    //             map(file => {
    //               return {
    //                 fileName: file,
    //                 chunk: fileType(readChunk.sync(file, 0, fileType.minimumBytes)),
    //               };
    //             }),
    //           )
    //           .pipe(filter(fileInfo => fileInfo.chunk !== undefined))
    //           .pipe(filter(fileInfo => fileInfo.chunk !== null))
    //           .pipe(filter(fileInfo => 'application/pdf' === fileInfo.chunk!.mime))
    //           .pipe(mergeMap(fileInfo => TediPdfParser.extractFromFile(fileInfo.fileName)))
    //           .subscribe(
    //             invoice => {
    //               // if (invoice.reference) {
    //               //   const expectedTotal = TELEFONICA_INVOICES[invoice.reference];
    //               //   if (expectedTotal !== undefined) {
    //               //     // tslint:disable-next-line: no-console
    //               //     console.log(invoice.reference + ' ----- ' + invoice.total);
    //               //     expect(invoice.total, `TOTAL OF ${invoice.reference} INVOICE`).eq(expectedTotal);
    //               //   }
    //               // }
    //             },
    //             error => done(error),
    //             () => done(),
    //           );
    //       }),
    //     );
    // });
    // it('Iberdrola', done => {
    //   fs.createReadStream('test/resources/iberdrola.zip')
    //     .pipe(unzip.Extract({ path: 'test/pdfs' }))
    //     .on('close', () =>
    //       fs.readdir('./test/pdfs/iberdrola', (err, files: string[]) => {
    //         if (err) {
    //           return done('Unable to scan directory: ' + err);
    //         }
    //         from(files)
    //           .pipe(map(file => `./test/pdfs/iberdrola/${file}`))
    //           .pipe(
    //             map(file => {
    //               return {
    //                 fileName: file,
    //                 chunk: fileType(readChunk.sync(file, 0, fileType.minimumBytes)),
    //               };
    //             }),
    //           )
    //           .pipe(filter(fileInfo => fileInfo.chunk !== undefined))
    //           .pipe(filter(fileInfo => fileInfo.chunk !== null))
    //           .pipe(filter(fileInfo => 'application/pdf' === fileInfo.chunk!.mime))
    //           .pipe(mergeMap(fileInfo => TediPdfParser.extractFromFile(fileInfo.fileName)))
    //           .subscribe(
    //             invoice => {
    //               // if (invoice.reference) {
    //               //   const expectedTotal = TELEFONICA_INVOICES[invoice.reference];
    //               //   if (expectedTotal !== undefined) {
    //               //     // tslint:disable-next-line: no-console
    //               //     console.log(invoice.reference + ' ----- ' + invoice.total);
    //               //     expect(invoice.total, `TOTAL OF ${invoice.reference} INVOICE`).eq(expectedTotal);
    //               //   }
    //               // }
    //             },
    //             error => done(error),
    //             () => done(),
    //           );
    //         // Promise.all(
    //         //   files
    //         //   .map( file => `./test/pdfs/iberdrola/${file}`)
    //         //   .filter( file => "application/pdf" == fileType(readChunk.sync(file, 0, fileType.minimumBytes)).mime )
    //         //   .map( file =>  {
    //         //     // Do whatever you want to do with the file
    //         //     return new Promise((resolve, reject) =>
    //         //       pdfParser.extractFromFile(
    //         //         file
    //         //         , (err, invoice) => {
    //         //           if ( err ) {
    //         //             reject(`${file}: ${err}`);
    //         //           }
    //         //           else {
    //         //             resolve(invoice);
    //         //           }
    //         //         }
    //         //       )
    //         //     );
    //         // }))
    //         // .then(invoices => done() )
    //         // .catch( err => done(err) )
    //         // ;
    //       }),
    //     );
    // });
  });
});
