package uoftprojects.ergo.engine;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import uoftprojects.ergo.alerts.handlers.AlertsHandler;
import uoftprojects.ergo.metrics.IMetric;
import uoftprojects.ergo.metrics.Proximity;

/**
 * Created by Harsha Balasubramanian on 3/2/2015.
 */
public class SparkPlug {

    private static ErgoEngine ergoEngine = null;

    private static  AlertsEngine alertsEngine = null;

    private static Timer timer;

    //private static SparkPlug INSTANCE;

   /* private SparkPlug(){
        ergoEngine = new ErgoEngine();
        alertsEngine = new AlertsEngine();
        createMetricsUpdateLoop();
    }*/

    public static void start(){
        /*if(INSTANCE == null){
            INSTANCE = new SparkPlug();
        }*/
        ergoEngine = new ErgoEngine();
        alertsEngine = new AlertsEngine();
        createMetricsUpdateLoop();
    }

    public static void stop(){
        if(ergoEngine != null) {
            ergoEngine.unregister();
        }

        if(timer != null) {
            timer.cancel();
        }

        AlertsHandler.cancelAlerts();

        //INSTANCE = null;
    }

    private static void createMetricsUpdateLoop(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            GetUpdates getUpdates = new GetUpdates();
            getUpdates.execute();
            }
        }, 5000, 2500);
    }

    static class GetUpdates extends AsyncTask<String, Void, List<IMetric>> {
        @Override
        protected List<IMetric> doInBackground(String... params) {
            IMetric tilt = ergoEngine.getTilt();

            IMetric proximity = ergoEngine.getProximity();
            ((Proximity)proximity).setTilt(tilt);

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