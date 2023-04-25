import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { ModalCreateConsultComponent } from './components/modal-create-consult/modal-create-consult.component';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-inbox',
  templateUrl: './inbox.component.html',
  styleUrls: ['./inbox.component.scss']
})
export class InboxComponent implements OnInit {

  bandejaClass: string = 'optionMenuInbox';
  consultasClass: string = 'optionMenuInbox';
  tareasClass: string = 'optionMenuInbox';
  notificacionesclass: string = 'optionMenuInbox';
  class: string = 'optionMenuInbox';
  onClickClass: string = 'optionMenuInbox optionMenuInboxClicked';
  selectedMenu : string = "";
  menuColor: string = 'white'
  menuCircleColor: string = '#feedec'
  private readonly unsubscribeSubject$: Subject<void> = new Subject<void>();


  constructor(public dialog: MatDialog, public cdr: ChangeDetectorRef) {
    this.selectedMenu = 'bandeja';
    this.bandejaClass = this.onClickClass;
  }

  resetClass(){
    this.consultasClass = this.bandejaClass = this.tareasClass = this.notificacionesclass = this.class;
  }

  openDialog(){
    const dialogRef = this.dialog.open(ModalCreateConsultComponent, {width: 'auto', maxWidth: '100vw', hasBackdrop: false}); //data: {name: this.name, animal: this.animal}
    dialogRef
        .afterClosed()
        .pipe(takeUntil(this.unsubscribeSubject$))
        .subscribe((result) => {
          this.cdr.detectChanges();
        });
  }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
    this.unsubscribeSubject$.next();
    this.unsubscribeSubject$.complete();
    return;
  }
}
