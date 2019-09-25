[toc]

> 参考：  
> [Chapter 6. 使用Spring进行面向切面编程(AOP)](http://shouce.jb51.net/spring/aop.html#aop-introduction-defn)  
> [AOP基本概念](https://www.jianshu.com/p/b114eebcd2e9)  
> [spring aop 五种通知的执行顺序](https://www.jianshu.com/p/87171860901c)   
> [Spring的AOP](https://www.jianshu.com/p/9aed3fb454df)
> [Spring AOP(二) 修饰者模式和JDK Proxy](https://www.jianshu.com/p/f2f2828f4c5b)
> [Spring AOP(三) Advisor类架构](https://www.jianshu.com/p/7f1116b6b6f5)

### aop

#### aop: 概念和术语
> Aspect-oriented programming面向切面编程  

面向切面编程(AOP)通过提供另外一种思考程序结构的途经来弥补面向对象编程(OOP)的不足。在OOP中模块化的关键单元是类(classes)，而在AOP中模块化的单元则是切面。切面能对关注点进行模块化，例如横切多个类型和对象的事务管理。(在AOP术语中通常称作横切(crosscutting)关注点。)

AOP框架是Spring的一个重要组成部分。但是Spring IoC容器并不依赖于AOP，这意味着你有权利选择是否使用AOP，AOP做为Spring IoC容器的一个补充,使它成为一个强大的中间件解决方案。

AOP在Spring Framework中的作用:
* 提供声明式企业服务，特别是为了替代EJB声明式服务。最重要的服务是声明性事务管理。
* 允许用户实现自定义切面，用AOP来完善OOP的使用


AOP概念和术语:
* 切面(Aspect): 切面是一个横切关注点的模块化，一个切面能够包含同一个类型的不同增强方法，比如说事务处理和日志处理可以理解为两个切面。
切面由切入点和通知组成，它既包含了横切逻辑的定义，也包括了切入点的定义。 
Spring AOP就是负责实施切面的框架，它将切面所定义的横切逻辑织入到切面所指定的连接点中。
``` 
@Aspect
@Component
public class WebRequestLogAspect {//可以简单地认为, 使用 @Aspect 注解的类就是切面
}
```

* 连接点(Joinpoint): 在程序执行过程中某个特定的点，比如某方法调用的时候或者处理异常的时候。在Spring AOP中，一个连接点总是表示一个方法的执行。
简单来说，连接点就是被拦截到的程序执行点，因为Spring只支持方法类型的连接点，所以在Spring中连接点就是被拦截到的方法。
``` 
@Before("webLog()")
public void doBefore(JoinPoint joinPoint) {//JoinPoint参数就是连接点
}
```

* 切入点(PointCut): 指定哪些Bean组件的哪些方法使用切面组件  
``` 
//这个切入点的匹配规则是com.xzj.stu.web.controller包下的所有类的所有函数
@Pointcut("execution(public * com.xzj.stu.web.controller.*.*(..))")
public void webLog() {
}
```

* 通知(Advice): 通知是指拦截到连接点之后要执行的代码  
通知类型:   
> * 前置通知（Before advice）: 在某连接点之前执行的通知，但这个通知不能阻止连接点之前的执行流程（除非它抛出一个异常）。
> * 后置通知（After returning advice）: 在某连接点正常完成后执行的通知: 例如，一个方法没有抛出任何异常，正常返回。
> * 异常通知（After throwing advice）: 在方法抛出异常退出时执行的通知。
> * 最终通知（After (finally) advice）: 当某连接点退出的时候执行的通知（不论是正常返回还是异常退出）。
> * 环绕通知（Around Advice）: 包围一个连接点的通知，如方法调用。这是最强大的一种通知类型。环绕通知可以在方法调用前后完成自定义的行为。它也会选择是否继续执行连接点或直接返回它自己的返回值或抛出异常来结束执行。

通知类型执行顺序：  
正常情况：  
![](https://upload-images.jianshu.io/upload_images/12170632-2931198a9b094f45.jpeg?imageMogr2/auto-orient/strip|imageView2/2/format/webp)  

多个切面的情况下，可以通过@Order指定先后顺序，数字越小，优先级越高。  
![](https://upload-images.jianshu.io/upload_images/12170632-67b89b71dc0cb8ac.jpeg?imageMogr2/auto-orient/strip|imageView2/2/format/webp)


* 织入(Weaving): 织入是将切面和业务逻辑对象连接起来, 并创建通知代理的过程。织入可以在编译时，类加载时和运行时完成。在编译时进行织入就是静态代理，而在运行时进行织入则是动态代理。

* 增强器(Adviser): Advisor是切面的另外一种实现，能够将通知以更为复杂的方式织入到目标对象中，是将通知包装为更复杂切面的装配器。Advisor由切入点和Advice组成。

#### aop实现
AOP实现可分为两类（按AOP框架修改源代码的时机）:  
* 静态AOP实现：AOP框架在编译阶段对程序进行修改，即实现对目标类的增强，生成静态的AOP代理类（生成的*.class文件已经被改掉了，需要使用特定的编辑器）。以AspectJ为代表。
* 动态AOP实现：AOP框架在运行阶段动态生成AOP代理（在内存中以JDK动态代理或cglib动态代理生成AOP代理类）。以实现对目标类的增强。以Spring AOP为代表。

##### 静态代理模式
所谓静态代理就是AOP框架会在编译阶段生成AOP代理类，因此也称为编译时增强。ApsectJ是静态代理的实现之一，也是最为流行的。静态代理由于在编译时就生成了代理类，效率相比动态代理要高一些。

##### 动态代理模式
与静态代理不同，动态代理就是说AOP框架不会去修改编译时生成的字节码，而是在运行时在内存中生成一个AOP代理对象，这个AOP对象包含了目标对象的全部方法，并且在特定的切点做了增强处理，并回调原对象的方法。
Spring aop目前仅支持将方法调用作为连接点（Joinpoint），如果需要把对成员变量的访问和更新也作为增强处理的连接点，则可以考虑使用AspectJ。

##### spring aop动态代理实现方式
* JDK动态代理  
spring aop 缺省使用jdk动态代理，这要求被代理类必须实现一个接口。JDK代理通过反射来处理被代理的类，核心类是 InvocationHandler接口 和 Proxy类。
当目标类没有实现接口时，Spring AOP框架会使用CGLIB来动态代理目标类。  

* CGLIB动态代理   
CGLIB（Code Generation Library），是一个代码生成的类库，可以在运行时动态的生成某个类的子类。CGLIB是通过继承的方式做的动态代理，因此如果某个类被标记为final，那么它是无法使用CGLIB做动态代理的。核心类是 MethodInterceptor 接口和Enhancer 类



### aop如何实现代理的
？？？

### aop在spring ioc 中如何装配
？？？