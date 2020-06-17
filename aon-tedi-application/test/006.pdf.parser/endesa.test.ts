import { expect } from 'chai';
import { Invoice, InvoiceStatus, TaxType } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class EndesaTest {

  public static checkInvoice(invoice: Invoice, endesaInvoice: Invoice): void {
    expect(invoice).not.to.be.null;
    expect(invoice).not.to.be.undefined;
    if (invoice) {
      expect(invoice.status).eq(endesaInvoice.status);
      expect(invoice.date).eql(endesaInvoice.date);
      expect(invoice.reference).eq(endesaInvoice.reference);

      expect(invoice.receiver).not.to.be.undefined;
      if (invoice.receiver && endesaInvoice.receiver) {
        expect(invoice.receiver.name).eq(endesaInvoice.receiver.name);
        expect(invoice.receiver.document_country).eq(endesaInvoice.receiver.document_country);
        expect(invoice.receiver.document).eq(endesaInvoice.receiver.document);
        expect(invoice.receiver.address).not.to.be.undefined;
        if (invoice.receiver.address && endesaInvoice.receiver.address) {
          expect(invoice.receiver.address.address).eq(endesaInvoice.receiver.address.address);
          expect(invoice.receiver.address.postal_code).eq(endesaInvoice.receiver.address.postal_code);
          expect(invoice.receiver.address.city).eq(endesaInvoice.receiver.address.city);
        }
      }
      //expect(invoice.details).to.be.an('array').not.to.empty;
      if (invoice.details && endesaInvoice.details) {
        for (let i = 0; i < invoice.details.length; i++) {
          expect(invoice.details[i].description).eq(endesaInvoice.details[i].description);
          expect(invoice.details[i].quantity).eq(endesaInvoice.details[i].quantity);
          expect(invoice.details[i].base).eq(endesaInvoice.details[i].base);
          expect(invoice.details[i].price).eq(endesaInvoice.details[i].price);
        }
      }

      expect(invoice.taxes).to.be.an('array').not.to.empty;
      if (invoice.taxes && endesaInvoice.taxes) {
        for (let i = 0; i < invoice.taxes.length; i++) {
          expect(invoice.taxes[i].tax).eq(endesaInvoice.taxes[i].tax);
          expect(invoice.taxes[i].quota).eq(endesaInvoice.taxes[i].quota);
          expect(invoice.taxes[i].base).eq(endesaInvoice.taxes[i].base);
          expect(invoice.taxes[i].percentage).eq(endesaInvoice.taxes[i].percentage);
        }
      }
      expect(invoice.total).eq(endesaInvoice.total);
      expect(invoice.finances).not.to.be.undefined;
      expect(invoice.finances).to.be.an('array').not.to.empty;
      if (invoice.finances && endesaInvoice.finances) {
        for (let i = 0; i < invoice.finances.length; i++) {
          expect(invoice.finances[i].due_date).eql(endesaInvoice.finances[i].due_date);
          expect(invoice.finances[i].amount).eq(endesaInvoice.finances[i].amount);
        }
      }
    }
  }
  public static endesaTest() : void {

    const endesa1: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2018, 11, 21 , 0  , 0),
      reference: 'B82846825',
      total: 49.44,
      receiver: {
        name: 'GARCIA-LOMAS&AS SL',
        document_country: 'ES',
        document: 'B23741903',
        address: {
          address: 'PARROCO JACINTO MUELA 8 BAJO',
          postal_code: '23200',
          city: 'LA CAROLINA'
        }
      },
      details: [
        {
            price: 27.78,
            quantity: 1,
            base: 27.78,
            discount: 0,
            description: 'Por potencia contratada ',
            },
            {
            price: 10.28,
            quantity: 1,
            base: 10.28,
            discount: 0,
            description: 'Por energía consumida ',
            },
            {
            price: 1.95,
            quantity: 1,
            base: 1.95,
            discount: 0,
            description: 'Impuesto electricidad ',
            },
            {
            price: 0.85,
            quantity: 1,
            base: 0.85,
            discount: 0,
            description: 'Alquiler equipos de medida y control ',
            },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 8.58,
          base: 40.86,
          percentage: 21
        },
      ],
      finances: [
        {
          due_date:  new Date(2018, 11, 28 , 0  , 0),
          amount: 49.44
        }
      ]
    };

    const endesa4 : Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 0, 18, 0, 0),
      reference: 'A81948077',
      total: 313.89,
      receiver: {
        name: 'ARUNDEL S.L',
        document_country: 'ES',
        document: 'B87951026',
        address: {
          address: 'TRAMONTANA 21-A 3 HUMERA-SOMOSAGUAS-PRADO DEL RE',
          postal_code: '28223',
          //city: 'LA CAROLINA'
        }
      },
      details: [
          {
            price: 20.94,
            quantity: 1,
            base: 20.94,
            discount: 0,
            description: 'Fijo',
            },
            {
            price: 241.48,
            quantity: 1,
            base: 241.48,
            discount: 0,
            description: 'Variable',
            },
            {
            price: 13.19,
            quantity: 1,
            base: 13.19,
            discount: 0,
            description: 'Descuentos',
            },
            {
            price: 0.57,
            quantity: 1,
            base: 0.57,
            discount: 0,
            description: 'Otros',
            },
            {
            price: 10.75,
            quantity: 1,
            base: 10.75,
            discount: 0,
            description: 'Impuestos',
            },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          base: 259.41,
          quota: 54.48,
          percentage: 21
        }
      ],
      finances: [
        {
          due_date: new Date(2019, 0, 25, 0, 0),
          amount: 313.89
        }
      ]
    };

    describe('TEDI PDF PARSER TEST [ENDESA INVOICES]', () => {
      it('PARSE ENDESA INVOICE [ENDESA-I.pdf]', done => {
        const filename = 'test/resources/Endesa_1.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, endesa1);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });

      it('PARSE ENDESA INVOICE [ENDESA-IV.pdf]', done => {
        const filename = 'test/resources/Endesa_4.pdf'

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            // console.log(JSON.stringify(invoice, null, 1));
            this.checkInvoice(invoice, endesa4);
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
