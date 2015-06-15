import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import redis.clients.jedis.Jedis;

public class Redis {
	
	//初始化日志对象
	Logger log = Logger.getLogger(Redis.class);
	{
	PropertyConfigurator.configure("d:/temp/log4j.properties"); 
	}
	//获得jedis连接
	Jedis jedis = GetJedis.getJedis();
	
	/**
	 * 无key存入 向redis存数组以map形式存,返回key存入失败返回-1
	 * @param String[][]
	 * @return key
	 */
	public String saveToRedis(String[][] ObjectMap) {
		log.info("执行saveToRedis方法");
		log.debug(ObjectMap.length+"saveToRedis方法中数组长度");
		String key = null;
		Lock lock = new ReentrantLock();
		try{
			lock.lock();
			key = new Date().getTime()+"";
		}finally{
			lock.unlock();
		}
		HashMap<String,String> map = new HashMap<String,String>();
		for(int i=0;i<ObjectMap.length;i++){
			log.info(ObjectMap[i][0]+"-------"+ObjectMap[i][1]);
			map.put(ObjectMap[i][0],ObjectMap[i][1]);
		}
		//GetRedisCluster redisCluster = new GetRedisCluster();
		try {
			System.out.println(System.currentTimeMillis());			
			jedis.hmset(key, map);
			System.out.println(System.currentTimeMillis());
		} catch (Exception e) {
			log.info("saveToRedis中的异常");
			e.printStackTrace();
			return "-1";
		}
		log.info(key+"++++++++++++key");
		return key;
	}
	
	/**
	 * 有key存入 向redis存数组以map形式存,返回key存入失败返回-1
	 * @param String[][] key
	 * @return key
	 */
	
	public String saveToRedisWithKey(String key,String[][] ObjectMap) {
		log.info("执行saveToRedisWithKey方法");
		HashMap<String,String> map = new HashMap<String,String>();
		//log.debug("saveToRedisWithKey方法中数组长度"+ObjectMap.length);
		for(int i=0;i<ObjectMap.length;i++){
			log.debug("传入的数组第"+(i+1)+"组key的值     "+ObjectMap[i][0]);
			log.debug("传入的数组第"+(i+1)+"组value的值 "+ObjectMap[i][1]);
			map.put(ObjectMap[i][0],ObjectMap[i][1]);		
		}
		try {
			//long startTime=System.currentTimeMillis();
			log.info("存入前时间"+System.currentTimeMillis());
			//redisCluster.getRedisCluster().hmset(key, map);
			jedis.hmset(key, map);
			log.info("存入后时间"+System.currentTimeMillis());
		} catch (Exception e) {
			log.info("saveToRedisWithKey中的异常");
			e.printStackTrace();
			return "-1";
		}
		log.info("返回的key值"+key);
		return key;
	}
	
	/**
	 * 用key从redis取数组 异常时返回[-1][-1]数组
	 * @param key
	 * @return String[][]
	 */
	public String[][] getFromRedis(String key) {
		log.info("执行getFromRedis方法");
		log.info("得到的key："+key);
		//GetRedisCluster redisCluster = new GetRedisCluster();		
		Map map = new HashMap();
		try {
			//map = (Map) redisCluster.getRedisCluster().hgetAll(key);
			log.info("操作前时间"+System.currentTimeMillis());
			//redisCluster.getRedisCluster().hmset(key, map);
			map = (Map)jedis.hgetAll(key);
			log.info("得到后时间"+System.currentTimeMillis());
		} catch (Exception e) {
			log.info("getFromRedis中的异常");
			e.printStackTrace();
			String[][] fail = {{"-1","-1"}};
			return fail;
		}
		Set<Map.Entry<String, String>> entrySet = map.entrySet(); 
		 
		Iterator<Map.Entry<String, String>> it = entrySet.iterator();
		//System.out.println(entrySet.size());
		String[][] ObjectMap = new String[entrySet.size()][2];
		int i = 0; 
		while(it.hasNext()){			    
		        Map.Entry<String, String> me = it.next();
		        String key1 = me.getKey();
		        String value = me.getValue();
		        ObjectMap[i][0] = key1;
		        ObjectMap[i][1] = value;
		        log.info("得到的第"+(i+1)+"组key: "+key1+"-->value: "+value);
		        i++;
		}	
		return ObjectMap;
	}
	/**
	 * 删除指定的key的map
	 * @param key
	 * @return String 1为删除成功 -1为发生异常 0表示该key不存在
	 */
	public String deleteFromRedis(String key){
		log.info("执行deleteFromRedis方法");
		String resultString = "begin";
		
		//GetRedisCluster redisCluster = new GetRedisCluster();
		//System.out.println(key+"deleteFromRedis方法得到的key");
		if(existsKey(key).equals("0")){
			resultString = "0";
			log.info("deleteFromRedis方法的返回值"+resultString);
			return resultString;
		}
		try {
			log.info("删除前时间"+System.currentTimeMillis());
			//redisCluster.getRedisCluster().hmset(key, map);
			jedis.del(key);
			log.info("删除后时间"+System.currentTimeMillis());
			//redisCluster.getRedisCluster().del(key);
			resultString = "1";
		} catch (Exception e) {
			log.info("deleteFromRedis中的异常");
			e.printStackTrace();
			return "-1";
		}
		log.info("deleteFromRedis方法的返回值"+resultString);
		return resultString;
	}
	/**
	 *判断指定的key存不存在
	 * @param key
	 * @return String 1为存在 0为不存在 -1为调用发生异常
	 */
	public String existsKey(String key){
		log.info("执行existsKey方法");
		//GetRedisCluster redisCluster = new GetRedisCluster();		
		log.info("existsKey方法得到的key "+key);
		String resultString = "begin";
		Boolean result;
		try {
			System.out.println(System.currentTimeMillis());
			result = jedis.exists(key);	
			System.out.println(System.currentTimeMillis());
		} catch (Exception e) {
			log.info("existsKey中的异常");
			e.printStackTrace();
			return "-1";
		}
		if(result){
			resultString = "1";
		}
		else{
			resultString = "0";
		}
		System.out.println("existsKey方法的返回值"+resultString );
		return resultString;
	}
	/**
	 * 查询Redis数据库
	 * @return
	 */
	
