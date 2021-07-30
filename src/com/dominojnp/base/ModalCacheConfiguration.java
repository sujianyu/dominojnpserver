/**
 * DomCacheV1.0的配置数据映射类
 * 作者：苏建宇
 * 日期：2009-05-10
 * @version1.0
 */
package com.dominojnp.base;
import java.util.ArrayList;
public class ModalCacheConfiguration {
	private String query = null;
	private String dbname = null;
	private String classname = null;
	private String domain = null;
	private String primarykey = null;
	private String[] aryfldLst = null;
	private String[] aryflddatatype = null;
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getDbname() {
		return dbname;
	}
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	public String[] getAryfldLst() {
		return aryfldLst;
	}
	public void setAryfldLst(String[] aryfldLst) {
		this.aryfldLst = aryfldLst;
	}
	public String[] getAryflddatatype() {
		return aryflddatatype;
	}
	public void setAryflddatatype(String[] aryflddatatype) {
		this.aryflddatatype = aryflddatatype;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getPrimarykey() {
		return primarykey;
	}
	public void setPrimarykey(String primarykey) {
		this.primarykey = primarykey;
	}
}