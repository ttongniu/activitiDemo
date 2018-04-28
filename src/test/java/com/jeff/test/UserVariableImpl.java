package com.jeff.test;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class UserVariableImpl implements TaskListener {

	private static final long serialVersionUID = -8130651488066713690L;

	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		delegateTask.addCandidateUser("狗狗");
	    delegateTask.addCandidateUser("猫猫");
	}

	

}
