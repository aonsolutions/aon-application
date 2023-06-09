import { HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { map } from 'rxjs/operators';
import { Enterprise } from '../models/class/enterprise';

@Injectable({
  providedIn: 'root'
})
export class ExampleServiceService {

  constructor(private apiService: ApiService, private router: Router) { }

}
