package com.teamchat.integration.meetup.test;
import java.util.*;

import com.teamchat.integration.meetup.library.Event;
import com.teamchat.integration.meetup.library.EventSearchCriteria;

public class EventTest extends AbstractClientTest
{
	 

	public void callGetEventByZipCode() throws Exception
	{
		EventSearchCriteria crit = new EventSearchCriteria();
		
		crit.setZipCode("97206");
		crit.setRadiusInMiles(20);
		
		List<Event> events = getClient().getEvents(crit);
		
		assertNotNull(events);
		
		assertTrue(events.size() > 0);
		
		
	}

	public void callGetEventByTopic() throws Exception
	{
		EventSearchCriteria crit = new EventSearchCriteria();
		
		crit.setZipCode("97206");
		crit.setTopic("singles");
		crit.setRadiusInMiles(10);
		
		List<Event> events = getClient().getEvents(crit);
		
		assertNotNull(events);
		
		assertTrue(events.size() > 0);
		
		
	}
	
	public void callGetEventByLatLong() throws Exception
	{
		EventSearchCriteria crit = new EventSearchCriteria();
		
        // PGE Park in Portland Oregon:   45.521694,-122.691806
		
		crit.setLatitude("45.521694");
		crit.setLongitude("-122.691806");
		crit.setRadiusInMiles(2);
		
		List<Event> events = getClient().getEvents(crit);
		
		assertNotNull(events);
		
		assertTrue(events.size() > 0);
		
	}
	
	public String callGetEventByCity(String city) throws Exception
	{
		EventSearchCriteria crit = new EventSearchCriteria();
		
		crit.setCity(city);
		String state = "";
		
		if (city.equalsIgnoreCase("Delhi")){
			state = "Delhi";
		} else if (city.equalsIgnoreCase("Mumbai")){
			state = "Maharashtra";
		} else if (city.equalsIgnoreCase("Chennai")){
			state = "Tamil Nadu";
		} else if (city.equalsIgnoreCase("Kolkata")){
			state = "West Bengal";
		} else if (city.equalsIgnoreCase("Hyderabad")){
			state = "Andhra Pradesh";
		}
		crit.setState(state);
		crit.setCountry("in");
		
		//List<Event> events = getClient().getEvents(crit);
		
		String eventsJson = getClient().getEventsAkshit(crit);
		
		System.out.println(eventsJson);
		
		return eventsJson;
		
		//assertNotNull(events);
		
		//assertTrue(events.size() > 0);
		
	}

	public void callBeforeAndAfter() throws Exception
	{
		EventSearchCriteria crit = new EventSearchCriteria();
		
		crit.setCity("Chicago");
		crit.setState("IL");
		crit.setCountry("US");
		
		Calendar after = Calendar.getInstance();
		after.add(Calendar.DAY_OF_YEAR, 2);
		
		Calendar before = Calendar.getInstance();
		before.add(Calendar.DAY_OF_YEAR, 5);

		crit.setBefore(before);
		crit.setAfter(after);
		
		List<Event> events = getClient().getEvents(crit);
		
		assertNotNull(events);
		
		assertTrue(events.size() > 0);
	}
}