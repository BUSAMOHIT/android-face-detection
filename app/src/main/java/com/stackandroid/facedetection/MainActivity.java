package com.stackandroid.facedetection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    FaceDetector detector;
    Bitmap imageBitmap;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        InputStream stream = getResources().openRawResource(R.raw.sample_image);
        imageBitmap = BitmapFactory.decodeStream(stream);

        imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageBitmap(imageBitmap);

        Button btnDetectFaces = (Button) findViewById(R.id.btn_detect_faces);
        btnDetectFaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectFaces();
            }
        });
    }

    private void detectFaces() {
        Bitmap bmp = Bitmap.createBitmap(imageBitmap.getWidth(), imageBitmap.getHeight(), imageBitmap.getConfig());
        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(imageBitmap, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        Paint landmarkPaint = new Paint();
        landmarkPaint.setColor(Color.RED);
        landmarkPaint.setStyle(Paint.Style.STROKE);
        landmarkPaint.setStrokeWidth(5);


        Frame frame = new Frame.Builder().setBitmap(imageBitmap).build();
        SparseArray<Face> faces = detector.detect(frame);
        if(faces.size() > 0){
            for (int i = 0; i < faces.size(); ++i) {
                Face face = faces.valueAt(i);

                canvas.drawRect(
                        face.getPosition().x,
                        face.getPosition().y,
                        face.getPosition().x + face.getWidth(),
                        face.getPosition().y + face.getHeight(), paint);

                for (Landmark landmark : face.getLandmarks()) {
                    int cx = (int) (landmark.getPosition().x);
                    int cy = (int) (landmark.getPosition().y);
                    canvas.drawCircle(cx, cy, 5, landmarkPaint);
                }
            }

            imageView.setImageBitmap(bmp);
            Toast.makeText(this, faces.size() + " faces detected", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "No faces detected", Toast.LENGTH_LONG).show();
        }
    }
}
