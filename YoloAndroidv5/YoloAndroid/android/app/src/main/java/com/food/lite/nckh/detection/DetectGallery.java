package com.food.lite.nckh.detection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
//import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.food.lite.nckh.detection.adapter.FoodAdapter;
import com.food.lite.nckh.detection.customview.OverlayView;
import com.food.lite.nckh.detection.env.ImageUtils;
import com.food.lite.nckh.detection.env.Logger;
import com.food.lite.nckh.detection.env.Utils;
import com.food.lite.nckh.detection.model.Food;
import com.food.lite.nckh.detection.sqlite.sqlFood;
import com.food.lite.nckh.detection.tflite.Classifier;
import com.food.lite.nckh.detection.tflite.YoloV5Classifier;
import com.food.lite.nckh.detection.tracking.MultiBoxTracker;

import org.tensorflow.lite.examples.detection.R;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class DetectGallery extends AppCompatActivity {
    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.3f;
    public static final int TF_OD_API_INPUT_SIZE = 416;
    private static final boolean TF_OD_API_IS_QUANTIZED = false;
    private static final String TF_OD_API_MODEL_FILE = "best-fp16.tflite";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/coco.txt";

    //private  Classifier detector;
    private YoloV5Classifier detector;

    ImageView imgHinh;
    Button btnCamera, btnChoose;
    TextView txtResult;
    int imgSize = 416;

    RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private FoodAdapter foodAdapter;
    sqlFood sqlfood;


    private static final boolean MAINTAIN_ASPECT = true;
    final Integer sensorOrientation = 90; //edit

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;
    private MultiBoxTracker tracker;
    private OverlayView trackingOverlay;

    protected int previewWidth = 0;
    protected int previewHeight = 0;

    //private Bitmap sourceBitmap;
    private Bitmap cropBitmap;

    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_detect);

        //Khang add
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle(R.string.galarrey_detection);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //RecycleView cho detect
        recyclerView = (RecyclerView) findViewById(R.id.idRecycle2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setVisibility(View.GONE);

        imgHinh = (ImageView) findViewById(R.id.imageViewHinh);
        btnCamera = (Button) findViewById(R.id.btnOpenCam);
        btnChoose = (Button) findViewById(R.id.btnChooseImg);
        txtResult = (TextView) findViewById(R.id.result);

        btnCamera.setOnClickListener(v -> openCamera());

        btnChoose.setOnClickListener(v -> openGallery());

        try {
            detector =
                    YoloV5Classifier.create(
                            getAssets(),
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_IS_QUANTIZED,
                            TF_OD_API_INPUT_SIZE);
        } catch (final IOException e) {
            e.printStackTrace();
            //LOGGER.e(e, "Exception initializing classifier!");
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
    }


    public void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 2);
    }
    public void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 3);
    }

    private static final Logger LOGGER = new Logger();

    public void classifyImage(Bitmap img){
        final List<Classifier.Recognition> results = detector.recognizeImage(img);
        //final Canvas canvas = new Canvas(img);
        final Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);
