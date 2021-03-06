package dcogburn.hometown;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

/**
 * Created by danielle on 8/4/17.
 */

public class RateFavoriteDialog extends DialogFragment {
    String TAG = "RateFavoriteDialog";
    View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View textEntryView = inflater.inflate(R.layout.favorite_dialog, null);
        builder.setView(textEntryView);
        final RatingBar ratingBar = textEntryView.findViewById(R.id.rating_bar);
        ratingBar.setNumStars(5);

        builder.setMessage(R.string.rate_this_album)
                .setPositiveButton(R.string.add_to_favorites, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        float rating = ratingBar.getRating();
                        Log.d(TAG, String.valueOf(rating));
                        mListener.onDialogPositiveClick(rating);
                        //favoriteAlbum.setImageResource(R.drawable.ic_action_favorite_pressed);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    /* Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        void onDialogPositiveClick(float rating);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}