	public String[][][] selectFromRedis(String field,String value){
		log.info("执行selectFromRedis方法");
		long startTime=System.currentTimeMillis();  
		Set<String> setCluster = new HashSet<String>();
		Set<String> set79 = new HashSet<String>();
		Set<String> set80 = new HashSet<String>();
		Set<String> set81 = new HashSet<String>();
		//获得6379所有key
//		Jedis jed79 =new Jedis("192.168.56.101",6379);		
//		set79 = jed79.keys("*");
//		Iterator<String> it = set79.iterator();  
//		while (it.hasNext()) {  
//		  String str = it.next();  
//		  System.out.println(str+"----6379");  
//		}
//		setCluster.addAll(set79);
		//获得6380所有key	
		set80 = jedis.keys("*");
		Iterator<String> it1 = set80.iterator();  
		while (it1.hasNext()) {  
		  String str = it1.next();  
		  System.out.println(str+"----6380");  
		}
		setCluster.addAll(set80);
		//获得6381所有key
//		Jedis jed81 =new Jedis("192.168.56.101",6381);		
//		set81 = jed81.keys("*");
//		Iterator<String> it2 = set80.iterator();  
//		while (it2.hasNext()) {  
//		  String str = it2.next();  
//		  System.out.println(str+"----6381");  
//		}
//		setCluster.addAll(set81);
//		
//		long endTime=System.currentTimeMillis();
//		System.out.println("获取所有key程序运行时间： "+(endTime-startTime)+"ms");
		
//		GetRedisCluster redisCluster = new GetRedisCluster();
//		JedisCluster rec = redisCluster.getRedisCluster();
		Map map = new HashMap();
		Map map1 = new HashMap();
		Set mapAll = new HashSet();
		try {
			long startTime2=System.currentTimeMillis();
			Iterator<String> itAll = setCluster.iterator();  
			log.info("获得的所有key的个数  :"+setCluster.size());
			while (itAll.hasNext()) {
				String str = itAll.next();
				log.info(str+"===========================");
				//System.out.println(value.equals(rec.hget(str, field))+"&&&&&&&&&&&&&&&&&&&&");
				log.info(System.currentTimeMillis());
				if(value.equals(jedis.hget(str, field))){				
					map = (Map) jedis.hgetAll(str);
					//System.out.println(map);
					mapAll.add(map);
					log.debug(mapAll);
					//System.out.println(str+"----setCluster");
				}
				log.info(System.currentTimeMillis());
				log.info("比较一个map中字段程序运行时间： ms");
			}
			long endTime2=System.currentTimeMillis();
			log.info("比较所有map中字段程序运行时间： "+(endTime2-startTime2)+"ms");
			 
		} catch (Exception e) {
			log.info("selectFromRedis中的异常");
			e.printStackTrace();
			String[][][] fail = {{{"-1"}},{{"-1"}},{{"-1"}}};
			return fail;
		}
		
		log.info(mapAll.size()+"有多少条符合的记录#####################mapAll.size()");
		String[][][] ObjectMapAll = null;
		Iterator<Map> it3 = mapAll.iterator();				  			    				 
		 int j = 0;
		 ObjectMapAll = new String[mapAll.size()][][];
		while (it3.hasNext()) {
			 map1 = it3.next(); 
			 //关键是这个值entrySet.size()在循环外面得不到
			 Set<Map.Entry<String, String>> entrySet = map1.entrySet();              
			 Iterator<Map.Entry<String, String>> itMap = entrySet.iterator();
			// System.out.println(entrySet.size());			 
			 String[][] ObjectMap = new String[entrySet.size()][];
			  int i = 0; 
			  while(itMap.hasNext()){
				  String[] arr = new String[2];
				  Map.Entry<String, String> me = itMap.next();
				  String key1 = me.getKey();
				  String value1 = me.getValue();
				  arr[0] = key1;
				  arr[1] = value1;
				  ObjectMap[i] = arr;
				  ObjectMapAll[j] = ObjectMap;
//				  ObjectMap[i][0] = key1;
//				  ObjectMap[i][1] = value1;
//				  ObjectMapAll[j][i][0] = ObjectMap[i][0];
//				  ObjectMapAll[j][i][1] = ObjectMap[i][1];
				  log.info("selectFromRedis方法中"+"key: "+key1+"-->value: "+value1);
				  i++;				 
			  }			  
			  j++;
			  log.info(j+"^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			}
		for (int i = 0; i < ObjectMapAll.length; i++) {         // 遍历数组  
            for (int j1 = 0; j1 < ObjectMapAll[i].length; j1++) {  
                for (int k = 0; k < ObjectMapAll[i][j1].length; k++) {  
                	log.info(ObjectMapAll[i][j1][k] + "\t");  
                }  
                System.out.println();                       // 输出一维数组后换行  
            }  
		}
			return ObjectMapAll;
		}
	
}
