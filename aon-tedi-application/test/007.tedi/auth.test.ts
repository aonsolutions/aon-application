import { assert } from 'chai';
import { TediCompanyTest, TediUserTest, TediInvoiceTest, TediRegisterTest, TediRegistryTest} from './TediTests'

const myFunctions = require('../../src/index');

export class TediAuthTest {

  public static authTest(sessionId: string, type: number) : void {
    const types = ['out', 'root', 'admin', 'gestor', 'writer', 'reader'];
    const isOut = types[type] === 'out';
    const isRoot = types[type] === 'root';
    const isAdmin = types[type] === 'admin';
    const isGestor = types[type] === 'gestor';
    const isReader = types[type] === 'reader';
    const isWriter = types[type] === 'writer';

    let TEST_EMAIL: string;
    if(isRoot) TEST_EMAIL = 'tedi.tests@gmail.com'
    else if(isAdmin) TEST_EMAIL = 'tedi.tests1@gmail.com'
    else if(isGestor) TEST_EMAIL = 'tedi.tests2@gmail.com'
    else if(isWriter) TEST_EMAIL = 'tedi.tests3@gmail.com'
    else if(isReader) TEST_EMAIL = 'tedi.tests4@gmail.com'
    if(isOut) {
      describe('AUTH - WITHOUT USER', () => {
        const sessionId = 'LsJgU81czVQRlPM';
        const OUT = 'out';
        it('should return a 403 Forbidden', async done => {
          await myFunctions.auth(
            { method: 'PUT' },
            {
              status: code => {
                assert.equal(code, 403);
                return {
                  send: res => {
                    assert.equal(res, 'Forbidden!');
                    TediRegisterTest.registerTest(sessionId, OUT);
                    TediRegistryTest.registryTest(sessionId, OUT);
                    TediCompanyTest.companyTest(sessionId, OUT);
                    TediUserTest.userTest(sessionId, OUT);
                    TediInvoiceTest.invoiceTest(sessionId, OUT);
                    TediAuthTest.authTest(sessionId, 1);
                    done();
                  },
                };
              },
            },
          );
        });
      });
    } else {
      describe(`AUTH - ${types[type]} USER`, () => {
        it(`should return a JWT(JSON Web Token) - ${types[type]} User`, done => {
          myFunctions.auth(
            {
              method: 'GET',
              headers: {
                origin: 'any.domain.com',
              },
              body: {
                email: TEST_EMAIL,
                password: 'test',
              },
            },
            {
              status: code => {
                assert.equal(code, 200);
                return {
                  json: res => {
                    assert.exists(res.user);
                    assert.exists(res.user.name);
                    assert.exists(res.session_id);
                    assert.equal(res.user.name, 'Tedi');
                    if(isRoot) {
                      TediUserTest.createTestUsers(res.session_id)
                    }
                    TediCompanyTest.companyTest(res.session_id, types[type]);
                    TediUserTest.userTest(res.session_id, types[type]);
                    TediInvoiceTest.invoiceTest(res.session_id, types[type]);
                    if(!isReader){
                      TediAuthTest.authTest(isRoot ? res.session_id : sessionId, type+1);
                    } else {
                      TediUserTest.deleteTestUsers(sessionId);
                      TediCompanyTest.deleteTestCompany(sessionId);
                    }

                    done();
                  },
                };
              },
              // tslint:disable-next-line: no-empty
              getHeader: header => {},
              // tslint:disable-next-line: no-empty
              setHeader: (header, value) => {},
            },
          );
        });
      });
    }
  }
}
