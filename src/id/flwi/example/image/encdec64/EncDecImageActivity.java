package id.flwi.example.image.encdec64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EncDecImageActivity extends Activity {
	Button buttonLoad = null;
	Button buttonConvert = null;
	TextView textEncode = null;
	ImageView imageConvertResult = null;

	static final int INTENT_REQUEST_CODE_BROWSE_PICTURE = 1;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        buttonLoad = (Button)findViewById(R.id.buttonLoadImage);
        buttonConvert = (Button)findViewById(R.id.buttonConvertToImage);
        textEncode = (TextView)findViewById(R.id.textViewResult);
        imageConvertResult = (ImageView)findViewById(R.id.imageViewConvert);
        
		SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
		String previouslyEncodedImage = shre.getString("image_data", "");
		
		if( !previouslyEncodedImage.equalsIgnoreCase("") ){
			byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
			imageConvertResult.setImageBitmap(bitmap);
		}

        
        buttonLoad.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				startActivityForResult(intent,
						INTENT_REQUEST_CODE_BROWSE_PICTURE);

			}
		});
        
        buttonConvert.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				byte[] b = Base64.decode(textEncode.getText().toString(), Base64.DEFAULT);
				Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
				imageConvertResult.setImageBitmap(bitmap);
			}
		});
    }
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		switch (requestCode) {
		case INTENT_REQUEST_CODE_BROWSE_PICTURE:
			if( resultCode == Activity.RESULT_OK ){

				InputStream stream;
				String filePath = null;
				try {
					stream = getContentResolver().openInputStream(intent.getData());

					Bitmap realImage = BitmapFactory.decodeStream(stream);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					realImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);   
					byte[] b = baos.toByteArray(); 
					
					String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
					textEncode.setText(encodedImage);
					
					SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
				    Editor edit=shre.edit();
				    edit.putString("image_data",encodedImage);
				    edit.commit();
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		}
	}
}