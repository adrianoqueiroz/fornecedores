package bean;

import JPA.CategoriaJpaController;
import JPA.CidadeJpaController;
import JPA.EstadoJpaController;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import model.Categoria;
import model.Cidade;
import model.Estado;
import model.Fornecedor;
import org.primefaces.event.RateEvent;

@ManagedBean
public class FornecedoresBean {

    @EJB
    private EstadoJpaController estadoJpaController;
    @EJB
    private CidadeJpaController cidadeJpaController;
    @EJB
    private CategoriaJpaController categoriaJpaController;

    private Fornecedor fornecedor = new Fornecedor();

    private int estadoSelecionado;

    private int cidadeSelecionada;

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public int getEstadoSelecionado() {
        return estadoSelecionado;
    }

    public void setEstadoSelecionado(int estadoSelecionado) {
        this.estadoSelecionado = estadoSelecionado;
    }

    public int getCidadeSelecionada() {
        return cidadeSelecionada;
    }

    public void setCidadeSelecionada(int cidadeSelecionada) {
        this.cidadeSelecionada = cidadeSelecionada;
    }

    public List<SelectItem> getSelectItemCategorias() {
        List<Categoria> listaCategorias = categoriaJpaController.findCategoriaEntities();
        List<SelectItem> itens = new ArrayList<>(listaCategorias.size());

        for (Categoria c : listaCategorias) {
            itens.add(new SelectItem(c.getId(), c.getNome()));
        }
        return itens;
    }

    public List<SelectItem> getSelectItemEstados() {

        List<Estado> listaEstados = estadoJpaController.findEstadoEntities();
        List<SelectItem> itens = new ArrayList<>(listaEstados.size());

        for (Estado e : listaEstados) {
            itens.add(new SelectItem(e.getId(), e.getSigla()));

        }
        return itens;
    }

    public List<SelectItem> getSelectItemCidades() {
        List<Cidade> listaCidades = cidadeJpaController.findCidadeEntities();
        //List<Cidade> listaCidades = cidadeJpaController.findCidadeEntitiesByEstadoId(estadoSelecionado);
        List<SelectItem> itens = new ArrayList<>(listaCidades.size());

        for (Cidade c : listaCidades) {
            if (estadoSelecionado == c.getEstadoId().getId()) {
                itens.add(new SelectItem(c.getId(), c.getNome()));
            }
        }
        return itens;
    }

    public void onrate(RateEvent rateEvent) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Qualificação do Fornecedor", "Você qualificou:" + ((Integer) rateEvent.getRating()));
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void oncancel() {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Qualificação do Fornecedor", "Qualificação resetada");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}
