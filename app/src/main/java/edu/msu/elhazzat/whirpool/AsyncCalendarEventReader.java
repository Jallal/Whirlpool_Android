package edu.msu.elhazzat.whirpool;

import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.List;

public class AsyncCalendarEventReader extends AsyncTask<Void, Void, List<Event>> {
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
        } catch (UserRecoverableAuthIOException userRecoverableException) {
        } catch (Exception e) {
        }
        return null;
    }

    private List<Event> getCalendarEvents() throws IOException {
        Events events = mCalendarService.events().list("primary")
                .setMaxResults(mMaxResults)
                .setTimeMin(mTime)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        return events.getItems();
    }

    @Override
    public void onPostExecute(List<Event> events) {
        mDelegate.onAsyncFinished(events);
    }
}
