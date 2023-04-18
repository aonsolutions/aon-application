import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Usuario } from '../../core/models/usuario';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  urlBase:string = environment.urlApiAon;
  headers: any = {
    domain_name: "a54212356-cau.aonsolutions.org",
    domain_id: 545,
    domain_login: 87811999,
  };

  constructor(private http: HttpClient) { }

  login(user: any) {
    this.http.post<any>(this.urlBase+'login', user).subscribe(data => { // Subscribe actua como una 'promesa' de JS
      this.headers['session_id'] = data.session_id;
      console.log('Login exitoso. Veamos los detalles de este user');
      // this.setToken(data.session_id);
      this.getUser().subscribe(usuario => {
        console.log(usuario);
      }
      )
      // console.log('Se ha establecido el siguiente token: '+this.getToken());
    });
  }

  getUser(): Observable<Usuario> {
    let cabeceras: HttpHeaders = new HttpHeaders(this.headers);

    return this.http.get<Usuario>(this.urlBase+'auth', {headers: cabeceras});
  }
}
