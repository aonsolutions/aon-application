import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, Observer } from 'rxjs';

import { User, UserFilter, AonMaker } from '../models/models';
import { SharedService } from './shared.service';
import { environment } from '../../environments/environment';

@Injectable()
export class UserService {

  private readonly API_URL = environment.apiUrl;

  user: User = AonMaker.createUser();
  userList: User[];
  filter: UserFilter = { gestor: true };
  filterObservable: Observable<UserFilter>;
  filterObserver: Observer<UserFilter>;

  constructor (private httpClient: HttpClient, private service: SharedService) {
    this.filterObservable = Observable.create((observer: Observer<UserFilter>) => this.filterObserver = observer);
  }

  setUser(data: User): void {
    this.user = AonMaker.createUser(data);
  }

  setFilter(filter: UserFilter) {
    this.filter = filter;
    this.filterObserver.next(this.filter);
  }

  rememberPassword(user: any, token: string): Observable<any> {
    let headers = new HttpHeaders();
    headers = headers.append('session_id', token);
    headers = headers.append('Content-Type', 'application/json');
    return this.httpClient.post(this.API_URL + '/user/password', user, {headers: headers});
  }

  public updateUser(user?: User, password?: boolean) {
    if(!password && user){
      delete user.password;
    } else if(!password){
      delete this.user.password;
    }
    this.service.updateUser(user ? user : this.user).subscribe();
  }
}
