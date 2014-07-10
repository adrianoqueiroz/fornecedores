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
import model.Cidade;
import model.Endereco;
import model.Fornecedor;

/**
 *
 * @author Adriano
 */
@Stateless
public class EnderecoJpaController implements Serializable {
    
    @PersistenceUnit(unitName = "fornecedoresPU") //inject from your application server
    private EntityManagerFactory emf;
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }   

    public void create(Endereco endereco) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (endereco.getFornecedorCollection() == null) {
            endereco.setFornecedorCollection(new ArrayList<Fornecedor>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            Cidade cidadeId = endereco.getCidadeId();
            if (cidadeId != null) {
                cidadeId = em.getReference(cidadeId.getClass(), cidadeId.getId());
                endereco.setCidadeId(cidadeId);
            }
            Collection<Fornecedor> attachedFornecedorCollection = new ArrayList<Fornecedor>();
            for (Fornecedor fornecedorCollectionFornecedorToAttach : endereco.getFornecedorCollection()) {
                fornecedorCollectionFornecedorToAttach = em.getReference(fornecedorCollectionFornecedorToAttach.getClass(), fornecedorCollectionFornecedorToAttach.getId());
                attachedFornecedorCollection.add(fornecedorCollectionFornecedorToAttach);
            }
            endereco.setFornecedorCollection(attachedFornecedorCollection);
            em.persist(endereco);
            if (cidadeId != null) {
                cidadeId.getEnderecoCollection().add(endereco);
                cidadeId = em.merge(cidadeId);
            }
            for (Fornecedor fornecedorCollectionFornecedor : endereco.getFornecedorCollection()) {
                Endereco oldEnderecoIdOfFornecedorCollectionFornecedor = fornecedorCollectionFornecedor.getEnderecoId();
                fornecedorCollectionFornecedor.setEnderecoId(endereco);
                fornecedorCollectionFornecedor = em.merge(fornecedorCollectionFornecedor);
                if (oldEnderecoIdOfFornecedorCollectionFornecedor != null) {
                    oldEnderecoIdOfFornecedorCollectionFornecedor.getFornecedorCollection().remove(fornecedorCollectionFornecedor);
                    oldEnderecoIdOfFornecedorCollectionFornecedor = em.merge(oldEnderecoIdOfFornecedorCollectionFornecedor);
                }
            }
        } catch (Exception ex) {
            if (findEndereco(endereco.getId()) != null) {
                throw new PreexistingEntityException("Endereco " + endereco + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Endereco endereco) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Endereco persistentEndereco = em.find(Endereco.class, endereco.getId());
            Cidade cidadeIdOld = persistentEndereco.getCidadeId();
            Cidade cidadeIdNew = endereco.getCidadeId();
            Collection<Fornecedor> fornecedorCollectionOld = persistentEndereco.getFornecedorCollection();
            Collection<Fornecedor> fornecedorCollectionNew = endereco.getFornecedorCollection();
            List<String> illegalOrphanMessages = null;
            for (Fornecedor fornecedorCollectionOldFornecedor : fornecedorCollectionOld) {
                if (!fornecedorCollectionNew.contains(fornecedorCollectionOldFornecedor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Fornecedor " + fornecedorCollectionOldFornecedor + " since its enderecoId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (cidadeIdNew != null) {
                cidadeIdNew = em.getReference(cidadeIdNew.getClass(), cidadeIdNew.getId());
                endereco.setCidadeId(cidadeIdNew);
            }
            Collection<Fornecedor> attachedFornecedorCollectionNew = new ArrayList<Fornecedor>();
            for (Fornecedor fornecedorCollectionNewFornecedorToAttach : fornecedorCollectionNew) {
                fornecedorCollectionNewFornecedorToAttach = em.getReference(fornecedorCollectionNewFornecedorToAttach.getClass(), fornecedorCollectionNewFornecedorToAttach.getId());
                attachedFornecedorCollectionNew.add(fornecedorCollectionNewFornecedorToAttach);
            }
            fornecedorCollectionNew = attachedFornecedorCollectionNew;
            endereco.setFornecedorCollection(fornecedorCollectionNew);
            endereco = em.merge(endereco);
            if (cidadeIdOld != null && !cidadeIdOld.equals(cidadeIdNew)) {
                cidadeIdOld.getEnderecoCollection().remove(endereco);
                cidadeIdOld = em.merge(cidadeIdOld);
            }
            if (cidadeIdNew != null && !cidadeIdNew.equals(cidadeIdOld)) {
                cidadeIdNew.getEnderecoCollection().add(endereco);
                cidadeIdNew = em.merge(cidadeIdNew);
            }
            for (Fornecedor fornecedorCollectionNewFornecedor : fornecedorCollectionNew) {
                if (!fornecedorCollectionOld.contains(fornecedorCollectionNewFornecedor)) {
                    Endereco oldEnderecoIdOfFornecedorCollectionNewFornecedor = fornecedorCollectionNewFornecedor.getEnderecoId();
                    fornecedorCollectionNewFornecedor.setEnderecoId(endereco);
                    fornecedorCollectionNewFornecedor = em.merge(fornecedorCollectionNewFornecedor);
                    if (oldEnderecoIdOfFornecedorCollectionNewFornecedor != null && !oldEnderecoIdOfFornecedorCollectionNewFornecedor.equals(endereco)) {
                        oldEnderecoIdOfFornecedorCollectionNewFornecedor.getFornecedorCollection().remove(fornecedorCollectionNewFornecedor);
                        oldEnderecoIdOfFornecedorCollectionNewFornecedor = em.merge(oldEnderecoIdOfFornecedorCollectionNewFornecedor);
                    }
                }
            }
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = endereco.getId();
                if (findEndereco(id) == null) {
                    throw new NonexistentEntityException("The endereco with id " + id + " no longer exists.");
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
            Endereco endereco;
            try {
                endereco = em.getReference(Endereco.class, id);
                endereco.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The endereco with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Fornecedor> fornecedorCollectionOrphanCheck = endereco.getFornecedorCollection();
            for (Fornecedor fornecedorCollectionOrphanCheckFornecedor : fornecedorCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Endereco (" + endereco + ") cannot be destroyed since the Fornecedor " + fornecedorCollectionOrphanCheckFornecedor + " in its fornecedorCollection field has a non-nullable enderecoId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Cidade cidadeId = endereco.getCidadeId();
            if (cidadeId != null) {
                cidadeId.getEnderecoCollection().remove(endereco);
                cidadeId = em.merge(cidadeId);
            }
            em.remove(endereco);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Endereco> findEnderecoEntities() {
        return findEnderecoEntities(true, -1, -1);
    }

    public List<Endereco> findEnderecoEntities(int maxResults, int firstResult) {
        return findEnderecoEntities(false, maxResults, firstResult);
    }

    private List<Endereco> findEnderecoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Endereco.class));
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

    public Endereco findEndereco(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Endereco.class, id);
        } finally {
            em.close();
        }
    }

    public int getEnderecoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Endereco> rt = cq.from(Endereco.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
