package manager;

import manager.model.exceptions.RoomException;
import manager.model.exceptions.RoomNotAvailableException;
import manager.model.HotelManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HotelManagerTest {

	int top = 10;
	HotelManager manager;
	String[] guests;
	LocalDate[] dates;

	@BeforeEach
	void setUp() {
		manager = new HotelManager(top);
		guests = new String[top];
		dates = new LocalDate[top];
		for(int i = 0; i < top; i++){
			guests[i] = "g" + i;
			dates[i] = LocalDate.ofYearDay(2022, i+1);
		}
	}

	@Test
	void isRoomAvailable() throws RoomException {
		for(int i = 0; i < top; i++)
			assert manager.isRoomAvailable(i, dates[i]);
		manager.addBooking("g1", 0, dates[0]);
		assert !manager.isRoomAvailable(0, dates[0]);
		for(int i = 1; i < top; i++)
			assert manager.isRoomAvailable(0, dates[i]);
	}

	@Test
	void addBooking() throws RoomException {
		for(int i = 0; i < top; i++) {
			for(int r = 0; r < i; r++) {
				assert !manager.isRoomAvailable(r, dates[r]);
			}

			assert manager.isRoomAvailable(i, dates[i]);
			manager.addBooking(guests[i], i, dates[i]);
			assert !manager.isRoomAvailable(i, dates[i]);

			int finalI = i;
			assertThrows(RoomNotAvailableException.class, () -> {
				manager.addBooking(guests[finalI], finalI, dates[finalI]);
			});

			for(int r = i+1; r < top; r++){
				assert manager.isRoomAvailable(r, dates[r]);
			}
		}
	}

	@Test
	void getAvailableRooms() throws RoomException {
		LocalDate date = dates[0];
		Set<Integer> bookedRooms = new HashSet<>();
		for(int i = 0; i < top; i++) {
			Iterable<Integer> iter = manager.getAvailableRooms(date);
			int count = 0;
			for(Integer room : iter){
				assert !bookedRooms.contains(room);
				count++;
			}
			assert count == top - i;

			manager.addBooking(guests[i], i, date);
			bookedRooms.add(i);
		}

		Iterable<Integer> iter = manager.getAvailableRooms(date);
		int count = 0;
		for(Integer room : iter){
			assert !bookedRooms.contains(room);
			count++;
		}
		assert count == 0;

		iter = manager.getAvailableRooms(dates[1]);
		count = 0;
		for(Integer room : iter){
			assert bookedRooms.contains(room); // all rooms booked should be available on other dates
			count++;
		}
		assert count == top;
	}

	@Test
	void testUnsanitisedDates() throws RoomException {
		LocalDate date = LocalDate.ofYearDay(2022, 123);
		LocalDate dateDupe = LocalDate.ofInstant(Instant.ofEpochSecond(1651536000L), ZoneId.systemDefault()); // same date as above, midnight
		LocalDate datePlus5Hours = LocalDate.ofInstant(Instant.ofEpochSecond(1651554000L), ZoneId.systemDefault()); // 5 hours on
		assert manager.isRoomAvailable(0, date);
		manager.addBooking("g", 0, date);
		assert !manager.isRoomAvailable(0, dateDupe);
		for(int i = 1; i < 10; i++){
			assert manager.isRoomAvailable(0, date.plusDays(i));
			assert manager.isRoomAvailable(0, date.minusDays(i));
		}
	}
}