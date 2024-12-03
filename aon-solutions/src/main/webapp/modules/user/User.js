export class User {

	
  roles;

  constructor() {
    this.roles = [];
  }

  createUser(user) {
    if(user) {
      this.roles = user.roles;
    }
  }

  isAdmin() {
    return this.roles.includes('ADMIN');
  }

}
