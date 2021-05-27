// tslint:disable: no-unused-expression
import { fail } from 'assert';
import { expect } from 'chai';
import fs = require('fs');
import readline = require('readline');
import { of, range } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { Company, Invoice, User } from '../src/tedi-ewok/TediEwok';
import { TediValidator } from '../src/tedi-validator/TediValidator';
import { TediFaker } from './TediFaker';

function logError(what: string) {
  // tslint:disable-next-line: no-console
  console.error('[ERROR]' + what);
}

function habemusError(error: Error) {
  logError(error.message);
  fail('Habemus error. tedi-validator error!');
}

describe('TEDI VALIDATOR TESTS', () => {
  before(done => {
    const lineReader = readline.createInterface({
      input: fs.createReadStream('./test/resources/002.po.txt'),
    });

    lineReader.on('line', line => {
      // tslint:disable-next-line: no-console
      console.log(line);
    });
    lineReader.on('close', () => {
      done();
    });
  });

  const times = 1000;

  it(`${times} random users validation`, done => {
    range(0, times)
      .pipe(map((x: number) => TediFaker.randomUser()))
      .pipe(tap((user: User) => TediValidator.validateUser(user)))
      .subscribe(
        (user: User) => {
          // printUser(user);
          // tslint:disable-next-line: no-unused-expression
          expect(user).not.to.be.null;
        },
        error => {
          habemusError(error);
          done();
        },
        () => done(),
      );
  });

  it('Wrong user validation', done => {
    of(TediFaker.randomUser())
      .pipe(tap((user: User) => user.email = 'jgsdfjhglskdjghsdfh'))
      .pipe(tap((user: User) => TediValidator.validateUser(user)))
      .subscribe(
        (user: User) => fail('Wrong User!'),
        error => {
          expect(error).not.to.be.null;
          done();
        },
        () => done(),
      );
  });

  it(`${times} random companies validation`, done => {
    range(0, times)
      .pipe(map((x: number) => TediFaker.randomCompany()))
      .pipe(tap((company: Company) => TediValidator.validateCompany(company)))
      .subscribe(
        (company: Company) => {
          // printCompany(company);
          // tslint:disable-next-line: no-unused-expression
          expect(company).not.to.be.null;
        },
        error => {
          habemusError(error);
          done();
        },
        () => done(),
      );
  });

  it('Wrong company validation', done => {
    of(TediFaker.randomCompany())
      .pipe(tap((comp) => comp.document = 'jgsdfjhglskdjghsdfh'))
      .pipe(tap((comp) => TediValidator.validateCompany(comp)))
      .subscribe(
        (comp: Company) => fail('Wrong Company!'),
        error => {
          expect(error).not.to.be.null;
          done();
        },
        () => done(),
      );
  });

  it(`${times} random invoices validation`, done => {
    range(0, times)
      .pipe(
        map((x: number) => {
          const invoice: Invoice = TediFaker.randomInvoice();
          invoice.number = x;
          return invoice;
        }),
      )
      .pipe(tap((invoice: Invoice) => TediValidator.validateInvoice(invoice)))
      .subscribe(
        (data: Invoice) => {
          // printInvoice(data);
          // tslint:disable-next-line: no-unused-expression
          expect(data).not.to.be.null;
        },
        error => {
          habemusError(error);
          done();
        },
        () => done(),
      );
  });

  it('Wrong invoice validation', done => {
    of(TediFaker.randomInvoice())
      .pipe(tap((invoice) => invoice.reference = '0123456789012345678901234567890123456789'))
      .pipe(tap((invoice) => TediValidator.validateInvoice(invoice)))
      .subscribe(
        (invoice: Invoice) => fail('Wrong Invoice!'),
        error => {
          expect(error).not.to.be.null;
          done();
        },
        () => done(),
      );
  });

});
