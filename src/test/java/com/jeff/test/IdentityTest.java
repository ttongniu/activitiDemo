package com.jeff.test;

import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.junit.Before;
import org.junit.Test;
/**
 * IdentityService 身份信息Service
 * @author ntt
 *
 * 2017年8月31日
 */
public class IdentityTest {
         ProcessEngine processEngine=null;
         RepositoryService repositoryService=null;
         RuntimeService runtimeService=null;
         TaskService taskService=null;
         IdentityService identityService=null;
         @Before
         public void init(){
        	 //创建ProcessEngine
        	  processEngine=ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        	  repositoryService=processEngine.getRepositoryService();
        	  runtimeService=processEngine.getRuntimeService();
        	  taskService=processEngine.getTaskService();
              identityService=processEngine.getIdentityService();      	  
         }
         
         @Test
        public void saveUsers(){
        	UserEntity  userEntity=new UserEntity();
        	userEntity.setFirstName("niu");
        	userEntity.setLastName("tongtong");
        	userEntity.setEmail("12@qq.com");
        	identityService.saveUser(userEntity);
        } 
         @Test
         public void deleteuser(){
        	 identityService.deleteUser("197501");  //按照id 进行删除
         }
         
         @Test
         public void saveGroup(){
        	 GroupEntity  groupEntity=new GroupEntity();
        	 groupEntity.setName("班长");
        	 groupEntity.setType("请假权限组");
        	 identityService.saveGroup(groupEntity);
         }
         @Test
         public void deleteGroup(){
        	 identityService.deleteGroup("200001");
         }
         @Test
         public void saveMembership(){
        	 identityService.createMembership("202501", "205001");
         }
         
         
         @Test
         public void findMembershipgroup(){ //人员对应的组
        	 List<Group> grouplist	= identityService.createGroupQuery().groupMember("202501").list();   
        	 for(Group group:grouplist){
        		 System.out.println(group.getId());
        	 }
         }
         
         @Test
         public void findMembershipuser(){ //组对应人员
        	 List<User> userlist	= identityService.createUserQuery().memberOfGroup("部门经理").list();
        	 for(User user:userlist){
        		 System.out.println(user.getId());
        	 }
         }
         @Test
         public void split(){
        	 //String ss="1211,1111";
        	 String ss="21323,dkkldf";
        	 String[] ddd=ss.split(",");
        	 /*for(int i=0; ddd.length>i;i++){
        		 System.out.println(ddd[i]);
        	 }*/
        	 System.out.println(ddd[1]);
         }
         
         
}
