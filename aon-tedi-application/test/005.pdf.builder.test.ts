import { expect } from 'chai';
import fs = require('fs');
import readline = require('readline');
import { range } from 'rxjs';
import { filter, map, mergeMap, tap } from 'rxjs/operators';
import { InvoiceType, PrinterConfiguration } from '../src/tedi-ewok/TediEwok';
import { TediPDFBuilder } from '../src/tedi-pdf-builder/invoice';
import { TediValidator } from '../src/tedi-validator/TediValidator';
import { TediFaker } from './TediFaker';

describe('TEDI PDF BUILDER TEST', () => {
  before(done => {
    const lineReader = readline.createInterface({
      input: fs.createReadStream('./test/resources/005.bubu.txt'),
    });

    lineReader.on('line', line => {
      // tslint:disable-next-line: no-console
      console.log(line);
    });
    lineReader.on('close', () => {
      done();
    });
  });

  it('TEST-0 . PDF BUILD --> Observable', done => {
    const props: PrinterConfiguration = {
      header: 100,
      footer: 100,
      detailed: false,
      adjustment: false,
    };

    range(0, 100)
      .pipe(tap(n => (props.detailed = n % 2 === 0)))
      .pipe(map(() => TediFaker.randomInvoice()))
      .pipe(filter(inv => inv.type === InvoiceType.EMITIDA))
      .pipe(tap(inv => TediValidator.validateInvoice(inv)))
      .pipe(mergeMap(inv => TediPDFBuilder.build$(inv, props)))
      .subscribe(
        result => {
          // tslint:disable-next-line: no-unused-expression
          expect(result).to.not.be.null;
          // const fileName = '/tmp/01.bubu.test_' + faker.system.commonFileName('pdf');
          // fs.createWriteStream(fileName).write(Buffer.from(result, 'base64'));
        },
        error => done(error),
        () => done(),
      );
  });

  it('TEST-1 . PDF PREVIEW BUILD (NO DETAILS) --> Observable', done => {
    const props: PrinterConfiguration = {
      header: 100,
      footer: 100,
      detailed: false,
      adjustment: false,
    };

    TediPDFBuilder.buildPreview$('B66941873', props).subscribe(
      (result: string) => {
        // const fileName = '/tmp/01.bubu.test_preview_no_details.pdf';
        // tslint:disable-next-line: no-unused-expression
        expect(result).to.not.be.null;
        // fs.createWriteStream(fileName).write(Buffer.from(result, 'base64'));
      },
      (error) => done(error),
      () => done(),
    );
  });

  it('TEST-2 . PDF PREVIEW BUILD (DETAILS) --> Observable', done => {
    const props: PrinterConfiguration = {
      header: 100,
      footer: 100,
      detailed: true,
      adjustment: false,
    };

    TediPDFBuilder.buildPreview$('B66941873', props).subscribe(
      (result: string) => {
        // tslint:disable-next-line: no-unused-expression
        expect(result).to.not.be.null;
        // const fileName = '/tmp/01.bubu.test_preview_details.pdf';
        // fs.createWriteStream(fileName).write(Buffer.from(result, 'base64'));
      },
      (error: string | Error) => done(error),
      () =>done()
    );
  });

});
