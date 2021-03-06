/*
 * TestCapability.java 1.1 2012-06-29
 */

package edu.cmu.gizmo.unittest;

import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;

import junit.framework.TestCase;
import edu.cmu.gizmo.management.capability.Capability;
import edu.cmu.gizmo.management.capability.Capability.CapabilityStatus;
import edu.cmu.gizmo.management.capability.PausableCapability;
import edu.cmu.gizmo.management.taskbus.GizmoTaskBus;
import edu.cmu.gizmo.management.taskbus.messages.CapabilityInputMessage;
import edu.cmu.gizmo.management.taskbus.messages.CapabilityOutputMessage;
import edu.cmu.gizmo.management.taskbus.messages.StartCapabilityMessage;
import edu.cmu.gizmo.management.taskbus.messages.TaskMessage;



class MockCapability extends Capability implements PausableCapability {
	private String room; 
	private Boolean complete = false;
	private Integer runCount = 0;
	
	@Override
	public void terminate() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public String getCapabilityName() {
		
		return null;
	}
	
	public Object getConfigurationValue(String key) { 
		return super.getConfigurationValue(key);
	}

	@Override
	public String getCapabilityDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Boolean isComplete() { 
		return complete;
	}

	public Integer getRunCount() { 
		return runCount;
	}
	
	@Override
	public void execute() {
		runCount++;
		sendOutput("mockoutput", "testoutput");
		setStatus(CapabilityStatus.COMPLETE, TaskMessage.CAPABILITY_COMPLETE);
		complete = true;
	}

	@Override
	public void setInput(ConcurrentHashMap<Object, Object> input) {
		
		if (input.containsKey("room")) {
			if (input.get("room") != null) {
				room = (String) input.get("room");
			}
		}		
	}

	@Override
	public Object getInputParameterValue(Object param) {
		if (((String) param).equals("room")) {
			return room;
		}
		return null;
	}

	@Override
	public Object pause() {
		return null;
	}

	@Override
	public void resume(Object state) {
		// TODO Auto-generated method stub		
	}

	@Override
	public ConcurrentHashMap<String, Class> getInputRequirements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConcurrentHashMap<String, Class> getOutputRequirements() {
		ConcurrentHashMap<String,Class>outputReqs = 
			new ConcurrentHashMap<String,Class>();

		outputReqs.put("mockoutput", String.class);
		return outputReqs;
	}	
}

/**
 * Unit test the Capability Messages.
 *
 * @author jsg
 */
public class TestCapability extends TestCase {
	
	/** The helo. */
	private Boolean helo;
	
	/** The complete. */
	private Boolean complete;
	
	/** The output. */
	private Boolean output;
	
	/** The c. */
	private Capability c;
	
	/** The bus. */
	private GizmoTaskBus bus;
	
