package manager.model;

import manager.BookingManager;
import manager.model.data.Guest;
import manager.model.data.Room;
import manager.model.exceptions.RoomException;
import manager.model.exceptions.RoomNotAvailableException;
import manager.model.exceptions.RoomNotFoundException;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HotelManager implements BookingManager {

	private final Map<Integer, Room> roomMap = new ConcurrentHashMap<>();
	private final Map<Room, HashMap<LocalDate, Guest>> bookingCalendar = new ConcurrentHashMap<>();

	public HotelManager(){ // creates a hotel with 10 rooms, from 101 - 110
		for(int i = 1; i < 11; i++)
			addRoom(100+i);
	}

	public HotelManager(int x){ // creates a hotel with x rooms, from 0 onward, for ease of testing - this should be read from db
		for(int i = 0; i < x; i++)
			addRoom(i);
	}

	@Override
	public boolean isRoomAvailable(Integer room, LocalDate date){
		Room r = getRoom(room);
		if(r == null) return false;
		return isRoomAvailable(r, date);
	}

	private boolean isRoomAvailable(Room room, LocalDate date){
		HashMap<LocalDate, Guest> calendar = bookingCalendar.get(room);
		return calendar == null || calendar.get(date) == null;
	}

	@Override
	public void addBooking(String guest, Integer room, LocalDate date) throws RoomException {
		Room r = getRoom(room);
		if (r == null) throw new RoomNotFoundException("Room "+room+" does not exist");
		if(isRoomAvailable(r, date)){
			bookingCalendar.get(r).put(date, new Guest(guest));
		} else throw new RoomNotAvailableException("Room "+room+" is not available");
	}

	@Override
	public Iterable<Integer> getAvailableRooms(LocalDate date) {
		return roomMap.entrySet()
				.stream()
				.filter(entry -> isRoomAvailable(entry.getValue(), date))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	private void addRoom(Integer room){
		Room newRoom = new Room(room);
		roomMap.put(room, newRoom);
		bookingCalendar.put(newRoom, new HashMap<>());
	}

	public Room getRoom(Integer room){
		return roomMap.get(room);
	}

	public Guest getGuestOnDate(Integer room, LocalDate date){
		return bookingCalendar.get(getRoom(room)).get(date);
	}
}
