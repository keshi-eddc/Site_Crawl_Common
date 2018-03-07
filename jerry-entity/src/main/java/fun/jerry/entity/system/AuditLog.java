package fun.jerry.entity.system;

import fun.jerry.entity.Model;
import fun.jerry.entity.annotation.TableMapping;

/**
 * 日志记录
 * @author conner
 *
 */
@TableMapping("System_Audit_Log")
public class AuditLog extends Model {

	private static final long serialVersionUID = 6411307312808485316L;

	private String level;
	
	private String detail;

	public AuditLog() {
		super();
	}

	public AuditLog(String level, String detail) {
		super();
		this.level = level;
		this.detail = detail;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

}
