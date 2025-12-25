### 

### Build project with docker
```
docker build -t my-application .
```
After build finished copy org.wso2.carbon.apimgt.gateway-9.20.74.jar  to ${WSO2_SERVER_HOME}/repository/components/plugins/org.wso2.carbon.apimgt.gateway_9.20.74.jar

### Build local - using java 8 and maven
```
cd components/apimgt/org.wso2.carbon.apimgt.gateway
mvn build
```
After build finish copy jar file under target folder to ${WSO2_SERVER_HOME}/repository/components/plugins/org.wso2.carbon.apimgt.gateway_9.20.74.jar