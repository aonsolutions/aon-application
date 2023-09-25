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
  banksList: any[] = [];
  selectedBank: any = '';

  public collectionFactory = new CollectionFactory();
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

  listBanks() {
    this.bankService.getBankList().then((response) => {
      this.banks = response;
      this.banksSubject.next(this.banks);

      this.showBanksList = true;

      this.banksList = this.banks
        .toArray()
        .map((element) => ({ value: element.Iban, text: element.Iban }));
    });
  }

  selectBank(event: any) {}
}
