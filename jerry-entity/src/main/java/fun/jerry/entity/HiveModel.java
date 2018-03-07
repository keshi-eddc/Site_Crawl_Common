package fun.jerry.entity;

import fun.jerry.entity.annotation.FieldInsertExclude;
import fun.jerry.entity.annotation.FieldUpdateExclude;

/**
 * 将数据放到Hive中时使用
 * @author conner
 *
 */
public class HiveModel extends Model {
	
	static final long serialVersionUID = -8357847346186989504L;
	
	@FieldInsertExclude
	@FieldUpdateExclude
	protected String site;
	
	@FieldInsertExclude
	@FieldUpdateExclude
	protected String ym;

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getYm() {
		return ym;
	}

	public void setYm(String ym) {
		this.ym = ym;
	}
	
	
	
}
