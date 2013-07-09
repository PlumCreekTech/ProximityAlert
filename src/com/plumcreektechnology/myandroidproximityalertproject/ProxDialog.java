package com.plumcreektechnology.myandroidproximityalertproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

public class ProxDialog extends DialogFragment {
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// create a dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(
				"You're near "
						+ ((ProxAlertActivity) getActivity()).getAlertName()
						+ "!")
				.setPositiveButton("Show",
						new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface di, int which) {
				// TODO THINGS
				Intent intend = new Intent(Intent.ACTION_VIEW, Uri.parse( ((ProxAlertActivity)getActivity()).getAlertUri() ));
				startActivity(intend);
			}
		})
		.setNegativeButton("Nope", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO poooooop
				dialog.dismiss();
			}
		});
		
		//return
		return builder.create();
	}
}
