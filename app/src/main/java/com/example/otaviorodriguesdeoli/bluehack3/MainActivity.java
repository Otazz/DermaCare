package com.example.otaviorodriguesdeoli.bluehack3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.ssh.Scp;
import org.jibble.simpleftp.SimpleFTP;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    TextView txt, t2;
    JSONObject jso;
    Button bt, b2;
    SCP scp;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_IMAGE = 2;
    Double score;
    String mCurrentPhotoPath, cla;
    RequestQueue queue;
    JsonObjectRequest jsObjRequest;


    String baseUrl ="http://oplab134.parqtec.unicamp.br:20256/powerai-vision/api/dlapis/9ae47ad9-70e2-46a9-893d-0c01e8879bc8?imageUrl=";

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void selectPhoto(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void dispatchTakePictureIntent() {
        Log.w("Veio", "aqui");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                Log.e("a", "NOPE");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txt = (TextView) findViewById(R.id.t1);
        t2 = (TextView) findViewById(R.id.t2);
        bt = (Button) findViewById(R.id.button);
        b2 = (Button) findViewById(R.id.b2);



        scp = new SCP();
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //serverPath = uploadImg("", 10, "", "");
            Log.w("aa", mCurrentPhotoPath);
        }
        if (requestCode == PICK_IMAGE && data != null) {
            //Uri selectedImageURI = data.getData();
            mCurrentPhotoPath = MainActivity.getRealPath(this, data.getData());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //mCurrentPhotoPath =
            Log.w("aa", mCurrentPhotoPath);

        }
        try {
            org.apache.tools.ant.taskdefs.optional.ssh.Scp scp = new Scp();
            int portSSH = 22;
            String srvrSSH = "54.149.66.124";
            String userSSH = "ubuntu";
            String pswdSSH = "senha";
            String localFile = mCurrentPhotoPath;
            String remoteDir = "/var/www/html/";

            scp.setPort( portSSH );
            scp.setLocalFile( localFile );
            scp.setProject( new Project() );
            scp.setTodir( userSSH + ":" + pswdSSH + "@" + srvrSSH + ":" + remoteDir );
            scp.setTrust( true );
            scp.execute();
        }

        catch (Exception e){
            Log.w("aa", "aak");
            e.printStackTrace();
        }
            String[] path = mCurrentPhotoPath.split("/");
            String url = baseUrl + "http://54.149.66.124"+"/"+path[path.length-1];
            Log.w("aa", url);
            getProblem(url);
    }

    public static String getRealPath(Context context, Uri uri) {
        String filePath = "";

        if (uri.getHost().contains("com.android.providers.media")) {
            // Image pick from recent
            String wholeID = DocumentsContract.getDocumentId(uri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Images.Media.DATA};

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null);

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
            return filePath;
        } else {
            // image pick from gallery
            return  getRealPathFromURI_BelowAPI11(context,uri);
        }

    }
    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri){
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String uploadImg(String server, int port, String usr, String pwd){
        try {
            SimpleFTP ftp = new SimpleFTP();

            // Connect to an FTP server on port 21.
            ftp.connect(server, port, usr, pwd);

            // Set binary mode.
            ftp.bin();

            // Change to a new working directory on the FTP server.
            ftp.cwd("images");

            // Upload some files.
            ftp.stor(new File(mCurrentPhotoPath));

            // Quit from the FTP server.
            ftp.disconnect();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return mCurrentPhotoPath;
    }

    public void getProblem(String url){
        jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        jso = response;
                        try {
                            String[] dict = jso.getString("classified").split(":");
                            cla = dict[0].replace("{", "").replace("\"", "");
                            score = Double.parseDouble(dict[1].replace("\"", "")
                                    .replace("}", ""));
                            String msg;
                            if (score > 0.7)
                                if(cla.equals("Melanoma Benigno"))
                                    msg = "Não há indícios de um problema de pele " +
                                            ". No entanto uma visita regular ao médico é recomendada";
                                else
                                    msg = "Suspeita de um problema de pele. " +
                            "Recomenda-se a visita a um especialista.";

                            /*else if(score > 0.5)
                                msg = "Foi classicado como " + cla +
                                        ", mas com uma porcentagem baixa.";*/
                            else
                                msg = "Não há nenhuma patologia que foi destacada,"+
                                        " em caso de preocupação visite um dermatologista";


                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                            alertDialog.setTitle("Sr. Paciente");
                            alertDialog.setMessage(msg);
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        } catch(Exception e){
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        txt.setText(error.getMessage());
                    }
                });
        queue.add(jsObjRequest);
        /*try{Thread.sleep(5000);}
        catch(InterruptedException e){
            e.printStackTrace();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}



