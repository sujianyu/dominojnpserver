package com.dominojnp.base;

public class JnpConfig {
	String jnpserver = "";
	String ip = "";
	int port = 0;
	String initial = "";
	String pkgs = "";
	String url = "";
	int lnum = 0;
	
	
	public String getJnpserver() {
		return jnpserver;
	}
	public void setJnpserver(String jnpserver) {
		this.jnpserver = jnpserver;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getInitial() {
		return initial;
	}
	public void setInitial(String initial) {
		this.initial = initial;
	}
	public String getPkgs() {
		return pkgs;
	}
	public void setPkgs(String pkgs) {
		this.pkgs = pkgs;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getLnum() {
		return lnum;
	}
	public void setLnum(int lnum) {
		this.lnum = lnum;
	}
	
	
}
