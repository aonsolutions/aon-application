import { assert } from 'chai';
import { AppOptions } from 'firebase-admin';
import firebaseTest = require('firebase-functions-test');
import { FeaturesList } from 'firebase-functions-test/lib/features';
import fs = require('fs');
import readline = require('readline');
import { TediGmail, TediInvoiceMessage } from '../src/tedi-gmail/lib/helpers';
import { TediInvoiceObject } from '../src/tedi-storage/lib/helpers';
import * as oauth from '../src/tedi-gmail/lib/oauth';
import { TediAuthTest, TediParseTest } from './007.tedi/TediTests';

// tslint:disable-next-line: no-var-requires
const myFunctions = require('../src/index');

const projectConfig: AppOptions = {
  // apiKey: "AIzaSyAHsPikxx02P2_Ad7J_sBXLBwFu-3AFZmg",
  // authDomain: "tedi-snapshot.firebaseapp.com",
  projectId: 'tedi-snapshot',
  storageBucket: 'tedi-snapshot.appspot.com',
  databaseURL: 'https://tedi-snapshot.firebaseio.com',
  // messagingSenderId: "53233459808",
  // appId: "1:53233459808:web:fe603e2348d00722"
};

const test: FeaturesList = firebaseTest(projectConfig, './test/service-account-key.json');

// If index.js calls admin.initializeApp at the top of the file,
// we need to stub it out before requiring index.js. This is because the
// functions will be executed as a part of the require process.
// Here we stub admin.initializeApp to be a dummy function that doesn't do anything.
// adminInitStub = sinon.stub(admin, 'initializeApp');

