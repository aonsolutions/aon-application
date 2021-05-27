import { Observable, Observer, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';
import { Company, OperationType, PrinterConfiguration, TediError, User } from '../tedi-ewok/TediEwok';
import { TediOccam } from '../tedi-occam/TediOccam';
import { COMPANY, SidAttribute, SidBinaryOperation } from '../tedi-sid/TediSid';

export class TediCompany {
  public static getCompany(document: string): Observable<Company> {
    return TediOccam.getCompany(document);
  }

  public static getCompaniesArray(filter?: SidAttribute): Observable<Company[]> {
    return TediOccam.getCompaniesArray(filter);
  }

  public static updateCompany(company: Company): Observable<Company> {
    return TediOccam.updateCompany(company);
  }

  public static putCompany(company: Company): Observable<Company> {
    return TediOccam.putCompany(company);
  }

  public static deleteCompany(document: string): Observable<string> {
    return TediOccam.deleteCompany(document);
  }

  public static getFilter(data, user: User): Observable<SidAttribute | undefined> {
    let f: SidBinaryOperation | undefined;
    if (data.user) {
      f = COMPANY.USERS.ct(data.user);
    } else {
      if (!user.root) {
        if (user.admin && user.company) {
          f = COMPANY.COMPANY.eq(user.company);
        } else {
          f = COMPANY.USERS.ct(user.email);
        }
      }

      if (data.name) {
        f = f ? f.and(COMPANY.NAME.like(data.name)) : COMPANY.NAME.like(data.name);
      } else if (data.document) {
        f = f ? f.and(COMPANY.DOCUMENT.like(data.document)) : COMPANY.DOCUMENT.like(data.document);
      } else if (user.root && data.company) {
        f = f ? f.and(COMPANY.COMPANY.eq(data.company)) : COMPANY.COMPANY.eq(data.company);
      }

      if (data.active !== undefined) {
        f = f ? f.and(COMPANY.ACTIVE.eq(data.active)) : COMPANY.ACTIVE.eq(data.active);
      }
    }
    return of(f);
  }

  public static checkPermission = (user: User, company: Company, operation: OperationType) => {
    if (operation === OperationType.CREATE) {
      if (!user || user.root || user.admin || user.gestor) {
        return TediOccam.getCompany(company.document).pipe(
          mergeMap(cp => {
            if (!cp) {
              return Observable.create((observer: Observer<Company>) => {
                observer.next(cp);
                observer.complete();
              });
            } else {
              return Observable.create(observer => observer.error(new TediError('La empresa que intenta crear ya existe.', 403)));
            }
          }),
        );
      } else {
        return Observable.create(observer => observer.error(new TediError('No tiene permisos para realizar la operación', 403)));
      }
    } else if (operation === OperationType.DELETE) {
      return Observable.create(observer => {
        if (user.root || user.admin || user.gestor) {
          observer.next({});
          observer.complete();
        } else {
          observer.error(new TediError('No tiene permisos para realizar la operación', 403));
        }
      });
    } else {
      return Observable.create(observer => {
        observer.next({});
        observer.complete();
      });
    }
  };

  public static getPrinterConfiguration = (cp: Company): Observable<PrinterConfiguration> => {
    return of(TediCompany.ensurePrinterConfiguration(cp.printer_configuration));
  };

  public static ensurePrinterConfiguration(pc?: PrinterConfiguration): PrinterConfiguration {
    return pc
      ? {
          adjustment: pc.adjustment || false,
          detailed: pc.detailed || false,
          header: pc.header || 100,
          footer: pc.footer || 100,
        }
      : {
          adjustment: false,
          detailed: false,
          header: 100,
          footer: 100,
        };
  }
}
