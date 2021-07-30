/**
 *本接口继承自Serializable接口，因此用户需要实现：
 *writeObject(java.io.ObjectOutputStream out)方法和
 *readObject(java.io.ObjectInputStream in)
 *@version1.0
 */
package com.dominojnp.base;
import lotus.domino.*;
public interface customObjectInterface extends java.io.Serializable {
	//根据目标文档，从文档中读取域值，写到接口的实现类中。
	public void init(Document doc,ModalCacheConfiguration modalcachecfg);
}
