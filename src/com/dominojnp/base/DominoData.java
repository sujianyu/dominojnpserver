package com.dominojnp.base;

import java.io.IOException;
import java.util.HashMap;

//Domino 文档映射为POJO对象
public class DominoData implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String dataid = "";
	String field1 = "";
	String field2 = "";
	String field3 = "";
	String field4 = "";
	String field5 = "";
	String field6 = "";
	public String getDataid() {
		return dataid;
	}
	public void setDataid(String dataid) {
		this.dataid = dataid;
	}
	public String getField1() {
		return field1;
	}
	public void setField1(String field1) {
		this.field1 = field1;
	}
	public String getField2() {
		return field2;
	}
	public void setField2(String field2) {
		this.field2 = field2;
	}
	public String getField3() {
		return field3;
	}
	public void setField3(String field3) {
		this.field3 = field3;
	}
	public String getField4() {
		return field4;
	}
	public void setField4(String field4) {
		this.field4 = field4;
	}
	public String getField5() {
		return field5;
	}
	public void setField5(String field5) {
		this.field5 = field5;
	}
	public String getField6() {
		return field6;
	}
	public void setField6(String field6) {
		this.field6 = field6;
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException{
		out.writeObject(dataid);
		out.writeObject(field1);
		out.writeObject(field2);
		out.writeObject(field3);
		out.writeObject(field4);
		out.writeObject(field5);
		out.writeObject(field6);
	}
	private void readObject(java.io.ObjectInputStream in) throws IOException,ClassNotFoundException {
		dataid = (String)in.readObject();
		field1 = (String)in.readObject();
		field2 = (String)in.readObject();
		field3 = (String)in.readObject();
		field4 = (String)in.readObject();
		field5 = (String)in.readObject();
		field6 = (String)in.readObject();
	}
	public String getValue(){
		return dataid;
	}
	
}
