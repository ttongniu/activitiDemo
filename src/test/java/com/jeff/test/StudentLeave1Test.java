package com.jeff.test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;

public class StudentLeave1Test {

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
   	 InputStream inputStreamBpmn = this.getClass().getResourceAsStream("/diagrams/studentLeave2.bpmn");  
        InputStream inputStreamPng = this.getClass().getResourceAsStream("/diagrams/studentLeave2.png");  
        Deployment deployment = processEngine.getRepositoryService()//与流程定义和部署对象相关的Service  
                        .createDeployment()//创建一个部署对象  
                        .name("测试并行网管")//添加部署的名称  
                        .addInputStream("diagrams/studentLeave2.bpmn", inputStreamBpmn)//  
                        .addInputStream("diagrams/studentLeave2.png", inputStreamPng)//  
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
        String processDefinitionKey = "studentLeave2";  
        /**启动流程实例的同时，设置流程变量，使用流程变量用来指定任务的办理人，对应task.pbmn文件中#{userID}*/  
        Map<String, Object> variables = new HashMap<String, Object>();  
        variables.put("userId", "niutongtong");  //
        variables.put("userId", "niutongtong2"); 
        ProcessInstance pi = processEngine.getRuntimeService()//与正在执行的流程实例和执行对象相关的Service  
                        .startProcessInstanceByKey(processDefinitionKey,variables);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动  
        System.out.println("流程实例ID:"+pi.getId());//流程实例ID      
        System.out.println("流程定义ID:"+pi.getProcessDefinitionId());//流程定义ID   
    }
    /**
     * 查看任务
     */
    @Test
    public void findTask(){
        List<Task> taskList=processEngine.getTaskService() // 任务相关Service
            .createTaskQuery() // 创建任务查询
            .taskAssignee("aaa") // 指定某个人
            .list();
        for(Task task:taskList){
            System.out.println("任务ID:"+task.getId()); 
            System.out.println("任务名称:"+task.getName());
            System.out.println("任务创建时间:"+task.getCreateTime());
            System.out.println("任务委派人:"+task.getAssignee());
            System.out.println("流程实例ID:"+task.getProcessInstanceId());
        }
    }
     
     
    /**
     * 完成任务
     */
    @Test
    public void completeTask(){
        processEngine.getTaskService() // 任务相关Service
            .complete("192503");
    }
     
    @Test
    public void completeTask2(){
 
        Map<String, Object> variables=new HashMap<String,Object>();
        variables.put("days", 4);
        processEngine.getTaskService() // 任务相关Service
            .complete("175005", variables); //完成任务的时候，设置流程变量
    }
     
 

}
