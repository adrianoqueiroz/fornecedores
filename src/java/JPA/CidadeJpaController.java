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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import model.Cidade;
import model.Estado;
import model.Fornecedor;

/**
 *
 * @author Adriano
 */
@Stateless
public class CidadeJpaController implements Serializable {

    @PersistenceUnit(unitName = "fornecedoresPU") //inject from your application server
    private EntityManagerFactory emf;
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }   
    
    public void create(Cidade cidade) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (cidade.getFornecedorCollection() == null) {
            cidade.setFornecedorCollection(new ArrayList<Fornecedor>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            Estado estadoId = cidade.getEstadoId();
            if (estadoId != null) {
                estadoId = em.getReference(estadoId.getClass(), estadoId.getId());
                cidade.setEstadoId(estadoId);
            }
            Collection<Fornecedor> attachedFornecedorCollection = new ArrayList<Fornecedor>();
            for (Fornecedor fornecedorCollectionFornecedorToAttach : cidade.getFornecedorCollection()) {
                fornecedorCollectionFornecedorToAttach = em.getReference(fornecedorCollectionFornecedorToAttach.getClass(), fornecedorCollectionFornecedorToAttach.getId());
                attachedFornecedorCollection.add(fornecedorCollectionFornecedorToAttach);
            }
            cidade.setFornecedorCollection(attachedFornecedorCollection);
            em.persist(cidade);
            if (estadoId != null) {
                estadoId.getCidadeCollection().add(cidade);
                estadoId = em.merge(estadoId);
            }
            for (Fornecedor fornecedorCollectionFornecedor : cidade.getFornecedorCollection()) {
                Cidade oldCidadeIdOfFornecedorCollectionFornecedor = fornecedorCollectionFornecedor.getCidadeId();
                fornecedorCollectionFornecedor.setCidadeId(cidade);
                fornecedorCollectionFornecedor = em.merge(fornecedorCollectionFornecedor);
                if (oldCidadeIdOfFornecedorCollectionFornecedor != null) {
                    oldCidadeIdOfFornecedorCollectionFornecedor.getFornecedorCollection().remove(fornecedorCollectionFornecedor);
                    oldCidadeIdOfFornecedorCollectionFornecedor = em.merge(oldCidadeIdOfFornecedorCollectionFornecedor);
                }
            }
        } catch (Exception ex) {
            if (findCidade(cidade.getId()) != null) {
                throw new PreexistingEntityException("Cidade " + cidade + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cidade cidade) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Cidade persistentCidade = em.find(Cidade.class, cidade.getId());
            Estado estadoIdOld = persistentCidade.getEstadoId();
            Estado estadoIdNew = cidade.getEstadoId();
            Collection<Fornecedor> fornecedorCollectionOld = persistentCidade.getFornecedorCollection();
            Collection<Fornecedor> fornecedorCollectionNew = cidade.getFornecedorCollection();
            List<String> illegalOrphanMessages = null;
            for (Fornecedor fornecedorCollectionOldFornecedor : fornecedorCollectionOld) {
                if (!fornecedorCollectionNew.contains(fornecedorCollectionOldFornecedor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Fornecedor " + fornecedorCollectionOldFornecedor + " since its cidadeId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (estadoIdNew != null) {
                estadoIdNew = em.getReference(estadoIdNew.getClass(), estadoIdNew.getId());
                cidade.setEstadoId(estadoIdNew);
            }
            Collection<Fornecedor> attachedFornecedorCollectionNew = new ArrayList<Fornecedor>();
            for (Fornecedor fornecedorCollectionNewFornecedorToAttach : fornecedorCollectionNew) {
                fornecedorCollectionNewFornecedorToAttach = em.getReference(fornecedorCollectionNewFornecedorToAttach.getClass(), fornecedorCollectionNewFornecedorToAttach.getId());
                attachedFornecedorCollectionNew.add(fornecedorCollectionNewFornecedorToAttach);
            }
            fornecedorCollectionNew = attachedFornecedorCollectionNew;
            cidade.setFornecedorCollection(fornecedorCollectionNew);
            cidade = em.merge(cidade);
            if (estadoIdOld != null && !estadoIdOld.equals(estadoIdNew)) {
                estadoIdOld.getCidadeCollection().remove(cidade);
                estadoIdOld = em.merge(estadoIdOld);
            }
            if (estadoIdNew != null && !estadoIdNew.equals(estadoIdOld)) {
                estadoIdNew.getCidadeCollection().add(cidade);
                estadoIdNew = em.merge(estadoIdNew);
            }
            for (Fornecedor fornecedorCollectionNewFornecedor : fornecedorCollectionNew) {
                if (!fornecedorCollectionOld.contains(fornecedorCollectionNewFornecedor)) {
                    Cidade oldCidadeIdOfFornecedorCollectionNewFornecedor = fornecedorCollectionNewFornecedor.getCidadeId();
                    fornecedorCollectionNewFornecedor.setCidadeId(cidade);
                    fornecedorCollectionNewFornecedor = em.merge(fornecedorCollectionNewFornecedor);
                    if (oldCidadeIdOfFornecedorCollectionNewFornecedor != null && !oldCidadeIdOfFornecedorCollectionNewFornecedor.equals(cidade)) {
                        oldCidadeIdOfFornecedorCollectionNewFornecedor.getFornecedorCollection().remove(fornecedorCollectionNewFornecedor);
                        oldCidadeIdOfFornecedorCollectionNewFornecedor = em.merge(oldCidadeIdOfFornecedorCollectionNewFornecedor);
                    }
                }
            }
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cidade.getId();
                if (findCidade(id) == null) {
                    throw new NonexistentEntityException("The cidade with id " + id + " no longer exists.");
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
            Cidade cidade;
            try {
                cidade = em.getReference(Cidade.class, id);
                cidade.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cidade with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Fornecedor> fornecedorCollectionOrphanCheck = cidade.getFornecedorCollection();
            for (Fornecedor fornecedorCollectionOrphanCheckFornecedor : fornecedorCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cidade (" + cidade + ") cannot be destroyed since the Fornecedor " + fornecedorCollectionOrphanCheckFornecedor + " in its fornecedorCollection field has a non-nullable cidadeId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Estado estadoId = cidade.getEstadoId();
            if (estadoId != null) {
                estadoId.getCidadeCollection().remove(cidade);
                estadoId = em.merge(estadoId);
            }
            em.remove(cidade);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cidade> findCidadeEntities() {
        return findCidadeEntities(true, -1, -1);
    }

    public List<Cidade> findCidadeEntitiesByEstadoId(int estadoId) {
        return findCidadeEntitiesByEstadoId(estadoId, true, -1, -1);
    }

    public List<Cidade> findCidadeEntities(int maxResults, int firstResult) {
        return findCidadeEntities(false, maxResults, firstResult);
    }

    private List<Cidade> findCidadeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cidade.class));
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

    public List<Cidade> findCidadeEntitiesByEstadoId(int estadoId, boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cidade.class));
            Query q = em.createQuery(cq);

            return q.getResultList();
        } finally {
            em.close();
        }

  //      EntityManager em = getEntityManager();
    //    try {
    //        TypedQuery<Cidade> query = em.createQuery("select p from Produto p where p.codigo = :codigo", Cidade.class);
    //        query.setParameter("id", estadoId);
     //       return List<Cidade> query.getMaxResults();
      //  } finally {
       //     em.close();
     //   }
    }

    public Cidade findCidade(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cidade.class, id);
        } finally {
            em.close();
        }
    }

    public int getCidadeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cidade> rt = cq.from(Cidade.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
