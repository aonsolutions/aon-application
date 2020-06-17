// tslint:disable: no-unused-expression
import { expect } from 'chai';
import fs = require('fs');
import readline = require('readline');
import { of, from } from 'rxjs';
import { map, mergeMap, tap, filter } from 'rxjs/operators';
import { Company, Registry, User, Invoice, InvoiceStatus, TediError } from '../src/tedi-ewok/TediEwok';
import { TediOccam } from '../src/tedi-occam/TediOccam';
import { COMPANY, REGISTRY, USER, INVOICE, SidAttribute } from '../src/tedi-sid/TediSid';
import { TediValidator } from '../src/tedi-validator/TediValidator';
import { TediFaker } from './TediFaker';

function log(what: any) {
  // tslint:disable-next-line: no-console
  console.log(what);
}

function logError(what: string) {
  // tslint:disable-next-line: no-console
  console.error('[ERROR]' + what);
}

function habemusError(error: TediError) {
  logError(error.message + ' ' + error.details);

  // fail('Habemus error. tedi-baloo error! ' + '[' + error.message + ']');
}
describe('TEDI OCCAM TESTS', () => {
  before(done => {
    const lineReader = readline.createInterface({
      input: fs.createReadStream('./test/resources/003.baloo.txt'),
    });

    lineReader.on('line', line => {
      // tslint:disable-next-line: no-console
      console.log(line);
    });
    lineReader.on('close', () => {
      done();
    });
  });

  describe('TEDI COMPANY TESTS', () => {
    let companyId = '';
    let activeCompanies = 0;
    let startWithACompanies = 0;
    const companies: Company[] = [];
    for (let index = 0; index < 2; index++) {
      const comp = TediFaker.randomCompany();
      activeCompanies = activeCompanies + (comp.active ? 1 : 0);
      startWithACompanies = startWithACompanies + (comp.name.startsWith('A') ? 1 : 0);
      companies.push(comp);
    }

    it('VALIDATE company', done => {
      of(TediFaker.randomCompany())
        .pipe(tap((company: Company) => TediValidator.validateCompany(company)))
        .subscribe(
          company => {
            expect(company).not.to.be.null;
          },
          (error: TediError) => {
            habemusError(error);
            done(error);
          },
          () => done(),
        );
    });

    it('PUT COMPANY', done => {
      const company = TediFaker.randomCompany();
      TediOccam.putCompany(company).subscribe(
        (comp: Company) => {
          expect(comp).not.to.be.null;
          log('\tCompany PUT: ' + comp.document);
          companyId = comp.document;
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('GET COMPANY', done => {
      log('\tGet company ---> ' + companyId);
      TediOccam.getCompany(companyId).subscribe(
        (comp: Company) => {
          expect(comp).not.to.be.null;
          log('\tFound!');
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('DELETE COMPANY', done => {
      log('\tDelete company ---> ' + companyId);
      TediOccam.deleteCompany(companyId).subscribe(
        (comp: string) => {
          expect(comp).not.to.be.null;
          log('\tDeleted!');
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('GET BEFORE PUT ACTIVE COMPANIES', done => {
      const filter: SidAttribute = COMPANY.ACTIVE.eq(true);
      TediOccam.getCompaniesArray(filter).subscribe(
        (comps: Company[]) => {
          activeCompanies = activeCompanies + comps.length;
          log('\tCurrent active companies ..: ' + activeCompanies);
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('GET CURRENT COMPANIES WHICH NAME STARTS WITH "A"', done => {
      const filter: SidAttribute = COMPANY.NAME.like('A');
      TediOccam.getCompaniesArray(filter).subscribe(
        (comps: Company[]) => {
          expect(comps).not.to.be.null;
          startWithACompanies = startWithACompanies + comps.length;
          log('\tCurrent Companies which name starts with "A" ..: ' + startWithACompanies);
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('PUT COMPANIES', done => {
      TediOccam.putCompanies(companies).subscribe(
        (comps: Company[]) => {
          expect(comps).not.to.be.null;
          log('\tPUT: ' + comps.length + ' companies (' + activeCompanies + ' actives, ' + startWithACompanies + ' starts with "A")');
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('GET ALL ACTIVE COMPANIES', done => {
      const filter: SidAttribute = COMPANY.ACTIVE.eq(true);
      log('\tActive companies ..: ' + activeCompanies);
      TediOccam.getCompaniesArray(filter).subscribe(
        (comps: Company[]) => {
          expect(comps).not.to.be.null;
          log('\tSearched array size ..: ' + comps.length);
          expect(activeCompanies).eq(comps.length);
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('GET ALL COMPANIES WHICH NAME STARTS WITH "A"', done => {
      const filter: SidAttribute = COMPANY.NAME.like('A');
      log('\tCompanies which name starts with "A" ..: ' + startWithACompanies);
      TediOccam.getCompaniesArray(filter).subscribe(
        (comps: Company[]) => {
          expect(comps).not.to.be.null;
          log('\tSearched array size ..: ' + comps.length);
          expect(startWithACompanies).eq(comps.length);
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('DELETE INSERTED COMPANIES', done => {
      TediOccam.deleteCompanies(companies).subscribe(
        (comps: Company[]) => {
          expect(comps).not.to.be.null;
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('CRUD company', done => {
      const company = TediFaker.randomCompany();
      of(company)
        .pipe(mergeMap(TediOccam.putCompany))
        .pipe(tap((comp: Company) => log('\t (1) PUT ' + comp.document + ' - ' + comp.name)))
        .pipe(map((comp: Company) => comp.document))
        .pipe(mergeMap(TediOccam.getCompany))
        .pipe(tap((comp: Company) => log('\t (2) GET ' + comp.document + ' - ' + comp.name)))
        .pipe(
          map((comp: Company) => {
            comp.name = 'Updated Name';
            return comp;
          }),
        )
        .pipe(mergeMap(TediOccam.putCompany))
        .pipe(tap((comp: Company) => log('\t (3) UPDATE ' + comp.document + ' - ' + comp.name)))
        .pipe(map((comp: Company) => COMPANY.DOCUMENT.eq(comp.document)))
        .pipe(mergeMap(TediOccam.getCompanies))
        .pipe(tap((comp: Company) => log('\t (4) GET COMPANIES ' + comp.document + ' - ' + comp.name)))
        .pipe(map((comp: Company) => comp.document))
        .pipe(mergeMap(TediOccam.deleteCompany))
        .pipe(tap((doc: string) => log('\t (5) DELETE ' + doc)))
        .subscribe(
          (document: string) => {
            log('\t (6) CRUD PASSED: ' + document);
            expect(document).not.to.be.null;
            done();
          },
          (error: TediError) => {
            habemusError(error);
            done(error);
          },
        );
    });
  });

  describe('TEDI REGISTRY TESTS', () => {
    let registryId = '';
    let startWithARegistries = 0;
    const registries: Registry[] = [];
    for (let index = 0; index < 2; index++) {
      const reg = TediFaker.randomRegistry();
      startWithARegistries = startWithARegistries + (reg.name!.startsWith('A') ? 1 : 0);
      registries.push(reg);
    }

    it('VALIDATE REGISTRY', done => {
      of(TediFaker.randomRegistry())
        .pipe(tap(registry => TediValidator.validateRegistry(registry)))
        .subscribe(
          registry => expect(registry).not.to.be.null,
          error => {
            habemusError(error);
            done(error);
          },
          () => done(),
        );
    });

    it('VALIDATE REGISTRY ERROR', done => {
      let reg = {
        document_country:'AAAA'
      };
      of(reg)
        .pipe(tap(registry => TediValidator.validateRegistry(registry)))
        .subscribe(
          registry => expect(registry).not.to.be.null,
          () => done(),
          () => done(),
        );
    });

    it('PUT REGISTRY', done => {
      const registry = TediFaker.randomRegistry();
      TediOccam.putRegistry(registry).subscribe(
        (reg: Registry) => {
          expect(reg).not.to.be.null;
          expect(reg.document).not.to.be.null;
          log('\tRegistry PUT: ' + reg.document);
          registryId = reg.document!;
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('GET REGISTRY', done => {
      log('\tGet Registry ---> ' + registryId);
      TediOccam.getRegistry(registryId).subscribe(
        (reg: Registry) => {
          expect(reg).not.to.be.null;
          log('\tFound!');
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('DELETE REGISTRY', done => {
      log('\tDelete Registry ---> ' + registryId);
      TediOccam.deleteRegistry(registryId).subscribe(
        (reg: string) => {
          expect(reg).not.to.be.null;
          log('\tDeleted!');
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('GET CURRENT REGISTRIES WHICH NAME STARTS WITH "A"', done => {
      const fltr: SidAttribute = REGISTRY.NAME.like('A');
      TediOccam.getRegistriesArray(fltr).subscribe(
        (regs: Registry[]) => {
          expect(regs).not.to.be.null;
          startWithARegistries = startWithARegistries + regs.length;
          log('\tCurrent Registries which name starts with "A" ..: ' + startWithARegistries);
          for (const reg of regs) {
            log('\t\t ' + reg.name);
          }
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('PUT REGISTRY', done => {
      TediOccam.putRegistries(registries).subscribe(
        (regs: Registry[]) => {
          expect(regs).not.to.be.null;
          log('\tPUT: ' + regs.length + ' registries (' + startWithARegistries + ' starts with "A")');
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('GET ALL REGISTRIES WHICH NAME STARTS WITH "A"', done => {
      const filter: SidAttribute = REGISTRY.NAME.like('A');
      log('Registries which name starts with "A" ..: ' + startWithARegistries);
      TediOccam.getRegistriesArray(filter).subscribe(
        (regs: Registry[]) => {
          expect(regs).not.to.be.null;
          log('\tSearched array size ..: ' + regs.length);
          expect(startWithARegistries).eq(regs.length);
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('DELETE INSERTED REGISTRIES', done => {
      TediOccam.deleteRegistries(registries).subscribe(
        regs => expect(regs).not.to.be.null,
        error => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('CRUD registry', done => {
      const registry = TediFaker.randomRegistry();
      of(registry)
        .pipe(mergeMap(TediOccam.putRegistry))
        .pipe(filter(reg => !!reg.document))
        .pipe(tap(reg => log('\t (1) PUT ' + reg.document + ' - ' + reg.name)))
        .pipe(map(reg => reg.document!))
        .pipe(mergeMap(TediOccam.getRegistry))
        .pipe(tap(reg => log('\t (2) GET ' + reg.document + ' - ' + reg.name)))
        .pipe(map(reg => {reg.name = 'Updated Name'; return reg;}))
        .pipe(mergeMap(TediOccam.updateRegistry))
        .pipe(tap(reg => log('\t (3) UPDATE ' + reg.document + ' - ' + reg.name)))
        .pipe(filter(reg => !!reg.document))
        .pipe(map(reg => REGISTRY.DOCUMENT.eq(reg.document!)))
        .pipe(mergeMap(TediOccam.getRegistries))
        .pipe(tap(reg => log('\t (4) GET REGISTRIES ' + reg.document + ' - ' + reg.name)))
        .pipe(filter(reg => !!reg.document))
        .pipe(map(reg => reg.document!))
        .pipe(mergeMap(TediOccam.deleteRegistry))
        .pipe(tap((doc: string) => log('\t (5) DELETE ' + doc)))
        .subscribe(
          (document: string) => {
            log('\t (6) CRUD PASSED: ' + document);
            expect(document).not.to.be.null;
            done();
          },
          (error: TediError) => {
            habemusError(error);
            done(error);
          },
        );
    });
  });

  describe('TEDI USER TESTS', () => {
    let userId = '';
    let startWithAUsers = 0;
    const users: User[] = [];
    for (let index = 0; index < 2; index++) {
      const user = TediFaker.randomUser();
      startWithAUsers = startWithAUsers + (user.name!.startsWith('A') ? 1 : 0);
      users.push(user);
    }

    it('VALIDATE USER', done => {
      of(TediFaker.randomUser())
        .pipe(tap(user => TediValidator.validateUser(user)))
        .subscribe(
          user => expect(user).not.to.be.null,
          error => {
            habemusError(error);
            done(error);
          },
          () => done(),
        );
    });

    it('PUT USER', done => {
      const user = TediFaker.randomUser();
      TediOccam.putUser(user).subscribe(
        (usr: User) => {
          expect(usr).not.to.be.null;
          expect(usr.email).not.to.be.null;
          log('\tUser PUT: ' + usr.email);
          userId = usr.email!;
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('GET USER', done => {
      log('\tGet User ---> ' + userId);
      TediOccam.getUser(userId).subscribe(
        (usr: User) => {
          expect(usr).not.to.be.null;
          log('\tFound!');
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('DELETE USER', done => {
      log('\tDelete User ---> ' + userId);
      TediOccam.deleteUser(userId).subscribe(
        (usr: string) => {
          expect(usr).not.to.be.null;
          log('\tDeleted!');
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('GET CURRENT USERS WHICH NAME STARTS WITH "A"', done => {
      const fltr: SidAttribute = USER.NAME.like('A');
      TediOccam.getUsersArray(fltr).subscribe(
        (usrs: User[]) => {
          expect(usrs).not.to.be.null;
          startWithAUsers = startWithAUsers + usrs.length;
          log('\tCurrent Users which name starts with "A" ..: ' + startWithAUsers);
          for (const usr of usrs) {
            log('\t\t ' + usr.name);
          }
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('PUT USERS', done => {
      TediOccam.putUsers(users).subscribe(
        (usrs: User[]) => {
          expect(usrs).not.to.be.null;
          log('\tPUT: ' + usrs.length + ' users (' + startWithAUsers + ' starts with "A")');
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('GET ALL USERS WHICH NAME STARTS WITH "A"', done => {
      const filter: SidAttribute = USER.NAME.like('A');
      log('Users which name starts with "A" ..: ' + startWithAUsers);
      TediOccam.getUsersArray(filter).subscribe(
        (usrs: User[]) => {
          expect(usrs).not.to.be.null;
          log('\tSearched array size ..: ' + usrs.length);
          expect(startWithAUsers).eq(usrs.length);
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('DELETE USER', done => {
      log('\tDelete Users ---> ');
      from(users)
      .pipe(mergeMap(user =>TediOccam.deleteUser(user.email)))
      .subscribe(
        (usr: string) => {
          expect(usr).not.to.be.null;
          log('\tDeleted!');
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('CRUD user', done => {
      const user = TediFaker.randomUser();
      of(user)
        .pipe(mergeMap(TediOccam.putUser))
        .pipe(filter(usr => !!usr.email))
        .pipe(tap(usr => log('\t (1) PUT ' + usr.email + ' - ' + usr.name + ' ' + usr.surname)))
        .pipe(map(usr => usr.email!))
        .pipe(mergeMap(TediOccam.getUser))
        .pipe(tap(usr => log('\t (2) GET ' + usr.email + ' - ' + usr.name + ' ' + usr.surname)))
        .pipe(map(usr => {usr.name = 'Updated Name'; return usr;}))
        .pipe(mergeMap(TediOccam.updateUser))
        .pipe(tap(usr => log('\t (3) UPDATE ' + usr.email + ' - ' + usr.name + ' ' + usr.surname)))
        .pipe(filter(usr => !!usr.email))
        .pipe(map(usr => USER.EMAIL.eq(usr.email!)))
        .pipe(mergeMap(TediOccam.getUsers))
        .pipe(tap(usr => log('\t (4) GET USERS ' + usr.document + ' - ' + usr.name + ' ' + usr.surname)))
        .pipe(filter(usr => !!usr.email))
        .pipe(map(usr => usr.email!))
        .pipe(mergeMap(TediOccam.deleteUser))
        .pipe(tap((email: string) => log('\t (5) DELETE ' + email)))
        .subscribe(
          (email: string) => {
            log('\t (6) CRUD PASSED: ' + email);
            expect(email).not.to.be.null;
            done();
          },
          (error: TediError) => {
            habemusError(error);
            done(error);
          },
        );
    });
  });

  describe('TEDI INVOICE TESTS', () => {
    let invoiceId = '';
    let company = 'B00000000';
    let startWith2Invoices = 0;
    let invoices: Invoice[] = [];
    for (let index = 0; index < 2; index++) {
      const invoice = TediFaker.randomInvoice();
      invoice.company = company;
      startWith2Invoices = startWith2Invoices + (invoice.reference && invoice.reference.includes('2') ? 1 : 0);
      invoices.push(invoice);
    }

    it('GET COUNT INVOICES', done => {
      log('\tGet Invoice ---> ' + company + ' - ' + invoiceId);
      TediOccam.getCountInvoices(company, InvoiceStatus.inbox).subscribe(
        (count: number) => {
          log('\tFound! ' + count);
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });


    it('VALIDATE INVOICE', done => {
      of(TediFaker.randomInvoice())
        .pipe(tap((inv: Invoice) => TediValidator.validateInvoice(inv)))
        .subscribe(
          (inv: Invoice) => expect(inv).not.to.be.null,
          (error: TediError) => {
            habemusError(error);
            done(error);
          },
          () => done(),
        );
    });

    it('PUT INVOICE', done => {
      const invoice = TediFaker.randomInvoice();
      invoice.company = company;
      TediOccam.putInvoice(company, invoice).subscribe(
        (inv: Invoice) => {
          expect(inv).not.to.be.null;
          expect(inv.uuid).not.to.be.null;
          log('\tInvoice PUT: ' + inv.uuid);
          invoiceId = inv.uuid!;
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('GET INVOICE', done => {
      log('\tGet Invoice ---> ' + company + ' - ' + invoiceId);
      TediOccam.getInvoice(company, invoiceId).subscribe(
        (inv: Invoice) => {
          expect(inv).not.to.be.null;
          log('\tFound!');
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('DELETE INVOICE', done => {
      log('\tDelete Invoice ---> ' + company + ' - ' + invoiceId);
      TediOccam.deleteInvoice(company, invoiceId).subscribe(
        (inv: string) => {
          expect(inv).not.to.be.null;
          log('\tDeleted!');
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('GET CURRENT INVOICES WHICH NAME CONTAINS WITH "2"', done => {
      //const fltr: SidAttribute = INVOICE.REFERENCE.like('2');
      TediOccam.getInvoicesArray(company, undefined, {reference: '2'}).subscribe(
        (invs: Invoice[]) => {
          expect(invs).not.to.be.null;
          startWith2Invoices = startWith2Invoices + invs.length;
          log('\tCurrent Users which name starts with "2" ..: ' + startWith2Invoices);
          for (const inv of invs) {
            log('\t\t ' + inv.reference);
          }
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('PUT INVOICES', done => {
      TediOccam.putInvoices(company, invoices, InvoiceStatus.inbox).subscribe(
        (invs: Invoice[]) => {
          expect(invs).not.to.be.null;
          log('\tPUT: ' + invs.length + ' invoices (' + startWith2Invoices + ' starts with "2")');
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('GET ALL INVOICES WHICH REFERENCE CONTAINS WITH "2"', done => {
      log('Invoice which reference contains with "2" ..: ' + startWith2Invoices);
      TediOccam.getInvoicesArray(company, undefined, {reference: '2'}, InvoiceStatus.inbox).subscribe(
        (invs: Invoice[]) => {

          expect(invs).not.to.be.null;
          log('\tSearched array size ..: ' + invs.length);
          expect(startWith2Invoices).eq(invs.length);
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('DELETE INVOICES', done => {
      TediOccam.getInvoicesArray(company, undefined, {}, InvoiceStatus.inbox)
      .pipe(mergeMap((invs: Invoice[]) =>
      {
        return TediOccam.deleteInvoices(company, invs.map((i:Invoice) => i.uuid ? i.uuid : ""), InvoiceStatus.inbox)
      }
      )).subscribe(
        (uuids: String[]) => {
          expect(uuids).not.be.null;
          expect(invoices.length).eq(uuids.length);
        },
        (error: TediError) => {
          habemusError(error);
          done(error);
        },
        () => done(),
      );
    });

    it('CRUD invoice', done => {
      const invoice = TediFaker.randomInvoice();
      invoice.company = company;
      of(invoice)
        .pipe(mergeMap((inv: Invoice) => TediOccam.putInvoice(company, inv)))
        .pipe(filter((inv: Invoice) => !!inv.uuid))
        .pipe(tap((inv: Invoice) => log('\t (1) PUT ' + inv.uuid + ' - ' + inv.reference)))
        .pipe(map((inv: Invoice) => inv.uuid!))
        .pipe(mergeMap((uuid: string) => TediOccam.getInvoice(company, uuid)))
        .pipe(tap((inv: Invoice) => log('\t (2) GET ' + inv.uuid + ' - ' + inv.reference)))
        .pipe(map((inv: Invoice) => {inv.reference = 'Updated Reference'; return inv;}))
        .pipe(mergeMap((inv:Invoice) => TediOccam.updateInvoice(company, inv)))
        .pipe(tap((inv: Invoice) => log('\t (3) UPDATE ' + inv.uuid + ' - ' + inv.reference)))
        .pipe(filter((inv: Invoice) => !!inv.uuid))
        .pipe(map((inv: Invoice) => INVOICE.UUID.eq(inv.uuid!)))
        .pipe(mergeMap(filter => TediOccam.getInvoices(company, filter)))
        .pipe(tap((inv: Invoice) => log('\t (4) GET INVOICES ' + inv.uuid + ' - ' + inv.reference)))
        .pipe(filter(inv => !!inv.uuid))
        .pipe(map(inv => inv.uuid!))
        .pipe(mergeMap((uuid: string)=> TediOccam.deleteInvoice(company, uuid)))
        .pipe(tap((uuid: string) => log('\t (5) DELETE ' + uuid)))
        .subscribe(
          (uuid: string) => {
            log('\t (6) CRUD PASSED: ' + uuid);
            expect(uuid).not.to.be.null;
            done();
          },
          (error: TediError) => {
            habemusError(error);
            done(error);
          },
        );
    });
  });
});
