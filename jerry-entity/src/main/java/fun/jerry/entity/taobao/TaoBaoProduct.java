package fun.jerry.entity.taobao;

public class TaoBaoProduct {

	private String egoodsId;
	
	private String url;

	@Override
	public String toString() {
		return "TaoBaoProduct [egoodsId=" + egoodsId + ", url=" + url + "]";
	}

	public String getEgoodsId() {
		return egoodsId;
	}

	public void setEgoodsId(String egoodsId) {
		this.egoodsId = egoodsId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
