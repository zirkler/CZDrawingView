package com.zirkler.czdrawingview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;


public class MainActivity extends AppCompatActivity {

    CZDrawingView mDrawingView;
    Toolbar mBottomToolbar;
    Button mUndo;
    Button mRedo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawingView = (CZDrawingView) findViewById(R.id.drawingView);
        mUndo        = (Button) findViewById(R.id.bttUndo);
        mRedo        = (Button) findViewById(R.id.bttRedo);

        mUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawingView.undo();
            }
        });

        mRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawingView.redo();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_freehand_drawing) {
            mDrawingView.setCurrentDrawingAction(new CZDrawingActionFreehand(this, null));
        } else if (item.getItemId() == R.id.action_eraser) {
            mDrawingView.setCurrentDrawingAction(new CZDrawingActionEraser(this, null));
        } else if (item.getItemId() == R.id.action_pick_background) {
            EasyImage.openChooserWithDocuments(this, "Choose Background Image", 0);
        } else if (item.getItemId() == R.id.action_measurement_line) {
            mDrawingView.setCurrentDrawingAction(new CZDrawingActionMeasurementLine(this, null));
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
                mDrawingView.setImageBitmap(bitmap);
            }
        });
    }
}
