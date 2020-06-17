import { expect } from 'chai';
import { Invoice, InvoiceStatus, TaxType } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class TapoTest {

    public static checkInvoice(invoice: Invoice, tapoInvoice: Invoice): void {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if(invoice) {
            expect(invoice.status).eq(tapoInvoice.status);
            expect(invoice.date).eql(tapoInvoice.date);
            expect(invoice.reference).eq(tapoInvoice.reference);

            expect(invoice.receiver).not.to.be.undefined;

            if (invoice.receiver && tapoInvoice.receiver) {
                expect(invoice.receiver.name).eq(tapoInvoice.receiver.name);
                expect(invoice.receiver.document_country).eq(tapoInvoice.receiver.document_country);
                expect(invoice.receiver.document).eq(tapoInvoice.receiver.document);

                expect(invoice.receiver.address).not.to.be.undefined;

                if (invoice.receiver.address && tapoInvoice.receiver.address) {
                  expect(invoice.receiver.address.address).eq(tapoInvoice.receiver.address.address);
                  // expect(invoice.receiver.address.postal_code).eq(tapoInvoice.receiver.address.postal_code);
                  // expect(invoice.receiver.address.city).eq(tapoInvoice.receiver.address.city);
                }
            }
            //expect(invoice.details).to.be.an('array').not.to.empty;
            if (invoice.details && tapoInvoice.details) {
                for (let i = 0; i < invoice.details.length; i++) {
                  expect(invoice.details[i].description).eq(tapoInvoice.details[i].description);
                  expect(invoice.details[i].quantity).eq(tapoInvoice.details[i].quantity);
                  expect(invoice.details[i].base).eq(tapoInvoice.details[i].base);
                  expect(invoice.details[i].price).eq(tapoInvoice.details[i].price);
                }
            }

            expect(invoice.taxes).to.be.an('array').not.to.empty;
            if (invoice.taxes && tapoInvoice.taxes) {
                for (let i = 0; i < invoice.taxes.length; i++) {
                    expect(invoice.taxes[i].tax).eq(tapoInvoice.taxes[i].tax);
                    expect(invoice.taxes[i].quota).eq(tapoInvoice.taxes[i].quota);
                    expect(invoice.taxes[i].base).eq(tapoInvoice.taxes[i].base);
                    expect(invoice.taxes[i].percentage).eq(tapoInvoice.taxes[i].percentage);
                }
            }
            expect(invoice.total).eq(tapoInvoice.total);
            expect(invoice.finances).not.to.be.undefined;
            expect(invoice.finances).to.be.an('array').not.to.empty;
            if (invoice.finances && tapoInvoice.finances) {
                for (let i = 0; i < invoice.finances.length; i++) {
                    expect(invoice.finances[i].due_date).eql(tapoInvoice.finances[i].due_date);
                    expect(invoice.finances[i].amount).eq(tapoInvoice.finances[i].amount);
                }
            }
        }
    }

    public static tapoTest() : void {
        const tapo1: Invoice = {
            status: InvoiceStatus.inbox,
            date: new Date(2019, 2, 1, 0, 0),
            reference: '18702172',
            total: 50.82,
            receiver: {
              name: 'MARIA JENNIFER VIQUE QUINDE',
              document_country: 'ES',
              document: 'ES52908166R',
              address: {
                address: 'PASEO DE LOS ARTILLEROS 6, 28032 MADRID, Madrid',
                // postal_code: '28032',
                // province: 'Madrid'
              },
            },
            // details: [
            //     {
            //     price: 30,
            //     quantity: 1,
            //     base: 30,
            //     discount: 0,
            //     description: 'SERVICIO FISCAL CONTABLE',
            //     },
            //     {
            //     price: 24,
            //     quantity: 1,
            //     base: 24,
            //     discount: 0,
            //     description: 'SERVICIO LABORAL',
            //     },
            //     {
            //     price: -12,
            //     quantity: 1,
            //     base: -12,
            //     discount: 0,
            //     description: 'Regularización Laboral Enero',
            //     },
            // ],
            taxes: [
              {
                tax: TaxType.IVA,
                quota: 8.82,
                base: 42.0,
                percentage: 21.0
              }
            ],
        };

        describe('TEDI PDF PARSER TEST [TAPO INVOICES]', () => {
            it('PARSE TAPO INVOICE [TAPO_I.pdf]', done => {
            const filename = 'test/resources/TU_ASESORIA_PERSONAL_ONLINE_1.pdf';

              TediPdfParser.extractFromFile(filename).subscribe(
                invoice => {
                  // console.log( JSON.stringify( invoice , null , 1 ) )
                  this.checkInvoice(invoice, tapo1);
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
