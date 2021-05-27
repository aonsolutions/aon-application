import { expect } from 'chai';
import { Invoice, InvoiceStatus, TaxType } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class TAPOTest {
  public static checkInvoice(invoice: Invoice, tuAsesoInvoice: Invoice): void {
    expect(invoice).not.to.be.null;
    expect(invoice).not.to.be.undefined;
    if (invoice) {
      expect(invoice.status).eq(tuAsesoInvoice.status);
      expect(invoice.date).eql(tuAsesoInvoice.date);
      expect(invoice.reference).eq(tuAsesoInvoice.reference);

      expect(invoice.receiver).not.to.be.undefined;
      if (invoice.receiver && tuAsesoInvoice.receiver) {
        expect(invoice.receiver.name).eq(tuAsesoInvoice.receiver.name);
        expect(invoice.receiver.document_country).eq(tuAsesoInvoice.receiver.document_country);
        expect(invoice.receiver.document).eq(tuAsesoInvoice.receiver.document);
        expect(invoice.receiver.address).not.to.be.undefined;
        if (invoice.receiver.address && tuAsesoInvoice.receiver.address) {
          expect(invoice.receiver.address.address).eq(tuAsesoInvoice.receiver.address.address);
          expect(invoice.receiver.address.postal_code).eq(tuAsesoInvoice.receiver.address.postal_code);
          expect(invoice.receiver.address.city).eq(tuAsesoInvoice.receiver.address.city);
        }
      }
     // expect(invoice.details).to.be.an('array').not.to.empty;
      if (invoice.details && tuAsesoInvoice.details) {
        for (let i = 0; i < invoice.details.length; i++) {
          expect(invoice.details[i].description).eq(tuAsesoInvoice.details[i].description);
          expect(invoice.details[i].quantity).eq(tuAsesoInvoice.details[i].quantity);
          expect(invoice.details[i].base).eq(tuAsesoInvoice.details[i].base);
          expect(invoice.details[i].price).eq(tuAsesoInvoice.details[i].price);
        }
      }

      expect(invoice.taxes).to.be.an('array').not.to.empty;
      if (invoice.taxes && tuAsesoInvoice.taxes) {
        for (let i = 0; i < invoice.taxes.length; i++) {
          expect(invoice.taxes[i].tax).eq(tuAsesoInvoice.taxes[i].tax);
          expect(invoice.taxes[i].quota).eq(tuAsesoInvoice.taxes[i].quota);
          expect(invoice.taxes[i].base).eq(tuAsesoInvoice.taxes[i].base);
          expect(invoice.taxes[i].percentage).eq(tuAsesoInvoice.taxes[i].percentage);
        }
      }
      expect(invoice.total).eq(tuAsesoInvoice.total);
      expect(invoice.finances).not.to.be.undefined;
      expect(invoice.finances).to.be.an('array').not.to.empty;
      if (invoice.finances && tuAsesoInvoice.finances) {
        for (let i = 0; i < invoice.finances.length; i++) {
          expect(invoice.finances[i].due_date).eql(tuAsesoInvoice.finances[i].due_date);
          expect(invoice.finances[i].amount).eq(tuAsesoInvoice.finances[i].amount);
        }
      }
    }
  }

  public static tapoTest(): void {
    const tuaseso1: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 0, 2, 0, 0),
      reference: '18701520',
      total: 36.3,
      receiver: {
        name: 'DOMINIQUE BRABANT',
        document_country: 'ES',
        document: 'ESX5738371D',
        address: {
          address: 'Ctra. de Cerro Alarcón, 9C, 28210 Valdemorillo, Madrid',
          postal_code: '28210',
          city: 'Valdemorillo'
        },
      },

      details: [
        {
          price: 30.0,
          quantity: 1,
          base: 30.0,
          discount: 0,
          description: 'SERVICIO FISCAL CONTABLE',
        },
      ],

      taxes: [
        {
          tax: TaxType.IVA,
          quota: 6.3,
          base: 30.0,
          percentage: 21.0,
        },
      ],
      finances: [
        {
          due_date: new Date(2019, 0, 2, 0, 0),
          amount: 36.3,
        },
      ],
    };
    const tuaseso2: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 1, 1, 0, 0),
      reference: '18701909',
      total: 121.0,
      receiver: {
        name: 'IRENE ESPINOLA BUSQUI',
        document_country: 'ES',
        document: 'ES47183579E',
        address: {
          address: 'C/ BALTARGA 7 LOCAL 1, 08207 SABADELL, Barcelona',
          postal_code: '08207',
          city: 'SABADELL'
        },
      },

      details: [
        {
          price: 50.0,
          quantity: 1,
          base: 50.0,
          discount: 0,
          description: 'SERVICIO FISCAL CONTABLE',
        },
        {
          price: 50.0,
          quantity: 1,
          base: 50.0,
          discount: 0,
          description: 'SERVICIO LABORAL',
        },
      ],

      taxes: [
        {
          tax: TaxType.IVA,
          quota: 21.0,
          base: 100.0,
          percentage: 21.0,
        },
      ],
      finances: [
        {
          due_date: new Date(2019, 1, 1, 0, 0),
          amount: 121.0,
        },
      ],
    };
    const tuaseso3: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 4, 1, 0, 0),
      reference: '18702774',
      total: 36.3,
      receiver: {
        name: 'DOMINIQUE BRABANT',
        document_country: 'ES',
        document: 'ESX5738371D',
        address: {
          address: 'Ctra. de Cerro Alarcón, 9C, 28210 Valdemorillo, Madrid',
          postal_code: '28210',
          city: 'Valdemorillo',
        },
      },

      details: [
        {
          price: 30.0,
          quantity: 1,
          base: 30.0,
          discount: 0,
          description: 'SERVICIO FISCAL CONTABLE',
        },
      ],

      taxes: [
        {
          tax: TaxType.IVA,
          quota: 6.3,
          base: 30.0,
          percentage: 21.0,
        },
      ],
      finances: [
        {
          due_date: new Date(2019, 4, 1, 0, 0),
          amount: 36.3,
        },
      ],
    };

    describe('TEDI PDF PARSER TEST [(TAPO) INVOICES]', () => {
      it('PARSE TU_ASESORIA (TAPO) INVOICE [TUASESORIA.pdf]', done => {
        const filename = 'test/resources/TUASESORIA.pdf';
        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, tuaseso1);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });

      it('PARSE TU_ASESORIA (TAPO) INVOICE [TUASESORIA2.pdf]', done => {
        const filename = 'test/resources/TUASESORIA2.pdf';
        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, tuaseso2);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
      it('PARSE TU_ASESORIA (TAPO) INVOICE [TUASESORIA3.pdf]', done => {
        const filename = 'test/resources/TUASESORIA3.pdf';
        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, tuaseso3);
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
