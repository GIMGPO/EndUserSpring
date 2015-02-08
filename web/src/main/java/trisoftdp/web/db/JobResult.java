package trisoftdp.web.db;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import trisoftdp.core.DynException;
import trisoftdp.core.ToolKit;


@Entity
@Table(name = "job_results")
public class JobResult {
	public enum JOB_STATUS {PENDING, STARTED, SUCCEDED, FAILED}
	
	@Id
	@Column(name = "result_id")
	private long resultId;
	
	@Column(name = "md5")
	private String md5;
	
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "cdate", nullable = false)
	private Date cDate;
	
	@Column(name = "status")
	@Enumerated(EnumType.ORDINAL)
	private JOB_STATUS status;
	
	@Column(name = "note")
	private String note;
	
	@Column(name = "request_obj")
	private Blob requestObj;
	
	@OneToMany(fetch=FetchType.EAGER, mappedBy="jobResult")
	private Set<JobMark> marks;

	private JobResult() { 
		super();
		cDate = new Date();
	} 
	
	public void setMd5(String md5) { this.md5 = md5; }
	
	public void setStatus(JOB_STATUS status) { this.status = status; }
	
	public void setResultId(long resultId) { this.resultId = resultId; }
	
	public void setRequestObj(Blob requestObj) { this.requestObj = requestObj; }
	
	public void setCDate(Date cDate) { this.cDate = cDate; }
	
	public void setNote(String note) { this.note = note; }
	
	public void setMarks(Set<JobMark> marks) { this.marks = marks; }
	
	
	public String getMd5() { return md5; }
	
	public JOB_STATUS getStatus() { return status; }
	
	public long getResultId() { return resultId; }
	
	public Blob getRequestObj() { return requestObj; }
	
	public Date getCDate() { return cDate; }
	
	public String getNote() { return note; }
	
	public Set<JobMark> getMarks() { return marks; }
	
	
	public static JobResult makeJobResult(Serializable request, JOB_STATUS status, long resultId, String note) throws DynException { 
		JobResult jr = new JobResult();
		jr.setStatus(status);
		jr.setResultId(resultId);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(request);
			byte[] obj = baos.toByteArray();
			oos.close();
			baos.close();
			Blob requestObj = new SerialBlob(obj);
			jr.setRequestObj(requestObj);
			jr.setMd5(ToolKit.getMD5(request));
		} catch (IOException e) {
			throw new DynException("IOException: " + e.getMessage());
		} catch (SerialException e) {
			throw new DynException("SerialException: " + e.getMessage());
		} catch (SQLException e) {
			throw new DynException("SQLException: " + e.getMessage());
		}
		return jr;
	}
}
