package com.example.pixabayandr;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        simpleGallery = (Gallery) findViewById(R.id.languagesGallery);

        // get the reference of ImageView
        selectedImageView = (ImageView) findViewById(R.id.imageView2);

        // initialize the adapter


        // Let us do item click of gallery and image can be identified by its position
        simpleGallery.setOnItemClickListener((parent, view, position, id) -> {
            // Whichever image is clicked, that is set in the  selectedImageView
            // position will indicate the location of image
            selectedImageView.setImageBitmap(images[position]);
        });

    }
    Bitmap[]images;
    Gallery simpleGallery;
    CustomizedGalleryAdapter customGalleryAdapter;
    ImageView selectedImageView;
    ImageView imageView;
    public void getImgs(View view) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                EditText edTxt=findViewById(R.id.textSearch);
                if(!edTxt.getText().equals("")){
                    try {
                        URL githubEndpoint = new URL("https://pixabay.com/api/?key=29849780-56b32ebf0329ff595c02e6893&q="+edTxt.getText()+"&image_type=photo");
                        HttpsURLConnection myConnection = (HttpsURLConnection) githubEndpoint.openConnection();
                        myConnection.setRequestMethod("GET");
                        BufferedReader in = new BufferedReader(new InputStreamReader(myConnection.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        JSONObject root = new JSONObject(response.toString());
                        JSONArray pict = root.getJSONArray("hits");
                        ArrayList<Bitmap> temp=getBitmapArrey(pict);
                        images = temp.toArray(new Bitmap[0]);

                        setGallery();
                        addImgToImgView(images[0]);
                        myConnection.disconnect();
                    } catch (MalformedURLException e) {
                        //Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        //throw new RuntimeException(e);
                    } catch (ProtocolException e) {
                        //Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        //throw new RuntimeException(e);
                    } catch (IOException e) {
                        //throw new RuntimeException(e);
                    } catch (JSONException e) {
                        //Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        //throw new RuntimeException(e);
                    }
                }
            }
        });
    }
    public ArrayList<Bitmap> getBitmapArrey(JSONArray arr) throws IOException, JSONException {
        ArrayList<Bitmap> temp = new ArrayList<>();
        for(int i=0;i<arr.length();i++){
            JSONObject c = arr.getJSONObject(i);
            String largeImageURL = c.getString("largeImageURL");
            URL imgUrl = new URL(largeImageURL);
            Bitmap bitMap = BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream());
            temp.add(bitMap);
        }
        return temp;
    }
    public void addImgToImgView(Bitmap bm){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                selectedImageView.setImageBitmap(bm);
            }
        });
    }
    public void setGallery(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                customGalleryAdapter = new CustomizedGalleryAdapter(getApplicationContext(), images);
                simpleGallery.setAdapter(customGalleryAdapter);
            }
        });
    }
}