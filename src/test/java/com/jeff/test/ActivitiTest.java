package com.jeff.test;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class ActivitiTest {
         ProcessEngine processEngine=null;
         RepositoryService repositoryService=null;
         RuntimeService runtimeService=null;
         TaskService taskService=null;
         
         @Before
         public void init(){
        	 //创建ProcessEngine
        	  processEngine=ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        	  repositoryService=processEngine.getRepositoryService();
        	  runtimeService=processEngine.getRuntimeService();
        	  taskService=processEngine.getTaskService();
         }
         /**
          * 部署流程定义
          */
         @Test
         public void deploy(){
        	 Deployment deployment=repositoryService.createDeployment()//
        			 .addClasspathResource("diagrams/MyProcess1.bpmn")//
        			 .name("2017请假流程")//
        			 .deploy();
        	 String id=  deployment.getId();
        	 String name= deployment.getName();
        	 System.out.println("**** 流程部署定义完成 *****"+id+"名称为："+name);
         }
         /**
          * 启动一个请假流程
          */
         @Test
         public void start(){
        	 for(int i=0;i<2;i++){
        		 String processId=runtimeService.startProcessInstanceByKey("myProcess").getId();
        		 System.out.println("****************启动第"+i+"个请假流程完成****************"+processId);
        	 }
         }
                
         /**
          * 启动一个请假流程--流程与业务相关联。
          */
         @Test
         public void startwithItem(){
        	 
        	     String  objId="myProcess.1";
        	     Map<String,Object>  variables=new HashMap<String,Object>();
        	     variables.put("creater", "niutongtong");
        	     variables.put("objId", "myProcess.1");
        		 String processId=runtimeService.startProcessInstanceByKey("myProcess",objId,variables).getId();
        		 
        		 System.out.println("****************启动请假流程完成****************"+processId);
        	 
         }
         /**
          * 流程第一步
          */
         @Test
         public void step1(){
        	 System.out.println("******step1流程开始*******");
        	 List<Task> tasks=taskService.createTaskQuery().taskId("2506").list();
        	
        	 //List<Task> tasks2=taskService.createTaskQuery().taskAssignee("李四").list();//李四的任务
        	 System.out.println(tasks.size()+"张三任务");
        	// System.out.println(tasks2.size()+"李四任务");
        	 
        	 for(Task task:tasks){
        		 System.out.println("张三的任务taskName:"+task.getName()+",id"+task.getId());
        		 System.out.println("任务拥有者:"+task.getAssignee());
        		 System.out.println("流程实例ID"+task.getProcessInstanceId());
        		 System.out.println("流程定义ID"+task.getProcessDefinitionId());
        		  taskService.claim(task.getId(), "niutongtong");
        		  taskService.complete(task.getId());
        	 }
        	 /*
        	 System.out.println("张三的任务数量:"+taskService.createTaskQuery().taskAssignee("张三").count());
        	 System.out.println("*********step1流程结束************");
        	 */
         }
         @Test
         @SuppressWarnings({ "deprecation", "unused" })
		public  void  Todo(){
        	// String user="李四"; 
        	 List<Task> tasks =  taskService.createTaskQuery().executionId("2501").list();
        	if(tasks.size()==0&&tasks==null){
        	 for(Task task:tasks){
        		 System.out.println("任务taskName:"+task.getName()+",id"+task.getId());
        		 System.out.println("任务拥有者:"+task.getAssignee());
        		 System.out.println("流程实例ID"+task.getProcessInstanceId());
        		 System.out.println("流程定义ID"+task.getProcessDefinitionId());
        		 System.out.println("任务描述"+task.getDescription());
        		 System.out.println("任务发起人"+task.getOwner());
        		 System.err.println("流程实例key"+task.getTaskDefinitionKey());
        		 System.out.println("创建时间"+task.getCreateTime());
        		 System.out.println("due时间"+task.getDueDate());
        		 System.out.println("状态："+task.getDelegationState());
        		 taskService.claim(task.getId(), "tongtongniu");
        		 taskService.complete(task.getId());
        		 //System.out.println("张三的任务taskName:"+task.getName()+",id"+task.getId()+"已经完成！！！");
        	 }
        	 }else{
        		System.out.println("无待办任务！！！"); 
        	 }
         }
         
         
        //查询待办事宜个数
          @Test
        public  void  TodoNum(){
        	 String user="李四"; 
        	 List<Task> tasks =  taskService.createTaskQuery().taskAssignee(user).list();
        	 List<Task> groupTasks =  taskService.createTaskQuery().taskCandidateUser(user).list();
        	 tasks.addAll(groupTasks);
        	 
             System.out.println("待办事宜个数"+tasks.size());
        	
        }
         
         @Test
         public void step2(){
        	 System.out.println("******step2流程开始***********");
        	 List<Task> tasks= taskService.createTaskQuery().taskCandidateUser("张三").list();
        	 List<Task> tasks2=taskService.createTaskQuery().taskCandidateUser("李四").list();
        	 System.out.println("张三可领的任务数量:"+tasks.size());
        	 System.out.println("李四可领的任务数量:"+tasks2.size());
        	 
        	 for(int i=0;i<tasks.size();i++){
        		 Task task=tasks.get(i);
        		 System.out.println("任务名称："+task.getName()+",id为"+task.getId());
        		 //领取任务
        		 if(i%2==0){
        			 taskService.claim(task.getId(), "张三"); //张三领取任务
        			 
        		 }else{
        			 taskService.claim(task.getId(), "李四");//李四领取任务
        		 }
        	 }
        	 
        	 tasks= taskService.createTaskQuery().taskAssignee("张三").list();
        	 tasks2=taskService.createTaskQuery().taskAssignee("李四").list();
        	 System.out.println("张三已领的任务数量:"+tasks.size());
        	 System.out.println("李四已领的任务数量:"+tasks2.size());
        	 //完成任务
        	 for(Task task:tasks){
        		 taskService.complete(task.getId());
        	 }
        	 
        	 for(Task task:tasks2){
        		 taskService.complete(task.getId());
        	 }
        	 
        	 
        	 tasks= taskService.createTaskQuery().taskAssignee("张三").list();
        	 tasks2=taskService.createTaskQuery().taskAssignee("李四").list();
        	 System.out.println("张三所剩的任务数量:"+tasks.size());
        	 System.out.println("李四所剩的任务数量:"+tasks2.size());	 
        	 System.out.println("*********step2流程结束********");
        	 
         }
         @Test
         public  void  step3(){
        	 System.out.println("*********step3开始************");
        	 List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("王五").list();//王五组的任务
        	 List<Task> tasks2 = taskService.createTaskQuery().taskCandidateGroup("赵六").list();//赵六组的任务
        	 System.out.println("王五组的任务数："+tasks.size());
        	 System.out.println("赵六组的任务数："+tasks2.size());
        	 
        	 for(int i=0;i<tasks.size();i++){
        		  Task task=tasks.get(i);
                  System.out.println("task任务name："+task.getName()+",Id为："+task.getId());
                  
                  if(i%2==0){
                	
                	  taskService.claim(task.getId(), "李四");
                  }else{
                	  
                	  taskService.claim(task.getId(), "张三");
                  }
        		 
        	 }
        	tasks=taskService.createTaskQuery().taskAssignee("张三").list();
        	tasks2=taskService.createTaskQuery().taskAssignee("李四").list();
        	 System.out.println("张三已领的任务"+tasks.size());
        	 System.out.println("李四已领的任务"+tasks2.size());
        	 
        	 for(Task task:tasks){
        		 taskService.complete(task.getId());
        	 }
        	 
        	 for(Task task:tasks2){
        		 taskService.complete(task.getId());
        	 }
        	 tasks=taskService.createTaskQuery().taskAssignee("张三").list();
         	 tasks2=taskService.createTaskQuery().taskAssignee("李四").list();
         	 System.out.println("张三所剩的任务"+tasks.size());
         	 System.out.println("李四所剩的任务"+tasks2.size());
        	 System.out.println("*********step3结束************");
         }
         
         /**
          * activiti历史活动查询
          */
         @Test
         public void historyAction(){
        	 
        	 List<HistoricActivityInstance> list =processEngine.getHistoryService()
        			 .createHistoricActivityInstanceQuery()
        			 .processInstanceId("10001")
        			 .list(); 
        	 
        	 for(HistoricActivityInstance has:list){
        		 System.out.println("任务ID:"+has.getId());
        		 System.out.println("流程实例ID:"+has.getProcessInstanceId());
        		 System.out.println("活动名称:"+has.getActivityName());
        		 System.out.println("办理人:"+has.getAssignee());
        		 System.out.println("开始时间"+has.getStartTime());
        		 System.out.println("结束时间"+has.getEndTime());
        	 }
        	 
         }
         
         
         /**
          * 历史任务查询
          */
         @Test
         public void historyTaskList(){
        	 List<HistoricTaskInstance> list=processEngine.getHistoryService()
        			 .createHistoricTaskInstanceQuery()
        			 .taskAssignee("张三")
        			 .finished()
        			 .list();
        	 
        	 for(HistoricTaskInstance has:list){
        		 System.out.println("任务ID:"+has.getId());
        		 System.out.println("流程实例ID:"+has.getProcessInstanceId());
        		 System.out.println("办理人:"+has.getAssignee());
        		 System.out.println("开始时间"+has.getStartTime());
        		 System.out.println("结束时间"+has.getEndTime());
        	 }
        	 
         }
         
         /**
          * 查询流程状态(正在执行or已经执行结束)
          */
         @Test
         public void processState(){
        	 ProcessInstance pi=processEngine.getRuntimeService()
        			 .createProcessInstanceQuery()
        			 .processDefinitionId("10001")
        			 .singleResult();
        	 if(pi!=null){
        		 System.out.println("流程正在执行");
        	 }else{
        		 System.out.println("流程已经结束");
        	 }
        	 
         }
         
         /**
          * 删除所有key相同的流程定义
          * @throws Exception
          */
         public void  deleteByKey()  throws Exception {
        	 List<ProcessDefinition> pdList=processEngine.getRepositoryService()
        			 .createProcessDefinitionQuery()//
        			 .processDefinitionKey("helloworld")//
        			 .list();
        	 for(ProcessDefinition pd:pdList){
        		 processEngine.getRepositoryService().deleteDeployment(pd.getDeploymentId(),true);
        	 }
         }
         /**
          * 查询最新版本的流程定义
          */
         @Test
         public void listLastVersion() throws Exception{
        	 List<ProcessDefinition> listAll=processEngine.getRepositoryService()
        			 .createProcessDefinitionQuery()
        			 .orderByProcessDefinitionVersion().asc()//版本升序
        			 .list();
        	 //定义有序Map，相同的key 假如添加map的值  后面的值 会覆盖前面的key的值
        	Map<String,ProcessDefinition>  map=new LinkedHashMap<String,ProcessDefinition>();
        	//遍历集合  根据key来覆盖前面的值来保证最新的key覆盖前面的所有老的key的值
        	for(ProcessDefinition pd:listAll){
        		map.put(pd.getKey(), pd);
        	}
        	 
        	List<ProcessDefinition> pdList=new LinkedList<ProcessDefinition>(map.values());
        	for(ProcessDefinition pd:pdList){
        		System.out.println("id:"+pd.getId());
        		System.out.println("版本为："+pd.getVersion());
        		System.out.println("名称："+pd.getName());
        		System.out.println("Key:"+pd.getKey());
        	}
        	
         }
         
         /**
          * 通过流程部署id获取流程图图片
          */
         @Test
         public void getImageById() throws Exception{
        	 InputStream inputStream=processEngine.getRepositoryService()
        			 .getResourceAsStream("12501", "diagrams/helloworld.helloworld.png");
        	 FileUtils.copyInputStreamToFile(inputStream, new File("D:/helloworld.png"));
         }
         
         
         /**
          * 级联删除 已经使用的流程实例信息 也会被删除
          */
         public void deleteCascade(){
        	 processEngine.getRepositoryService()
        	 .deleteDeployment("12501",true);//默认为false true就是级联删除
        	 System.out.println("delete cascade ok!");
         }
         
         
         
         
         
         
}
