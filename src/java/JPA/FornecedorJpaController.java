/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package JPA;

import JPA.exceptions.IllegalOrphanException;
import JPA.exceptions.NonexistentEntityException;
import JPA.exceptions.PreexistingEntityException;
import JPA.exceptions.RollbackFailureException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import model.Categoria;
import model.Contato;
import model.Endereco;
import model.Fornecedor;

/**
 *
 * @author Adriano
 */
@Stateless
public class FornecedorJpaController implements Serializable {
    @PersistenceUnit(unitName = "fornecedoresPU") //inject from your application server
    private EntityManagerFactory emf;
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }   
    
    public void create(Fornecedor fornecedor) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (fornecedor.getCategoriaCollection() == null) {
            fornecedor.setCategoriaCollection(new ArrayList<Categoria>());
        }
        if (fornecedor.getContatoCollection() == null) {
            fornecedor.setContatoCollection(new ArrayList<Contato>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            Endereco enderecoId = fornecedor.getEnderecoId();
            if (enderecoId != null) {
                enderecoId = em.getReference(enderecoId.getClass(), enderecoId.getId());
                fornecedor.setEnderecoId(enderecoId);
            }
            Collection<Categoria> attachedCategoriaCollection = new ArrayList<Categoria>();
            for (Categoria categoriaCollectionCategoriaToAttach : fornecedor.getCategoriaCollection()) {
                categoriaCollectionCategoriaToAttach = em.getReference(categoriaCollectionCategoriaToAttach.getClass(), categoriaCollectionCategoriaToAttach.getId());
                attachedCategoriaCollection.add(categoriaCollectionCategoriaToAttach);
            }
            fornecedor.setCategoriaCollection(attachedCategoriaCollection);
            Collection<Contato> attachedContatoCollection = new ArrayList<Contato>();
            for (Contato contatoCollectionContatoToAttach : fornecedor.getContatoCollection()) {
                contatoCollectionContatoToAttach = em.getReference(contatoCollectionContatoToAttach.getClass(), contatoCollectionContatoToAttach.getId());
                attachedContatoCollection.add(contatoCollectionContatoToAttach);
            }
            fornecedor.setContatoCollection(attachedContatoCollection);
            em.persist(fornecedor);
            if (enderecoId != null) {
                enderecoId.getFornecedorCollection().add(fornecedor);
                enderecoId = em.merge(enderecoId);
            }
            for (Categoria categoriaCollectionCategoria : fornecedor.getCategoriaCollection()) {
                categoriaCollectionCategoria.getFornecedorCollection().add(fornecedor);
                categoriaCollectionCategoria = em.merge(categoriaCollectionCategoria);
            }
            for (Contato contatoCollectionContato : fornecedor.getContatoCollection()) {
                Fornecedor oldFornecedorIdOfContatoCollectionContato = contatoCollectionContato.getFornecedorId();
                contatoCollectionContato.setFornecedorId(fornecedor);
                contatoCollectionContato = em.merge(contatoCollectionContato);
                if (oldFornecedorIdOfContatoCollectionContato != null) {
                    oldFornecedorIdOfContatoCollectionContato.getContatoCollection().remove(contatoCollectionContato);
                    oldFornecedorIdOfContatoCollectionContato = em.merge(oldFornecedorIdOfContatoCollectionContato);
                }
            }
        } catch (Exception ex) {
            if (findFornecedor(fornecedor.getId()) != null) {
                throw new PreexistingEntityException("Fornecedor " + fornecedor + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Fornecedor fornecedor) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Fornecedor persistentFornecedor = em.find(Fornecedor.class, fornecedor.getId());
            Endereco enderecoIdOld = persistentFornecedor.getEnderecoId();
            Endereco enderecoIdNew = fornecedor.getEnderecoId();
            Collection<Categoria> categoriaCollectionOld = persistentFornecedor.getCategoriaCollection();
            Collection<Categoria> categoriaCollectionNew = fornecedor.getCategoriaCollection();
            Collection<Contato> contatoCollectionOld = persistentFornecedor.getContatoCollection();
            Collection<Contato> contatoCollectionNew = fornecedor.getContatoCollection();
            List<String> illegalOrphanMessages = null;
            for (Contato contatoCollectionOldContato : contatoCollectionOld) {
                if (!contatoCollectionNew.contains(contatoCollectionOldContato)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Contato " + contatoCollectionOldContato + " since its fornecedorId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (enderecoIdNew != null) {
                enderecoIdNew = em.getReference(enderecoIdNew.getClass(), enderecoIdNew.getId());
                fornecedor.setEnderecoId(enderecoIdNew);
            }
            Collection<Categoria> attachedCategoriaCollectionNew = new ArrayList<Categoria>();
            for (Categoria categoriaCollectionNewCategoriaToAttach : categoriaCollectionNew) {
                categoriaCollectionNewCategoriaToAttach = em.getReference(categoriaCollectionNewCategoriaToAttach.getClass(), categoriaCollectionNewCategoriaToAttach.getId());
                attachedCategoriaCollectionNew.add(categoriaCollectionNewCategoriaToAttach);
            }
            categoriaCollectionNew = attachedCategoriaCollectionNew;
            fornecedor.setCategoriaCollection(categoriaCollectionNew);
            Collection<Contato> attachedContatoCollectionNew = new ArrayList<Contato>();
            for (Contato contatoCollectionNewContatoToAttach : contatoCollectionNew) {
                contatoCollectionNewContatoToAttach = em.getReference(contatoCollectionNewContatoToAttach.getClass(), contatoCollectionNewContatoToAttach.getId());
                attachedContatoCollectionNew.add(contatoCollectionNewContatoToAttach);
            }
            contatoCollectionNew = attachedContatoCollectionNew;
            fornecedor.setContatoCollection(contatoCollectionNew);
            fornecedor = em.merge(fornecedor);
            if (enderecoIdOld != null && !enderecoIdOld.equals(enderecoIdNew)) {
                enderecoIdOld.getFornecedorCollection().remove(fornecedor);
                enderecoIdOld = em.merge(enderecoIdOld);
            }
            if (enderecoIdNew != null && !enderecoIdNew.equals(enderecoIdOld)) {
                enderecoIdNew.getFornecedorCollection().add(fornecedor);
                enderecoIdNew = em.merge(enderecoIdNew);
            }
            for (Categoria categoriaCollectionOldCategoria : categoriaCollectionOld) {
                if (!categoriaCollectionNew.contains(categoriaCollectionOldCategoria)) {
                    categoriaCollectionOldCategoria.getFornecedorCollection().remove(fornecedor);
                    categoriaCollectionOldCategoria = em.merge(categoriaCollectionOldCategoria);
                }
            }
            for (Categoria categoriaCollectionNewCategoria : categoriaCollectionNew) {
                if (!categoriaCollectionOld.contains(categoriaCollectionNewCategoria)) {
                    categoriaCollectionNewCategoria.getFornecedorCollection().add(fornecedor);
                    categoriaCollectionNewCategoria = em.merge(categoriaCollectionNewCategoria);
                }
            }
            for (Contato contatoCollectionNewContato : contatoCollectionNew) {
                if (!contatoCollectionOld.contains(contatoCollectionNewContato)) {
                    Fornecedor oldFornecedorIdOfContatoCollectionNewContato = contatoCollectionNewContato.getFornecedorId();
                    contatoCollectionNewContato.setFornecedorId(fornecedor);
                    contatoCollectionNewContato = em.merge(contatoCollectionNewContato);
                    if (oldFornecedorIdOfContatoCollectionNewContato != null && !oldFornecedorIdOfContatoCollectionNewContato.equals(fornecedor)) {
                        oldFornecedorIdOfContatoCollectionNewContato.getContatoCollection().remove(contatoCollectionNewContato);
                        oldFornecedorIdOfContatoCollectionNewContato = em.merge(oldFornecedorIdOfContatoCollectionNewContato);
                    }
                }
            }
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = fornecedor.getId();
                if (findFornecedor(id) == null) {
                    throw new NonexistentEntityException("The fornecedor with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Fornecedor fornecedor;
            try {
                fornecedor = em.getReference(Fornecedor.class, id);
                fornecedor.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The fornecedor with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Contato> contatoCollectionOrphanCheck = fornecedor.getContatoCollection();
            for (Contato contatoCollectionOrphanCheckContato : contatoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Fornecedor (" + fornecedor + ") cannot be destroyed since the Contato " + contatoCollectionOrphanCheckContato + " in its contatoCollection field has a non-nullable fornecedorId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Endereco enderecoId = fornecedor.getEnderecoId();
            if (enderecoId != null) {
                enderecoId.getFornecedorCollection().remove(fornecedor);
                enderecoId = em.merge(enderecoId);
            }
            Collection<Categoria> categoriaCollection = fornecedor.getCategoriaCollection();
            for (Categoria categoriaCollectionCategoria : categoriaCollection) {
                categoriaCollectionCategoria.getFornecedorCollection().remove(fornecedor);
                categoriaCollectionCategoria = em.merge(categoriaCollectionCategoria);
            }
            em.remove(fornecedor);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Fornecedor> findFornecedorEntities() {
        return findFornecedorEntities(true, -1, -1);
    }

    public List<Fornecedor> findFornecedorEntities(int maxResults, int firstResult) {
        return findFornecedorEntities(false, maxResults, firstResult);
    }

    private List<Fornecedor> findFornecedorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Fornecedor.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Fornecedor findFornecedor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Fornecedor.class, id);
        } finally {
            em.close();
        }
    }

    public int getFornecedorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Fornecedor> rt = cq.from(Fornecedor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
