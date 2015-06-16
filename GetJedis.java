import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import com.wandoulabs.jodis.JedisResourcePool;
import com.wandoulabs.jodis.RoundRobinJedisPool;

public class GetJedis{
	private static JedisResourcePool jedisPool = new RoundRobinJedisPool("10.64.4.57:2181,192.10.64.4.95:2181,192.10.64.4.99:2181",
			30000, "/zk/codis/db_test/proxy", new JedisPoolConfig());//第一个参数为zookeeper节点列表
	public static Jedis getJedis(){
		return jedisPool.getResource();
	}
}
