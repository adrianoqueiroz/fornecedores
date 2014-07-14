/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bean;

import JPA.FornecedorJpaController;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import model.Fornecedor;

/**
 *
 * @author Adriano
 */
@ManagedBean
@RequestScoped
public class ListaBean {
    @EJB
    private FornecedorJpaController fornecedorJpaController;
    
    public List<Fornecedor> getListaFornecedores(){
        return fornecedorJpaController.findFornecedorEntities();
    }
    
}
