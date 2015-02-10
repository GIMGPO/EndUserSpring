package trisoftdp.web.db;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import trisoftdp.core.DynException;
import trisoftdp.web.db.TriSoftDb;


/**
 * 
 * @author shadrn1
 *
 */
public class TriSoftDbHelper implements TriSoftDb {

	private static final SessionFactory sessionFactory = buildSessionFactory();

	public TriSoftDbHelper() {}
	
	private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();
            //ServiceRegistry  serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            //SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            SessionFactory sessionFactory = configuration.buildSessionFactory();
            return sessionFactory;
        }
        catch (Throwable e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e.getMessage());
        }
    }

    private static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
	public long[] getAllResultIds() {
		Session session = null;
		Transaction tx = null; 
		long[] resultIds = null;
		try {
			session = getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			SQLQuery query = session.createSQLQuery("SELECT distinct result_id FROM job_results");
			query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			@SuppressWarnings("unchecked")
			List<Map<String,Long>> results = query.list();
			resultIds = new long[results.size()];
			for(int i = 0; i < resultIds.length; i++)
				resultIds[i] = results.get(i).get("result_id");
			tx.commit();
		} catch(Exception e) {
			if(tx != null && tx.isActive()) tx.rollback();
			e.printStackTrace();
		} finally {
			if(session != null && session.isOpen()) try {session.close();} catch(SessionException e) { e.printStackTrace(); }
		}
		return resultIds;
	}

	public long[] getMarkedResultIds(String mark)  {
		Session session = null;
		Transaction tx = null; 
		long[] resultIds = null;
		try {
			session = getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			SQLQuery query = session.createSQLQuery("SELECT distinct result_id FROM marked_jobs WHERE mark=:mark");
			query.setParameter("mark", mark);
			query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			@SuppressWarnings("unchecked")
			List<Map<String,BigInteger>> results = query.list();
			resultIds = new long[results.size()];
			for(int i = 0; i < resultIds.length; i++)
				resultIds[i] = results.get(i).get("result_id").longValue();
			tx.commit();
		} catch(Exception e) {
			if(tx != null && tx.isActive()) tx.rollback();
			e.printStackTrace();
		} finally {
			if(session != null && session.isOpen()) try {session.close();} catch(SessionException e) { e.printStackTrace(); }
		}
		return resultIds;
	}

	private void saveResult(long resultId, Serializable request, JobResult.JOB_STATUS status, String note) throws SQLException {
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();
		JobResult jobResult = null;
		try {
			jobResult = JobResult.makeJobResult(request, status, resultId, note);
			assert(jobResult != null);
			System.out.println("In saveResult() cdate = " + jobResult.getCDate());
			session.save(jobResult);
			session.getTransaction().commit();
		} catch (DynException e) {
			throw new SQLException("DynException: " + e);
		} finally {
			if(session != null && session.isOpen()) try {session.close();} catch(SessionException e) {
				throw new SQLException("SessionException: " + e.getMessage());
			}
		}
	}
	
	public void saveResult(long resultId, String md5, Serializable request,	File result) throws SQLException {
		//md5, result not used
		saveResult(resultId, request, JobResult.JOB_STATUS.SUCCEDED, null /* note */);
	}

	public void addFailedJob(String note, String md5, Serializable request)	throws SQLException {
		//md5 not used
			saveResult(-1/* resultId */, request, JobResult.JOB_STATUS.FAILED, note);
	}

	public void markRecord(long resultId, String mark) throws SQLException {
		Session session = null;
		Transaction tx = null;
		try {
			session = getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			JobResult jobResult = (JobResult) session.get(JobResult.class, resultId);
			JobMark jm = new JobMark(jobResult, "mark");
			session.save(jm);
			session.getTransaction().commit();
		} catch (Exception e) {
			if (tx != null && tx.isActive())	tx.rollback();
			throw new SQLException("Exception: " + e.getMessage());
		} finally {
			if (session != null && session.isOpen())
				try {session.close();} catch (SessionException e) {	e.printStackTrace(); }
		}
		
	}

	public void markRecord(JobResult jobResult, String mark) throws SQLException {
		Session session = null;
		Transaction tx = null;
		try {
			session = getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			JobMark jm = new JobMark(jobResult, "mark");
			session.save(jm);
			session.getTransaction().commit();
		} catch (Exception e) {
			if (tx != null && tx.isActive())	tx.rollback();
			throw new SQLException("Exception: " + e.getMessage());
		} finally {
			if (session != null && session.isOpen())
				try {session.close();} catch (SessionException e) {	e.printStackTrace(); }
		}
		
	}
	
	
	public void updateJobResult(JobResult jr) throws SQLException { 
		Session session = null;
		Transaction tx = null; 
		try {
			session = getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			session.saveOrUpdate(jr);
			tx.commit();
		} catch(Exception e) {
			if(tx != null && tx.isActive()) tx.rollback();
			throw new SQLException("Exception: " + e.getMessage());
		} finally {
			if(session != null && session.isOpen()) try {session.close();} catch(SessionException e) {
				throw new SQLException("SessionException: " + e.getMessage());
			}
		}
	}
	
	public long getResultId(String md5) throws SQLException {
		long resultId = -1L;
		Session session = null;
		Transaction tx = null; 
		try {
			session = getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			SQLQuery query = session.createSQLQuery("SELECT result_id FROM job_results WHERE  md5=:md5");
			query.setParameter("md5", md5);
			query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			@SuppressWarnings("unchecked")
			List<Map<String,BigInteger>> results = query.list();
			if(results.size() == 1) {
				resultId = results.get(0).get("result_id").longValue();
			}
			tx.commit();
		} catch(Exception e) {
			if(tx != null && tx.isActive()) tx.rollback();
			throw new SQLException("Exception: " + e.getMessage());
		} finally {
			if(session != null && session.isOpen()) try {session.close();} catch(SessionException e) {
				e.printStackTrace();
			}
		}
		return resultId;
	}
	
	
	public Serializable getRequest(long resultId) throws SQLException {
		Session session = null;
		Transaction tx = null; 
		Serializable request = null;
		try {
			session = getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			SQLQuery query = session.createSQLQuery("SELECT * FROM job_results WHERE  result_id=" + resultId);			
			query.addEntity(JobResult.class);
			@SuppressWarnings("unchecked")
			List<JobResult> results = query.list();
			System.out.format("results.size()=%d%n", results.size());
			if(results.size() != 1) 
				throw new SQLException("results.size()= " + results.size() + "  != 1 for result_id=" + resultId);
			ObjectInputStream objectIn =  new ObjectInputStream(results.get(0).getRequestObj().getBinaryStream());
			Object obj = null;
			obj = objectIn.readObject();
			request = (Serializable)obj;
			tx.commit();
		} catch(Exception e) {
			if(tx != null && tx.isActive()) tx.rollback();
			throw new SQLException("Exception: " + e.getMessage());
		} finally {
			if(session != null && session.isOpen()) try {session.close();} catch(SessionException e) {
				e.printStackTrace();
			}
		}
		return request;
	}

	public JobResult getJobResult(long reusltId) throws SQLException { 
		Session session = null;
		Transaction tx = null; 
		JobResult jr = null;
		try {
			session = getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			jr = (JobResult) session.get(JobResult.class, reusltId);
			tx.commit();
		} catch(Exception e) {
			if(tx != null && tx.isActive()) tx.rollback();
			throw new SQLException("Exception: " + e.getMessage());
		} finally {
			if(session != null && session.isOpen()) try {session.close();} catch(SessionException e) {	e.printStackTrace();}
		}
		return jr;
	}
	
	public JobMark getJobMark(long id) throws SQLException { 
		Session session = null;
		Transaction tx = null; 
		JobMark jm = null;
		try {
			session = getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			jm = (JobMark) session.get(JobMark.class, id);
			tx.commit();
		} catch(Exception e) {
			if(tx != null && tx.isActive()) tx.rollback();
			throw new SQLException("Exception: " + e.getMessage());
		} finally {
			if(session != null && session.isOpen()) try {session.close();} catch(SessionException e) {	e.printStackTrace();}
		}
		return jm;
	}
	
	public void saveJobMark(JobMark jm) throws SQLException { 
		Session session = null;
		Transaction tx = null; 
		try {
			session = getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			session.saveOrUpdate(jm);
			tx.commit();
		} catch(Exception e) {
			if(tx != null && tx.isActive()) tx.rollback();
			throw new SQLException("Exception: " + e.getMessage());
		} finally {
			if(session != null && session.isOpen()) try {session.close();} catch(SessionException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void getResultToFile(long resultId, File result) throws SQLException {
		// TODO Auto-generated method stub
		throw new SQLException("getResultToFile to be implemented");
		
	}

	public void getResultToStream(long resultId, OutputStream out) throws SQLException {
		// TODO Auto-generated method stub
		throw new SQLException("getResultToFile to be implemented");
	}

	public void close() throws SQLException {
		
	}

	public void startTransaction() throws SQLException {
		//do nothing
	}

	public void rollback() throws SQLException {
		//do nothing
		
	}

	public void commitTransaction() throws SQLException {
		//do nothing
		
	}

	public int selectPubResults(String tableName) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int cleanPubResults(String exemptMark) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int cleanPubResults() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int deletePubResultsEntry(long resId) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

}
