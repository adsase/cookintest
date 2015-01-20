package dam2.sixapp.cookin;
 
import java.util.ArrayList;
import java.util.List;
 

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
 

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
 
public class NewProductActivity extends Activity {
 
    // Progress Dialog
    private ProgressDialog pDialog;
 
    JSONParser jsonParser = new JSONParser();
    //EditText inputName;
    //EditText inputPrice;
    //EditText inputDesc;
 
    // url to create new product
    private static String url_create_product = "http://cookin.hol.es/android_connect/create_user.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new CreateNewProduct().execute();
    }
 
    /**
     * Background Async Task to Create new product
     * */
    class CreateNewProduct extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewProductActivity.this);
            pDialog.setMessage("Creando usuario...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
        	Intent intent = getIntent();
        	String id = intent.getStringExtra("id");
            String nombre = intent.getStringExtra("nombre");
            String mail = intent.getStringExtra("email");
            Log.d("id", id);
            Log.d("name", nombre);
            Log.d("mail", mail);
 
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id));
            //Log.d("test1", "1");
            params.add(new BasicNameValuePair("nombre", nombre));
            params.add(new BasicNameValuePair("mail", mail));
            //Log.d("test2", "2");
            // getting JSON Object
            // Note that create product url accepts POST method
            //Log.d("AntesURL", "mierda");
            JSONObject json = jsonParser.makeHttpRequest(url_create_product,"GET", params);
            Log.d("DespuesURL", "despuesURL");
            // check log cat from response
            Log.d("Create Response", json.toString());
 
            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                Log.d("sdasd", "hola");
                if (success == 1) {
                    // successfully created product
                    //Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
                    //startActivity(i);
                	Log.d("asfsaf", "hola1");
                    // closing this screen
                    finishActivity(success);
                } else {
                	//finishActivity(success);
                    // failed to create product
                	Log.d("asfasgf", "hola2");
                	Toast.makeText(getApplicationContext(),
    						"Error al añadir usuario", Toast.LENGTH_LONG)
    						.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
 
    }
}