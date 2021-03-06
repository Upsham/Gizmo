/*
 * GizmoTestSuite.java Jul 10, 2012 1.0
 */
package edu.cmu.gizmo.unittest;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * The Class GizmoTestSuite.
 */
public class GizmoTestSuite {

	/**
	 * Suite.
	 *
	 * @return the test
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(TestTaskClient.class);
		suite.addTestSuite(TestGoToRoomDriveCapability.class);
		suite.addTestSuite(TestGizmoTaskBus.class);
		suite.addTestSuite(TestTaskExecutor.class);		
		suite.addTestSuite(TestCapability.class);		
		suite.addTestSuite(TestSkypeCommunicationCapability.class);
		suite.addTestSuite(TestScriptTaskStrategy.class);
		suite.addTestSuite(TestCobot3DashboardCapability.class);
		suite.addTestSuite(TestManifestReader.class);
		suite.addTestSuite(TestDBConnection.class);
		suite.addTestSuite(TestTaskResolver.class);
		
		//suite.addTestSuite(TestCoBot3Robot.class);
		//$JUnit-BEGIN$

		//$JUnit-END$
		return suite;
	}

}
