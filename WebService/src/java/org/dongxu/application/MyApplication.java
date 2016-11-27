/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dongxu.application;

import java.util.Set;
import java.util.HashSet;

/**
 *
 * @author dongxu
 */
public class MyApplication extends javax.ws.rs.core.Application {
   public Set<Class<?>> getClasses() {
      Set<Class<?>> s = new HashSet<Class<?>>();
      s.add(org.dongxu.ws.WebService.class);
      return s;
   }
}
