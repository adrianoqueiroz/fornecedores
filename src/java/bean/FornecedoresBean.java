package bean;

import JPA.CategoriaJpaController;
import JPA.CidadeJpaController;
import JPA.ContatoJpaController;
import JPA.EnderecoJpaController;
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
import model.Contato;
import model.Endereco;
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
    @EJB
    private EnderecoJpaController enderecoJpaController;
    @EJB
    private ContatoJpaController contatoJpaController;

    private Fornecedor fornecedor = new Fornecedor();

    private Endereco endereco = new Endereco();

    private Contato contato = new Contato();

    private Contato contatoAlternativo = new Contato();

    private int estadoSelecionado;

    private int cidadeSelecionada;

    private List<String> categoriasSelecionadasString = new ArrayList<>();

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Contato getContato() {
        return contato;
    }

    public void setContato(Contato contato) {
        this.contato = contato;
    }

    public Contato getContatoAlternativo() {
        return contatoAlternativo;
    }

    public void setContatoAlternativo(Contato contatoAlternativo) {
        this.contatoAlternativo = contatoAlternativo;
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

    public List<String> getCategoriasSelecionadasString() {
        return categoriasSelecionadasString;
    }

    public void setCategoriasSelecionadasString(List<String> categoriasSelecionadasString) {
        this.categoriasSelecionadasString = categoriasSelecionadasString;
    }

    public List<Categoria> getCategorias() {
        return categoriaJpaController.findCategoriaEntities();
    }

    public List<SelectItem> getSelectItemEstados() {

        List<Estado> listaEstados = estadoJpaController.findEstadoEntities();
        List<SelectItem> itens = new ArrayList<>(listaEstados.size());

        for (Estado e : listaEstados) {
            itens.add(new SelectItem(e.getId(), e.getNome()));

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
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Qualificação do Fornecedor", "Você atribuiu nota " + ((Integer) rateEvent.getRating()));
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void oncancel() {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Qualificação do Fornecedor", "Atribuição resetada");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void salvar() {
        try {
            List<Categoria> categoriasSelecionadas = categoriaJpaController.findCategoriasByNomes(categoriasSelecionadasString);
            fornecedor.setCategoriaCollection(categoriasSelecionadas);

            Cidade cidade = cidadeJpaController.findCidade(cidadeSelecionada);
            endereco.setCidadeId(cidade);
            enderecoJpaController.create(endereco);
            fornecedor.setEnderecoId(endereco);

            contatoJpaController.create(contato);
            contatoJpaController.create(contatoAlternativo);
            fornecedor.setContatoCollection(new ArrayList<Contato>());
            fornecedor.getContatoCollection().add(contato);
            fornecedor.getContatoCollection().add(contatoAlternativo);

            fornecedorJpaController.create(fornecedor);

        } catch (Exception e) {
            System.out.println("Erro na persistência do fornecedor: " + e.getMessage());
        }

    }
}
