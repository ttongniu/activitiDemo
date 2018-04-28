package com.jeff.test;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

import groovy.json.JsonBuilder;

public class Dome2 {
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
                             .name("ntt6任务")//添加部署的名称  
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
             variables.put("userId", "niutongtong6");  //  
             ProcessInstance pi = processEngine.getRuntimeService()//与正在执行的流程实例和执行对象相关的Service  
                             .startProcessInstanceByKey(processDefinitionKey,variables);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动  
             System.out.println("流程实例ID:"+pi.getId());//流程实例ID      
             System.out.println("流程定义ID:"+pi.getProcessDefinitionId());//流程定义ID   
         }
         
         
         /**个人任务待办查询*/
         @Test
         public void  findTask(){
        	 String assignee = "niutongtong6";  
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
         
         /**完成任务*/
         @Test
         public void completeTask(){
        	 String taskId="82505";
        	 processEngine.getTaskService().complete(taskId);
        	 System.out.println("**********taskId:"+taskId+"完成！");
         }
         
         
         /**
          * 查询正在执行的任务办里人表
          */
         @Test
         public void findRunPersonTask(){
           ProcessInstance	pi= runtimeService.createProcessInstanceQuery()
        	 .processInstanceId("17501")
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
         public void getFlowstr(){
        	 String processInstanceId="82501";
        	 System.out.println(JSON.toJSONString(getFlows(processInstanceId)));
        	 
         }
         
         /**
          * 根据processInstanceId获取出线
          * （获取本节点后面的所有出线）
          * @param taskId
          * @return
          */
         public List<String> getFlows(String processInstanceId) {
             Task task = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstanceId).singleResult();
             ExecutionEntity execution = (ExecutionEntity) processEngine.getRuntimeService().createExecutionQuery().executionId(task.getExecutionId()).singleResult();
             String activitiId = execution.getActivityId();
             System.out.println("根据taskid获取activitiId：" + activitiId);  
             String processDefinitionId = processEngine.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult().getProcessDefinitionId();
             ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) processEngine.getRepositoryService()).getDeployedProcessDefinition(processDefinitionId);
             List<ActivityImpl> activities = processDefinitionEntity.getActivities();
             System.out.println("根据processInstanceId获取本流程图的所有节点");
             List<PvmTransition> outgoingTransitions = new ArrayList<PvmTransition>();
             List<String> messageList = new ArrayList<String>();
             for (ActivityImpl activityImpl : activities) {
                 if (activityImpl.getId().equals(activitiId)) {
                     outgoingTransitions = activityImpl.getOutgoingTransitions();
                     for (PvmTransition pvmTransition : outgoingTransitions) {
                         messageList.add(pvmTransition.getProperty("name").toString());
                     }
                   System.out.println("获取本节点的所有出线");  
                 }
             }
             return messageList;
         }
         
         
         
         /**17501
          * 查询流程定义的所有节点信息
          */
         @Test
         public void findbpmn(){
        	 BpmnModel model = repositoryService.getBpmnModel("demo1:3:27504");
        	 if(model != null) {
        	     Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
        	     for(FlowElement e : flowElements) {
        	         System.out.println("flowelement id:" + e.getId() + "  name:" + e.getName() + "   class:" + e.getClass().toString());
        	     }
        	 }
         }
         
         @Test
         public void findOutComeListByTaskId(){
        	 System.out.println(JSON.toJSONString(findOutComeListByTaskId("45003")));
         }
         
         
         /**已知任务ID，查询processDefinitionEntity对象，从而获取当前任务完成之后的连线名称，并放置到List<String>集合中**/
         
         public   List<String>  findOutComeListByTaskId(String taskId){
			//返回存放连线名称的集合
        	 List<String> outCome=new ArrayList<String>();
        	 //1.使用任务Id，查询任务对象
        	 Task task=taskService.createTaskQuery()//
        			 .taskId(taskId)//
        			 .singleResult();
        	 //2.获取流程定义的id
             String processDefinitionId=task.getProcessDefinitionId();
             //3.查询ProcessDefinitionEntiy对象
             ProcessDefinitionEntity processDefinitionEntity   =(ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);		 
        	 //4.使用任务对象 获取流程实例id
             
             String processInstanceId= task.getProcessInstanceId();
             //5.使用流程实例id,查询正在执行的执行对象表，返回流程实例对象
            ProcessInstance pi= runtimeService.createProcessInstanceQuery()//
                          .processInstanceId(processInstanceId)
                          .singleResult();
           
             //6. 获取当前活动id
             String activityId=  pi.getActivityId();
             //7.获取当前活动
             ActivityImpl activityImpl=   processDefinitionEntity.findActivity(activityId);
        	 //8.获取当前活动完成之后连线的名称
             List<PvmTransition> pvmList=  activityImpl.getIncomingTransitions();
        	 if(pvmList!=null&&pvmList.size()>0){
        		 for(PvmTransition pvm:pvmList){
            	   String name= (String) pvm.getProperty("name");
            	   if(StringUtils.isNotBlank(name)){
            		   outCome.add(name);
            	   }else {
            		   outCome.add("默认提交");
            	   }
            	 } 
        	 }
        	 return outCome;
         }
         
         
         
         @Test
         public void completeMytask(){
        	  //任务ID  
             String taskId = "42504";  
             String IsAgree = "经理批准";  
             //String IsNotAgree = "驳回";  
             //String IsStudent = "";  
             String nextUser ="jee"; 
             //完成任务的同时，设置流程变量，使用流程变量用来指定完成任务后，下一个连线，对应sequenceFlow.bpmn文件中${message=='不重要'}  
             Map<String, Object> variables = new HashMap<String, Object>();  
             variables.put("message", IsAgree);
             variables.put("nextUser", nextUser);
             processEngine.getTaskService()//与正在执行的任务管理相关的Service  
                         .complete(taskId,variables);
             System.out.println("完成任务：任务ID："+taskId);  
         }
        
       
           
        
       
}
