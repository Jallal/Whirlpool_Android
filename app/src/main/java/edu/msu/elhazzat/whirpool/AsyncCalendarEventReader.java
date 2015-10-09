package edu.msu.elhazzat.whirpool;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.List;

/**
 * Created by christianwhite on 9/20/15.
 */
public class AsyncCalendarEventReader extends AsyncTask<Void, Void, List<Event>> {

    private static final String LOG_TAG = AsyncCalendarEventWriter.class.getSimpleName();

    public interface AsyncCalendarReaderDelegate {
        public void onAsyncFinished(List<com.google.api.services.calendar.model.Event> events);
        public void handleUserRecoverableAuthIOException(UserRecoverableAuthIOException e);
    }

    private AsyncCalendarReaderDelegate mDelegate;
    private com.google.api.services.calendar.Calendar mCalendarService;
    private DateTime mTime;
    private int mMaxResults;

    AsyncCalendarEventReader(AsyncCalendarReaderDelegate delegate,
                             com.google.api.services.calendar.Calendar service,
                             DateTime time, int max) {
        mDelegate = delegate;
        mCalendarService = service;
        mTime = time;
        mMaxResults = max;
    }

    @Override
    protected List<Event> doInBackground(Void... params) {
        try {
            List<Event> events = getCalendarEvents();
            return events;
        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            Log.e(LOG_TAG, "Error: ", availabilityException);
        }
        catch(IOException e) {
            Log.e(LOG_TAG, "Error: ", e);
        }
        return null;
    }

    private List<Event> getCalendarEvents() throws IOException {
        try {
            Events events = mCalendarService.events().list("primary")
                    .setMaxResults(mMaxResults)
                    .setTimeMin(mTime)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            return events.getItems();
        }catch(UserRecoverableAuthIOException e) {
           mDelegate.handleUserRecoverableAuthIOException(e);
        }
        return null;
    }

    @Override
    public void onPostExecute(List<Event> events) {
        mDelegate.onAsyncFinished(events);
    }
}
