import { TediFaker } from '../TediFaker';
import request from 'supertest';
import { assert } from 'chai';

const myFunctions = require('../../src/index');

export class TediCompanyTest {

  public static companyTest(sessionId: string, type: string) : void {
    const isOut = type === 'out';
    // const isRoot = type === 'root';
    // const isAdmin = type === 'admin';
    // const isGestor = type === 'gestor';
    const isReader = type === 'reader';
    const isWriter = type === 'writer';
    const TEST_EMAIL = 'aibanez@aonsolutions.es';

    describe(`Company Test with ${type} user`, () => {

    const company1 = TediFaker.randomCompany();
    // const company2 = TediFaker.randomCompany();
    // const company3 = TediFaker.randomCompany();

    const outMsg = 'error: Token Incorrecto';
    it(isOut ? `GET '/' should return ${outMsg}` : `GET '/' should returns an array of all companies`, () => {
      return request(myFunctions.company)
        .get('/')
        .set('session_id', sessionId)
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(isOut? 403 : 200)
        .then(res => {
          if(isOut) {
            assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
          } else {
            const companies = res.body;
            assert.typeOf(companies, 'array', 'we have an array');
          }
          return res;
        });
    });

    it(isOut ? `GET '/' should return ${outMsg}` : `GET '/' should returns an array of companies with filter document = B01487271`, () => {
      return request(myFunctions.company)
        .get('/')
        .query({document: 'B01487271'})
        .set('session_id', sessionId)
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(isOut? 403 : 200)
        .then(res => {
          if(isOut) {
            assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
          } else {
            const companies = res.body;
            assert.typeOf(companies, 'array', 'we have an array');
      //    assert.deepEqual(companies.length, 1);
      //    assert.deepEqual(companies[0].document, 'B01487271');
          }
          return res;
        });
    });

    it(isOut ?  `GET '/' should return ${outMsg}` : `GET '/' should returns an array of companies with filter name = AON SOLUTIONS`, () => {
      return request(myFunctions.company)
        .get('/')
        .query({name: 'AON SOLUTIONS'})
        .set('session_id', sessionId)
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(isOut? 403 : 200)
        .then(res => {
          if(isOut) {
            assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
          } else {
            const companies = res.body;
            assert.typeOf(companies, 'array', 'we have an array');
      //    assert.deepEqual(companies.length, 1);
      //    assert.deepEqual(companies[0].document, 'B01487271');
          }
          return res;
        });
    });

    it(isOut ? `GET '/user/${TEST_EMAIL}' should return ${outMsg}` : `GET '/user/${TEST_EMAIL}' should returns an array of companies of ${TEST_EMAIL}`, () => {
      return request(myFunctions.company)
        .get(`/user/${TEST_EMAIL}`)
        .set('session_id', sessionId)
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(isOut? 403 : 200)
        .then(res => {
          if(isOut) {
            assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
          } else {
            const companies = res.body;
            assert.typeOf(companies, 'array', 'we have an array');
          }
          return res;
        });
    });

    it(isOut ? `POST '/' should return ${outMsg}` : `POST '/' should adds company ${company1.document}`, () => {
      return request(myFunctions.company)
        .post('/')
        .send(company1)
        .set('session_id', sessionId)
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(isOut || isWriter || isReader ? 403 : 200)
        .then(res => {
          if(isOut) {
            assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
          } else if(isWriter || isReader){
            // @ts-ignore
            const errorMsg = JSON.parse(res.error.text).error;
            assert.deepEqual(errorMsg, 'No tiene permisos para realizar la operación');
          } else {
            assert.deepEqual(res.body, company1);
          }
          return res;
        });
    });

    it(isOut ? `POST '/' should return ${outMsg}` : `POST '/' should be return the error: existing company ${company1.document}`, () => {
      return request(myFunctions.company)
        .post('/')
        .send(company1)
        .set('session_id', sessionId)
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(403)
        .then(res => {
          if(isOut) {
            assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
          } else {
            // @ts-ignore
            const errorMsg = JSON.parse(res.error.text).error;
            assert.deepEqual(errorMsg, isWriter || isReader ? 'No tiene permisos para realizar la operación' : 'La empresa que intenta crear ya existe.');
          }
          return res;
        });
    });

    it(isOut ? `POST '/' should return ${outMsg}` : `POST '/${company1.document}' should update company ${company1.document}`, () => {
      company1.iban = 'ES6621000418401234567891';
      assert.equal(company1.iban, 'ES6621000418401234567891');
      return request(myFunctions.company)
        .post(`/${company1.document}`)
        .send(company1)
        .set('session_id', sessionId)
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(isOut || isWriter || isReader ? 403 : 200)
        .then(res => {
          if(isOut){
             assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
          } else if(!isWriter && !isReader){
            assert.deepEqual(res.body, company1);
          }
          return res;
        });
    });

    it(`GET '/${company1.document}' should returns company ${company1.document}`, () => {
      return request(myFunctions.company)
        .get(`/${company1.document}`)
        .set('session_id', sessionId)
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
          if(!isOut && !isWriter && !isReader) assert.deepEqual(res.body, company1);
          return res;
        });
    });

    it(isOut ? `DELETE '/${company1.document}' should return ${outMsg}` :`DELETE '/${company1.document}' should delete company ${company1.document}`, done => {
      request(myFunctions.company)
        .delete(`/${company1.document}`)
        .set('session_id', sessionId)
        .expect(isOut || isWriter || isReader ? 403 : 200)
        .then(res => {
          if(isOut){
             assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
          } else if(isWriter || isReader){
            // @ts-ignore
            const errorMsg = JSON.parse(res.error.text).error;
            assert.deepEqual(errorMsg, 'No tiene permisos para realizar la operación');
          }
          done();
        })
        .catch(err => {
          if(isOut) done();
          else done(err.message);
        });
    });
  });
  }
  public static deleteTestCompany(sessionId: string) : void {
    describe(`DELETE TEST COMPANY`, () => {
    it(`DELETE '/B99999999' should delete company B99999999`, done => {
      request(myFunctions.company)
        .delete(`/B99999999`)
        .set('session_id', sessionId)
        .expect(200)
        .then(res => {
          done();
        });
    });
    });
  }
}
