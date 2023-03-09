<h1>项目介绍</h1>
<p>
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;RedKale (中文名: 红菜苔，湖北武汉的一种特产蔬菜) 是基于Java 8全新的微服务框架， 包含HTTP、WebSocket、TCP/UDP、数据序列化、数据缓存、依赖注入等功能。 本框架致力于简化集中式和微服务架构的开发，在增强开发敏捷性的同时保持高性能。
</p>
<strong>RedKale 有如下主要特点：</strong>
<ol>
<li>大量使用Java 8新特性（接口默认值、Stream、Lambda、JDk8内置的ASM等）</li>
<li>提供HTTP服务，同时内置JSON功能与限时缓存功能</li>
<li>TCP层完全使用NIO.2，并统一TCP与UDP的接口换</li>
<li>提供分布式与集中式部署的无缝切换</li>
<li>提供类似JPA功能，并包含数据缓存自动同步与简洁的数据层操作接口</li>
<li>可以动态修改已依赖注入的资源</li>
</ol>
&nbsp;&nbsp;&nbsp;本工程项目依赖 <strong>redkale-plugins</strong> 项目。

###缺点
<ol>
<li>JDK的更新、相关组件的开发、相关jar的安全与升级</li>
<li>json、缓存查询、数据更新性能强劲、但单次查询是弱项</li>
<li>缺乏大规模集群部署能力</li>
</ol>

&nbsp;&nbsp;&nbsp;由于RedKale使用了JDK 8 内置的ASM包，所以需要在源码工程中的编译器选项中加入： <b>-XDignore.symbol.file=true</b>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<h5>详情请访问:&nbsp;&nbsp;&nbsp;&nbsp;<a href='http://redkale.org' target='_blank'>http://redkale.org</a></h5>

C：操作系统、编译器、 驱动、 非gc类语言
C++：中间件 驱动 大型项目  gc类语言
Rust：区块链、编译器、高性能强劲web、驱动程序、操作系统 非gc类语言、核武重器战略部队

AI：指挥部

Java：高性能web服务集群/大数据服务 常规武器群 陆军部队 gc类语言

特种战斗部队：耗小快速高效：redkale xitca-web actix-web/WebSocket

JS/TS/CSS/Html5/Vue/React：常规武器群  两栖部队

Go：区块链、云原生、k8s 高并发服务集群 高端武器群  高并发 空军部队 gc类语言 
//消耗cpu和内存
gin
gorm m 


Python/R/Scala：爬虫、大数据分析 航母战斗群

Kotin/Swift/OC：海军部队

IM：websocket netty gws  gorilla
内存
cpu

存储 
go:minio ffmpeg


数据同步集成
DataX

部署
k8s

计算
flink
spark

消息 

日志 

监控 

运维 

测试 

低代码平台

图片加载

音视频播放

###适用场景
异步
多缓存查询
websocket

&nbsp;&nbsp;&nbsp;由于RedKale使用了JDK 8 内置的ASM包，所以需要在源码工程中的编译器选项中加入： <b>-XDignore.symbol.file=true</b>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<h5>详情请访问:&nbsp;&nbsp;&nbsp;&nbsp;<a href='http://redkale.org' target='_blank'>http://redkale.org</a></h5>
