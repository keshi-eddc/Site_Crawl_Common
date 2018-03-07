package fun.jerry.common;

import org.apache.log4j.Logger;

public class LogSupport {

	private static final Logger autohomeLog;

	private static final Logger pcautoLog;

	private static final Logger bitautoLog;

	private static final Logger xcarLog;

	private static final Logger jdLog;

	private static final Logger yhdLog;

	private static final Logger httpLog;

	private static final Logger wccLog;
	
	private static final Logger offtakeLog;

	private static final Logger dianPingLog;

	static {
		autohomeLog = Logger.getLogger("autohome.log");
		pcautoLog = Logger.getLogger("pcauto.log");
		bitautoLog = Logger.getLogger("bitauto.log");
		xcarLog = Logger.getLogger("xcar.log");
		jdLog = Logger.getLogger("jd.log");
		yhdLog = Logger.getLogger("yhd.log");
		httpLog = Logger.getLogger("http.log");
		wccLog = Logger.getLogger("wcc.log");
		offtakeLog = Logger.getLogger("offtake.log");
		dianPingLog = Logger.getLogger("dianping.log");
	}

	public static Logger getAutohomelog() {
		return autohomeLog;
	}

	public static Logger getPcautolog() {
		return pcautoLog;
	}

	public static Logger getHttplog() {
		return httpLog;
	}

	public static Logger getBitautolog() {
		return bitautoLog;
	}

	public static Logger getJdlog() {
		return jdLog;
	}

	public static Logger getYhdlog() {
		return yhdLog;
	}

	public static Logger getXcarlog() {
		return xcarLog;
	}

	public static Logger getWcclog() {
		return wccLog;
	}

	public static Logger getOfftakelog() {
		return offtakeLog;
	}

	public static Logger getDianpinglog() {
		return dianPingLog;
	}

}
