package ajal.arsocialmessaging.DBServer;

import ajal.arsocialmessaging.DBServer.holidayapi.Holiday;
import ajal.arsocialmessaging.DBServer.holidayapi.ServiceGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class HolidayAPITest {

    @AfterEach
    public void sleep1Second() throws InterruptedException {
        // This is needed as the free plan for Abstract API only allows 1 request per second
        Thread.sleep(1000);
    }

    @Test
    public void test_ReturnsChristmasDay() {
        Map<String,String> parameters = Map.of(
                "country", "GB",
                "year", "2022",
                "month", "12",
                "day", "25");
        try {
            HttpResponse<Supplier<Holiday[]>> response = ServiceGenerator.getInstance().getHttpResponse(parameters);
            Holiday[] holidays = response.body().get();
            assertNotNull(holidays);
            assertNotEquals(0, holidays.length);
            assertEquals("Christmas Day", holidays[0].name);
            assertEquals("12/25/2022", holidays[0].date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_ReturnsBeginningOfRamadan() {
        Map<String,String> parameters = Map.of(
                "country", "SA",
                "year", "2022",
                "month", "4",
                "day", "3");
        try {
            HttpResponse<Supplier<Holiday[]>> response = ServiceGenerator.getInstance().getHttpResponse(parameters);
            Holiday[] holidays = response.body().get();
            assertNotNull(holidays);
            assertNotEquals(0, holidays.length);
            assertEquals("Ramadan begins", holidays[0].name);
            assertEquals("04/03/2022", holidays[0].date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_ReturnsBeginningOfDiwali() {
        Map<String,String> parameters = Map.of(
                "country", "MU",
                "year", "2022",
                "month", "10",
                "day", "24");
        try {
            HttpResponse<Supplier<Holiday[]>> response = ServiceGenerator.getInstance().getHttpResponse(parameters);
            Holiday[] holidays = response.body().get();
            assertNotNull(holidays);
            assertNotEquals(0, holidays.length);
            assertEquals("Divali", holidays[0].name);
            assertEquals("10/24/2022", holidays[0].date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
