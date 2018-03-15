package com.github.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.common.ModuleTest.*;

public class ModuleTest {

    static String PACKAGE = "com.github";
    // static String PARENT = "~/project/mall-dubbo/";
    static String PARENT = ModuleTest.class.getClassLoader().getResource("").getFile() + "../../../";
    static String PACKAGE_PATH = PACKAGE.replaceAll("\\.", "/");

    static String capitalize(String name) {
        StringBuilder sbd = new StringBuilder();
        for (String str : name.split("[.-]")) {
            sbd.append(str.substring(0, 1).toUpperCase()).append(str.substring(1));
        }
        return sbd.toString();
    }
    static void writeFile(File file, String content) {
        try (OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            write.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        generate("0-common",  "20880", "公共服务");
        generate("1-user",    "20881", "用户");
        generate("2-product", "20882", "商品");
        generate("3-order",   "20883", "订单");

        soutInfo();
    }

    private static List<List<String>> moduleNameList = new ArrayList<>();
    private static List<List<String>> moduleList = new ArrayList<>();
    private static void generate(String basicModuleName, String port, String comment) throws Exception {
        String moduleName = "module-" + basicModuleName;
        String packageName = basicModuleName;
        if (basicModuleName.contains("-")) {
            packageName = basicModuleName.substring(basicModuleName.indexOf("-") + 1);
        }
        String model = packageName + "-model";
        String server = packageName + "-server";
        String module = PARENT + moduleName;

        Parent.generateParent(moduleName, model, server, module, comment);
        Model.generateModel(moduleName, packageName, model, module, comment);
        Server.generateServer(moduleName, packageName, model, server, module, port, comment);

        moduleNameList.add(Arrays.asList(comment, moduleName));
        moduleList.add(Arrays.asList(model, server));
    }

    private static void soutInfo() throws Exception {
        System.out.println();
        for (List<String> list : moduleNameList) {
            System.out.println(String.format("<!-- %s模块 -->\n<module>%s</module>", list.get(0), list.get(1)));
        }
        System.out.println();
        for (List<String> list : moduleList) {
            System.out.println(String.format("\n<dependency>\n" +
                    "    <groupId>${project.groupId}</groupId>\n" +
                    "    <artifactId>%s</artifactId>\n" +
                    "    <version>${project.version}</version>\n" +
                    "</dependency>\n" +
                    "<dependency>\n" +
                    "    <groupId>${project.groupId}</groupId>\n" +
                    "    <artifactId>%s</artifactId>\n" +
                    "    <version>${project.version}</version>\n" +
                    "</dependency>", list.get(0), list.get(1)));
        }
        System.out.println("\n");
        for (List<String> list : moduleList) {
            System.out.println(String.format("<dependency>\n" +
                    "    <groupId>${project.groupId}</groupId>\n" +
                    "    <artifactId>%s</artifactId>\n" +
                    "</dependency>", list.get(0)));
        }
        System.out.println();
        for (List<String> list : moduleList) {
            String module = list.get(0);
            String className = capitalize(module.substring(0, module.indexOf("-model")));
            System.out.println(String.format("ALL_MODEL_ENUM.add(Loader.getEnumArray(" +
                    "%sConst.class, Const.enumPath(%sConst.MODULE_NAME)));", className, className));
        }
        System.out.println();
        Thread.sleep(20);
    }
}


class Parent {
    static void generateParent(String moduleName, String model, String server, String module, String comment) {
        new File(module).mkdirs();
        String PARENT_POM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <parent>\n" +
                "        <artifactId>mall-dubbo</artifactId>\n" +
                "        <groupId>com.github</groupId>\n" +
                "        <version>1.0-SNAPSHOT</version>\n" +
                "    </parent>\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "    <artifactId>%s</artifactId>\n" +
                "    <description>%s模块</description>\n" +
                "    <packaging>pom</packaging>\n" +
                "\n" +
                "    <modules>\n" +
                "        <module>%s</module>\n" +
                "        <module>%s</module>\n" +
                "    </modules>\n" +
                "</project>\n";
        String pom = String.format(PARENT_POM, moduleName, comment, model, server);
        writeFile(new File(module, "pom.xml"), pom);
    }
}


class Model {
    private static String CONST = "package " + PACKAGE + ".%s.constant;\n"+
            "\n"+
            "public final class %sConst {\n"+
            "\n"+
            "    /** 当前模块名 */\n"+
            "    public static final String MODULE_NAME = \"%s\";\n" +
            "\n" +
            "    /** 当前模块说明. 当用在文档中时有用 */\n" +
            "    public static final String MODULE_INFO = MODULE_NAME + \"-%s\";\n"+
            "}\n";

    private static String INTERFACE = "package " + PACKAGE + ".%s.service;\n" +
            "\n" +
            "public interface %sService {\n" +
            "}\n";

