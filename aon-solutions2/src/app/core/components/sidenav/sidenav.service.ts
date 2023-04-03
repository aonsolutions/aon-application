import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SidenavService {

  opened : boolean;
  resize : number;

  constructor() {
    this.opened = true;
    this.resize = 1;
  }

  setOpened(state:boolean){
    this.opened = state;
  }
  
  setResize(state:number){
    this.resize = state
  }
}
