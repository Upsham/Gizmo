package unittest;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import junit.framework.TestCase;
import edu.cmu.gizmo.management.dataaccess.jdbc.ScheduleDBAccessImpl;
import edu.cmu.gizmo.management.taskmanager.TaskReservation;

public class TestScheduleDB extends TestCase {

	public void testShouldLoadTheNextTaskScheduledToStartInTheCurrentHour() {
		
		ScheduleDBAccessImpl schedDB = new ScheduleDBAccessImpl();
		String user = "mzayer";
		
		Calendar cal = Calendar.getInstance();
			
		java.util.Date date = cal.getTime();
		Timestamp timestamp = new Timestamp(date.getTime());
		
		Integer duration = 60;
		String taskName = "FindPerson";
		String scriptName = "FindPerson.xml";

		assertTrue(schedDB.addScheduleEntry(user, timestamp, duration, taskName, scriptName));

		TaskReservation rsvp = schedDB.loadNextScheduledTask("mzayer");
		
		assertTrue(rsvp != null);
		
		//schedDB.deleteScheduleEntry(user, timestamp, duration, taskName, scriptName);
		schedDB.close();
	}

	public void testShouldReturnNullIfNoTaskExistsForSpecifiedUser() {
		
		ScheduleDBAccessImpl schedDB = new ScheduleDBAccessImpl();
		String user = "nobody";
		
		Calendar cal = Calendar.getInstance();
			
		java.util.Date date = cal.getTime();
		Timestamp timestamp = new Timestamp(date.getTime());
		
		Integer duration = 60;
		String taskName = "FindPerson";
		String scriptName = "FindPerson.xml";

		//schedDB = new ScheduleDBAccessImpl();
		TaskReservation rsvp = schedDB.loadNextScheduledTask("test");
		
		assertNull(rsvp);
		
		schedDB.close();
	}

	
	public void testShouldAddAScheduleEntryWithCorrectArguments() {

		ScheduleDBAccessImpl schedDB = new ScheduleDBAccessImpl();
		String user = "test";
		
		Calendar cal = Calendar.getInstance();
			
		java.util.Date date = cal.getTime();
		Timestamp timestamp = new Timestamp(date.getTime());
		
		Integer duration = 60;
		String taskName = "FindPerson";
		String scriptName = "FindPerson.xml";

		assertTrue(schedDB.addScheduleEntry(user, timestamp, duration, taskName, scriptName));

		//schedDB.deleteScheduleEntry(user, timestamp, duration, taskName, scriptName);
		
		schedDB.close();
	}

	public void testShouldNotAllowConflictingScheduleEntries() {

		ScheduleDBAccessImpl schedDB = new ScheduleDBAccessImpl();
		String user = "test";
		
		Calendar cal = Calendar.getInstance();
			
		java.util.Date date = cal.getTime();
		Timestamp timestamp = new Timestamp(date.getTime());
		
		Integer duration = 60;
		String taskName = "FindPerson";
		String scriptName = "FindPerson.xml";

		assertTrue(schedDB.addScheduleEntry(user, timestamp, duration, taskName, scriptName));
		assertTrue(schedDB.addScheduleEntry(user, timestamp, duration, taskName, scriptName) == false);
		
		//schedDB.deleteScheduleEntry(user, timestamp, duration, taskName, scriptName);
		
		schedDB.close();
	}
	
}
