import { Component, Inject, OnInit } from '@angular/core';
import {
  CollectionFactory,
  ErrorResponse,
  IBank,
  ICollection,
} from 'libraries/AonSDK/aon';
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
  showBanksList: boolean = false;
  banksList: any[] = [];
  selectedBank: string = '';
  isCreateBankOpen: boolean = false;
  newBankDescription: string = '';
  newBankIban: string = '';
  newBankBic: string = '';


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

  ngOnInit(): void {
    this.listBanks();
  }

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

      // Asigna el primer banco de la lista a selectedBank
      if (this.banksList.length > 0) {
        this.selectedBank = this.banksList[0].value;
      }
    });
  }

  selectBank(event: any) {
    this.selectedBank = event.target.value;
    console.log(this.selectedBank);

  }

  async createBank() {
    // Elimina espacios en blanco alrededor de los valores de entrada
    const trimmedDescription = this.newBankDescription.trim();
    const trimmedIban = this.newBankIban.trim();
    const trimmedBic = this.newBankBic.trim();

    // Verifica si alguno de los campos está vacío después de quitar los espacios en blanco
    if (!trimmedDescription || !trimmedIban || !trimmedBic) {
      // Si al menos uno de los campos está vací, que no se envíe el mensaje
      // muestra mensaje de error por consola

      return;
    }

    // Convierte el IBAN a mayúsculas antes de verificar duplicados
    const ibanToCheck = this.newBankIban.toUpperCase();

    // Verifica si el IBAN ya existe en la lista de bancos
    const isDuplicateIban = this.banks
      .toArray()
      .some((element) => element.Iban.toUpperCase() === ibanToCheck);

    if (isDuplicateIban) {
      // Muestra un mensaje de error o realiza la acción adecuada para manejar un IBAN duplicado
      console.log('El IBAN ya existe en la lista de bancos.');
      return;
    }

    //crear banco
    const newBank: IBank = this.bankService.objectFactory.createBank();
    newBank.Name = this.newBankDescription;
    newBank.Total = 0;
    newBank.Logo = '';
    newBank.SwiftBic = this.newBankBic;
    newBank.Iban = ibanToCheck;
    newBank.LastUpdate = new Date();
    newBank.SyncStatus = '';

    try {
      //  Crear el banco
      const createdBank = await this.bankService.createBank(newBank);

      // Después de enviar el mensaje, restablece los valores de los campos
      this.newBankDescription = '';
      this.newBankIban = '';
      this.newBankBic = '';
    } catch (error) {
      throw error instanceof ErrorResponse ? error : new ErrorResponse(error);
    }

    this.showCreateBank = false;

    this.listBanks();
  }
}
