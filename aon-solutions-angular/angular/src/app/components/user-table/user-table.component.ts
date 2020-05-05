import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog, MatTable, MatSnackBar } from '@angular/material';
import { SureDialogComponent } from '../dialogs/sure-dialog/sure-dialog.component';
import { TediUtils } from '../../utils/tedi-utils';
import { SharedService, UserService} from '../../services/services';
import { User } from '../../models/models';
import TEDI from '@translogia/tedi-sdk';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-user-table',
  templateUrl: './user-table.component.html',
  styleUrls: ['./user-table.component.css'],
})
export class UserTableComponent implements OnInit, OnDestroy {
  private readonly API_URL = environment.apiUrl;

  displayedColumns: string[] = ['name', 'surname', 'email', 'document', 'actions'];
  displayedMobileColumns: string[] = ['email', 'actions'];
  dataSource: User[];

  @ViewChild(MatTable, {static: false}) public matTable: MatTable<User>;
  constructor(public service: SharedService,
          private router: Router, public dialog: MatDialog,
          public snackBar: MatSnackBar, public usrService: UserService) {
      this.usrService.filterObservable.subscribe(r => this.init());
  }

  ngOnInit() {
    this.init();
  }

  ngOnDestroy() {
    if (this.service.selection === 'users') {
      this.service.selection = undefined;
    }
  }

  public init(): void {
    this.service.loading = true;
    this.service.getUsers().subscribe(
      result => {
        this.usrService.userList = result || []
        this.dataSource = this.usrService.userList.filter(f => this.userFilter(f));
        this.service.loading = false;
      },
      () => this.dataSource = []
    );
  }

  private userFilter(f: User):  boolean{
    let q = this.usrService.filter;
    let val = true;
    if(q.value) {
      const name = q.value ? f.name && f.name.toUpperCase().includes(q.value.toUpperCase()) : true;
      const surname = q.value ? f.surname && f.surname.toUpperCase().includes(q.value.toUpperCase()) : true;
      const email = q.value ? f.email && f.email.toUpperCase().includes(q.value.toUpperCase()) : true;
      const doc = q.value ? f.document && f.document.toUpperCase().includes(q.value.toUpperCase()) : true;
      val = name || surname || email || doc;
    }
    const admin = q.admin ? f.admin : true;
    const gestor =  q.gestor ? f.gestor || f.admin : true;

    return val && admin && gestor;
  }
  public editUser(user: User): void {
    this.usrService.setUser(user);
    this.router.navigate(['/myAccount/editUser']);
  }

  public deleteUser(email: string): void {
    TEDI.user.deleteUser({email: email}, this.service.getToken(), this.API_URL)
    .subscribe(
      result => {
        this.init();
        this.snackBar.open('El usuario se ha borrado correctamente.', '', {
          duration: 2000,
        });
      },
      error => TediUtils.showError(this.dialog, error.error.error)
    );
  }

  showSure(email: string) {
    const dialogRef = this.dialog.open(SureDialogComponent, {
      height: '150px',
      panelClass: 'tedi-user-dialog-panel',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleteUser(email);
      }
    });
  }

  public permissionUser(user: User): void {
    this.usrService.setUser(user);
    this.router.navigate(['/myAccount/permission']);
  }

  public onScroll() {

  }

  isMobile(): boolean {
    return this.service.isMobile;
  }
}
