import { Component, Inject, OnInit } from '@angular/core';
import { CollectionFactory, IBank, ICollection } from 'libraries/AonSDK/aon';
import { BehaviorSubject } from 'rxjs';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { BankService } from 'src/app/core/services/bank.service';

@Component({
  selector: 'app-modal-payment',
  templateUrl: './modal-payment.component.html',
  styleUrls: ['./modal-payment.component.scss'],
})
export class ModalPaymentComponent implements OnInit {
  selectedOption: string = '1';
  showText: boolean = false;
  showCreateBank: boolean = false;
  showConfirmation: boolean = false;
  public showBanksList: boolean = false;

  public collectionFactory  = new CollectionFactory();
  banks: ICollection<IBank> = this.collectionFactory.createBankCollection();
  private banksSubject = new BehaviorSubject<ICollection<IBank>>(
    this.collectionFactory.createBankCollection()
  );


  constructor(
    private bankService: BankService,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ModalPaymentComponent>
  ) {}

  ngOnInit(): void {}

  toggleCreateBank() {
    this.showCreateBank = !this.showCreateBank;
  }

  showConfirmationContent() {
    this.showConfirmation = true;
  }

listBanks(){
  console.log("Se hizo clic en el ícono de la tarjeta de crédito");
  this.bankService.getBankList().then((response) => {
    console.log("Solicitando la lista de bancos...", response);
    this.banks = response;
    this.banksSubject.next(this.banks);


    this.showBanksList = true;

  });
}


}


