import { Observable, of } from 'rxjs';
import { Registry, TediError } from '../tedi-ewok/TediEwok';
import { TediOccam } from '../tedi-occam/TediOccam';
import { REGISTRY, SidAttribute } from '../tedi-sid/TediSid';

export class TediRegistry {
  public static getRegistry(document: string): Observable<Registry> {
    return TediOccam.getRegistry(document);
  }

  public static getRegistries(filter: SidAttribute): Observable<Registry[]> {
    return TediOccam.getRegistriesArray(filter);
  }

  public static updateRegistry(registry: Registry): Observable<Registry> {
    return TediOccam.updateRegistry(registry);
  }

  public static putRegistry(registry: Registry): Observable<Registry> {
    return TediOccam.putRegistry(registry);
  }

  public static deleteRegistry(document: string): Observable<string> {
    return TediOccam.deleteRegistry(document);
  }

  public static getFilter(data: Registry): Observable<SidAttribute> {
    if (data && data.name) {
      return of(REGISTRY.NAME.like(data.name));
    }
    if (data && data.document) {
      return of(REGISTRY.DOCUMENT.like(data.document));
    }
    return Observable.create(observer => observer.error(new TediError('Invalid registry filter')));
  }

  public static getRegistriesAsync(documents: string[]): Promise<Registry[]> {
    return TediOccam.getRegistriesAsync(documents);
  }

  public static getForeignRegistriesAsync(): Promise<Registry[]> {
    const foreigns: Promise<Registry[]> = Promise.all([
      TediOccam.getRegistriesArrayAsync(REGISTRY.DOCUMENT_COUNTRY.lt('ES')),
      TediOccam.getRegistriesArrayAsync(REGISTRY.DOCUMENT_COUNTRY.gt('ES')),
    ]).then((r: [Registry[], Registry[]]) => r[0].concat(r[1]));
    return foreigns;
  }
}
