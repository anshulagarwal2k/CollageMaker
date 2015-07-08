package com.anshul.android.collagemaker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    private final int REQUEST_ADD_IMAGE = 1000;
    private String selectedImagePath;
    private CollageView mCollageView;
    private int[] backgroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCollageView = (CollageView) findViewById(R.id.collage_view);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            if (mCollageView.canAddMoreImage()) {
                addImage();
            } else {
                Toast.makeText(this, getString(R.string.max_image_msg), Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.action_save) {
            mCollageView.save();
        } else {
            showBackgroundColorDialog();

        }

        return super.onOptionsItemSelected(item);
    }

    private void showBackgroundColorDialog() {
        if (backgroundColor == null) {
            backgroundColor = getResources().getIntArray(R.array.background_color);
        }
        AlertDialog.Builder mAlertBuilder = new AlertDialog.Builder(this);
        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.backgroun_grid, null);
        GridView gridView = (GridView) linearLayout.findViewById(R.id.grid_view);
        gridView.setAdapter(new GridAdapter());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCollageView.setcolor(backgroundColor[i]);
            }
        });
        mAlertBuilder.setView(linearLayout);
        mAlertBuilder.setTitle(getString(R.string.background_dialog_title));
        mAlertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        mAlertBuilder.show();
    }

    private void addImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), REQUEST_ADD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_ADD_IMAGE:
                if (resultCode == RESULT_OK) {
                    selectedImagePath = getPath(data.getData());
                    if (!TextUtils.isEmpty(selectedImagePath)) {
                        Log.i("anshul", "Image path " + selectedImagePath);
                        mCollageView.addImage(selectedImagePath);
                    } else {
                        Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getPath(Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();

    }

    private class GridAdapter extends BaseAdapter {

        @Override

        public int getCount() {
            return backgroundColor.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.grid_item, viewGroup, false);
                view = imageView;
            }
            ((ImageView) view).setBackgroundColor(backgroundColor[i]);

            return view;
        }
    }
}
