package com.litijun.photochooser;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.litijun.photochooser.adapter.PictureAdapter;
import com.litijun.photochooser.adapter.vo.AlbumItem;
import com.litijun.photochooser.adapter.vo.ImageItem;
import com.litijun.photochooser.consts.LoadeImageConsts;
import com.litijun.photochooser.fragment.SelectAlbumFragment;
import com.litijun.photochooser.manager.ImageLoaderMgr;
//import com.litijun.photochooser.R;


public class ChooseImageActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String[] LOADING_COLUMN = {MediaStore.Images.ImageColumns._ID, // ID “559497”
            MediaStore.Images.Media.DATA,// “/storage/emulated/0/DCIM/Camera/IMG_20141206_203606.jpg”
            MediaStore.Images.ImageColumns.DISPLAY_NAME,// 图片名称 “IMG_20141206_203606.jpg”
            MediaStore.Images.Media.BUCKET_ID, // dir id 目录
    };
    private Integer albumId;

    private GridView gridView;
    private PictureAdapter adapter;
    private SelectAlbumFragment albumFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_image);
        ImageLoaderMgr.getInstance(this).setMaxSelectSize(6);

        gridView = (GridView) findViewById(R.id.choose_image_gridview);
        adapter = new PictureAdapter(this);
        albumFragment = (SelectAlbumFragment) getSupportFragmentManager().findFragmentById(R.id.choose_image_album);
        refreshGridViewByAlbumId(LoadeImageConsts.LOADER_IMAGE_CURSOR);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Toast.makeText(ChooseImageActivity.this, "拍照", Toast.LENGTH_LONG).show();
                } else {
                    ImageItem item = adapter.getItem(position - 1);
                    Toast.makeText(ChooseImageActivity.this, item.name, Toast.LENGTH_LONG).show();
                }
            }
        });

        initHeader();

    }

    public void refreshGridViewByAlbumId(int id) {
        this.albumId = id;
        getSupportLoaderManager().initLoader(id, null, this);
    }

    private void initHeader() {
        Button backButton = (Button) findViewById(R.id.header_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView titleView = (TextView) findViewById(R.id.header_title);
        titleView.setText("选择图片");

        Button confirmButton = (Button) findViewById(R.id.header_right_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo confirm
            }
        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = null;
        String[] selectionArgs = null;
        if (albumId != null && albumId < 0) {
            selection = "bucket_id=?";
            selectionArgs = new String[]{"" + id};
        }
        String orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC";//MediaStore.Images.Media._ID + " ASC";
        return new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, LOADING_COLUMN, selection, selectionArgs, orderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // 第一次默认取全部图片，所以相册第一项为所有图片
        if (loader.getId() == LoadeImageConsts.LOADER_IMAGE_CURSOR) {
            AlbumItem item = new AlbumItem();
            item.id = loader.getId();
            item.imageCount = cursor.getCount();
            item.albumName = "所有图片";
            cursor.moveToFirst();
            item.firstImageId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
            albumFragment.setFirstItem(item);
            adapter.setLoadCursor(cursor);
        } else {
            adapter.setLoadCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (adapter.getLoadCursor() != null) {
            adapter.getLoadCursor().close();
            adapter.setLoadCursor(null);
        }
    }
}
