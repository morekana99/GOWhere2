package com.example.ryan.gomap3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.translate.ocr.OcrCallback;
import com.baidu.translate.ocr.OcrClient;
import com.baidu.translate.ocr.OcrClientFactory;
import com.baidu.translate.ocr.entity.Language;
import com.baidu.translate.ocr.entity.OcrContent;
import com.baidu.translate.ocr.entity.OcrResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;


public class TransActivity extends AppCompatActivity {
    public static final String LANDMARK_NAME = "Landmark_name";
    public  static final String LANDMARK_IMAGE_ID = "landmark_image_id";
    public  static final String LANDMARK_CITY_NAME = "landmark_cityname";

    public  static final int ERROR = 0;
    public  static final int TO_JA = 1;
    public  static final int TO_EN = 2;
    private ImageView transpicture;
    private Uri imageUri;
    private TextView textView;
    private String transResult;
    private ProgressBar progressBar;


//    OcrClient client = OcrClientFactory.create(TransActivity.this,"20190325000281031","KphRdTtQ7cnXCLIgNcE1");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans);
        transpicture = (ImageView) findViewById(R.id.trans_image);
        textView = (TextView) findViewById(R.id.trans_result);
        Button button = (Button) findViewById(R.id.search_botton);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        File outputImage = new File(getExternalCacheDir(),"output_image.jpg");
        try{
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        }catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT>=24){
            imageUri = FileProvider.getUriForFile(TransActivity.this,"com.example.ryan.gomap3.fileprovider",outputImage);
        }
        else {
            imageUri = Uri.fromFile(outputImage);
        }
       button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent LandmarkIntent = new Intent(TransActivity.this,Landmark_DetailActivity.class);
               LandmarkIntent.putExtra(Landmark_DetailActivity.LANDMARK_NAME,"东京塔");
               LandmarkIntent.putExtra(Landmark_DetailActivity.LANDMARK_IMAGE_ID,R.drawable.p20);
               LandmarkIntent.putExtra(Landmark_DetailActivity.LANDMARK_CITY_NAME,"东京");
               startActivity(LandmarkIntent);

           }
       });
        Intent languageIntent = getIntent();
        int getLanguage = languageIntent.getIntExtra("language",0);

        Intent takePhotoIntent =new Intent("android.media.action.IMAGE_CAPTURE");
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(takePhotoIntent,getLanguage);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        progressBar.setVisibility(View.VISIBLE);
        switch (requestCode){
            case TO_JA:
                if(resultCode == RESULT_OK){
                    try{
                         Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));


                        OcrClient client = OcrClientFactory.create(TransActivity.this,
                                        "20210428000806058","EeT716HJ6txHFTz5Yiz_");
                        client.getOcrResult(Language.JP, Language.ZH, bitmap, new OcrCallback() {
                            @Override
                            public void onOcrResult(OcrResult ocrResult) {
                                if (ocrResult == null) {
                                    Toast.makeText(TransActivity.this,"回调为空",Toast.LENGTH_SHORT).show();
                                }
                                transResult=toContentsString(ocrResult.getContents());
                                textView.setText(transResult);
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                        transpicture.setImageBitmap(bitmap);



                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            case TO_EN:
                if(resultCode == RESULT_OK){
                    try{
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));


                        OcrClient client = OcrClientFactory.create(TransActivity.this,
                                "20210428000806058","EeT716HJ6txHFTz5Yiz_ ");
                        client.getOcrResult(Language.EN, Language.ZH, bitmap, new OcrCallback() {
                            @Override
                            public void onOcrResult(OcrResult ocrResult) {
                                if (ocrResult == null) {
                                    Toast.makeText(TransActivity.this,"回调为空",Toast.LENGTH_SHORT).show();
                                }
                                transResult=toContentsString(ocrResult.getContents());
                                textView.setText(transResult);
                                Log.d("qqqqqqqqqqq", ocrResult.getError()+"");

                                progressBar.setVisibility(View.GONE);
                            }
                        });

                        transpicture.setImageBitmap(bitmap);




                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            case ERROR:
                finish();
            default:
                finish();
        }


    }
    private String toResultString(OcrResult ocrResult) {
        if (ocrResult == null) {
            return "null";
        }
        return "OcrResult{\n"
                + "   error=" + ocrResult.getError()
                + ",\n   errorMsg=" + ocrResult.getErrorMsg()
                + ",\n   from='" + ocrResult.getFrom()
                + ",\n   to='" + ocrResult.getTo()
                + ",\n   contents=" + toContentsString(ocrResult.getContents())
                + ",\n   sumSrc='" + ocrResult.getSumSrc()
                + ",\n   sumDst='" + ocrResult.getSumDst()
                + "\n}";
    }
    private String toContentsString(List<OcrContent> contents) {
        if (contents == null) {
            return " ";
        }
        Iterator<OcrContent> it = contents.iterator();
        if (!it.hasNext())
            return " ";
        StringBuilder sb = new StringBuilder();
        for (; ; ) {
            OcrContent e = it.next();
            sb.append(toContentString(e));
            if (!it.hasNext())
                return sb.append(' ').toString();
            sb.append(' ');
        }
    }
    private String toContentString(OcrContent content) {
        if (content == null) {
            return "null";
        }
        return "\n "+content.getDst();
    }
}


