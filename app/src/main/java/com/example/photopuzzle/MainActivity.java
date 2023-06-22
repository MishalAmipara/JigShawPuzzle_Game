package com.example.photopuzzle;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnDragListener, View.OnLongClickListener {

    RelativeLayout selectImg;
    ImageView imageView;
    Button startBtn;

    ImageView[] img = new ImageView[9];
    LinearLayout[] linear = new LinearLayout[9];
    List<Pictures> imgList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectImg = findViewById(R.id.selectImg);
        imageView = findViewById(R.id.img);
        startBtn = findViewById(R.id.startBtn);

        for (int i = 0; i < 9; i++) {
            int id1 = getResources().getIdentifier("linear" + i, "id", getPackageName());
            linear[i] = findViewById(id1);
            linear[i].setOnDragListener(this);

            int id3 = getResources().getIdentifier("img" + i, "id", getPackageName());
            img[i] = findViewById(id3);
        }

        selectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 404);
                }
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageView.getDrawable() != null) {
                    splitImage(imageView, 9);
                } else {
                    Toast.makeText(MainActivity.this, "Select Image First", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 404) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 202);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 202) {
                imageView.setImageURI(data.getData());
            }
        }
    }

    public void splitImage(ImageView imageView, int numOfPart) {
        int rows, cols;
        int chunkHeight, chunkWidth;
        List<Pictures> chunkedImages = new ArrayList<>();

        Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable drawablebg = imageView.getBackground();
        if (drawablebg != null) {
            drawablebg.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        imageView.draw(canvas);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        rows = cols = (int) Math.sqrt(numOfPart);//3
        chunkHeight = bitmap.getHeight() / rows;//15/3=5
        chunkWidth = bitmap.getWidth() / cols;//15/3=5

        int yCoord = 0;
        int i = 0;
        for (int x = 0; x < rows; x++) {
            int xCorod = 0;
            for (int y = 0; y < cols; y++) {
                //Pictures pic=new Pictures(Bitmap.createBitmap(scaledBitmap, xCorod, yCoord, chunkWidth, chunkHeight), i);
                chunkedImages.add(new Pictures(Bitmap.createBitmap(scaledBitmap, xCorod, yCoord, chunkWidth, chunkHeight), i));
                i++;
                xCorod += chunkWidth;
            }
            yCoord += chunkHeight;
        }

        imgList.addAll(chunkedImages);
        startBtn.setVisibility(View.GONE);
        selectImg.setVisibility(View.GONE);
        findViewById(R.id.grid).setVisibility(View.VISIBLE);
        Collections.shuffle(imgList);
        for (int j = 0; j < 9; j++) {
            img[j].setImageBitmap(imgList.get(j).getImg());
            img[j].setTag(String.valueOf(imgList.get(j).getTag()));
            img[j].setOnLongClickListener(MainActivity.this);
        }
    }

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        switch (dragEvent.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return true;

            case DragEvent.ACTION_DRAG_ENTERED:
                view.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                view.invalidate();
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                view.getBackground().clearColorFilter();
                view.invalidate();
                return true;

            case DragEvent.ACTION_DROP:
                view.getBackground().clearColorFilter();
                view.invalidate();

                LinearLayout layout = (LinearLayout) view;
                if (layout.getChildCount() == 0) {
                    View vw = (View) dragEvent.getLocalState();
                    ViewGroup group = (ViewGroup) vw.getParent();
                    group.removeView(vw);
                    group.setVisibility(View.GONE);
                    layout.addView(vw);
                    vw.setVisibility(View.VISIBLE);
                } else {
                    try {
                        View vw = (View) dragEvent.getLocalState();
                        ViewGroup group = (ViewGroup) vw.getParent();
                        View vw2 = layout.getChildAt(0);
                        layout.removeView(vw2);
                        group.removeView(vw);
                        layout.addView(vw);
                        group.addView(vw2);
                        vw.setVisibility(View.VISIBLE);
                        vw2.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                checkWin();
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                view.getBackground().clearColorFilter();
                view.invalidate();
                return true;

            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onLongClick(View view) {
        ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
        String[] mimeType = {ClipDescription.MIMETYPE_TEXT_PLAIN};
        ClipData data = new ClipData(view.getTag().toString(), mimeType, item);
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
        view.startDrag(data, shadowBuilder, view, 0);
        return true;
    }

    public void checkWin() {
        int count = 0;
        for (int i = 0; i < 9; i++) {
            View view = linear[i].getChildAt(0);
            int tag = Integer.parseInt(view.getTag().toString());
            if (tag != i) {
                count++;
            }
        }

        if (count == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(false);
            builder.setTitle("Win!");
            builder.setMessage("Congratulations You Win!!!");
            builder.setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    recreate();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
