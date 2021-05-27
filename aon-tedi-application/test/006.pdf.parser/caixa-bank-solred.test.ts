import { expect } from 'chai';
import { Invoice, InvoiceStatus, TaxType } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class CaixaBankSolredTest {
  public static checkInvoice(invoice: Invoice, caixaInvoice: Invoice): void {
    expect(invoice).not.to.be.null;
    expect(invoice).not.to.be.undefined;
    if (invoice) {
      expect(invoice.status).eq(caixaInvoice.status);
      expect(invoice.date).eql(caixaInvoice.date);
      expect(invoice.reference).eq(caixaInvoice.reference);

      expect(invoice.receiver).not.to.be.undefined;
      if (invoice.receiver && caixaInvoice.receiver) {
        expect(invoice.receiver.name).eq(caixaInvoice.receiver.name);
        expect(invoice.receiver.document_country).eq(caixaInvoice.receiver.document_country);
        expect(invoice.receiver.document).eq(caixaInvoice.receiver.document);
        expect(invoice.receiver.address).not.to.be.undefined;
        if (invoice.receiver.address && caixaInvoice.receiver.address) {
          expect(invoice.receiver.address.address).eq(caixaInvoice.receiver.address.address);
          expect(invoice.receiver.address.postal_code).eq(caixaInvoice.receiver.address.postal_code);
          expect(invoice.receiver.address.city).eq(caixaInvoice.receiver.address.city);
        }
      }
      // expect(invoice.details).to.be.an('array').not.to.empty;
      if (invoice.details && caixaInvoice.details) {
        for (let i = 0; i < invoice.details.length; i++) {
          expect(invoice.details[i].description).eq(caixaInvoice.details[i].description);
          expect(invoice.details[i].quantity).eq(caixaInvoice.details[i].quantity);
          expect(invoice.details[i].base).eq(caixaInvoice.details[i].base);
          expect(invoice.details[i].price).eq(caixaInvoice.details[i].price);
        }
      }

      expect(invoice.taxes).to.be.an('array').not.to.empty;
      if (invoice.taxes && caixaInvoice.taxes) {
        for (let i = 0; i < invoice.taxes.length; i++) {
          expect(invoice.taxes[i].tax).eq(caixaInvoice.taxes[i].tax);
          expect(invoice.taxes[i].quota).eq(caixaInvoice.taxes[i].quota);
          expect(invoice.taxes[i].base).eq(caixaInvoice.taxes[i].base);
          expect(invoice.taxes[i].percentage).eq(caixaInvoice.taxes[i].percentage);
        }
      }
      expect(invoice.total).eq(caixaInvoice.total);
      expect(invoice.finances).not.to.be.undefined;
      expect(invoice.finances).to.be.an('array').not.to.empty;
      if (invoice.finances && caixaInvoice.finances) {
        for (let i = 0; i < invoice.finances.length; i++) {
          expect(invoice.finances[i].due_date).eql(caixaInvoice.finances[i].due_date);
          expect(invoice.finances[i].amount).eq(caixaInvoice.finances[i].amount);
          expect(invoice.finances[i].iban).eq(caixaInvoice.finances[i].iban);
        }
      }
    }
  }

  public static solredTest(): void {
    const solred1: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 4, 31),
      reference: 'CX/19/0000321245',
      total: 126.24,
      receiver: {
        name: 'AON SOLUTIONS SL',
        document_country: 'ES',
        document: 'B01487271',
        address: {
          address: 'DUQU E DE WELL INGT ON 52',
          postal_code: '01010',
          city: 'VITORIA',
        },
      },

      details: [
        {
          price: 52.18,
          quantity: 1,
          base: 52.18,
          description: 'E.S. QUINTANAPALLAII QUINTANAPALLA EFITEC 95 N',
          discount: 1.04,
        },
        {
          price: 76.63,
          quantity: 1,
          base: 76.63,
          description: 'LOPIDANA (M.D.) LOPIDANA EFITEC 95 N',
          discount: 1.53,
        },
      ],

      taxes: [
        {
          tax: TaxType.IVA,
          quota: 21.91,
          base: 104.33,
          percentage: 21.0,
        },
      ],

      finances: [
        {
          iban: 'ES76 2100 5849 4102 0004 9423',
          due_date: new Date(2019, 5, 1, 0, 0),
          amount: 126.24,
        },
      ],
    };
    const solred2: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 6, 4),
      reference: 'CX/19/0000506591',
      total: 96.96,
      receiver: {
        name: 'AON SOLUTIONS SL',
        document_country: 'ES',
        document: 'B01487271',
        address: {
          address: 'DUQU E DE WELL INGT ON 52',
          postal_code: '01010',
          city: 'VITORIA',
        },
      },
      details: [
        {
          price: 54.0,
          quantity: 1,
          base: 54.0,
          description: 'E.S LAS TRIANAS VITORIA EFITEC 95 N',
          discount: 1.08,
        },
        {
          price: 5.9,
          quantity: 1,
          base: 5.9,
          description: 'E.S LAS TRIANAS VITORIA LAVADOS',
          discount: 0,
        },
        {
          price: 38.92,
          quantity: 1,
          base: 38.92,
          description: 'E.S. TUDELA NAVARRA TUDELA EFITEC 95 N',
          discount: 0.78,
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 16.82,
          base: 80.14,
          percentage: 21.0,
        },
      ],

      finances: [
        {
          iban: 'ES76 2100 5849 4102 0004 9423',
          due_date: new Date(2019, 6, 1, 0, 0),
          amount: 96.96,
        },
      ],
    };
    const solred3: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 8, 4),
      reference: 'CX/19/0000697970',
      total: 284.9,
      receiver: {
        name: 'AON SOLUTIONS SL',
        document_country: 'ES',
        document: 'B01487271',
        address: {
          address: 'DUQU E DE WELL INGT ON 52',
          postal_code: '01010',
          city: 'VITORIA',
        },
      },
      details: [
        {
          price: 33.64,
          quantity: 1,
          base: 33.64,
          description: 'E.S. QUINTANAPALLAII QUINTANAPALLA EFITEC 95 N',
          discount: 0.67,
        },
        {
          price: 73.16,
          quantity: 1,
          base: 73.16,
          description: 'ES GASOLEOS MARMOLEJO SL MARMOLEJO EFITEC 95 N',
          discount: 1.46,
        },
        {
          price: 18.08,
          quantity: 1,
          base: 18.08,
          description: 'E.S. LUCENA AUTOVIA M.D. LUCENA EFITEC 95 N',
          discount: 0.36,
        },
        {
          price: 40.0,
          quantity: 1,
          base: 40.0,
          description: 'TOTANA ALHAMA DE MUR EFITEC 95 N',
          discount: 0.8,
        },
        {
          price: 45.31,
          quantity: 1,
          base: 45.31,
          description: 'GRUPO MAICAS PEIRO S.L.U. SOT DE FERRER EFITEC 95 N',
          discount: 0.91,
        },
        {
          price: 80.52,
          quantity: 1,
          base: 80.52,
          description: 'LOPIDANA (M.D.) LOPIDANA EFITEC 95 N',
          discount: 1.61,
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 49.44,
          base: 235.46,
          percentage: 21.0,
        },
      ],

      finances: [
        {
          iban: 'ES76 2100 5849 4102 0004 9423',
          due_date: new Date(2019, 8, 1, 0, 0),
          amount: 284.9,
        },
      ],
    };
    const solred4: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 0, 31),
      reference: 'CX/19/0000056978',
      total: 136.23,
      receiver: {
        name: 'AON SOLUTIONS SL',
        document_country: 'ES',
        document: 'B01487271',
        address: {
          address: 'DUQU E DE WELL INGT ON 52',
          postal_code: '01010',
          city: 'VITORIA',
        },
      },
      details: [
        {
          price: 66.0,
          quantity: 1,
          base: 66.0,
          description: 'ES ECHEVARRIA SL VITORIA EFITEC 95 N',
          discount: 1.32,
        },

        {
          price: 73.01,
          quantity: 1,
          base: 73.01,
          description: 'LOPIDANA MD LOPIDANA EFITEC 95 N',
          discount: 1.46,
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 23.64,
          base: 112.59,
          percentage: 21.0,
        },
      ],

      finances: [
        {
          iban: 'ES76 2100 5849 4102 0004 9423',
          due_date: new Date(2019, 1, 1, 0, 0),
          amount: 136.23,
        },
      ],
    };
    const solred5: Invoice = {
      status: InvoiceStatus.inbox,
      date: new Date(2019, 2, 31),
      reference: 'CX/19/0000230300',
      total: 107.56,
      receiver: {
        name: 'AON SOLUTIONS SL',
        document_country: 'ES',
        document: 'B01487271',
        address: {
          address: 'DUQU E DE WELL INGT ON 52',
          postal_code: '01010',
          city: 'VITORIA',
        },
      },
      details: [
        {
          price: 31.71,
          quantity: 1,
          base: 31.71,
          description: 'QUINTANAPALLA II QUINTANAPALLA EFITEC 95 N',
          discount: 0.63,
        },
        {
          price: 71.0,
          quantity: 1,
          base: 71.0,
          description: 'E.S LAS TRIANAS VITORIA EFITEC 95 N',
          discount: 1.42,
        },
        {
          price: 6.9,
          quantity: 1,
          base: 6.9,
          description: 'E.S LAS TRIANAS VITORIA LAVADOS',
          discount: 0,
        },
      ],
      taxes: [
        {
          tax: TaxType.IVA,
          quota: 18.66,
          base: 88.9,
          percentage: 21.0,
        },
      ],

      finances: [
        {
          iban: 'ES76 2100 5849 4102 0004 9423',
          due_date: new Date(2019, 3, 1, 0, 0),
          amount: 107.56,
        },
      ],
    };

    describe('TEDI PDF PARSER TEST [ CAIXABANK SOLRED ]', () => {
      it('PARSE  CAIXA_BANK_SOLRED INVOICE [ CAIXA_BANK_SOLRED_I.pdf ]', done => {
        const filename = 'test/resources/CAIXA_BANK_SOLRED_I.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, solred1);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });

      it('PARSE  CAIXA_BANK_SOLRED INVOICE [ CAIXA_BANK_SOLRED_II.pdf ]', done => {
        const filename = 'test/resources/CAIXA_BANK_SOLRED_II.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, solred2);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
      it('PARSE  CAIXA_BANK_SOLRED INVOICE [ CAIXA_BANK_SOLRED_III.pdf ]', done => {
        const filename = 'test/resources/CAIXA_BANK_SOLRED_III.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, solred3);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });

      it('PARSE  CAIXA_BANK_SOLRED INVOICE [ CAIXA_BANK_SOLRED_IV.pdf ]', done => {
        const filename = 'test/resources/CAIXA_BANK_SOLRED_IV.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, solred4);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });
      it('PARSE  CAIXA_BANK_SOLRED INVOICE [ CAIXA_BANK_SOLRED_V.pdf ]', done => {
        const filename = 'test/resources/CAIXA_BANK_SOLRED_V.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
            this.checkInvoice(invoice, solred5);
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
