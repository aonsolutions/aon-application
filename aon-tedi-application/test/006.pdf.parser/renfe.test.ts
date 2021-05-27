import { expect } from 'chai';
import { Invoice, InvoiceStatus, TaxType } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class RenfeTest {

  public static checkInvoice(invoice: Invoice, renfeInvoice: Invoice): void {
    expect(invoice).not.to.be.null;
    expect(invoice).not.to.be.undefined;
    if (invoice) {
      expect(invoice.status).eq(renfeInvoice.status);
      expect(invoice.date).eql(renfeInvoice.date);
      expect(invoice.reference).eq(renfeInvoice.reference);

      expect(invoice.receiver).not.to.be.undefined;
      if (invoice.receiver && renfeInvoice.receiver) {
        expect(invoice.receiver.name).eq(renfeInvoice.receiver.name);
        expect(invoice.receiver.document_country).eq(renfeInvoice.receiver.document_country);
        expect(invoice.receiver.document).eq(renfeInvoice.receiver.document);
        expect(invoice.receiver.address).not.to.be.undefined;
        if (invoice.receiver.address && renfeInvoice.receiver.address) {
          expect(invoice.receiver.address.address).eq(renfeInvoice.receiver.address.address);
          expect(invoice.receiver.address.postal_code).eq(renfeInvoice.receiver.address.postal_code);
          expect(invoice.receiver.address.city).eq(renfeInvoice.receiver.address.city);
        }
      }
     // expect(invoice.details).to.be.an('array').not.to.empty;
      if (invoice.details && renfeInvoice.details) {
        for (let i = 0; i < invoice.details.length; i++) {
          expect(invoice.details[i].description).eq(renfeInvoice.details[i].description);
          expect(invoice.details[i].quantity).eq(renfeInvoice.details[i].quantity);
          expect(invoice.details[i].base).eq(renfeInvoice.details[i].base);
          expect(invoice.details[i].price).eq(renfeInvoice.details[i].price);
        }
      }

      expect(invoice.taxes).to.be.an('array').not.to.empty;
      if (invoice.taxes && renfeInvoice.taxes) {
        for (let i = 0; i < invoice.taxes.length; i++) {
          expect(invoice.taxes[i].tax).eq(renfeInvoice.taxes[i].tax);
          expect(invoice.taxes[i].quota).eq(renfeInvoice.taxes[i].quota);
          expect(invoice.taxes[i].base).eq(renfeInvoice.taxes[i].base);
          expect(invoice.taxes[i].percentage).eq(renfeInvoice.taxes[i].percentage);
        }
      }
      expect(invoice.total).eq(renfeInvoice.total);
      expect(invoice.finances).not.to.be.undefined;
      expect(invoice.finances).to.be.an('array').not.to.empty;
      if (invoice.finances && renfeInvoice.finances) {
        for (let i = 0; i < invoice.finances.length; i++) {
          expect(invoice.finances[i].due_date).eql(renfeInvoice.finances[i].due_date);
          expect(invoice.finances[i].amount).eq(renfeInvoice.finances[i].amount);
        }
      }
    }
  }

  public static renfeTest() : void {

    const renfe1: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 0, 18, 0, 0),
      reference: 'VJ00000006889621',
      total: 76.4,
      receiver: {
        name: 'LUDUS TECH SL',
        document_country: 'ES',
        document: 'B95661856',
        address: {
          address: 'CALLE COSTA 12 1 DERECHA, 48010 BILBAO, VIZCAYA',
          postal_code: '48010',
          province: 'VIZCAYA'
        }
      },
      details: [
        {
          price: 69.45,
          quantity: 1,
          base: 69.45,
          discount: 0,
          description: 'FACTURA DEL BILLETE: 7784000954965 - 05070 - MADRID-PUERTA DE ATOCHA - VALENCIA JOAQUIN SOROLLA - F. VIAJE: 21-01-2019 - LOC: 524TL7',
        }
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 6.95,
          base: 69.45,
          percentage: 10.0
        }
      ],
      finances: [
        {
          due_date: new Date(2019, 0, 18, 0, 0),
          amount: 76.4
        }
      ]
    };

    const renfe2: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 1, 18, 0, 0),
      reference: 'VJ00000007071424',
      total: 30.5,
      receiver: {
        name: 'INNOVACIÓN COLABORATIVA, S.L.',
        document_country: 'ES',
        document: 'B87059358',
        address: {
          address: 'CALLE VANDERGOTEN 1, 28014 MADRID, MADRID',
          postal_code: '28014',
          province: 'MADRID'
        }
      },
      details: [
        {
          price: 27.73,
          quantity: 1,
          base: 27.73,
          discount: 0,
          description:
            'FACTURA DEL BILLETE: 7617300951580 - 03081 - MADRID-PUERTA DE ATOCHA - BARCELONA-SANTS - F. VIAJE: 11-02-2019 - LOC: 4M22VR',
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 2.77,
          base: 27.73,
          percentage: 10.0
        }
      ],
      finances: [
        {
          due_date: new Date(2019, 1, 18, 0, 0),
          amount: 30.5
        }
      ]
    };

    const renfe3: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 0, 8, 0, 0),
      reference: 'VJ00000006817227',
      total: 22.4,
      receiver: {
        name: 'GARINTER SUPPLIERS GROUP S.L.',
        document_country: 'ES',
        document: 'B87756490',
        address: {
          address: 'CALLE JORGE JUAN 2 6 4TO, 28703 SAN SEBASTIAN DE LOS REYES, MADRID',
          postal_code: '28703',
          province: 'MADRID'
        }
      },
      details: [
        {
          price: 20.36,
          quantity: 1,
          base: 20.36,
          discount: 0,
          description:
            'FACTURA DEL BILLETE: 7051100958993 - 01341 - BARCELONA-SANTS - VALENCIA JOAQUIN SOROLLA - F. VIAJE: 08-01-2019 - LOC: FX5FLK',
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 2.04,
          base: 20.36,
          percentage: 10.0
        }
      ],
      finances: [
        {
          due_date: new Date(2019, 0, 8, 0, 0),
          amount: 22.4
        }
      ]
    };

    const renfe4: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 4, 14, 0, 0),
      reference: 'VJ00000007549566',
      total: 127.1,
      receiver: {
        name: 'THE BRAIN WASH AGENCY S.L',
        document_country: 'ES',
        document: 'B88257910',
        address: {
          address: 'ALAMEDA DE LA PEDRIZA 6, 28210 VALDEMORILLO, MADRID',
          postal_code: '28210',
          province: 'MADRID'
        }
      },
      details: [
        {
          price: 115.55,
          quantity: 1,
          base: 115.55,
          discount: 0,
          description:
            'FACTURA DEL BILLETE: 7530900977667 - 03170 - BARCELONA-SANTS - MADRID-PUERTA DE ATOCHA - F. VIAJE: 28-03-2019 - LOC: LJCDTW',
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 11.55,
          base: 115.55,
          percentage: 10.0
        }
      ],
      finances: [
        {
          due_date: new Date(2019, 4, 14, 0, 0),
          amount: 127.1
        }
      ]
    };

    const renfe5: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 3, 9, 0, 0),
      reference: 'VJ00000007379729',
      total: 73.2,
      receiver: {
        name: '',
        document_country: 'ES',
        document: '49030018K',
        address: {
          address: 'CALLE MALAGA 61 NULL, 41702 DOS HERMANAS, SEVILLA',
          postal_code: '41702',
          province: 'SEVILLA'
        }
      },
      details: [
        {
          price: 66.55,
          quantity: 1,
          base: 66.55,
          discount: 0,
          description:
            'FACTURA DEL BILLETE: 7881400985779 - 02200 - MADRID-PUERTA DE ATOCHA - SEVILLA-SANTA JUSTA - F. VIAJE: 10-04-2019 - LOC: B22R52',
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 6.65,
          base: 66.55,
          percentage: 10.0
        }
      ],
      finances: [
        {
          due_date: new Date(2019, 3, 9, 0, 0),
          amount: 73.2
        }
      ]
    };

    const renfe6: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 3, 22, 0, 0),
      reference: 'VJ00000007432221',
      total: 52.2,
      receiver: {
        name: 'GRUPO TECNOLOGICO ARBINOVA SL',
        document_country: 'ES',
        document: 'B32440927',
        address: {
          address: 'C/VALDOMIÑO Nº 1, 32002 BARBADÁS, ORENSE',
          postal_code: '32002',
          province: 'ORENSE'
        }
      },
      details: [
        {
          price: 47.45,
          quantity: 1,
          base: 47.45,
          discount: 0,
          description:
            'FACTURA DEL BILLETE: 7941000984612 - 02260 - MADRID-PUERTA DE ATOCHA - SEVILLA-SANTA JUSTA - F. VIAJE: 12-04-2019 - LOC: 95SCZF',
        }
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 4.75,
          base: 47.45,
          percentage: 10.0
        }
      ],
      finances: [
        {
          due_date: new Date(2019, 3, 22, 0, 0),
          amount: 52.2
        }
      ]
    };

    describe('TEDI PDF PARSER TEST [RENFE INVOICES]', () => {
      it('PARSE RENFE INVOICE [RENFE_I.pdf]', done => {
        const filename = 'test/resources/RENFE_I.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, renfe1);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
      it('PARSE RENFE INVOICE [RENFE_II.pdf]', done => {
        const filename = 'test/resources/RENFE_II.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, renfe2);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
      it('PARSE RENFE INVOICE [RENFE_III.pdf]', done => {
        const filename = 'test/resources/RENFE_III.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, renfe3);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
      it('PARSE RENFE INVOICE [RENFE_IV.pdf]', done => {
        const filename = 'test/resources/RENFE_IV.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, renfe4);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
      it('PARSE RENFE INVOICE [RENFE_V.pdf]', done => {
        const filename = 'test/resources/RENFE_V.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, renfe5);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
      it('PARSE RENFE INVOICE [RENFE_VI.pdf]', done => {
        const filename = 'test/resources/RENFE_VI.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, renfe6);
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
