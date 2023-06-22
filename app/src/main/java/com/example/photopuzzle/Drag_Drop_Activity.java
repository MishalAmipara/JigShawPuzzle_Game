package com.example.photopuzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Drag_Drop_Activity extends AppCompatActivity {

    ImageView imageView;
    TextView txt1, txt2, txt3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_drop);
        //imageView=findViewById(R.id.imgView);
        txt1=findViewById(R.id.textView1);
        txt2=findViewById(R.id.textView2);
        txt3=findViewById(R.id.textView3);

        txt1.setOnLongClickListener(longClickListener);
        txt2.setOnLongClickListener(longClickListener);
        txt3.setOnDragListener(dragListener);
    }

    View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            ClipData clipData = ClipData.newPlainText("", "");
            View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(clipData, dragShadowBuilder, v, 0);


            return true;
        }
    };
    View.OnDragListener dragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    final View view= (View) event.getLocalState();
                    Log.d("TTT", "onDrag: DragEntered");
                    if(view.getId()==R.id.textView1)
                    {
                        txt3.setText("Txt1 is Dragged");
                    }
                    if(view.getId()==R.id.textView2)
                    {
                        txt3.setText("Txt2 is Dragged");
                    }
                    break;
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.d("TTT", "onDrag: DragStarted");
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    Log.d("TTT", "onDrag: DragExited");
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    Log.d("TTT", "onDrag: DragEnded");
                    break;
                case DragEvent.ACTION_DROP:
                    Log.d("TTT", "onDrag: Dropped");
                    View view1= (View) event.getLocalState();
                    if(view1.getId()==R.id.textView1)
                    {
                        txt1.setText("Txt3");
                    }
                    break;
            }

            return true;
        }
    };

//    float x,y;
//    float rawX,rawY;
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if(event.getAction()==MotionEvent.ACTION_DOWN)
//        {
//            x=event.getX();
//            y=event.getY();
//        }
//        if(event.getAction()==MotionEvent.ACTION_MOVE)
//        {
//            rawX=event.getX() - x;
//            rawY=event.getY() - y;
//
//            imageView.setX(imageView.getX()+rawX);
//            imageView.setY(imageView.getY()+rawY);
//
//            x=event.getX();
//            y=event.getY();
//        }
//        return super.onTouchEvent(event);
//
//    }
}