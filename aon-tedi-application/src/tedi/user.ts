import { Observable, of } from 'rxjs';
import { User } from '../tedi-ewok/TediEwok';
import { TediGmail } from '../tedi-gmail/lib/helpers';
import { TediOccam } from '../tedi-occam/TediOccam';
import { SidAttribute, SidBinaryOperation, USER } from '../tedi-sid/TediSid';

export class TediUser {
  public static getUser(email: string): Observable<User> {
    return TediOccam.getUser(email);
  }
  public static getUsers(filter: SidAttribute | undefined): Observable<User[]> {
    return TediOccam.getUsersArray(filter);
  }
  public static updateUser(user: User): Observable<User> {
    return TediOccam.updateUser(user);
  }

  public static putUser(user: User): Observable<User> {
    return TediOccam.putUser(user);
  }

  public static deleteUser(email: string): Observable<string> {
    return TediOccam.deleteUser(email);
  }

  public static password(to: string, passw: string): Observable<boolean> {
    return Observable.create(observer => {
      TediGmail.buildPasswordMessage(to, passw).subscribe((msg: string) => {
        TediGmail.sendMail('invoice@translogia.net', msg).subscribe(
          r => {
            observer.next({ ok: 'ok' });
            observer.complete();
          },
          e => observer.error(e),
        );
      });
    });
  }

  public static getFilter(data, user: User): Observable<SidAttribute | undefined> {
    let f: SidBinaryOperation | undefined;

    if (data.user) {
      f = USER.USERS.ct(data.user);
    } else {
      if (!user.root) {
        if (user.admin && user.company) {
          f = USER.COMPANY.eq(user.company);
        } else {
          f = USER.USERS.ct(user.email);
        }
      }
      if (data.admin) {
        f = f ? f.and(USER.ADMIN.eq(data.admin === 'true')) : USER.ADMIN.eq(data.admin === 'true');
      }
      if (data.gestor) {
        f = f ? f.and(USER.GESTOR.eq(data.gestor === 'true')) : USER.GESTOR.eq(data.gestor === 'true');
      }
    }

    if (data.filter) {
      f = f ? f.and(USER.EMAIL.like(data.filter)) : USER.EMAIL.like(data.filter);
    }
    return of(f);
  }
}
