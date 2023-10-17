import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TaxModelService } from 'src/app/core/services/tax-model.service';

@Component({
  selector: 'app-modal-taxes-details',
  templateUrl: './modal-taxes-details.component.html',
  styleUrls: ['./modal-taxes-details.component.scss'],
})
export class ModalTaxesDetailsComponent {
  model: any;
  nameModel: string = '';
  constructor(
    private taxModelService: TaxModelService,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ModalTaxesDetailsComponent>
  ) {
    this.taxModelService.getTax(data.key).then((response) => {
      this.model = response;
      this.nameModel = this.model.Name;
    });
  }

  ngOnInit():void {
  }
}
