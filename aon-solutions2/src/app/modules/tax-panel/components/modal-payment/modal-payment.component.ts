import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import {
  CollectionFactory,
  IBank,
  ICollection,
  statusTaxModel,
} from 'libraries/AonSDK/src/aon';
import { BehaviorSubject } from 'rxjs';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { BankService } from 'src/app/core/services/bank.service';
import { TaxModelService } from 'src/app/core/services/tax-model.service';

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
  codeNrc: any;
  isCreateBankOpen: boolean = false;
  newBankDescription: string = '';
  newBankIban: string = '';
  newBankBic: string = '';
  model: any;
  amountResult: number = 0;
  newStatus: string = '';
  spinner: boolean = true;

  @Output() statusChanged: EventEmitter<string> = new EventEmitter<string>();

  public collectionFactory = new CollectionFactory();
  banks: ICollection<IBank> = this.collectionFactory.createBankCollection();
  private banksSubject = new BehaviorSubject<ICollection<IBank>>(
    this.collectionFactory.createBankCollection()
  );

  constructor(
    private taxModelService: TaxModelService,
    private bankService: BankService,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ModalPaymentComponent>
  ) {
    this.listBanks();

    this.taxModelService.getTax(data.key).then((response) => {
      this.model = response;
      this.amountResult = this.model.Result;
      this.newStatus = this.model.Status;
    });
  }

  ngOnInit(): void {

  }

  toggleCreateBank() {
    this.showCreateBank = !this.showCreateBank;
  }

  showConfirmationContent() {
    this.showConfirmation = true;
  }

  listBanks() {
    this.bankService.getBankList().then((response) => {
      this.spinner = false;
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
    this.selectedBank = event;
  }

  async createBank() {
    // Elimina espacios en blanco alrededor de los valores de entrada
    const trimmedDescription = this.newBankDescription.trim();
    const trimmedIban = this.newBankIban.trim();
    const trimmedBic = this.newBankBic.trim();

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

    //  Crear el banco
    const createdBank = await this.bankService.createBank(newBank);

    // Después de enviar el mensaje, restablece los valores de los campos
    this.newBankDescription = '';
    this.newBankIban = '';
    this.newBankBic = '';
    this.showCreateBank = false;
    this.listBanks();
  }
  
  // sendPayment() {
  //   if (this.newStatus === statusTaxModel.PENDIENTE) {
  //     this.newStatus = statusTaxModel.PRESENTADO;
  //   }
  //   // Oculta el spinner que se muestra cuando se abre el modal
  //   this.spinner = false;

  //   // Muestra el spinner mientras se envía el mensaje
  //   this.spinner = true;

  //   // Emite el nuevo valor de status
  //   this.statusChanged.emit(this.newStatus);
  //   console.log('Nuevo status emitido:', this.newStatus);

  //   this.spinner = false;
  //   this.dialogRef.close();
  // }

  getNrc(event: any) {
    this.codeNrc = event;
  }

  async sendPaymentNRC() {
    if (this.codeNrc) {
      // Oculta el spinner que se muestra cuando se abre el modal
      this.spinner = false;

      // Muestra el spinner mientras se envía el mensaje
      this.spinner = true;
      const nrcPay = await this.taxModelService.payTaxModel(
        this.model,
        this.codeNrc
      );

      if (nrcPay) {
        // Pago exitoso, realiza las acciones necesarias
        console.log('Pago exitoso.');
        // Limpia el campo NRC
        this.codeNrc = ''; // Establece el campo en una cadena vacía para borrar su contenido
      } else {
        // Pago fallido, realiza las acciones necesarias
        console.log('El pago falló.');
      }
      this.spinner = false;
      this.dialogRef.close();
      window.location.reload();
    } else {
      // Campo NRC vacío o no válido
      console.log('El campo NRC está vacío o no tiene un valor válido.');
    }
  }
  async sendPaymentIban() {
    if (this.selectedBank) {
      // Oculta el spinner que se muestra cuando se abre el modal
      this.spinner = false;
      // Muestra el spinner mientras se envía el mensaje
      this.spinner = true;
      const ibanPay = await this.taxModelService.payTaxModel(
        this.model,
        this.selectedBank
      );
      if (ibanPay) {
        // Pago exitoso, realiza las acciones necesarias
        console.log('Pago exitoso.');

      } else {
        // Pago fallido, realiza las acciones necesarias
        console.log('El pago falló.');
      }
      this.spinner = false;
      this.dialogRef.close();
      window.location.reload();
    } else {
      // Campo NRC vacío o no válido
      console.log('El campo  está vacío o no tiene un valor válido.');
    }
  }
}