//        adapter = null;
//        recyclerView.setAdapter(adapter);

        final List<Classifier.Recognition> mappedRecognitions =
                new LinkedList<Classifier.Recognition>();

        String s = "";
        String maxClass = "";
        float maxconf = 0;
        int maxpos = -1;
        for (final Classifier.Recognition result : results) {
            final RectF location = result.getLocation();
            if (location != null && result.getConfidence() >= MINIMUM_CONFIDENCE_TF_OD_API) {
                //canvas.drawRect(location, paint);
                cropToFrameTransform.mapRect(location);
                result.setLocation(location);
                mappedRecognitions.add(result);
            }
//            float confidences = result.getConfidence();
            if(result.getConfidence() > MINIMUM_CONFIDENCE_TF_OD_API) {
                if (result.getConfidence() > maxconf) {
                    maxClass = result.getTitle();
                    maxconf = result.getConfidence();
                    maxpos = result.getDetectedClass();
                }
            }
        }
        String sx = String.format("%s: %.2f%% \n", maxClass, maxconf * 100);
        s = s.concat(sx);
        tracker.trackResults(mappedRecognitions, new Random().nextInt());
        trackingOverlay.postInvalidate();
        trackingOverlay.setVisibility(View.VISIBLE);
        imgHinh.setImageBitmap(img);
        if(maxpos == -1) {
            recyclerView.setVisibility(View.GONE);
            txtResult.setText("Không thể nhận dạng");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            getDetect(maxpos+1);
            txtResult.setText(s);
        }
    }

    //add
    private void getDetect(Integer food_id) {
        sqlfood = new sqlFood(this);
        foodAdapter = new FoodAdapter(this, sqlfood.getFoodId(food_id));
        recyclerView.setAdapter(foodAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                Bitmap imgCam = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(imgCam.getWidth(), imgCam.getHeight());
                imgCam = ThumbnailUtils.extractThumbnail(imgCam, dimension, dimension);

                cropBitmap = Utils.processBitmap(imgCam, TF_OD_API_INPUT_SIZE);
                imgHinh.setImageBitmap(imgCam);

                loading = new ProgressDialog(this);
                loading.setMessage("Vui lòng chờ, ứng dụng đang tiến hành nhận dạng");
                loading.show();
                initBox();
                imgCam = Bitmap.createScaledBitmap(imgCam, imgSize, imgSize, false);
//                ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//                ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
//
//                System.err.println(Double.parseDouble(configurationInfo.getGlEsVersion()));
//                System.err.println(configurationInfo.reqGlEsVersion >= 0x30000);
//                System.err.println(String.format("%X", configurationInfo.reqGlEsVersion));

                Bitmap finalImgCam = imgCam;
                new CountDownTimer(2000,1000) {
                    public void onFinish() {
                        classifyImage(finalImgCam);
                        loading.dismiss();
                    }

                    public void onTick(long millisUntilFinished) {

                    }
                }.start();

            } else if (requestCode == 3) {
                Uri dat = data.getData();
                Bitmap imgChoose = null;
                try {
                    imgChoose = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                cropBitmap = Utils.processBitmap(imgChoose, TF_OD_API_INPUT_SIZE);
                imgHinh.setImageBitmap(imgChoose);
                loading = new ProgressDialog(this);
                loading.setMessage("Vui lòng chờ, ứng dụng đang tiến hành nhận dạng");
                loading.show();
                initBox();
//                ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//                ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
//
//                System.err.println(Double.parseDouble(configurationInfo.getGlEsVersion()));
//                System.err.println(configurationInfo.reqGlEsVersion >= 0x30000);
//                System.err.println(String.format("%X", configurationInfo.reqGlEsVersion));
                imgChoose = Bitmap.createScaledBitmap(imgChoose, imgSize, imgSize, false);
                Bitmap finalImgChoose = imgChoose;
                new CountDownTimer(2000,1000) {
                    public void onFinish() {
                        classifyImage(finalImgChoose);
                        loading.dismiss();
                    }

                    public void onTick(long millisUntilFinished) {

                    }
                }.start();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initBox() {
        previewHeight = TF_OD_API_INPUT_SIZE;
        previewWidth = TF_OD_API_INPUT_SIZE;
        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE,
                        sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        tracker = new MultiBoxTracker(this);
        trackingOverlay = findViewById(R.id.tracking_overlay);
        trackingOverlay.addCallback(
                canvas -> tracker.draw(canvas));

        tracker.setFrameConfiguration(TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE, sensorOrientation);

        try {
            detector =
                    YoloV5Classifier.create(
                            getAssets(),
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_IS_QUANTIZED,
                            TF_OD_API_INPUT_SIZE);
            Log.d("YoloV5Classifier",  "model loaded successfully: " + TF_OD_API_MODEL_FILE);
            //detector.useGpu();
        } catch (final IOException e) {
            e.printStackTrace();
            LOGGER.e(e, "Exception initializing classifier!");
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
    }
}

