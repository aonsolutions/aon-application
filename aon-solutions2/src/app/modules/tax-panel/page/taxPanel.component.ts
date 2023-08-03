import { Component, OnInit, ViewChild } from '@angular/core';
import { ModalEditTaxModelComponent } from '../components/modal-edit-tax-model/modal-edit-tax-model.component';
import { ModalPaymentComponent } from '../components/modal-payment/modal-payment.component';
import { ModalTaxesDetailsComponent } from '../components/modal-taxes-details/modal-taxes-details.component';


export interface Tabs {
  name: string; // Nombre de la tab
  icon?: string; // Icono opcional de la tab
  color?: string; // Color del texto y del icono de la tab
}


@Component({
  selector: 'app-taxPanel',
  templateUrl: './taxPanel.component.html',
  styleUrls: ['./taxPanel.component.scss']
})


export class TaxPanelComponent implements OnInit {

  tabIndex:number = 0

  tabs: Tabs[] = [
    { name: 'tab1', icon: 'create', color: 'black' },
    { name: 'tab2', icon: 'delete', color: 'black' },
    { name: 'tab3', color: 'black' },
    { name: 'todos', color: 'black' },
  ];



  constructor() { }

  @ViewChild('modalEdit') modalComponentEdit: any = '';

  @ViewChild('modalPayment') modalComponentPayment: any = '';

  @ViewChild('modalTaxesDetails') modalComponentTaxesDetails : any = '';

  functionHome: any = (result:any) => this.afterModalClosed(result);

  afterModalClosed(result?: any){
      console.log(result);
  }

  showModalEdit(){
      this.modalComponentEdit.openDialog(ModalEditTaxModelComponent,this.functionHome, 'Data from home');
  }

  showModalPayment(){
    this.modalComponentPayment.openDialog(ModalPaymentComponent,this.functionHome, 'Data from home');
}

showModalTaxesDetails(){
  this.modalComponentTaxesDetails.openDialog(ModalTaxesDetailsComponent,this.functionHome, 'Data from home');
}


  ngOnInit() {
  }

}