    private static String POM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
            "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
            "    <parent>\n" +
            "        <artifactId>%s</artifactId>\n" +
            "        <groupId>com.github</groupId>\n" +
            "        <version>1.0-SNAPSHOT</version>\n" +
            "    </parent>\n" +
            "    <modelVersion>4.0.0</modelVersion>\n" +
            "\n" +
            "    <artifactId>%s</artifactId>\n" +
            "    <description>%s模块中 所有与数据库相关的实体、枚举, 以及 dubbo 的接口 等</description>\n" +
            "\n" +
            "    <dependencies>\n" +
            "        <dependency>\n" +
            "            <groupId>${project.groupId}</groupId>\n" +
            "            <artifactId>erp-common</artifactId>\n" +
            "            <scope>provided</scope>\n" +
            "        </dependency>\n" +
            "    </dependencies>\n" +
            "</project>\n";

    static void generateModel(String moduleName, String packageName, String model,
                              String module, String comment) throws IOException {
        String parentPackageName = packageName.replace("-", ".");
        String clazzName = capitalize(parentPackageName);

        File modelPath = new File(module + "/" + model + "/src/main/java");
        modelPath.mkdirs();
        String modelPom = String.format(POM, moduleName, model, comment);
        writeFile(new File(module + "/" + model, "pom.xml"), modelPom);

        File modelSourcePath = new File(modelPath, PACKAGE_PATH + "/" + parentPackageName.replaceAll("\\.", "/"));
        File model_constant = new File(modelSourcePath, "constant");
        File model_interface = new File(modelSourcePath, "service");
        model_constant.mkdirs();
        model_interface.mkdirs();
        new File(modelSourcePath, "enums").mkdirs();
        new File(modelSourcePath, "model").mkdirs();
        String constModel = String.format(CONST, parentPackageName, clazzName, packageName, comment);
        writeFile(new File(model_constant, clazzName + "Const.java"), constModel);

        String interfaceModel = String.format(INTERFACE, parentPackageName, clazzName);
        writeFile(new File(model_interface, clazzName + "Service.java"), interfaceModel);
    }
}


class Server {
    private static String APPLICATION = "package " + PACKAGE + ";\n" +
            "\n" +
            "import " + PACKAGE + ".common.util.A;\n" +
            "import " + PACKAGE + ".common.util.LogUtil;\n" +
            "import org.springframework.boot.autoconfigure.SpringBootApplication;\n" +
            "import org.springframework.boot.builder.SpringApplicationBuilder;\n" +
            "import org.springframework.context.ApplicationContext;\n" +
            "\n" +
            "@SpringBootApplication\n" +
            "public class %sApplication {\n" +
            "\n" +
            "    public static void main(String[] args) {\n" +
            "        ApplicationContext ctx = new SpringApplicationBuilder(%sApplication.class).web(false).run(args);\n" +
            "        if (LogUtil.ROOT_LOG.isDebugEnabled()) {\n" +
            "            String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();\n" +
            "            if (A.isNotEmpty(activeProfiles)) {\n" +
            "                LogUtil.ROOT_LOG.debug(\"current profile : ({})\", A.toStr(activeProfiles));\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}\n";

    private static final String CONFIG_DATA = "package " + PACKAGE + ".%s.config;\n" +
            "\n" +
            "import " + PACKAGE + ".common.resource.CollectMybatisTypeHandlerUtil;\n" +
            "import " + PACKAGE + ".common.resource.CollectResourceUtil;\n" +
            "import " + PACKAGE + ".common.util.A;\n" +
            "import " + PACKAGE + ".global.constant.GlobalConst;\n" +
            "import " + PACKAGE + ".%s.constant.%sConst;\n" +
            "import org.apache.ibatis.type.TypeHandler;\n" +
            "import org.springframework.core.io.Resource;\n" +
            "\n" +
            "/** %s模块的配置数据. 主要是 mybatis 的多配置目录和类型处理器 */\n" +
            "final class %sConfigData {\n" +
            "\n" +
            "    private static final String[] RESOURCE_PATH = new String[] {\n" +
            "            %sConst.MODULE_NAME + \"/*.xml\",\n" +
            "            %sConst.MODULE_NAME + \"-custom/*.xml\"\n" +
            "    };\n" +
            "    /** 要加载的 mybatis 的配置文件目录 */\n" +
            "    static final Resource[] RESOURCE_ARRAY = CollectResourceUtil.resource(A.maps(\n" +
            "            %sConfigData.class, RESOURCE_PATH\n" +
            "    ));\n" +
            "    \n" +
            "    /** 要加载的 mybatis 类型处理器的目录 */\n" +
            "    static final TypeHandler[] HANDLER_ARRAY = CollectMybatisTypeHandlerUtil.handler(A.maps(\n" +
            "            GlobalConst.MODULE_NAME, GlobalConst.class,\n" +
            "            %sConst.MODULE_NAME, %sConfigData.class\n" +
            "    ));\n" +
            "}\n";

