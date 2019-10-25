
[toc]

## Redis
> [springboot+redis项目实战](https://www.jianshu.com/p/b9154316227e)  
> [redis中文官方文档](http://www.redis.cn/documentation.html)
> [spring boot 集成 redis lettuce](https://www.cnblogs.com/taiyonghai/p/9454764.html)
> [解决了redis的这些问题，你就是redis高手](https://www.toutiao.com/a6749708288830472716/)

### Redis基本数据结构
#### 1、string（字符串）
二进制安全的。意思是 redis 的 string 可以包含任何数据。比如jpg图片或者序列化的对象。string 类型的值最大能存储 512MB。  

命令：
``` 
SET name "xzj"
GET name
```

#### 2、hash（哈希）
一个键值(key=>value)对集合，每个 hash 可以存储 232 -1 键值对（40多亿）。

命令：
``` 
HMSET person name "xzj" age 29 country "china"
HGET person age
```

#### 3、list（列表）
是简单的字符串列表，按照插入顺序排序。你可以添加一个元素到列表的头部（左边）或者尾部（右边）。
列表最多可存储 232 - 1 元素 (4294967295, 每个列表可存储40多亿)。

命令：
``` 
lpush personList xzj
lpush personList zs
lpush personList ls
lpush personList we mz xm zs
lrange personList 0 10
```

#### 4、set（集合）
Set 是 string 类型的无序集合。  
集合是通过哈希表实现的，所以添加，删除，查找的复杂度都是 O(1)。  
集合中最大的成员数为 232 - 1

命令：
sadd添加一个 string 元素到 key 对应的 set 集合中，成功返回 1，如果元素已经在集合中返回 0。
```
sadd testSet set1
sadd testSet set2
sadd testSet set3
sadd testSet set4
sadd testSet set1
smembers testSet //
```

#### 5、zset(sorted set：有序集合)
 zset 和 set 一样也是string类型元素的集合,且不允许重复的成员。  
不同的是每个元素都会关联一个double类型的分数。redis正是通过分数来为集合中的成员进行从小到大的排序。  
zset的成员是唯一的,但分数(score)却可以重复。

命令：
zadd key score member //添加元素到集合，元素在集合中存在则更新对应score
``` 
zadd zset 0 test1
zadd zset 2 test2
zadd zset 3 test3
zadd zset 4 test4
zadd zset 5 test5
zadd zset 1 test1
zrangebyscore zset 0 10
```




### Redis 使用
#### Redis命令 
> [Redis命令](http://www.redis.cn/commands.html)

基本数据类型操作命令见【Redis基本数据结构】  
常用命令： 

| 命令 | 描述 | 返回结果 |
| --- | --- | --- |
| DEL key | 该命令用于在 key 存在时删除 key | 成功后输出1，失败输出0 |
| EXISTS key | 检查给定 key 是否存在 | 存在后输出1，不存在输出0 |
| EXPIRE key seconds | 为给定 key 设置过期时间，以秒计 | 成功后输出1，失败输出0 |
| PEXPIRE key milliseconds | 设置 key 的过期时间以毫秒计 | 成功后输出1，失败输出0 |
| KEYS pattern | 查找所有符合给定模式( pattern)的 key |  |
| MOVE key db | 将当前数据库的 key 移动到给定的数据库 db 当中 |  |
| GETSET key value | 将给定 key 的值设为 value ，并返回 key 的旧值(old value) |  |
| SETNX key value | 只有在 key 不存在时设置 key 的值 |  |
| INCR key | 将 key 中储存的数字值增一 |  |
| DECR key | 将 key 中储存的数字值减一 |  |




#### 管道（Pipelining）
> [Redis 管道（Pipelining）](http://www.redis.cn/topics/pipelining.html)
> [Redis管道](https://www.jianshu.com/p/f37a63c3e0f3)

学习如何一次发送多个命令，节省往返时间。
> test.java.com.xzj.stu.redis.RedisTest.test_03_pipeline

通过测试可知通过管道发送比单次IO快一个量级

**注意：** 使用管道发送命令时，服务器将被迫回复一个队列答复，占用很多内存。所以，如果你需要发送大量的命令，最好是把他们按照合理数量分批次的处理，
例如10K的命令，读回复，然后再发送另一个10k的命令，等等。这样速度几乎是相同的，但是在回复这10k命令队列需要非常大量的内存用来组织返回数据内容。




#### Redis 发布/订阅（Pub/Sub）
redis是一个快速、稳定的发布/订阅的信息系统。

> com.xzj.stu.redis.common.config.RedisSubListenerConfig
> test.java.com.xzj.stu.redis.RedisTest.test_05_pubAndSub




#### Lua 脚本调试
Redis 3.2 Lua 脚本调试相关文档。




#### 内存优化
了解如何使用内存和学习一些使用技巧。
> [redis内存优化](http://www.redis.cn/topics/memory-optimization.html)




#### 过期（Expires）
Redis允许为每一个key设置不同的过期时间，当它们到期时将自动从服务器上删除。

**过期和持久**  
Keys的过期时间使用Unix时间戳存储(从Redis 2.6开始以毫秒为单位)。这意味着即使Redis实例不可用，时间也是一直在流逝的。

要想过期的工作处理好，计算机必须采用稳定的时间。 如果你将RDB文件在两台时钟不同步的电脑间同步，有趣的事会发生（所有的 keys装载时就会过期）。

即使正在运行的实例也会检查计算机的时钟，例如如果你设置了一个key的有效期是1000秒，然后设置你的计算机时间为未来2000秒，这时key会立即失效，而不是等1000秒之后。

**Redis如何淘汰过期的keys**  
Redis keys过期有两种方式：被动和主动方式。

当一些客户端尝试访问它时，key会被发现并主动的过期。

当然，这样是不够的，因为有些过期的keys，永远不会访问他们。 无论如何，这些keys应该过期，所以定时随机测试设置keys的过期时间。所有这些过期的keys将会从密钥空间删除。

具体就是Redis每秒10次做的事情：  
1. 测试随机的20个keys进行相关过期检测。
2. 删除所有已经过期的keys。
3. 如果有多于25%的keys过期，重复步奏1.

这是一个平凡的概率算法，基本上的假设是，我们的样本是这个密钥控件，并且我们不断重复过期检测，直到过期的keys的百分百低于25%,这意味着，在任何给定的时刻，最多会清除1/4的过期keys。





#### 将Redis当做使用LRU算法的缓存来使用
如何配置并且将Redis当做缓存来使用，通过限制内存及自动回收键。

**todo**



#### Redis 事务
> [Redis之Redis事务](https://www.cnblogs.com/DeepInThought/p/10720132.html)

1. Redis事务的概念：  
Redis 事务的本质是一组命令的集合。事务支持一次执行多个命令，一个事务中所有命令都会被序列化。在事务执行过程，会按照顺序串行化执行队列中的命令，其他客户端提交的命令请求不会插入到事务执行命令序列中。  
总结说：redis事务就是一次性、顺序性、排他性的执行一个队列中的一系列命令。　　

2. Redis事务没有隔离级别的概念：  
批量操作在发送 EXEC 命令前被放入队列缓存，并不会被实际执行，也就不存在事务内的查询要看到事务里的更新，事务外查询不能看到。

3. Redis不保证原子性：  
Redis中，单条命令是原子性执行的，但事务不保证原子性，且没有回滚。事务中任意命令执行失败，其余的命令仍会被执行。

4. Redis事务的三个阶段：  
* 开始事务
* 命令入队
* 执行事务

5. Redis事务相关命令：  
* watch key1 key2 ... : 监视一或多个key,如果在事务执行之前，被监视的key被其他命令改动，则事务被打断 （ 类似乐观锁 ）
* multi : 标记一个事务块的开始（ queued ）
* exec : 执行所有事务块的命令 （ 一旦执行exec后，之前加的监控锁都会被取消掉 ）　
* discard : 取消事务，放弃事务块中的所有命令
* unwatch : 取消watch对所有key的监控




#### 大量插入数据
如何在短时间里向Redis写入大量数据。
#### 从文件中批量插入数据
将文件中的指令批量执行。



#### 分区（Partitioning）
如何将你的数据分布在多个Redis里面。
> [分区](http://www.redis.cn/topics/partitioning.html)


#### 分布式锁（Distributed locks）
用Redis实现分布式锁管理器
> [分布式锁](http://www.redis.cn/topics/distlock.html)


#### key事件通知（Redis keyspace notifications）
通过发布/订阅获得key事件的通知（版本2.8或更高）。
#### 创建二级索引（Creating secondary indexes with Redis）
使用redis的数据结构创建二级索引。


### Redis 管理
#### Redis-Cli
学习怎么通过命令行使用redis。
#### 配置（Configuration）
怎么配置 redis。
#### 复制（Replication）
你需要知道怎么设置主从复制。
#### 持久化（Persistence）
了解如何配置redis的持久化。
#### Redis 管理（Redis Administration）
学习redis管理方面的知识。
#### 安全性（Security）
概述Redis的安全。
#### 加密（encryption）
如何加密redis的客户端与服务端通信。。
#### 信号处理（Signals Handling）
如何处理Redis信号。
#### 连接处理（Connections Handling）
如何处理Redis客户端连接。
#### 高可用性（High Availability）
Redis Sentinel是Redis官方的高可用性解决方案。目前工作进展情况（beta阶段，积极发展），已经可用。
#### 延迟监控（Latency monitoring）
redis集成的延迟监控和报告功能对于为低延迟应用场景优化redis很有帮助。
#### 基准（Benchmarks）
看看Redis在不同平台上跑得有多快。
#### Redis Releases
Redis的开发周期和版本编号。

### Redis 集群
#### Redis 如何实现集群

### Redis 实际问题
#### incr递增是如何实现原子性的？
redis所有操作都是原子性的  

对于Redis而言，命令的原子性指的是：一个操作的不可以再分，操作要么执行，要么不执行。

Redis的操作之所以是原子性的，是因为Redis是单线程的。

Redis本身提供的所有API都是原子操作，Redis中的事务其实是要保证批量操作的原子性。

#### Jedis和Lettuce
* Jedis和Lettuce都是Redis Client
* Jedis 是直连模式，在多个线程间共享一个 Jedis 实例时是线程不安全的，
* 如果想要在多线程环境下使用 Jedis，需要使用连接池，
* 每个线程都去拿自己的 Jedis 实例，当连接数量增多时，物理连接成本就较高了。
* Lettuce的连接是基于Netty的，连接实例可以在多个线程间共享，
* 所以，一个多线程的应用可以使用同一个连接实例，而不用担心并发线程的数量。
* 当然这个也是可伸缩的设计，一个连接实例不够的情况也可以按需增加连接实例。
* 通过异步的方式可以让我们更好的利用系统资源，而不用浪费线程等待网络或磁盘I/O。
* Lettuce 是基于 netty 的，netty 是一个多线程、事件驱动的 I/O 框架，
* 所以 Lettuce 可以帮助我们充分利用异步的优势。

#### 序列化
spring-data-redis中序列化类有以下几个：
* 1、GenericToStringSerializer：可以将任何对象泛化为字符创并序列化
* 2、Jackson2JsonRedisSerializer：序列化Object对象为json字符创（与JacksonJsonRedisSerializer相同）
* 3、JdkSerializationRedisSerializer：序列化java对象
* 4、StringRedisSerializer：简单的字符串序列化
 
JdkSerializationRedisSerializer序列化
* 被序列化对象必须实现Serializable接口，被序列化除属性内容还有其他内容，长度长且不易阅读
* 存储内容如下：
* "\xac\xed\x00\x05sr\x00!com.oreilly.springdata.redis.User\xb1\x1c \n\xcd\xed%\xd8\x02\x00\x02I\x00\x03ageL\x00\buserNamet\x00\x12Ljava/lang/String;xp\x00\x00\x00\x14t\x00\x05user1"

JacksonJsonRedisSerializer序列化
* 被序列化对象不需要实现Serializable接口，被序列化的结果清晰，容易阅读，而且存储字节少，速度快
* 存储内容如下：
* "{\"userName\":\"user1\",\"age\":20}"

StringRedisSerializer序列化
* 一般如果key、value都是string字符串的话，就是用这个就可以了


#### redis 布隆过滤器

#### redis消息队列
不要使用redis去做消息队列，这不是redis的设计目标