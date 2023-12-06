package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogUtils {
    // Utility method to append log details to login_activity.txt
    public static void logLoginActivity(String username, boolean isSuccess) {
        try {
            // Set up the file writer to append to the login_activity.txt file
            FileWriter fw = new FileWriter("login_activity.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            // Get the current date and time
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String currentTime = now.format(formatter);

            // Write the login details to the file
            pw.println(String.format("%s - User: %s, Login Attempt: %s", currentTime, username, isSuccess ? "SUCCESSFUL" : "FAILED"));

            // Close the writers
            pw.close();
            bw.close();
            fw.close();
        } catch (IOException e) {
            System.out.println("Error writing to log file: " + e.getMessage());
        }
    }


}
