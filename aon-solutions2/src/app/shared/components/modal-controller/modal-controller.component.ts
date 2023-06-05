import { ChangeDetectorRef, Component, ComponentFactoryResolver, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { takeUntil} from 'rxjs/operators';

@Component({
  selector: 'app-modal-controller',
  templateUrl: './modal-controller.component.html',
  styleUrls: ['./modal-controller.component.scss']
})
export class ModalControllerComponent implements OnInit {

  private readonly unsubscribeSubject$: Subject<void> = new Subject<void>();

  constructor(public dialog: MatDialog, public cdr: ChangeDetectorRef) {
  }

  openDialog(modal:any, f: any, data?: any): void {
    const dialogRef = this.dialog.open(modal, {width: 'auto', data: data});
    dialogRef
        .afterClosed()
        .pipe(takeUntil(this.unsubscribeSubject$))
        .subscribe((result) => {
          f(result);
          this.cdr.detectChanges();
        });
  }
  
  ngOnDestroy(): void {
    this.unsubscribeSubject$.next();
    this.unsubscribeSubject$.complete();
    return;
  }

  ngOnInit(): void {
  }

}
