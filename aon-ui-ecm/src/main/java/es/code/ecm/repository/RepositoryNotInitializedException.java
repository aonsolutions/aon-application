/**
 * 
 * Copyright 1993-2006 obinary Ltd. (http://www.obinary.com) All rights reserved.
 */
package es.code.ecm.repository;

import javax.jcr.RepositoryException;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 20/06/2007
 */

public class RepositoryNotInitializedException extends RepositoryException {

   public RepositoryNotInitializedException(String message) {
       super(message);
   }

   public RepositoryNotInitializedException(String message, Exception cause) {
       super(message, (cause instanceof RepositoryNotInitializedException)
           ? ((RepositoryNotInitializedException) cause).getCause()
           : cause);
   }

   public RepositoryNotInitializedException(Throwable root) {
       super(root);
   }
}
