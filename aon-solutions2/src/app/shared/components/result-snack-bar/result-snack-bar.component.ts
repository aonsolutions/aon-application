import { Component, Inject, OnInit } from '@angular/core';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';

@Component({
  selector: 'app-result-snack-bar',
  templateUrl: './result-snack-bar.component.html',
  styleUrls: ['./result-snack-bar.component.scss']
})
export class ResultSnackBarComponent implements OnInit {

  constructor(
    @Inject(MAT_SNACK_BAR_DATA) public data: any,
    public _snackbar: MatSnackBarRef<ResultSnackBarComponent>
  ) {}

  ngOnInit(): void {}

  destroy() {
    this.data.preClose();
  }

}
