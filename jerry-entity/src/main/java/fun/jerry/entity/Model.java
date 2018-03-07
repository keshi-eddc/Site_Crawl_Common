package fun.jerry.entity;

import java.io.Serializable;

import fun.jerry.entity.annotation.FieldInsertExclude;
import fun.jerry.entity.annotation.FieldUpdateExclude;

/**
 * 该类和子类中的属性必须是基本数据类型的包装类，为了保证new出来的对象初始值为null
 * @author conner
 *
 */
public class Model implements Serializable {
	
	static final long serialVersionUID = -8357847346186989504L;
	
	/**
	 * 一般情况下作为物理主键，insert，update不用该字段
	 */
	@FieldInsertExclude
	@FieldUpdateExclude
	protected Long id;
	
	/** 保存方式， 1：直接保存到数据库；2：保存到Execute_Fail_Log表中 */
	@FieldInsertExclude
	@FieldUpdateExclude
	protected Integer saveMode = 1;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getSaveMode() {
		return saveMode;
	}

	public void setSaveMode(Integer saveMode) {
		this.saveMode = saveMode;
	}
	
}
