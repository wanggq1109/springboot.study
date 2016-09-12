# springboot.study
近期因为公司项目的需要，用上了maven和Springboot，对于java开发这块，早闻maven是个好东西，但一直没有去用，感觉用maven帮我们自己做了太多的事情，一个项目跑起来都不知道背后做了些什么，现在想想，可能那个时候脑子进水了吧。
Springboot作为Spring的简约版（我是这么叫的，没有任何依据），将原来Spring需要做的配置文件，改为了注解，提供了大量的***start组件来帮我们做了很多初始化的处理。
但是今天我想要分享的不是maven如何搭建一个helloword的项目，也不是演示Springboot如何入门。这些网上有很多例子，我想我后续也会慢慢整理属于我的一套。今天想分享的是如何用Springboot配置多数据源（dataSource）的问题，之前一直知道Springboot可以很方便的额管理多数据源，当时遇到这个问题的时候，在网上查找了很长时间，也看了看Springboot的官方文档，都没有找到自己想要的解决方案，无奈看了看Spring是如何配置多数据源的，慢慢的有了些思路，废话不说了，开始吧
1.准备：一个maven工程，mysql数据库

2.pom.xml配置
<dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.4.1</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>1.1.1</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>
    </dependencies>

3.创建两个数据库company1,company2
company1中创建表t_user
-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `userid` int(11) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES ('0', 'Hello World');
company2中创建表t_subject
-- ----------------------------
-- Table structure for t_subject
-- ----------------------------
DROP TABLE IF EXISTS `t_subject`;
CREATE TABLE `t_subject` (
  `subid` int(11) NOT NULL,
  `subname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`subid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_subject
-- ----------------------------
INSERT INTO `t_subject` VALUES ('0', 'Start');

4.application.properties增加对数据源的配置
#database1 config
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/company1?useUnicode=true&characterEncoding=utf-8
spring.datasource.username=root
spring.datasource.password=root
#database2 config
spring.datasource2.driver-class-name=com.mysql.jdbc.Driver
spring.datasource2.url=jdbc:mysql://127.0.0.1:3306/company2?useUnicode=true&characterEncoding=utf-8
spring.datasource2.username=root
spring.datasource2.password=root
5.配置类的实现
首先是ApplicationStart.java
没有其他的意义，只是让它成为程序的入口
@SpringBootApplication(scanBasePackages="springboot.start",excludeName={"springboot.start.db.company1.mapper","springboot.start.db.company2.mapper"})
public class ApplicationStart {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationStart.class, args);
    }
}
数据源的配置，因为有两个数据源，最好是用两个配置类进行初始话，原因后面会提到
第一个数据源：MysqlDataSource1Config
@Configuration
@MapperScan("springboot.start.db.company1.mapper")
public class MysqlDataSource1Config {
    @Bean(name = "primaryDataSource")
    @Primary
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource dataSource(){
        return DataSourceBuilder.create().build();
    }
    @Bean(name="primarySqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        return sessionFactory.getObject();
    }
}
第二个数据源：MysqlDataSource2Config
@Configuration
@MapperScan("springboot.start.db.company2.mapper")
public class MysqlDataSource2Config {
    @Bean(name = "secondDataSource")
    @ConfigurationProperties(prefix="spring.datasource2")
    public DataSource dataSource(){
        return DataSourceBuilder.create().build();
    }
    @Bean(name = "secondSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        return sessionFactory.getObject();
    }
}
我的整个包层次如下

下面来看看DbService是怎么用这两个数据源的
@Service
public class DbService {
    @Autowired
    TUserMapper tUserMapper;
    @Autowired
    TSubjectMapper tSubjectMapper;
    /**查询第一个数据源的表
     * 根据uid查询user
     * @param uid
     * @return
     */
    public TUser selectUserById(int uid){
        TUser tUser = null;
        TUserExample example = new TUserExample();
        Criteria criteria = example.createCriteria();
        criteria.andUseridEqualTo(uid);
        List<TUser> users = tUserMapper.selectByExample(example);
        if(users!=null && users.size()>0){
            tUser = users.get(0);
        }
        return tUser;
    }
    /**
     * 查询第二个数据源的表
     * @param sid
     * @return
     */
    public TSubject selectSubjectById(int sid){
        TSubject subject = null;
        TSubjectExample example = new TSubjectExample();
        example.createCriteria().andSubidEqualTo(sid);
        List<TSubject> subjects = tSubjectMapper.selectByExample(example);
        if(subjects!=null && subjects.size()>0){
            subject = subjects.get(0);
        }
        return subject;
    }
}
是不是很简单，不管怎么说，我来看看测试的情况


第二个呢……

靠出错了，尼玛，这是要打脸的节奏，从异常的意思来看是说程序还是从数据源1中去查的t_subject表，为什么呢？
回到第二个数据源的配置，再看看MapperScan的属性，其中有一个sqlSessionFactoryRef，天哪噜，这个Spring中的xml配置何其的相似啊，于是乎在第二个数据源上加上sqlSessionFactoryRef="secondSqlSessionFactory" （secondSqlSessionFactory在bean注解是做了声明的）

至此呢，关于Springboot和mybatis关于双数据源的配置问题，就告一段落了，对于多数据源也适用哦，小菜试过了的

总结：
1.不同数据源的配置，要写在不同的配置类中个，因为MapperScan好像只支持（猜的，有待深入）一个数据源的配置
2.不同数据源对应的mapper,实体类，最好放在不同的包下，在指定MapperScan中的basePackages时，如果两个数据源的包结构出现父子关系，子包的MapperScan好像就不再生效了，如一个数据源的的mapper,实体类放在springboot.datasource包中,第二个数据源的mapper,实体类放在了springboot.datasource.datasource2中，后面那个MapperScan就不会在扫了，这点我没有直接的截图那样的依据，但我有试过不分包的情况，那样只会有一个默认的那个数据源有效（我的理解是，MapperScan应该是有一个处理机制，当遇到basePackages出现包含关系时，将会实现范围大的那个）
3.在多数据源的配置时，注意在默认的那个要加上@primary
4.在非primary配置中，要注意sqlSessionFactoryRef的配置（当然这个只有用mybatis时需要的）

