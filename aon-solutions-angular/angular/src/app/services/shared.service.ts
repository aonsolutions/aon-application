import { Injectable } from '@angular/core';
import { Subject, Observable, Observer } from 'rxjs';
import { Company, CompanyFilter, User, UserFilter} from '../models/models';
import { DeviceDetectorService } from 'ngx-device-detector';

import TEDI from '@translogia/tedi-sdk';
import { environment } from '../../environments/environment';

@Injectable()
export class SharedService {
    private readonly API_URL = environment.apiUrl;

    public showMenu: boolean = true;
    public isMobile: boolean;
    public isUserLoggedIn: boolean;
    public isRegister: Subject<boolean> = new Subject<boolean>();

    public company: Company;

    public selection: string;
    public isSheet: Subject<boolean> = new Subject<boolean>();
    public isMultipleSelection: Subject<boolean> = new Subject<boolean>();
    public filesToUpload: Subject<File[]> = new Subject<File[]>();

    public newUser: string;
    public newCompany: string;
    public user: User;

    public loading = false;

    constructor(private deviceService: DeviceDetectorService) {
      this.isMobile = this.deviceService.isMobile();
    }

    close() {
      this.company = undefined;
    }

    isSnapshot(): boolean {
      return !environment.production;
    }

    getToken(): string {
      return localStorage.getItem('aon_session_id');
    }

    setToken(token: string): void {
      localStorage.setItem('aon_session_id', token);
    }

    getActualCompany(): string {
      return this.user ? this.user.actual_company: localStorage.getItem('tedi_session_company');
    }

    // -------------------- USER

    getUserEmail(): string {
      return localStorage.getItem('tedi_session_user');
    }

    setUserEmail(email: string): void {
      localStorage.setItem('tedi_session_user', email);
    }

    getUsers(filter?:UserFilter): Observable<User[]> {
      return TEDI.user.getUsers(filter ? filter : {}, this.getToken(), this.API_URL);
    }

    getUser(): Observable<User> {
      if (this.user) {
        return Observable.create((observer: Observer<User>) => {
          observer.next(this.user);
          observer.complete();
        });
      } else {
        return Observable.create((observer: Observer<User>) => {
          TEDI.user.getUser({email: this.getUserEmail()}, this.getToken(), this.API_URL)
          .subscribe((user: User) => {
            this.setUser(user);
            localStorage.setItem('tedi_session_company', this.user.actual_company);
            observer.next(this.user);
            observer.complete();
          });
        });
      }
    }

    // TODO updateUser: User ** FIX USER MODEL!
    updateUser(updateUser: any): Observable<User> {
      localStorage.setItem('tedi_session_company', updateUser.actual_company);
      return TEDI.user.updateUser(updateUser, this.getToken(), this.API_URL);
    }

    setUser(user: User): void {
      this.user = user;
      localStorage.setItem('tedi_session_company', this.user.actual_company);
    }

    // ------------------ REGISTRY

    getRegistries(filter?:CompanyFilter, token?: string): Observable<Company[]> {
      return TEDI.registry.getRegistries(filter ? filter : {}, token ? token : this.getToken(), this.API_URL);
    }

    // -------------------- COMPANY

    getCompanies(filter?:CompanyFilter): Observable<Company[]> {
      return TEDI.company.getCompanies(filter ? filter : {}, this.getToken(), this.API_URL);
    }

    getCountInvoices(company: string, status: string) : Observable<number> {
      return TEDI.company.getCountInvoices(company, status, this.getToken(), this.API_URL);
    }

    getCompany(): Observable<Company> {
      if (this.company) {
        return Observable.create((observer: Observer<Company>) => {
          observer.next(this.company);
          observer.complete();
        });
      } else {
        return Observable.create((observer: Observer<Company>) => {
          TEDI.company.getCompany({document: this.getActualCompany()}, this.getToken(), this.API_URL)
          .subscribe((r: Company) => {
            this.company = r;
            localStorage.setItem('tedi_session_company_name', this.company.name);
            observer.next(r);
            observer.complete();
          });
        });
      }
    }

    getCompanyName():string {
      return this.company ? this.company.name : '';
    }
    // TODO updateCompany: Company ** FIX COMPANY MODEL!
    updateCompany(cp: any, token?: string) : Observable<Company> {
      if(this.company.document === cp.document) {
        this.company = cp;
      }
      return TEDI.company.updateCompany(cp, token ? token : this.getToken(), this.API_URL);
    }

    createCompany(cp: Company, token?: string) : Observable<Company> {
      return TEDI.company.createCompany(cp, token ? token : this.getToken(), this.API_URL);
    }
}
