/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package JPA;

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
import model.Fornecedor;

/**
 *
 * @author Adriano
 */
@Stateless
public class CategoriaJpaController implements Serializable {
    @PersistenceUnit(unitName = "fornecedoresPU") //inject from your application server
    private EntityManagerFactory emf;
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }   
    
    public void create(Categoria categoria) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (categoria.getFornecedorCollection() == null) {
            categoria.setFornecedorCollection(new ArrayList<Fornecedor>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            Collection<Fornecedor> attachedFornecedorCollection = new ArrayList<Fornecedor>();
            for (Fornecedor fornecedorCollectionFornecedorToAttach : categoria.getFornecedorCollection()) {
                fornecedorCollectionFornecedorToAttach = em.getReference(fornecedorCollectionFornecedorToAttach.getClass(), fornecedorCollectionFornecedorToAttach.getId());
                attachedFornecedorCollection.add(fornecedorCollectionFornecedorToAttach);
            }
            categoria.setFornecedorCollection(attachedFornecedorCollection);
            em.persist(categoria);
            for (Fornecedor fornecedorCollectionFornecedor : categoria.getFornecedorCollection()) {
                fornecedorCollectionFornecedor.getCategoriaCollection().add(categoria);
                fornecedorCollectionFornecedor = em.merge(fornecedorCollectionFornecedor);
            }
        } catch (Exception ex) {
            if (findCategoria(categoria.getId()) != null) {
                throw new PreexistingEntityException("Categoria " + categoria + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Categoria categoria) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Categoria persistentCategoria = em.find(Categoria.class, categoria.getId());
            Collection<Fornecedor> fornecedorCollectionOld = persistentCategoria.getFornecedorCollection();
            Collection<Fornecedor> fornecedorCollectionNew = categoria.getFornecedorCollection();
            Collection<Fornecedor> attachedFornecedorCollectionNew = new ArrayList<Fornecedor>();
            for (Fornecedor fornecedorCollectionNewFornecedorToAttach : fornecedorCollectionNew) {
                fornecedorCollectionNewFornecedorToAttach = em.getReference(fornecedorCollectionNewFornecedorToAttach.getClass(), fornecedorCollectionNewFornecedorToAttach.getId());
                attachedFornecedorCollectionNew.add(fornecedorCollectionNewFornecedorToAttach);
            }
            fornecedorCollectionNew = attachedFornecedorCollectionNew;
            categoria.setFornecedorCollection(fornecedorCollectionNew);
            categoria = em.merge(categoria);
            for (Fornecedor fornecedorCollectionOldFornecedor : fornecedorCollectionOld) {
                if (!fornecedorCollectionNew.contains(fornecedorCollectionOldFornecedor)) {
                    fornecedorCollectionOldFornecedor.getCategoriaCollection().remove(categoria);
                    fornecedorCollectionOldFornecedor = em.merge(fornecedorCollectionOldFornecedor);
                }
            }
            for (Fornecedor fornecedorCollectionNewFornecedor : fornecedorCollectionNew) {
                if (!fornecedorCollectionOld.contains(fornecedorCollectionNewFornecedor)) {
                    fornecedorCollectionNewFornecedor.getCategoriaCollection().add(categoria);
                    fornecedorCollectionNewFornecedor = em.merge(fornecedorCollectionNewFornecedor);
                }
            }
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = categoria.getId();
                if (findCategoria(id) == null) {
                    throw new NonexistentEntityException("The categoria with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Categoria categoria;
            try {
                categoria = em.getReference(Categoria.class, id);
                categoria.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The categoria with id " + id + " no longer exists.", enfe);
            }
            Collection<Fornecedor> fornecedorCollection = categoria.getFornecedorCollection();
            for (Fornecedor fornecedorCollectionFornecedor : fornecedorCollection) {
                fornecedorCollectionFornecedor.getCategoriaCollection().remove(categoria);
                fornecedorCollectionFornecedor = em.merge(fornecedorCollectionFornecedor);
            }
            em.remove(categoria);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Categoria> findCategoriaEntities() {
        return findCategoriaEntities(true, -1, -1);
    }

    public List<Categoria> findCategoriaEntities(int maxResults, int firstResult) {
        return findCategoriaEntities(false, maxResults, firstResult);
    }

    private List<Categoria> findCategoriaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Categoria.class));
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

    public Categoria findCategoria(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Categoria.class, id);
        } finally {
            em.close();
        }
    }

    public int getCategoriaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Categoria> rt = cq.from(Categoria.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
