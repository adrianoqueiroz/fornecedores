/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package JPA;

import JPA.exceptions.NonexistentEntityException;
import JPA.exceptions.RollbackFailureException;
import java.io.Serializable;
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
import model.Contato;
import model.Fornecedor;

/**
 *
 * @author Adriano
 */
@Stateless
public class ContatoJpaController implements Serializable {

    @PersistenceUnit(unitName = "fornecedoresPU") //inject from your application server
    private EntityManagerFactory emf;
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }  
    
    public void create(Contato contato) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Fornecedor fornecedorId = contato.getFornecedorId();
            if (fornecedorId != null) {
                fornecedorId = em.getReference(fornecedorId.getClass(), fornecedorId.getId());
                contato.setFornecedorId(fornecedorId);
            }
            em.persist(contato);
            if (fornecedorId != null) {
                fornecedorId.getContatoCollection().add(contato);
                fornecedorId = em.merge(fornecedorId);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Contato contato) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Contato persistentContato = em.find(Contato.class, contato.getId());
            Fornecedor fornecedorIdOld = persistentContato.getFornecedorId();
            Fornecedor fornecedorIdNew = contato.getFornecedorId();
            if (fornecedorIdNew != null) {
                fornecedorIdNew = em.getReference(fornecedorIdNew.getClass(), fornecedorIdNew.getId());
                contato.setFornecedorId(fornecedorIdNew);
            }
            contato = em.merge(contato);
            if (fornecedorIdOld != null && !fornecedorIdOld.equals(fornecedorIdNew)) {
                fornecedorIdOld.getContatoCollection().remove(contato);
                fornecedorIdOld = em.merge(fornecedorIdOld);
            }
            if (fornecedorIdNew != null && !fornecedorIdNew.equals(fornecedorIdOld)) {
                fornecedorIdNew.getContatoCollection().add(contato);
                fornecedorIdNew = em.merge(fornecedorIdNew);
            }
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = contato.getId();
                if (findContato(id) == null) {
                    throw new NonexistentEntityException("The contato with id " + id + " no longer exists.");
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
            Contato contato;
            try {
                contato = em.getReference(Contato.class, id);
                contato.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The contato with id " + id + " no longer exists.", enfe);
            }
            Fornecedor fornecedorId = contato.getFornecedorId();
            if (fornecedorId != null) {
                fornecedorId.getContatoCollection().remove(contato);
                fornecedorId = em.merge(fornecedorId);
            }
            em.remove(contato);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Contato> findContatoEntities() {
        return findContatoEntities(true, -1, -1);
    }

    public List<Contato> findContatoEntities(int maxResults, int firstResult) {
        return findContatoEntities(false, maxResults, firstResult);
    }

    private List<Contato> findContatoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Contato.class));
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

    public Contato findContato(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Contato.class, id);
        } finally {
            em.close();
        }
    }

    public int getContatoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Contato> rt = cq.from(Contato.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
