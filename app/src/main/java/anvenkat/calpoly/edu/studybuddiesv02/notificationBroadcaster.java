package anvenkat.calpoly.edu.studybuddiesv02;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by aniru on 11/22/2016.
 */

public class NotificationBroadcaster extends BroadcastReceiver {
    private String workName;
    private int id;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        workName = b.getString("nameofwork");
        id = b.getInt("id");
        Intent transferToService = new Intent(context, NotificationSenderService.class);
        transferToService.putExtra("nameofwork", workName);
        transferToService.putExtra("id", id);
        context.startService(transferToService);
    }
}