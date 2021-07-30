/**Domino Jndi V2.0
 * 作者：苏建宇
 * 修改历史：
 * 2009-05-17 创建。
 * 2015年，JnpServer升级到5.05。
 * 
 *
 */
import lotus.domino.*;
import lotus.notes.addins.JavaServerAddin;
import lotus.notes.internal.MessageQueue;
import java.util.*;
import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import org.jnp.server.Main;
import org.jnp.server.NamingBeanImpl;

import com.dominojnp.base.*;

public class dominoCache extends JavaServerAddin {
	/* global variables */
	// the name of this program
	String progName = new String("dominoCache");
	// the "friendly" name for this Addin
	String addinName = new String("dominoCache");
	// Message Queue name for this Addin (normally uppercase);
	// MSG_Q_PREFIX is defined in JavaServerAddin.class
	String qName = new String(MSG_Q_PREFIX + "dominoCache");
	// MessageQueue Constants
	public static final int MQ_MAX_MSGSIZE = 256;
	// this is already defined (should be = 1):
	public static final int MQ_WAIT_FOR_MSG = MessageQueue.MQ_WAIT_FOR_MSG;
	// MessageQueue errors:
	public static final int PKG_MISC = 0x0400;
	public static final int ERR_MQ_POOLFULL = PKG_MISC + 94;
	public static final int ERR_MQ_TIMEOUT = PKG_MISC + 95;
	public static final int ERR_MQSCAN_ABORT = PKG_MISC + 96;
	public static final int ERR_DUPLICATE_MQ = PKG_MISC + 97;
	public static final int ERR_NO_SUCH_MQ = PKG_MISC + 98;
	public static final int ERR_MQ_EXCEEDED_QUOTA = PKG_MISC + 99;
	public static final int ERR_MQ_EMPTY = PKG_MISC + 100;
	public static final int ERR_MQ_BFR_TOO_SMALL = PKG_MISC + 101;
	public static final int ERR_MQ_QUITTING = PKG_MISC + 102;
	// Domino Object Definition
	Session session = null;
	private NamingBeanImpl namingBean;
	private Main namingMain;
	

	
	public void dominoCache() {
		setName(progName);
	}
	public void dominoCache(String[] args) {
		setName(progName);
		// do something with the args that were passed...
		if (args.length > 0) {
			// whatever...
		}
	}
	/* the runNotes method, which is the main loop of the Addin */
	public void runNotes() {
		int taskID;
		MessageQueue mq;
		Database dbCustom = null;
		Database dbconf = null;
		ArrayList aryIDLst = new ArrayList();
		StringBuffer qBuffer = new StringBuffer();
		String strbindname = null;
		JnpConfig jnpconfig = null;
		InitialContext ctx;
		int mqError;
		Main jnpserver = null;
		
		//jnpserver 默认参数
		String sip = "";
		String sjnpserver = "";
		int iport = 0;
		
		try {

			// Properties env = new Properties();
			// env.load(new FileInputStream("jndi.properties"));
			System.out.println("starting DominoJnpserver...");
			if (namingBean == null){
				namingBean = new NamingBeanImpl();
				
				dbconf = getConfDb();
				if (!dbconf.isOpen()) {
					System.out.println("没有发现DominoJnpServer配置数据库.");
					return;
				}
				
				try { 
					System.out.println("启动JnpServer...");
		            namingBean.start();

					jnpconfig = loadjnpconfig(dbconf);
					if (jnpconfig == null){
						sjnpserver = "org.jnp.server";
						sip = "127.0.0.1";
						iport = 1099;
					}else{	
						sjnpserver = jnpconfig.getJnpserver();
						if (sjnpserver.equals("")){
							sjnpserver = "org.jnp.server";
						}
						sip = jnpconfig.getIp();
						if (sip.equals("")){
							sip = "127.0.0.1";
						}
						iport = jnpconfig.getPort();
						if (iport == 0 ){
							iport = 1099;
						}
					}
		           
		            namingMain = new Main(sjnpserver);  
		            namingMain.setNamingInfo(namingBean);  
		            namingMain.setPort(iport);// 
		            namingMain.setBindAddress(sip);//为本机IP  
		            namingMain.start();  
		        } catch (Exception e) {  
		            throw new RuntimeException("启动JnpServer服务失败!",e.getCause());  
		        }  
			}


			ModalCacheConfiguration modalcachecfg = loadModalCachecfg(dbconf);
			if (modalcachecfg != null){
				dbCustom = getCustomDb(modalcachecfg.getDbname());
				
				if(!dbCustom.isOpen()){
					System.out.println("没有找到" + modalcachecfg.getDbname()+ "数据库.");
					return;
				}
				ctx = new InitialContext();
				aryIDLst = bind(ctx,dbCustom,modalcachecfg);
				
			}else{
				System.out.println("没有找到DominoCache的配置信息.");
				return;
			}
			// set the text to be displayed if a user issues a SHOW STAT
			// or SHOW TASKS command (normally 20 characters or less for
			// the CreateStatusLine descriptor, 80 characters or less for
			// SetStatusLine, although larger strings can be used). You
			// can have multiple StatusLines that all display separately
			// by making multiple calls to AddInCreateStatusLine, and
			// keeping track of the different task IDs. Make sure you
			// deallocate the memory for the StatusLines at the exit point
			// of your program by calling AddInDeleteStatusLine for each
			// of the StatusLines that you use.
			taskID = AddInCreateStatusLine(addinName);
			AddInSetStatusLine(taskID, "Initialization in progress...");

			// set up the message queue (make sure the queue gets closed
			// when the program exits, with a call to mq.close). Note that
			// you are not required to use a MessageQueue if you don't need
			// one, because if someone tells your Addin to "Quit", then that
			// condition should be handled automatically by the JavaServerAddin
			// class. However, if you want your Addin to respond to custom
			// commands (like "Tell MyAddin Explode", or whatever), you have
			// to maintain a MessageQueue.
			mq = new MessageQueue();
			mqError = mq.create(qName, 0, 0); // use like MQCreate in API

			if (mqError != NOERROR) {
				// if there was an error creating the MessageQueue, just exit
				// (this could just mean that there's already an instance of
				// this Addin loaded)
				consolePrint("Error creating the Message Queue. Exiting...");
				doCleanUp(taskID, mq);
				return;
			}
			// if we got here, we must be running
			AddInSetStatusLine(taskID, "Idle");
			// open the MessageQueue, and wait for instructions
			mqError = mq.open(qName, 0); // use like MQOpen in API
			if (mqError != NOERROR) {
				// if we can't open the MessageQueue, we should exit
				consolePrint("Error opening the Message Queue. Exiting...");
				doCleanUp(taskID, mq);
				return;
			}

			while ((addInRunning()) && (mqError != ERR_MQ_QUITTING)) {
				// in case this is a non-preemptive operating system...
				OSPreemptOccasionally();

				// wait half a second (500 milliseconds) for a message,
				// then check for other conditions -- use 0 as the last
				// parameter to wait forever. You can use a longer interval
				// if you're not checking for any of the AddInElapsed
				// conditions -- otherwise you should keep the timeout to
				// a second or less (see comments below)
				mqError = mq.get(qBuffer, MQ_MAX_MSGSIZE, MQ_WAIT_FOR_MSG, 500);

				if (mqError == NOERROR) {
					// if we got a message in the queue, process it
					AddInSetStatusLine(taskID, "Processing Command");
					processMsg(qBuffer);
					AddInSetStatusLine(taskID, "Idle");
				}
				// display messages every once in a while.
				// NOTE: the AddInMinutesHaveElapsed and AddInSecondsHaveElapsed
				// functions seem to depend on a rigid calculation to return
				// a "true" value, so if you are waiting for a message with
				// a MessageQueue.get call, keep the timeout value short
				// (preferably a second or less). Otherwise, you can run
				// into a situation where you want to do something every
				// 30 seconds, but the timeout in the mq.get call only lets
				// you check the AddInSecondsHaveElapsed return value at 29
				// seconds or 31 seconds or something, and then the call will
				// return "false" even if you haven't done anything in over
				// 30 seconds. It will only return "true" at EXACTLY 30 seconds.
				// Likewise, the AddInMinutesHaveElapsed is just a macro that
				// calls AddInSecondsHaveElapsed(60), so it has the same
				// behavior (it only returns "true" in EXACT 60 second
				// intervals).
				if (AddInDayHasElapsed()) {
					// AddInSetStatusLine(taskID, "Doing Daily Stuff");
					// consolePrint(progName + ": Another day has passed...");
					AddInSetStatusLine(taskID, "Idle");
				} else if (AddInHasMinutesElapsed(3)) {
					// AddInSetStatusLine(taskID, "Doing 10-Minute Stuff");
					// consolePrint(progName + ": 3 more minutes have gone
					// by...");
					// AddInSetStatusLine(taskID, "Idle");
				} else if (AddInHasSecondsElapsed(30)) {
					// AddInSetStatusLine(taskID, "Doing 30-Second Stuff");
					// consolePrint(progName + ": 30 seconds, and all is
					// well...");
					// AddInSetStatusLine(taskID, "Idle");
				}
			}
			// unbind NamingService
			System.out.println("正在停止DominoCache...");
			unbind(aryIDLst,ctx,modalcachecfg);
			// Destory DataSource
			// System.out.println("Destorying PooledDataSource...");
			// DataSources.destroy(pooled);
			
			namingMain.stop();
			// once we've exited the loop, the task is supposed to terminate,
			// so we should clean up
			doCleanUp(taskID, mq);
			System.out.println("DominoCache服务已经停止。");

		} catch (Exception ne) {
			ne.printStackTrace();
		} finally {
			try {
				if(dbCustom != null){
					dbCustom.recycle();
				}
				if (dbconf != null) {
					dbconf.recycle();
				}
				if (session != null) {
					session.recycle();
				}
			} catch (Exception e) {
			}
		}
	}
	/*
	 * the consolePrint method, which is a tiny wrapper around the
	 * AddInLogMessageText method (because AddInLogMessageText requires a second
	 * parameter of 0, and I always forget to type it)
	 */
	private void consolePrint(String msg) {
		AddInLogMessageText(msg, 0);
	}
	/*
	 * the doCleanUp method, which performs all the tasks we should do when the
	 * Addin terminates
	 */
	private void doCleanUp(int taskID, MessageQueue mq) {
		try {
			AddInSetStatusLine(taskID, "Terminating...");
			consolePrint("Stopping " + addinName + "...");
			AddInDeleteStatusLine(taskID);
			mq.close(0);
			consolePrint(addinName + " has terminated.");
		} catch (Exception e) {
		}
	}
	/*
	 * the processMsg method, which translates and reacts to user commands, like
	 * "TELL JavaAddinTest THIS THAT" (where "THIS" and "THAT" are the messages
	 * we'll see in the queue)
	 */
	private int processMsg(StringBuffer qBuffer) {
		StringTokenizer st;
		String token;
		int tokenCount;

		st = new StringTokenizer(qBuffer.toString());
		tokenCount = st.countTokens();
		// do a quick error check
		if (tokenCount == 0) {
			displayHelp();
			return -1;
		}
		// get the first token, and check it against our known list of arguments
		token = st.nextToken();
		// ? or HELP should display a help screen
		if ((token.equalsIgnoreCase("?")) || (token.equalsIgnoreCase("HELP"))) {
			displayHelp();
			return 0;
		}
		// VER should display the version of Notes we're running
		if (token.equalsIgnoreCase("VER")) {
			return displayNotesVersion();
		}
		// QUIT and EXIT will stop the Addin
		if ((token.equalsIgnoreCase("QUIT")) || (token.equalsIgnoreCase("EXIT"))) {
			// automatically handled by the system
			return 0;
		}
		// if we got here, the user gave us an unknown argument, so we should
		// just display the help screen
		consolePrint("Unknown argument for " + addinName + ": " + token);
		displayHelp();
		return -1;
	}
	/* the displayHelp method simply shows a little Help screen on the console */
	private void displayHelp() {
		consolePrint(addinName + " Usage:");
		consolePrint("Tell " + progName + " HELP  -- displays this help screen");
		consolePrint("Tell " + progName + " VER  -- displays the Notes version of this server");
		consolePrint("Tell " + progName + " QUIT  -- terminates this addin");
	}
	/*
	 * the displayNotesVersion method, which just prints the Notes version that
	 * we're running
	 */
	private int displayNotesVersion() {
		int retVal = 0;
		Session session = null;
		try {
			session = NotesFactory.createSession();
			String ver = session.getNotesVersion();
			consolePrint(progName + " - Domino version: " + ver);
		} catch (NotesException e) {
			consolePrint(progName + " - Notes Error getting Notes version: " + e.id + " " + e.text);
			retVal = -1;
		} catch (Exception e) {
			consolePrint(progName + " - Java Error getting Notes version: " + e.getMessage());
			retVal = -1;
		}
		// keep the memory clean
		try {
			session.recycle();
		} catch (Exception e) {
		}
		return retVal;
	}
	
