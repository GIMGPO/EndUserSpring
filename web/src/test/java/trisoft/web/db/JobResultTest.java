package trisoft.web.db;

import static org.junit.Assert.*;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
//import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.Test;

import trisoftdp.core.DynException;
import trisoftdp.core.DynamicPublishingPackage;
import trisoftdp.web.db.JobMark;
import trisoftdp.web.db.JobResult;

public class JobResultTest {

	private static final SessionFactory sessionFactory = buildSessionFactory();


	private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();
            //ServiceRegistry  serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            //SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            SessionFactory sessionFactory = configuration.buildSessionFactory();
            return sessionFactory;
        }
        catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    @Test
	public void fake() { } 
    
    //@Test
    public void markRecordTest() {
		Session session = null;
		Transaction tx = null;
		long reusltId = 1393350642889L;
		try {
			session = getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			JobResult jr = (JobResult) session.get(JobResult.class, reusltId);
			JobMark jm = new JobMark(jr, "mark");
			session.save(jm);
			session.getTransaction().commit();
		} catch (Exception e) {
			if (tx != null && tx.isActive()) tx.rollback();
		} finally {
			if (session != null && session.isOpen())
				try {session.close();} catch (SessionException e) {	e.printStackTrace(); }
		}
    }
    
    //@Test
    public void getMarkedResultIdsTest() {
		Session session = null;
		Transaction tx = null; 
		long[] resultIds = null;
		String mark = "mark";
		try {
			session = getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			SQLQuery query = session.createSQLQuery("SELECT distinct result_id FROM marked_jobs WHERE mark=:mark");
			query.setParameter("mark", mark);
			query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			@SuppressWarnings("unchecked")
			List<Map<String,Long>> results = query.list();
			resultIds = new long[results.size()];
			for(int i = 0; i < resultIds.length; i++)
				resultIds[i] = results.get(i).get("result_id");
			tx.commit();
		} catch(Exception e) {
			if(tx != null && tx.isActive()) tx.rollback();
		} finally {
			if(session != null && session.isOpen()) try {session.close();} catch(SessionException e) { e.printStackTrace(); }
		}
		if(resultIds == null)
			fail("resultIds == null");
		else 
			for(long id: resultIds)
				System.out.format("result_id=%d%n", id);
    }
    
	//@Test
	public void saveJobMark() {
		Session session = null;
		Transaction tx = null;
		long reusltId = 1393350642889L;
		try {
			session = getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			JobResult jr = (JobResult) session.get(JobResult.class, reusltId);
			JobMark jm = new JobMark(jr, "mark");
			session.save(jm);
			session.getTransaction().commit();
		} catch (Exception e) {
			if (tx != null && tx.isActive()) tx.rollback();
		} finally {
			if (session != null && session.isOpen())
				try {session.close();} catch (SessionException e) {	e.printStackTrace(); }
		}
	}	
		
	//@Test
	public void saveJobResult() {
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();
		DynamicPublishingPackage request = new DynamicPublishingPackage();
		request.createDate =  new Date();
		JobResult jobResult = null;
		String note = null;
		try {
			jobResult = JobResult.makeJobResult(request, JobResult.JOB_STATUS.SUCCEDED, request.createDate.getTime(), note);
		} catch (DynException e) {
			e.printStackTrace();
		}
		assert(jobResult != null);
		session.save(jobResult);
		session.getTransaction().commit();
	}	
		
	
	//@Test
	public void getAllResultIdsTest() { 
		Session session = null;
		Transaction tx = null; 
		try {
			session = getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			SQLQuery query = session.createSQLQuery("SELECT distinct result_id  FROM job_results");
			query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			List<Map<String,Long>> results = query.list();
			System.out.format("results.size()=%d%n", results.size());
			for(Map<String,Long> map : results)
	            System.out.format("result_id=%d%n",map.get("result_id")); 
			tx.commit();
		} catch(Exception e) {
			if(tx != null && tx.isActive()) tx.rollback();
		} finally {
			if(session != null && session.isOpen()) try {session.close();} catch(SessionException e) {
				e.printStackTrace();
			}
		}
	}
	
	//@Test
	public void getJobResultByRequestID() {
		Session session = null;
		Transaction tx = null; 
		long  resultId = 1394128544346L;
		Serializable request = null;
		try {
			session = getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			//SQLQuery query = session.createSQLQuery("SELECT request_obj FROM job_results WHERE  result_id=" + resultId);			
			SQLQuery query = session.createSQLQuery("SELECT * FROM job_results WHERE  result_id=" + resultId);			
			//query.addEntity("request_obj", Blob.class);
			query.addEntity(JobResult.class);
			@SuppressWarnings("unchecked")
			List<JobResult> results = query.list();
			//List<Blob> results = query.list();
			System.out.format("results.size()=%d%n", results.size());
			if(results.size() != 1) 
				throw new SQLException("results.size()= " + results.size() + "  != 1 for result_id=" + resultId);
			ObjectInputStream objectIn =  new ObjectInputStream(results.get(0).getRequestObj().getBinaryStream());
			//ObjectInputStream objectIn =  new ObjectInputStream(results.get(0).getBinaryStream());
			Object obj = null;
			obj = objectIn.readObject();
			request = (Serializable)obj;
			System.out.println("request create date: " + ((DynamicPublishingPackage) request).createDate);
			tx.commit();
		} catch(Exception e) {
			if(tx != null && tx.isActive()) tx.rollback();
		} finally {
			if(session != null && session.isOpen()) try {session.close();} catch(SessionException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	//@Test
	public void getJobResult() {
		Session session = null;
		String md5 = "583a5ac3040e43ca6204d2e259c667b3";		
		Transaction tx = null; 
		JobResult jr = null;
		try {
			session = getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			jr = (JobResult) session.get(JobResult.class, md5);	
			if(jr != null)
				System.out.format("md5=%s cdate=%s status=%s note=<%s>%n", jr.getMd5(), jr.getCDate(), jr.getStatus(), jr.getNote());
			tx.commit();
		} catch(Exception e) {
			if(tx != null && tx.isActive()) tx.rollback();
		} finally {
			if(session != null && session.isOpen()) try {session.close();} catch(SessionException e) {
				System.err.println(e.getMessage());
			}
		}		
	}	
	
}
