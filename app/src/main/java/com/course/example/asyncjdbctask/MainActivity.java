package com.course.example.asyncjdbctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView text;

    private ProgressBar bar2;
    public static Integer NumberOfRecords = 20;
    public static Integer Offset = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bar2 = (ProgressBar) findViewById(R.id.progress2);
        text = (TextView) findViewById(R.id.text01);

        new JDBCTask(text).execute(NumberOfRecords, Offset);
    }


    //----------------------------------------------------------
    //AsyncTask inner class
    private class JDBCTask extends AsyncTask<Integer, Void, ArrayList<String>> {
        private TextView textView;

        String URL = "jdbc:mysql://frodo.bentley.edu:3306/world";
        String username = "harry";
        String password = "harry";
        Statement stmt = null;
        ArrayList<String> cities;

        public JDBCTask(TextView textView) {
            this.textView = textView;
            this.cities = new ArrayList<String>();
        }

        //start dialog widget
        @Override
        protected void onPreExecute() {
            bar2.setVisibility(View.VISIBLE);
        }

        //runs on background thread
        @Override
        protected ArrayList<String> doInBackground(Integer... integers) {
            String name;
            String records = integers[0].toString();
            String offset = integers[1].toString();

            try  //create connection to database
                (Connection con = DriverManager.getConnection(
                        URL,
                        username,
                        password)){
                stmt = con.createStatement();

                ResultSet result = stmt.executeQuery(
                        "SELECT * FROM City ORDER BY Name LIMIT " + records + " OFFSET " + offset + ";");

                //for each record in result set add city to ArrayList and add city to log
                while (result.next()) {
                    name = result.getString("Name");
                    cities.add(name);
                    Log.e("City", name);
                }
            } catch (SQLException e) {
                e.printStackTrace();
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

            bar2.setVisibility(View.INVISIBLE);

            //write data to UI
            for (int i = 0; i < cities.size(); i++) {
                textView.append(cities.get(i) + "\n");
            }
        }
    }

}
