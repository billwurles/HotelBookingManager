package manager;

import manager.model.exceptions.RoomException;
import manager.model.exceptions.RoomNotAvailableException;
import manager.model.HotelManager;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookingManagerTest {

	@Test
	void example() throws RoomException {
		BookingManager bm = new HotelManager();// create your manager here;
		LocalDate today = LocalDate.parse("2012-07-21");
		assert(bm.isRoomAvailable(101, today)); // outputs true
		bm.addBooking("Smith", 101, today);
		assert(!bm.isRoomAvailable(101, today)); // outputs false
		assertThrows(RoomNotAvailableException.class, () -> {
			bm.addBooking("Jones", 101, today); // throws an exception
		});
	}

}