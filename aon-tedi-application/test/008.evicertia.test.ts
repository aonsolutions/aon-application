import { assert } from 'chai';
import { TediEvicertia } from '../src/tedi-evicertia/evicertia';
import { TediFaker } from './TediFaker';
import { TediSepaPDFBuilder, TediContractPDFBuilder } from '../src/tedi-pdf-builder/TediPdfBuilder';
import { TediPlanEnum, TediPlanPeriod } from '../src/tedi-ewok/TediModel';

const TEST_EMAIL = 'tedi.tests1@gmail.com';

describe('TEDI EVICERTIA ', () => {
  it('EVICERTIA ' + TEST_EMAIL, done => {
    const user = TediFaker.randomUser();
    user.email = TEST_EMAIL;
    user.phone = '658785498';
    const company = TediFaker.randomCompany();
    company.document = 'B01487271';
    company.plan = { plan: TediPlanEnum.PLAN10, period: TediPlanPeriod.MENSUAL};
    company.company = 'B01487271';
    company.iban = 'ES458798574875898751';
    company.bic = 'VBBNUAXX';
    TediEvicertia.contract(user, company).subscribe(
      (res: any) => {
        assert.exists(res.uniqueId);
        done();
      }
    );
  });

  it('Generate SEPA', done => {
    const company = TediFaker.randomCompany();
    company.document = 'B01487271';
    company.company = 'B01487271';
    company.iban = 'ES458798574875898751';
    company.bic = 'VBBNUAXX';
    TediSepaPDFBuilder.build(company, 154).subscribe(
      r => {
        done();
      },
      e => {
        done();
      }
    )
  });

  it('Generate Contract', done => {
    const user = TediFaker.randomUser();
    const company = TediFaker.randomCompany();
    company.document = 'B01487271';
    company.company = 'B01487271';
    company.plan = { plan: TediPlanEnum.PLANPYME, period: TediPlanPeriod.MENSUAL};
    company.iban = 'ES458798574875898751';
    company.bic = 'VBBNUAXX';
    TediContractPDFBuilder.build(user, company, 21, 22).subscribe(
      r => {
        done();
      },
      e => {
        done();
      }
    )
  });
});
