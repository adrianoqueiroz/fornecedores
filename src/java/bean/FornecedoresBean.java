package bean;

import JPA.CategoriaJpaController;
import JPA.CidadeJpaController;
import JPA.EstadoJpaController;
import JPA.FornecedorJpaController;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import model.Categoria;
import model.Cidade;
import model.Estado;
import model.Fornecedor;
import org.primefaces.event.RateEvent;

@ManagedBean
@ViewScoped
public class FornecedoresBean {

    @EJB
    private EstadoJpaController estadoJpaController;
    @EJB
    private CidadeJpaController cidadeJpaController;
    @EJB
    private CategoriaJpaController categoriaJpaController;
    @EJB
    private FornecedorJpaController fornecedorJpaController;

    private Fornecedor fornecedor = new Fornecedor();

    private int estadoSelecionado;

    private int cidadeSelecionada;

    private List<String> categoriasSelecionadas = new ArrayList<>();

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

    public List<String> getCategoriasSelecionadas() {
        return categoriasSelecionadas;
    }

    public void setCategoriasSelecionadas(List<String> categoriasSelecionadas) {
        this.categoriasSelecionadas = categoriasSelecionadas;
    }

    public List<String> getListaCategorias() {
        List<Categoria> listaCategorias = categoriaJpaController.findCategoriaEntities();
        List<String> lista = new ArrayList<>(listaCategorias.size());

        for (Categoria c : listaCategorias) {
            lista.add(c.getNome());
        }
        return lista;
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
        List<Cidade> listaCidades = cidadeJpaController.findCidadeByEstado(estadoSelecionado);
        List<SelectItem> itens = new ArrayList<>(listaCidades.size());

        for (Cidade c : listaCidades) {
            itens.add(new SelectItem(c.getId(), c.getNome()));

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

    public void salvar() throws Exception {
        if (cidadeSelecionada != 0) {
            Cidade cidade = cidadeJpaController.findCidade(cidadeSelecionada);
            fornecedor.getEnderecoId().setCidadeId(cidade);
            
            fornecedorJpaController.create(fornecedor);
        }
    }
}
