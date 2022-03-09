package manager;

import manager.model.exceptions.RoomNotAvailableException;
import manager.model.HotelManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParallelTests {

	@BeforeAll
	private static void setUp(){
		bm = new HotelManager(1);
		date = LocalDate.ofYearDay(2022, 1);
	}

	static HotelManager bm;
	static LocalDate date;

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	public void first() throws Exception{
		Thread.sleep(100);
		if(bm.isRoomAvailable(0, date)) {
			bm.addBooking("g1", 0, date);
			assert bm.getGuestOnDate(0, date).surname().equals("g1");
		}
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	public void second() throws Exception{
		Thread.sleep(100);
		if(bm.isRoomAvailable(0, date)){
			Thread.sleep(200);
			assertThrows(RoomNotAvailableException.class, () -> {
				bm.addBooking("g2", 0, date);
			});
			assert bm.getGuestOnDate(0, date).surname().equals("g1");
		} else {
			System.out.println("Thread 2 was too slow! run again");
			assert false;
		}
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	public void third() throws Exception{
		assertThrows(NullPointerException.class, () -> {
			bm.getGuestOnDate(0, date).surname().equals("g1");
		});

		Thread.sleep(400);

		Iterable<Integer> iter = bm.getAvailableRooms(date);
		ArrayList<Integer> freeRooms = new ArrayList<>();
		for(Integer room : iter){
			freeRooms.add(room);
		}
		assert !freeRooms.contains(0);
	}
}
