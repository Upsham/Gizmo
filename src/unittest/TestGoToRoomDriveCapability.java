/*
 * TestGoToRoomDriveCapability.java Jul 10, 2012 1.0
 */
package edu.cmu.gizmo.unittest;

import java.io.BufferedReader;
import java.io.IOException;
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
import edu.cmu.gizmo.management.capability.Capability.CapabilityStatus;
import edu.cmu.gizmo.management.capability.GoToRoomDriveCapability;
import edu.cmu.gizmo.management.capability.PausableCapability;
import edu.cmu.gizmo.management.robot.Cobot3Robot;
import edu.cmu.gizmo.management.robot.RobotFactory;
import edu.cmu.gizmo.management.robot.RobotFactory.RobotModel;
import edu.cmu.gizmo.management.taskbus.GizmoTaskBus;
import edu.cmu.gizmo.management.taskbus.messages.StartCapabilityMessage;
import edu.cmu.gizmo.management.taskbus.messages.TaskMessage;




/**
 * Unit tests for GoToRoomCapability.java
 * @author jsg
 *
 */
public class TestGoToRoomDriveCapability extends TestCase { 
	
	/** The robot. */
	private Cobot3Robot robot;
	
	/** The c. */
	private Capability c;
	
	/** The bus. */
	private GizmoTaskBus bus;
	
	/** The robo server. */
	private MockCobot roboServer;
	
	private ConcurrentHashMap<Object,Object> config;
	
	private ConcurrentHashMap<Object,Object> input;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() {
		System.out.println("***");
		
		bus = GizmoTaskBus.connect();
		roboServer = new MockCobot();
		roboServer.start();
		
		robot = (Cobot3Robot) RobotFactory.newRobot(RobotModel.COBOT3);
		c = new GoToRoomDriveCapability();
		
		config = 
			new ConcurrentHashMap<Object,Object>(); 
		
		config.put("robot.object",robot);
		config.put("ui.class","XXX");
		config.put("ui.display","XXX");
		
		input = 
			new ConcurrentHashMap<Object,Object>();
		input.put("room","261");
		
		
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown() {
		c.unload();
		roboServer.kill();
		roboServer = null;
		bus.disconnect();
		
	}

	/**
	 * Test sleep.
	 *
	 * @param time the time
	 */
	private void testSleep(long time) { 
		try { Thread.sleep(time); }
		catch (InterruptedException e) { } 
	}

	/**
	 * Test should send gtr command after launched and configured.
	 */
	public void testShouldSendGTRCommandAfterLaunchedAndConfigured() {

		c.load(42, 42, config); 
		c.setInput(input);
		c.launch();

		testSleep(1000);

		// send the start message
		MessageProducer p = bus.getTaskProducer();
		ObjectMessage m = 
			bus.generateMessage(
					new StartCapabilityMessage(42, 42),
					TaskMessage.START_CAPABILITY);
		try {
			p.send(m);
		} catch (JMSException e) {
			e.printStackTrace();
		}

		testSleep(3000);

		System.out.println("GOT DRIVE " + roboServer.isGotDriveCommand());
		assertTrue(roboServer.isGotDriveCommand());	

	}	

	/**
	 * Test should not send gtr command before configured.
	 */
	public void testShouldNotSendGTRCommandBeforeConfigured() {

		c.load(42, 42, config); 
		c.launch();

		testSleep(1000);

		// send the start message
		MessageProducer p = bus.getTaskProducer();
		ObjectMessage m = 
			bus.generateMessage(
					new StartCapabilityMessage(42, 42),
					TaskMessage.START_CAPABILITY);
		try {
			p.send(m);
		} catch (JMSException e) {
			e.printStackTrace();
		}

		testSleep(1000);

		assertFalse(roboServer.isGotDriveCommand());
		
	}
	
	/**
	 * Test should not allow room to change while running.
	 */
	public void testShouldNotAllowRoomToChangeWhileRunning() {

		c.load(42, 42, config); 
		c.setInput(input);
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

		testSleep(3000);
		
		input.replace("room", "162");
		c.setInput(input);
		
		if (c.getStatus()  == CapabilityStatus.RUNNING) 
			assertEquals(c.getInputParameterValue("room"), "261");
	}

	/**
	 * Test should not set invalid parameters.
	 */
	public void testShouldNotSetInvalidParameters() {

		// test that the capability must be configured before it can be RUNNING
		c.load(42, 42, config);
		c.setInput(input);

		String room = (String) c.getInputParameterValue("room");
		assertTrue(room.equals("261") == true);

		String invalidRoom = (String) c.getInputParameterValue("roo_");

		assertTrue(invalidRoom == null);
	}


	/**
	 * Test should allow room to change after terminated.
	 */
	public void testShouldAllowRoomToChangeAfterTerminated() {
		
		c.load(42, 42, config); 
		c.setInput(input);
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

		testSleep(3000);
		c.terminate();

		testSleep(1000);

		assertTrue(c.getStatus() != CapabilityStatus.RUNNING);
		
		input.replace("room","162");
		c.setInput(input);

		assertEquals(c.getInputParameterValue("room"), "162");
		
	}
	
	/**
	 * Test should set status to canceled or error after terminate.
	 */
	public void testShouldSetStatusToCanceledOrErrorAfterTerminate() {
	
		c.load(42, 42, config);
		c.setInput(input);			
		c.terminate();

		assertTrue(c.getStatus() == CapabilityStatus.CANCELED || 
				c.getStatus() == CapabilityStatus.ERROR);

	}

	/**
	 * Test should set state to paused after pause.
	 */
	public void testShouldSetStateToPausedAfterPause() { 
		
		c.load(42, 42, config); 
		c.setInput(input);

		PausableCapability p = (PausableCapability) c;
		ConcurrentHashMap<Object,Object> state = 
			(ConcurrentHashMap<Object, Object>) p.pause();

		assertTrue(c.getStatus() == CapabilityStatus.PAUSED);
		
	}	

	
	/**
	 * Test should handle invalid room exception.
	 */
	public void testShouldHandleInvalidRoomException() {

		c.load(42, 42, config); 
		
		input.replace("room", "888");
		c.setInput(input);
		c.launch();

		// send the start message
		MessageProducer p = bus.getTaskProducer();
		ObjectMessage m = bus.generateMessage(
				new StartCapabilityMessage(42, 42),
				TaskMessage.START_CAPABILITY);
		try {
			p.send(m);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		testSleep(5000);

		System.out.println(c.getStatus());
		assertTrue(c.getStatus() == CapabilityStatus.ERROR);

		c.unload();
	}	



	// ------------------------------------------------------------------------

	/**
	 * Dummy CoBot server.
	 *
	 * @author jsg
	 */
	class MockCobot extends Thread {
		
		/** The Constant INVALID_ROOM. */
		public static final String INVALID_ROOM = "888";
		
		/** The s. */
		private Socket socket;
		
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
				socket = new Socket();
				SocketAddress sockaddr = new InetSocketAddress("localhost", 4242);
				socket.connect(sockaddr);
				System.out.println("connected");
				
				running = true;
				Integer counter = 0;

				while (running) {
					BufferedReader r = new BufferedReader(
							new InputStreamReader(
									socket.getInputStream())
					);
					char[] in = new char[1024];

					System.out.println("Waiting for command");
					r.read(in,0,1024);
					String cmd = String.valueOf(in);

					PrintWriter w = new PrintWriter(
							socket.getOutputStream());

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
			running=false;
			try {
				socket.close();
			} catch (IOException e) {

			}
			running = false;
		}
	}	
}	
