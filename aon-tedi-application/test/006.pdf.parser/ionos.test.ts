import { expect } from 'chai';
import { Invoice, InvoiceStatus, TaxType } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class IonosTest {
    public static checkInvoice(invoice: Invoice, ionosInvoice: Invoice): void {
        expect(invoice).not.to.be.null;
        expect(invoice).not.to.be.undefined;
        if (invoice) {
            expect(invoice.status).eq(ionosInvoice.status);
            expect(invoice.date).eql(ionosInvoice.date);
            expect(invoice.reference).eq(ionosInvoice.reference);

            expect(invoice.receiver).not.to.be.undefined;
            if (invoice.receiver && ionosInvoice.receiver) {
                expect(invoice.receiver.name).eq(ionosInvoice.receiver.name);
                expect(invoice.receiver.document_country).eq(ionosInvoice.receiver.document_country);
                expect(invoice.receiver.document).eq(ionosInvoice.receiver.document);
                
                expect(invoice.receiver.address).not.to.be.undefined;
                if (invoice.receiver.address && ionosInvoice.receiver.address) {
                    expect(invoice.receiver.address.address).eq(ionosInvoice.receiver.address.address);
                    expect(invoice.receiver.address.postal_code).eq(ionosInvoice.receiver.address.postal_code);
                    expect(invoice.receiver.address.city).eq(ionosInvoice.receiver.address.city);
                }
            }
            //expect(invoice.details).to.be.an('array').not.to.empty;
            if (invoice.details && ionosInvoice.details) {
            for (let i = 0; i < invoice.details.length; i++) {
                expect(invoice.details[i].description).eq(ionosInvoice.details[i].description);
                expect(invoice.details[i].quantity).eq(ionosInvoice.details[i].quantity);
                expect(invoice.details[i].base).eq(ionosInvoice.details[i].base);
                expect(invoice.details[i].price).eq(ionosInvoice.details[i].price);
            }
            }
            expect(invoice.taxes).to.be.an('array').not.to.empty;
            if (invoice.taxes && ionosInvoice.taxes) {
                for (let i = 0; i < invoice.taxes.length; i++) {
                    expect(invoice.taxes[i].tax).eq(ionosInvoice.taxes[i].tax);
                    expect(invoice.taxes[i].quota).eq(ionosInvoice.taxes[i].quota);
                    expect(invoice.taxes[i].base).eq(ionosInvoice.taxes[i].base);
                    expect(invoice.taxes[i].percentage).eq(ionosInvoice.taxes[i].percentage);
                }
            }
            expect(invoice.total).eq(ionosInvoice.total);
        }
    }
    
    public static ionosTest(): void {
    const ionos1: Invoice = {
        status: InvoiceStatus.inbox,
        date: new Date(2019, 5, 9, 0, 0),
        reference: 'B85049435',
        total: 1.21,
        receiver: {
        name: 'TRANSLOGIA DEVELOPMENT, S.L.',
        document_country: 'ES',
        document: 'B66941873',
        address: {
            address: 'Duque de Wellington 52',
            postal_code: '01010',
            city: 'Vitoria-Gasteiz',
        },
        },
        // details: [
        // {
        //     price: 11.00,
        //     quantity: 1,
        //     base: 11.00,
        //     discount: 0,
        //     description: 'Cuota Mensual',
        // },
        // {
        //     price: 10.00,
        //     quantity: 1,
        //     base: 10.00,
        //     discount: 0,
        //     description: 'Descuento',
        // },
        // ],
        taxes: [
        {
            tax: TaxType.IVA,
            quota: 0.21,
            base: 1.00,
            percentage: 21,
        },
        ],

    };

    const ionos2: Invoice = {
        status: InvoiceStatus.inbox,
        date: new Date(2019, 6, 20, 0, 0),
        reference: 'B85049435',
        total: 1,
        receiver: {
        name: 'ARRENDAMIENTOS IBAIONDO CB',
        document_country: 'ES',
        document: 'E01393842',
        address: {
            address: 'AVENIDA DEL ZADORRA 28',
            postal_code: '01013',
            city: 'Vitoria-Gasteiz',
        },
        },
        // details: [
        // {
        //     price: 11.00,
        //     quantity: 1,
        //     base: 11.00,
        //     discount: 0,
        //     description: 'Cuota Mensual',
        // },
        // {
        //     price: 10.00,
        //     quantity: 1,
        //     base: 10.00,
        //     discount: 0,
        //     description: 'Descuento',
        // },
        // ],
        taxes: [
        {
            tax: TaxType.IVA,
            quota: 0.17,
            base: 0.83,
            percentage: 21,
        },
        ],
    };
    
    describe('TEDI PDF PARSER TEST [IONOS INVOICES]', () => {
        it('PARSE IONOS INVOICE [IONOS_1.pdf]', done => {
        const filename = 'test/resources/Ionos_1.pdf';

            TediPdfParser.extractFromFile(filename).subscribe(
                invoice => {
                this.checkInvoice(invoice, ionos1);
                },
                error => {
                done(error);
                },
                () => {
                done();
                },
            );
        });

        it('PARSE IONOS INVOICE [IONOS_2.pdf]', done => {
        const filename = 'test/resources/Ionos_2.pdf';

            TediPdfParser.extractFromFile(filename).subscribe(
                invoice => {

                this.checkInvoice(invoice, ionos2);
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
