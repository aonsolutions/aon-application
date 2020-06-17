import { expect } from 'chai';
import { Invoice, InvoiceStatus, TaxType } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class OrangeTest {
  public static checkInvoice(invoice: Invoice, orangeInvoice: Invoice): void {
    expect(invoice).not.to.be.null;
    expect(invoice).not.to.be.undefined;
    if (invoice) {
      expect(invoice.status).eq(orangeInvoice.status);
      expect(invoice.date).eql(orangeInvoice.date);
      expect(invoice.reference).eq(orangeInvoice.reference);

      expect(invoice.receiver).not.to.be.undefined;
      if (invoice.receiver && orangeInvoice.receiver) {
        expect(invoice.receiver.name).eq(orangeInvoice.receiver.name);
        expect(invoice.receiver.document_country).eq(orangeInvoice.receiver.document_country);
        expect(invoice.receiver.document).eq(orangeInvoice.receiver.document);
        expect(invoice.receiver.address).not.to.be.undefined;
        if (invoice.receiver.address && orangeInvoice.receiver.address) {
          expect(invoice.receiver.address.address).eq(orangeInvoice.receiver.address.address);
          //   expect(invoice.receiver.address.postal_code).eq(orangeInvoice.receiver.address.postal_code);
          expect(invoice.receiver.address.city).eq(orangeInvoice.receiver.address.city);
        }
      }
      //expect(invoice.details).to.be.an('array').not.to.empty;
      if (invoice.details && orangeInvoice.details) {
        for (let i = 0; i < invoice.details.length; i++) {
          expect(invoice.details[i].description).eq(orangeInvoice.details[i].description);
          expect(invoice.details[i].quantity).eq(orangeInvoice.details[i].quantity);
          expect(invoice.details[i].base).eq(orangeInvoice.details[i].base);
          expect(invoice.details[i].price).eq(orangeInvoice.details[i].price);
        }
      }

      expect(invoice.taxes).to.be.an('array').not.to.empty;
      if (invoice.taxes && orangeInvoice.taxes) {
        for (let i = 0; i < invoice.taxes.length; i++) {
          expect(invoice.taxes[i].tax).eq(orangeInvoice.taxes[i].tax);
          expect(invoice.taxes[i].quota).eq(orangeInvoice.taxes[i].quota);
          expect(invoice.taxes[i].base).eq(orangeInvoice.taxes[i].base);
          expect(invoice.taxes[i].percentage).eq(orangeInvoice.taxes[i].percentage);
        }
      }
      expect(invoice.total).eq(orangeInvoice.total);
      expect(invoice.finances).not.to.be.undefined;
      expect(invoice.finances).to.be.an('array').not.to.empty;
      if (invoice.finances && orangeInvoice.finances) {
        for (let i = 0; i < invoice.finances.length; i++) {
          expect(invoice.finances[i].due_date).eql(orangeInvoice.finances[i].due_date);
          expect(invoice.finances[i].amount).eq(orangeInvoice.finances[i].amount);
        }
      }
    }
  }

  public static orangeTest(): void {
    const orange1: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2018, 8, 5, 0, 0),
      reference: 'E1AL00023624-0918',
      total: 247.61,
      receiver: {
        name: 'AON SOLUTIONS S L',
        document_country: 'ES',
        document: 'B01487271',
        address: {
          address: 'CALLE DUQUE DE WELLINGTON 52 BJ, 01010 VITORIA-GASTEIZ, ALAVA',
          postal_code: '48010',
          province: 'VIZCAYA',
        },
      },

      details: [
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
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 20.5,
          base: 97.64,
          percentage: 21.0,
        },
        {
          tax: TaxType.IVA,
          quota: 0,
          base: 129.47,
          percentage: 0,
        },
      ],
      finances: [
        {
          due_date: new Date(2018, 8, 6, 0, 0),
          amount: 247.61,
        },
      ],
    };
    const orange2: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2018, 9, 5, 0, 0),
      reference: 'E1AL00023966-1018',
      total: 151.71,
      receiver: {
        name: 'AON SOLUTIONS S L',
        document_country: 'ES',
        document: 'B01487271',
        address: {
          address: 'CALLE DUQUE DE WELLINGTON 52 BJ, 01010 VITORIA-GASTEIZ, ALAVA',
          postal_code: '48010',
          province: 'VIZCAYA',
        },
      },

      details: [
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
      ],

      taxes: [
        {
          tax: TaxType.IVA,
          quota: 19.61,
          base: 93.38,
          percentage: 21.0,
        },
        {
          tax: TaxType.IVA,
          quota: 0,
          base: 38.72,
          percentage: 0,
        },
      ],
      finances: [
        {
          due_date: new Date(2018, 9, 6, 0, 0),
          amount: 151.71,
        },
      ],
    };

    const orange3: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2018, 10, 5, 0, 0),
      reference: 'E1AL00024204-1118',
      total: 146.62,
      receiver: {
        name: 'AON SOLUTIONS S L',
        document_country: 'ES',
        document: 'B01487271',
        address: {
          address: 'CALLE DUQUE DE WELLINGTON 52 BJ, 01010 VITORIA-GASTEIZ, ALAVA',
          postal_code: '01010',
          province: 'ALAVA',
        },
      },

      details: [
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
      ],

      taxes: [
        {
          tax: TaxType.IVA,
          quota: 18.73,
          base: 89.17,
          percentage: 21.0,
        },
        {
          tax: TaxType.IVA,
          quota: 0,
          base: 38.72,
          percentage: 0,
        },
      ],
      finances: [
        {
          due_date: new Date(2018, 10, 6, 0, 0),
          amount: 146.62,
        },
      ],
    };

    describe('TEDI PDF PARSER TEST [ORANGE INVOICES]', () => {
      it('PARSE ORANGE INVOICE [Orange_I.pdf]', done => {
        const filename = 'test/resources/Orange_I.pdf';
        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, orange1);
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
            this.checkInvoice(invoice, orange2);
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
            this.checkInvoice(invoice, orange3);
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
