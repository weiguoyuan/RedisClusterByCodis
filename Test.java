public class Test {
	public static void main(String[] args) {	
		Redis redis = new Redis();
		String key="19992234";
		String[][] value = {{"1","2"},{"1","2"}};
		for(int i=0;i<10000;i++){
			redis.saveToRedisWithKey(key+i, value);
			//redis.deleteFromRedis(key+i);
		}
		//redis.saveToRedisWithKey(key, value);
		//redis.getFromRedis(key);
		//redis.deleteFromRedis(key);
	}
}
