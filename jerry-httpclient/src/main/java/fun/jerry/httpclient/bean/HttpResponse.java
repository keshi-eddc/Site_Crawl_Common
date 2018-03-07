package fun.jerry.httpclient.bean;

import java.io.Serializable;

/**
 * Http响应
 * @author rconne
 *
 */
public class HttpResponse implements Serializable {
	
	private static final long serialVersionUID = -6430146906816242811L;

	private String content;
	
	private Integer code;
	
	private String failReason;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}
	
}
