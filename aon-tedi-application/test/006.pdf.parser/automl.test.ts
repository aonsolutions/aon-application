import { expect } from 'chai';
// import { File } from '@google-cloud/storage';
import { Invoice, InvoiceStatus, InvoiceType, TediImportInvoicesInfo, TaxType } from '../../src/tedi-ewok/TediEwok';
import { TediPdfParser } from '../../src/tedi-pdf-parser/TediPdfParser';
import { TediAutoML } from '../../src/tedi-automl/TediAutoML';

export class AutoMLTest {

	public static checkInvoice(invoice: Invoice, targetInvoice: Invoice): void {

		targetInvoice.sender = targetInvoice.sender || {};
		targetInvoice.receiver = targetInvoice.receiver || {};

    expect(invoice).not.to.be.null
		expect(invoice).not.to.be.undefined;
		expect(invoice).to.has.property('company', targetInvoice.company );
		expect(invoice).to.nested.include({'sender.document': targetInvoice.sender.document} )
		expect(invoice).to.nested.include({'receiver.document': targetInvoice.receiver.document} )
		//;
		// expect(invoice).to.has.property('automl_tables');
		// expect(invoice).to.have.nested.property('automl_tables.amounts');
		// expect(invoice.automl_tables && invoice.automl_tables.amounts || []).to.have.lengthOf.at.least(1);

		expect(invoice).to.has.property('insight');
		expect(invoice).to.have.nested.property('insight.amounts');
		expect(invoice.insight && invoice.insight.amounts || []).to.have.lengthOf.at.least(1);

  }

