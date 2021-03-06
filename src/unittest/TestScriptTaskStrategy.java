/*
 * ScriptTaskStrategyUnitTests.java Jul 10, 2012 1.0
 */
package edu.cmu.gizmo.unittest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;

import junit.framework.TestCase;
import edu.cmu.gizmo.management.capability.Capability;
import edu.cmu.gizmo.management.capability.PausableCapability;
import edu.cmu.gizmo.management.robot.Cobot3Robot;
import edu.cmu.gizmo.management.robot.Robot;
import edu.cmu.gizmo.management.robot.RobotFactory;
import edu.cmu.gizmo.management.robot.RobotFactory.RobotModel;
import edu.cmu.gizmo.management.taskbus.GizmoTaskBus;
import edu.cmu.gizmo.management.taskbus.messages.TaskMessage;
import edu.cmu.gizmo.management.taskmanager.ScriptTaskStrategy;
import edu.cmu.gizmo.management.taskmanager.TaskExecutor;
import edu.cmu.gizmo.management.taskmanager.TaskStatus;
import edu.cmu.gizmo.management.taskmanager.exceptions.TaskPlanNotFoundException;



class DummyCapabilityMockup extends Capability implements PausableCapability {
	private final String DESCRIPTION = "DummyCapability";

	public DummyCapabilityMockup() {
		setStatus(CapabilityStatus.RUNNING, getCapabilityName()
				+ " in initial state");
	}

	public void execute()
	{
		try{
			  Thread.currentThread().sleep(10000); //sleep for 10s
		} catch(Exception ie){
			ie.printStackTrace();
		}

		// COMPLETE
		setStatus(CapabilityStatus.COMPLETE, getCapabilityName()
				+ " in complete state");
		System.out.println("[DummyCapability] Complete...");		
	}
    public String getCapabilityName()
    {
    	return new String();    	
    }
    public String getCapabilityDescription()
    {
    	return new String();
    }

    public ConcurrentHashMap<Object,Object> pause()
    {
    	ConcurrentHashMap<Object, Object> hp = new ConcurrentHashMap<Object, Object>();    	
    	return hp;
    }
    public void resume(ConcurrentHashMap<Object,Object> state)
    {
    	
    }
    protected void handleMessage(Message message)
    {
    	
    }

    public void terminate()
    {
    	
    }

    public Object getConfigurationParameter(Object param)
    {
    	return new Object();
    }

