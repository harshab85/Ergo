package uoftprojects.ergo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import uoftprojects.ergo.engine.AlertsEngine;
import uoftprojects.ergo.engine.ErgoEngine;
import uoftprojects.ergo.metrics.IMetric;
import uoftprojects.ergo.metrics.Proximity;
import uoftprojects.ergo.metrics.Tilt;

public class MainActivity extends Activity {

    // Flag used to ensure that ergo engine and alert engine are created only once during the app's lifetime
    private static boolean createOnce = true;

    private ErgoEngine ergoEngine = null;

    private AlertsEngine alertsEngine = null;

    private Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(createOnce) {
            ergoEngine = new ErgoEngine(this);
            alertsEngine = new AlertsEngine(this);

            createMetricsUpdateLoop();

            createOnce = false;
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        ergoEngine.register();
    }

    @Override
    protected void onPause() {
        ergoEngine.unregister();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        ergoEngine.unregister();
        super.onDestroy();
    }



    /*
            Run a timer every 5 seconds to get metric updates
         */
    private void createMetricsUpdateLoop(){

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                GetUpdates getUpdates = new GetUpdates();
                getUpdates.execute();
            }
        }, 5000, 3000);
    }


    class GetUpdates extends AsyncTask<String, Void, List<IMetric>> {

        @Override
        protected List<IMetric> doInBackground(String... params) {
            final IMetric tilt = ergoEngine.getTilt();
            final IMetric proximity = ergoEngine.getProximity();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView currentPhoneTilt = (TextView)findViewById(R.id.current_PhoneTilt);
                    currentPhoneTilt.setText("Current phone angle is " + ((Tilt)tilt).getValue());

                    TextView currProximity = (TextView)findViewById(R.id.curr_proximity);
                    currProximity.setText("Current rect area angle is " + ((Proximity)proximity).getRectArea());
                }
            });



            IMetric startTime = ergoEngine.getStartTime();

            List<IMetric> metricsList = new ArrayList<>();
            metricsList.add(tilt);
            metricsList.add(proximity);
            metricsList.add(startTime);
            return metricsList;
        }

        @Override
        protected void onPostExecute(List<IMetric> metricList) {
            alertsEngine.testWithBaseLine(metricList);
        }


    }
}
