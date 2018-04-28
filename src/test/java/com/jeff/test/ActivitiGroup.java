package com.jeff.test;

import java.io.File;
import java.io.InputStream;
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

public class ActivitiGroup {
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
        	 Deployment deployment=repositoryService.createDeployment().addClasspathResource("diagrams/helloGroup.bpmn").name("组任务分配").deploy();
        	 String id=  deployment.getId();
        	 System.out.println("**** 流程部署定义完成 *****"+id+deployment.getName());
         }
         /**
          * 启动一个请假流程
          */
         @Test
         public void start(){
        	 //for(int i=0;i<5;i++){
        		 String processId=runtimeService.startProcessInstanceByKey("helloGroup").getId();
        		 System.out.println("****************启动第请假流程完成****************"+processId);
        	// }
         }
         
         @Test
         public void  findTask(){
        	 List<Task> list=	 taskService.createTaskQuery().taskCandidateUser("小花").list();
        	 if(!list.isEmpty()){
        		 for(Task li:list){
        			 System.out.println(li.getAssignee());
        			 System.out.println(li.getName());
        			 System.out.println(li.getId());
        		 }
        	 }
        	 
         }
         /**
          * 查询正在执行的任务办里人表
          */
         @Test
         public void findRunPersonTask(){
        	 String taskId="65004";
        	 List<IdentityLink> list= taskService.getIdentityLinksForTask(taskId);
        	 if(!list.isEmpty()){
        		 for(IdentityLink link:list){
        			 System.out.println(link.getTaskId()+"   "+link.getType()+"   "+link.getProcessInstanceId()+"  "+link.getUserId());
        		 }
        	 }
         }
         
         /**
          * 查询历史任务的办里人表
          */
         @Test
         public void  findHistoryPersonTask(){
        	 String processInstanceId="65001";
        	 List<HistoricIdentityLink> list=historyService.getHistoricIdentityLinksForProcessInstance(processInstanceId);
        	 if(!list.isEmpty()){
        		 for(HistoricIdentityLink link:list){
        			 System.out.println(link.getTaskId()+"   "+link.getType()+"   "+link.getProcessInstanceId()+"  "+link.getUserId());
        		 }
        	 } 
         }
         
          /**
           * 拾取任务  将组任务分给个人任务   (给小a也可以不是组内的人 如：郭靖)
           */
           @Test
         public void  claim(){
        	 String taskId="65004";
        	 String Assignee="郭靖";
          
        	 taskService.claim(taskId, Assignee);
        	 System.out.println("拾取"+taskId+"任务成功");
         } 
         /**
          * 回退任务    个人任务回退到组任务  （前提原本要是组任务）
          */
           
           public void  setAssignee(){
          	 String taskId="65004";
          	 String Assignee=null;
          	 taskService.setAssignee(taskId, Assignee);
          	 System.out.println("回退"+taskId+"任务成功");
           } 
           
         /**
          * 向组任务中添加成员
          */
           @Test
           public void addUser(){
        	   String taskId="65004";
            	 String userId="大f";
            	 taskService.addCandidateUser(taskId, userId);
            	 System.out.println("成员添加成功");
            	 
           }
           
           /**
            * 从组任务中删除成员
            */
           @Test
             public void deleteUser(){
            	 String taskId="65004";
              	 String userId="小b";
              	 taskService.deleteCandidateUser(taskId, userId);
             }
           
      
}
