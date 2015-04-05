package uoftprojects.ergo.email;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import uoftprojects.ergo.metrics.usage.MetricsStorage;

/**
 * Created by home on 2015-04-05.
 */
public class EmailClient {

    private static final EmailClient INSTANCE = new EmailClient();

    private HttpClient httpClient = new DefaultHttpClient();

    private boolean completed = false;

    private Object lock = new Object();

    private EmailClient(){}

    public static final EmailClient getInstance(){
        return INSTANCE;
    }


    public void send(final String emailAddress, final JSONObject emailBody){

        System.out.println();

        new Thread(){
            @Override
            public void run() {
                threadLogic(emailAddress, emailBody);

                synchronized (lock) {
                    completed = true;
                    lock.notifyAll();
                }
            }
        }.start();

        synchronized (lock){
            while(!completed){
                try {
                    lock.wait();
                }
                catch (InterruptedException e) {
                    // Ignore
                }
            }
        }
    }


    public boolean threadLogic(String emailAddress, JSONObject emailBody){

        try {
            JSONObject params = new JSONObject();
            params.put("emailAddress", emailAddress);
            params.put("metrics", emailBody);
            StringEntity stringEntity = new StringEntity(params.toString(), "UTF-8");

            URL postUrl = new URL("http://ergo-env.elasticbeanstalk.com/email/sendEmail");
            HttpPost post = new HttpPost(postUrl.toURI());
            post.setEntity(stringEntity);
            post.setHeader("Content-Type", "application/json");

            return executePost(post);

        }
        catch (Exception e){
            // Ignore
        }


        return false;
    }

    protected boolean executePost(HttpPost post){
        HttpResponse response = null;
        ByteArrayOutputStream out = null;
        try {
            response = httpClient.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                String responseString = out.toString();
                JSONObject responseObject = new JSONObject(responseString);
                int status = responseObject.getInt("status");
                return (status == 200);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            // ignore
        }
        finally{

            try {
                if(response != null){
                    HttpEntity entity = response.getEntity();
                    if(entity != null) {
                        InputStream stream = entity.getContent();
                        if (stream != null) {
                            stream.close();
                        }
                    }
                }

                if(out != null){
                    out.flush();
                    out.close();
                }

            }
            catch (Exception e){
                // Ignore
            }
        }

        return false;
    }


}
