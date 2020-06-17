import { expect } from 'chai';
import { Invoice, InvoiceStatus, TaxType } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class EuskaltelTest {
  public static checkInvoice(invoice: Invoice, euskaltelInvoice: Invoice): void {
    expect(invoice).not.to.be.null;
    expect(invoice).not.to.be.undefined;
    if (invoice) {
      expect(invoice.status).eq(euskaltelInvoice.status);
      expect(invoice.date).eql(euskaltelInvoice.date);
      expect(invoice.reference).eq(euskaltelInvoice.reference);

      expect(invoice.receiver).not.to.be.undefined;
      if (invoice.receiver && euskaltelInvoice.receiver) {
        expect(invoice.receiver.name).eq(euskaltelInvoice.receiver.name);
        expect(invoice.receiver.document_country).eq(euskaltelInvoice.receiver.document_country);
        expect(invoice.receiver.document).eq(euskaltelInvoice.receiver.document);
        expect(invoice.receiver.address).not.to.be.undefined;
        if (invoice.receiver.address && euskaltelInvoice.receiver.address) {
          expect(invoice.receiver.address.address).eq(euskaltelInvoice.receiver.address.address);
          expect(invoice.receiver.address.postal_code).eq(euskaltelInvoice.receiver.address.postal_code);
          expect(invoice.receiver.address.city).eq(euskaltelInvoice.receiver.address.city);
        }
      }
      //expect(invoice.details).to.be.an('array').not.to.empty;
      if (invoice.details && euskaltelInvoice.details) {
        for (let i = 0; i < invoice.details.length; i++) {
          expect(invoice.details[i].description).eq(euskaltelInvoice.details[i].description);
          expect(invoice.details[i].quantity).eq(euskaltelInvoice.details[i].quantity);
          expect(invoice.details[i].base).eq(euskaltelInvoice.details[i].base);
          expect(invoice.details[i].price).eq(euskaltelInvoice.details[i].price);
        }
      }

      expect(invoice.taxes).to.be.an('array').not.to.empty;
      if (invoice.taxes && euskaltelInvoice.taxes) {
        for (let i = 0; i < invoice.taxes.length; i++) {
          expect(invoice.taxes[i].tax).eq(euskaltelInvoice.taxes[i].tax);
          expect(invoice.taxes[i].quota).eq(euskaltelInvoice.taxes[i].quota);
          expect(invoice.taxes[i].base).eq(euskaltelInvoice.taxes[i].base);
          expect(invoice.taxes[i].percentage).eq(euskaltelInvoice.taxes[i].percentage);
        }
      }
      expect(invoice.total).eq(euskaltelInvoice.total);
      //expect(invoice.finances).not.to.be.undefined;
      //expect(invoice.finances).to.be.an('array').not.to.empty;
      if (invoice.finances && euskaltelInvoice.finances) {
        for (let i = 0; i < invoice.finances.length; i++) {
          expect(invoice.finances[i].due_date).eql(euskaltelInvoice.finances[i].due_date);
          expect(invoice.finances[i].amount).eq(euskaltelInvoice.finances[i].amount);
        }
      }
    }
  }

  public static euskaltelTest(): void {
    const euskatel5: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 4, 22, 0, 0),
      reference: 'A48766695',
      total: 119.99,
      receiver: {
        name: 'AITOR ZUBIZARRETA UNCETA',
        document_country: 'ES',
        document: '15397670K',
        address: {
          address: 'CALLE URKI KURUTZEKUA 004 05 B',
          postal_code: '20600',
          city: 'EIBAR',
        },
      },
      details: [
        {
          price: 18.2562,
          quantity: 1,
          base: 18.2562,
          discount: 0,
          description: 'Finkoa/Fijo',
        },
        {
          price: 25.5372,
          quantity: 1,
          base: 25.5372,
          discount: 0,
          description: 'Internet/Internet',
        },
        {
          price: 0.0,
          quantity: 1,
          base: 0.0,
          discount: 0,
          description: 'Televisión/Televisión',
        },
        {
          price: 55.3719,
          quantity: 1,
          base: 55.3719,
          discount: 0,
          description: 'Movil/Móvil',
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 20.8247,
          base: 99.1653,
          percentage: 21.0,
        },
      ],
      finances: [
        {
          due_date: new Date(2019, 1, 11, 0, 0),
          amount: 119.99,
        },
      ],
    };
    const euskatel6: Invoice = {
        status: InvoiceStatus.inbox,
        date: new Date(2019, 4, 23, 0, 0),
        reference: 'A48766695',
        total: 157.1,
        receiver: {
          name: 'SANTANA 27, S.L.',
          document_country: 'ES',
          document: 'B95308094',
          address: {
            address: 'CALLE TELLERIA 027 PB',
            postal_code: '48004',
            city: 'BILBAO',
          },
        },
        details: [
          {
            price: 2.9766,
            quantity: 1,
            base: 76.2508,
            discount: 76.2508,
            description: 'Finkoa/Fijo',
          },
          {
            price: 30.4959,
            quantity: 1,
            base: 33.0459,
            discount: 2.55,
            description: 'Internet/Internet',
          },
          {
            price: 37.1901,
            quantity: 1,
            base: 37.1901,
            discount: 0,
            description: 'Movil/Móvil',
          },
          {
            price: 59.1733,
            quantity: 1,
            base: 64.0,
            discount: 4.8267,
            description: 'Otros/Otros',
          },
        ],
        taxes: [
          {
            tax: TaxType.IVA,
            quota: 27.2655,
            base: 129.8359,
            percentage: 21.0,
          },
        ],
      };
  
    const euskatel8: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 0, 22, 0, 0),
      reference: 'A48766695',
      total: 46.59,
      receiver: {
        name: 'ERGOSFERA TRAINING AND MANAGERS S.L',
        document_country: 'ES',
        document: 'B95792495',
        address: {
          address: 'PLAZA MOLINAR 006 PB',
          postal_code: '48192',
          city: 'GORDEXOLA',
        },
      },
      details: [
        {
          price: 10.0,
          quantity: 1,
          base: 20.7355,
          discount: 10.7355,
          description: 'Finkoa/Fijo',
        },
        {
          price: 25.4545,
          quantity: 1,
          base: 25.4545,
          discount: 0,
          description: 'Internet/Internet',
        },
        {
          price: 3.049,
          quantity: 1,
          base: 15.7025,
          discount: 15.7025,
          description: 'Movil/Móvil',
        },
        {
          price: 0.0,
          quantity: 1,
          base: 0.0,
          discount: 0,
          description: 'Otros/Otros',
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 8.0857,
          base: 38.5035,
          percentage: 21.0,
        },
      ],
    };
   
    describe('TEDI PDF PARSER TEST [ EUSKALTEL ]', () => {
      it('PARSE EUSKALTEL INVOICE [ EUSKALTEL_5.pdf ]', done => {
        const filename = 'test/resources/Euskaltel_5.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, euskatel5);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
      it('PARSE EUSKALTEL INVOICE [ EUSKALTEL_6.pdf ]', done => {
        const filename = 'test/resources/Euskaltel_6.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, euskatel6);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });

      it('PARSE EUSKALTEL INVOICE [ EUSKALTEL_8.pdf ]', done => {
        const filename = 'test/resources/Euskaltel_8.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, euskatel8);
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
