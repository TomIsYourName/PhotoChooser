package com.litijun.photochooser;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
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
import com.litijun.photochooser.fragment.PreviewFragment;
import com.litijun.photochooser.fragment.SelectAlbumFragment;
import com.litijun.photochooser.manager.ImageLoaderMgr;
import com.litijun.photochooser.utils.DebugLog;
import com.litijun.photochooser.utils.LoadeImageConsts;
import com.litijun.photochooser.utils.Utils;

public class ChooseImageActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final String[]	LOADING_COLUMN	= { MediaStore.Images.ImageColumns._ID,//
			MediaStore.Images.Media.DATA, //
			MediaStore.Images.ImageColumns.DISPLAY_NAME,//
			MediaStore.Images.Media.BUCKET_ID,		};
	private Integer					albumId;

	private GridView				gridView;
	private PictureAdapter			adapter;
	private SelectAlbumFragment		albumFragment;
	private Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_image);
		ImageLoaderMgr.getInstance(getApplication()).setMaxSelectSize(6);
		ImageLoaderMgr.getInstance(getApplication()).setTakePhoto(true);
		gridView = (GridView) findViewById(R.id.choose_image_gridview);
		adapter = new PictureAdapter(this);
		albumFragment = (SelectAlbumFragment) getSupportFragmentManager().findFragmentById(R.id.choose_image_album);
		refreshGridViewByAlbumId(LoadeImageConsts.LOADER_IMAGE_CURSOR);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0 && ImageLoaderMgr.getInstance(getApplication()).isTakePhoto()) {
					Toast.makeText(ChooseImageActivity.this, "拍照", Toast.LENGTH_LONG).show();
				}
				else {
					ImageItem item = adapter.getItem(position);
					Fragment fragment = new PreviewFragment();
					Bundle args = new Bundle();
					args.putInt("offset", ImageLoaderMgr.getInstance(getApplication()).isTakePhoto() ? position - 1 : position);
					args.putBoolean("show_all", true);
					args.putSerializable("ImageItem", item);
					fragment.setArguments(args);
					getSupportFragmentManager().beginTransaction().replace(R.id.preview, fragment).commit();
				}
			}
		});

		initHeader();

	}

	public void refreshGridViewByAlbumId(int id) {
		DebugLog.d("album id = " + id);
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
		DebugLog.d("album id = " + id);
		String selection = null;
		String[] selectionArgs = null;
		if (albumId != null && albumId != 1) {
			selection = "bucket_id=?";
			selectionArgs = new String[] { "" + id };
		}
		String orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC";//MediaStore.Images.Media._ID + " ASC";
		return new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, LOADING_COLUMN, selection, selectionArgs, orderBy);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// 第一次默认取全部图片，所以相册第一项为所有图片
		if (loader.getId() == LoadeImageConsts.LOADER_IMAGE_CURSOR) {
			AlbumItem item = new AlbumItem();
			cursor.moveToPosition(0);
			item.firstImageId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
			item.firstImagePath = Utils.getImagePath(getApplication(), item.firstImageId);
			item.id = loader.getId();
			item.imageCount = cursor.getCount();
			item.albumName = getString(R.string.all_photos);
			albumFragment.setFirstItem(item);
			adapter.setLoadCursor(cursor);
		}
		else {
			adapter.setLoadCursor(cursor);
		}
		this.cursor = cursor;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (this.cursor != null && !this.cursor.isClosed()) {
			this.cursor.close();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	public void showPreview() {
		getSupportFragmentManager().beginTransaction().replace(R.id.preview, new PreviewFragment()).commit();
	}

}
