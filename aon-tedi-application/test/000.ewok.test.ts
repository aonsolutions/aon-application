// tslint:disable: no-unused-expression
import { expect } from 'chai';
import fs = require('fs');
import readline = require('readline');
import { Company, Invoice, TediMath, TediPrint, TediString, User, Nif, NifType } from '../src/tedi-ewok/TediEwok';
import { TediFaker } from './TediFaker';

describe('', () => {
  before(done => {
    const lineReader = readline.createInterface({
      input: fs.createReadStream('./test/resources/000.ewok.txt'),
    });

    lineReader.on('line', line => {
      // tslint:disable-next-line: no-console
      console.log(line);
    });
    done();
  });
  describe('TediString Tests', () => {
    it('defaultEmpty()', done => {
      expect('').to.be.equal(TediString.defaultEmpty(undefined));
      expect('').to.be.equal(TediString.defaultEmpty(null));
      expect('').not.to.be.equal(TediString.defaultEmpty('dsfkljh'));
      done();
    });

    it('isBlank()', done => {
      expect(true).to.be.equal(TediString.isBlank(undefined));
      expect(true).to.be.equal(TediString.isBlank(null));
      expect(true).to.be.equal(TediString.isBlank(''));
      expect(true).to.be.equal(TediString.isBlank(' '));
      expect(false).to.be.equal(TediString.isBlank('notEmpty'));
      done();
    });

    it('defaultBlank()', done => {
      expect(' ').to.be.equal(TediString.defaultBlank(undefined));
      expect(' ').to.be.equal(TediString.defaultBlank(null));
      expect(' ').not.to.be.equal(TediString.defaultBlank('dsfkljh'));
      done();
    });

    it('pad()', done => {
      expect('     ').to.be.equal(TediString.pad(undefined, 5));
      expect('  A  ').to.be.equal(TediString.pad('A', 5));
      expect('..A..').to.be.equal(TediString.pad('A', 5, '.'));
      done();
    });

    it('trim()', done => {
      expect('').to.be.equal(TediString.trim(undefined));
      expect('').to.be.equal(TediString.trim('   '));
      expect('A').to.be.equal(TediString.trim('   A'));
      expect('A').to.be.equal(TediString.trim('   A    '));
      expect('A').to.be.equal(TediString.trim('A    '));
      done();
    });

    it('repeat()', done => {
      expect('     ').to.be.equal(TediString.repeat(undefined, 5));
      expect('AAAAA').to.be.equal(TediString.repeat('A', 5));
      expect('ABABABABAB').to.be.equal(TediString.repeat('AB', 5));
      done();
    });

    it('polish()', done => {
      expect('').to.be.equal(TediString.polish(undefined));
      expect('CALLE DUQUE DE WELLINGTON').to.be.equal(TediString.polish('CALLE DUQUE DE WELLINGTON'));
      expect('CALLE DUQUE DE WELLINGTON').to.be.equal(TediString.polish('   CALLE DUQUE DE    WELLINGTON  '));
      expect('CALLE DUQUE DE WELLINGTON').to.be.equal(TediString.polish('     CALLE     DUQUE DE WELLINGTON'));
      expect('CALLE DUQUE DE WELLINGTON').to.be.equal(TediString.polish(' CALLE    DUQUE   DE  WELLINGTON '));
      expect('CALLE DUQUE DE WELLINGTON').to.be.equal(TediString.polish('  CALLE DUQUE          DE WELLINGTON         '));
      expect('CALLE DUQUE DE WELLINGTON').to.be.equal(TediString.polish('    CALLE DUQUE DE WELLINGTON       '));
      expect('CALLE DUQUE DE WELLINGTON').to.be.equal(TediString.polish(' CALLE         DUQUE        DE WELLINGTON '));
      done();
    });
  });

  describe('TediMath Tests', () => {
    it('round()', done => {
      expect(TediMath.round(NaN)).to.be.NaN;
      expect(0).to.be.equal(TediMath.round(0, 0));
      expect(1).to.be.equal(TediMath.round(0.6, 0));
      expect(1).to.be.equal(TediMath.round(0.7, 0));
      expect(1).to.be.equal(TediMath.round(1.1, 0));
      expect(1).to.be.equal(TediMath.round(1.4, 0));
      expect(1).to.be.equal(TediMath.round(1, 0));

      expect(0.0).to.be.equal(TediMath.round(0, 2));
      expect(0.6).to.be.equal(TediMath.round(0.6, 2));
      expect(0.7).to.be.equal(TediMath.round(0.7, 2));
      expect(1.1).to.be.equal(TediMath.round(1.1, 2));
      expect(1.15).to.be.equal(TediMath.round(1.15, 2));
      expect(1.4).to.be.equal(TediMath.round(1.4, 2));
      expect(1.0).to.be.equal(TediMath.round(1, 2));

      expect(0.01).to.be.equal(TediMath.round(0.006, 2));
      expect(0.61).to.be.equal(TediMath.round(0.605, 2));
      expect(0.71).to.be.equal(TediMath.round(0.708, 2));
      expect(1.1).to.be.equal(TediMath.round(1.104, 2));
      expect(1.16).to.be.equal(TediMath.round(1.156, 2));
      expect(1.41).to.be.equal(TediMath.round(1.408, 2));
      expect(1.01).to.be.equal(TediMath.round(1.005, 2));

      expect(11.29).to.be.equal(TediMath.round(1.1287e1, 2));
      expect(0.12).to.be.equal(TediMath.round(1.1887e-1, 2));
      done();
    });

    it('defaultZero()', done => {
      expect(0).to.be.equal(TediMath.defaultZero(undefined));
      expect(0).to.be.equal(TediMath.defaultZero(null));
      expect(0).to.be.equal(TediMath.defaultZero(0));
      expect(0).not.to.be.equal(TediMath.defaultZero(12));
      done();
    });

    it('ensure()', done => {
      expect(0).to.be.equal(TediMath.ensure(undefined));
      expect(0).to.be.equal(TediMath.ensure(null));
      expect(0).to.be.equal(TediMath.ensure(NaN));
      expect(0).to.be.equal(TediMath.ensure(Infinity));
      expect(0).to.be.equal(TediMath.ensure(0));
      expect(1).to.be.equal(TediMath.ensure(1));

      expect(5).to.be.equal(TediMath.ensure(undefined,5));
      expect(5).to.be.equal(TediMath.ensure(null,5));
      expect(5).to.be.equal(TediMath.ensure(NaN,5));
      expect(5).to.be.equal(TediMath.ensure(Infinity,5));
      expect(0).to.be.equal(TediMath.ensure(0,5));
      expect(1).to.be.equal(TediMath.ensure(1));
      done();
    });
  });

  describe('TediPrint Tests', () => {
    it('COMPANY CONSOLE PRINT', done => {
      const company: Company = TediFaker.randomCompany();
      TediPrint.printCompany(company);
      done();
    });

    it('INVOICE CONSOLE PRINT', done => {
      const invoice: Invoice = TediFaker.randomInvoice();
      TediPrint.printInvoice(invoice);
      done();
    });

    it('USER CONSOLE PRINT', done => {
      const user: User = TediFaker.randomUser();
      TediPrint.printUser(user);
      done();
    });
  });

	describe('TediNif Tests', () => {
    it('DNI', done => {
			expect(Nif.isDni('83989926K')).to.be.true;
			expect(Nif.isDni('83989926k')).to.be.true;
			expect(Nif.isDni('83969926K')).to.be.false;
      done();
    });
		it('NIE', done => {
			expect(Nif.isNie('Y8808677J')).to.be.true;
			expect(Nif.isNie('y8808677J')).to.be.true;
			expect(Nif.isNie('Y3625695M')).to.be.false;
      done();
    });
		it('CIF', done => {
			expect(Nif.isCif('B38440566')).to.be.true;
			expect(Nif.isCif('b38440566')).to.be.true;
			expect(Nif.isCif('B38640566')).to.be.false;
      done();
    });
		it('PARSE', done => {
			[
				'B95738324',
				'B47509591',
				'B03301801',
				'B11609856',
				'B85536308',
				'A14466940',
				'F95722369',
				'B01132703',
				'26001810B',
				'B86962495',
				'B23762503',
				'B04299996',
				'B84994011',
				'B85880938',
				'53659046T',
				'B25413535',
				'12428686S',
				'B26322891',
				'b96122510',
				'B92977461',
				'B85445633',
				'51540505n',
				'B97611248',
				'b84353572'
			].forEach(doc => expect(Nif.parse(doc)).to.have.property('str', doc) )
      done();
    });
		it('FIND', done => {
			expect(Nif.find('Calle Joan Cirera Pons, 13 Bajo El Prat de Llobregat CIF / NIF: 00B67129247'))
			.to.have.lengthOf(1)
			.that.deep.include({ str: 'B67129247', type: NifType.CIF} )
			;
			expect(Nif.find(`
			Calle Joan Cirera Pons
			,13 Bajo El Prat de Llobregat
			CIF / NIF: 00B67129247`))
			.to.have.lengthOf(1)
			.that.deep.include({ str: 'B67129247', type: NifType.CIF} )
			;

			expect(Nif.find('A-82018474'))
			.to.have.lengthOf(1)
			.that.deep.include({ str: 'A82018474', type: NifType.CIF} )
			;

			expect(Nif.find('TELEFONICA DE ESPAÑA, SAU. Gran Vía, 28. 28013 - Madrid. R.M.de Madrid, Hª M-213180, Fº 6, Tº 13.170,I. 1ª. CIF Nº A-82018474'))
			.to.have.lengthOf(1)
			.that.deep.include({ str: 'A82018474', type: NifType.CIF} )
			;

			expect(Nif.find(`
				TELEFONICA DE ESPAÑA, SAU.
				Gran Vía, 28. 28013 - Madrid. R.M.de Madrid,
				00B67129247
				Hª M-213180,
				B68129247
				Fº 6, Tº 13.170,I. 1ª.
				CIF Nº A-82018474`))
			.to.have.lengthOf(2)
			.that.deep.include({ str: 'A82018474', type: NifType.CIF} )
			.that.deep.include({ str: 'B67129247', type: NifType.CIF} )
			;

      done();
    });

  });
});
