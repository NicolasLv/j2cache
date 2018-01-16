# J2Cache 版本更新记录

**J2Cache 2.3.0-release (2018-1-15)**
* 删除缓存读取方法 getXxx 只保留 get 方法（自动识别不同类型数据），可通过 `CacheObject` 的 asXxx 方法来获取不同类型数据
* 删除 incr 和 decr 方法

**J2Cache 2.3.0-beta (2018-1-15)**
* 内部结构的重构，减少模块间的耦合，简化内部接口
* 去掉对 `commons-beanutils` 的依赖，由于 beanutils 1.8 和 1.9 版本 api 变化大，此举可避免对宿主系统的影响
* 修复了 RedisGenericCache 中 clear 方法的逻辑错误[BUG]

**J2Cache 2.2.4-release (2018-1-14)**
* 修复了配置中设置 Caffeine 缓存有效期时，单位无效的问题

**J2Cache 2.2.3-release (2018-1-10)**
* 修复字符串数据的处理问题

**J2Cache 2.2.2-release (2018-1-10)**
* 修复使用 `redis` 的订阅广播方式没有释放 `redis` 连接的问题（严重）
* `sharded` 模式下的 `Redis` 启用连接池
* `redis.mode` 的配置如果无效默认使用 `single` 模式，而不是抛出异常

**J2Cache 2.2.1-release (2018-1-10)**

* 提供 `clear` 和 `keys` 方法在 `generic` 存储方式下的非 `cluster` 模式下可用（性能可能会比较差，慎用）  
官方文档声称：`KEYS 的速度非常快，但在一个大的数据库中使用它仍然可能造成性能问题，如果你需要从一个数据集中查找特定的 key ，你最好还是用 Redis 的集合结构(set)来代替。`
* 命令行测试工具支持上下键调用历史命令记录（依赖 `JLine` 库）
* 当 Redis 重启时会导致订阅线程断开连接，J2Cache 将进行自动重连
* 支持指定 jgroups 配置文件名称 (`jgroups.configXml = /network.xml`）
* 删除 setIfAbsent 方法（有点多余，二级缓存以及有一些缓存框架不支持这个方法，开发者只需自行判断即可)
* 支持自定义的 ClusterPolicy ，详情请看 j2cache.properties
* `getObject` 改名 `get` ，能自动识别缓存中的数据是字符串还是序列化对象

**J2Cache 2.2.0 beta (2018-1-6)**

注意，该版本跟以往版本在 API 上不兼容！！！  

***新特性***

* 原有的 get/getAll 方法替换成 getXxxx 方法（删除原有方法）
* 增加对 Caffeine 的支持（一级缓存）
* 支持设置缓存对象的有效期
* 支持多种 Redis 普通存储模式和哈希存储模式(`redis.storage = generic|hash`)
* 增加 incr 和 decr 方法

***Bug修复***
* 修复 redis 连接没有释放的问题（严重,必须升级）

**J2Cache 2.1 (2018-1-3)**
* 为了避免在实际应用中的混淆，缓存的key统一为字符串（如果你不能确定，请谨慎升级到2.1）
* 增加更多的缓存操作方法(getAll,setAll)
* 增加新节点加入和退出集群的日志信息
* 增加了 Spring Boot Starter 模块（感谢 @zhangsaizz 的贡献）


**J2Cache 2.0.1 (2017-12-26)**
* 修复了 database 参数无效的问题
* 统一了几种 Redis 模式下的密码认证处理
* 修复了 sharded 模式无法使用 database 和 password 参数的问题
* 给子模块定义版本号，统一父模块的版本号
* 补充和完善 Javadoc 文档

**J2Cache 2.0-release (2017-12-24)**

* 增加对 Ehcache 3.x 的支持 `j2cache.L1.provider_class = ehcache3`
* 合并 1.x 中的 hibernate3 和 hibernate4 支持模块
* J2Cache 命令行工具改名 J2CacheCmd

**J2Cache 2.0-beta (2017-12-22)**

* 要求 Java 8 支持
* 全 Maven 模块化，去掉老版本的 Ant 支持
* 重构内部的各个接口，更加清晰直观，减少依赖关系
* 支持多种 Redis 单机和集群模式，并启用 Redis 连接池
* 支持带密码认证的 Redis 服务
* 支持 Ehcache 3.x (`j2cache.L1.provider_class = ehcache3`)
* 启用线程方式发送缓存失效的广播通知，避免网络问题导致的堵塞
* [重要] 尽管接口变化不大，但是 J2Cache 2.0 的接口跟 1.x 不兼容
* 对 Hibernate 以及其他框架的支持将在后期通过模块的方式引入项目中

**J2Cache 1.4.0 ()**

**J2Cache 1.3.0 (2015-11-5)**

* 支持使用 Redis 发布订阅机制实现缓存更新通知，用于替换 JGroups 组播方式，两种方式可在 j2cache.properties 中进行配置切换 (感谢 @flyfox 330627517@qq.com)
* 对 J2Cache 的调用进行重构，无法直接从老版本升级，需要更改调用方式为 J2Cache.getChannel()

**J2Cache 1.2.0 (2015-10-27)**

* 升级 jedis 和 jgroups 到最新版本
* 使用 Maven 模块对项目结构进行重新整理