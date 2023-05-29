import { Component, OnInit, ViewChild } from '@angular/core';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  accordionState: boolean = false;

  functionHome: any = (result:any) => this.afterModalClosed(result);
  
  @ViewChild('modal') modalComponent: any = '';

  afterModalClosed(result?: any){
    console.log(result);
  }

  showModal(){
    // this.modalComponent.openDialog(ModalComponent,this.functionHome, 'Data from home');
  }

  constructor() {
  }

  ngOnInit(): void {
  }

  
}
