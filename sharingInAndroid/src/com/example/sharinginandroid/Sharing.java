package com.example.sharinginandroid;

import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.ProfilePictureView;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class Sharing extends Activity implements OnClickListener {

	public static String AppId = "725020704203049";
	private UiLifecycleHelper uiHelper;
	private Button btn_txtLogin;
	String url = "" ;
	URL img_value = null;
	private String stringURL;
	private ProfilePictureView img_userpicture;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);

		initView();

		Session.openActiveSession(this, true, new Session.StatusCallback() {

			// callback when session changes state
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				if (session.isOpened()) {

					// make request to the /me API
					Request.newMeRequest(session,
							new Request.GraphUserCallback() {


								// callback after Graph API response with user
								// object
								@Override
								public void onCompleted(GraphUser user,
										Response response) {
									if (user != null) {
										TextView welcome = (TextView) findViewById(R.id.welcome);
										welcome.setText("Hello "
														+ user.getName() + "!");	
										img_userpicture.setProfileId(user.getId());
									}
								}
							}).executeAsync();
				}
			}
		});

		uiHelper = new UiLifecycleHelper(this, null);
		uiHelper.onCreate(savedInstanceState);

	}

	private void publishFeedDialog() {
		Bundle params = new Bundle();
		params.putString("name", "Facebook SDK for Android");
		params.putString("caption",
				"Build great social apps and get more installs.");
		params.putString(
				"description",
				"The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
		params.putString("link", "https://developers.facebook.com/android");
		/*
		 * params.putString("picture",
		 * "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png"
		 * );
		 */params
				.putString("picture",
						"https://hi.co/bundles/hitomain/images/hi_big.png?v=1403178644");

		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(Sharing.this,
				Session.getActiveSession(), params)).setOnCompleteListener(
				new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error == null) {
							// When the story is posted, echo the success
							// and the post Id.
							final String postId = values.getString("post_id");
							if (postId != null) {
								Toast.makeText(Sharing.this,
										"Posted story, id: " + postId,
										Toast.LENGTH_SHORT).show();
							} else {
								// User clicked the Cancel button
								Toast.makeText(
										Sharing.this.getApplicationContext(),
										"Publish cancelled", Toast.LENGTH_SHORT)
										.show();
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							// User clicked the "x" button
							Toast.makeText(
									Sharing.this.getApplicationContext(),
									"Publish cancelled", Toast.LENGTH_SHORT)
									.show();
						} else {
							// Generic, ex: network error
							Toast.makeText(
									Sharing.this.getApplicationContext(),
									"Error posting story", Toast.LENGTH_SHORT)
									.show();
						}
					}

				}).build();
		feedDialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);

		if (Session.getActiveSession().isOpened()) {
			uiHelper.onActivityResult(requestCode, resultCode, data,
					new FacebookDialog.Callback() {
						@Override
						public void onError(
								FacebookDialog.PendingCall pendingCall,
								Exception error, Bundle data) {
							Log.e("Activity", String.format("Error: %s",
									error.toString()));
						}

						@Override
						public void onComplete(
								FacebookDialog.PendingCall pendingCall,
								Bundle data) {
							Log.i("Activity", "Success!");
						}
					});
		}
	}

	public void initView() {
		btn_txtLogin = (Button) findViewById(R.id.btn_txtLogin);
		btn_txtLogin.setOnClickListener(this);

		img_userpicture = (ProfilePictureView) findViewById(R.id.img_userpicture);
	}

	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_txtLogin:
			if (Session.getActiveSession().isOpened()) {

				if (FacebookDialog.canPresentShareDialog(
						getApplicationContext(),
						FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
					// Publish the post using the Share Dialog
					FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(
							this).setLink(
							"https://developers.facebook.com/android").build();
					uiHelper.trackPendingDialogCall(shareDialog.present());

				} else {

					publishFeedDialog();
				}
			}
			break;

		default:
			break;
		}
	}	
	
}
