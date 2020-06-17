import { TediFaker } from '../TediFaker';
import request from 'supertest';
import { assert } from 'chai';

const myFunctions = require('../../src/index');

export class TediUserTest {

  public static userTest(sessionId: string, type: string) : void {
    const isOut = type === 'out';
    // const isRoot = type === 'root';
    // const isAdmin = type === 'admin';
    const isReader = type === 'reader';
    // const isWriter = type === 'writer';
    const TEST_EMAIL = 'tedi.tests1@gmail.com';
    describe(`User Test with ${type} user`, () => {
      const user = TediFaker.randomUser();
      const outMsg = 'error: Token Incorrecto';
      it(isOut? `GET '/' should return ${outMsg}` :`GET '/' should returns an array of all users`, () => {
        return request(myFunctions.user)
          .get('/')
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(isOut ? 403 : 200)
          .then(res => {
            if(isOut){
               assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
            } else {
              // res.body, res.headers, res.status
              const users = res.body;
              assert.typeOf(users, 'array', 'we have an array');
            }
            return res;
          });
      });

      it(isOut ? `GET '/' should return ${outMsg}` :`GET '/' should returns an array of all users with filter admin = true`, () => {
        return request(myFunctions.user)
          .get('/')
          .query({admin: true})
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(isOut ? 403 : 200)
          .then(res => {
            if(isOut){
               assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
            } else {
              const users = res.body;
              assert.typeOf(users, 'array', 'we have an array');
            }
            return res;
          });
      });

      it(isOut? `GET '/' should return ${outMsg}` : `GET '/' should returns an array of all users with filter gestor = true`, () => {
        return request(myFunctions.user)
          .get('/')
          .query({gestor: true})
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(isOut ? 403 : 200)
          .then(res => {
            if(isOut){
               assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
            } else {
              const users = res.body;
              assert.typeOf(users, 'array', 'we have an array');
            }
            return res;
          });
      });

      it(isOut ? `POST '/' should return ${outMsg}` : `POST '/' should adds user ${user.email}`, () => {
        return request(myFunctions.user)
          .post('/')
          .send(user)
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(isOut ? 403 : 200)
          .then(res => {
            if(isOut){
               assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
            } else if(!isReader) {
              delete res.body.password;
              delete user.password;
              assert.deepEqual(res.body, user);
            }
            return res;
          });
      });

      it(isOut ? `POST '/${user.email}' should return ${outMsg}` : `POST '/${user.email}' should update user ${user.email}`, () => {
        user.name = 'Updated Name';
        return request(myFunctions.user)
          .post(`/${user.email}`)
          .send(user)
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(isOut ? 403 : 200)
          .then(res => {
            if(isOut){
               assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
            } else if(!isReader){
              delete res.body.password;
              delete user.password;
              assert.deepEqual(res.body, user);
            }
            return res;
          });
      });

      it(isOut ? `GET '/${user.email}' should return ${outMsg} ` : `GET '/${user.email}' should returns user ${user.email}`, () => {
        return request(myFunctions.user)
          .get(`/${user.email}`)
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(isOut ? 403 : 200)
          .then(res => {
            if(isOut){
               assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
            } else {
              delete res.body.password;
              delete user.password;
              assert.deepEqual(res.body, user);
            }
            return res;
          });
      });

      it(isOut ? `DELETE '/${user.email}' should return ${outMsg}` : `DELETE '/${user.email}' should delete user ${user.email}`, done => {
        request(myFunctions.user)
          .delete(`/${user.email}`)
          .set('session_id', sessionId)
          .expect(isOut ? 403 : 200)
          .then(res => {
            if(isOut){
               assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
            }
            done();
          })
          .catch(err => {
            done(err.message);
          });
      });

      it(`POST '/password should update user ${TEST_EMAIL}`, () => {
        const updateUser = {
          email: TEST_EMAIL
        }
        return request(myFunctions.user)
          .post(`/password`)
          .send(updateUser)
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(200)
          .then(res => {
            assert.deepEqual(res.body, {ok:'ok'});
            return res;
          });
      });

      it(isOut ? `POST '/${TEST_EMAIL}' should return ${outMsg}` : `POST '/${TEST_EMAIL}' should update user password ${TEST_EMAIL}`, () => {
        const updateUser = {
          email: TEST_EMAIL,
          password: 'test'
        };
        return request(myFunctions.user)
          .post(`/${updateUser.email}`)
          .send(updateUser)
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(isOut ? 403 : 200)
          .then(res => {
            if(isOut){
               assert.equal(JSON.parse(res.text).error, 'Token incorrecto');
            } else {
              delete res.body.password;
              delete updateUser.password;
              assert.deepEqual(res.body, updateUser);
            }
            return res;
          });
      });
    });
  }