    private static String DATA_SOURCE = "package " + PACKAGE + ".%s.config;\n" +
            "\n" +
            "import com.alibaba.druid.pool.DruidDataSource;\n" +
            "import com.github.liuanxin.page.PageInterceptor;\n" +
            "import " + PACKAGE + ".common.Const;\n" +
            "import org.apache.ibatis.plugin.Interceptor;\n" +
            "import org.apache.ibatis.session.SqlSessionFactory;\n" +
            "import org.mybatis.spring.SqlSessionFactoryBean;\n" +
            "import org.mybatis.spring.SqlSessionTemplate;\n" +
            "import org.mybatis.spring.annotation.MapperScan;\n" +
            "import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;\n" +
            "import org.springframework.boot.context.properties.ConfigurationProperties;\n" +
            "import org.springframework.cache.annotation.EnableCaching;\n" +
            "import org.springframework.context.annotation.Bean;\n" +
            "import org.springframework.context.annotation.Configuration;\n" +
            "\n" +
            "import javax.sql.DataSource;\n" +
            "\n" +
            "/**\n" +
            " * 扫描指定目录. MapperScan 的处理类是 MapperScannerRegistrar, 其基于 ClassPathMapperScanner<br>\n" +
            " *\n" +
            " * @see org.mybatis.spring.annotation.MapperScannerRegistrar#registerBeanDefinitions\n" +
            " * @see org.mybatis.spring.mapper.MapperScannerConfigurer#postProcessBeanDefinitionRegistry\n" +
            " * @see org.mybatis.spring.mapper.ClassPathMapperScanner\n" +
            " */\n" +
            "@Configuration\n" +
            "@EnableCaching\n" +
            "@MapperScan(basePackages = Const.BASE_PACKAGE)\n" +
            "public class %sDataSourceInit {\n" +
            "\n" +
            "    /** mybatis 的分页插件 */\n" +
            "    private Interceptor mybatisPage() {\n" +
            "        return new PageInterceptor(\"mysql\");\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * http://docs.spring.io/spring-boot/docs/1.3.6.RELEASE/reference/htmlsingle/#howto-two-datasources<br/>\n" +
            "     * &#064;EnableTransactionManagement 注解等同于: &lt;tx:annotation-driven /&gt;\n" +
            "     *\n" +
            "     * @see org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration\n" +
            "     * @see org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration\n" +
            "     */\n" +
            "    @Bean\n" +
            "    @ConfigurationProperties(prefix = \"database\")\n" +
            "    public DataSource dataSource() {\n" +
            "        return DataSourceBuilder.create().type(DruidDataSource.class).build();\n" +
            "    }\n" +
            "\n" +
            "    @Bean\n" +
            "    public SqlSessionFactory sqlSessionFactory() throws Exception {\n" +
            "        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();\n" +
            "        sessionFactory.setDataSource(dataSource());\n" +
            "        // 装载 xml 实现\n" +
            "        sessionFactory.setMapperLocations(%sConfigData.RESOURCE_ARRAY);\n" +
            "        // 装载 handler 实现\n" +
            "        sessionFactory.setTypeHandlers(%sConfigData.HANDLER_ARRAY);\n" +
            "        // 插件\n" +
            "        sessionFactory.setPlugins(new Interceptor[] { mybatisPage() });\n" +
            "        return sessionFactory.getObject();\n" +
            "    }\n" +
            "\n" +
            "    /** 要构建 or 语句, 参考: http://www.mybatis.org/generator/generatedobjects/exampleClassUsage.html */\n" +
            "    @Bean(name = \"sqlSessionTemplate\", destroyMethod = \"clearCache\")\n" +
            "    public SqlSessionTemplate sqlSessionTemplate() throws Exception {\n" +
            "        return new SqlSessionTemplate(sqlSessionFactory());\n" +
            "    }\n" +
            "\n" +
            "    /*\n" +
            "     * 事务控制, 默认已经装载了\n" +
            "     *\n" +
            "     * @see DataSourceTransactionManagerAutoConfiguration\n" +
            "     */\n" +
            "    /*\n" +
            "    @Bean\n" +
            "    public PlatformTransactionManager transactionManager() {\n" +
            "        return new DataSourceTransactionManager(dataSource());\n" +
            "    }\n" +
            "    */\n" +
            "}\n";

    private static String SERVICE = "package " + PACKAGE + ".%s.service;\n" +
            "\n" +
            "import com.alibaba.dubbo.config.annotation.Service;\n" +
            "import " + PACKAGE + ".common.Const;\n" +
            "\n" +
            "/**\n" +
            " * <p>类上的注解相当于下面的配置</p>\n" +
            " *\n" +
            " * &lt;dubbo:service exported=\"false\" unexported=\"false\"\n" +
            " *     interface=\"" + PACKAGE + ".%s.service.%sService\"\n" +
            " *     listener=\"\" version=\"1.0\" filter=\"\" timeout=\"5000\"\n" +
            " *     id=\"" + PACKAGE + ".%s.service.%sService\" /&gt;\n" +
            " */\n" +
            "@Service(version = Const.DUBBO_VERSION, timeout = Const.DUBBO_TIMEOUT, filter = Const.DUBBO_FILTER)\n" +
            "public class %sServiceImpl implements %sService {\n" +
            "}\n";

