package edu.cmu.gizmo.unittest;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import junit.framework.TestCase;
import edu.cmu.gizmo.management.taskorchestrator.CapabilityResourceTable;

public class TestCapabilityResourceTable extends TestCase {
	public void testShouldCreateDataStructure() {
		CapabilityResourceTable capabilityResourceTable = new CapabilityResourceTable();
		capabilityResourceTable.createDataStructure();

		ConcurrentHashMap<String, Vector<String>> keyValueLookup = (ConcurrentHashMap<String, Vector<String>>)capabilityResourceTable.getKeyValueLookup();

		for(Map.Entry<String, Vector<String>> entry: keyValueLookup.entrySet()) {
			String key = (String)entry.getKey();
			Vector<String> value = (Vector<String>)keyValueLookup.get((Object)key);
			for(int i=0; i < value.size(); ++i) {
				System.out.println("key: " + key + " value.get(i): " + value.get(i));
			}
		}

		ConcurrentHashMap<String, Vector<String>> capabilityLookup = (ConcurrentHashMap<String, Vector<String>>)capabilityResourceTable.getCapabilityLookup();
		for(Map.Entry<String, Vector<String>> entry: capabilityLookup.entrySet()) {
			String key = (String)entry.getKey();
			Vector<String> value = (Vector<String>)capabilityLookup.get((Object)key);
		}
	}
	
	public void testShouldReturnNumOfCapabilityForKeyValue() {
		CapabilityResourceTable capabilityResourceTable = new CapabilityResourceTable();
		capabilityResourceTable.createDataStructure();
		assertEquals("3", new Integer(capabilityResourceTable.returnNumOfCapabilityForKeyValue("[robot.exist,true]")).toString());
	}
	
	
	public void testShouldReturnCapabilityInput() {
		CapabilityResourceTable capabilityResourceTable = new CapabilityResourceTable();
		capabilityResourceTable.createDataStructure();
		Vector<String> inputList = capabilityResourceTable.returnCapabilityInput("SkypeCommunicationCapability");
		assertEquals("1", new Integer(inputList.size()).toString());
	}
	
	public void testShouldReturnCapabilityOutput() {
		CapabilityResourceTable capabilityResourceTable = new CapabilityResourceTable();
		capabilityResourceTable.createDataStructure();
		Vector<String> outputList = capabilityResourceTable.returnCapabilityOutput("SkypeCommunicationCapability");
		assertEquals("1", new Integer(outputList.size()).toString());
	}
		
}
