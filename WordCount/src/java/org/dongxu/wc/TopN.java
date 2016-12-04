/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dongxu.wc;

import org.dongxu.ejb_db.TopNRemote;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author dongxu
 */
@Stateless
public class TopN implements TopNRemote {

    private static SingMessageClass singMessageClass;

    public TopN() {
        singMessageClass = SingMessageClass.getInstance();
    }
    
    @Override
    public String getTopN(Integer n) {
        return singMessageClass.getTopN(n);
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
}
