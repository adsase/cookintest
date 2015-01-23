package dam2.sixapp.cookin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class AddUserActivity extends AsyncTask<String,Void, Void> {

	// Dialogo de progreso
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	InputStream is = null;
	String result = null;
	String line = null;
	int code;

	// URL al servicio de creacion de usuario
	private static String url_create_product = "http://cookin.hol.es/android_connect/create_user.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";

	/**
	 * Antes de empezar en hilo en segundo plano, muestra el dialogo de
	 * progreso
	 * */
	public AddUserActivity(MainActivity activity) {
        pDialog = new ProgressDialog(activity);
    }
 
    @Override
    protected void onPreExecute() {
        pDialog.setMessage("Cargando");
        pDialog.show();
    }
	

	/**
	 * AsyncTask en segundo plano para crear el nuevo usuario
	 * */
		
		@Override
		/**
		 * Creando usuario
		 * */
		protected Void doInBackground(String... args) {
			//Intent intent = getIntent();
			String id = args[0];//intent.getStringExtra("id");
			String nombre = args[1]; //intent.getStringExtra("nombre");
			String mail = args[2];//intent.getStringExtra("email");
			// Log.d("id", id);
			// Log.d("name", nombre);
			// Log.d("mail", mail);

			/** CODIGO DE PRUEBAS **/

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("id", id));
			nameValuePairs.add(new BasicNameValuePair("nombre", nombre));
			nameValuePairs.add(new BasicNameValuePair("mail", mail));

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url_create_product);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				Log.e("pass 1", "connection success ");
			} catch (Exception e) {
				Log.e("Fail 1", e.toString());
				//Toast.makeText(getApplicationContext(), "Invalid host Address",
				//		Toast.LENGTH_LONG).show();
			}

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
				Log.e("pass 2", "connection success ");
			} catch (Exception e) {
				Log.e("Fail 2", e.toString());
			}

			try {
				JSONObject json_data = new JSONObject(result);
				Log.d("json_data", json_data.toString());
				code = (json_data.getInt("success"));
				String enteroString = Integer.toString(code);
				Log.d("codeG", enteroString);

				if (code == 1) {
					// Toast.makeText(getApplicationContext(),
					// "Usuario insertado", Toast.LENGTH_LONG)
					// .show();
					Log.d("code1-OK", "code1-OK");
					
					//finishActivity(code);
				} else {
					// Toast.makeText(getApplicationContext(),
					// "Error al a�adir usuario", Toast.LENGTH_LONG)
					// .show();
					Log.d("code0-FAIL", "code0-FAIL");
				}
			} catch (Exception e) {
				Log.e("Fail 3", e.toString());
			}
			return null;

			/** CODIGO DE PRUEBAS **/

			
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(Void res) {
			//super.onPostExecute(null);
			Log.d("cookin","Estic en el onPostExecute");
			// dismiss the dialog once done
			pDialog.dismiss();
		}

		

	
}