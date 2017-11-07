
## 商城项目

将下面的内容保存成 settings.xml 并放到 ${user.home}/.m2 目录下, 如果想要改变仓库地址, 注释解开并设置即可
```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <!-- <localRepository>仓库地址. 默认是 ${user.home}/.m2/repository </localRepository> -->
    
    <mirrors>
        <mirror>
            <id>aliyun</id>
            <name>aliyun maven</name>
            <mirrorOf>central</mirrorOf>
            <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
        </mirror>
    </mirrors>

    <profiles>
        <profile>
            <id>company</id>
            <repositories>
                <repository>
                    <id>aliyun-repository</id>
                    <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
                    <releases><enabled>true</enabled></releases>
                    <snapshots><enabled>false</enabled></snapshots>
                </repository>
            </repositories>
            <pluginRepositories>
                <pluginRepository>
                    <id>aliyun-plugin</id>
                    <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
                    <releases><enabled>true</enabled></releases>
                    <snapshots><enabled>false</enabled></snapshots>
                </pluginRepository>
            </pluginRepositories>
        </profile>
    </profiles>

    <activeProfiles>
        <activeProfile>company</activeProfile>
    </activeProfiles>
</settings>
```
