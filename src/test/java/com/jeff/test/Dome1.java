package com.jeff.test;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class Dome1 {
         ProcessEngine processEngine=null;
         RepositoryService repositoryService=null;
         RuntimeService runtimeService=null;
         TaskService taskService=null;
         HistoryService historyService=null;
         @Before
         public void init(){
        	 //创建ProcessEngine
        	  processEngine=ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        	  repositoryService=processEngine.getRepositoryService();
        	  runtimeService=processEngine.getRuntimeService();
        	  taskService=processEngine.getTaskService();
        	  historyService=processEngine.getHistoryService();
         }
         /**
          * 部署流程定义
          */
         @Test
         public void deploy(){
        	 InputStream inputStreamBpmn = this.getClass().getResourceAsStream("/diagrams/demo1.bpmn");  
             InputStream inputStreamPng = this.getClass().getResourceAsStream("/diagrams/demo1.png");  
             Deployment deployment = processEngine.getRepositoryService()//与流程定义和部署对象相关的Service  
                             .createDeployment()//创建一个部署对象  
                             .name("ntt任务v1.0")//添加部署的名称  
                             .addInputStream("diagrams/demo1.bpmn", inputStreamBpmn)//  
                             .addInputStream("diagrams/demo1.png", inputStreamPng)//  
                             .deploy();//完成部署  
             System.out.println("部署ID："+deployment.getId());//  
             System.out.println("部署名称："+deployment.getName());//  
         }
         /**
          * 启动一个请假流程
          */
         @Test
         public void start(){
        	//流程定义的key  
             String processDefinitionKey = "demo1";  
             /**启动流程实例的同时，设置流程变量，使用流程变量用来指定任务的办理人，对应task.pbmn文件中#{userID}*/  
             Map<String, Object> variables = new HashMap<String, Object>();  
             variables.put("userId", "niutongtong");  //
             variables.put("userId", "niutongtong2"); 
             ProcessInstance pi = processEngine.getRuntimeService()//与正在执行的流程实例和执行对象相关的Service  
                             .startProcessInstanceByKey(processDefinitionKey,variables);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动  
             System.out.println("流程实例ID:"+pi.getId());//流程实例ID      
             System.out.println("流程定义ID:"+pi.getProcessDefinitionId());//流程定义ID   
         }
         
         
         /**个人任务待办查询*/
         @Test
         public void  findTask(){
        	 String assignee = "niutongtong2";  
             List<Task> list = processEngine.getTaskService()//与正在执行的任务管理相关的Service  
                             .createTaskQuery()//创建任务查询对象  
                             /**查询条件（where部分）*/  
                             .taskAssignee(assignee)//指定个人任务查询，指定办理人  
                             /**排序*/  
                             .orderByTaskCreateTime().asc()//使用创建时间的升序排列  
                             /**返回结果集*/  
                             .list();//返回列表  
             if(list!=null && list.size()>0){  
                 for(Task task:list){  
                     System.out.println("任务ID:"+task.getId());  
                     System.out.println("任务名称:"+task.getName());  
                     System.out.println("任务的创建时间:"+task.getCreateTime());  
                     System.out.println("任务的办理人:"+task.getAssignee());  
                     System.out.println("流程实例ID："+task.getProcessInstanceId());  
                     System.out.println("执行对象ID:"+task.getExecutionId());  
                     System.out.println("流程定义ID:"+task.getProcessDefinitionId());  
                     System.out.println("########################################################"); 
                     
                     
                 }  
             }  
        	 
         }
         
         /**
          * 查询正在执行的任务办里人表
          */
         @Test
         public void findRunPersonTask(){
           ProcessInstance	pi= runtimeService.createProcessInstanceQuery()
        	 .processInstanceId("10001")
        	 .singleResult();
           if(pi!=null){
        	   //  System.out.println(pi.getBusinessKey());
               System.out.println(pi.getId());
               System.out.println(pi.getName());
               System.out.println(pi.getProcessDefinitionKey());
               System.out.println(pi.getProcessInstanceId());
           }
        
         }
        
         
         @Test
         public void completeMytask(){
        	  //任务ID  
             String taskId = "10005";  
             String IsAgree = "批准";  
             String IsNotAgree = "驳回";  
             String IsStudent = "";  
               
             //完成任务的同时，设置流程变量，使用流程变量用来指定完成任务后，下一个连线，对应sequenceFlow.bpmn文件中${message=='不重要'}  
             Map<String, Object> variables = new HashMap<String, Object>();  
             variables.put("outcome", IsAgree);  
             processEngine.getTaskService()//与正在执行的任务管理相关的Service  
                         .complete(taskId,variables);  
             System.out.println("完成任务：任务ID："+taskId);  
         }
        
       
           
        
       
}
