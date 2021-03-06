package edu.cmu.gizmo.unittest;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import junit.framework.TestCase;
import edu.cmu.gizmo.management.robot.Robot;
import edu.cmu.gizmo.management.taskmanager.TaskCapabilityLoader;

public class TestTaskCapabilityLoader extends TestCase {

	
	public void testShouldInstantiateCapability() {
		
	}

	/* 
	 * TaskCapabilityLoader should load all of the needed objects to the 
	 * capability
	 */
	public void testShouldLoadParameters() {
		ConcurrentHashMap<Object, Object> config = new ConcurrentHashMap<Object, Object>(0);
		config.put("robot.exist", "true");
		// cobot = (Cobot3Robot) RobotFactory.newRobot("cobot3");

		Robot robot = null;
		TaskCapabilityLoader taskCapabilityLoader = new TaskCapabilityLoader(robot);
		taskCapabilityLoader.loadConfiguration(config);
		
		ConcurrentHashMap<Object, Object> newConfig = taskCapabilityLoader.getConfig();
		Object obj = newConfig.get("robot.object");

		Set<Object> keys = newConfig.keySet();

		Iterator itr = keys.iterator(); 
		while(itr.hasNext()) {
		    String element = (String)itr.next();
		    if(element.equals("robot.object")) {
		    	assertTrue(true);
		    	return;
		    }
		}
		fail();
	}
	
}
