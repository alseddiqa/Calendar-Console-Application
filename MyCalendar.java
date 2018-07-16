package myFirstCalendar;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class MyCalendar {
	final int CURRENT_VIEW = 1;
	final int EVENT_VIEW = 2;

	Scanner scan = new Scanner(System.in);

	private GregorianCalendar today;
	private ArrayList<Event> events;
	private ArrayList<Event> dayEventsQ;
	private ArrayList<Event> monthEvents;

	public MyCalendar() {
		today = new GregorianCalendar();
		events = new ArrayList<Event>();

		// Show Initial Screen
		initialScreen();
	}

	/**
	 * A method to print the initial screen of the Calendar
	 */
	private void initialScreen() {
		printCalendar(this.getToday(), CURRENT_VIEW);
		printMainMenu();

		String input = "";

		while ((input = getUserSelection()) != null) {
			if (input.equalsIgnoreCase("L")) {
				loadEventsFile();
			} else if (input.equalsIgnoreCase("V")) {
				viewEventMenu();
			} else if (input.equalsIgnoreCase("C")) {
				createEventMenu();
			} else if (input.equalsIgnoreCase("G")) {
				goToMenu();
			} else if (input.equalsIgnoreCase("E")) {
				viewEvents();
			} else if (input.equalsIgnoreCase("D")) {
				delteMenu();
			} else if (input.equalsIgnoreCase("Q")) {
				quit();
			}

			printMainMenu();
		}

	}

	/**
	 * A method to delete events
	 */
	private void delteMenu() {
		System.out.println("Enter the date of the event you want to delete (format: mm/dd/yyyy):");
		String date = scan.nextLine();
		if (getEvents().size() != 0) {
			String[] parts = date.split("/");
			int m = Integer.parseInt(parts[0]);
			int d = Integer.parseInt(parts[1]);
			int y = Integer.parseInt(parts[2]);
			GregorianCalendar toDelete = new GregorianCalendar(y, m, d);
			popEvents(toDelete);
			System.out.println("Type [A]ll to delete all events on this date or [S]elected to choose one to delete");
			String option = scan.next();
			if (option.equalsIgnoreCase("A")) {
				deleteAllEvents(toDelete);
				System.out.println("You deleted all events on this date!");
			} else if (option.equalsIgnoreCase("S")) {
				System.out.println("Enter the number of event you want to delete (e.g 1)");
				scan.nextLine();
				int eventIndex = scan.nextInt();
				System.out.print("Event: ");
				deleteEvent(toDelete, eventIndex);
				System.out.print(" is deleted");
			}
		}

	}

	/**
	 * A method to save events and add them to events file
	 */
	private void quit() {
		saveEventsToFile();
		System.exit(0);
	}

	private void saveEventsToFile() {
		try {
			PrintWriter writer = new PrintWriter("events.txt", "UTF-8");
			
			for(Event event: getEvents()) {
				String title = event.getTitle();
				String date = event.getStartTime().get(Calendar.MONTH)+"/"+event.getEventDate()+"/"+event.getYearOfEvent();
				String st = event.getEventStartTime();
				String et = event.getEventEndTime();

				String eventString = title + "-";
				eventString += date + "-";
				eventString += st;
				if (et != null) {
					eventString += "-" + et;
				}
				
				writer.println(eventString);
			}
			
			writer.close();
			
			System.out.println("Events saved succesfuly to file");
			
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	/**
	 * A method to help view the day of day if it has events
	 */
	private void goToMenu() {
		System.out.println("Enter the date you want to display (format:mm/dd/yyyy)");
		String date1 = getUserInput();

		if (getEvents().size() != 0) {
			String[] parts = date1.split("/");
			int m = Integer.parseInt(parts[0]) - 1;
			int d = Integer.parseInt(parts[1]);
			int y = Integer.parseInt(parts[2]);
			GregorianCalendar displayDay = new GregorianCalendar(y, m, d);
			goTo(displayDay);
		}

	}

	/**
	 * A method to create an event and added to events list
	 */
	private void createEventMenu() {
		// Reading title from the user
		System.out.println("Enter the title of the event:");
		String title = scan.nextLine();

		// Reading date from the user
		System.out.println("Enter the date of the event (format: mm/dd/yyyy):");
		String date = scan.nextLine();
		
		// Reading start time from the user
		System.out.println("Enter the starting time (24 hours format) e.g 6:00 for 6AM or 15:00 for 3PM");
		String st = scan.next();
				
		// Reading end time from the user
		System.out.println("Enter the end time or no if there isn't");
		String option = scan.next();
				
		String eventString = title + "-";
		eventString += date + "-";
		eventString += st;
		if (!option.equalsIgnoreCase("no")) {
			eventString += "-" + option;
		}
		
		boolean eventCreated = createEvent(eventString);
		
		if(eventCreated) {
			System.out.println("Thank you your event was sucssefully added!");	
		} else {
			System.out.println("problem when adding event");
		}	
	}
	
	/**
	 * A method to check if events is created
	 * @param event to create 
	 * @return true if event is added
	 */
	private boolean createEvent(String event) {
		String[] eventElements = event.split("-");
		
		String title = eventElements[0];
		String date = eventElements[1];
		String startTime = eventElements[2];
		String endTime = null;
		if (eventElements.length == 4) {
			endTime = eventElements[3];
		}
		
		String[] parts = date.split("/");
		int m = Integer.parseInt(parts[0]) - 1; // -1 because January is
		int d = Integer.parseInt(parts[1]);
		int y = Integer.parseInt(parts[2]);
		
		String[] time = startTime.split(":");
		int hr = Integer.parseInt(time[0]);
		int min = Integer.parseInt(time[1]);
		
		// Initiating a date with time
		GregorianCalendar eventDate = new GregorianCalendar(y, m, d, hr, min);
		
		if (endTime == null) {
			// event with no end time
			Event e = new Event(title, eventDate);
			addEvent(e);
		} else {
			// event with end time
			String[] time1 = endTime.split(":");
			int h1 = Integer.parseInt(time1[0]);
			int min1 = Integer.parseInt(time1[1]);
			GregorianCalendar end = new GregorianCalendar(y, m, d, h1, min1);
			Event e = new Event(title, eventDate, end);
			addEvent(e);
		}
	
		return true;
	}

	/**
	 * A method for the view of events either in month or a day
	 */
	private void viewEventMenu() {
		GregorianCalendar cal = new GregorianCalendar();
		System.out.println("[D]ay view or [M]view ? ");
		String option = scan.nextLine();
		if (option.equals("D")) {
			while (true)
			{
				goTo(cal);
				printViewMenu();
				String input = getUserSelection();
				if (input.equalsIgnoreCase("P")) {
					cal.add(Calendar.DATE, -1);
				} else if (input.equalsIgnoreCase("N")) {
					cal.add(Calendar.DATE, 1);
				} else {
					break;
				}				
			}				
			
		} else if (option.equals("M")) {
			while (true) {
				printCalendar(cal, EVENT_VIEW);
				printViewMenu();
				String input = getUserSelection();
				if (input.equalsIgnoreCase("P")) {
					cal.add(Calendar.MONTH, -1);
				} else if (input.equalsIgnoreCase("N")) {
					cal.add(Calendar.MONTH, 1);
				} else {
					break;
				}
			}
		}
	}

	/**
	 * A method to load events from file
	 */
	private void loadEventsFile() {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("events.txt"));
			String event = br.readLine();
			
			while(event != null) {
				createEvent(event);
				event = br.readLine();
			}
			
			System.out.println("Events loaded successfully");
			
		} catch (FileNotFoundException e) {
			System.out.println("Error: can't read file");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public GregorianCalendar getToday() {
		return today;
	}

	public void setToday(GregorianCalendar today) {
		this.today = today;
	}

	public ArrayList<Event> getMonthEvents() {
		return monthEvents;
	}

	public void setMonthEvents(ArrayList<Event> monthEvents) {
		this.monthEvents = monthEvents;
	}

	public ArrayList<Event> getEvents() {
		Collections.sort(events);
		return events;
	}

	public void setEvents(ArrayList<Event> events) {
		this.events = events;
	}

	public void addEvent(Event e) {
		events.add(e);
		Collections.sort(events);
	}

	public void viewEvents() {
		if (events.size() == 0) {
			System.out.println("You do not have any events schedualed !");
			return;
		}

		int year = events.get(0).getYearOfEvent();

		System.out.println(year);
		for (int i = 0; i < events.size(); i++) {
			Event e = events.get(i);
			if (e.getYearOfEvent() != year) {
				year = e.getYearOfEvent();
				System.out.println(year);
			}
			if (e.getYearOfEvent() == year) {
				String event = formatEventLong(e);
				System.out.println(event);
			}
		}
	}

	private String formatEventLong(Event e) {
		String space = "  ";
		String event = space;
		event += e.getDayOfEvent() + " ";
		event += e.getMonthOfEvent() + " ";
		event += e.getEventDate() + " ";
		event += e.getEventStartTime();
		if (e.getEventEndTime() != null) {
			event += " - " + e.getEventEndTime();
		}
		event += " " + e.getTitle();

		return event;
	}

	private String formatEventShort(Event e) {
		String event = "";
		event += e.getTitle() + " ";
		event += e.getEventStartTime() + " ";
		if (e.getEventEndTime() != null) {
			event += "- " + e.getEventEndTime();
		}

		return event;
	}

	/**
	 * A method to delete all events in calendar
	 * @param date
	 */
	public void deleteAllEvents(GregorianCalendar date) {

		int y = date.get(Calendar.YEAR);
		int m = date.get(Calendar.MONTH) - 1;
		int d = date.get(Calendar.DATE);

		Iterator<Event> iterator = events.iterator();
		while (iterator.hasNext()) {
			Event e = iterator.next();
			if (e.getYearOfEvent() == y && (e.getStartTime().get(Calendar.MONTH)) == m && e.getEventDate() == d) {
				iterator.remove();
			}
		}
	}

	public void popEvents(GregorianCalendar date) {

		ArrayList<Event> dayEvents = new ArrayList<>();
		int y = date.get(Calendar.YEAR);
		int m = date.get(Calendar.MONTH) - 1;
		int d = date.get(Calendar.DATE);
		int eventNum = 1;
		for (Event e : events) {
			if (e.getYearOfEvent() == y && (e.getStartTime().get(Calendar.MONTH)) == m && e.getEventDate() == d) {
				dayEvents.add(e);
			}
		}

		if (dayEvents.size() == 0) {
			System.out.println("There are no events in this day");
			return;
		} else {
			System.out.println("In this day you have these events:");
			for (Event e : dayEvents) {
				System.out.println(eventNum + ") " + formatEventShort(e));
				eventNum++;
			}
		}

	}

	/**
	 * A method to delete an event with a specified date
	 * @param date of event to delete
	 * @param eventIndex
	 */
	public void deleteEvent(GregorianCalendar date, int eventIndex) {
		ArrayList<Event> dayEvents = new ArrayList<>();
		int y = date.get(Calendar.YEAR);
		int m = date.get(Calendar.MONTH) - 1;
		int d = date.get(Calendar.DATE);
		for (Event e : events) {
			if (e.getYearOfEvent() == y && (e.getStartTime().get(Calendar.MONTH)) == m && e.getEventDate() == d) {
				dayEvents.add(e);
			}
		}
		Event e = dayEvents.get(eventIndex - 1);
		events.remove(e);
		System.out.print(e.getTitle());
		Collections.sort(events);
	}

	/**
	 * A method to display the events for a specified date of event
	 * @param date
	 */
	public void goTo(GregorianCalendar date) {
		ArrayList<Event> dayEvents = new ArrayList<>();
		int y = date.get(Calendar.YEAR);
		int m = date.get(Calendar.MONTH);
		int d = date.get(Calendar.DATE);
		for (Event e : events) {
			if (e.getYearOfEvent() == y && (e.getStartTime().get(Calendar.MONTH)) == m && e.getEventDate() == d) {
				dayEvents.add(e);
			}
		}
		Event temp = new Event("t", date);
		System.out.println(temp.getDayOfEvent() + ", " + temp.getMonthOfEvent() + " " + temp.getEventDate() + ", "
				+ temp.getYearOfEvent());

		if (dayEvents.size() == 0) {
			System.out.println("No events for this day");
		} else {
			for (Event e : events) {
				String event = formatEventShort(e);
				System.out.println(event);
			}
		}
	}

	/**
	 * A method to show the view of initial screen of calendar
	 * 
	 * @param c
	 *            is the Calendar to print
	 */
	public void printCalendar(Calendar c, int option) {
		MONTHS[] arrayOfMonths = MONTHS.values();
		int date = 1;
		// Getting the number of days in a month
		int daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		GregorianCalendar temp = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
		int firstDayOfMonth = temp.get(Calendar.DAY_OF_WEEK) - 1;
		System.out.println("    " + arrayOfMonths[c.get(Calendar.MONTH)] + " " + c.get(Calendar.YEAR));
		System.out.println("Su " + "Mo " + "Tu " + "We " + "Th " + "Fr " + "Sa");

		HashSet<Integer> eventDays = getMonthEventDays(c);
		// Print initial empty days cells
		for (int i = 0; i < firstDayOfMonth; i++) {
			System.out.print("   ");
		}

		// Print the dates in the Calendar
		int i = firstDayOfMonth;
		while (date <= daysInMonth) {
			for (; i <= 6 && date <= daysInMonth; i++) {
				System.out.print(getDayString(c, date, eventDays, option));
				date++;
			}
			i = 0;
			System.out.println();

		}
	}

	private String getDayString(Calendar c, int day, HashSet<Integer> eventDays, int option) {

		String date = "";

		if (option == CURRENT_VIEW) {
			if (day < 10) {
				if (day == c.get(Calendar.DATE)) {
					date = "[" + day + "]";
				} else {
					date = " " + day + " ";
				}

			} else {
				if (day == c.get(Calendar.DATE)) {
					date = "[" + day + "]";
				} else {
					date = day + " ";
				}

			}
		} else if (option == EVENT_VIEW) {
			if (eventDays.contains(day)) {
				date = "{" + day + "}";
			} else {
				if (day < 10) {
					date = " " + day + " ";
				} else {
					date = day + " ";
				}
			}
		}

		return date;

	}

	private HashSet<Integer> getMonthEventDays(Calendar c) {
		HashSet<Integer> daysSet = new HashSet<>();
		int month = c.get(Calendar.MONTH);
		for (Event event : getEvents()) {
			if (event.getStartTime().get(Calendar.MONTH) == month) {
				daysSet.add(event.getEventDate());
			}
		}

		return daysSet;
	}

	public String getUserInput() {
		String input = "";
		input = scan.nextLine();

		return input;
	}

	public String getUserSelection() {
		String input = "";
		input = scan.next();
		scan.nextLine();

		return input;
	}

	public void printMainMenu() {
		System.out.println();
		System.out.println("Select one of the following options:");
		System.out.println("[L]oad   [V]iew by  [C]reate, [G]o to [E]vent list [D]elete  [Q]uit");
	}

	public static void printViewMenu() {
		System.out.println("[P]revious or [N]ext or [M]ain menu ?");
	}

}
