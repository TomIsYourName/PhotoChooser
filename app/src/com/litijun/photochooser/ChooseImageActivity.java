package com.litijun.photochooser;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
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

public class ChooseImageActivity extends FragmentActivity implements View.OnClickListener,LoaderManager.LoaderCallbacks<Cursor> {
	public static final String[]	LOADING_COLUMN	= { MediaStore.Images.ImageColumns._ID,//
			MediaStore.Images.Media.DATA, //
			MediaStore.Images.ImageColumns.DISPLAY_NAME,//
			MediaStore.Images.Media.BUCKET_ID,		};
	private Integer					albumId;

	private Button header_back;
	private TextView header_title;
	private Button header_right_button;

	private GridView				gridView;
	private PictureAdapter			adapter;
	private SelectAlbumFragment		albumFragment;
	private PreviewFragment			previewFragment;
	private Cursor					cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_image);
		ImageLoaderMgr.getInstance(getApplication()).setMaxSelectSize(6);
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
				} else {
					ImageItem item = adapter.getItem(position);
					previewFragment = new PreviewFragment();
					Bundle args = new Bundle();
					args.putInt("offset", ImageLoaderMgr.getInstance(getApplication()).isTakePhoto() ? position - 1 : position);
					args.putBoolean("show_all", true);
					args.putSerializable("ImageItem", item);
					previewFragment.setArguments(args);

					showPreviewFragment(previewFragment);
				}
			}
		});

		initHeader();

	}

	public void refreshGridViewByAlbumId(int id) {
		DebugLog.d("album id = " + id);
		if (id == LoadeImageConsts.LOADER_IMAGE_CURSOR) {
			ImageLoaderMgr.getInstance(getApplication()).setTakePhoto(ImageLoaderMgr.getInstance(getApplication()).isTakePhoto());
		}else{
			ImageLoaderMgr.getInstance(getApplication()).setTakePhoto(false);
		}
		this.albumId = id;
		getSupportLoaderManager().initLoader(id, null, this);
	}

	private void initHeader() {
		header_back = (Button) findViewById(R.id.header_back);
		header_back.setOnClickListener(this);

		header_title = (TextView) findViewById(R.id.header_title);
		header_title.setText("选择图片");

		header_right_button = (Button) findViewById(R.id.header_right_button);
		header_right_button.setOnClickListener(this);

		changeSelectedCount();
	}

	public void changeSelectedCount(){

		ImageLoaderMgr imageLoaderMgr = ImageLoaderMgr.getInstance(getApplication());
		int selectedCount = imageLoaderMgr.getSelectCount();
		if(selectedCount>0)
			header_right_button.setText(getString(R.string.select_done, selectedCount, imageLoaderMgr.getMaxSelectSize()));
		else
			header_right_button.setText("完成");
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
		if(previewFragment != null){
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); 
			ft.setCustomAnimations(R.anim.slide_up,R.anim.slide_down); 
			ft.remove(previewFragment).commit();
			previewFragment = null;
			return;
		}
		super.onBackPressed();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.header_back:
			case R.id.header_right_button:
				onBackPressed();
				break;
		}
	}

	private void showPreviewFragment(PreviewFragment previewFragment){
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
		ft.replace(R.id.preview, previewFragment).commit();
	}

	public void showPreview() {
		if(ImageLoaderMgr.getInstance(getApplication()).getSeletectList().size()<=0){
			Toast.makeText(ChooseImageActivity.this, getString(R.string.have_no_chosen), Toast.LENGTH_LONG).show();
		}else{
			previewFragment = new PreviewFragment();
			showPreviewFragment(previewFragment);
		}
	}

}
