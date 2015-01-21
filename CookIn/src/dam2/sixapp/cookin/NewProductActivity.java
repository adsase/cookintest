package dam2.sixapp.cookin;
 
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
 



import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
 



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
 
public class NewProductActivity extends Activity {
 
    // Progress Dialog
    private ProgressDialog pDialog;
 
    JSONParser jsonParser = new JSONParser();
    InputStream is=null;
    String result=null;
	String line=null;
	int code;
 
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
            
            /*
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id));
            //Log.d("test1", "1");
            params.add(new BasicNameValuePair("nombre", nombre));
            params.add(new BasicNameValuePair("mail", mail));
            //Log.d("test2", "2");
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_product,"POST", params);
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
             */
    						/** CODIGO DE PRUEBAS **/
            
            
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("id",id));
            nameValuePairs.add(new BasicNameValuePair("nombre",nombre));
            nameValuePairs.add(new BasicNameValuePair("mail",mail));

            try{
            	HttpClient httpclient = new DefaultHttpClient();
            	HttpPost httppost = new HttpPost(url_create_product);
            	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            	HttpResponse response = httpclient.execute(httppost); 
            	HttpEntity entity = response.getEntity();
            	is = entity.getContent();
            	Log.e("pass 1", "connection success ");
            }
            catch(Exception e){
            	Log.e("Fail 1", e.toString());
            	Toast.makeText(getApplicationContext(), "Invalid host Address",
            			Toast.LENGTH_LONG).show();
            }     

            try{
            	BufferedReader reader = new BufferedReader
            			(new InputStreamReader(is,"iso-8859-1"),8);
            	StringBuilder sb = new StringBuilder();
            	while ((line = reader.readLine()) != null)
            	{
            		sb.append(line + "\n");
            	}
            	is.close();
            	result = sb.toString();
            	Log.e("pass 2", "connection success ");
            }
            catch(Exception e){
            	Log.e("Fail 2", e.toString());
            }     

            try{
            	JSONObject json_data = new JSONObject(result);
            	Log.d("json_data",json_data.toString());
            	code=(json_data.getInt("success"));
            	String enteroString = Integer.toString(code);
            	Log.d("codeG",enteroString);

            	if(code==1){
            		//Toast.makeText(getApplicationContext(),
    					//	"Usuario insertado", Toast.LENGTH_LONG)
    						//.show();
            		Log.d("code1-OK","code1-OK");
            		finishActivity(code);
            	}
            	else{
            		//Toast.makeText(getApplicationContext(),
    				//		"Error al añadir usuario", Toast.LENGTH_LONG)
    				//		.show();
            		Log.d("code0-FAIL","code0-FAIL");
            	}
            }
            catch(Exception e){
            	Log.e("Fail 3", e.toString());
            }

            		/** CODIGO DE PRUEBAS **/
            
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