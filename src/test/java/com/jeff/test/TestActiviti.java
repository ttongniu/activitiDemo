package com.jeff.test;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;

public class TestActiviti {
	       @SuppressWarnings("static-access")
	       //代码创建23张表
		  @Test
          public void createTable(){
        	 ProcessEngineConfiguration processEngineConfiguration= ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
        	 processEngineConfiguration.setJdbcDriver("com.mysql.jdbc.Driver");
        	 processEngineConfiguration.setJdbcUrl("jdbc:mysql://localhost:3306/test_new?useUnicode=true&amp;characterEncoding=utf8");
        	 processEngineConfiguration.setJdbcUsername("root");
        	 processEngineConfiguration.setJdbcPassword("root");
        	 
        	 processEngineConfiguration.setDatabaseSchemaUpdate(processEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        	  ProcessEngine processEngine= processEngineConfiguration.buildProcessEngine();
        	 System.out.println(processEngine);
          }
	       //配置文件创建表
	       @Test
	       public void createTable2(){
	    	   ProcessEngine processEngine=ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
	    	   System.out.println(processEngine);
	       }
	       
}
