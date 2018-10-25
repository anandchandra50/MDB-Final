package com.demo.mdb.spring2017finalassessment;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by hp on 3/14/2017.
 */

public class Utils {
    /* TODO Part 5
     * implement getRandomPhrase on a thread pool of size 1. Use a callable to make a GET request on
     * this urlString: "https://api.whatdoestrumpthink.com/api/v1/quotes/random". You'll probably
     * need to actually go to the URL to see the JSON structure to know what String you want (don't
     * worry, it's a very simple JSON file.)
     *
     * convertStreamToString has been provided
     *
     * Note: if you can't remember how to use a Callable, you can get partial credit without one!
     */
    static Future<String> getRandomPhrase() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        //create a list to hold the Future object associated with Callable
        Callable<String> callable = new Utils.QuoteCallable();
        Future<String> future = executor.submit(callable);

        //shut down the executor service now
        executor.shutdown();
        return future;
    }

    private static class QuoteCallable implements Callable<String> {

        @Override
        public String call() throws Exception {
            // RUN ASYNC TASK HERE
            String urlString = "https://api.whatdoestrumpthink.com/api/v1/quotes/random";

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String stream = convertStreamToString(in);
                    return new JSONObject(stream).getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // will only get here if the url is broken or the json cannot be parsed
            return null;
        }

    }

    public static class FetchJSON extends AsyncTask<Void, Void, JSONObject> {
        private String urlString = "https://api.whatdoestrumpthink.com/api/v1/quotes/random";
        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String stream = convertStreamToString(in);
                    return new JSONObject(stream);
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        private void readStream(InputStream stream) {
            System.out.println(stream);
        }

    }


    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * Show a toast for an error
     * @param context
     * @param message
     */
    public static void displayError(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * See if email has valid format
     * @param target
     * @return
     */
    private static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * checks if email and pass are valid
     * @param context
     * @param email
     * @param pass
     * @return
     */
    public static boolean isValid(Context context, String email, String pass) {
        if (email.isEmpty() || !isValidEmail(email)) {
            displayError(context, "Invalid email");
            return false;
        } else if (pass.isEmpty()) {
            displayError(context, "Incorrect Password");
            return false;
        } else if (pass.length() < 6) {
            displayError(context, "Incorrect Password");
            return false;
        } else {
            // no errors, create user
            return true;
        }
    }

    /**
     * Same thing as isValid, except with confirm pass this time
     * @param context
     * @param email
     * @param pass
     * @param confirmPass
     * @return
     */
    public static boolean isValid(Context context, String email, String pass, String confirmPass) {
        if (!isValid(context, email, pass)) {
            return false;
        } else if (!pass.equals(confirmPass)) {
            displayError(context, "Passwords must match");
            return false;
        } else {
            return true;
        }
    }
}


