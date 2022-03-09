import manager.BookingManager;
import manager.model.exceptions.RoomException;
import manager.model.HotelManager;

import java.time.LocalDate;

public class HotelMain {

	public static void main(String[] args) throws RoomException {
		BookingManager bm = new HotelManager(); // create your manager here;
		LocalDate today = LocalDate.parse("2012-07-21");
		System.out.println(bm.isRoomAvailable(101, today)); // outputs true
		bm.addBooking("Smith", 101, today);
		System.out.println(bm.isRoomAvailable(101, today)); // outputs false
		bm.addBooking("Jones", 101, today); // throws an exception
	}
}
