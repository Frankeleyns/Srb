# Redis 缓存



# 01-集成 Redis 缓存



## 一、场景

由于数据字典的变化不是很频繁，而且系统对数据字典访问较为频繁，所以我们有必要将数据字典的数据存入缓存，提高系统访问速度，这里我选择的是 **Redis** ( 不知道 Redis 的可自行百度) 作为缓存插件。



## 二、引入 Redis



### 1. 引入依赖

在 **service-core** 中引入 redis 的依赖：

```xml
<!-- spring boot redis缓存引入 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<!-- 缓存连接池-->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>

<!-- redis 存储 json序列化 -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>
```



### 2. 配置 redis

在配置文件 **applicaiton.properties** 加入 redis 配置

```properties
# redis 服务器地址
spring.redis.host=192.168.209.125
spring.redis.port=6379
spring.redis.database=0
spring.redis.timeout=3000
#最大连接数，负值表示没有限制，默认8
spring.redis.lettuce.pool.max-active=20
#最大阻塞等待时间，负值表示没限制，默认-1
spring.redis.lettuce.pool.max-wait=-1
#最大空闲连接，默认8
spring.redis.lettuce.pool.max-idle=8
#最小空闲连接，默认0
spring.redis.lettuce.pool.min-idle=0
```



### 3. Redis 序列化配置

在 **com.frankeleyn.srb.core.config** 包下新建类 **RedisConfig**：

```java
package com.frankeleyn.srb.core.config;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //首先解决key的序列化方式
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        //解决value的序列化方式
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        //序列化时将类的数据类型存入json，以便反序列化的时候转换成正确的类型
        ObjectMapper objectMapper = new ObjectMapper();
        //objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        // 解决jackson2无法反序列化LocalDateTime的问题
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());

        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);


        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        return redisTemplate;
    }
}
```

一定要进行 redis 的序列化配置，如果不设置，redis 默认是使用 jdk 的序列化方式存储数据，

```java
@Test
public void testSetKey() {
    redisTemplate.opsForValue().set("key1", "value1");
}
```

redis服务器中 存储结果为：

 **key: \xAC\xED\x00\x05t\x00\x08key1**

 **value: \xAC\xED\x00\x05t\x00\x0Dvalue1**



### 4. 测试 Redis

新建测试类 **RedisTest** :

```java
package com.frankeleyn.srb.core;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void testSetKey() {
        redisTemplate.opsForValue().set("key1", "value1");
    }

    @Test
    public void testGetKey() {
        Object key1 = redisTemplate.opsForValue().get("key1");
        System.out.println(key1);
    }
}
```

设置 redis 序列化后， key 使用了字符串存储，value 使用了 json 存储。





# 02-使用缓存存取数据



## 将数据字典存入 Redis

修改 **DictServiceImpl** 中的 listByParentId() 方法：

```java
@Autowired
private RedisTemplate redisTemplate;

@Override
public List<Dict> listByParentId(Long parentId) {

    List<Dict> dictList;

    // 1. 先查询 Redis 缓存中中是否有数据字典
    dictList = (List<Dict>) redisTemplate.opsForValue().get("srb:core:dictList:" + parentId);
    if (Objects.nonNull(dictList)) {
        return dictList;
    }

    // 2. 缓存为空就查询  DB
    dictList = baseMapper.selectList(new QueryWrapper<Dict>().eq("parent_id", parentId));
    dictList.forEach(dict -> {
        boolean hasChildren = this.hasChildren(dict.getId());
        dict.setHasChildren(hasChildren);
    });

    // 3. 数据库不为空，同步缓存
    if (Objects.nonNull(dictList)) {
        redisTemplate.opsForValue().set("srb:core:dictList:" + parentId, dictList);
    }

    return dictList;
}
```
