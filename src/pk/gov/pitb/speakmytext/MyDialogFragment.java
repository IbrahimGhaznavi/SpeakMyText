package pk.gov.pitb.speakmytext;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class MyDialogFragment extends DialogFragment{
    Context mContext;
    public MyDialogFragment() {
        mContext = getActivity();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Uploading");
        alertDialogBuilder.setMessage("Picture Uploading");
        //null should be your on click listener
        return alertDialogBuilder.create();
    }
}