  public static createTestUsers(sessionId: string) {
    describe(`CREATE TEST USERS`, () => {
    it(`POST '/' should add GESTOR user 'tedi.tests2@gmail.com'`, () => {
      const gestorUser = {
        email: 'tedi.tests2@gmail.com',
        active:true,
        actual_company: 'B99999999',
        document: '11111111H',
        company: 'B99999999',
        name:'Tedi',
        surname: 'Tests',
        root: false,
        admin: false,
        gestor:true,
        password: 'test',
        permissions:[
          {
            name:'Gestión de Facturas Emitidas',
            reader: true,
            tag: 'invoice_issued',
            writer: true
          },
          {
            name:'Gestión de Facturas Recibidas',
            reader: true,
            tag: 'invoice_received',
            writer: true
          },
          {
            name:'Gestión de Facturas Tickets',
            reader: true,
            tag: 'invoice_ticket',
            writer: true
          }
        ]
      };
      return request(myFunctions.user)
        .post('/')
        .send(gestorUser)
        .set('session_id', sessionId)
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
          delete res.body.password;
          delete gestorUser.password;
          assert.deepEqual(res.body, gestorUser);
          return res;
        });
    });

    it(`POST '/' should add WRITER user 'tedi.tests2@gmail.com'`, () => {
      const writerUser = {
        email: 'tedi.tests3@gmail.com',
        active:true,
        actual_company: 'B99999999',
        document: '11111111H',
        company: 'B99999999',
        name:'Tedi',
        surname: 'Tests',
        root: false,
        admin: false,
        gestor:false,
        password: 'test',
        permissions:[
          {
            name:'Gestión de Facturas Emitidas',
            reader: true,
            tag: 'invoice_issued',
            writer: true
          },
          {
            name:'Gestión de Facturas Recibidas',
            reader: true,
            tag: 'invoice_received',
            writer: true
          },
          {
            name:'Gestión de Facturas Tickets',
            reader: true,
            tag: 'invoice_ticket',
            writer: true
          }
        ]
      };
      return request(myFunctions.user)
        .post('/')
        .send(writerUser)
        .set('session_id', sessionId)
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
          delete res.body.password;
          delete writerUser.password;
          assert.deepEqual(res.body, writerUser);
          return res;
        });
    });

    it(`POST '/' should add READER user 'tedi.tests2@gmail.com'`, () => {
      const writerUser = {
        email: 'tedi.tests4@gmail.com',
        active:true,
        actual_company: 'B99999999',
        document: '11111111H',
        company: 'B99999999',
        name:'Tedi',
        surname: 'Tests',
        root: false,
        admin: false,
        gestor:false,
        password: 'test',
        permissions:[
          {
            name:'Gestión de Facturas Emitidas',
            reader: true,
            tag: 'invoice_issued',
            writer: false
          },
          {
            name:'Gestión de Facturas Recibidas',
            reader: true,
            tag: 'invoice_received',
            writer: false
          },
          {
            name:'Gestión de Facturas Tickets',
            reader: true,
            tag: 'invoice_ticket',
            writer: false
          }
        ]
      };
      return request(myFunctions.user)
        .post('/')
        .send(writerUser)
        .set('session_id', sessionId)
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
          delete res.body.password;
          delete writerUser.password;
          assert.deepEqual(res.body, writerUser);
          return res;
        });
    });

    it(`POST '/tedi.tests1@gmail.com' should active admin user 'tedi.tests1@gmail.com'`, () => {
      const adminUser = {
        email: 'tedi.tests1@gmail.com',
        active:true,
      };
      return request(myFunctions.user)
        .post('/tedi.tests1@gmail.com')
        .send(adminUser)
        .set('session_id', sessionId)
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(res => {
          delete res.body.password;

          assert.deepEqual(res.body, adminUser);
          return res;
        });
    });
  });
  }

  public static deleteTestUsers(sessionId: string) : void {
    describe(`DELETE TEST USERS`, () => {
    it(`DELETE '/tedi.tests1@gmail.com' should delete company tedi.tests1@gmail.com`, done => {
      request(myFunctions.user)
        .delete(`/tedi.tests1@gmail.com`)
        .set('session_id', sessionId)
        .expect(200)
        .then(res => {
          done();
        });
    });

    it(`DELETE '/tedi.tests2@gmail.com' should delete company tedi.tests2@gmail.com`, done => {
      request(myFunctions.user)
        .delete(`/tedi.tests2@gmail.com`)
        .set('session_id', sessionId)
        .expect(200)
        .then(res => {
          done();
        });
    });

    it(`DELETE '/tedi.tests3@gmail.com' should delete company tedi.tests3@gmail.com`, done => {
      request(myFunctions.user)
        .delete(`/tedi.tests3@gmail.com`)
        .set('session_id', sessionId)
        .expect(200)
        .then(res => {
          done();
        });
    });

    it(`DELETE '/tedi.tests4@gmail.com' should delete company tedi.tests4@gmail.com`, done => {
      request(myFunctions.user)
        .delete(`/tedi.tests4@gmail.com`)
        .set('session_id', sessionId)
        .expect(200)
        .then(res => {
          done();
        });
    });
    });
  }
}
