import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ContainerService {

  detailOpened : boolean;

  constructor() { 
    this.detailOpened = false;
  }

  showHideDetails(){
    this.detailOpened ? this.detailOpened = false : this.detailOpened = true;
  }
}
