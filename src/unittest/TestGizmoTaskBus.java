/*
 * TestGizmoTaskBus.java Jul 10, 2012 1.0
 */
package edu.cmu.gizmo.unittest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;

import junit.framework.TestCase;
import edu.cmu.gizmo.management.taskbus.GizmoTaskBus;


/**
 * The Class TestGizmoTaskBus.
 */
public class TestGizmoTaskBus extends TestCase {
	
	/** The recv. */
	private Boolean recv;
	
	/** The bus. */
	private GizmoTaskBus bus;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() {
		bus = GizmoTaskBus.connect();
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown() {
		bus.disconnect();
	}
	
	/**
	 * Test should not crash when message null.
	 */
	public void testShouldNotCrashWhenMessageNull() {
		final String[] SELECTORS = {
				"TEST_INPUT",	
		};
		MessageConsumer sub = bus.getTaskConsumer(SELECTORS);
		MessageProducer pub = bus.getTaskProducer();


		try {
			ObjectMessage m = 
				bus.generateMessage(null,"TEST_INPUT");
			pub.send(m);
			ObjectMessage rm = (ObjectMessage)sub.receive();
			assertNull(rm.getObject());
			
		} catch (Exception e) { 
			e.printStackTrace();
			fail("testNullMessage should not crash on null message body");
		}
	}	
	
	/**
	 * Test should accept messages using multiple selectors.
	 */
	public void testShouldAcceptMessagesUsingMultipleSelectors() {

		final String[] SELECTORS = {
				"TEST_INPUT",
				"TEST_INPUT2",
				"TEST_INPUT3"
		};
		MessageConsumer sub = bus.getTaskConsumer(SELECTORS);
		MessageProducer pub = bus.getTaskProducer();


		try {
			ObjectMessage m = 
				bus.generateMessage(new String("Hello Test"),"TEST_INPUT");
			pub.send(m);

			m = (ObjectMessage) sub.receive(10);
			String r = (String) m.getObject();			
			assertTrue(r.equals("Hello Test"));

			// ...

			m =	bus.generateMessage(new String("Hello Test2"),"TEST_INPUT2");
			pub.send(m);
			m = (ObjectMessage) sub.receive(10);
			r = (String) m.getObject();			
			assertTrue(r.equals("Hello Test2"));

			// ... 

			m =	bus.generateMessage(new String("Hello Test3"),"TEST_INPUT3");
			pub.send(m);
			m = (ObjectMessage) sub.receive(10);
			r = (String) m.getObject();			
			assertTrue(r.equals("Hello Test3"));
			
		} catch (JMSException e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
	}
		

	/**
	 * Test should not receive message with different selector.
	 */
	public void testShouldNotReceiveMessageWithDifferentSelector() {

		final String[] SELECTORS = {
				"TEST_INPUT",
		};
		MessageConsumer sub = bus.getTaskConsumer(SELECTORS);
		MessageProducer pub = bus.getTaskProducer();
		try {
			ObjectMessage m = 
				bus.generateMessage(new String("Hello Test3"),"TEST_INPUT4");
			pub.send(m);

			// timeout and make sure nothing was received
			m = (ObjectMessage) sub.receive(10);

			assertNull(m);		

		} catch (JMSException e) {
			fail(e.getMessage());
			e.printStackTrace();
		}

	}
	
	/**
	 * Test should receive message based on selector.
	 */
	public void testShouldReceiveMessageBasedOnSelector() {
	
		final String[] SELECTORS = {
				"HELLO TEST"
		};
		
		MessageConsumer sub = bus.getTaskConsumer(SELECTORS);
		MessageProducer pub = bus.getTaskProducer();
		
		try {
			ObjectMessage m = 
				bus.generateMessage(new String("Hello Test"),"HELLO TEST");
			pub.send(m);
			
			m = (ObjectMessage) sub.receive();
			
			String r = (String) m.getObject();			
			assertTrue(r.equals("Hello Test"));

		} catch (JMSException e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Test should buffer messages until subscriber running.
	 */
	public void testShouldBufferMessagesUntilSubscriberRunning() {
		
		MessageConsumer sub = bus.getTaskConsumer(new String[] {
				"HELLO TEST"
		});
		MessageProducer pub = bus.getTaskProducer();
		
		try {
			ObjectMessage m = 
				bus.generateMessage(
						new String("Hello Test"),"HELLO TEST");
			
			// send before receive. This test ensures that sent messages will
			// be queued until there is a receiver
			
			pub.send(m);
			sub.setMessageListener(new MessageListener() {
				
				@Override
				public void onMessage(Message m) {
					String r;
					try {
						r = (String) ((ObjectMessage) m).getObject();
						assertTrue(r.equals("Hello Test"));
						recv = true;
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
					
				}
			});
			
		} catch (JMSException e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
		
		// wait 1sec to see if the message survived
		try { Thread.sleep(1000); } catch (InterruptedException i) { } 
		
		if (recv == false)
			fail("Didn't receive");
		
	}
	
	/**
	 * Test should broadcast messages to all consumers.
	 */
	public void testShouldBroadcastMessagesToAllConsumers() {
		
		MessageConsumer sub = bus.getTaskConsumer(null);
		MessageConsumer sub2 = bus.getTaskConsumer(null);
		MessageProducer pub = bus.getTaskProducer();
		
		try {
		
			ObjectMessage m = 
				bus.generateMessage(new String("Hello Test"), null);
			pub.send(m);
			
			m = (ObjectMessage) sub.receive();
			String r = (String) m.getObject();
			
			ObjectMessage m2 = (ObjectMessage) sub2.receive();
			String r2 = (String) m2.getObject();
			
			// these should be the same
			assertTrue(r.equals("Hello Test"));
			assertTrue(r2.equals("Hello Test"));
			

		} catch (JMSException e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
	}

	
	/**
	 * Test should not allow messaging after resources released.
	 */
	public void testShouldNotAllowMessagingAfterResourcesReleased() {
		
		MessageConsumer sub = bus.getTaskConsumer(null);
		MessageProducer pub = bus.getTaskProducer();
		bus.releaseProducer(pub);
		bus.releaseConsumer(sub);
				
		try {
			ObjectMessage m = bus.generateMessage(new String("Test"),null);
			pub.send(m);
		} catch (JMSException e) {
			assertTrue(true);
			return;
		}
		fail("Resources not released");
	}
}