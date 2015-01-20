package dam2.sixapp.cookin;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.System;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class MainActivitySplash extends Activity implements OnClickListener,
		ConnectionCallbacks, OnConnectionFailedListener {

	private static final int RC_SIGN_IN = 0;
	// Logcat tag
	private static final String TAG = "MainActivity";

	// Google client to interact with Google API
	private GoogleApiClient mGoogleApiClient;

	/**
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	private boolean mIntentInProgress;

	private boolean mSignInClicked;

	private ConnectionResult mConnectionResult;

	private SignInButton btnSignIn;
	private Button btnSignOut, btnRevokeAccess;
	private ImageView imgProfilePic, imgCoverImage;
	private TextView personNameView, personEmailView;
	private LinearLayout llDatos;
	private RelativeLayout rlDatos, rlBoton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_main_activity_splash);

		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#00000000")));
		// getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		/*
		 * View decorView = getWindow().getDecorView(); // Hide the status bar.
		 * int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		 * decorView.setSystemUiVisibility(uiOptions); // Remember that you
		 * should never show the action bar if the // status bar is hidden, so
		 * hide that too if necessary. ActionBar actionBar = getActionBar();
		 * actionBar.hide();
		 */

		btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
		btnSignOut = (Button) findViewById(R.id.btn_sign_out);
		btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);
		imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
		imgCoverImage = (ImageView) findViewById(R.id.imgCoverImage);
		personNameView = (TextView) findViewById(R.id.txtName);
		personEmailView = (TextView) findViewById(R.id.txtEmail);
		llDatos = (LinearLayout) findViewById(R.id.lldata);
		rlDatos = (RelativeLayout) findViewById(R.id.rldata);
		rlBoton = (RelativeLayout) findViewById(R.id.rlbutton);

		// Button click listeners
		btnSignIn.setOnClickListener(this);
		btnSignOut.setOnClickListener(this);
		btnRevokeAccess.setOnClickListener(this);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
	}

	public static Bitmap getCircularBitmapWithWhiteBorder(Bitmap bitmap,
			int borderWidth) {
		if (bitmap == null || bitmap.isRecycled()) {
			return null;
		}

		final int width = bitmap.getWidth() + borderWidth;
		final int height = bitmap.getHeight() + borderWidth;

		Bitmap canvasBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP,
				TileMode.CLAMP);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setShader(shader);

		Canvas canvas = new Canvas(canvasBitmap);
		float radius = width > height ? ((float) height) / 2f
				: ((float) width) / 2f;
		canvas.drawCircle(width / 2, height / 2, radius, paint);
		paint.setShader(null);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(borderWidth);
		canvas.drawCircle(width / 2, height / 2, radius - borderWidth / 2,
				paint);
		return canvasBitmap;
	}

	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	/**
	 * Method to resolve any signin errors
	 * */
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
		//Toast.makeText(this, "Usuario  conectado", Toast.LENGTH_LONG).show();

		// Get user's information
		getProfileInformation();

		// Update the UI after signin
		updateUI(true);

	}

	/**
	 * Updating the UI, showing/hiding buttons and profile layout
	 * */
	private void updateUI(boolean isSignedIn) {
		if (isSignedIn) {
			rlBoton.setVisibility(View.GONE);
			llDatos.setVisibility(View.VISIBLE);
			rlDatos.setVisibility(View.VISIBLE);
		} else {
			rlBoton.setVisibility(View.VISIBLE);
			llDatos.setVisibility(View.GONE);
			rlDatos.setVisibility(View.GONE);
		}
	}

	/**
	 * Fetching user's information name, email, profile pic
	 * */
	private void getProfileInformation() {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi
						.getCurrentPerson(mGoogleApiClient);
				Intent intent = new Intent(getApplicationContext(), NewProductActivity.class);
				intent.putExtra("id", currentPerson.getId().toString());
				intent.putExtra("nombre", currentPerson.getDisplayName());
				intent.putExtra("email", Plus.AccountApi.getAccountName(mGoogleApiClient));
				Toast.makeText(getApplicationContext(),
						currentPerson.getId().toString(), Toast.LENGTH_LONG)
						.show();
				startActivity(intent);
				//startActivityForResult(intent, 0);
				//personNameView.setText(currentPerson.getDisplayName());
				//personNameView.setText(currentPerson.getId()); 
				//personEmailView.setText(Plus.AccountApi
				//		.getAccountName(mGoogleApiClient));
				if (currentPerson.hasImage()) {

					Person.Image image = currentPerson.getImage();

					new AsyncTask<String, Void, Bitmap>() {

						@Override
						protected Bitmap doInBackground(String... params) {

							try {
								int pos = params[0].indexOf("?");
								String novaURL = params[0].substring(0, pos);
								URL url = new URL(novaURL);
								InputStream in = url.openStream();
								return BitmapFactory.decodeStream(in);
							} catch (Exception e) {
								/* TODO log error */
							}
							return null;
						}

						/*@Override
						protected void onPostExecute(Bitmap bitmap) {
							Bitmap bmp = bitmap;
							imgProfilePic
									.setImageBitmap(getCircularBitmapWithWhiteBorder(
											bmp, 15));
						}*/
					}.execute(image.getUrl());
				}

				if (currentPerson.hasCover()) {

					Person.Cover.CoverPhoto cover = currentPerson.getCover()
							.getCoverPhoto();

					new AsyncTask<String, Void, Bitmap>() {

						@Override
						protected Bitmap doInBackground(String... params) {

							try {
								URL url = new URL(params[0]);
								InputStream in = url.openStream();
								return BitmapFactory.decodeStream(in);
							} catch (Exception e) {
								/* TODO log error */
							}
							return null;
						}

						/*@Override
						protected void onPostExecute(Bitmap bitmap) {
							imgCoverImage.setImageBitmap(bitmap);
						}*/
					}.execute(cover.getUrl());
				}
			} else {
				Toast.makeText(getApplicationContext(),
						"No hay informacion de la persona", Toast.LENGTH_LONG)
						.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
		updateUI(false);
	}

	/**
	 * Button on click listener
	 * */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sign_in:
			// Signin button clicked
			signInWithGplus();
			break;
		case R.id.btn_sign_out:
			// Signout button clicked
			signOutFromGplus();
			break;
		case R.id.btn_revoke_access:
			// Revoke access button clicked
			revokeGplusAccess();
			break;
		}
	}

	/**
	 * Verifica si hay conexion a la red
	 * */

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	/**
	 * Sign-in into google
	 * */
	private void signInWithGplus() {
		if (!mGoogleApiClient.isConnecting() && isNetworkAvailable() == true) {
			mSignInClicked = true;
			resolveSignInError();
		} else {
			Toast.makeText(getApplicationContext(), "Problemas con la red",
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Sign-out from google
	 * */
	private void signOutFromGplus() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();
			updateUI(false);
		}
	}

	/**
	 * Revoking access from google
	 * */
	private void revokeGplusAccess() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
					.setResultCallback(new ResultCallback<Status>() {
						@Override
						public void onResult(Status arg0) {
							Log.e(TAG, "Acceso revocado");
							mGoogleApiClient.connect();
							updateUI(false);
						}

					});
		}
	}
}