    private static String APPLICATION_YML = "\n" +
            "online: false\n" +
            "\n" +
            "logging.config: classpath:log-dev.xml\n" +
            "\n" +
            "spring.application.name: %s\n" +
            "\n" +
            "database:\n" +
            "  url:  jdbc:mysql://127.0.0.1:3306/mall?useSSL=false&useUnicode=true&characterEncoding=utf8" +
            "&autoReconnect=true&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&statementInterceptors=" +
            PACKAGE + ".common.sql.ShowSqlInterceptor\n" +
            "  userName: root\n" +
            "  password: root\n" +
            "  initialSize: 1\n" +
            "  minIdle: 1\n" +
            "  maxActive: 1\n" +
            "  maxWait: 60000\n" +
            "  timeBetweenEvictionRunsMillis: 60000\n" +
            "  minEvictableIdleTimeMillis: 300000\n" +
            "  validationQuery: SELECT 'x'\n" +
            "  testWhileIdle: true\n" +
            "  testOnBorrow: false\n" +
            "  testOnReturn: false\n" +
            "  filters: stat,wall\n" +
            "\n" +
            "\n" +
            "spring.redis:\n" +
            "  host: 127.0.0.1\n" +
            "  port: 6379\n" +
            "\n" +
            "\n" +
            "# io.dubbo.springboot.DubboProperties\n" +
            "spring.dubbo:\n" +
            "  # 扫描 @Service @Reference 注解所在的目录. 目录如果有多个用 英文逗号(,) 隔开\n" +
            "  scan: " + PACKAGE + "\n" +
            "  # 只在服务端时需要下面的配置, 相当于这个配置: <dubbo:protocol name=\"dubbo\" port=\"xx\" serialization=\"kryo\" />\n" +
            "  protocol:\n" +
            "    # 序列化方式如果想使用 kryo, 需要引入 kryo 和 kryo-serializers 包, 见项目的 pom.xml 中的注释部分, 且需要 dubbox\n" +
            "    # serialization: kryo\n" +
            "    name: dubbo\n" +
            "    port: %s\n" +
            "  application:\n" +
            "    # <dubbo:application name=\"p-s\" id=\"p-s\" />\n" +
            "    name: ${spring.application.name}\n" +
            "    registry:\n" +
            "      address: zookeeper://127.0.0.1:2181\n" +
            "      timeout: 10000\n" +
            "    # 如果 zk 有集群, 去掉上面, 解开下面即可, 上面的方式只是基于一个 zk 实例\n" +
            "#    registries:\n" +
            "#      -\n" +
            "#        address: zookeeper://127.0.0.1:2181\n" +
            "#        timeout: 10000\n" +
            "#      -\n" +
            "#        address: zookeeper://192.168.0.124:2181\n" +
            "#        timeout: 1000\n" +
            "#\n";

    private static String APPLICATION_TEST_YML = "\n" +
            "online: false\n" +
            "\n" +
            "logging.config: classpath:log-test.xml\n" +
            "\n" +
            "spring.application.name: %s\n" +
            "\n" +
            "database:\n" +
            "  url:  jdbc:mysql://test_ip:test_port/x123x?useSSL=false&useUnicode=true&characterEncoding=utf8" +
            "&autoReconnect=true&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&statementInterceptors=" +
            PACKAGE + ".common.sql.ShowSqlInterceptor\n" +
            "  userName: xx123xx\n" +
            "  password: xxx123xxx\n" +
            "  initialSize: 2\n" +
            "  minIdle: 2\n" +
            "  maxActive: 10\n" +
            "  maxWait: 60000\n" +
            "  timeBetweenEvictionRunsMillis: 60000\n" +
            "  minEvictableIdleTimeMillis: 300000\n" +
            "  validationQuery: SELECT 'x'\n" +
            "  testWhileIdle: true\n" +
            "  testOnBorrow: false\n" +
            "  testOnReturn: false\n" +
            "  filters: stat,wall\n" +
            "\n" +
            "spring.redis:\n" +
            "  host: 127.0.0.1\n" +
            "  port: 6379\n" +
            "\n" +
            "spring.dubbo:\n" +
            "  scan: " + PACKAGE + "\n" +
            "  protocol:\n" +
            "    name: dubbo\n" +
            "    port: %s\n" +
            "  application:\n" +
            "    name: ${spring.application.name}\n" +
            "    registry:\n" +
            "      address: zookeeper://127.0.0.1:2181\n" +
            "      timeout: 10000\n";

