import { expect } from 'chai';
import { Invoice, InvoiceStatus, TaxType } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class IberdrolaClientesTest {
  public static checkInvoice(invoice: Invoice, iberdrolaClientInvoice: Invoice): void {
    expect(invoice).not.to.be.null;
    expect(invoice).not.to.be.undefined;
    if (invoice) {
      expect(invoice.status).eq(iberdrolaClientInvoice.status);
      expect(invoice.date).eql(iberdrolaClientInvoice.date);
      expect(invoice.reference).eq(iberdrolaClientInvoice.reference);

      expect(invoice.receiver).not.to.be.undefined;
      if (invoice.receiver && iberdrolaClientInvoice.receiver) {
        expect(invoice.receiver.name).eq(iberdrolaClientInvoice.receiver.name);
        expect(invoice.receiver.document_country).eq(iberdrolaClientInvoice.receiver.document_country);
        expect(invoice.receiver.document).eq(iberdrolaClientInvoice.receiver.document);
        expect(invoice.receiver.address).not.to.be.undefined;
        if (invoice.receiver.address && iberdrolaClientInvoice.receiver.address) {
          expect(invoice.receiver.address.address).eq(iberdrolaClientInvoice.receiver.address.address);
          expect(invoice.receiver.address.postal_code).eq(iberdrolaClientInvoice.receiver.address.postal_code);
          expect(invoice.receiver.address.city).eq(iberdrolaClientInvoice.receiver.address.city);
        }
      }
      //expect(invoice.details).to.be.an('array').not.to.empty;
      if (invoice.details && iberdrolaClientInvoice.details) {
        for (let i = 0; i < invoice.details.length; i++) {
          expect(invoice.details[i].description).eq(iberdrolaClientInvoice.details[i].description);
          expect(invoice.details[i].quantity).eq(iberdrolaClientInvoice.details[i].quantity);
          expect(invoice.details[i].base).eq(iberdrolaClientInvoice.details[i].base);
          expect(invoice.details[i].price).eq(iberdrolaClientInvoice.details[i].price);
        }
      }

      expect(invoice.taxes).to.be.an('array').not.to.empty;
      if (invoice.taxes && iberdrolaClientInvoice.taxes) {
        for (let i = 0; i < invoice.taxes.length; i++) {
          expect(invoice.taxes[i].tax).eq(iberdrolaClientInvoice.taxes[i].tax);
          expect(invoice.taxes[i].quota).eq(iberdrolaClientInvoice.taxes[i].quota);
          expect(invoice.taxes[i].base).eq(iberdrolaClientInvoice.taxes[i].base);
          expect(invoice.taxes[i].percentage).eq(iberdrolaClientInvoice.taxes[i].percentage);
        }
      }
      expect(invoice.total).eq(iberdrolaClientInvoice.total);
      expect(invoice.finances).not.to.be.undefined;
      expect(invoice.finances).to.be.an('array').not.to.empty;
      if (invoice.finances && iberdrolaClientInvoice.finances) {
        for (let i = 0; i < invoice.finances.length; i++) {
          expect(invoice.finances[i].due_date).eql(iberdrolaClientInvoice.finances[i].due_date);
          expect(invoice.finances[i].amount).eq(iberdrolaClientInvoice.finances[i].amount);
        }
      }
    }
  }

  public static iberdrolaClienteTest(): void {
    const iberdrolaCliente1: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2018, 10, 19, 0, 0),
      reference: '21181119010327055',
      total: 585.87,
      receiver: {
        name: 'IN - BLAN MAQUINARIA DE HOSTELERIA, S.L.',
        document_country: 'ES',
        document: 'B47434253',
        address: {
          address: 'C/ COBALTO, 21, Bajo 1 47012 VALLADOLID',
          province: 'VALLADOLID',
        },
      },

      details: [
        {
          description: 'ENERGÍA',
          quantity: 1,
          price: 477.66,
          discount: 0,
          base: 477.66,
          vat: 21,
        },
        {
          description: 'SERVICIOS Y OTROS CONCEPTOS',
          quantity: 1,
          price: 6.53,
          discount: 0,
          base: 6.53,
          vat: 21,
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 101.68,
          base: 484.19,
          percentage: 21.0,
        },
      ],
      finances: [
        {
          iban: 'ES41 0049 6064 3129 1603 ****',
          due_date: new Date(2018, 10, 19, 0, 0),
          amount: 585.87,
        },
      ],
    };
    const iberdrolaCliente2: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2018, 10, 19, 0, 0),
      reference: '21181119010327056',
      total: 835.95,
      receiver: {
        name: 'IN - BLAN MAQUINARIA DE HOSTELERIA, S.L.',
        document_country: 'ES',
        document: 'B47434253',
        address: {
          address: 'C/ COBALTO, 21 47012 VALLADOLID',
          province: 'VALLADOLID',
        },
      },
      /* Comment details
          const expected = [
            {
              description: 'ENERGÍA',
              quantity: 1,
              price: 684.34,
              discount: 0,
              base: 684.34,
              vat: 21,
            },
            {
              description: 'SERVICIOS Y OTROS CONCEPTOS',
              quantity: 1,
              price: 6.53,
              discount: 0,
              base: 6.53,
              vat: 21,
            },
          ];
         End Details */
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 145.08,
          base: 690.87,
          percentage: 21.0,
        },
      ],
      finances: [
        {
          iban: 'ES41 0049 6064 3129 1603 ****',
          due_date: new Date(2018, 10, 19, 0, 0),
          amount: 835.95,
        },
      ],
    };

    describe('TEDI PDF PARSER TEST [IBERDROLA CLIENTES INVOICES]', () => {
      it('PARSE IBERDROLA INVOICE [Iberdrola_Clientes_I.pdf]', done => {
        const filename = 'test/resources/Iberdrola_Clientes_I.pdf';
        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, iberdrolaCliente1);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
      it('PARSE IBERDROLA INVOICE [Iberdrola_Clientes_II.pdf]', done => {
        const filename = 'test/resources/Iberdrola_Clientes_II.pdf';
        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, iberdrolaCliente2);
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
