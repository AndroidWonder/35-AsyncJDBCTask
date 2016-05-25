package com.course.example.asyncjdbctask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView text;
    private ProgressDialog progDailog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.text01);

        new JDBCTask(text).execute();
    }


    //----------------------------------------------------------
    //AsyncTask inner class
    private class JDBCTask extends AsyncTask<String, Void, ArrayList<String>> {
        private TextView textView;

        String URL = "jdbc:mysql://frodo.bentley.edu:3306/world";
        String username = "Android";
        String password = "android";
        Statement stmt = null;
        Connection con = null;
        ArrayList<String> cities;


        public JDBCTask(TextView textView) {
            this.textView = textView;
            this.cities = new ArrayList<String>();
        }

        //start dialog widget
        @Override
        protected void onPreExecute() {
            progDailog = ProgressDialog.show(MainActivity.this, "JDBC Demo",
                    "Working....", true);
        }

        //runs on background thread
        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            String name;

            try { //load driver into VM memory
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                Log.e("JDBC", "Did not load driver");

            }

            try { //create connection to database
                con = DriverManager.getConnection(
                        URL,
                        username,
                        password);
                stmt = con.createStatement();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                ResultSet result = stmt.executeQuery(
                        "SELECT * FROM City ORDER BY Name LIMIT 20 OFFSET 10;");

                //for each record in City table add City to ArrayList and add city data to log
                while (result.next()) {
                    name = result.getString("Name");
                    cities.add(name);
                    Log.e("City", name);
                }
            } catch (SQLException e) {
            }
            ;

            //give the progress dialog a chance to show off
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }

            return cities;
        }

        //takes return of background thread and places it on UI
        @Override
        protected void onPostExecute(ArrayList<String> cities) {

            progDailog.dismiss();
            //write data to UI
            for (int i = 0; i < cities.size(); i++) {
                textView.append(cities.get(i) + "\n");
            }
        }
    }

}
