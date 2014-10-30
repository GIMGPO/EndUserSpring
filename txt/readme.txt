1. copy mysql-connector-java-5.1.19-bin.jar to $JBOSS_INSTALL_DIR/standalone/deployments
2. copy commons-lang3-3.0.jar to $JBOSS_INSTALL_DIR/standalone/lib/ext
3. visit http://localhost:8080, click on "Administration Console", login, click "Profile" in the upper right corner
   create datasource 
   name: jdbc/tdp_ghe_ext_test
   JNDI: java:jboss/jdbc/tdp_ghe_ext_test
   
   
 