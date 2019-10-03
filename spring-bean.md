[toc]

### spring bean
> [Spring Framework 5.2 Documentation](https://docs.spring.io/spring/docs/5.2.0.RELEASE/spring-framework-reference/core.html#beans-factory-scopes)  
> [Spring Framework 5 Core 学习笔记](https://www.jianshu.com/p/f2a8c63a4927)  
> [看起来很长但还是有用的Spring学习笔记](https://www.jianshu.com/p/c4164e9a407c)


#### bean 
每一个类实现了Bean的规范才可以由Spring来接管，那么Bean的规范是什么呢？
* 必须是个公有(public)类
* 有无参构造函数
* 用公共方法暴露内部成员属性(getter,setter)
实现这样规范的类，被称为Java Bean。即是一种可重用的组件。


#### bean 作用域

代码中定义bean作用域示例：
```java
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IndexController {}
 ```

| 作用域 | 使用 | 描述 |
|---|---|---|  
| singleton | @Scope("singleton") | IoC 容器（ApplicationContext）范围单例 |  
| prototype | @Scope("prototype") | 每次获取该 Bean 都会 new 一个新的对象返回 |  
| request | @Scope("request") 或 @RequestScope | 单次 HTTP 请求范围单例 |  
| session | @Scope("session") 或 @SessionScope | 单个 HTTP 会话范围单例 |  
| application | @Scope("application") 或 @ApplicationScope | Web 应用（ServletContext）范围单例 |  
| websocket | @Scope("websocket") | 单个 WebSocket 会话范围单例 |

##### singleton
单例模式，Spring IoC容器中只会存在一个共享的Bean实例，无论有多少个Bean引用它，始终指向同一对象。
该模式在多线程下是不安全的。Singleton作用域是Spring中的缺省作用域

##### prototype
原型模式，每次通过Spring容器获取prototype定义的bean时，容器都将创建一个新的Bean实例，每个Bean实例都有自己的属性和状态。

##### request
在一次Http请求中，容器会返回该Bean的同一实例。而对不同的Http请求则会产生新的Bean，而且该bean仅在当前Http Request内有效，
当前Http请求结束，该bean实例也将会被销毁。

##### session
在一次Http Session中，容器会返回该Bean的同一实例。而对不同的Session请求则会创建新的实例，该bean实例仅在当前Session内有效。
同Http请求相同，每一次session请求创建新的实例，而不同的实例之间不共享属性，且实例仅在自己的session请求内有效，请求结束，则实例将被销毁。

##### application

##### websocket

##### 自定义作用域
实现 org.springframework.beans.factory.config.Scope 接口，并将其对象注册到 BeanFactory 中，然后即可通过 @Scope("...") 方式使用。  
Spring 内置了一个线程范围单例作用域 org.springframework.context.support.SimpleThreadScope ，但默认没有注册，可通过如下方式注册：
```java
@Bean
public static CustomScopeConfigurer customScopeConfigurer() {
     CustomScopeConfigurer customScopeConfigurer = new CustomScopeConfigurer();
     customScopeConfigurer.addScope("thread", new SimpleThreadScope());
     return customScopeConfigurer;
}
```

##### 不同作用域注入问题
> singleton : 长作用域  
> prototype : 短作用域  

当在 singleton Bean 中注入短作用域 Bean 时，需要通过 AOP 为短作用域 Bean 生成代理 Bean，才能确保在 singleton Bean 中每次获取到最新的短作用域 Bean。
配置 @Scope 注解的 proxyMode 属性即可。比如：
```java
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
```
proxyMode 属性可配置值如下：  

| 代理模式 | 描述 |
| ------------ | ------------ |
| ScopedProxyMode.DEFAULT | 默认值，等同于 NO |  
| ScopedProxyMode.NO | 不创建代理 |  
| ScopedProxyMode.INTERFACES | 通过实现接口的方式生成代理（JDK），注入到接口时适用 |  
| ScopedProxyMode.TARGET_CLASS | 通过继承的方式生成代理（CGLIB），非 final 类适用 |  


#### bean 生命周期
##### @PostConstruct、@PreDestroy、@Bean(initMethod)、@Bean(destroyMethod)、InitializingBean、DisposableBean
1. 在 Spring 管理的 Bean 中，可以使用 @PostConstruct 和 @PreDestroy 注解标注需要在 Bean 初始化之后和销毁之前需要执行的方法。比如：
```java
@Service
public class Foo {

     @PostConstruct
     public void init() {
         // 该方法会在 Bean 初始化完成、装载所有依赖之后、AOP 拦截器应用到该 Bean 之前执行
     }
 
     @PreDestroy
     public void destroy() {
         // 该方法会在 Bean 销毁之前执行
     }
}
```


2. 通过 @Bean 注解的 initMethod 和 destroyMethod 属性指定初始化之后和销毁之前需要执行的方法。比如：
```java
 public class Foo {
 
     public void init() {
         // initialization logic
     }
 }
 
 public class Bar {
 
     public void cleanup() {
         // destruction logic
     }
 }
 
 @Configuration
 public class AppConfig {
 
     @Bean(initMethod = "init")
     public Foo foo() {
         return new Foo();
     }
 
     @Bean(destroyMethod = "cleanup")
     public Bar bar() {
         return new Bar();
     }
 }
```
> 注意： 如果不指定 @Bean 注解的 destroyMethod 属性，默认以 public 类型的 close 方法或 shutdown 方法作为销毁方法。如果开发者的类中有这些方法且不希望作为销毁方法，需指定 destroyMethod = ""。


3. 让 Bean 实现 InitializingBean 和 DisposableBean 接口，afterPropertiesSet() 方法和 destroy() 方法会在该 Bean 初始化之后和销毁之前执行。比如：  
```java
 @Compenent
 public class AnotherExampleBean implements InitializingBean, DisposableBean {

     @Override
     public void afterPropertiesSet() {
         // do some initialization work
     }

     @Override
     public void destroy() {
         // do some destruction work (like releasing pooled connections)
     }
 }
```


##### Bean的生命周期
bean工厂执行启动步骤:  
1. Spring对bean进行实例化；//实例化
2. Spring将值和bean的引用注入到bean对应的属性中；//IOC依赖注入
3. 如果bean实现了BeanNameAware接口，Spring将bean的ID传递给setBeanName()方法；
4. 如果bean实现了BeanFactoryAware接口，Spring将调用setBeanFactory()方法，将BeanFactory容器实例传入；
5. 如果bean实现了ApplicationContextAware接口，Spring将调用setApplicationContext()方法，将bean所在的应用上下文的引用传入进来；
6. 如果bean实现了BeanPostProcessor接口，Spring将调用它们的postProcessBeforeInitialization()方法；
7. 如果bean实现了InitializingBean接口，Spring将调用它们的afterPropertiesSet()方法。类似地，如果bean使用initMethod声明了初始化方法，该方法也会被调用；
8. 如果bean实现了BeanPostProcessor接口，Spring将调用它们的postProcessAfterInitialization()方法；
9. 此时，bean已经准备就绪，可以被应用程序使用了，它们将一直驻留在应用上下文中，直到该应用上下文被销毁；
10. 如果bean实现了DisposableBean接口，Spring将调用它的destroy()接口方法。同样，如果bean使用destroyMethod声明了销毁方法，该方法也会被调用。  
![spring-bean](https://ws2.sinaimg.cn/large/005UybFhly1g7lc2n1d1rj30lu0kltka.jpg)


#### bean 依赖注入

