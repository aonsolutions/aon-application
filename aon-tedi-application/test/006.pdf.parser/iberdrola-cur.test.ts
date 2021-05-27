import { expect } from 'chai';
import { Invoice, InvoiceStatus, TaxType } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class IberdrolaCurTest {
  public static checkInvoice(invoice: Invoice, iberdrolaInvoice: Invoice): void {
    expect(invoice).not.to.be.null;
    expect(invoice).not.to.be.undefined;
    if (invoice) {
      expect(invoice.status).eq(iberdrolaInvoice.status);
      expect(invoice.date).eql(iberdrolaInvoice.date);
      expect(invoice.reference).eq(iberdrolaInvoice.reference);

      expect(invoice.receiver).not.to.be.undefined;
      if (invoice.receiver && iberdrolaInvoice.receiver) {
        expect(invoice.receiver.name).eq(iberdrolaInvoice.receiver.name);
        expect(invoice.receiver.document_country).eq(iberdrolaInvoice.receiver.document_country);
        expect(invoice.receiver.document).eq(iberdrolaInvoice.receiver.document);
        expect(invoice.receiver.address).not.to.be.undefined;
        if (invoice.receiver.address && iberdrolaInvoice.receiver.address) {
          expect(invoice.receiver.address.address).eq(iberdrolaInvoice.receiver.address.address);
          expect(invoice.receiver.address.postal_code).eq(iberdrolaInvoice.receiver.address.postal_code);
          expect(invoice.receiver.address.city).eq(iberdrolaInvoice.receiver.address.city);
        }
      }
      //expect(invoice.details).to.be.an('array').not.to.empty;
      if (invoice.details && iberdrolaInvoice.details) {
        for (let i = 0; i < invoice.details.length; i++) {
          expect(invoice.details[i].description).eq(iberdrolaInvoice.details[i].description);
          expect(invoice.details[i].quantity).eq(iberdrolaInvoice.details[i].quantity);
          expect(invoice.details[i].base).eq(iberdrolaInvoice.details[i].base);
          expect(invoice.details[i].price).eq(iberdrolaInvoice.details[i].price);
        }
      }

      expect(invoice.taxes).to.be.an('array').not.to.empty;
      if (invoice.taxes && iberdrolaInvoice.taxes) {
        for (let i = 0; i < invoice.taxes.length; i++) {
          expect(invoice.taxes[i].tax).eq(iberdrolaInvoice.taxes[i].tax);
          expect(invoice.taxes[i].quota).eq(iberdrolaInvoice.taxes[i].quota);
          expect(invoice.taxes[i].base).eq(iberdrolaInvoice.taxes[i].base);
          expect(invoice.taxes[i].percentage).eq(iberdrolaInvoice.taxes[i].percentage);
        }
      }
      expect(invoice.total).eq(iberdrolaInvoice.total);
      expect(invoice.finances).not.to.be.undefined;
      expect(invoice.finances).to.be.an('array').not.to.empty;
      if (invoice.finances && iberdrolaInvoice.finances) {
        for (let i = 0; i < invoice.finances.length; i++) {
          expect(invoice.finances[i].due_date).eql(iberdrolaInvoice.finances[i].due_date);
          expect(invoice.finances[i].amount).eq(iberdrolaInvoice.finances[i].amount);
        }
      }
    }
  }

  public static iberdrolaCurTest(): void {
    const iberdrolaCur1: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 2, 5, 0, 0),
      reference: '09190305010085331',
      total: 12.1,
      receiver: {
        name: 'EUGENIO CASTELLANO HURTADO',
        document_country: 'ES',
        document: '44671367P',
        address: {
          address: 'C/ URSULETA, 2, Bajo 1 01240 ALEGRIA DULANTZI',
        },
      },

      details: [
        {
          description: 'Por potencia contratada',
          quantity: 1,
          price: 4.74,
          discount: 0,
          base: 4.74,
          vat: 21,
        },
        {
          description: 'Por energía consumida',
          quantity: 1,
          price: 4.55,
          discount: 0,
          base: 4.55,
          vat: 21,
        },
        {
          description: 'Por energía consumida',
          quantity: 1,
          price: 0.47,
          discount: 0,
          base: 0.47,
          vat: 21,
        },
        {
          description: 'Alquiler equipos medida y control',
          quantity: 1,
          price: 0.24,
          discount: 0,
          base: 0.24,
          vat: 21,
        },
      ],

      taxes: [
        {
          tax: TaxType.IVA,
          quota: 2.1,
          base: 10.0,
          percentage: 21.0,
        },
      ],
      finances: [
        {
          iban: 'ES41 0049 6064 3129 1603 ****',
          due_date: new Date(2019, 2, 13, 0, 0),
          amount: 12.1,
        },
      ],
    };

    describe('TEDI PDF PARSER TEST [IBERDROLA CUR (COM. ULT. RECURSO) INVOICES]', () => {
      it('PARSE IBERDROLA INVOICE [Iberdrola_CUR_I.pdf]', done => {
        const filename = 'test/resources/Iberdrola_CUR_I.pdf';
        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, iberdrolaCur1);
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