	private JnpConfig loadjnpconfig(Database dbconf){
		JnpConfig jnpconfig = null;
		View vwconfig = null;
		Document docconfig = null;
		String svalue = "";
		try{
			vwconfig = dbconf.getView("jnpserver");
			docconfig = vwconfig.getFirstDocument();
			if (docconfig != null){
				jnpconfig = new JnpConfig();
				svalue = docconfig.getItemValueString("jnpserver");
				jnpconfig.setJnpserver(svalue);
				
				svalue = docconfig.getItemValueString("IP");
				jnpconfig.setIp(svalue);
				
				svalue = docconfig.getItemValueString("port");
				if (svalue.equals("")){
					jnpconfig.setPort(0);	
				}else{
					jnpconfig.setPort(Integer.parseInt(svalue));
				}
				
				svalue = docconfig.getItemValueString("initial");
				jnpconfig.setInitial(svalue);
				
				svalue = docconfig.getItemValueString("pkgs");
				jnpconfig.setPkgs(svalue);
				
				svalue = docconfig.getItemValueString("url");
				jnpconfig.setUrl(svalue);
			}
			
			return jnpconfig;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			try{
				if(docconfig != null){
					docconfig.recycle();
				}
				if (vwconfig != null){
					vwconfig.recycle();
				}
			}catch(Exception e){
				
			}
		}
		
	}
	//绑定数据
	private ArrayList bind(InitialContext ctx,Database dbCustom, ModalCacheConfiguration modalcachecfg) throws Exception,NamingException{
		ArrayList aryIDLst = new ArrayList();
		DocumentCollection dcquery = null;
		customObjectInterface customobject = null;
		Document docquery = null;
		Document doctorecycle = null;
		String  strquery = null;
		String strprikeyvalue = null;
		String strbindname = null;
		
		try{
			if (modalcachecfg != null) {
				strquery = modalcachecfg.getQuery().trim();
				System.out.println("装入类：" + modalcachecfg.getClassname());
				if(strquery != null || strquery.length()>0){
					dcquery = dbCustom.search(strquery);
					docquery = dcquery.getFirstDocument();
					while(docquery != null){
						if(docquery.hasItem(modalcachecfg.getPrimarykey())){
							strprikeyvalue = docquery.getItemValueString(modalcachecfg.getPrimarykey()).trim();
							if(strprikeyvalue != null && strprikeyvalue.length()> 0){
								strbindname = modalcachecfg.getDomain() + "\\" + strprikeyvalue;
								System.out.println("正在缓存：" + strbindname);
								customobject = (customObjectInterface)Class.forName(modalcachecfg.getClassname()).newInstance();
								customobject.init(docquery,modalcachecfg);
								try{
									ctx.bind(strbindname, customobject);
									aryIDLst.add(strprikeyvalue);
								}catch(NameAlreadyBoundException nae){
									//continue;
									System.out.println(strbindname + "有重复数据.");
								}
							}
						}else{
							System.out.println("搜索结果文档中没有发现主键字段.");
							aryIDLst = null;
						}
						doctorecycle = docquery;
						docquery = dcquery.getNextDocument();
						doctorecycle.recycle();
					}
					System.out.println("完成目标数据的缓存.一共缓存" + aryIDLst.size() + "个对象数据.");
				}
			}
		}catch(Exception e){
			aryIDLst = null;
			throw e;
		}finally{
			try{
				if (dcquery != null){
					dcquery.recycle();
				}
				if(doctorecycle != null){
					doctorecycle.recycle();
				}
				if(docquery != null){
					docquery.recycle();
				}
			}catch(Exception e){
			}
		}
		return aryIDLst;
	}
	private void unbind(ArrayList<String> aryIDLst,InitialContext ctx,ModalCacheConfiguration modalcachecfg)throws NamingException{
		for(String strid:aryIDLst){
			ctx.unbind(modalcachecfg.getDomain() + "\\" + strid);
		}
	}
	// 读取dominoCache 配置信息
	private ModalCacheConfiguration loadModalCachecfg(Database dbConfig) {
		ModalCacheConfiguration modalcache = null;
		View vwModalCache = null;
		Document docModalCache = null;
		String strdbname = null;
		String strquery = null;
		String strclassname = null;
		String strdomain = null;
		String strprimarykey = null;

		try {
			if (dbConfig.isOpen()) {
				vwModalCache = dbConfig.getView("vwCacheConf");
				docModalCache = vwModalCache.getFirstDocument();
				modalcache = new ModalCacheConfiguration();
				if (docModalCache != null){
					// 缓存的数据库
					strdbname = docModalCache.getItemValueString("dbname");
					modalcache.setDbname(strdbname);
					// 搜索语句
					strquery = docModalCache.getItemValueString("query");
					modalcache.setQuery(strquery);
					//作用域
					strdomain = docModalCache.getItemValueString("domain");
					modalcache.setDomain(strdomain);
					//主键字段
					strprimarykey = docModalCache.getItemValueString("primarykey");
					modalcache.setPrimarykey(strprimarykey);
					// 缓存字段
					Vector<String> vfldlst = docModalCache.getItemValue("fldlist");
					modalcache.setAryfldLst((String[]) vfldlst.toArray(new String[1]));
					// 缓存字段类型
					Vector vflddatatype = docModalCache.getItemValue("flddatatype");
					modalcache.setAryflddatatype((String[]) vflddatatype.toArray(new String[1]));
					// 缓存的类
					strclassname = docModalCache.getItemValueString("classname");
					modalcache.setClassname(strclassname);
				}else{
					modalcache = null;
				}
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
			modalcache = null;
		} finally {
			try {
				if (docModalCache != null) {
					docModalCache.recycle();
				}
				if (vwModalCache != null) {
					vwModalCache.recycle();
				}
			} catch (Exception e) {
			}
		}
		return modalcache;
	}
	//取目标数据库
	private Database getCustomDb(String strdbfullpath) {
		Database dbCustom = null;
		try {
			if (session == null){	
				session = NotesFactory.createSession();
			}
			dbCustom = session.getDatabase("", strdbfullpath);
		} catch (Exception e) {
			e.printStackTrace();
			dbCustom = null;
		} 
		return dbCustom;
	}
	//取domCache.nsf数据库
	private Database getConfDb() {
		Database dbconf = null;
		try {
			session = NotesFactory.createSession();
			dbconf = session.getDatabase("", "domcache.nsf");
		} catch (Exception e) {
			e.printStackTrace();
			dbconf = null;
		}
		return dbconf;
	}
	/**
	 * @param args
	 */
	/* the main method, which just kicks off the runNotes method */
	public static void main(String[] args) {
		// kick off the Addin from main -- you can also do something
		// here with optional program args, if you want to...
		dominoCache dcache = new dominoCache();
		dcache.start();
	}
}