    private static String APPLICATION_PROD_YML = "\n" +
            "online: true\n" +
            "\n" +
            "logging.config: classpath:log-prod.xml\n" +
            "\n" +
            "spring.application.name: %s\n" +
            "\n" +
            "database:\n" +
            "  url:  jdbc:mysql://prod_ip:prod_port/y123y?useSSL=false&useUnicode=true&characterEncoding=utf8" +
            "&autoReconnect=true&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true\n" +
            "  userName: yy123yy\n" +
            "  password: yyy123yyy\n" +
            "  initialSize: 10\n" +
            "  minIdle: 10\n" +
            "  maxActive: 100\n" +
            "  maxWait: 60000\n" +
            "  timeBetweenEvictionRunsMillis: 60000\n" +
            "  minEvictableIdleTimeMillis: 300000\n" +
            "  validationQuery: SELECT 'x'\n" +
            "  testWhileIdle: true\n" +
            "  testOnBorrow: false\n" +
            "  testOnReturn: false\n" +
            "  filters: wall\n" +
            "\n" +
            "spring.redis:\n" +
            "  host: 127.0.0.1\n" +
            "  port: 6379\n" +
            "\n" +
            "spring.dubbo:\n" +
            "  scan: " + PACKAGE + "\n" +
            "  protocol:\n" +
            "    name: dubbo\n" +
            "    port: %s\n" +
            "  application:\n" +
            "    name: ${spring.application.name}\n" +
            "    registries:\n" +
            "      -\n" +
            "        address: zookeeper://10.10.10.5:2181\n" +
            "        timeout: 10000\n" +
            "      -\n" +
            "        address: zookeeper://10.10.10.6:2181\n" +
            "        timeout: 10000\n";

