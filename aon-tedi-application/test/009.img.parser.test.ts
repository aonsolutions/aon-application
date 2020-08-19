// tslint:disable: no-unused-expression
import { expect } from 'chai';
// import fileType from 'file-type';
import fs = require('fs');
// import { TediAutoML } from '../src/tedi-automl/TediAutoML';
// import path = require('path');
import { TediImportInvoicesInfo, TaxType } from '../src/tedi-ewok/TediEwok';

import readline = require('readline');

import { TediImgParser } from '../src/tedi-img-parser/TediImgParser';

describe('TEDI IMG PARSER TESTS', () => {
  before(done => {
    const lineReader = readline.createInterface({
      input: fs.createReadStream('./test/resources/009.lotso.txt'),
    });

    lineReader.on('line', line => {
      // tslint:disable-next-line: no-console
      console.log(line);
    });
    lineReader.on('close', () => {
      done();
    });
  });
});

//AmazonTest.amazonTest();

describe('TEDI IMG PARSER TESTs', () => {
  it('PARSE AREAS S.A.U [AREAS.jpg]', done => {

		const file = './test/resources/Areas.jpg';
		const buffer: Buffer = fs.readFileSync(file);

		const info: TediImportInvoicesInfo  = {
			content: buffer,
			contentType: 'image/jpg'
		};

		TediImgParser.parse(info)
		.then( invoice => {
			expect(invoice).not.to.be.null;
			expect(invoice).not.to.be.undefined;
			// expect(invoice).to.has.property('rdocument', 'A08225013');
			done();
		})
      	.catch (reason => done(reason));
  });

	it('PARSE BM [BM.jpg]', done => {

		const file = './test/resources/BM.jpg';
		const buffer: Buffer = fs.readFileSync(file);

		const info: TediImportInvoicesInfo  = {
			content: buffer,
			contentType: 'image/jpg'
		};
		TediImgParser.parse(info)
		.then( invoice => {
			expect(invoice).not.to.be.null;
			expect(invoice).not.to.be.undefined;
			// expect(invoice).to.has.property('rdocument', 'B20099586');
			done();
		})
      	.catch (reason => done(reason));

  });

	// it('PARSE BM II [BM_SMALL.jpg]', done => {
	//
	// 	const file = './test/resources/BM_SMALL.jpg';
	// 	const buffer: Buffer = fs.readFileSync(file);
	//
	// 	const info: TediImportInvoicesInfo  = {
	// 		content: buffer,
	// 		contentType: 'image/jpg'
	// 	};
	//
	// 	TediImgParser.predict(info).subscribe(
  //     invoice => {
	// 			expect(invoice).not.to.be.null;
	// 	    expect(invoice).not.to.be.undefined;
	// 			expect(invoice).to.has.property('rdocument', 'B20099586');
	// 			done();
	// 		},
	// 		error => {
  //       done(error);
  //     },
  //     () => {
  //       done();
  //     },
	// 	);
  // });

	it('PARSE VITORIA-GASTEIZ [GASTEIZ.jpg]', done => {

		const file = './test/resources/GASTEIZ.jpg';
		const buffer: Buffer = fs.readFileSync(file);

		const info: TediImportInvoicesInfo  = {
			content: buffer,
			contentType: 'image/jpg'
		};

		TediImgParser.parse(info)
		.then( invoice => {
			expect(invoice).not.to.be.null;
			expect(invoice).not.to.be.undefined;
			// expect(invoice).to.has.property('rdocument', 'P0106800F');
			done();
		})
		.catch (reason => done(reason));
		  
    });

	it('PARSE ARTEPAN [artepan.jpg]', done => {

		const file = './test/resources/artepan.jpg';
		const buffer: Buffer = fs.readFileSync(file);

		const info: TediImportInvoicesInfo  = {
			content: buffer,
			contentType: 'image/jpg'
		};

		TediImgParser.parse(info)
		.then( invoice => {
			expect(invoice).not.to.be.null;
			expect(invoice).not.to.be.undefined;
			// expect(invoice).to.has.property('rdocument', 'A01023720');
			expect(invoice.date).to.be.eql(new Date(2019,9,26));

			expect(invoice.total).to.be.eq(2.90);
			expect(invoice.taxes).to.have.deep.members([
				{
					tax: TaxType.IVA,
					base: 2.78,
					quota: 0.11,
					percentage: 4.00
				}
			]);
		done();
		})
		.catch (reason => done(reason));
    });

	it('PARSE NESPRESSO [nespresso.jpg]', done => {

		const file = './test/resources/nespresso.jpg';
		const buffer: Buffer = fs.readFileSync(file);

		const info: TediImportInvoicesInfo  = {
			content: buffer,
			contentType: 'image/jpg'
		};

		TediImgParser.parse(info)
		.then( invoice => {
			expect(invoice).not.to.be.null;
			expect(invoice).not.to.be.undefined;
			// expect(invoice).to.has.property('rdocument', 'A01023720');
			// expect(invoice.date).to.be.eql(new Date(2019,9,11));

			expect(invoice.total).to.be.eq(27.60);
			expect(invoice.taxes).to.have.deep.members([
				{
					tax: TaxType.IVA,
					base: 25.09,
					quota: 2.51,
					percentage: 10.00
				}
			]);			done();
		})
		.catch (reason => done(reason));
		

  });

	it.skip('PARSE ZARA [zara.jpg]', done => {

		const file = './test/resources/zara.jpg';
		const buffer: Buffer = fs.readFileSync(file);

		const info: TediImportInvoicesInfo  = {
			content: buffer,
			contentType: 'image/jpg'
		};

		TediImgParser.parse(info)
		.then( invoice => {
			expect(invoice).not.to.be.null;
			expect(invoice).not.to.be.undefined;
			// expect(invoice).to.has.property('rdocument', 'A01023720');
			expect(invoice.date).to.be.eql(new Date(2019,9,24));

			expect(invoice.total).to.be.eq(72.75);
			expect(invoice.taxes).to.have.deep.members([
				{
					tax: TaxType.IVA,
					base: 60.11,
					quota: 12.64,
					percentage: 21.00
				}
			]);
			done();
		})
		.catch (reason => done(reason));


  });

	it('PARSE IBERICOS [ibericos.jpg]', done => {

		const file = './test/resources/ibericos.jpg';
		const buffer: Buffer = fs.readFileSync(file);

		const info: TediImportInvoicesInfo  = {
			content: buffer,
			contentType: 'image/jpg'
		};

		TediImgParser.parse(info)
		.then( invoice => {
			expect(invoice).not.to.be.null;
			expect(invoice).not.to.be.undefined;
			// expect(invoice).to.has.property('rdocument', 'B37503935');
			// expect(invoice.date).to.be.eql(new Date(2019,9,26));

			expect(invoice.total).to.be.eq(30.69);
			expect(invoice.taxes).to.have.deep.members([
				{
					tax: TaxType.IVA,
					base: 27.90,
					quota: 2.79,
					percentage: 10.00
				}
			]);
			done();
		})
		.catch (reason => done(reason));
  	});

	it('PARSE MEDIAMARKT [mediamarkt.jpg]', done => {

		const file = './test/resources/mediamarkt.jpg';
		const buffer: Buffer = fs.readFileSync(file);

		const info: TediImportInvoicesInfo  = {
			content: buffer,
			contentType: 'image/jpg'
		};

		TediImgParser.parse(info)
		.then( invoice => {
			expect(invoice).not.to.be.null;
			expect(invoice).not.to.be.undefined;
			// expect(invoice).to.has.property('rdocument', 'A62581798');

			expect(invoice.total).to.be.eq(14.99);
			expect(invoice.taxes).to.have.deep.members([
				{
					tax: TaxType.IVA,
					base: 12.39,
					quota: 2.60,
					percentage: 21.00
				}
			]);
			done();
		})
		.catch (reason => done(reason));


  	});

	it('PARSE TULIPAN [tulipandeoro.jpg]', done => {

		const file = './test/resources/tulipandeoro.jpg';
		const buffer: Buffer = fs.readFileSync(file);

		const info: TediImportInvoicesInfo  = {
			content: buffer,
			contentType: 'image/jpg'
		};

		TediImgParser.parse(info)
		.then( invoice => {
			expect(invoice).not.to.be.null;
			expect(invoice).not.to.be.undefined;
			// expect(invoice).to.has.property('rdocument', 'A62581798');

			expect(invoice.total).to.be.eq(5.50);
			expect(invoice.taxes).to.have.deep.members([
				{
					tax: TaxType.IVA,
					base: 5.00,
					quota: 0.50,
					percentage: 10.00
				}
			]);

			done();
		})
		.catch (reason => done(reason));


  	});

	it('PARSE GINOS [ginos.jpg]', done => {

		const file = './test/resources/ginos.jpg';
		const buffer: Buffer = fs.readFileSync(file);

		const info: TediImportInvoicesInfo  = {
			content: buffer,
			contentType: 'image/jpg'
		};

		TediImgParser.parse(info)
		.then( invoice => {
			expect(invoice).not.to.be.null;
			expect(invoice).not.to.be.undefined;
			// expect(invoice).to.has.property('rdocument', 'A62581798');

			expect(invoice.total).to.be.eq(28.9);
			expect(invoice.taxes).to.have.deep.members([
				{
					tax: TaxType.IVA,
					base: 26.27,
					quota: 2.63,
					percentage: 10.00
				}
			]);
			done();
		})
		.catch (reason => done(reason));

  	});

	it.skip('PARSE IKASTOLA [ikastola.jpg]', done => {

		const file = './test/resources/ikastola.jpg';
		const buffer: Buffer = fs.readFileSync(file);

		const info: TediImportInvoicesInfo  = {
			content: buffer,
			contentType: 'image/jpg'
		};

		TediImgParser.parse(info)
		.then( invoice => {
			expect(invoice).not.to.be.null;
			expect(invoice).not.to.be.undefined;
			// expect(invoice).to.has.property('rdocument', 'A62581798');
			// expect(invoice.date).to.be.eql(new Date(2019,9,28));

			expect(invoice.total).to.be.eq(15.00);
			expect(invoice.taxes).to.have.deep.members([
				{
					tax: TaxType.IVA,
					base: 12.40,
					quota: 2.60,
					percentage: 21.00
				}
			]);
			done();
		})
		.catch (reason => done(reason));
		
  });

});
