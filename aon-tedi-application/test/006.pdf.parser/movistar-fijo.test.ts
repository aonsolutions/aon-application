import { expect } from 'chai';
import { Invoice, InvoiceStatus, TaxType } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class MovistarFijoTest {
  public static checkInvoice(invoice: Invoice, movistarFijoInvoice: Invoice): void {
    expect(invoice).not.to.be.null;
    expect(invoice).not.to.be.undefined;
    if (invoice) {
      expect(invoice.status).eq(movistarFijoInvoice.status);
      expect(invoice.date).eql(movistarFijoInvoice.date);
      expect(invoice.reference).eq(movistarFijoInvoice.reference);

      expect(invoice.receiver).not.to.be.undefined;
      if (invoice.receiver && movistarFijoInvoice.receiver) {
        expect(invoice.receiver.name).eq(movistarFijoInvoice.receiver.name);
        expect(invoice.receiver.document_country).eq(movistarFijoInvoice.receiver.document_country);
        expect(invoice.receiver.document).eq(movistarFijoInvoice.receiver.document);
        expect(invoice.receiver.address).not.to.be.undefined;
        if (invoice.receiver.address && movistarFijoInvoice.receiver.address) {
          expect(invoice.receiver.address.address).eq(movistarFijoInvoice.receiver.address.address);
          expect(invoice.receiver.address.postal_code).eq(movistarFijoInvoice.receiver.address.postal_code);
          expect(invoice.receiver.address.city).eq(movistarFijoInvoice.receiver.address.city);
        }
      }
      //expect(invoice.details).to.be.an('array').not.to.empty;
      if (invoice.details && movistarFijoInvoice.details) {
        for (let i = 0; i < invoice.details.length; i++) {
          expect(invoice.details[i].description).eq(movistarFijoInvoice.details[i].description);
          expect(invoice.details[i].quantity).eq(movistarFijoInvoice.details[i].quantity);
          expect(invoice.details[i].base).eq(movistarFijoInvoice.details[i].base);
          expect(invoice.details[i].price).eq(movistarFijoInvoice.details[i].price);
        }
      }

      expect(invoice.taxes).to.be.an('array').not.to.empty;
      if (invoice.taxes && movistarFijoInvoice.taxes) {
        for (let i = 0; i < invoice.taxes.length; i++) {
          expect(invoice.taxes[i].tax).eq(movistarFijoInvoice.taxes[i].tax);
          expect(invoice.taxes[i].quota).eq(movistarFijoInvoice.taxes[i].quota);
          expect(invoice.taxes[i].base).eq(movistarFijoInvoice.taxes[i].base);
          expect(invoice.taxes[i].percentage).eq(movistarFijoInvoice.taxes[i].percentage);
        }
      }
      expect(invoice.total).eq(movistarFijoInvoice.total);
      expect(invoice.finances).not.to.be.undefined;
      expect(invoice.finances).to.be.an('array').not.to.empty;
      if (invoice.finances && movistarFijoInvoice.finances) {
        for (let i = 0; i < invoice.finances.length; i++) {
          expect(invoice.finances[i].due_date).eql(movistarFijoInvoice.finances[i].due_date);
          expect(invoice.finances[i].amount).eq(movistarFijoInvoice.finances[i].amount);
        }
      }
    }
  }

  public static movistarFijoTest(): void {
    const movFijo1: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2018, 10, 19, 0, 0),
      reference: 'TA5ZH0179306',
      total: 20.66,
      receiver: {
        name: 'UDAPA SDAD.COOP.',
        document_country: 'ES',
        document: '00F01131978',
        address: {
          address: 'Calle Paduleta Kalea, 1 Bajo',
          postal_code: '01015',
          city: 'Jundiz',
        },
      },

      details: [
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
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 3.586,
          base: 17.076,
          percentage: 21.0,
        },
      ],
      finances: [
        {
          due_date: new Date(2018, 10, 19, 0, 0),
          amount: 20.66,
        },
      ],
    };
    const movFijo2: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 1, 10, 0, 0),
      reference: 'XXXXXXXXXXXX',
      total: 82.43,
      receiver: {
        name: 'Mondar 2003 S.L.',
        document_country: 'ES',
        document: 'XXXXXXXXXXX',
        address: {
          address: 'Calle Igorreko Industrialdea Kalea,',
          postal_code: '48140',
          city: 'Sabino Arana',
        },
      },

      taxes: [
        {
          tax: TaxType.IVA,
          quota: 14.3062,
          base: 68.1247,
          percentage: 21.0,
        },
      ],
      finances: [
        {
          due_date: new Date(2019, 1, 10, 0, 0),
          amount: 82.43,
        },
      ],
    };

    const movFijo3: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 2, 25, 0, 0),
      reference: 'TA66X0042537',
      total: 32.3,
      receiver: {
        name: 'Colegio Oficial de Licenciados de Educacion',
        document_country: 'ES',
        document: '00Q5755009G',
        address: {
          address: 'Calle Uruguai, 2 Atco',
          postal_code: '07010',
          city: 'Palma',
        },
      },

      taxes: [
        {
          tax: TaxType.IVA,
          quota: 5.6055,
          base: 26.693,
          percentage: 21.0,
        },
      ],
      finances: [
        {
          due_date: new Date(2019, 2, 25, 0, 0),
          amount: 32.3,
        },
      ],
    };

    describe('TEDI PDF PARSER TEST [MOVISTAR FIJO INVOICES]', () => {
      it('PARSE MOVISTAR FIJO INVOICE [Movistar_Fijo_I.pdf]', done => {
        const filename = 'test/resources/Movistar_Fijo_I.pdf';
        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, movFijo1);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });

      it('PARSE MOVISTAR FIJO  INVOICE [Movistar_Fijo_2.pdf]', done => {
        const filename = 'test/resources/Movistar_Fijo_2.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, movFijo2);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });

      it('PARSE MOVISTAR FIJO  INVOICE [Movistar_Fijo_3.pdf]', done => {
        const filename = 'test/resources/Movistar_Fijo_3.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, movFijo3);
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