    public void setInput(Object param, Object value)
    {
    	
    }
	@Override
	public Object getInputParameterValue(Object param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConcurrentHashMap<String, Class> getInputRequirements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConcurrentHashMap<String, Class> getOutputRequirements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resume(Object state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInput(ConcurrentHashMap<Object, Object> input) {
		// TODO Auto-generated method stub
		
	}
}

class DummyCapabilityMonitorMockup extends Capability {
	private final String DESCRIPTION = "DummyCapability";

	public DummyCapabilityMonitorMockup() {
		setStatus(CapabilityStatus.RUNNING, getCapabilityName()
				+ " in initial state");
	}

	public void execute()
	{
		try{
			  Thread.currentThread().sleep(10000); //sleep for 10s
		} catch(Exception ie){
			ie.printStackTrace();
		}

		// COMPLETE
		setStatus(CapabilityStatus.COMPLETE, getCapabilityName()
				+ " in complete state");
		System.out.println("[DummyCapability] Complete...");		
	}
    public String getCapabilityName()
    {
    	return new String();    	
    }
    public String getCapabilityDescription()
    {
    	return new String();
    }

    public Object pause()
    {
    	ConcurrentHashMap<Object, Object> hp = new ConcurrentHashMap<Object, Object>();    	
    	return hp;
    }
    public void resume(Object state)
    {
    	
    }
    protected void handleMessage(Message message)
    {
    	
    }

    public void terminate()
    {
    	
    }

    public Object getConfigurationParameter(Object param)
    {
    	return new Object();
    }

    public void setInput(Object param, Object value)
    {
    	
    }
	@Override
	public Object getInputParameterValue(Object param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConcurrentHashMap<String, Class> getInputRequirements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConcurrentHashMap<String, Class> getOutputRequirements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInput(ConcurrentHashMap<Object, Object> input) {
		// TODO Auto-generated method stub
		
	}
}

class DummyCapabilityFailureMockup extends Capability {
	private final String DESCRIPTION = "DummyCapability";

	public DummyCapabilityFailureMockup() {
		setStatus(CapabilityStatus.RUNNING, getCapabilityName()
				+ " in initial state");
	}

	public void execute()
	{
		try{
			  Thread.currentThread().sleep(10000); //sleep for 10s
		} catch(Exception ie){
			ie.printStackTrace();
		}

		// ERROR
		setStatus(CapabilityStatus.ERROR, getCapabilityName()
				+ " in error state");
		System.out.println("[DummyCapability] Error...");
	}
    public String getCapabilityName()
    {
    	return new String();    	
    }
    public String getCapabilityDescription()
    {
    	return new String();
    }

    public ConcurrentHashMap<Object,Object> pause()
    {
    	ConcurrentHashMap<Object, Object> hp = new ConcurrentHashMap<Object, Object>();    	
    	return hp;
    }
    public void resume(ConcurrentHashMap<Object,Object> state)
    {
    	
    }
    protected void handleMessage(Message message)
    {
    	
    }

    public void terminate()
    {
    	
    }

    public Object getConfigurationParameter(Object param)
    {
    	return new Object();
    }

    public void setInput(Object param, Object value)
    {
    	
    }
	@Override
	public Object getInputParameterValue(Object param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConcurrentHashMap<String, Class> getInputRequirements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConcurrentHashMap<String, Class> getOutputRequirements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInput(ConcurrentHashMap<Object, Object> input) {
		// TODO Auto-generated method stub
		
	}
}

class TaskManagerWithBusMockup implements Observer {
	private final String TASK_CHANNEL = "cobot3task";
	private ConcurrentHashMap<Integer,TaskExecutor>tasks;
	private GizmoTaskBus bus;
	private MessageProducer pub;
	private MessageConsumer sub;
	private ExecutorService taskRunner; 
	private Cobot3Robot cobot;
	private Integer taskId;
	private Boolean taskCompleted;
	private String roomNumber;
	public TaskStatus ts= null;

	public TaskManagerWithBusMockup(final GizmoTaskBus bus) {
		taskCompleted = false;
		this.bus = bus;
		taskRunner = Executors.newSingleThreadExecutor();
		sub =  bus.getTaskConsumer(null);
		try {
			System.out.println("[TaskManagerWithBusMockup] ");
			sub.setMessageListener(new MessageListener() {
			
				@Override
				public void onMessage(Message arg0) {
					System.out.println("OnMessage11");
					TaskMessage m = (TaskMessage)arg0;

				}
			});
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void update(Observable o, Object status) {
		ts = (TaskStatus) status;		
	}

}

class TaskManagerMockup implements Observer {
	public TaskStatus ts= null;

	public TaskManagerMockup() {
	}
	
	@Override
	public void update(Observable o, Object status) {
		ts = (TaskStatus) status;		
	}
}

public class TestScriptTaskStrategy extends TestCase {

	
	
	class MockCobot extends Thread {
		
		/** The Constant INVALID_ROOM. */
		public static final String INVALID_ROOM = "888";
		
		/** The s. */
		private Socket s;
		
		/** The running. */
		private Boolean running;
		
		/** The got drive command. */
		private Boolean gotDriveCommand;

		/**
		 * Instantiates a new mock cobot.
		 */
		public MockCobot() {
			gotDriveCommand = false;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			try {
				System.out.println("trying to connect to the server");
				s = new Socket();
				SocketAddress sockaddr = new InetSocketAddress("localhost", 4242);
				s.connect(sockaddr);
				running = true;
				Integer counter = 0;

				while (running) {
					BufferedReader r = new BufferedReader(
							new InputStreamReader(
									s.getInputStream())
					);
					char[] in = new char[1024];

					r.read(in,0,1024);
					String cmd = String.valueOf(in);

					PrintWriter w = new PrintWriter(
							s.getOutputStream());

					String[] arr = cmd.split(",");

					System.out.println("CMD: " + cmd);
					if (arr[0].equals("MoveCamera")) {
						System.out.println("MoveCamera received");
					}	
					else if (arr[0].equals("GoToRoom")) {
						char[] room = arr[1].toCharArray();
						gotDriveCommand = true;
						if (counter < 5) {
							if (arr[1].trim().equals(INVALID_ROOM)) {
								w.println("invalid room");
							}
							else { 
								w.println("success,42");
							}
						}
					}
					else if (arr[0].equals("status") && counter < 5) {
						if (arr[1].trim().equals("42")) {
							w.println("running,Moving & happy");
						} 
						else {
							w.println("does not exist");
						}
						counter++;
					}
					else {
						w.println("complete,hi majed");
						counter = 0;
					}
					w.flush();
				}
			} catch (IOException e) {

			}			
		}

		/**
		 * Checks if is got drive command.
		 *
		 * @return the boolean
		 */
		public Boolean isGotDriveCommand() { 
			return gotDriveCommand;
		}

		/**
		 * Kill.
		 */
		public void kill() { 

			try {
				s.shutdownOutput();
				s.shutdownInput();
				s.close();
				while (!s.isClosed());
			} catch (IOException e) {

			}
			running = false;
		}
	}	

	/**
	 * 1. Create TaskManagerMockup
	 * 2. Create ScriptTaskStrategy class
	 * 2. Call parse(malformedScript.xml)
	 * 3. Notify TaskManager that it is malformed
	 */ 
	public void testShouldDetectMalformedTaskScript() {
		TaskManagerMockup tmMockup = new TaskManagerMockup();
		Integer taskId = null;
		Robot cobot = null;
		GizmoTaskBus bus = null;
		String taskPlan = "malformedScript.xml";

		try {			
			ScriptTaskStrategy tes = new ScriptTaskStrategy(cobot, taskId,
					taskPlan);
			tes.addObserver(tmMockup);
			tes.parse();
		} catch (TaskPlanNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();

		}
		assertEquals(tmMockup.ts.getStatus(), TaskStatus.TaskStatusValue.ERROR);		
	}

	/**
	 * 1. Create TaskManagerMockup
	 * 2. Create ScriptTaskStrategy class
	 * 3. Call parse(leg1demo.xml)
	 * 4. In data structure created,
	 *    check whether the first element's name is correct 
	 *    - QueryCalendarCapability
	 * 5. Check whether the first element has success flag defined
	 * 6. Check whether the first element has failure flag defined
	 * 7. Check whether the second element's name is correct
	 *    - GoToRoomCapability
	 * 8. Check whether the second element has success flag defined
	 * 9. Check whether the second element has failure flag defined
	 * 10. Check whether the dependency is set to 1
	 */
	public void testShouldParseOneTaskSuccessfully() {
		TaskManagerMockup tmMockup = new TaskManagerMockup();
		Integer taskId = new Integer(1);
		Robot cobot = null;
		GizmoTaskBus bus = null;
		String taskPlan = "leg1demo.xml";
		
		try {
			ScriptTaskStrategy tes = new ScriptTaskStrategy(cobot, taskId, 
					taskPlan);
			tes.addObserver(tmMockup);
			tes.parse();

			assertEquals(tmMockup.ts.getStatus(), TaskStatus.TaskStatusValue.READY);

		} catch (TaskPlanNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

	/**
	 * 1. Create TaskManagerMockup
	 * 2. Create ScriptTaskStrategy class
	 * 3. Start DummyCapabilityFailureMockup
	 * 4. Monitor DummyCapabilityFailureMockup
	 * 5. DummyCapabilityFailureMockup fails
	 * 6. Check whether the status is ERROR or not
	 */
	public void testShouldNotifyTaskManagerWhenCapabilityFailedForOneTask() {
		TaskManagerMockup tm = new TaskManagerMockup();

		Integer taskId = new Integer(1);
		Integer capabilityId = new Integer(1);
		Robot cobot = null;
		GizmoTaskBus bus = null;
		String taskPlan = "leg1demo.xml";
		
		try {
			ScriptTaskStrategy tes = new ScriptTaskStrategy(cobot, taskId, 
					taskPlan);
			tes.addObserver(tm);

			Capability c = new DummyCapabilityFailureMockup();

			ExecutorService taskRunner;
			taskRunner = Executors.newSingleThreadExecutor();
			taskRunner.execute(c);

			tes.monitor(c);

			assertEquals(tm.ts.getStatus(), TaskStatus.TaskStatusValue.ERROR);

		} catch (TaskPlanNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	/**
	 * 1. Create TaskManagerMockup
	 * 2. Create ScriptTaskStrategy class
	 * 3. Create DummyCapabilityMockup
	 * 4. Call monitor(DummyCapabilityMockup)
	 * 5. Print out the status
	 * 6. Inform TaskManger Once completed
	 * 7. Check whether the status is COMPLETE on TaskManager
	 */
	public void testShouldMonitorOneTaskSuccessfully() {
		GizmoTaskBus bus = GizmoTaskBus.connect();
		//TaskManagerWithBusMockup tm = new TaskManagerWithBusMockup(bus);
		TaskManagerWithBusMockup tm = new TaskManagerWithBusMockup(bus);

		Integer taskId = new Integer(1);
		Integer capabilityId = new Integer(1);
		Robot cobot = null;
		String taskPlan = "leg1demo.xml";
		ConcurrentHashMap<Object,Object> config = new ConcurrentHashMap<Object,Object>(0);
		config.put("capability.directory", "src/main/java/edu/cmu/gizmo/management/capability");
		config.put("capability.path", "src/main/java/edu/cmu/gizmo/management/capability");
		config.put("ui.class", "XXX");
		config.put("ui.display", "YYY");

		try {
			ScriptTaskStrategy tes = new ScriptTaskStrategy(cobot, taskId, 
					taskPlan);
			tes.addObserver(tm);

			Capability c = new DummyCapabilityMonitorMockup();
			c.load(taskId, capabilityId, config);

			ExecutorService taskRunner;
			taskRunner = Executors.newSingleThreadExecutor();
			taskRunner.execute(c);

			tes.monitor(c);
						
			assertEquals(tm.ts.getStatus(), TaskStatus.TaskStatusValue.COMPLETE);

		} catch (TaskPlanNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}		
	}
	
	/**
	 * 1. Create TaskManagerMockup
	 * 2. Create ScriptTaskStrategy class
	 * 3. Call parse(leg1demo.xml)
	 * 5. Call execute()
	 * 7. Check whether the status is COMPLETE or not
	 */
	public void testShouldExecuteOneTaskSuccessfully() {
		GizmoTaskBus bus = GizmoTaskBus.connect();
		//TaskManagerWithBusMockup tm = new TaskManagerWithBusMockup(bus);
		TaskManagerWithBusMockup tm = new TaskManagerWithBusMockup(bus);

		Integer taskId = new Integer(1);
		Integer capabilityId = new Integer(1);
		Robot cobot = null;
		String taskPlan = "dummyleg1demo.xml";

		
		try {
			ScriptTaskStrategy tes = new ScriptTaskStrategy(cobot, taskId, 
					taskPlan);
			tes.addObserver(tm);
	
			tes.execute();
						
			assertEquals(tm.ts.getStatus(), TaskStatus.TaskStatusValue.COMPLETE);

		} catch (TaskPlanNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
//LEG3-----------------------------------------------------------	
	
	/*
	 * 1. Create TaskManagerMockup
	 * 2. Create ScriptTaskStrategy class
	 * 3. Call setTaskPlan(seq2tasks.xml)
	 * 4. Call execute()
	 * 5. Check whether the status in TM is COMPLETE
	 */
	public void testShouldLoadFirstTaskInformTaskManagerLoadSecondTaskInformManagerSequentially() {
		Integer taskId = new Integer(1);
		Integer capabilityId = new Integer(1);
		Robot cobot = null;
		//(Cobot3Robot) RobotFactory.newRobot("cobot3");
		GizmoTaskBus bus = GizmoTaskBus.connect();
		String taskPlan = "dummyleg3demo.xml";

		Cobot3Robot robot;
		MockCobot roboServer;

		//TaskManagerMockup tm = new TaskManagerMockup();
		TaskManagerWithBusMockup tm = new TaskManagerWithBusMockup(bus);

		try {
			roboServer = new MockCobot();
			roboServer.start();

			cobot = (Cobot3Robot) RobotFactory.newRobot(RobotModel.COBOT3);
			ConcurrentHashMap<Object,Object> config = new ConcurrentHashMap<Object,Object>(0);
			config.put("robot.object",cobot);

			ScriptTaskStrategy tes = new ScriptTaskStrategy(cobot, taskId, 
					taskPlan);
			tes.addObserver(tm);

			tes.parse();
			tes.execute();
			try{
				  Thread.currentThread().sleep(20000); //sleep for 20s
			} catch(Exception ie){
				ie.printStackTrace();
			}
						

			assertEquals(tm.ts.getStatus(), TaskStatus.TaskStatusValue.COMPLETE);

		} catch (TaskPlanNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
}