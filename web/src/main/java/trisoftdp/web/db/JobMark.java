package trisoftdp.web.db;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "marked_jobs")
public class JobMark {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Column(name = "mark")
	private String mark;
	
	@ManyToOne
	@JoinColumn(name="result_id")
	private JobResult jobResult;
	
	public JobMark() { super(); }
	
	public JobMark(JobResult jobResult, String mark) {
		this.jobResult = jobResult;
		this.mark = mark;
	}
	
	public void setId(long id) { this.id = id; }
	public void setMark(String mark) { this.mark = mark; }
	
	public long getId() { return id; }	
	public String getMark() { return mark; }
		
}
