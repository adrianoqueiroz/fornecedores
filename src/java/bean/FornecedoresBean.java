package bean;

import javax.faces.bean.ManagedBean;
import model.Empresa;


@ManagedBean
public class FornecedoresBean {
    private Empresa empresa = new Empresa();
    
    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }
}