    private static String LOG_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<configuration>\n" +
            "    <include resource=\"org/springframework/boot/logging/logback/defaults.xml\" />\n" +
            "    <property name=\"CONSOLE_LOG_PATTERN\" value=\"[%X{receiveTime}%d] [${PID:- } %t\\\\(%logger\\\\) : %p]%X{requestInfo}%n%class.%method\\\\(%file:%line\\\\)%n%m%n%n\"/>\n" +
            "    <include resource=\"org/springframework/boot/logging/logback/console-appender.xml\" />\n" +
            "\n\n" +
            "    <logger name=\"" + PACKAGE + ".~MODULE_NAME~.repository\" level=\"warn\"/>\n" +
            "    <logger name=\"" + PACKAGE + ".common.mvc\" level=\"warn\"/>\n" +
            "    <!--<logger name=\"com.github\" level=\"warn\"/>-->\n" +
            "    <logger name=\"com.alibaba\" level=\"warn\"/>\n" +
            "    <logger name=\"com.sun\" level=\"warn\"/>\n" +
            "\n" +
            "    <logger name=\"io.github\" level=\"warn\"/>\n" +
            "\n" +
            "    <logger name=\"org.springframework\" level=\"warn\"/>\n" +
            "    <logger name=\"org.hibernate\" level=\"warn\"/>\n" +
            "    <logger name=\"org.mybatis\" level=\"warn\"/>\n" +
            "    <logger name=\"org.apache\" level=\"warn\"/>\n" +
            "    <logger name=\"org.I0Itec\" level=\"warn\"/>\n" +
            "    <logger name=\"org.jboss\" level=\"warn\"/>\n" +
            "\n\n" +
            "    <root level=\"debug\">\n" +
            "        <appender-ref ref=\"CONSOLE\"/>\n" +
            "    </root>\n" +
            "</configuration>\n";
    private static String LOG_TEST_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<configuration>\n" +
            "    <property name=\"FILE_PATH\" value=\"${user.home}/logs/~MODULE_NAME~-test\"/>\n" +
            "    <property name=\"SQL_PATTERN\" value=\"%d [${PID:- } %t\\\\(%logger\\\\) : %p] %class.%method\\\\(%file:%line\\\\)%n%m%n%n\"/>\n" +
            "    <property name=\"LOG_PATTERN\" value=\"[%X{receiveTime}%d] [${PID:- } %t\\\\(%logger\\\\) : %p]%X{requestInfo} %class{30}#%method\\\\(%file:%line\\\\)%n%m%n%n\"/>\n" +
            "\n" +
            "    <appender name=\"PROJECT\" class=\"ch.qos.logback.core.rolling.RollingFileAppender\">\n" +
            "        <file>${FILE_PATH}.log</file>\n" +
            "        <rollingPolicy class=\"ch.qos.logback.core.rolling.TimeBasedRollingPolicy\">\n" +
            "            <fileNamePattern>${FILE_PATH}-%d{yyyy-MM-dd}.log</fileNamePattern>\n" +
            "            <maxHistory>7</maxHistory>\n" +
            "        </rollingPolicy>\n" +
            "        <encoder>\n" +
            "            <pattern>${LOG_PATTERN}</pattern>\n" +
            "        </encoder>\n" +
            "    </appender>\n" +
            "\n" +
            "    <appender name=\"SQL\" class=\"ch.qos.logback.core.rolling.RollingFileAppender\">\n" +
            "        <file>${FILE_PATH}-sql.log</file>\n" +
            "        <rollingPolicy class=\"ch.qos.logback.core.rolling.TimeBasedRollingPolicy\">\n" +
            "            <fileNamePattern>${FILE_PATH}-sql-%d{yyyy-MM-dd}.log</fileNamePattern>\n" +
            "            <maxHistory>7</maxHistory>\n" +
            "        </rollingPolicy>\n" +
            "        <encoder>\n" +
            "            <pattern>${SQL_PATTERN}</pattern>\n" +
            "        </encoder>\n" +
            "    </appender>\n" +
            "\n\n" +
            "    <logger name=\"" + PACKAGE + ".~MODULE_NAME~.repository\" level=\"warn\"/>\n" +
            "    <logger name=\"" + PACKAGE + ".common.mvc\" level=\"warn\"/>\n" +
            "    <!--<logger name=\"com.github\" level=\"warn\"/>-->\n" +
            "    <logger name=\"com.alibaba\" level=\"warn\"/>\n" +
            "    <logger name=\"com.sun\" level=\"warn\"/>\n" +
            "\n" +
            "    <logger name=\"io.github\" level=\"warn\"/>\n" +
            "\n" +
            "    <logger name=\"org.springframework\" level=\"warn\"/>\n" +
            "    <logger name=\"org.hibernate\" level=\"warn\"/>\n" +
            "    <logger name=\"org.mybatis\" level=\"warn\"/>\n" +
            "    <logger name=\"org.apache\" level=\"warn\"/>\n" +
            "    <logger name=\"org.I0Itec\" level=\"warn\"/>\n" +
            "    <logger name=\"org.jboss\" level=\"warn\"/>\n" +
            "\n\n" +
            "    <logger name=\"sqlLog\" level=\"debug\" additivity=\"false\">\n" +
            "        <appender-ref ref=\"SQL\" />\n" +
            "    </logger>\n" +
            "\n" +
            "    <root level=\"debug\">\n" +
            "        <appender-ref ref=\"PROJECT\"/>\n" +
            "    </root>\n" +
            "</configuration>\n";
    private static String LOG_PROD_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<configuration>\n" +
            "    <property name=\"FILE_PATH\" value=\"${user.home}/logs/~MODULE_NAME~-prod\"/>\n" +
            "    <property name=\"LOG_PATTERN\" value=\"[%X{receiveTime}%d] [${PID:- } %t\\\\(%logger\\\\) : %p]%X{requestInfo} %class{30}#%method\\\\(%file:%line\\\\) %m%n%n\"/>\n" +
            "\n" +
            "    <appender name=\"PROJECT\" class=\"ch.qos.logback.core.rolling.RollingFileAppender\">\n" +
            "        <file>${FILE_PATH}.log</file>\n" +
            "        <rollingPolicy class=\"ch.qos.logback.core.rolling.TimeBasedRollingPolicy\">\n" +
            "            <fileNamePattern>${FILE_PATH}-%d{yyyy-MM-dd}.log</fileNamePattern>\n" +
            "            <maxHistory>60</maxHistory>\n" +
            "        </rollingPolicy>\n" +
            "        <encoder>\n" +
            "            <pattern>${LOG_PATTERN}</pattern>\n" +
            "        </encoder>\n" +
            "    </appender>\n" +
            "\n" +
            "    <appender name=\"ASYNC\" class=\"ch.qos.logback.classic.AsyncAppender\">\n" +
            "        <discardingThreshold>0</discardingThreshold>\n" +
            "        <includeCallerData>true</includeCallerData>\n" +
            "        <appender-ref ref =\"PROJECT\"/>\n" +
            "    </appender>\n" +
            "\n\n" +
            "    <logger name=\"" + PACKAGE + ".~MODULE_NAME~.repository\" level=\"warn\"/>\n" +
            "    <logger name=\"" + PACKAGE + ".common.mvc\" level=\"warn\"/>\n" +
            "    <!--<logger name=\"com.github\" level=\"warn\"/>-->\n" +
            "    <logger name=\"com.alibaba\" level=\"warn\"/>\n" +
            "    <logger name=\"com.sun\" level=\"warn\"/>\n" +
            "\n" +
            "    <logger name=\"io.github\" level=\"warn\"/>\n" +
            "\n" +
            "    <logger name=\"org.springframework\" level=\"warn\"/>\n" +
            "    <logger name=\"org.hibernate\" level=\"warn\"/>\n" +
            "    <logger name=\"org.mybatis\" level=\"warn\"/>\n" +
            "    <logger name=\"org.apache\" level=\"warn\"/>\n" +
            "    <logger name=\"org.I0Itec\" level=\"warn\"/>\n" +
            "    <logger name=\"org.jboss\" level=\"warn\"/>\n" +
            "\n\n" +
            "    <root level=\"info\">\n" +
            "        <appender-ref ref=\"ASYNC\"/>\n" +
            "    </root>\n" +
            "</configuration>\n";


