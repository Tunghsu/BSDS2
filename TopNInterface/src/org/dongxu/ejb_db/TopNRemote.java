/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dongxu.ejb_db;

import javax.ejb.Remote;

/**
 *
 * @author dongxu
 */
@Remote
public interface TopNRemote {

    String getTopN(Integer n);
    
}
