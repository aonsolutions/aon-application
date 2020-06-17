import { assert } from 'chai';
import request from 'supertest';
import { Company, User, TediPlanPeriod, TediPlanEnum} from '../../src/tedi-ewok/TediEwok';
// import { assert } from 'chai';

const myFunctions = require('../../src/index');

export class TediRegisterTest {

  public static registerTest(sessionId: string, type: string) : void {
    describe(`Register Test with ${type} user`, () => {
      it(`POST '/' should register new company & admin user`, () => {
        const company = this.testCompany();
        const user = this.testUserAdmin();

        const r = {
          company: company,
          user: user
        }
        return request(myFunctions.register)
          .post('/')
          .send(r)
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(200)
          .then(res => {
            assert.deepEqual(res.status, 200);
            return res;
          }
        );
      });
    });
  }

  private static testCompany() : Company {
      return {
        active: true,
        document: 'B99999999',
        company: 'B99999999',
        name: 'TEDI TESTS',
        address: {
          country: 'ES',
          address: 'DUQUE DE WELLINGTON',
          postal_code: '01010',
       },
       plan: {
         period: TediPlanPeriod.MENSUAL,
         plan: TediPlanEnum.PLAN100
       },
       iban: 'ES820000000000000000000000',
       users: [
         'tedi.tests1@gmail.com',
         'tedi.tests2@gmail.com',
         'tedi.tests3@gmail.com',
         'tedi.tests4@gmail.com'
       ]

     };
  }

  private static testUserAdmin() : User {
      return {
        active: true,
        email: 'tedi.tests1@gmail.com',
        actual_company: 'B99999999',
        document: 'B99999999',
        company: 'B99999999',
        name:'Tedi',
        surname: 'Tests 1',
        root: false,
        admin: true,
        gestor:true,
        password: 'test'
     };
  }
}
