/**
 *���ӿڼ̳���Serializable�ӿڣ�����û���Ҫʵ�֣�
 *writeObject(java.io.ObjectOutputStream out)������
 *readObject(java.io.ObjectInputStream in)
 *@version1.0
 */
package com.dominojnp.base;
import lotus.domino.*;
public interface customObjectInterface extends java.io.Serializable {
	//����Ŀ���ĵ������ĵ��ж�ȡ��ֵ��д���ӿڵ�ʵ�����С�
	public void init(Document doc,ModalCacheConfiguration modalcachecfg);
}
