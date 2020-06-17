import { TediFaker } from '../TediFaker';
import { InvoiceStatus, InvoiceType } from '../../src/tedi-ewok/TediEwok';
import request from 'supertest';
import { assert } from 'chai';

const myFunctions = require('../../src/index');

export class TediInvoiceTest {



  public static invoiceTest(sessionId: string, type: string) : void {
    const isOut = type === 'out';
    // const isRoot = type === 'root';
    // const isAdmin = type === 'admin';
    // const isReader = type === 'reader';
    // const isWriter = type === 'writer';
    const TEST_EMAIL = 'tedi.tests1@gmail.com';

    describe(`Invoice Test with ${type} user`, () => {
      const invoice = TediFaker.randomInvoice();
      const company = 'B01487271';
      invoice.company = company;

      const invoice2 = TediFaker.randomInvoice();
      invoice2.company = company;
      const outMsg = 'error: Token Incorrecto';
      it(isOut ? `GET '/' should return ${outMsg}`: `GET '/' should returns an array of all invoices`, () => {
        return request(myFunctions.invoice)
          .get('/')
          .query({company: 'B01487271', status: 'pending'})
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(isOut ? 403 : 200)
          .then(res => {
            if(isOut) {
              assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
            } else {
              const users = res.body;
              assert.typeOf(users, 'array', 'we have an array');
            }
            return res;
          });
      });
      if(!isOut) {
        it(  `GET COUNT '/count/B01487271/inbox' should return number`, () => {
          return request(myFunctions.invoice)
          .get('/count/B01487271/inbox')
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(isOut ? 403 : 200)
          .then(res => {
            assert.typeOf(res.body, 'number', 'we have an number');
            return res;
          });
        });
      }
      it(`GET '/' should returns an array of invoices with filter`, () => {
        return request(myFunctions.invoice)
          .get('/')
          .query({company: 'B01487271', status: 'accepted', rdocument: '95759934D', type: InvoiceType.TICKET, category: '629.1', fromDate:'2019-06-12T00:00:00.000Z', reference:'Update'})
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(isOut? 403 : 200)
          .then(res => {
            if(isOut) {
              assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
            } else {
              // res.body, res.headers, res.status
              const users = res.body;
              assert.typeOf(users, 'array', 'we have an array');
            }
            return res;
          });
      });

      it(`POST '/' should adds invoice ${invoice.reference}`, () => {
        return request(myFunctions.invoice)
          .post('/')
          .send(invoice)
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(isOut ? 403 : 200)
          .then(res => {
            if(isOut) {
              assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
            } else {
              invoice.uuid = res.body.uuid;
              assert.deepEqual(res.body.reference, invoice.reference);
            }
            return res;
          });
      });

      it(`POST '/' should adds invoice ${invoice2.reference}`, () => {
        return request(myFunctions.invoice)
          .post('/')
          .send(invoice2)
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(isOut ? 403 : 200)
          .then(res => {
            if(isOut) {
              assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
            } else {
              invoice2.uuid = res.body.uuid;
              assert.deepEqual(res.body.reference, invoice2.reference);
            }
            return res;
          });
      });

      it(`POST '/' should update invoice ${invoice.uuid}`, () => {
        invoice.reference = 'Updated Name';
        return request(myFunctions.invoice)
          .post('/')
          .send(invoice)
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(isOut ? 403 : 200)
          .then(res => {
            if(isOut) {
              assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
            } else {
              assert.deepEqual(res.body.reference, invoice.reference);
            }
            return res;
          });
      });

      it(`POST '/' should update status of invoice ${invoice.uuid}`, () => {
        invoice.oldStatus = invoice.status;
        invoice.status = invoice.status === InvoiceStatus.inbox ? InvoiceStatus.accepted : InvoiceStatus.inbox;
        return request(myFunctions.invoice)
          .post('/')
          .send(invoice)
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(isOut ? 403 : 200)
          .then(res => {
            if(isOut) {
              assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
            } else {
              assert.deepEqual(res.body.reference, invoice.reference);
            }
            return res;
          });
      });

      it(`POST '/${invoice.status}/${company}/${invoice.uuid}' should update status of invoice ${invoice.uuid}`, () => {
        invoice.reference = 'UPDATED 2 REFERENCE'
        return request(myFunctions.invoice)
          .post(`/${invoice.status}/${company}/${invoice.uuid}`)
          .send({reference: invoice.reference})
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(isOut? 403 : 200)
          .then(res => {
            if(isOut) {
              assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
            } else {
              assert.deepEqual(res.body.reference, invoice.reference);
            }
            return res;
          });
      });

      it(`GET '/${invoice.status}/${company}/${invoice.uuid}' should returns invoice ${invoice.uuid}`, () => {
        return request(myFunctions.invoice)
          .get(`/${invoice.status}/${company}/${invoice.uuid}`)
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(isOut ? 403 : 200)
          .then(res => {
            if(isOut) {
              assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
            } else {
              assert.deepEqual(res.body.uuid, invoice.uuid);
            }
            return res;
          });
      });

      it(`POST '/s' send invoice`, () => {
        const data = {
          company: invoice.company,
          to: TEST_EMAIL,
          invoices: [invoice]
        };
        return request(myFunctions.invoice)
          .post(`/s`)
          .send(data)
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(isOut ? 403 : 200)
          .then(res => {
            if(isOut) {
              assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
            } else {
              assert.deepEqual(res.body, { ok: 'ok' });
            }
            return res;
          });
      });

      it(`DELETE '/${invoice.status}/${company}/${invoice.uuid}' should delete invoice ${invoice.uuid}`, done => {
        request(myFunctions.invoice)
          .delete(`/${invoice.status}/${company}/${invoice.uuid}`)
          .send({status: invoice.status})
          .set('session_id', sessionId)
          .expect(isOut ? 403 : 200)
          .then(res => {
            if(isOut) {
              assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
            }
            done();
          })
          .catch(err => {
            done(err.message);
          });
      });

      it(`DELETE '/' should delete invoices`, done => {
        const params = {
          company: company,
          uuids: [invoice2.uuid],
          status: invoice2.status
        }

        request(myFunctions.invoice)
          .delete('/')
          .send(params)
          .set('session_id', sessionId)
          .expect(isOut? 403 : 200)
          .then(res => {
            if(isOut) {
              assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
            }
            done();
          })
          .catch(err => {
            done(err.message);
          });
      });

    });
  }
}
