/*
 * TestTaskResolver.java Jul 10, 2012 1.0
 */
package edu.cmu.gizmo.unittest;

import java.lang.reflect.Constructor;

import junit.framework.TestCase;
import edu.cmu.gizmo.management.capability.Capability;
import edu.cmu.gizmo.management.robot.Robot;
import edu.cmu.gizmo.management.taskbus.GizmoTaskBus;
import edu.cmu.gizmo.management.taskmanager.TaskResolver;
import edu.cmu.gizmo.management.taskmanager.exceptions.CapabilityNotFoundForPrimitive;


public class TestTaskResolver extends TestCase {
	
	
	public void testShouldRetrieveCapabilityName() {
		// primitive should exist in the database
		// Capability object should safely be loaded

		Robot robot = null;
		Integer tid = new Integer(1);
		Integer cid = new Integer(1);
		GizmoTaskBus bus = GizmoTaskBus.connect();
		TaskResolver tr = new TaskResolver();
		Capability c = null;
		Constructor constructor = null;

		String capabilityName = null;
		try {
			capabilityName = tr.retrieveCapablityName("GoToRoomCapability");
		} catch (CapabilityNotFoundForPrimitive e) {
			e.printStackTrace();
			fail();
		}
		assertEquals(capabilityName, "GoToRoomDriveCapability");
	}
	

}



