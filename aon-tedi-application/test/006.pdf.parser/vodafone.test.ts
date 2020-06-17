import { expect } from 'chai';
import { Invoice, InvoiceStatus, TaxType } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class VodafoneTest {
  public static checkInvoice(invoice: Invoice, vodafoneInvoice: Invoice): void {
    expect(invoice).not.to.be.null;
    expect(invoice).not.to.be.undefined;
    if (invoice) {
      expect(invoice.status).eq(vodafoneInvoice.status);
      expect(invoice.date).eql(vodafoneInvoice.date);
      expect(invoice.reference).eq(vodafoneInvoice.reference);

      expect(invoice.receiver).not.to.be.undefined;
      if (invoice.receiver && vodafoneInvoice.receiver) {
        expect(invoice.receiver.name).eq(vodafoneInvoice.receiver.name);
        expect(invoice.receiver.document_country).eq(vodafoneInvoice.receiver.document_country);
        expect(invoice.receiver.document).eq(vodafoneInvoice.receiver.document);
        expect(invoice.receiver.address).not.to.be.undefined;
        if (invoice.receiver.address && vodafoneInvoice.receiver.address) {
          expect(invoice.receiver.address.address).eq(vodafoneInvoice.receiver.address.address);
          expect(invoice.receiver.address.postal_code).eq(vodafoneInvoice.receiver.address.postal_code);
          expect(invoice.receiver.address.city).eq(vodafoneInvoice.receiver.address.city);
        }
      }
      //expect(invoice.details).to.be.an('array').not.to.empty;
      if (invoice.details && vodafoneInvoice.details) {
        for (let i = 0; i < invoice.details.length; i++) {
          expect(invoice.details[i].description).eq(vodafoneInvoice.details[i].description);
          expect(invoice.details[i].quantity).eq(vodafoneInvoice.details[i].quantity);
          expect(invoice.details[i].base).eq(vodafoneInvoice.details[i].base);
          expect(invoice.details[i].price).eq(vodafoneInvoice.details[i].price);
        }
      }

      expect(invoice.taxes).to.be.an('array').not.to.empty;
      if (invoice.taxes && vodafoneInvoice.taxes) {
        for (let i = 0; i < invoice.taxes.length; i++) {
          expect(invoice.taxes[i].tax).eq(vodafoneInvoice.taxes[i].tax);
          expect(invoice.taxes[i].quota).eq(vodafoneInvoice.taxes[i].quota);
          expect(invoice.taxes[i].base).eq(vodafoneInvoice.taxes[i].base);
          expect(invoice.taxes[i].percentage).eq(vodafoneInvoice.taxes[i].percentage);
        }
      }
      expect(invoice.total).eq(vodafoneInvoice.total);
      expect(invoice.finances).not.to.be.undefined;
      expect(invoice.finances).to.be.an('array').not.to.empty;
      if (invoice.finances && vodafoneInvoice.finances) {
        for (let i = 0; i < invoice.finances.length; i++) {
          expect(invoice.finances[i].due_date).eql(vodafoneInvoice.finances[i].due_date);
          expect(invoice.finances[i].amount).eq(vodafoneInvoice.finances[i].amount);
        }
      }
    }
  }

  public static vodafoneTest(): void {
    const vodafone1: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 6, 1, 0, 0),
      reference: 'B-87539284',
      total: 88.32,
      receiver: {
        name: 'AON SOLUTIONS SL',
        document_country: 'ES',
        document: 'B01487271',
        address: {
          address: 'CL DUQUE DE WELLINGTON 52  BA 1',
          postal_code: '01010',
          city: 'VITORIA-GASTEIZ',
        },
      },
      details: [
        {
          price: 18.44,
          quantity: 1,
          base: 18.44,
          discount: 0,
          description: 'Servicios',
        },
        {
          price: 54.55,
          quantity: 1,
          base: 54.55,
          discount: 0,
          description: 'Cuotas a nivel de cuenta Total',
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 15.33,
          base: 72.99,
          percentage: 21.0
        }
      ],
      finances: [
        {
          due_date: new Date(2019, 6, 12, 0, 0),
          amount: 88.32,
        },
      ],
    };

    describe('TEDI PDF PARSER TEST [ VODAFONE ]', () => {
        it('PARSE VODAFONE INVOICE [ VODAFONE-1.pdf ]', done => {
          const filename = 'test/resources/Vodafone_1.pdf';
  
          TediPdfParser.extractFromFile(filename).subscribe(
            invoice => {
              this.checkInvoice(invoice, vodafone1);
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
