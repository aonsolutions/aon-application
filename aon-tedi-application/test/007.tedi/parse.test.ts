import request from 'supertest';
import { assert, use } from 'chai';
use(require('chai-datetime'));
import { Invoice, InvoiceType, TaxType } from '../../src/tedi-ewok/TediEwok';

const myFunctions = require('../../src/index');

export class TediParseTest {

  public static parseTest() : void {

    describe(`Parse`, () => {

		it.skip(`Translogia`, () => {
      return request(myFunctions.parse)
        .post(`/F01131978`)
				.field('company', 'B01487271')
				.attach('invoice_file', './test/resources/Translogia.pdf')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					assert.equal(invoice.total, 33802.24);
					assert.equal(invoice.sender!.document , 'B01487271');
					assert.equal(invoice.receiver!.document , 'B01487271');
					assert.equalDate(new Date(invoice.date!),new Date(2019,11,5));


        });
    });

		it(`PARKING II`, () => {
      return request(myFunctions.parse)
        .post(`/F01131978`)
				.field('company', 'F01131978')
				.attach('invoice_file', './test/resources/parking II.jpg')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					assert.equal(invoice.total, 2.8);
					assert.equal(invoice.type, InvoiceType.TICKET);
					assert.equal(invoice.sender!.document , 'A01046150');
					assert.equal(invoice.receiver!.document , 'F01131978');
					assert.equalDate(new Date(invoice.date!),new Date(2019,11,10));


        });
    });

		it(`PARKING`, () => {
      return request(myFunctions.parse)
        .post(`/F01131978`)
				.field('company', 'F01131978')
				.attach('invoice_file', './test/resources/parking.jpg')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					assert.equal(invoice.total, 2.8);
					assert.equal(invoice.type, InvoiceType.TICKET);
					assert.equal(invoice.sender!.document , 'A01046150');
					assert.equal(invoice.receiver!.document , 'F01131978');
					assert.equalDate(new Date(invoice.date!),new Date(2019,11,10));


        });
    });

		it.skip(`SoloOptical`, () => {
      return request(myFunctions.parse)
        .post(`/F01131978`)
				.field('company', 'F01131978')
				.attach('invoice_file', './test/resources/SoloOptical.jpg')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					assert.equal(invoice.total, 40.00);
					assert.equal(invoice.type, InvoiceType.TICKET);
					assert.equal(invoice.sender!.document , 'B92470335');
					assert.equal(invoice.receiver!.document , 'F01131978');
					assert.equalDate(new Date(invoice.date!),new Date(2019,10,29));


        });
    });

		it(`RITUALS`, () => {
      return request(myFunctions.parse)
        .post(`/F01131978`)
				.field('company', 'F01131978')
				.attach('invoice_file', './test/resources/RITUALS.jpeg')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					assert.equal(invoice.total, 40.8);
					assert.equal(invoice.type, InvoiceType.TICKET);
					assert.equal(invoice.sender!.document , 'B64936610');
					assert.equal(invoice.receiver!.document , 'F01131978');
					assert.equalDate(new Date(invoice.date!),new Date(2019,11,9));

					assert.equal( invoice.taxes![0].base , 33.72 );
					assert.equal( invoice.taxes![0].quota , 7.08 );
					assert.equal( invoice.taxes![0].tax , TaxType.IVA);


        });
    });

		it.skip(`Eroski`, () => {
      return request(myFunctions.parse)
        .post(`/F01131978`)
				.field('company', 'F01131978')
				.attach('invoice_file', './test/resources/Eroski.jpeg')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					assert.equal(invoice.total, 17.02);
					assert.equal(invoice.type, InvoiceType.TICKET);
					assert.equal(invoice.sender!.document , 'F20033361');
					assert.equal(invoice.receiver!.document , 'F01131978');
					assert.equalDate(new Date(invoice.date!),new Date(2019,11,5));

					assert.equal( invoice.taxes![0].base , 14.07 );
					assert.equal( invoice.taxes![0].quota , 2.95 );
					assert.equal( invoice.taxes![0].tax , TaxType.IVA);
        });
    });

		it.skip(`Gasolinera`, () => {
      return request(myFunctions.parse)
        .post(`/F01131978`)
				.field('company', 'F01131978')
				.attach('invoice_file', './test/resources/Gasolinera.jpg')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					assert.equal(invoice.total, 60.27);
					assert.equal(invoice.type, InvoiceType.TICKET);
					assert.equal(invoice.sender!.document , 'B48231351');
					assert.equal(invoice.receiver!.document , 'F01131978');
					assert.equalDate(new Date(invoice.date!),new Date(2019,11,9));

					assert.equal( invoice.taxes![0].base , 49.81 );
					assert.equal( invoice.taxes![0].quota , 10.46 );
					assert.equal( invoice.taxes![0].tax , TaxType.IVA);
        });
    });

		it.skip(`Conforama I`, () => {
      return request(myFunctions.parse)
        .post(`/F01131978`)
				.field('company', 'F01131978')
				.attach('invoice_file', './test/resources/Conforama I.jpg')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  console.log(`${JSON.stringify(invoice)}`);

					assert.equal(invoice.total, 69.99);
					assert.equal(invoice.type, InvoiceType.TICKET);
					assert.equal(invoice.sender!.document , 'A79103222');
					assert.equal(invoice.receiver!.document , 'F01131978');
					assert.equalDate(new Date(invoice.date!),new Date(2019,10,30));

        });
    });

		it(`Conforama II`, () => {
      return request(myFunctions.parse)
        .post(`/F01131978`)
				.field('company', 'F01131978')
				.attach('invoice_file', './test/resources/Conforama II.jpg')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					assert.equal(invoice.total, 69.99);
					assert.equal(invoice.type, InvoiceType.TICKET);
					assert.equal(invoice.sender!.document , 'A79103222');
					assert.equal(invoice.receiver!.document , 'F01131978');
					assert.equalDate(new Date(invoice.date!),new Date(2019,10,30));

        });
    });

		it.skip(`FARMACIA`, () => {
      return request(myFunctions.parse)
        .post(`/F01131978`)
				.field('company', 'F01131978')
				.attach('invoice_file', './test/resources/FARMACIA.jpg')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  console.log(`${JSON.stringify(invoice)}`);

					assert.equal(invoice.total, 11.45);
					assert.equal(invoice.type, InvoiceType.TICKET);
					assert.equal(invoice.sender!.document , '725800093');
					assert.equal(invoice.receiver!.document , 'F01131978');
					assert.equalDate(new Date(invoice.date!),new Date(2019,11,19));

        });
    });

		it(`ERKIAGA`, () => {
      return request(myFunctions.parse)
        .post(`/F01131978`)
				.field('company', 'F01131978')
				.attach('invoice_file', './test/resources/ERKIAGA.jpg')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					assert.equal(invoice.total, 11.30);
					assert.equal(invoice.type, InvoiceType.TICKET);
					assert.equal(invoice.sender!.document , 'J01542877');
					assert.equal(invoice.receiver!.document , 'F01131978');
					assert.equalDate(new Date(invoice.date!),new Date(2019,10,16));

        });
    });

		it(`CAFETERIA RIO`, () => {
      return request(myFunctions.parse)
        .post(`/F01131978`)
				.field('company', 'F01131978')
				.attach('invoice_file', './test/resources/RIO.jpg')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					assert.equal(invoice.total, 4.50);
					assert.equal(invoice.type, InvoiceType.TICKET);
					assert.equal(invoice.sender!.document , 'B01521905');
					assert.equal(invoice.receiver!.document , 'F01131978');

					assert.equalDate(new Date(invoice.date!),new Date(2013 /*2019*/,11,7));

        });
    });

		it(`RESTURANT EL 7`, () => {
      return request(myFunctions.parse)
        .post(`/F01131978`)
				.field('company', 'F01131978')
				.attach('invoice_file', './test/resources/7.jpg')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					assert.equal(invoice.total, 35.80);
					assert.equal(invoice.type, InvoiceType.TICKET);
					assert.equal(invoice.sender!.document , '16272662R');
					assert.equal(invoice.receiver!.document , 'F01131978');
					assert.equalDate(new Date(invoice.date!),new Date(2019,11,7));

        });
    });

		it(`ZUBI PUNTA`, () => {
      return request(myFunctions.parse)
        .post(`/F01131978`)
				.field('company', 'F01131978')
				.attach('invoice_file', './test/resources/Zubi Punta.JPG')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					assert.equal(invoice.total, 172.50);
					assert.equal(invoice.type, InvoiceType.TICKET);
					assert.equal(invoice.sender!.document , 'E31979289');
					assert.equal(invoice.receiver!.document , 'F01131978');
					assert.equalDate(new Date(invoice.date!),new Date(2019,11,7));

        });
    });

		it.skip(`FORUM SPORT`, () => {
      return request(myFunctions.parse)
        .post(`/F01131978`)
				.field('company', 'F01131978')
				.attach('invoice_file', './test/resources/FORUM.jpg')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					assert.equal(invoice.total, 13.29);
					assert.equal(invoice.type, InvoiceType.TICKET);
					assert.equal(invoice.sender!.document , 'A48450456');
					assert.equal(invoice.receiver!.document , 'F01131978');
					assert.equalDate(new Date(invoice.date!),new Date(2019,11,7));

        });
    });

		it(`El Tulipan de Oro`, () => {
      return request(myFunctions.parse)
        .post(`/F01131978`)
				.field('company', 'F01131978')
				.attach('invoice_file', './test/resources/Tulipan de Oro II.jpg')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					assert.equal(invoice.total, 2.30);
					assert.equal(invoice.type, InvoiceType.TICKET);
					assert.equal(invoice.sender!.document , 'X3071451P');
					assert.equal(invoice.receiver!.document , 'F01131978');
					assert.equalDate(new Date(invoice.date!),new Date(2019,11,5));

        });
    });

		it(`Restaurante-Pizzeria`, () => {
      return request(myFunctions.parse)
        .post(`/F01131978`)
				.field('company', 'F01131978')
				.attach('invoice_file', './test/resources/Tagliatella.jpg')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					assert.equal(invoice.total, 65.85);
					assert.equal(invoice.type, InvoiceType.TICKET);
					assert.equal(invoice.sender!.document , 'B95761052');
					assert.equal(invoice.receiver!.document , 'F01131978');

        });
    });

		it(`PUERTA DE BILBAO`, () => {
      return request(myFunctions.parse)
        .post(`/F01131978`)
				.field('company', 'F01131978')
				.attach('invoice_file', './test/resources/Puerta de Bilbao.jpg')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					assert.equal(invoice.total, 40.80);
					assert.equal(invoice.type, InvoiceType.TICKET);
					assert.equal(invoice.sender!.document , 'B31606114');
					assert.equal(invoice.receiver!.document , 'F01131978');

        });
    });

		it(`VIBIKE - Bikes & Triathlon`, () => {
      return request(myFunctions.parse)
        .post(`/F01131978`)
				.field('company', 'F01131978')
				.attach('invoice_file', './test/resources/VIBIKE.jpg')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					assert.equal(invoice.total, 299.90);
					assert.equal(invoice.type, InvoiceType.TICKET);
					assert.equal(invoice.sender!.document , 'J01469592');
					assert.equal(invoice.receiver!.document , 'F01131978');

        });
    });

    it(`Movistar Fijo I`, () => {
      return request(myFunctions.parse)
        .post(``)
				.field('company', 'F01131978')
				.attach('invoice_file', './test/resources/Movistar_Fijo_I.pdf')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					// assert.equal(invoice.company, 'F01131978');
					assert.equal(invoice.total, 20.66);
					assert.equal(invoice.reference, 'TA5ZH0179306');
					assert.equal(invoice.sender!.document , 'A82018474');
					assert.equal(invoice.receiver!.document , '00F01131978');

        });
    });

		it(`Media Markt`, () => {
      return request(myFunctions.parse)
        .post(`/invoice/B66941873`)
				.attach('invoice_file', './test/resources/mediamarkt.jpg')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					// assert.equal(invoice.company, 'F01131978');
					assert.equal(invoice.total, 14.99);
					assert.equal(invoice.type, InvoiceType.TICKET);
					assert.equal(invoice.sender!.document , 'A62581798');
					assert.equal(invoice.receiver!.document , 'B66941873');

        });
    });

		it(`Nespresso`, () => {
      return request(myFunctions.parse)
        .post(`/invoice/F01131978`)
				.attach('invoice_file', './test/resources/nespresso.jpg')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					// assert.equal(invoice.company, 'F01131978');
					assert.equal(invoice.total, 27.60);
					assert.equal(invoice.receiver!.document , 'F01131978');

        });
    });

		it(`BNP`, () => {
      return request(myFunctions.parse)
        .post(`/invoice/B01487271`)
				.attach('invoice_file', './test/resources/BNP_1.pdf')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
					const invoices: Invoice [] = res.body as Invoice [];
					const invoice: Invoice = invoices[0];

					// tslint:disable-next-line: no-console
				  // console.log(`${JSON.stringify(invoice)}`);

					// assert.equal(invoice.company, 'F01131978');
					assert.equal(invoice.total, 120.94);
					assert.equal(invoice.receiver!.document , 'B01487271');
					assert.equal(invoice.sender!.document , 'W0013547E');

        });
    });

  });
  }

}
