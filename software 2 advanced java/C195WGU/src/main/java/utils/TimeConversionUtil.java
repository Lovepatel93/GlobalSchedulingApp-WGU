package utils; // Based on the context you provided earlier

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class TimeConversionUtil {

    /**
     * Converts a given date-time string in the local time zone to its equivalent UTC representation.
     *
     * @param localDateTimeStr A date-time string in the format "yyyy-MM-dd HH:mm:ss" representing local time.
     * @return A date-time string in UTC.
     */
    public static String convertLocalToUTC(String localDateTimeStr) {
        LocalDateTime localDateTime = LocalDateTime.parse(localDateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        ZonedDateTime localZonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        System.out.println("here the local time :" +localZonedDateTime);//debug
        ZonedDateTime utcZonedDateTime = localZonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        System.out.println("here the utcZonedDateTime time :" +utcZonedDateTime);//debug
        LocalDateTime utcDateTime = utcZonedDateTime.toLocalDateTime();
        System.out.println("here the utcDateTime :" +utcDateTime);//debug
        return utcDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


    public static String convertUTCToLocal(String utcDateTimeStr) {
        Timestamp utcTimestamp = Timestamp.valueOf(utcDateTimeStr);
        LocalDateTime utcDateTime = utcTimestamp.toLocalDateTime();

        ZonedDateTime utcZonedDateTime = utcDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime localZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.systemDefault());

        LocalDateTime localDateTime = localZonedDateTime.toLocalDateTime();
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Checks if the given LocalDateTime in EST is during the weekend.
     *
     * @param estDateTime A LocalDateTime instance in EST.
     * @return True if it's during the weekend, false otherwise.
     */
    public static boolean isWeekendInEST(LocalDateTime estDateTime) {
        DayOfWeek day = estDateTime.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    /**
     * Checks if the given LocalDateTime in EST is within business hours (8 am to 10 pm).
     *
     * @param estDateTime A LocalDateTime instance in EST.
     * @return True if it's within business hours, false otherwise.
     */
    public static boolean isOutsideBusinessHoursInEST(LocalDateTime estDateTime) {
        LocalTime time = estDateTime.toLocalTime();
        return time.isBefore(LocalTime.of(8, 0)) || time.isAfter(LocalTime.of(22, 0));
    }



    /**
     * Converts a given LocalDateTime in the local time zone to its equivalent EST representation.
     *
     * @param localDateTime A LocalDateTime instance representing local time.
     * @return A LocalDateTime in EST.
     */
    public static LocalDateTime convertLocalToEST(LocalDateTime localDateTime) {
        ZonedDateTime localZonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime estZonedDateTime = localZonedDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));
        return estZonedDateTime.toLocalDateTime();
    }

    /**
     * Converts a given LocalDateTime in EST to its local time representation.
     *
     * @param estDateTime A LocalDateTime instance representing EST time.
     * @return A LocalDateTime in the local time zone.
     */
    public static LocalDateTime convertESTToLocal(LocalDateTime estDateTime) {
        ZonedDateTime estZonedDateTime = estDateTime.atZone(ZoneId.of("America/New_York"));
        ZonedDateTime localZonedDateTime = estZonedDateTime.withZoneSameInstant(ZoneId.systemDefault());
        return localZonedDateTime.toLocalDateTime();
    }
}