    private static String SERVER_POM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
            "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
            "    <parent>\n" +
            "        <artifactId>%s</artifactId>\n" +
            "        <groupId>com.github</groupId>\n" +
            "        <version>1.0-SNAPSHOT</version>\n" +
            "    </parent>\n" +
            "    <modelVersion>4.0.0</modelVersion>\n" +
            "\n" +
            "    <artifactId>%s</artifactId>\n" +
            "    <description>%s模块的服务端</description>" +
            "\n" +
            "    <dependencies>\n" +
            "        <dependency>\n" +
            "            <groupId>${project.groupId}</groupId>\n" +
            "            <artifactId>erp-common</artifactId>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>${project.groupId}</groupId>\n" +
            "            <artifactId>erp-global</artifactId>\n" +
            "        </dependency>\n" +
            "\n" +
            "        <dependency>\n" +
            "            <groupId>${project.groupId}</groupId>\n" +
            "            <artifactId>%s</artifactId>\n" +
            "        </dependency>\n" +
            "\n" +
            "        <dependency>\n" +
            "            <groupId>org.springframework.boot</groupId>\n" +
            "            <artifactId>spring-boot-starter</artifactId>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>org.springframework</groupId>\n" +
            "            <artifactId>spring-jdbc</artifactId>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>org.springframework.boot</groupId>\n" +
            "            <artifactId>spring-boot-starter-data-redis</artifactId>\n" +
            "        </dependency>\n" +
            "\n" +
            "        <dependency>\n" +
            "            <groupId>mysql</groupId>\n" +
            "            <artifactId>mysql-connector-java</artifactId>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>com.alibaba</groupId>\n" +
            "            <artifactId>druid</artifactId>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>org.mybatis</groupId>\n" +
            "            <artifactId>mybatis</artifactId>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>org.mybatis</groupId>\n" +
            "            <artifactId>mybatis-spring</artifactId>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>com.github.liuanxin</groupId>\n" +
            "            <artifactId>mybatis-page</artifactId>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>com.github.liuanxin</groupId>\n" +
            "            <artifactId>mybatis-redis-cache</artifactId>\n" +
            "        </dependency>\n" +
            "\n" +
            "        <dependency>\n" +
            "            <groupId>com.101tec</groupId>\n" +
            "            <artifactId>zkclient</artifactId>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>com.alibaba</groupId>\n" +
            "            <artifactId>dubbo</artifactId>\n" +
            "        </dependency>\n" +
            "        <!--\n" +
            "        <dependency>\n" +
            "            <groupId>com.esotericsoftware.kryo</groupId>\n" +
            "            <artifactId>kryo</artifactId>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>de.javakaffee</groupId>\n" +
            "            <artifactId>kryo-serializers</artifactId>\n" +
            "        </dependency>\n" +
            "        -->\n" +
            "        <dependency>\n" +
            "            <groupId>io.dubbo.springboot</groupId>\n" +
            "            <artifactId>spring-boot-starter-dubbo</artifactId>\n" +
            "        </dependency>\n" +
            "    </dependencies>\n" +
            "\n" +
            "    <build>\n" +
            "        <finalName>%s</finalName>\n" +
            "        <plugins>\n" +
            "            <plugin>\n" +
            "                <groupId>org.springframework.boot</groupId>\n" +
            "                <artifactId>spring-boot-maven-plugin</artifactId>\n" +
            "            </plugin>\n" +
            "        </plugins>\n" +
            "    </build>\n" +
            "</project>\n";

