package uoftprojects.ergo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import uoftprojects.ergo.alerts.engine.AlertsEngine;
import uoftprojects.ergo.engine.ErgoEngine;
import uoftprojects.ergo.metrics.IMetric;

public class MainActivity extends Activity{

    // Flag used to ensure that ergo engine and alert engine are created only once during the app's lifetime
    private static boolean createOnce = true;

    private ErgoEngine ergoEngine = null;

    private AlertsEngine alertsEngine = null;


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
        ergoEngine.unregister();
        super.onDestroy();
    }

    /*
        Run a timer every 5 seconds to get metric updates
     */
    private void createMetricsUpdateLoop(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                GetUpdates getUpdates = new GetUpdates();
                getUpdates.execute();
            }
        }, 0, 2500);
    }


    class GetUpdates extends AsyncTask<String, Void, List<IMetric>> {

        @Override
        protected List<IMetric> doInBackground(String... params) {
            IMetric tilt = ergoEngine.getTilt();
            IMetric proximity = ergoEngine.getProximity();
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
