package sharadcodes.opennotifysync;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Switch tgl_btn = findViewById(R.id.toggle_btn);

        tgl_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(tgl_btn.isChecked()){

                    Toast.makeText(getApplicationContext(), "ON", Toast.LENGTH_SHORT).show();

                    try
                    {
                        Intent i = new Intent(getApplicationContext(), MyNotificationListener.class);
                        startService(i);
                        Toast.makeText(getApplicationContext(), "Intent called successfully", Toast.LENGTH_SHORT).show();
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(getApplicationContext(), "Failed to call intent", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //    asynchronous class for SOCKET
    private static   class  My_asy_class extends AsyncTask<String, Void, String>{

        protected String doInBackground(String... messages){
            Exception e = new Exception();
            try {
                Socket bloody_socket = new Socket(messages[0],8080);
                ObjectOutputStream shit_out_object = new ObjectOutputStream(bloody_socket.getOutputStream());
                shit_out_object.writeUTF(messages[1]);
                shit_out_object.flush();
                shit_out_object.close();
                bloody_socket.close();
            }
            catch (Exception ee){
                e = ee;
            }
            return "OK" + e.getMessage();
        }
    }

    //    notification listener class
    public static class MyNotificationListener extends NotificationListenerService {
        @Override
        public void onNotificationPosted(StatusBarNotification sbn) {
            String pack = sbn.getPackageName();
            Bundle extras = sbn.getNotification().extras;
            String title = extras.getString("android.title");
            String text = extras.getCharSequence("android.text").toString();
            String subtext = "";
            Parcelable b[] = (Parcelable[]) extras.get(Notification.EXTRA_MESSAGES);

            if(b!=null){
                for (Parcelable tmp: b){
                    Bundle msgBundle = (Bundle) tmp;
                    subtext = msgBundle.getString("text");
                }
            }

            if(subtext.isEmpty()){
                subtext = text;
            }

            if (subtext.equals(text)) {
                subtext = "";
            }

            //Toast.makeText(this, pack + " " + title + " " + text  + " "+  subtext, Toast.LENGTH_LONG).show();
            new My_asy_class().execute("192.168.42.26", pack + " " + title + " " + text  + " "+  subtext);
        }
    }
}