describe('tEDI center ', () => {

  before(done => {
    const lineReader = readline.createInterface({
      input: fs.createReadStream('./test/resources/007.winnieThePooh.txt'),
    });

    lineReader.on('line', line => {
      // tslint:disable-next-line: no-console
      console.log(line);
    });
    lineReader.on('close', () => {
      done();
    });
  });

  after(() => {
    // Do cleanup tasks.
    test.cleanup();
  });

  describe('REST API', () => {
		TediAuthTest.authTest('', 0);
  });

	describe('PARSE API', () => {
		TediParseTest.parseTest();
  });

  //
  // Testing background (non-HTTP) functions
  //
  describe.skip('Mail Processing ( Gmail + GCF)', () => {
    let gmail4signon;
    let gmail2invoice;

    before(() => {
      gmail4signon = test.wrap(myFunctions.gmail4signon);
      gmail2invoice = test.wrap(myFunctions.gmail2invoice);
    });

    describe('signon', () => {
      it(`SignOn`, done => {
        gmail4signon(test.pubsub.makeMessage({ emailAddress: 'tedi.tests@gmail.com' }))
          .then(() => done())
          .catch(err => done(err));
      });
    });

    describe('invoice', () => {
      it(`Factura Movistar Fijo I`, () => {
        return oauth
          .fetchToken('tedi.tests@gmail.com')
          .then(() => TediGmail.cleanINBOX())
          .then(() => TediGmail.labelMessage('16ae0209f96e9176', ['INBOX']))
          .then(() => gmail2invoice(test.pubsub.makeMessage({ emailAddress: 'tedi.tests@gmail.com' })))
          .then((tediMessages: TediInvoiceMessage[]) => {
            return Promise.all(
              tediMessages.map(tediMessage => {
                assert.lengthOf(tediMessage.invoices, 1, 'One single invoice');
                assert.propertyVal(tediMessage.invoices[0], 'total', 20.66);
                return tediMessage;
              }),
            );
          })
          .then((tediMessages: TediInvoiceMessage[]) => {
            return Promise.all(
              tediMessages.map(tediMessage => {
                if (tediMessage.message && tediMessage.message.id) {
                  return TediGmail.trashMessageById(tediMessage.message.id);
                } else {
                  return new Promise((resolve, error) => resolve(tediMessage.message));
                }
              }),
            );
          });
        // .then(() => done())
        // .catch(err => {
        //   // tslint:disable-next-line: no-console
        //   console.error('ERROR RAISED..: ' + JSON.stringify(err));
        //   done(err);
        // });
      });

      it(`Facturas Iberdrola I, II`, () => {
        return oauth
          .fetchToken('tedi.tests@gmail.com')
          .then(() => TediGmail.cleanINBOX())
          .then(() => TediGmail.labelMessage('16ae06f8d9f8a7db', ['INBOX']))
          .then(() => gmail2invoice(test.pubsub.makeMessage({ emailAddress: 'tedi.tests@gmail.com' })))
          .then((tediMessages: TediInvoiceMessage[]) => {
            return Promise.all(
              tediMessages.map(tediMessage => {
                assert.lengthOf(tediMessage.invoices, 2, 'Two invoices');
                return tediMessage;
              }),
            );
          })
          .then((tediMessages: TediInvoiceMessage[]) => {
            return Promise.all(
              tediMessages.map(tediMessage => {
                if (tediMessage.message && tediMessage.message.id) {
                  return TediGmail.trashMessageById(tediMessage.message.id);
                } else {
                  return new Promise((resolve, error) => resolve(tediMessage.message));
                }
              }),
            );
          });
        // .then(() => done())
        // .catch(err => done(err));
      });

      it(`Facturas Gas Natural I & Orange I, II & Movistar Fusion I, II & Factura Naturgy I, II`, () => {
        return oauth
          .fetchToken('tedi.tests@gmail.com')
          .then(() => TediGmail.cleanINBOX())
          .then(() => TediGmail.labelMessage('16ae07980b535b70', ['INBOX']))
          .then(() => gmail2invoice(test.pubsub.makeMessage({ emailAddress: 'tedi.tests@gmail.com' })))
          .then((tediMessages: TediInvoiceMessage[]) => {
            return Promise.all(
              tediMessages.map(tediMessage => {
                assert.lengthOf(tediMessage.invoices, 7, 'Seven invoices');
                return tediMessage;
              }),
            );
          })
          .then((tediMessages: TediInvoiceMessage[]) => {
            return Promise.all(
              tediMessages.map(tediMessage => {
                if (tediMessage.message && tediMessage.message.id) {
                  return TediGmail.trashMessageById(tediMessage.message.id);
                } else {
                  return new Promise((resolve, error) => resolve(tediMessage.message));
                }
              }),
            );
          });
        // .then(() => done())
        // .catch(err => done(err));
      });

      it(`Sin Facturas`, () => {
        return oauth
          .fetchToken('tedi.tests@gmail.com')
          .then(() => TediGmail.cleanINBOX())
          .then(() => TediGmail.labelMessage('16ae4cd33e04e536', ['INBOX']))
          .then(() => gmail2invoice(test.pubsub.makeMessage({ emailAddress: 'tedi.tests@gmail.com' })))
          .then((tediMessages: TediInvoiceMessage[]) => {
            return Promise.all(
              tediMessages.map(tediMessage => {
                assert.lengthOf(tediMessage.invoices, 0, 'No invoices');
                return tediMessage;
              }),
            );
          })
          .then((tediMessages: TediInvoiceMessage[]) => {
            return Promise.all(
              tediMessages.map(tediMessage => {
                if (tediMessage.message && tediMessage.message.id) {
                  return TediGmail.trashMessageById(tediMessage.message.id);
                } else {
                  return new Promise((resolve, error) => resolve(tediMessage.message));
                }
              }),
            );
          });
        // .then(() => done())
        // .catch(err => done(err));
      });

      it(`Facturas Movistar Fijo I & Iberdrola I, II`, () => {
        return oauth
          .fetchToken('tedi.tests@gmail.com')
          .then(() => TediGmail.cleanINBOX())
          .then(() => TediGmail.labelMessage('16ae0209f96e9176', ['INBOX']))
          .then(() => TediGmail.labelMessage('16ae06f8d9f8a7db', ['INBOX']))
          .then(() => gmail2invoice(test.pubsub.makeMessage({ emailAddress: 'tedi.tests@gmail.com' })))
          .then((tediMessages: TediInvoiceMessage[]) => {
            // TODO: Some kind of assert
            return tediMessages;
          })
          .then((tediMessages: TediInvoiceMessage[]) => {
            return Promise.all(
              tediMessages.map(tediMessage => {
                if (tediMessage.message && tediMessage.message.id) {
                  return TediGmail.trashMessageById(tediMessage.message.id);
                } else {
                  return new Promise((resolve, error) => resolve(tediMessage.message));
                }
              }),
            );
          });
        // .then(() => done())
        // .catch(err => done(err));
      });

			it(`Factura Sarenet`, () => {
        return oauth
          .fetchToken('tedi.tests@gmail.com')
          .then(() => TediGmail.cleanINBOX())
          .then(() => TediGmail.labelMessage('16db988def220816', ['INBOX']))
          .then(() => gmail2invoice(test.pubsub.makeMessage({ emailAddress: 'tedi.tests@gmail.com' })))
          .then((tediMessages: TediInvoiceMessage[]) => {
            return Promise.all(
              tediMessages.map(tediMessage => {
                assert.lengthOf(tediMessage.invoices, 1, 'One single invoice');
								// tslint:disable-next-line: no-console
			          // console.log(tediMessage.invoices[0]);
								assert.propertyVal(tediMessage.invoices[0], 'company', 'B01487271');
                // assert.propertyVal(tediMessage.invoices[0], 'rdocument', 'A48714489');

								assert.propertyVal(tediMessage.invoices[0], 'total', 231.74);
                return tediMessage;
              }),
            );
          })
          .then((tediMessages: TediInvoiceMessage[]) => {
            return Promise.all(
              tediMessages.map(tediMessage => {
                if (tediMessage.message && tediMessage.message.id) {
                  return TediGmail.trashMessageById(tediMessage.message.id);
                } else {
                  return new Promise((resolve, error) => resolve(tediMessage.message));
                }
              }),
            );
          });
      });

			it(`Ticket Areas SAU`, () => {
        return oauth
          .fetchToken('tedi.tests@gmail.com')
          .then(() => TediGmail.cleanINBOX())
          .then(() => TediGmail.labelMessage('16db99dd9755b833', ['INBOX']))
          .then(() => gmail2invoice(test.pubsub.makeMessage({ emailAddress: 'tedi.tests@gmail.com' })))
          .then((tediMessages: TediInvoiceMessage[]) => {
            return Promise.all(
              tediMessages.map(tediMessage => {
                assert.lengthOf(tediMessage.invoices, 1, 'One single invoice');
								// tslint:disable-next-line: no-console
			          // console.log(tediMessage.invoices[0]);
								// assert.propertyVal(tediMessage.invoices[0], 'company', 'B01487271');
                // assert.propertyVal(tediMessage.invoices[0], 'rdocument', 'A08225013');
								assert.propertyVal(tediMessage.invoices[0], 'total', 14.85);
                return tediMessage;
              }),
            );
          })
          .then((tediMessages: TediInvoiceMessage[]) => {
            return Promise.all(
              tediMessages.map(tediMessage => {
                if (tediMessage.message && tediMessage.message.id) {
                  return TediGmail.trashMessageById(tediMessage.message.id);
                } else {
                  return new Promise((resolve, error) => resolve(tediMessage.message));
                }
              }),
            );
          });
      });

      it(`Without messages at INBOX`, () => {
        return oauth.fetchToken('tedi.tests@gmail.com').then(() => gmail2invoice(test.pubsub.makeMessage({ emailAddress: 'tedi.tests@gmail.com' })));

        // .then(() => done())
        // .catch(err => done(err));
      });
			it(`+ 50 Facturas`, () => {
        return oauth
          .fetchToken('tedi.tests@gmail.com')
          .then(() => TediGmail.cleanINBOX())
          .then(() => TediGmail.labelMessage('16e89c8255640863', ['INBOX']))
          .then(() => gmail2invoice(test.pubsub.makeMessage({ emailAddress: 'tedi.tests@gmail.com' })))
          .then((tediMessages: TediInvoiceMessage[]) => {
            return Promise.all(
              tediMessages.map(tediMessage => {
                assert.lengthOf(tediMessage.invoices, 76, '76 invoices');
                return tediMessage;
              }),
            );
          })
          .then((tediMessages: TediInvoiceMessage[]) => {
            return Promise.all(
              tediMessages.map(tediMessage => {
                if (tediMessage.message && tediMessage.message.id) {
                  return TediGmail.trashMessageById(tediMessage.message.id);
                } else {
                  return new Promise((resolve, error) => resolve(tediMessage.message));
                }
              }),
            );
          });
        // .then(() => done())
        // .catch(err => done(err));
      })
			.timeout(500000)
			;

      /*
    it.skip(`Desconocida`, done => {
      oauth
      .fetchToken('tedi.tests@gmail.com')
      .then(() => helpers.labelMessage('16ae562f350d799e', ['INBOX']))
      .then(() => gmail2invoice(test.pubsub.makeMessage({ emailAddress: 'tedi.tests@gmail.com' })))
      .then(([[invoices, re]]) => {
        assert.lengthOf(invoices, 1, 'Only one invoice');
        return re;
      })
      .then(re => helpers.trashMessageById(re.id))
      .then(() => done())
      .catch(err => done(err));
    });
    */
    });
  });

	//
	// Testing background (non-HTTP) functions
	//
	describe.skip('Storage Processing ( Storage + GCF )', () => {
		let storage2invoice;

		before(() => {
			storage2invoice = test.wrap(myFunctions.storage2invoice);
		});

		describe('invoice', () => {
      it(`Factura Movistar Fijo I`, () => {

					return storage2invoice(test.storage.makeObjectMetadata({
						bucket: 'tedi-snapshot-uploads',
					  contentType: 'application/pdf',
					  name: 'Factura_Movistar_Fijo_I.pdf',
					}))
          .then((tediInvoiceObject: TediInvoiceObject) => {
						assert.lengthOf(tediInvoiceObject.invoices, 1, 'One single invoice');
						assert.propertyVal(tediInvoiceObject.invoices[0], 'total', 20.66);
 						return new Promise((resolve, error) => resolve(tediInvoiceObject));
          })
					;
      });
		});

	});

});
