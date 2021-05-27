import { expect } from 'chai';
import fs = require('fs');
import readline = require('readline');
import { COMPANY, SidBinaryOperation, SidConstant, SidField, SidVisitor } from '../src/tedi-sid/TediSid';

class SidSQLTestVisitor extends SidVisitor {
  public buffer: string;
  public like: boolean;

  constructor() {
    super();
    this.buffer = '';
    this.like = false;
  }

  public visitLogicalOperation(logicalOperation :SidBinaryOperation ): void {
    if (logicalOperation.op.mustGroup()) {
      this.buffer = this.buffer + '(';
    }
    super.visitLogicalOperation(logicalOperation);
    if (logicalOperation.op.mustGroup()) {
      this.buffer = this.buffer + ')';
    }
  }

  public visitAndOperator(): void {
    this.buffer = this.buffer + ' and ';
  }
  public visitOrOperator(): void {
    this.buffer = this.buffer + ' or ';
  }
  public visitEqualOperator(): void {
    this.buffer = this.buffer + ' = ';
  }
  public visitGreatherThanOperator(): void {
    this.buffer = this.buffer + ' > ';
  }
  public visitGreatherEqualOperator(): void {
    this.buffer = this.buffer + ' >= ';
  }
  public visitLessThanOperator(): void {
    this.buffer = this.buffer + ' < ';
  }
  public visitLessEqualOperator(): void {
    this.buffer = this.buffer + ' <= ';
  }
  public visitNotEqualOperator(): void {
    this.buffer = this.buffer + ' != ';
  }
  public visitContainsOperator(): void {
    this.buffer = this.buffer + ' contains ';
  }
  public visitLikeOperator(): void {
    this.buffer = this.buffer + ' like ';
  }
  public visitField(field :SidField): void {
    this.buffer = this.buffer + field.name;
  }
  public visitConstant(constant :SidConstant ): void {
    this.buffer = this.buffer + constant.value + (this.like?'%':'');
    this.like = false;
  }

}

describe('SID EXPRESSION TESTS', () => {
  before((done) => {
    
    const lineReader = readline.createInterface({
      input: fs.createReadStream('./test/resources/001.sid.txt')
    });
    
    lineReader.on('line', (line) => {
      // tslint:disable-next-line: no-console
      console.log(line);
    });
    lineReader.on('close', () => {
      done();
    });
  });

  it('EQUAL TEST', (done) => {
    const visitor = new SidSQLTestVisitor();
    const expression = COMPANY.DOCUMENT.eq('FELIPE');
    expression.visit(visitor);
    expect(visitor.buffer).to.be.equal('document = FELIPE');
    done();
  });

  it('NOT EQUAL TEST', (done) => {
    const visitor = new SidSQLTestVisitor();
    const expression = COMPANY.DOCUMENT.ne('FELIPE');
    expression.visit(visitor);
    expect(visitor.buffer).to.be.equal('document != FELIPE');
    done();
  });

  it('GREATHER THAN TEST', (done) => {
    const visitor = new SidSQLTestVisitor();
    const expression = COMPANY.DOCUMENT.gt('FELIPE');
    expression.visit(visitor);
    expect(visitor.buffer).to.be.equal('document > FELIPE');
    done();
  });

  it('GREATHER THAN OR EQUAL TEST', (done) => {
    const visitor = new SidSQLTestVisitor();
    const expression = COMPANY.DOCUMENT.ge('FELIPE');
    expression.visit(visitor);
    expect(visitor.buffer).to.be.equal('document >= FELIPE');
    done();
  });

  it('LESS THAN TEST', (done) => {
    const visitor = new SidSQLTestVisitor();
    const expression = COMPANY.DOCUMENT.lt('FELIPE');
    expression.visit(visitor);
    expect(visitor.buffer).to.be.equal('document < FELIPE');
    done();
  });

  it('LESS THAN OR EQUAL TEST', (done) => {
    const visitor = new SidSQLTestVisitor();
    const expression = COMPANY.DOCUMENT.le('FELIPE');
    expression.visit(visitor);
    expect(visitor.buffer).to.be.equal('document <= FELIPE');
    done();
  });

  it('CONTAINS TEST', (done) => {
    const visitor = new SidSQLTestVisitor();
    const expression = COMPANY.DOCUMENT.ct('FELIPE');
    expression.visit(visitor);
    expect(visitor.buffer).to.be.equal('document contains FELIPE');
    done();
  });

  it('AND TEST', (done) => {
    const visitor = new SidSQLTestVisitor();
    const expression = COMPANY.DOCUMENT.eq('FELIPE').and(COMPANY.NAME.eq('GONZALEZ'));
    expression.visit(visitor);
    expect(visitor.buffer).to.be.equal('document = FELIPE and name = GONZALEZ');
    done();
  });

  it('OR TEST', (done) => {
    const visitor = new SidSQLTestVisitor();
    const expression = COMPANY.NAME.eq('FELIPE').or(COMPANY.NAME.eq('ALVARO'));
    expression.visit(visitor);
    expect(visitor.buffer).to.be.equal('(name = FELIPE or name = ALVARO)');
    done();
  });

  it('FIELD TEST', (done) => {
    const visitor = new SidSQLTestVisitor();
    const expression = COMPANY.DOCUMENT.eq(COMPANY.NAME);
    expression.visit(visitor);
    expect(visitor.buffer).to.be.equal('document = name');
    done();
  });
});
