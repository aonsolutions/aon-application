import { expect } from 'chai';
import fs = require('fs');
import * as mysql from 'mysql'
// import { File } from '@google-cloud/storage';
import { TediAutoML } from '../../src/tedi-automl/TediAutoML';
import { Invoice,TediImportInvoicesInfo } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';

export class AonTest {


  public static aonTest(): void {

    describe.skip('AON PREDICT TEST', () => {
			let connection: mysql.Connection;
			before(done => {
				connection = mysql.createConnection({
					host     : 'localhost',
				  user     : 'dbuser',
				  password : 'serubd2000',
				  database : 'sig-grupo-esferalia',
				});

				done();
			});

			// AND invoice.reference_code = '20190702' \


			it('PREDICT AON INVOICE [PDF] ', done => {
				connection
				.query({
				sql : `SELECT * \
				FROM invoice \
				INNER JOIN invoice_attach ON \
				( invoice.id = invoice_attach.invoice ) \
				INNER JOIN company ON (invoice.domain = company.domain) \
				INNER JOIN registry ON (company.registry = registry.id) \
				INNER JOIN registry AS sender ON (invoice.registry = sender.id) \
				WHERE invoice.type IN (0,2,3) \
				AND invoice.rdocument IS NOT NULL \
				AND invoice.reference_code IS NOT NULL \
				AND invoice_attach.mimeType=22 \
				AND invoice.issue_date >= '2019-07-01' \
				\
				AND invoice.reference_code = '20190702' \
				AND invoice.rname NOT IN ('GITHUB, INC') \
				AND invoice.rdocument NOT IN ( 'W0185696B', 'A82009812' ) \
				AND invoice.id NOT IN ( 1594012, 1594042, 1593581, 1593613, 1594028) \
				\
				LIMIT 120`,
				nestTables: true
				}
			).on('error', (err: mysql.MysqlError) => {
					// Handle error, an 'end' event will be emitted after this as well
					done(err);
				}
			).on('result', (row, index :number) => {
					if ( !row.invoice_attach.data ){
						return;
					}
					// Pausing the connnection is useful if your processing involves I/O
					connection.pause();

					const info: TediImportInvoicesInfo = {
						content: row.invoice_attach.data,
						contentType: 'application/pdf',
						companies : [
							{
								active: true,
								name: row.registry.alias,
								company: row.registry.name,
								document: row.registry.document,
							}
						]
					};

					TediPdfParser.predict(info).subscribe(
						(invoice: Invoice) => {
							try{
								// tslint:disable-next-line: no-console
								// console.log(`${JSON.stringify(row.invoice.reference_code)}`)

                if ( row.invoice.sender && row.invoice.sender.document ){
									expect(invoice.sender?.document).eq(row.invoice.sender.document, `[${row.invoice.id}]: Sender document  must be ${row.invoice.sender.document}`);
								} else {
									// tslint:disable-next-line: no-console
									// console.log(`${JSON.stringify(invoice.sender)}`)
								}
								if ( invoice.company ) {
									expect(invoice.company).eq(row.registry.document, `[${row.invoice.id}]: Company document  must be ${row.registry.document}` );
								}
								// expect(invoice.receiver.document).eq(row.registry.document);

								invoice = TediAutoML.insight(invoice);


								expect(invoice.total).eq(Math.abs(row.invoice.total), `[${row.invoice.id}]: Total must be ${row.invoice.total}` );

								// tslint:disable-next-line: no-console
								console.log(`[${row.invoice.id}]: ${invoice.sender?.document}  ${invoice.total} Sucess`);

								connection.resume();


							} catch ( err ) {
								// tslint:disable-next-line: no-console
								console.log(`${row.invoice.reference_code}`)
								fs.writeFileSync(`${row.invoice.reference_code.replace(/\//g,'')}.pdf`, row.invoice_attach.data );
								done(err);
								connection.end();
							}
						},
						error => {
	            done(error);
	          },
					)
					;


					// tslint:disable-next-line: no-console
					// console.log(`${referenceCode}[${rDocument}]: ${issueDate} ${total} ${row}`)

				}
			).on('end', () => {
				done();
			})
			;

			})
			.timeout(10*60*1000)
			;

			it('PREDICT AON INVOICE [IMAGE] ', done => {
				done();
			});

			after(() => {
			try {
				connection.end();
			} catch ( err ) {

			}
			});
  	});
	}
}
