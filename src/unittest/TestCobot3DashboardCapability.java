/*
 * TestCobot3Dashboard.java Jul 10, 2012 1.0
 */
package edu.cmu.gizmo.unittest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;

import junit.framework.TestCase;
import edu.cmu.gizmo.management.capability.Capability;
import edu.cmu.gizmo.management.capability.Cobot3DashboardCapability;
import edu.cmu.gizmo.management.robot.Cobot3Robot;
import edu.cmu.gizmo.management.robot.RobotFactory;
import edu.cmu.gizmo.management.robot.RobotFactory.RobotModel;
import edu.cmu.gizmo.management.taskbus.GizmoTaskBus;
import edu.cmu.gizmo.management.taskbus.messages.StartCapabilityMessage;
import edu.cmu.gizmo.management.taskbus.messages.TaskMessage;


/**
 * The Class TestCobot3DashboardCapability.
 */
public class TestCobot3DashboardCapability extends TestCase {

	/** The bus. */
	private GizmoTaskBus bus;
	private Capability c;
	private MockCobotForDashboard mockBot;
	private Cobot3Robot robot;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() {
		System.out.println("***");
		// set up unconnected dummy robot
		bus = GizmoTaskBus.connect();
		mockBot = new MockCobotForDashboard();
		mockBot.start();

		robot = (Cobot3Robot) RobotFactory.newRobot(RobotModel.COBOT3);
		
		c = new Cobot3DashboardCapability();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown() {
		bus.disconnect();
		c.unload();
		mockBot.kill();
	}

	/**
	 * Test sleep.
	 *
	 * @param time the time
	 */
	private void testSleep(long time ) { 
		try { 
			Thread.sleep(time);
		} catch (InterruptedException e) { } 
	}

	/**
	 * Test should allow the user to control the camera.
	 */
	public void testShouldAllowTheUserToControlTheCamera() { 

		ConcurrentHashMap<Object,Object> config = 
			new ConcurrentHashMap<Object,Object>(); 

		
		config.put("robot.object",robot);
		config.put("ui.class","XXX");
		config.put("ui.display","XXX");
		c.load( 42, 42, config);
		c.launch();

		testSleep(1000);

		// send the start message
		MessageProducer p = bus.getTaskProducer();
		ObjectMessage m = bus.generateMessage(
				new StartCapabilityMessage(42, 42),
				TaskMessage.START_CAPABILITY);
		try {
			p.send(m);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		bus.releaseProducer(p);
		testSleep(1000);

		Float[] point = { new Float(0.0), new Float(0.0) };
		
		ConcurrentHashMap<Object, Object> input = 
			new ConcurrentHashMap<Object, Object>();
		
			
		input.put("camera", point);
		c.setInput(input);
		

		testSleep(100);

		assertTrue(mockBot.movedCamera() == true);

	}

	public void testShouldAllowTheUserToIncrementallyMoveCobot() { 

		ConcurrentHashMap<Object,Object> config = 
			new ConcurrentHashMap<Object,Object>(); 

		
		config.put("robot.object",robot);
		config.put("ui.class","XXX");
		config.put("ui.display","XXX");
		c.load( 42, 42, config);
		c.launch();

		testSleep(1000);

		// send the start message
		MessageProducer p = bus.getTaskProducer();
		ObjectMessage m = bus.generateMessage(
				new StartCapabilityMessage(42, 42),
				TaskMessage.START_CAPABILITY);
		try {
			p.send(m);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		bus.releaseProducer(p);
		testSleep(1000);

		Float[] point = { new Float(0.0), new Float(0.0) };
		
		ConcurrentHashMap<Object, Object> input = 
			new ConcurrentHashMap<Object, Object>();
		
			
		input.put("moveCobot", point);
		c.setInput(input);
		
		testSleep(100);

		assertTrue(mockBot.movedCobot() == true);

	}

	
	public void testShouldSendCobotImagesAsOuptutOnDemand() { 

		ConcurrentHashMap<Object,Object> config = 
			new ConcurrentHashMap<Object,Object>(); 

		config.put("robot.object",robot);
		config.put("ui.class","XXX");
		config.put("ui.display","XXX");
		c.load( 42, 42, config);

		c.launch();

		testSleep(1000);

		// send the start message
		MessageProducer p = bus.getTaskProducer();
		ObjectMessage m = bus.generateMessage(
				new StartCapabilityMessage(42, 42),
				TaskMessage.START_CAPABILITY);
		try {
			p.send(m);
		} catch (JMSException e) {
			e.printStackTrace();
		}		

		bus.releaseProducer(p);
		testSleep(5000);

		assertTrue(mockBot.sentImage() == true);
	}

	//public void testShouldSendCobotMapAsOuptutOnDemand() { 

	//	}

	/**
	 * Dummy CoBot server.
	 *
	 * @author jsg
	 */
	class MockCobotForDashboard extends Thread {

		/** The Constant INVALID_ROOM. */
		public static final String INVALID_ROOM = "888";

		/** The s. */
		private Socket s;

		/** The running. */
		private Boolean running;

		/** The got drive command. */
		private Boolean gotDriveCommand;

		/** The got image request. */
		private Boolean gotImageRequest;

		/** The camera moved. */
		private Boolean cameraMoved;
		
		private Boolean cobotMoved;
		

		/**
		 * Instantiates a new mock cobot for dashboard.
		 */
		public MockCobotForDashboard() {
			gotDriveCommand = false;
			gotImageRequest = false;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			try {

				cameraMoved = false;
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

					if (arr[0].equals("MoveCamera")) {
						cameraMoved = true;
						System.out.println(":) MoveCamera received");
					}
					else if (arr[0].equals("MoveCobot")) {
						cobotMoved = true;
						System.out.println(":) MoveCobot received");
					}	
					else if (arr[0].trim().equals("GetImage")) {
						gotImageRequest = true;
						String b64 = new String("aGVsbG8gdGhlcmUh");

						w.println(b64.length());
						w.println(b64);
					}
					else if (arr[0].equals("GoToRoom")) {
						gotDriveCommand = true;
						if (counter < 5) {
							w.println("success,42"); 
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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * Moved camera.
		 *
		 * @return the boolean
		 */
		public Boolean movedCamera() {
			return cameraMoved; 

		}

		public Boolean movedCobot() {
			return cobotMoved; 
		}

		
		public Boolean sentImage() { 
			return gotImageRequest;
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
			running = false;
			try {
				s.close();
			} catch (Exception e) {

			}
		}
	}	
}
