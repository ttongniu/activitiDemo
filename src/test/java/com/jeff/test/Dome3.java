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
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

import groovy.json.JsonBuilder;

public class Dome3 {
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
        	 InputStream inputStreamBpmn = this.getClass().getResourceAsStream("/diagrams/leave.bpmn");  
             InputStream inputStreamPng = this.getClass().getResourceAsStream("/diagrams/leave.png");  
             Deployment deployment = processEngine.getRepositoryService()//与流程定义和部署对象相关的Service  
                             .createDeployment()//创建一个部署对象  
                             .name("leave任务1")//添加部署的名称  
                             .addInputStream("diagrams/leave.bpmn", inputStreamBpmn)//  
                             .addInputStream("diagrams/leave.png", inputStreamPng)//  
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
             String processDefinitionKey = "leave";  
             /**启动流程实例的同时，设置流程变量，使用流程变量用来指定任务的办理人，对应task.pbmn文件中#{userID}*/  
             //Map<String, Object> variables = new HashMap<String, Object>();  
            // variables.put("userId", "niutongtong4");  //  
             ProcessInstance pi = processEngine.getRuntimeService()//与正在执行的流程实例和执行对象相关的Service  
                             .startProcessInstanceByKey(processDefinitionKey);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动  
             System.out.println("流程实例ID:"+pi.getId());//流程实例ID      
             System.out.println("流程定义ID:"+pi.getProcessDefinitionId());//流程定义ID   
         }
         
         
         /**个人任务待办查询*/
         @Test
         public void  findTask(){
        	 String assignee = "狗狗";  
             List<Task> list = processEngine.getTaskService()//与正在执行的任务管理相关的Service  
                             .createTaskQuery()//创建任务查询对象  
                             /**查询条件（where部分）*/  
                             //.taskAssignee(assignee)//指定个人任务查询，指定办理人  
                             .taskCandidateOrAssigned(assignee)
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
        	 String taskId="107504";
        	 processEngine.getTaskService().complete(taskId);
        	 System.out.println("**********taskId:"+taskId+"完成！");
         }
         /**拾取任务*/
         @Test
          public void pickUp(){
        	  
        	  processEngine.getTaskService().claim("70002", "狗狗");
          }
         /**回退任务*/
         @Test
          public void goBack(){
        	  
        	  processEngine.getTaskService().setAssignee("70002", null);
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
        	 System.out.println(JSON.toJSONString(findOutComeListByTaskId("105002")));
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
             List<PvmTransition> pvmList=  activityImpl.getOutgoingTransitions();
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
         
         
         /***/
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
         
        /**按照连线名称完成任务*/
         @Test
       public  void completeTaskByoutcome(){
    	   
    	   String taskId="110002";
    	   String outcome="经理批准";
    	   String message="和发生大健康防护水电费和会计师很划算的金凤凰圣诞节";
    	   String userId="ntt7";
    	   /**
   		 * 1：在完成之前，添加一个批注信息，向act_hi_comment表中添加数据，用于记录对当前申请人的一些审核信息
   		 */
   		//使用任务ID，查询任务对象，获取流程流程实例ID
    	Task task=   processEngine.getTaskService()//
    			.createTaskQuery()
    			.taskId(taskId)
    			.singleResult();
    	//2.获取流程实例ID
    	 String  processInstanceId=   task.getProcessInstanceId();
    	 /**  注意：添加批注的时候，由于Activiti底层代码是使用：
		 		String userId = Authentication.getAuthenticatedUserId();
			    CommentEntity comment = new CommentEntity();
			    comment.setUserId(userId);
			  所有需要从Session中获取当前登录人，作为该任务的办理人（审核人），对应act_hi_comment表中的User_ID的字段，不过不添加审核人，该字段为null
			 所以要求，添加配置执行使用Authentication.setAuthenticatedUserId();添加当前任务的审核人
		 * */
    	 Authentication.setAuthenticatedUserId(userId);
 		 taskService.addComment(taskId, processInstanceId, message);
 		 
 		/**
 		 * 3：如果连线的名称是“默认提交”，那么就不需要设置，如果不是，就需要设置流程变量
 		 * 在完成任务之前，设置流程变量，按照连线的名称，去完成任务
 				 流程变量的名称：outcome
 				 流程变量的值：连线的名称
 		 */
 		Map<String, Object> variables = new HashMap<String,Object>();
 		
 			variables.put("outcome", outcome);
 		
 		//4：使用任务ID，完成当前人的个人任务，同时流程变量
 				taskService.complete(taskId, variables); 
 	    //5：当任务完成之后，需要指定下一个任务的办理人（使用类）-----已经开发完成
		/**
		 * 6：在完成任务之后，判断流程是否结束
   			如果流程结束了，更新请假单表的状态从1变成2（审核中-->审核完成）
		 */
 		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
 								.processInstanceId(processInstanceId)//使用流程实例ID查询
 								.singleResult();		
 		//流程结束了
 		if(pi==null){
 			System.out.println("//更新请假单表的状态从1变成2（审核中-->审核完成）");
 		}
       }
         
         /** 使用任务 id 查找 批注信息*/
         @Test
         public void findCommentByTaskId() {

         String taskId="112503"; // 现在的任务id
         HistoryService historyService=processEngine.getHistoryService();
         TaskService taskService=processEngine.getTaskService();
         List<Comment> list = new ArrayList();
         //使用当前的任务ID，查询当前流程对应的历史任务ID

         //使用当前任务ID，获取当前任务对象
         Task task = taskService.createTaskQuery()//
         .taskId(taskId)//使用任务ID查询
         .singleResult();
         //获取流程实例ID
         String processInstanceId = task.getProcessInstanceId();
         //方式一
         /*//使用流程实例ID，查询历史任务，获取历史任务对应的每个任务ID
         List<HistoricTaskInstance> htiList = historyService.createHistoricTaskInstanceQuery()//历史任务表查询
         .processInstanceId(processInstanceId)//使用流程实例ID查询
         .list();
         //遍历集合，获取每个任务ID
        
         if(htiList!=null && htiList.size()>0){
         for(HistoricTaskInstance hti:htiList){
         //任务ID
         String htaskId = hti.getId();
         //获取批注信息
         List<Comment> taskList = taskService.getTaskComments(htaskId);//对用历史完成后的任务ID
         list.addAll(taskList);
         }
         }*/
         //方式二
         list = taskService.getProcessInstanceComments(processInstanceId);
         for(Comment com:list){
         System.out.println("ID:"+com.getId());
         System.out.println("Message:"+com.getFullMessage());
         System.out.println("TaskId:"+com.getTaskId());
         System.out.println("ProcessInstanceId:"+com.getProcessInstanceId());
         System.out.println("UserId:"+com.getUserId());
         }

         System.out.println(list);
         }    
        
         /**
     	 * 二：查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中
     		 map集合的key：表示坐标x,y,width,height
     		 map集合的value：表示坐标对应的值
     	 */
     	@Test
     	public void findCoordingByTask() {
     		String taskId="112503";
     		//存放坐标
     		Map<String, Object> map = new HashMap<String,Object>();
     		//使用任务ID，查询任务对象
     		Task task = taskService.createTaskQuery()//
     					.taskId(taskId)//使用任务ID查询
     					.singleResult();
     		//获取流程定义的ID
     		String processDefinitionId = task.getProcessDefinitionId();
     		//获取流程定义的实体对象（对应.bpmn文件中的数据）
     		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processDefinitionId);
     		//流程实例ID
     		String processInstanceId = task.getProcessInstanceId();
     		//使用流程实例ID，查询正在执行的执行对象表，获取当前活动对应的流程实例对象
     		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//创建流程实例查询
     					.processInstanceId(processInstanceId)//使用流程实例ID查询
     					.singleResult();
     		//获取当前活动的ID
     		String activityId = pi.getActivityId();
     		//获取当前活动对象
     		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);//活动ID
     		//获取坐标
     		map.put("x", activityImpl.getX());
     		map.put("y", activityImpl.getY());
     		map.put("width", activityImpl.getWidth());
     		map.put("height", activityImpl.getHeight());
     		System.out.println("X*****"+ activityImpl.getX());
     		System.out.println("y*****"+ activityImpl.getY());
     		System.out.println("width******"+activityImpl.getWidth());
     		System.out.println("height******"+activityImpl.getHeight());
     		//return map;
     	}  
}
