import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ContainerService {

  detailOpened : boolean;

  constructor() { 
    this.detailOpened = false;
  }
  
  showDetails(){
    this.detailOpened = true;
  }

  hideDetails(){
    this.detailOpened = false;
  }
}
