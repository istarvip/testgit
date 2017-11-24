package com.walnutin.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.umeng.analytics.MobclickAgent;
import com.walnutin.activity.PhoneSettingActivity;
import com.walnutin.activity.WatchLinkActivity;
import com.walnutin.eventbus.CommonAvater;
import com.walnutin.eventbus.CommonUserAvaterResult;
import com.walnutin.hard.R;
import com.walnutin.http.HttpImpl;
import com.walnutin.Presenter.MyPresenter;
import com.walnutin.ViewInf.MyView;
import com.walnutin.util.BitmapUtil;
import com.walnutin.util.Conversion;
import com.walnutin.util.DensityUtils;
import com.walnutin.util.MySharedPf;
import com.walnutin.view.CircleImageView;
import com.walnutin.view.HeadSelectPopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

//import com.handmark.pulltorefresh.library.PullToRefreshGridView;

/**
 * Created by Administrator on 2016/5/6.
 */
public class MyFragment extends BaseFragment implements View.OnClickListener, MyView {
    //PullToRefreshGridView x;
    private View mView;
    private ImageView mBtn_phone_setting;
    private ImageView mBtn_watch_setting;
    private TextView tv_height;
    private TextView tv_weight;
    private TextView tv_goal;
    private TextView tv_bmi;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    AQuery aQuery;
    MySharedPf sharedPf;
    CircleImageView avatar;
    private File tempFile;
    // ImageLoader
    MyPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_me, container, false);
        sharedPf = MySharedPf.getInstance(getActivity());
        mBtn_phone_setting = (ImageView) mView.findViewById(R.id.btn_phone_setiing);
        mBtn_watch_setting = (ImageView) mView.findViewById(R.id.btn_watch_setting);
        tv_height = (TextView) mView.findViewById(R.id.tv_height);
        tv_weight = (TextView) mView.findViewById(R.id.tv_weight);
        tv_goal = (TextView) mView.findViewById(R.id.tv_goal);
        tv_bmi = (TextView) mView.findViewById(R.id.tv_bmi);
        avatar = (CircleImageView) mView.findViewById(R.id.head);
        initView();
        mBtn_phone_setting.setOnClickListener(this);
        mBtn_watch_setting.setOnClickListener(this);
        mBtn_watch_setting.setOnClickListener(this);
        avatar.setOnClickListener(this);
        EventBus.getDefault().register(this);
        aQuery = new AQuery(getActivity(), mView);

        return mView;
    }

    public void initView() {
        presenter = new MyPresenter(this);
        tv_height.setText(presenter.getHeight() + "cm");
        tv_weight.setText(presenter.getWeight() + "kg");
        tv_goal.setText(String.valueOf(presenter.getGoal()));
        tv_bmi.setText(presenter.getBMI());
    //    if (presenter.getHeadImage() != null)
            BitmapUtil.loadBitmap(getActivity(), presenter.getHeadImage(), R.drawable.head_image, R.drawable.head_image, avatar);

    }

    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_phone_setiing:
                Intent intent1 = new Intent(MyFragment.this.getActivity(), PhoneSettingActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_watch_setting:
                Intent WatchIntent = new Intent(MyFragment.this.getActivity(), WatchLinkActivity.class);
                startActivity(WatchIntent);
                //TODO:手表设置,现在先测试连接手表手环
                break;
            case R.id.head:
                //    Toast.makeText(getActivity(), sharedPf.getString("avatar"), Toast.LENGTH_SHORT).show();
                headSelectPopupWindow();
                break;
        }

    }

    private HeadSelectPopupWindow headPopWindow;

    private void headSelectPopupWindow() {
        headPopWindow = new HeadSelectPopupWindow(getActivity(), itemsOnClick);
        headPopWindow.showAtLocation(mView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("myFragment");
    }


    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.takephoto:
                    tempFile = new File(Environment.getExternalStorageDirectory(),
                            getPhotoFileName());
                    Intent cameraintent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    // 指定调用相机拍照后照片的储存路径
                    cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(tempFile));
                    startActivityForResult(cameraintent,
                            PHOTO_REQUEST_TAKEPHOTO);
                    break;
                case R.id.selectfromAlbum:
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", DensityUtils.dip2px(getContext(), 60));
                    intent.putExtra("outputY", DensityUtils.dip2px(getContext(), 60));
                    intent.putExtra("return-data", true);
                    startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("myFragment");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        headPopWindow.dismiss();
        if (data != null && requestCode == PHOTO_REQUEST_GALLERY) {
            showHead(data.getExtras().get("data"));
            //   BitmapUtil.loadBitmap(getContext(), (String) data.getExtras().get("data"),avatar);
        }
        if (requestCode == PHOTO_REQUEST_TAKEPHOTO) {
            startPhotoZoom(Uri.fromFile(tempFile));
        }
        if (requestCode == PHOTO_REQUEST_CUT) {
            if (data != null) {
                showHead(data.getExtras().get("data"));
                //    BitmapUtil.loadBitmap(getContext(), (String) data.getExtras().get("data"),avatar);
            }
        }

    }

    private void showHead(Object data) {
        Bitmap cameraBitmap = (Bitmap) data;
        avatar.setImageBitmap(cameraBitmap);
        String userhead = Conversion.convertIconToString(cameraBitmap);
        HttpImpl.getInstance().userHead(userhead);
    }

    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", DensityUtils.dip2px(getContext(), 60));
        intent.putExtra("outputY", DensityUtils.dip2px(getContext(), 60));
        intent.putExtra("return-data", true);
        //   intent.putExtra("noFaceDetection", true);
        // System.out.println("22================");
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    @Subscribe
    public void onHeadResult(CommonUserAvaterResult result) {
        {
            int state = result.getState();
            String msg = result.getMsg();
            //  Log.d(TAG, "onUpdataResult: 状态state:" + state);
            switch (state) {
                case 0:
                    //提交成功 跳转至首页
                    //   Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    HttpImpl.getInstance().getUserAvater();
                    break;
                default:
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }


    @Subscribe
    public void onGetAvaterResult(CommonAvater result) {
        int state = result.getState();
        String msg = result.getMsg();
        switch (state) {
            case 0:
                //      Toast.makeText(getContext(), "设置成功", Toast.LENGTH_SHORT).show();
                sharedPf.setString("headimage", result.getImage());
                BitmapUtil.loadBitmap(getActivity(), result.getImage(), avatar);
                break;
            case -1:
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void setHeadImage() {

    }


}