	private ConcurrentHashMap<Object, Object> defaultSettings;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() { 
		complete = false;
		helo = false;
		output = false;
		c = new MockCapability();
		bus = GizmoTaskBus.connect();
		defaultSettings = new ConcurrentHashMap<Object, Object>();
		
		defaultSettings.put("test", "setting");
		defaultSettings.put(Capability.UI_CLASS, "XXX");
		defaultSettings.put(Capability.UI_DISPLAY, "YYY");
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown() {
		c.unload();
		bus.disconnect();
	}
	
	/**
	 * Test should set state to loaded with valid parameters.
	 */
	public void testShouldSetStateToLoadedWithValidParameters() {
		
		c.load(42, 42,defaultSettings);
		assertTrue(c.getStatus() == CapabilityStatus.LOADED);
	}


	/**
	 * Test should not set state to loaded without valid parameters.
	 */
	public void testShouldNotSetStateToLoadedWithoutValidParameters() {
		c.load(null, null, null);
		assertTrue(c.getStatus() != CapabilityStatus.LOADED);
		
		c.load(null, 42, null);
		assertTrue(c.getStatus() != CapabilityStatus.LOADED);
		
		c.load(42, null, null);
		assertTrue(c.getStatus() != CapabilityStatus.LOADED);
	}
	
	
	/**
	 * Test should set input through capability input message.
	 */
	public void testShouldSetInputThroughCapabilityInputMessage() {
		
		MessageProducer pub = bus.getTaskProducer();
		
		c.load(42, 42, defaultSettings);
		
		ConcurrentHashMap<Object, Object> inHash = new ConcurrentHashMap<Object, Object>();
		inHash.put(new String("room"),new String("777"));
		CapabilityInputMessage msg = new CapabilityInputMessage(42, 42, inHash);	
		
		try {
			
			ObjectMessage om = 
				bus.generateMessage(msg,TaskMessage.CAPABILITY_INPUT);
			
			om.setObject((Serializable) msg);
			pub.send(om);
			 
		} catch (JMSException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try { Thread.sleep(1000); } 
		catch (InterruptedException e) { } 
		
		// this should be set
		assertEquals(c.getInputParameterValue("room"), "777");
	}
	
	/**
	 * Test should only process messages with correct i ds.
	 */
	public void testShouldOnlyProcessCapabilityMessagesWithCorrectIDs() {
		
		MessageProducer pub = bus.getTaskProducer();
		
		c.load(42, 42, defaultSettings);
		
		
		ConcurrentHashMap<Object, Object> inHash = new ConcurrentHashMap<Object, Object>();
		inHash.put(new String("room"),new String("777"));
			
		// invalid task id
		CapabilityInputMessage msg = new CapabilityInputMessage(43, 42, inHash);
		c.load(42, 42, null);
		
		inHash = new ConcurrentHashMap<Object, Object>();
		inHash.put(new String("room"),new String("888"));
		
		// invalid capability id
		CapabilityInputMessage msg2 = new CapabilityInputMessage(42, 43, inHash);
		
		
		try {		
			ObjectMessage om = 
				bus.generateMessage(msg, TaskMessage.CAPABILITY_INPUT);
			pub.send(om);
			
			om = bus.generateMessage(msg2, TaskMessage.CAPABILITY_INPUT);
			pub.send(om);
			
			
		} catch (JMSException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try { Thread.sleep(1000); } 
		catch (InterruptedException e) { } 
		
		// testShould not have been set
		assertEquals(c.getInputParameterValue("room"), null);		
	}
	
	/**
	 * Test should not set payload when invalid.
	 */
	public void testShouldNotSetPayloadWhenInvalid() {
		
		MessageProducer pub = bus.getTaskProducer();
		
		c.load(42, 42, defaultSettings);

		// invalid payload		
		CapabilityInputMessage msg = new CapabilityInputMessage(42, 42,null);
		
		try {		
			ObjectMessage om = bus.generateMessage(msg,null);
			
			pub.send(om);
						
		} catch (JMSException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try { Thread.sleep(1000); } 
		catch (InterruptedException e) { } 
		
		// testShould not have been set
		assertEquals(c.getInputParameterValue("room"), null);		
	}
	
	
	/**
	 * Test should not run until start message received.
	 */
	public void testShouldNotRunUntilStartMessageReceived() {
		
		MessageProducer pub = bus.getTaskProducer();
		try {	
			final String[] SELECTORS = {
					TaskMessage.HELO_CLIENT
			};
			
			bus.getTaskConsumer(SELECTORS).setMessageListener(
					new MessageListener() {
				
				@Override
				public void onMessage(Message arg0) {
					// send the start
					
					MessageProducer pub = bus.getTaskProducer();
					
					StartCapabilityMessage scm = 
						new StartCapabilityMessage(42, 42); 
					
					try {	
					pub.send(
							bus.generateMessage(
									scm, TaskMessage.START_CAPABILITY));
					} catch (JMSException e) {
						e.printStackTrace();
					}
					
				}
			});
	
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
		// Should not run 
		assertTrue(c.getStatus() != CapabilityStatus.RUNNING);
		
		c.load(42, 42, defaultSettings);	
		
		try { Thread.sleep(1000); } 
		catch (InterruptedException e) { } 
		
		assertTrue(c.getStatus() == CapabilityStatus.RUNNING);		
		
	}
	
	/**
	 * Test should not be running without first loading.
	 */
	public void testShouldNotBeRunningWithoutFirstLoading() {		
		assertTrue(c.getStatus() != CapabilityStatus.RUNNING);		
	}
	
	
	
	/**
	 * Test should capability sends helo client on capability load.
	 */
	public void testShouldCapabilitySendsHeloClientOnCapabilityLoad() {
		
		helo = false;
		
		try {	
			final String[] SELECTORS = {
					TaskMessage.HELO_CLIENT
			};
			
			bus.getTaskConsumer(SELECTORS).setMessageListener(
					new MessageListener() {
				
				@Override
				public void onMessage(Message arg0) {
					helo = true;					
				}
			});
	
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
		c.load(42, 42, defaultSettings);		
		
		try { Thread.sleep(1000); } 
		catch (InterruptedException e) { } 
		
		assertTrue(helo);
		helo = false;
	}
	
	/**
	 * Test should send capability complete message when capability completed.
	 */
	public void testShouldSendCapabilityCompleteMessageWhenCapabilityCompleted() {
			
		c.load(42, 42, defaultSettings);
		
		try {	
			final String[] SELECTORS = {
					TaskMessage.CAPABILITY_COMPLETE
			};
			
			bus.getTaskConsumer(SELECTORS).setMessageListener(
					new MessageListener() {
				
				@Override
				public void onMessage(Message arg0) {
					complete = true;					
				}
			});
		 
		} catch (JMSException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		c.launch();
		
		try { Thread.sleep(1000); } 
		catch (InterruptedException e) { }
		
		assertTrue(complete);
	}
	
	/**
	 * Test should run capabilities in parallel.
	 */
	public void testShouldRunCapabilitiesInParallel() {
		Capability c2 = new MockCapability();
		
		c.load(42, 42,defaultSettings);
		c2.load(42, 43,defaultSettings);
		
		c.launch();
		c2.launch();
		
		try { Thread.sleep(1000); } 
		catch (InterruptedException e) { }
	
		// both capabilities have run to completion
		assertTrue(((MockCapability) c).isComplete());
		assertTrue(((MockCapability) c2).isComplete());
		
		c2.unload();
		c.unload();
	}
	
	/**
	 * Test should run capabilities in sequence.
	 */
	public void testShouldRunCapabilitiesInSequence() {
		Capability c2 = new MockCapability();
		
		c.load(42, 42, defaultSettings);
		c2.load(42, 43, defaultSettings);
		
		c.launch();
		
		try { Thread.sleep(1000); } 
		catch (InterruptedException e) { }
		
		assertTrue(((MockCapability) c).isComplete());
		
		c.unload();
		c2.launch();
				
		try { Thread.sleep(1000); } 
		catch (InterruptedException e) { }
		
		assertTrue(((MockCapability) c2).isComplete());
		
		c2.unload();
	}
	
	
	/**
	 * Test should send capability output through bus message.
	 */
	public void testShouldSendCapabilityOutputThroughBusMessage() {
		c.load(42, 42, defaultSettings);
		
		try {	
			final String[] SELECTORS = {
					TaskMessage.CAPABILITY_OUTPUT
			};
			
			bus.getTaskConsumer(SELECTORS).setMessageListener(
					new MessageListener() {
				
				@Override
				public void onMessage(Message arg0) {
					
					ObjectMessage om = (ObjectMessage)arg0;
					try {
						if (!(om.getObject() instanceof CapabilityOutputMessage))
							fail("Wrong object: Not a Capability Output message");

						CapabilityOutputMessage out = 
							(CapabilityOutputMessage) om.getObject();

						ConcurrentHashMap<Object,Object> outVal =
							(ConcurrentHashMap<Object, Object>) out.getOutput();


						ConcurrentHashMap<String,Class> outReq = c.getOutputRequirements();
						Iterator i = outReq.keySet().iterator();
						
						while (i.hasNext()) {
							String k = (String) i.next();
							// make sure the output is correct
							if (k.equals("mockoutput") && outVal.get(k).equals("testoutput")) {
								output = true;
							}	
						}			

					} catch (JMSException e) {
						output = false;
						e.printStackTrace();
					}					
				}
			});
		 
		} catch (JMSException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		c.launch();
		
		try { Thread.sleep(1000); } 
		catch (InterruptedException e) { }
		
		assertTrue(output);
	}	
	
	public void testShouldHaveAccessToManifestConfiguration() { 
		c.load(42, 42, defaultSettings);
		
		assertTrue(((MockCapability)c).getConfigurationValue("test").equals("setting") );
	}
}
