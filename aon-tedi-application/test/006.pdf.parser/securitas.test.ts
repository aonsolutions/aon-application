import { expect } from 'chai';
import { Invoice, InvoiceStatus, TaxType } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class SecuritasTest {
  public static checkInvoice(invoice: Invoice, securitasInvoice: Invoice): void {
    expect(invoice).not.to.be.null;
    expect(invoice).not.to.be.undefined;
    if (invoice) {
      expect(invoice.status).eq(securitasInvoice.status);
      expect(invoice.date).eql(securitasInvoice.date);
      expect(invoice.reference).eq(securitasInvoice.reference);

      expect(invoice.receiver).not.to.be.undefined;
      if (invoice.receiver && securitasInvoice.receiver) {
        expect(invoice.receiver.name).eq(securitasInvoice.receiver.name);
        expect(invoice.receiver.document_country).eq(securitasInvoice.receiver.document_country);
        expect(invoice.receiver.document).eq(securitasInvoice.receiver.document);
        expect(invoice.receiver.address).not.to.be.undefined;
        if (invoice.receiver.address && securitasInvoice.receiver.address) {
          expect(invoice.receiver.address.address).eq(securitasInvoice.receiver.address.address);
          expect(invoice.receiver.address.postal_code).eq(securitasInvoice.receiver.address.postal_code);
          expect(invoice.receiver.address.city).eq(securitasInvoice.receiver.address.city);
        }
      }
      //expect(invoice.details).to.be.an('array').not.to.empty;
      if (invoice.details && securitasInvoice.details) {
        for (let i = 0; i < invoice.details.length; i++) {
          expect(invoice.details[i].description).eq(securitasInvoice.details[i].description);
          expect(invoice.details[i].quantity).eq(securitasInvoice.details[i].quantity);
          expect(invoice.details[i].base).eq(securitasInvoice.details[i].base);
          expect(invoice.details[i].price).eq(securitasInvoice.details[i].price);
        }
      }

      expect(invoice.taxes).to.be.an('array').not.to.empty;
      if (invoice.taxes && securitasInvoice.taxes) {
        for (let i = 0; i < invoice.taxes.length; i++) {
          expect(invoice.taxes[i].tax).eq(securitasInvoice.taxes[i].tax);
          expect(invoice.taxes[i].quota).eq(securitasInvoice.taxes[i].quota);
          expect(invoice.taxes[i].base).eq(securitasInvoice.taxes[i].base);
          expect(invoice.taxes[i].percentage).eq(securitasInvoice.taxes[i].percentage);
        }
      }
      expect(invoice.total).eq(securitasInvoice.total);
      // expect(invoice.finances).not.to.be.undefined;
      //  expect(invoice.finances).to.be.an('array').not.to.empty;
      if (invoice.finances && securitasInvoice.finances) {
        for (let i = 0; i < invoice.finances.length; i++) {
          expect(invoice.finances[i].due_date).eql(securitasInvoice.finances[i].due_date);
          expect(invoice.finances[i].amount).eq(securitasInvoice.finances[i].amount);
        }
      }
    }
  }

  public static securitasTest(): void {
    const securitas3: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 0, 1, 0, 0),
      reference: 'A26106013',
      total: 50.74,
      receiver: {
        name: 'KALDEVI INGENIERIA GERIATRICA S.L',
        document_country: 'ES',
        document: 'B96297403',
        address: {
          address: 'CALLE ACEQUIA DE QUART 7 POLIG,LA PASCUALETA',
          postal_code: '46200',
          city: 'PAIPORTA',
        },
      },

      details: [
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
      ],

      taxes: [
        {
          tax: TaxType.IVA,
          quota: 8.81,
          base: 41.93,
          percentage: 21.0,
        },
      ],
    };
    const securitas4: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 0, 1, 0, 0),
      reference: 'A26106013',
      total: 46.39,
      receiver: {
        name: 'JOSE ANTONIO GOMEZ MARTINEZ',
        document_country: 'ES',
        document: '25376850F',
        address: {
          address: 'CALLE PONIENTE 8 PUERTA 37',
          postal_code: '46017',
          city: 'VALENCIA',
        },
      },

      details: [
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
      ],

      taxes: [
        {
          tax: TaxType.IVA,
          quota: 8.05,
          base: 38.34,
          percentage: 21.0,
        },
      ],
    };

    describe('TEDI PDF PARSER TEST [ SECURITAS  ]', () => {
      it.skip('PARSE SECURITAS INVOICE [ SECURITAS_3.pdf ]', done => {
        const filename = 'test/resources/Securitas_3.pdf';
        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, securitas3);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });

      it.skip('PARSE SECURITAS INVOICE [ SECURITAS_4.pdf ]', done => {
        const filename = 'test/resources/Securitas_4.pdf';
        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, securitas4);
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
