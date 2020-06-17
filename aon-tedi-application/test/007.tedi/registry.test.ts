import { TediFaker } from '../TediFaker';
import request from 'supertest';
import { assert } from 'chai';

const myFunctions = require('../../src/index');

export class TediRegistryTest {



  public static registryTest(sessionId: string, type: string) : void {
    describe(`Registry Test with ${type} user`, () => {

      const registry = TediFaker.randomRegistry();

      it(`GET '/' should returns an array of all registries`, () => {
        return request(myFunctions.registry)
          .get('/')
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(403)
          .then(res => {
            // @ts-ignore
            const errorMsg = JSON.parse(res.error.text).error;
            assert.deepEqual(errorMsg, 'Invalid registry filter');

            return res;
          });
      });

      it(`GET '/' should returns an array of all registries with filter document = B01487271`, () => {
        return request(myFunctions.registry)
          .get('/')
          .query({document: 'B01487271'})
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(200)
          .then(res => {
            // res.body, res.headers, res.status
            const registries = res.body;
            assert.typeOf(registries, 'array', 'we have an array');
            return res;
          });
      });

      it(`GET '/' should returns an array of all registries with filter name = AON SOLUTIONS`, () => {
        return request(myFunctions.registry)
          .get('/')
          .query({name: 'AON SOLUTIONS'})
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(200)
          .then(res => {
            // res.body, res.headers, res.status
            const registries = res.body;
            assert.typeOf(registries, 'array', 'we have an array');
            return res;
          });
      });

      it(`POST '/' should adds registry ${registry.document}`, () => {
        return request(myFunctions.registry)
          .post('/')
          .send(registry)
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(200)
          .then(res => {
            assert.deepEqual(res.body, registry);
            return res;
          });
      });

      it(`POST '/${registry.document}' should update registry ${registry.document}`, () => {
        registry.name = 'Updated Name';
        return request(myFunctions.registry)
          .post(`/${registry.document}`)
          .send(registry)
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(200)
          .then(res => {
            assert.deepEqual(res.body, registry);
            return res;
          });
      });

      it(`GET '/${registry.document}' should returns registry ${registry.document}`, () => {
        return request(myFunctions.registry)
          .get(`/${registry.document}`)
          .set('session_id', sessionId)
          .set('Accept', 'application/json')
          .expect('Content-Type', /json/)
          .expect(200)
          .then(res => {
            assert.deepEqual(res.body, registry);
            return res;
          });
      });

      it(`DELETE '/${registry.document}' should delete registry ${registry.document}`, done => {
        request(myFunctions.registry)
          .delete(`/${registry.document}`)
          .set('session_id', sessionId)
          .expect(200)
          .then(res => {
            done();
          })
          .catch(err => {
            done(err.message);
          });
      });
    });
  }
}
