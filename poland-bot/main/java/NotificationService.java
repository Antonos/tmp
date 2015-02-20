/**
 * Created by anton on 26.01.2015.
 */

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

import static org.joda.time.LocalDateTime.now;

public class NotificationService {

    private static Calendar service;

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        //auth();
        //test();
        check();
    }

    private static void check() {
        NotificationData notificationData = loadNotificationInfo();
        System.out.println(notificationData);
    }

    private static void test() throws GeneralSecurityException, IOException {
        refresh();
        send("test","test");
    }

    public static void auth() throws GeneralSecurityException, IOException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        // The clientId and clientSecret can be found in Google Developers Console
        String clientId = PolandCheckBot.config.getString("GOOGLE_APP_CLIENT_ID");
        String clientSecret = PolandCheckBot.config.getString("GOOGLE_APP_SECRET");

        String redirectUrl = PolandCheckBot.config.getString("GOOGLE_APP_REDIRECT_URL");
        String scope = PolandCheckBot.config.getString("GOOGLE_APP_SCOPE");

        GoogleAuthorizationCodeFlow.Builder builder = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientId, clientSecret, Collections.singleton(scope));
        builder.setApprovalPrompt("force");
        builder.setAccessType("offline");
        GoogleAuthorizationCodeFlow flow = new CustomAuthorizationCodeFlow(builder);
        // Step 1: Authorize
        String authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUrl).build();

        // Point or redirect your user to the authorizationUrl.
        System.out.println("Go to the following link in your browser:");
        System.out.println(authorizationUrl);

        // Read the authorization code from the standard input stream.
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("What is the authorization code?");
        String code = null;
        try {
            code = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // End of Step 1

        // Step 2: Exchange
        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUrl)
                .execute();
        System.out.println(response.getAccessToken());
        System.out.println(response.getRefreshToken());
    }

    public static void refresh() throws GeneralSecurityException, IOException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        // The clientId and clientSecret can be found in Google Developers Console
        String clientId = PolandCheckBot.config.getString("GOOGLE_APP_CLIENT_ID");
        String clientSecret = PolandCheckBot.config.getString("GOOGLE_APP_SECRET");

        Credential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setClientSecrets(clientId, clientSecret)
                .build().setAccessToken(PolandCheckBot.config.getString("GOOGLE_APP_ACCESS_TOKEN"))
                        .setRefreshToken(PolandCheckBot.config.getString("GOOGLE_APP_REFRESH_TOKEN"));

        service = new Calendar.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("CalendarSMSApp").build();
    }

    public static void send(String region, String dateValue) throws GeneralSecurityException, IOException {

        Event event = new Event();
        event.setSummary("Visa Center Date: "+region+" "+dateValue);
        event.setLocation("Poland");

        Date startDate = now().plusMinutes(2).toDate();
        Date endDate = now().plusMinutes(3).toDate();
        DateTime start = new DateTime(startDate, TimeZone.getTimeZone("Europe/Kiev"));
        event.setStart(new EventDateTime().setDateTime(start));
        DateTime end = new DateTime(endDate, TimeZone.getTimeZone("Europe/Kiev"));
        event.setEnd(new EventDateTime().setDateTime(end));

        // Insert the new event
        Event.Reminders reminders = new Event.Reminders();
        reminders.setUseDefault(false);

        // 0 minute reminder
        EventReminder reminder = new EventReminder();
        reminder.setMinutes(1);
        reminder.setMethod("sms");
        List<EventReminder> listEventReminder = new ArrayList<EventReminder>();
        listEventReminder.add(reminder);
        reminders.setOverrides(listEventReminder);
        event.setReminders(reminders);

        Event createdEvent = service.events().insert(PolandCheckBot.config.getString("GOOGLE_APP_CALENDAR_ID"), event).execute();
    }

    public static NotificationData loadNotificationInfo(){
        try {
            FileInputStream fis = new FileInputStream(PolandCheckBot.config.getString("NOTIFICATION_DATA_PATH"));
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (NotificationData)ois.readObject();
        } catch (EOFException e){
            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return new NotificationData();
    }

    public static boolean checkNotificationInfo(String region){
        NotificationData notificationData = loadNotificationInfo();
        if(notificationData.getValues().containsKey(region)){
            Date notificationDate = notificationData.getValues().get(region);
            org.joda.time.DateTime shiftedNotificationDate = new org.joda.time.DateTime(notificationDate);
            shiftedNotificationDate = shiftedNotificationDate.plusMinutes(PolandCheckBot.config.getInt("NOTIFICATION_PERIOD"));
            return shiftedNotificationDate.isBeforeNow();
        }
        return true;
    }

    public static void storeNotificationInfo(String region){
        try {
            NotificationData notificationData = loadNotificationInfo();
            notificationData.getValues().put(region,new Date());
            FileOutputStream fos = new FileOutputStream(PolandCheckBot.config.getString("NOTIFICATION_DATA_PATH"));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(notificationData);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