    private static String TEST_LOGBACK = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<configuration>\n" +
            "\n" +
            "    <include resource=\"org/springframework/boot/logging/logback/defaults.xml\" />\n" +
            "    <property name=\"CONSOLE_LOG_PATTERN\" value=\"%d [%t\\\\(%logger\\\\) : %p] %class.%method\\\\(%file:%line\\\\)%n%m%n%n\"/>\n" +
            "    <include resource=\"org/springframework/boot/logging/logback/console-appender.xml\" />\n" +
            "\n" +
            "    <logger name=\"org.springframework\" level=\"warn\"/>\n" +
            "    <logger name=\"org.hibernate\" level=\"warn\"/>\n" +
            "    <logger name=\"com.alibaba\" level=\"error\"/>\n" +
            "    <logger name=\"com.netflix\" level=\"warn\"/>\n" +
            "    <logger name=\"org.mybatis\" level=\"warn\"/>\n" +
            "    <logger name=\"freemarker\" level=\"warn\"/>\n" +
            "    <logger name=\"com.github\" level=\"warn\"/>\n" +
            "    <logger name=\"org.apache\" level=\"warn\"/>\n" +
            "    <logger name=\"org.I0Itec\" level=\"warn\"/>\n" +
            "    <logger name=\"org.jboss\" level=\"warn\"/>\n" +
            "    <logger name=\"io.github\" level=\"warn\"/>" +
            "\n" +
            "    <root level=\"debug\">\n" +
            "        <appender-ref ref=\"CONSOLE\"/>\n" +
            "    </root>\n" +
            "</configuration>\n";
    private static String TEST_ENUM_HANDLE = "package " + PACKAGE + ".%s;\n" +
            "\n" +
            "import " + PACKAGE + ".common.Const;\n" +
            "import " + PACKAGE + ".common.util.GenerateEnumHandle;\n" +
            "import " + PACKAGE + ".%s.constant.%sConst;\n" +
            "import org.junit.Test;\n" +
            "\n" +
            "public class %sGenerateEnumHandle {\n" +
            "\n" +
            "    @Test\n" +
            "    public void generate() {\n" +
            "        GenerateEnumHandle.generateEnum(getClass(), Const.BASE_PACKAGE, %sConst.MODULE_NAME);\n" +
            "    }\n" +
            "}\n";

    static void generateServer(String moduleName, String packageName, String model,
                               String server, String module, String port, String comment) throws IOException {
        String parentPackageName = packageName.replace("-", ".");
        String clazzName = capitalize(parentPackageName);

        File serverPath = new File(module + "/" + server + "/src/main/java");
        serverPath.mkdirs();

        String serverPom = String.format(SERVER_POM, moduleName, server, comment, model, server + "-" + port);
        writeFile(new File(module + "/" + server, "pom.xml"), serverPom);

        File packagePath = new File(serverPath + "/" + PACKAGE_PATH);
        File sourcePath = new File(packagePath + "/" + parentPackageName.replaceAll("\\.", "/"));
        File configPath = new File(sourcePath, "config");
        File servicePath = new File(sourcePath, "service");
        configPath.mkdirs();
        servicePath.mkdirs();
        new File(sourcePath, "handler").mkdirs();
        new File(sourcePath, "repository").mkdirs();

        String application = String.format(APPLICATION, clazzName, clazzName);
        writeFile(new File(packagePath, clazzName + "Application.java"), application);

        String configData = String.format(CONFIG_DATA, parentPackageName, parentPackageName, clazzName, comment,
                clazzName, clazzName, clazzName, clazzName, clazzName, clazzName);
        writeFile(new File(configPath, clazzName + "ConfigData.java"), configData);

        String dataSource = String.format(DATA_SOURCE, parentPackageName, clazzName, clazzName, clazzName);
        writeFile(new File(configPath, clazzName + "DataSourceInit.java"), dataSource);

        String service = String.format(SERVICE, parentPackageName, parentPackageName,
                clazzName, parentPackageName, clazzName, clazzName, clazzName);
        writeFile(new File(servicePath, clazzName + "ServiceImpl.java"), service);


        File resourcePath = new File(module + "/" + server + "/src/main/resources");
        resourcePath.mkdirs();

        String applicationYml = String.format(APPLICATION_YML, packageName, port);
        writeFile(new File(resourcePath, "application.yml"), applicationYml);
        String applicationTestYml = String.format(APPLICATION_TEST_YML, packageName, port);
        writeFile(new File(resourcePath, "application-test.yml"), applicationTestYml);
        String applicationProdYml = String.format(APPLICATION_PROD_YML,  packageName, port);
        writeFile(new File(resourcePath, "application-prod.yml"), applicationProdYml);

        String logXml = LOG_XML.replaceAll("~MODULE_NAME~", parentPackageName);
        writeFile(new File(resourcePath, "log-dev.xml"), logXml);
        String testXml = LOG_TEST_XML.replaceAll("~MODULE_NAME~", parentPackageName);
        writeFile(new File(resourcePath, "log-test.xml"), testXml);
        String prodXml = LOG_PROD_XML.replaceAll("~MODULE_NAME~", parentPackageName);
        writeFile(new File(resourcePath, "log-prod.xml"), prodXml);


        File testParent = new File(module + "/" + server + "/src/test/java/" +
                PACKAGE_PATH + "/" + parentPackageName.replace('.', '/'));
        testParent.mkdirs();

        File testResource = new File(module + "/" + server + "/src/test/resources");
        testResource.mkdirs();
        writeFile(new File(testResource, "logback.xml"), TEST_LOGBACK);
        writeFile(new File(testResource, packageName + ".sql"), "");

        String test = String.format(TEST_ENUM_HANDLE, parentPackageName,
                parentPackageName, clazzName, clazzName, clazzName);
        writeFile(new File(testParent, clazzName + "GenerateEnumHandle.java"), test);
    }
}
