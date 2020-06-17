// tslint:disable: no-console

import { fail } from 'assert';
import { expect } from 'chai';
import fs = require('fs');
import readline = require('readline');
import { tap } from 'rxjs/operators';
import { TediBulkParser } from '../src/tedi-bulk-parser/TediBulkParser';
import { TediValidator } from '../src/tedi-validator/TediValidator';

describe('TEDI PARSER TEST', () => {
  const resourcesPath = 'test/resources/';

  before(done => {
    const lineReader = readline.createInterface({
      input: fs.createReadStream('./test/resources/004.yogui.txt'),
    });

    lineReader.on('line', line => {
      // tslint:disable-next-line: no-console
      console.log(line);
    });
    lineReader.on('close', () => {
      done();
    });
  });

  it('TEST-0 . AON PARSER', done => {
    TediBulkParser.extractCSVFromFile(resourcesPath + '004.yogui.0.aon')
      .pipe(tap(invoice => TediValidator.validateInvoice(invoice)))
      .subscribe(
        invoice => {
          // TediPrint.printInvoice(invoice);
          expect(invoice).to.not.be.null.and.to.be.equal('EMITIDA');
        },
        error => {
          console.log(JSON.stringify(error, null, 1));
          fail(error);
          done();
        },
        () => {
          done();
        },
      );
  });

  it('TEST-1 . CSV PARSER', (done) => {
    TediBulkParser.extractCSVFromFile(resourcesPath + '004.yogui.1.csv')
      .pipe(tap(invoice => TediValidator.validateInvoice(invoice)))
      .subscribe(
        row => expect(row).to.not.be.null.and.to.be.equal('EMITIDA'),
        error => {
          console.log(JSON.stringify(error, null, 1));
          fail(error);
          done();
        },
        () => {
          done();
        },
      );
  });

  it('TEST-2 . XLSX PARSER', (done) => {
    TediBulkParser.extractXLSXFromFile(resourcesPath + '004.yogui.2.xlsx')
      .pipe(tap(invoice => TediValidator.validateInvoice(invoice)))
      .subscribe(
        row => expect(row).to.not.be.null.and.to.be.equal('EMITIDA'),
        error => {
          console.log(JSON.stringify(error, null, 1));
          fail(error);
          done();
        },
        () => {
          done();
        },
      );
  });

  it('TEST-3 . XLS PARSER', (done) => {
    TediBulkParser.extractXLSXFromFile(resourcesPath + '004.yogui.3.xlsx')
    .pipe(tap(invoice => TediValidator.validateInvoice(invoice)))
    .subscribe(
        row => expect(row).to.not.be.null.and.to.be.equal('EMITIDA'),
        error => {
          console.log(JSON.stringify(error, null, 1));
          fail(error);
          done();
        },
        () => {
          done();
        },
      );
  });

  it('TEST-4 . ODS PARSER', (done) => {
    TediBulkParser.extractXLSXFromFile(resourcesPath + '004.yogui.4.ods')
    .pipe(tap(invoice => TediValidator.validateInvoice(invoice)))
      .subscribe(
        row => expect(row).to.not.be.null.and.to.be.equal('EMITIDA'),
        error => {
          console.log(JSON.stringify(error, null, 1));
          fail(error);
          done();
        },
        () => {
          done();
        },
      );
  });

  it('TEST-5 . XML SII EMITIDAS PARSER', (done) => {
    TediBulkParser.extractXMLSII(resourcesPath + '004.yogui.5.xml')
    .pipe(tap(invoice => TediValidator.validateInvoice(invoice)))
      .subscribe(
        row => expect(row).to.not.be.null.and.to.be.equal('EMITIDA'),
        error => {
          console.log(JSON.stringify(error, null, 1));
          fail(error);
          done();
        },
        () => {
          done();
        },
      );
  });

  it('TEST-6 . XML SII RECIBIDAS PARSER', (done) => {
    TediBulkParser.extractXMLSII(resourcesPath + '004.yogui.6.xml')
    .pipe(tap(invoice => TediValidator.validateInvoice(invoice)))
      .subscribe(
        row => expect(row).to.not.be.null.and.to.not.be.equal('EMITIDA'),
        error => {
          console.log(JSON.stringify(error, null, 1));
          fail(error);
          done();
        },
        () => {
          done();
        },
      );
  });

  it('TEST-7 . EXCEL PARSER', (done) => {
    TediBulkParser.xextractXLSXFromFile(resourcesPath + '004.yogui.7.xlsx')
    .pipe(tap(invoice => TediValidator.validateInvoice(invoice)))
      .subscribe(
        row => {},
        error => {
          console.log(JSON.stringify(error, null, 1));
          fail(error);
          done();
        },
        () => {
          done();
        },
      );
  });
});
