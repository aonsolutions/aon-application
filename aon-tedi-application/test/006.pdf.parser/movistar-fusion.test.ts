import { expect } from 'chai';
import { Invoice, InvoiceStatus, TaxType } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class MovistarFusionTest {
  public static checkInvoice(invoice: Invoice, movistarFusionInvoice: Invoice): void {
    expect(invoice).not.to.be.null;
    expect(invoice).not.to.be.undefined;
    if (invoice) {
      expect(invoice.status).eq(movistarFusionInvoice.status);
      expect(invoice.date).eql(movistarFusionInvoice.date);
      expect(invoice.reference).eq(movistarFusionInvoice.reference);

      expect(invoice.receiver).not.to.be.undefined;
      if (invoice.receiver && movistarFusionInvoice.receiver) {
        expect(invoice.receiver.name).eq(movistarFusionInvoice.receiver.name);
        expect(invoice.receiver.document_country).eq(movistarFusionInvoice.receiver.document_country);
        expect(invoice.receiver.document).eq(movistarFusionInvoice.receiver.document);
        expect(invoice.receiver.address).not.to.be.undefined;
        if (invoice.receiver.address && movistarFusionInvoice.receiver.address) {
          expect(invoice.receiver.address.address).eq(movistarFusionInvoice.receiver.address.address);
          expect(invoice.receiver.address.postal_code).eq(movistarFusionInvoice.receiver.address.postal_code);
          expect(invoice.receiver.address.city).eq(movistarFusionInvoice.receiver.address.city);
        }
      }
      //expect(invoice.details).to.be.an('array').not.to.empty;
      if (invoice.details && movistarFusionInvoice.details) {
        for (let i = 0; i < invoice.details.length; i++) {
          expect(invoice.details[i].description).eq(movistarFusionInvoice.details[i].description);
          expect(invoice.details[i].quantity).eq(movistarFusionInvoice.details[i].quantity);
          expect(invoice.details[i].base).eq(movistarFusionInvoice.details[i].base);
          expect(invoice.details[i].price).eq(movistarFusionInvoice.details[i].price);
        }
      }

      expect(invoice.taxes).to.be.an('array').not.to.empty;
      if (invoice.taxes && movistarFusionInvoice.taxes) {
        for (let i = 0; i < invoice.taxes.length; i++) {
          expect(invoice.taxes[i].tax).eq(movistarFusionInvoice.taxes[i].tax);
          expect(invoice.taxes[i].quota).eq(movistarFusionInvoice.taxes[i].quota);
          expect(invoice.taxes[i].base).eq(movistarFusionInvoice.taxes[i].base);
          expect(invoice.taxes[i].percentage).eq(movistarFusionInvoice.taxes[i].percentage);
        }
      }
      expect(invoice.total).eq(movistarFusionInvoice.total);
      expect(invoice.finances).not.to.be.undefined;
      expect(invoice.finances).to.be.an('array').not.to.empty;
      if (invoice.finances && movistarFusionInvoice.finances) {
        for (let i = 0; i < invoice.finances.length; i++) {
          expect(invoice.finances[i].due_date).eql(movistarFusionInvoice.finances[i].due_date);
          expect(invoice.finances[i].amount).eq(movistarFusionInvoice.finances[i].amount);
        }
      }
    }
  }
  public static movistarFusionTest(): void {
    const movistarFusion1: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2018, 10, 1, 0, 0),
      reference: 'TA60G0026117',
      total: 121.59,
      receiver: {
        name: 'AON SOLUTIONS S.L.U.',
        document_country: 'ES',
        document: '00B01487271',
        address: {
          address: 'Calle Duque de Wellington, 52 Bajo',
          postal_code: '01010',
          city: 'Vitoria-Gasteiz',
        },
      },
      details: [
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
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 16.7636,
          base: 79.8265,
          percentage: 21,
        },
        {
          tax: TaxType.IVA,
          quota: 0,
          base: 25.0,
          percentage: 0,
        },
      ],
      finances: [
        {
          due_date: new Date(2018, 10, 1, 0, 0),
          amount: 121.59,
        },
      ],
    };

    const movistarFusion2: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2018, 9, 1, 0, 0),
      reference: 'TA60F0026654',
      total: 87.99,
      receiver: {
        name: 'AON SOLUTIONS S.L.U.',
        document_country: 'ES',
        document: '00B01487271',
        address: {
          address: 'Calle Duque de Wellington, 52 Bajo',
          postal_code: '01010',
          city: 'Vitoria-Gasteiz',
        },
      },
      details: [
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
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 15.2712,
          base: 72.72,
          percentage: 21,
        },
      ],
      finances: [
        {
          due_date: new Date(2018, 9, 1, 0, 0),
          amount: 87.99,
        },
      ],
    };

    const movistarFusion3: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2018, 9, 1, 0, 0),
      reference: 'TA60F0248732',
      total: 89.12,
      receiver: {
        name: 'Business Process Management Systems',
        document_country: 'ES',
        document: '00B01398387',
        address: {
          address: 'Calle Orio Kalea, 6 1º D',
          postal_code: '01010',
          city: 'Vitoria-Gasteiz',
        },
      },
      details: [
        {
          price: 73.6529,
          quantity: 1,
          base: 73.6529,
          discount: 0,
          description: 'Cuotas Mensuales',
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 15.4671,
          base: 73.6529,
          percentage: 21,
        },
      ],
      finances: [
        {
          due_date: new Date(2018, 9, 1, 0, 0),
          amount: 89.12,
        },
      ],
    };

    const movistarFusion4: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2018, 6, 1, 0, 0),
      reference: 'TA60C0288630',
      total: 84.58,
      receiver: {
        name: 'Business Process Management Systems',
        document_country: 'ES',
        document: '00B01398387',
        address: {
          address: 'Calle Orio Kalea, 6 1º D',
          postal_code: '01010',
          city: 'Vitoria-Gasteiz',
        },
      },
      details: [
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
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 14.6793,
          base: 69.9014,
          percentage: 21,
        },
      ],
      finances: [
        {
          due_date: new Date(2018, 6, 1, 0, 0),
          amount: 84.58,
        },
      ],
    };

    const movistarFusion5: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2018, 8, 1, 0, 0),
      reference: 'TA60E0275244',
      total: 100.18,
      receiver: {
        name: 'Business Process Management Systems',
        document_country: 'ES',
        document: '00B01398387',
        address: {
          address: 'Calle Orio Kalea, 6 1º D',
          postal_code: '01010',
          city: 'Vitoria-Gasteiz',
        },
      },
      details: [
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
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 17.3867,
          base: 82.7939,
          percentage: 21,
        },
      ],
      finances: [
        {
          due_date: new Date(2018, 8, 1, 0, 0),
          amount: 100.18,
        },
      ],
    };

    describe('TEDI PDF PARSER TEST [MOVISTAR FUSION INVOICES]', () => {
      it('PARSE MOVISTAR FUSION  INVOICE [Movistar_Fusion_I.pdf]', done => {
        const filename = 'test/resources/Movistar_Fusion_I.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            // console.log( JSON.stringify( invoice , null , 1 ) )
            this.checkInvoice(invoice, movistarFusion1);
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
            // console.log( JSON.stringify( invoice , null , 1 ) )
            this.checkInvoice(invoice, movistarFusion2);
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
            // console.log( JSON.stringify( invoice , null , 1 ) )
            this.checkInvoice(invoice, movistarFusion3);
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
            // console.log( JSON.stringify( invoice , null , 1 ) )
            this.checkInvoice(invoice, movistarFusion4);
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
            // console.log( JSON.stringify( invoice , null , 1 ) )
            this.checkInvoice(invoice, movistarFusion5);
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
