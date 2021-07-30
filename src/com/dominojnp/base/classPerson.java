/**人员类
 * @author 苏建宇
 * @date 2009-05-17
 * @version 1.0
 */
package com.dominojnp.base;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import lotus.domino.*;
public class classPerson implements customObjectInterface{
	private HashMap<String,String> memberinfo = null;

	public void put(String strKeyName,String strKeyValue){
		memberinfo.put(strKeyName, strKeyValue);
	}
	public void init(Document docperson,ModalCacheConfiguration modalcachecfg) {
		String [] aryFldLst = null;
		String [] aryFldDataType = null;
		String strItemValue = null;
		Vector<String> vFld = null;
		memberinfo = new HashMap();
		aryFldLst = modalcachecfg.getAryfldLst();
		aryFldDataType = modalcachecfg.getAryflddatatype();
		for(int i=0; i<aryFldLst.length; i++){
			try{
				if(docperson.hasItem(aryFldLst[i])){
					
					if(aryFldDataType[i].equals("multitext")){
						vFld = docperson.getItemValue(aryFldLst[i]);
						strItemValue = util.vector2String(vFld, ";");
					}else if(aryFldDataType[i].equals("text")){
						strItemValue = docperson.getItemValueString(aryFldLst[i]);
					}
					put(aryFldLst[i],strItemValue);
				}
			}catch(NotesException ne){
				System.out.println("文档中不包含\"" + aryFldLst[i] + "\"这个域.");
				continue;
			}
		}
	}
	private void writeObject(java.io.ObjectOutputStream out) throws IOException{
		out.writeObject(memberinfo);
	}
	private void readObject(java.io.ObjectInputStream in) throws IOException,ClassNotFoundException {
		memberinfo = (HashMap)in.readObject();
	}
	public String getValue(String strfldname){
		return memberinfo.get(strfldname);
	}
}
