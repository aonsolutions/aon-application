import { expect } from 'chai';
import { Invoice, InvoiceStatus, TaxType } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class MovistarMovilTest {
  public static checkInvoice(invoice: Invoice, movistarMoInvoice: Invoice): void {
    expect(invoice).not.to.be.null;
    expect(invoice).not.to.be.undefined;
    if (invoice) {
      expect(invoice.status).eq(movistarMoInvoice.status);
      expect(invoice.date).eql(movistarMoInvoice.date);
      expect(invoice.reference).eq(movistarMoInvoice.reference);

      expect(invoice.receiver).not.to.be.undefined;
      if (invoice.receiver && movistarMoInvoice.receiver) {
        expect(invoice.receiver.name).eq(movistarMoInvoice.receiver.name);
        expect(invoice.receiver.document_country).eq(movistarMoInvoice.receiver.document_country);
        expect(invoice.receiver.document).eq(movistarMoInvoice.receiver.document);
        expect(invoice.receiver.address).not.to.be.undefined;
        if (invoice.receiver.address && movistarMoInvoice.receiver.address) {
          expect(invoice.receiver.address.address).eq(movistarMoInvoice.receiver.address.address);
          expect(invoice.receiver.address.postal_code).eq(movistarMoInvoice.receiver.address.postal_code);
          expect(invoice.receiver.address.city).eq(movistarMoInvoice.receiver.address.city);
        }
      }
      //expect(invoice.details).to.be.an('array').not.to.empty;
      if (invoice.details && movistarMoInvoice.details) {
        for (let i = 0; i < invoice.details.length; i++) {
          expect(invoice.details[i].description).eq(movistarMoInvoice.details[i].description);
          expect(invoice.details[i].quantity).eq(movistarMoInvoice.details[i].quantity);
          expect(invoice.details[i].base).eq(movistarMoInvoice.details[i].base);
          expect(invoice.details[i].price).eq(movistarMoInvoice.details[i].price);
        }
      }

      expect(invoice.taxes).to.be.an('array').not.to.empty;
      if (invoice.taxes && movistarMoInvoice.taxes) {
        for (let i = 0; i < invoice.taxes.length; i++) {
          expect(invoice.taxes[i].tax).eq(movistarMoInvoice.taxes[i].tax);
          expect(invoice.taxes[i].quota).eq(movistarMoInvoice.taxes[i].quota);
          expect(invoice.taxes[i].base).eq(movistarMoInvoice.taxes[i].base);
          expect(invoice.taxes[i].percentage).eq(movistarMoInvoice.taxes[i].percentage);
        }
      }
      expect(invoice.total).eq(movistarMoInvoice.total);
      expect(invoice.finances).not.to.be.undefined;
      expect(invoice.finances).to.be.an('array').not.to.empty;
      if (invoice.finances && movistarMoInvoice.finances) {
        for (let i = 0; i < invoice.finances.length; i++) {
          expect(invoice.finances[i].due_date).eql(movistarMoInvoice.finances[i].due_date);
          expect(invoice.finances[i].amount).eq(movistarMoInvoice.finances[i].amount);
        }
      }
    }
  }
  public static movistarMovilTest(): void {
    const movistar1: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 1, 1, 0, 0),
      reference: '28-B9M0-012276',
      total: 198.33,
      receiver: {
        name: 'UDAPA, S.COOP.',
        document_country: 'ES',
        document: 'F01131978',
        address: {
          address: 'CL ARRIURDINA N 6 P.I. JUNDIZ',
          postal_code: '01015',
          city: 'VITORIA-GASTEIZ',
        },
      },
      details: [
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
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 34.4202,
          base: 163.9055,
          percentage: 21.0,
        },
      ],
      finances: [
        {
          due_date: new Date(2019, 1, 1, 0, 0),
          amount: 198.33,
        },
      ],
    };
    const movistar2: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 1, 1, 0, 0),
      reference: '28-B9M0-006411',
      total: 312.8,
      receiver: {
        name: 'PTE SAN MIGUEL SL.',
        document_country: 'ES',
        document: 'B86501004',
        address: {
          address: 'PSO CASTELLANA N 114 ESC 1 PLANTA 2',
          postal_code: '28046',
          city: 'MADRID',
        },
      },
      details: [
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
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 54.2882,
          base: 258.5152,
          percentage: 21.0,
        },
      ],
    };
    const movistar3: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 1, 1, 0, 0),
      reference: '28-B9U1-025414',
      total: 107.38,
      receiver: {
        name: 'LOCAL PASSWORD S.L.',
        document_country: 'ES',
        document: 'B84129576',
        address: {
          address: 'CL ARLABAN N 7 OFICINA 69',
          postal_code: '28014',
          city: 'MADRID',
        },
      },
      details: [
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
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 15.1656,
          base: 72.2172,
          percentage: 21.0,
        },
        {
          tax: TaxType.IVA,
          quota: 0,
          base: 20,
          percentage: 0,
        },
      ],
    };
    const movistar4: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 1, 1, 0, 0),
      reference: '28-B9U1-084638',
      total: 8.81,
      receiver: {
        name: 'INNOVACION COLABORATIVA S.L.',
        document_country: 'ES',
        document: 'B87059358',
        address: {
          address: 'CL ALAMEDA N 22 BAJO',
          postal_code: '28014',
          city: 'MADRID',
        },
      },
      details: [
        {
          price: 7.28,
          quantity: 1,
          base: 7.28,
          discount: 0,
          description: 'Llamadas (18 Dic. 18 a 17 Ene. 19)',
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 1.5288,
          base: 7.28,
          percentage: 21.0,
        },
      ],
    };
    const movistar5: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 1, 1, 0, 0),
      reference: '28-B9U1-109754',
      total: 0.76,
      receiver: {
        name: 'IDEABLE SOLUTIONS S.L.',
        document_country: 'ES',
        document: 'B95660338',
        address: {
          address: 'CL VIRTUD N 1 BAJO',
          postal_code: '48901',
          city: 'BARAKALDO',
        },
      },
      details: [
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
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 0.1314,
          base: 0.6256,
          percentage: 21.0,
        },
      ],
    };
    const movistar6: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 1, 1, 0, 0),
      reference: '28-B9M0-392876',
      total: 2.5,
      receiver: {
        name: 'CORINA ELENA LANDA DAVILA',
        document_country: 'ES',
        document: '49186054W',
        address: {
          address: 'CL BERTRAN N 89 SO 1',
          postal_code: '08023',
          city: 'BARCELONA',
        },
      },
      details: [
        {
          price: 2.0621,
          quantity: 1,
          base: 2.0621,
          discount: 0,
          description: 'Llamadas (18 Dic. 18 a 17 Ene. 19)',
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 0.433,
          base: 2.0621,
          percentage: 21.0,
        },
      ],
    };

    describe('TEDI PDF PARSER TEST [ MOVISTAR MOVIL ]', () => {
      it('PARSE MOVISTAR MOVIL INVOICE [ MOVISTAR MOVIL_1.pdf ]', done => {
        const filename = 'test/resources/MOVISTAR_MOVIL_1.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, movistar1);
          },
          error => {
            done(error);
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
            this.checkInvoice(invoice, movistar2);
          },
          error => {
            done(error);
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
            this.checkInvoice(invoice, movistar3);
          },
          error => {
            done(error);
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
            this.checkInvoice(invoice, movistar4);
          },
          error => {
            done(error);
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
            this.checkInvoice(invoice, movistar5);
          },
          error => {
            done(error);
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
            this.checkInvoice(invoice, movistar6);
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
  }
}
