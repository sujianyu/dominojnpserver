/**
 * ͨ�ú���
 * ���ߣ��ս���
 * ���ڣ�2009-05-17
 * @version1.0
 */
package com.dominojnp.base;
import java.util.Vector;
import lotus.domino.*;

public class util {
	//���ո�תΪnull
	public static String BlankToNull(String strValue){

		return (strValue!=null && strValue.equals(""))?null:strValue;
	}
	//��Vector ת��ΪString������separator��Ϊ�ָ���
	public static String vector2String(Vector<String> vfldValue,String separator){
		String strReturn = "";
		for(String element:vfldValue){
			if(strReturn.length()==0){
				strReturn = element;
			}else{
				strReturn += separator + element;
			}
		}
		return strReturn;
	}
	
	public static void recyclenotes(Base object) {
		try {
			object.recycle();
		} catch (Exception e) {

		}
	}
	
}