  public static automlTest(): void {
		const endesa1: Invoice = {
		  type: InvoiceType.RECIBIDA,
		  status: InvoiceStatus.inbox,
		  receiver: {
		    name: 'GARCIA-LOMAS&AS SL',
		    address: undefined,
		    document: 'B23741903',
		    document_country: 'ES'
		  },
		  company: 'B23741903',
		  sender: {
		    document: 'B82846825',
		    name: 'ENDESA ENERGIA XXI SL',
		    address: {
		      province: 'Madrid',
		      postal_code: '28042',
		      country: 'ES',
		      address: 'CALLE RIBERA DEL LOIRA, 60'
		    }
		  }
		};

		const toledo1: Invoice = {
		  type: InvoiceType.RECIBIDA,
		  status: InvoiceStatus.inbox,
		  receiver: {
		    name: 'AON SOLUTIONS, S.L.',
		    address: undefined,
		    document: 'B01487271',
		    document_country: 'ES'
		  },
		  company: 'B01487271',
		  sender: {
		    document: 'B82435074',
		    name: 'TOLEDO Y ASOCIADOS ASESORIA Y GESTION, S.L',
		    address: {
		      province: 'Madrid',
		      postal_code: '28046',
		      country: 'ES',
		      address: 'PS. DE LA CASTELLANA 192, L-10'
		    }
		  }
		};

		const bnp1: Invoice = {
		  type: InvoiceType.RECIBIDA,
		  status: InvoiceStatus.inbox,
		  receiver: {
		    name: 'AON SOLUTIONS, S.L.',
		    address: undefined,
		    document: 'B01487271',
		    document_country: 'ES'
		  },
		  company: 'B01487271',
		  sender: {
		    document: 'W0013547E',
		    name: 'BNP PARIBAS LEASE GROUP S.A',
		    address: {
		      province: 'Madrid',
		      postal_code: '28045',
		      country: 'ES',
		      address: 'C/ Estrella Denébola 8 3a Planta'
		    }
		  }
		};

		const sarenet1: Invoice = {
		  type: InvoiceType.RECIBIDA,
		  status: InvoiceStatus.inbox,
		  receiver: {
		    name: 'AON SOLUTIONS, S.L.',
		    address: undefined,
		    document: 'B01487271',
		    document_country: 'ES'
		  },
		  company: 'B01487271',
		  sender: {
		    document: 'A48714489',
		    name: 'SARENET S.A',
		  }
		};

		const bpopular1: Invoice = {
		  type: InvoiceType.RECIBIDA,
		  status: InvoiceStatus.inbox,
		  receiver: {
		    name: 'AON SOLUTIONS, S.L.',
		    address: undefined,
		    document: 'B01487271',
		    document_country: 'ES'
		  },
		  company: 'B01487271',
		  sender: {
		    document: 'A39000013',
		    name: 'BANCO SANTANDER S.A',
		  }
		};

		let bnp: Invoice;
		let endesa: Invoice;
		let toledo: Invoice;
		let sarenet: Invoice;
		let bpopular: Invoice;


    describe('TEDI PDF PREDICT TEST ', () => {

			it('PREDICT ENDESA INVOICE [ ENDESA_1.pdf ]', done => {
        const filename = 'test/resources/Endesa_1.pdf';
				const info: TediImportInvoicesInfo = {
					content: '',
					contentType: 'application/pdf',
					companies : [
						{
							document: 'B23741903',
						  company: 'GARCIA-LOMAS&AS SL',
						  name: 'GARCIA-LOMAS&AS SL',
						  active: true
						}
					]
				};

				TediPdfParser.predictFromFile(filename, info).subscribe(
          invoice => {
			endesa = invoice;
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

			it('PREDICT TOLEAO & ASOCIADOS INVOICE [ TOLEDO_1.pdf ]', done => {
        const filename = 'test/resources/Toledo_1.pdf';
				const info: TediImportInvoicesInfo = {
					content: '',
					contentType: 'application/pdf',
					companies : [
						{
							document: 'B01487271',
						  company: 'AON SOLUTIONS, S.L.',
						  name: 'AON SOLUTIONS, S.L.',
						  active: true
						}
					]
				};

				TediPdfParser.predictFromFile(filename, info).subscribe(
          invoice => {
						toledo = invoice;
            this.checkInvoice(invoice, toledo1);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });

			it('PREDICT BNP INVOICE [ BNP_1.pdf ]', done => {
        const filename = 'test/resources/BNP_1.pdf';
				const info: TediImportInvoicesInfo = {
					content: '',
					contentType: 'application/pdf',
					companies : [
						{
							document: 'B01487271',
						  company: 'AON SOLUTIONS, S.L.',
						  name: 'AON SOLUTIONS, S.L.',
						  active: true
						}
					]
				};

				TediPdfParser.predictFromFile(filename, info).subscribe(
          invoice => {
						// tslint:
						bnp = invoice;
            this.checkInvoice(invoice, bnp1);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });

			it.skip('PREDICT SARENET INVOICE [ SARENET_1.pdf ]', done => {
        const filename = 'test/resources/SARENET_1.pdf';
				const info: TediImportInvoicesInfo = {
					content: '',
					contentType: 'application/pdf',
					companies : [
						{
						  document: 'B01487271',
						  company: 'AON SOLUTIONS, S.L.',
						  name: 'AON SOLUTIONS, S.L.',
						  active: true
						}
					]
				};

				TediPdfParser.predictFromFile(filename, info).subscribe(
          invoice => {
						// tslint:
						sarenet = invoice;
			// tslint:disable-next-line: no-console
			console.log(JSON.stringify(invoice));
            this.checkInvoice(invoice, sarenet1);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });

			it('PREDICT BANCO POPUPLAR INVOICE [ B.Popular I.pdf ]', done => {
        const filename = 'test/resources/B.Popular I.pdf';
				const info: TediImportInvoicesInfo = {
					content: '',
					contentType: 'application/pdf',
					companies : [
						{
							document: 'B01487271',
						  company: 'AON SOLUTIONS, S.L.',
						  name: 'AON SOLUTIONS, S.L.',
						  active: true
						}
					]
				};

				TediPdfParser.predictFromFile(filename, info).subscribe(
          invoice => {
						// tslint:
						bpopular = invoice;
            this.checkInvoice(invoice, bpopular1);
          },
          error => {
            done(error);
          },
          () => {
            done();
          },
        );
      });

		// 	it.skip('WRITE CSV ML INPUT FILE [ PHOTON_MILK_BATH.CSV ]', done => {

		// 			bnp.uuid = '37d5547a-f188-11e9-81b4-2a2ae2dbcce4';
		// 			toledo.uuid='37d5572c-f188-11e9-81b4-2a2ae2dbcce4';
		// 			endesa.uuid = '37d55880-f188-11e9-81b4-2a2ae2dbcce4';
		// 			sarenet.uuid = '37d559b6-f188-11e9-81b4-2a2ae2dbcce4';

		// 			TediAutoML.writeCSVFile([
		// 				bnp,
		// 				toledo,
		// 				endesa,
		// 				sarenet,
		// 			],
		// 			'PHOTON_MILK_BATH')
		// 			.then((file: File) => file.exists() )
		// 			.then(() => done())
		// 			.catch((err) => {
		// 				done(err);
		// 			})
		// 			;
		//   });

		// 		it.skip('BATCH PREDICT OF CSV ML INPUT FILE [ PHOTON_MILK_BATH.CSV ]', done => {
		// 			TediAutoML.batchPredict(
		// 			'PHOTON_MILK_BATH')
		// 			.then(responses => {
		// 		    const metadata = responses[1];
		// 				// tslint:disable-next-line: no-console
		//         console.log(metadata);
		// 				// tslint:disable-next-line: no-console
		//         console.log(metadata.batchPredictDetails.outputInfo.gcsOutputDirectory);
		// 				return metadata.batchPredictDetails.outputInfo.gcsOutputDirectory;
		// 			})
		// 			.then((gcsOutputDirectory: string) =>
		// 				TediAutoML.parsePredict([
		// 					bnp,
		// 					toledo,
		// 					endesa,
		// 					sarenet,
		// 				],
		// 				gcsOutputDirectory
		// 				)
		// 			)
		// 			.then((invoices: Invoice[]) => {
		// 				invoices.forEach((invoice: Invoice) => TediAutoML.predict(invoice,0.75) )
		// 				// tslint:disable-next-line: no-console
		//         console.log(invoices);
		// 				done();
		// 			})
		// 			.catch((err) => {
		// 				done(err);
		// 			})
		// 			;
		// 			// TediAutoML.parsePredict([
		// 			// 	bnp,
		// 			// 	toledo,
		// 			// 	endesa,
		// 			// 	sarenet,
		// 			// ],
		// 			// 'gs://tedi-snapshot-ml/PHOTON_MILK_BATH-predict/prediction-purchases_expense_20190930091517-2019-10-21T09:11:39.928Z'
		// 			// )
		// 			// .then((invoices: Invoice[]) => {
		// 			// 	invoices.forEach((invoice: Invoice) => TediAutoML.predict(invoice,0.75) )
		// 			// 	// tslint:disable-next-line: no-console
		//       //   console.log(invoices);
		// 			// 	done();
		// 			// })
		// 			// ;
		//   })
		// .timeout(10*60*1000)
		// ;

			it.skip('INSIGHT [TOLEDO, SARENET, ENDESA, BNP ]', done => {
				[
					toledo,
					sarenet,
					endesa,
					bnp,
					bpopular,
				].forEach((invoice: Invoice) => TediAutoML.insight(invoice) );

				expect(sarenet.total).to.be.eq(231.74);
				expect(sarenet.taxes).to.have.deep.members([
					{
						tax: TaxType.IVA,
						base: 191.52,
						quota: 40.22,
						percentage: 21.00
					}
				]);

				expect(toledo.total).to.be.eq(223.85);
				expect(toledo.taxes).to.have.deep.members([
					{
						tax: TaxType.IVA,
						base: 185.00,
						quota: 38.85,
						percentage: 21.00
					}
				]);

				expect(endesa.total).to.be.eq(49.44);
				expect(endesa.taxes).to.have.deep.members([
					{
						tax: TaxType.IVA,
						base: 40.86,
						quota: 8.58,
						percentage: 21.00
					}
				]);

				expect(bnp.total).to.be.eq(120.94);
				expect(bnp.taxes).to.have.deep.members([
					{
						tax: TaxType.IVA,
						base: 99.95,
						quota: 20.99,
						percentage: 21.00
					}
				]);

				// expect(bpopular.total).to.be.eq(18.76);

				done();
      });

			it('INSIGHT AMAZON INVOICE [ AMAZON_3.pdf ]', done => {
        const filename = 'test/resources/AMAZON_3.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
						const amazon: Invoice = invoice;

						const info: TediImportInvoicesInfo = {
							content: '',
							contentType: 'application/pdf',
							companies : [
								{
									document: 'B32440927',
								  company: 'ARBINOVA SL',
								  name: 'ARBINOVA S.L',
								  active: true
								}
							]
						};

						TediPdfParser.predictFromFile(filename, info).subscribe(
							invoice => {

								TediAutoML.insight(invoice);

								expect(invoice.total).to.be.eq(amazon.total);
								expect(invoice.taxes).to.be.deep.eq(amazon.taxes);
								// expect(invoice.sender.document).to.be.eq(amazon.sender.document);
								// expect(invoice.receiver.document).to.be.eq(amazon.receiver.document);

								done();
							},
							error => {
								done(error);
							}
						);
          },
          error => {
            done(error);
          },
        );
      });

			it('INSIGHT SECURITAS INVOICE [ Securitas_4.pdf ]', done => {
        const filename = 'test/resources/Securitas_4.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
						const securitas: Invoice = invoice;

						const info: TediImportInvoicesInfo = {
							content: '',
							contentType: 'application/pdf',
							companies : [
								{
									document: '25376850F',
								  company: 'JOSE ANTONIO GOMEZ MARTINEZ',
								  name: 'JOSE ANTONIO GOMEZ MARTINEZ',
								  active: true
								}
							]
						};

						TediPdfParser.predictFromFile(filename, info).subscribe(
							invoice => {

								TediAutoML.insight(invoice);

								expect(invoice.total).to.be.eq(securitas.total);
								expect(invoice.taxes).to.be.deep.eq(securitas.taxes);
								// expect(invoice.sender.document).to.be.eq(amazon.sender.document);
								// expect(invoice.receiver.document).to.be.eq(amazon.receiver.document);

								done();
							},
							error => {
								done(error);
							}
						);
          },
          error => {
            done(error);
          },
        );
      });

			it('INSIGHT RENFE INVOICE [ RENFE_I.pdf ]', done => {
        const filename = 'test/resources/RENFE_I.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
						const renfe: Invoice = invoice;

						const info: TediImportInvoicesInfo = {
							content: '',
							contentType: 'application/pdf',
							companies : [
								{
									document: 'B95661856',
								  company: 'LUDUS TECH SL',
								  name: 'LUDUS TECH SL',
								  active: true
								}
							]
						};

						TediPdfParser.predictFromFile(filename, info).subscribe(
							invoice => {

								TediAutoML.insight(invoice);

								expect(invoice.total).to.be.eq(renfe.total);
								expect(invoice.taxes).to.be.deep.eq(renfe.taxes);
								// expect(invoice.sender.document).to.be.eq(amazon.sender.document);
								// expect(invoice.receiver.document).to.be.eq(amazon.receiver.document);

								done();
							},
							error => {
								done(error);
							}
						);
          },
          error => {
            done(error);
          },
        );
      });

			it.skip('INSIGHT ORANGE INVOICE [ Orange_I.pdf ]', done => {
        const filename = 'test/resources/Orange_I.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
						const orange: Invoice = invoice;

						const info: TediImportInvoicesInfo = {
							content: '',
							contentType: 'application/pdf',
							companies : [
								{
									document: 'B01487271',
								  company: 'AON SOLUTIONS, S.L.',
								  name: 'AON SOLUTIONS, S.L.',
								  active: true
								}
							]
						};

						TediPdfParser.predictFromFile(filename, info).subscribe(
							invoice => {

								TediAutoML.insight(invoice);

								// expect(invoice.total).to.be.eq(orange.total);
								expect(invoice.taxes).to.be.deep.eq(orange.taxes);
								// expect(invoice.sender.document).to.be.eq(amazon.sender.document);
								// expect(invoice.receiver.document).to.be.eq(amazon.receiver.document);

								done();
							},
							error => {
								done(error);
							}
						);
          },
          error => {
            done(error);
          },
        );
      });

			it.skip('INSIGHT MAKRO INVOICE [ Makro_1.pdf ]', done => {
        const filename = 'test/resources/Makro_1.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
						const makro: Invoice = invoice;

						const info: TediImportInvoicesInfo = {
							content: '',
							contentType: 'application/pdf',
							companies : [
								{
									document: 'Y5549126E',
								  company: 'CZIPTAK ZSOFIA',
								  name: 'CZIPTAK ZSOFIA',
								  active: true
								}
							]
						};

						TediPdfParser.predictFromFile(filename, info).subscribe(
							invoice => {

								TediAutoML.insight(invoice);

								expect(invoice.total).to.be.eq(makro.total);
								expect(invoice.taxes).to.be.deep.eq(makro.taxes);
								// expect(invoice.sender.document).to.be.eq(amazon.sender.document);
								// expect(invoice.receiver.document).to.be.eq(amazon.receiver.document);

								done();
							},
							error => {
								done(error);
							}
						);
          },
          error => {
            done(error);
          },
        );
      });

			it('INSIGHT MOVISTAR MOVIL INVOICE [ MOVISTAR_MOVIL_1.pdf ]', done => {
        const filename = 'test/resources/MOVISTAR_MOVIL_1.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
						const movistar: Invoice = invoice;

						const info: TediImportInvoicesInfo = {
							content: '',
							contentType: 'application/pdf',
							companies : [
								{
									document: 'F01131978',
								  company: 'UDAPA, S.COOP',
								  name: 'UDAPA, S.COOP',
								  active: true
								}
							]
						};

						TediPdfParser.predictFromFile(filename, info).subscribe(
							invoice => {

								TediAutoML.insight(invoice);

								expect(invoice.total).to.be.eq(movistar.total);
								expect(invoice.taxes).to.be.deep.eq(movistar.taxes);
								// expect(invoice.sender.document).to.be.eq(amazon.sender.document);
								// expect(invoice.receiver.document).to.be.eq(amazon.receiver.document);

								done();
							},
							error => {
								done(error);
							}
						);
          },
          error => {
            done(error);
          },
        );
      });

			it.skip('INSIGHT UNKNOWN INVOICE [ Unknown.pdf ]', done => {
        const filename = 'test/resources/Unknown.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
						const unknown: Invoice = invoice;

						const info: TediImportInvoicesInfo = {
							content: '',
							contentType: 'application/pdf',
							companies : [
								{
									document: 'B01487271',
								  company: 'AON SOLUTIONS, S.L.',
								  name: 'AON SOLUTIONS, S.L.',
								  active: true
								}
							]
						};

						TediPdfParser.predictFromFile(filename, info).subscribe(
							invoice => {

								TediAutoML.insight(invoice);

								expect(invoice.total).to.be.eq(unknown.total);
								expect(invoice.taxes).to.be.deep.eq(unknown.taxes);
								// expect(invoice.sender.document).to.be.eq(amazon.sender.document);
								// expect(invoice.receiver.document).to.be.eq(amazon.receiver.document);

								done();
							},
							error => {
								done(error);
							}
						);
          },
          error => {
            done(error);
          },
        );
      });

	// 		it.skip('PARSE PREDICT OF AUTO ML TABLES [prediction-purchases_expense_20190930091517]', done => {
	// 			TediAutoML.parsePredict([
	// 				bnp,
	// 				toledo,
	// 				endesa,
	// 				sarenet,
	// 			],
	// 			'gs://tedi-snapshot-ml/PHOTON_MILK_BATH-predict/prediction-purchases_expense_20190930091517-2019-10-22T09:41:50.010Z'
	// 			)
	// 			.then((invoices: Invoice[]) => {
	// 				invoices.forEach((invoice: Invoice) => TediAutoML.predict(invoice,0.75) )
	// 				// tslint:disable-next-line: no-console
	//         console.log(endesa.taxes);

	// 				expect(sarenet.total).to.be.eq(231.74);
	// 				expect(sarenet.taxes).to.have.deep.members([
	// 					{
	// 						tax: TaxType.IVA,
	// 						base: 191.52,
	// 						quota: 40.22,
	// 						percentage: 21.00
	// 					}
	// 				]);

	// 				expect(toledo.total).to.be.eq(223.85);
	// 				expect(toledo.taxes).to.have.deep.members([
	// 					{
	// 						tax: TaxType.IVA,
	// 						base: 185.00,
	// 						quota: 38.85,
	// 						percentage: 21.00
	// 					}
	// 				]);
	// 				expect(endesa.total).to.be.eq(49.44);
	// 				// expect(endesa.taxes).to.have.deep.members([
	// 				// 	{
	// 				// 		tax: TaxType.IVA,
	// 				// 		base: 40.86,
	// 				// 		quota: 8.58,
	// 				// 		percentage: 21.00
	// 				// 	}
	// 				// ]);

	// 				// expect(bnp.total).to.be.eq(120.94); 20.99

	// 				done();
	// 			})
	// 			;
    //   });

			it('INSIGHT MOVISTAR MOVIL INVOICE [ MOVISTAR_MOVIL_1.pdf ]', done => {
        const filename = 'test/resources/MOVISTAR_MOVIL_1.pdf';

        TediPdfParser.extractFromFile(filename).subscribe(
          invoice => {
						const movistar: Invoice = invoice;

						const info: TediImportInvoicesInfo = {
							content: '',
							contentType: 'application/pdf',
							companies : [
								{
									document: 'F01131978',
								  company: 'UDAPA, S.COOP',
								  name: 'UDAPA, S.COOP',
								  active: true
								}
							]
						};

						TediPdfParser.predictFromFile(filename, info).subscribe(
							invoice => {

								TediAutoML.insight(invoice);

								expect(invoice.total).to.be.eq(movistar.total);
								expect(invoice.taxes).to.be.deep.eq(movistar.taxes);
								// expect(invoice.sender.document).to.be.eq(amazon.sender.document);
								// expect(invoice.receiver.document).to.be.eq(amazon.receiver.document);

								done();
							},
							error => {
								done(error);
							}
						);
          },
          error => {
            done(error);
          },
        );
      });


    });
  }
}
