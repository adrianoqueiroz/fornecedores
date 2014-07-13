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

    private int estadoSelecionado;

    private int cidadeSelecionada;

    private List<String> categoriasNome;
    private List<String> categoriasSelecionadasNome;

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

    public List<String> getCategoriasSelecionadasNome() {
        return categoriasSelecionadasNome;
    }

    public void setCategoriasSelecionadasNome(List<String> categoriasSelecionadasNome) {
        this.categoriasSelecionadasNome = categoriasSelecionadasNome;
    }

    public List<String> getCategoriasNome() {
        List<Categoria> categorias = categoriaJpaController.findCategoriaEntities();
        categoriasNome = new ArrayList<>(categorias.size());
        for (Categoria c : categorias) {
            categoriasNome.add(c.getNome());
        }

        return categoriasNome;
    }

    public void setCategoriasNome(List<String> categoriasNome) {
        this.categoriasNome = categoriasNome;
    }

    public List<String> getListaCategoriasNome() {
        List<Categoria> categorias = categoriaJpaController.findCategoriaEntities();
        List<String> nomesCategorias = new ArrayList<>();

        for (Categoria c : categorias) {
            nomesCategorias.add(c.getNome());
        }
        return nomesCategorias;
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

    public List<Categoria> getEntitiesCategoriaByNomes() {
        List<Categoria> listaCategorias = new ArrayList<>();
        Categoria categoraEncontrada;

        for (String nome : categoriasSelecionadasNome) {
            categoraEncontrada = categoriaJpaController.findCategoriaByNome(nome);
            listaCategorias.add(categoraEncontrada);
        }
        return listaCategorias;

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
            Cidade cidade = cidadeJpaController.findCidade(cidadeSelecionada);
            endereco.setCidadeId(cidade);
            enderecoJpaController.create(endereco);
            fornecedor.setEnderecoId(endereco);
            
            contatoJpaController.create(contato);
            fornecedor.setContatoCollection(new ArrayList<Contato>());
            fornecedor.getContatoCollection().add(contato);
            
            fornecedorJpaController.create(fornecedor);

        } catch (Exception e) {
            System.out.println("Erro na persistência do fornecedor: " + e.getMessage());
        }

    }
}
