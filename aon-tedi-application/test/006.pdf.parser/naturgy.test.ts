import { expect } from 'chai';
import { InvoiceStatus, TaxType, Invoice } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class NaturgyTest {
  public static checkInvoice(invoice: Invoice, naturgyInvoice: Invoice): void {
    expect(invoice).not.to.be.null;
    expect(invoice).not.to.be.undefined;
    if (invoice) {
      expect(invoice.status).eq(naturgyInvoice.status);
      expect(invoice.date).eql(naturgyInvoice.date);
      expect(invoice.reference).eq(naturgyInvoice.reference);

      expect(invoice.receiver).not.to.be.undefined;
      if (invoice.receiver && naturgyInvoice.receiver) {
        expect(invoice.receiver.name).eq(naturgyInvoice.receiver.name);
        expect(invoice.receiver.document_country).eq(naturgyInvoice.receiver.document_country);
        expect(invoice.receiver.document).eq(naturgyInvoice.receiver.document);
        expect(invoice.receiver.address).not.to.be.undefined;
        if (invoice.receiver.address && naturgyInvoice.receiver.address) {
          expect(invoice.receiver.address.address).eq(naturgyInvoice.receiver.address.address);
          expect(invoice.receiver.address.postal_code).eq(naturgyInvoice.receiver.address.postal_code);
          expect(invoice.receiver.address.city).eq(naturgyInvoice.receiver.address.city);
        }
      }
      //expect(invoice.details).to.be.an('array').not.to.empty;
      if (invoice.details && naturgyInvoice.details) {
        for (let i = 0; i < invoice.details.length; i++) {
          expect(invoice.details[i].description).eq(naturgyInvoice.details[i].description);
          expect(invoice.details[i].quantity).eq(naturgyInvoice.details[i].quantity);
          expect(invoice.details[i].base).eq(naturgyInvoice.details[i].base);
          expect(invoice.details[i].price).eq(naturgyInvoice.details[i].price);
        }
      }

      expect(invoice.taxes).to.be.an('array').not.to.empty;
      if (invoice.taxes && naturgyInvoice.taxes) {
        for (let i = 0; i < invoice.taxes.length; i++) {
          expect(invoice.taxes[i].tax).eq(naturgyInvoice.taxes[i].tax);
          expect(invoice.taxes[i].quota).eq(naturgyInvoice.taxes[i].quota);
          expect(invoice.taxes[i].base).eq(naturgyInvoice.taxes[i].base);
          expect(invoice.taxes[i].percentage).eq(naturgyInvoice.taxes[i].percentage);
        }
      }
      expect(invoice.total).eq(naturgyInvoice.total);
      expect(invoice.finances).not.to.be.undefined;
      expect(invoice.finances).to.be.an('array').not.to.empty;
      if (invoice.finances && naturgyInvoice.finances) {
        for (let i = 0; i < invoice.finances.length; i++) {
          expect(invoice.finances[i].due_date).eql(naturgyInvoice.finances[i].due_date);
          expect(invoice.finances[i].amount).eq(naturgyInvoice.finances[i].amount);
        }
      }
    }
  }
  public static naturgyTest(): void {
    const naturgy1: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2018, 10, 16, 0, 0),
      reference: 'RE18326000307639',
      total: 429.86,
      receiver: {
        name: 'AON SOLUTIONS S.L.',
        document_country: 'ES',
        document: 'B01487271',
        address: {
          address: 'DUQUE DE WELLINGTON 0052',
        },
      },

      details: [
        {
          description: 'Consumos',
          quantity: 1,
          price: 331.96,
          base: 331.96,
          discount: 0,
        },
        {
          description: 'Impuesto electricidad',
          quantity: 331.96,
          price: 0.0511269632,
          base: 16.97,
          discount: 0,
        },
        {
          description: 'Alquiler de contador',
          quantity: 32,
          price: 0.197813,
          base: 6.33,
          discount: 0,
        },
      ],

      taxes: [
        {
          tax: TaxType.IVA,
          quota: 74.6,
          base: 355.26,
          percentage: 21.0,
        },
      ],
      finances: [
        {
          iban: 'ES96 0075 4626 4106 0005 ****',
          due_date: new Date(2018, 10, 23, 0, 0),
          amount: 429.86,
        },
      ],
    };
    const naturgy2: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2018, 10, 16, 0, 0),
      reference: 'RE18326000307640',
      total: 421.39,
      receiver: {
        name: 'AON SOLUTIONS S.L.',
        document_country: 'ES',
        document: 'B01487271',
        address: {
          address: 'DUQUE DE WELLINGTON 0052',
        },
      },

      details: [
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
      ],

      taxes: [
        {
          tax: TaxType.IVA,
          quota: 73.13,
          base: 348.26,
          percentage: 21.0,
        },
      ],
      finances: [
        {
          iban: 'ES96 0075 4626 4106 0005 ****',
          due_date: new Date(2018, 10, 23, 0, 0),
          amount: 421.39,
        },
      ],
    };
    const naturgy3: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2018, 10, 8, 0, 0),
      reference: 'SU18320502428463',
      total: -428.46,
      receiver: {
        name: 'AON SOLUTIONS S.L.',
        document_country: 'ES',
        document: 'B01487271',
        address: {
          address: 'DUQUE DE WELLINGTON 0052',
        },
      },

      details: [
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
      ],

      taxes: [
        {
          tax: TaxType.IVA,
          quota: -74.36,
          base: -354.1,
          percentage: 21.0,
        },
      ],
      finances: [
        {
          iban: 'ES96 0075 4626 4106 0005 ****',
          due_date: new Date(2018, 9, 18, 0, 0),
          amount: -428.46,
        },
      ],
    };

    describe('TEDI PDF PARSER TEST [NATURGY INVOICES]', () => {
      it('PARSE NATURGY INVOICE [Naturgy_I.pdf]', done => {
        const filename = 'test/resources/Naturgy_I.pdf';
        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, naturgy1);
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
            this.checkInvoice(invoice, naturgy2);
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
            this.checkInvoice(invoice, naturgy3);
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
