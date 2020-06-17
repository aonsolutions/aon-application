import { expect } from 'chai';
import { Invoice, InvoiceStatus, TaxType } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class BipDriveTest {
  public static checkInvoice(invoice: Invoice, bipDriveInvoice: Invoice): void {
    expect(invoice).not.to.be.null;
    expect(invoice).not.to.be.undefined;
    if (invoice) {
      expect(invoice.status).eq(bipDriveInvoice.status);
      expect(invoice.date).eql(bipDriveInvoice.date);
      expect(invoice.reference).eq(bipDriveInvoice.reference);

      expect(invoice.receiver).not.to.be.undefined;
      if (invoice.receiver && bipDriveInvoice.receiver) {
        expect(invoice.receiver.name).eq(bipDriveInvoice.receiver.name);
        expect(invoice.receiver.document_country).eq(bipDriveInvoice.receiver.document_country);
        expect(invoice.receiver.document).eq(bipDriveInvoice.receiver.document);
        expect(invoice.receiver.address).not.to.be.undefined;
        if (invoice.receiver.address && bipDriveInvoice.receiver.address) {
          expect(invoice.receiver.address.address).eq(bipDriveInvoice.receiver.address.address);
          expect(invoice.receiver.address.postal_code).eq(bipDriveInvoice.receiver.address.postal_code);
          expect(invoice.receiver.address.city).eq(bipDriveInvoice.receiver.address.city);
        }
      }
      // expect(invoice.details).to.be.an('array').not.to.empty;
      if (invoice.details && bipDriveInvoice.details) {
        for (let i = 0; i < invoice.details.length; i++) {
          expect(invoice.details[i].description).eq(bipDriveInvoice.details[i].description);
          expect(invoice.details[i].quantity).eq(bipDriveInvoice.details[i].quantity);
          expect(invoice.details[i].base).eq(bipDriveInvoice.details[i].base);
          expect(invoice.details[i].price).eq(bipDriveInvoice.details[i].price);
        }
      }

      expect(invoice.taxes).to.be.an('array').not.to.empty;
      if (invoice.taxes && bipDriveInvoice.taxes) {
        for (let i = 0; i < invoice.taxes.length; i++) {
          expect(invoice.taxes[i].tax).eq(bipDriveInvoice.taxes[i].tax);
          expect(invoice.taxes[i].quota).eq(bipDriveInvoice.taxes[i].quota);
          expect(invoice.taxes[i].base).eq(bipDriveInvoice.taxes[i].base);
          expect(invoice.taxes[i].percentage).eq(bipDriveInvoice.taxes[i].percentage);
        }
      }
      expect(invoice.total).eq(bipDriveInvoice.total);
      expect(invoice.finances).not.to.be.undefined;
      expect(invoice.finances).to.be.an('array').not.to.empty;
      if (invoice.finances && bipDriveInvoice.finances) {
        for (let i = 0; i < invoice.finances.length; i++) {
          expect(invoice.finances[i].due_date).eql(bipDriveInvoice.finances[i].due_date);
          expect(invoice.finances[i].amount).eq(bipDriveInvoice.finances[i].amount);
          expect(invoice.finances[i].iban).eq(bipDriveInvoice.finances[i].iban);
        }
      }
    }
  }

  public static bipDriveTest(): void {
    const bipDriveInvoice1: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 7, 31),
      reference: 'CI0002464605-0819',
      total: 52.25,
      receiver: {
        name: 'JOSEP COMAS ARNAU',
        document_country: 'ES',
        document: '39311407Z',
        address: {
          address: 'El Ojáncano, 23,',
          postal_code: '39764',
          city: 'Voto',
        },
      },
      details: [
        {
          price: 28.18,
          quantity: 1,
          base: 28.18,
          description: 'Peajes autopistas',
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 9.07,
          base: 43.18,
          percentage: 21.0,
        },
      ],
      finances: [
        {
          due_date: new Date(2019, 8, 6, 0, 0),
          amount: 52.25,
        },
      ],
    };

    describe('TEDI PDF PARSER TEST [ BIPDRIVE ]', () => {
      it('PARSE  BIPDRIVE INVOICE [ BIP_DRIVE_I.pdf ]', done => {
        const filename = 'test/resources/BIP_DRIVE_I.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, bipDriveInvoice1);
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
