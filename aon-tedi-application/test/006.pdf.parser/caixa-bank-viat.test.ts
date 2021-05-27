import { expect } from 'chai';
import { Invoice, InvoiceStatus, TaxType } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class CaixaBankViaTest {
  public static checkInvoice(invoice: Invoice, viatInvoice: Invoice): void {
    expect(invoice).not.to.be.null;
    expect(invoice).not.to.be.undefined;
    if (invoice) {
      expect(invoice.status).eq(viatInvoice.status);
      expect(invoice.date).eql(viatInvoice.date);
      expect(invoice.reference).eq(viatInvoice.reference);

      expect(invoice.receiver).not.to.be.undefined;
      if (invoice.receiver && viatInvoice.receiver) {
        expect(invoice.receiver.name).eq(viatInvoice.receiver.name);
        expect(invoice.receiver.document_country).eq(viatInvoice.receiver.document_country);
        expect(invoice.receiver.document).eq(viatInvoice.receiver.document);
        expect(invoice.receiver.address).not.to.be.undefined;
        if (invoice.receiver.address && viatInvoice.receiver.address) {
          expect(invoice.receiver.address.address).eq(viatInvoice.receiver.address.address);
          expect(invoice.receiver.address.postal_code).eq(viatInvoice.receiver.address.postal_code);
          expect(invoice.receiver.address.city).eq(viatInvoice.receiver.address.city);
        }
      }
      // expect(invoice.details).to.be.an('array').not.to.empty;
      if (invoice.details && viatInvoice.details) {
        for (let i = 0; i < invoice.details.length; i++) {
          expect(invoice.details[i].description).eq(viatInvoice.details[i].description);
          expect(invoice.details[i].quantity).eq(viatInvoice.details[i].quantity);
          expect(invoice.details[i].base).eq(viatInvoice.details[i].base);
          expect(invoice.details[i].price).eq(viatInvoice.details[i].price);
        }
      }

      expect(invoice.taxes).to.be.an('array').not.to.empty;
      if (invoice.taxes && viatInvoice.taxes) {
        for (let i = 0; i < invoice.taxes.length; i++) {
          expect(invoice.taxes[i].tax).eq(viatInvoice.taxes[i].tax);
          expect(invoice.taxes[i].quota).eq(viatInvoice.taxes[i].quota);
          expect(invoice.taxes[i].base).eq(viatInvoice.taxes[i].base);
          expect(invoice.taxes[i].percentage).eq(viatInvoice.taxes[i].percentage);
        }
      }
      expect(invoice.total).eq(viatInvoice.total);
      expect(invoice.finances).not.to.be.undefined;
      expect(invoice.finances).to.be.an('array').not.to.empty;
      if (invoice.finances && viatInvoice.finances) {
        for (let i = 0; i < invoice.finances.length; i++) {
          expect(invoice.finances[i].iban).eq(viatInvoice.finances[i].iban);
          expect(invoice.finances[i].due_date).eql(viatInvoice.finances[i].due_date);
          expect(invoice.finances[i].amount).eq(viatInvoice.finances[i].amount);
        }
      }
    }
  }

  public static viatTest(): void {
    const viat1: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 3, 30),
      reference: 'ES0000010383461-0419',
      total: 86.0,
      receiver: {
        name: 'AON SOLUTIONS SL',
        document_country: 'ES',
        document: 'B01487271',
        address: {
          address: 'DUQU E DE WELL INGT ON 52',
          postal_code: '01010',
          city: 'VITORIA',
        },
      },
      details: [
        {
          description: 'OPERACIONES PEAJES AUTOPISTAS',
          quantity: 1,
          price: 67.2,
          discount: 0,
          base: 67.2,
        },
        {
          description: 'OPERACIONES PARKINGS',
          quantity: 1,
          price: 18.8,
          discount: 0,
          base: 18.8,
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 14.92,
          base: 71.08,
          percentage: 21.0,
        },
      ],

      finances: [
        {
          iban: 'ES76 2100 5849 4102 0004 9423',
          due_date: new Date(2019, 3, 30),
          amount: 86.0,
        },
      ],
    };

    const viat2: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2018, 11, 31),
      reference: 'ES0000009197132-1218',
      total: 90.1,
      receiver: {
        name: 'AON SOLUTIONS SL',
        document_country: 'ES',
        document: 'B01487271',
        address: {
          address: 'DUQU E DE WELL INGT ON 52',
          postal_code: '01010',
          city: 'VITORIA',
        },
      },
      details: [
        {
          description: 'OPERACIONES PEAJES AUTOPISTAS',
          quantity: 1,
          price: 90.1,
          discount: 0,
          base: 90.1,
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 15.64,
          base: 74.46,
          percentage: 21.0,
        },
      ],

      finances: [
        {
          iban: 'ES76 2100 5849 4102 0004 9423',
          due_date: new Date(2018, 11, 31),
          amount: 90.1,
        },
      ],
    };

    const viat3: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 6, 31),
      reference: 'ES0000011331615-0719',
      total: 67.4,
      receiver: {
        name: 'AON SOLUTIONS SL',
        document_country: 'ES',
        document: 'B01487271',
        address: {
          address: 'DUQU E DE WELL INGT ON 52',
          postal_code: '01010',
          city: 'VITORIA',
        },
      },
      details: [
        {
          description: 'PEAJES AUTOPISTAS',
          quantity: 1,
          price: 67.4,
          discount: 0,
          base: 67.4,
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 11.7,
          base: 55.7,
          percentage: 21.0,
        },
      ],

      finances: [
        {
          iban: 'ES76 2100 5849 4102 0004 9423',
          due_date: new Date(2019, 6, 31),
          amount: 67.4,
        },
      ],
    };

    describe('TEDI PDF PARSER TEST [ CAIXABANK VIA T ]', () => {
      it('PARSE  CAIXA_BANK_VIAT_I INVOICE [ CAIXA_BANK_VIAT_I.pdf ]', done => {
        const filename = 'test/resources/CAIXA_BANK_VIAT_I.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, viat1);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
      it('PARSE  CAIXA_BANK_VIAT_II INVOICE [ CAIXA_BANK_VIAT_II.pdf ]', done => {
        const filename = 'test/resources/CAIXA_BANK_VIAT_II.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, viat2);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
      it('PARSE  CAIXA_BANK_VIAT_III INVOICE [ CAIXA_BANK_VIAT_III.pdf ]', done => {
        const filename = 'test/resources/CAIXA_BANK_VIAT_III.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, viat3);
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
