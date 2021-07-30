/**
 * 通用函数
 * 作者：苏建宇
 * 日期：2009-05-17
 * @version1.0
 */
package com.dominojnp.base;
import java.util.Vector;
import lotus.domino.*;

public class util {
	//将空格转为null
	public static String BlankToNull(String strValue){

		return (strValue!=null && strValue.equals(""))?null:strValue;
	}
	//将Vector 转化为String，并以separator作为分隔符
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
