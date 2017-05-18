package com.sample.boot.module.signin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.okandroid.boot.AppContext;
import com.okandroid.boot.app.ext.preload.PreloadViewProxy;
import com.okandroid.boot.lang.Log;
import com.okandroid.boot.util.GrantResultUtil;
import com.okandroid.boot.util.IOUtil;
import com.okandroid.boot.util.SystemUtil;
import com.okandroid.boot.util.ViewUtil;
import com.okandroid.boot.widget.ptr.PtrLayout;
import com.sample.boot.R;
import com.sample.boot.app.BaseFragment;
import com.sample.boot.data.CircleTextView;
import com.sample.boot.module.datalist.DataListActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by idonans on 2017/2/3.
 */

public class SignInFragment extends BaseFragment implements SignInView {

    private static final String TAG = "SignInFragment";

    public static SignInFragment newInstance() {
        Bundle args = new Bundle();
        SignInFragment fragment = new SignInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public SignInViewProxy getDefaultViewProxy() {
        return (SignInViewProxy) super.getDefaultViewProxy();
    }

    @Override
    protected PreloadViewProxy newDefaultViewProxy() {
        return new SignInViewProxy(this);
    }

    private Content mContent;

    @Override
    protected void showPreloadContentView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup contentView) {
        IOUtil.closeQuietly(mContent);
        mContent = new Content(activity, inflater, contentView);
    }

    private AlertDialog mDownloadAndInstallApkDialog;

    @Override
    public void hideDownloadAndInstallApkDialog() {
        if (mDownloadAndInstallApkDialog != null) {
            mDownloadAndInstallApkDialog.dismiss();
            mDownloadAndInstallApkDialog = null;
        }
    }

    @Override
    public void showDownloadAndInstallApkDialog(SignInViewProxy.ApkInstallInfo apkInstallInfo) {
        if (mDownloadAndInstallApkDialog != null) {
            mDownloadAndInstallApkDialog.dismiss();
            mDownloadAndInstallApkDialog = null;
        }

        Context context = SystemUtil.getActivityFromFragment(this);
        mDownloadAndInstallApkDialog = new AlertDialog.Builder(context)
                .setTitle("apk 下载")
                .setMessage("url: " + apkInstallInfo.apkUrl)
                .show();
    }

    @Override
    public void updateDownloadAndInstallApkDialog(SignInViewProxy.ApkInstallInfo apkInstallInfo) {
        if (mDownloadAndInstallApkDialog == null) {
            return;
        }

        mDownloadAndInstallApkDialog.setMessage("url: " + apkInstallInfo.apkUrl + "\n" + " progress: " + apkInstallInfo.downloadProgress + "%");
    }

    @Override
    public void showDownloadAndInstallApkFail() {
        Toast.makeText(AppContext.getContext(), "apk 下载失败", Toast.LENGTH_SHORT).show();
    }

    private class Content extends PreloadSubViewHelper {

        private final PtrLayout mPtrLayout;

        private final View mPrefetchImage;
        private final View mTestLoading;
        private final View mTestOpenUrl;
        private final View mTestTakePhoto;
        private final View mTestDataList;
        private final View mTestInstallApk;
        private final CircleTextView mCircleTextView;

        private Content(Activity activity, LayoutInflater inflater, ViewGroup contentView) {
            super(activity, inflater, contentView, R.layout.sample_sign_in_view);

            mPtrLayout = ViewUtil.findViewByID(mRootView, R.id.ptr_layout);
            mPtrLayout.setOnRefreshListener(new PtrLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mRootView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPtrLayout.setRefreshing(false);
                        }
                    }, 2000L);
                }
            });

            mPrefetchImage = ViewUtil.findViewByID(mRootView, R.id.prefetch_image);
            mPrefetchImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SignInViewProxy viewProxy = getDefaultViewProxy();
                    if (viewProxy == null) {
                        return;
                    }
                    viewProxy.prefetchImage();
                }
            });

            mTestLoading = ViewUtil.findViewByID(mRootView, R.id.test_loading);
            mTestLoading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SignInViewProxy viewProxy = getDefaultViewProxy();
                    if (viewProxy == null) {
                        return;
                    }

                    invokeAutoRefresh();

                    viewProxy.testLoading();
                }
            });

            mTestOpenUrl = ViewUtil.findViewByID(mRootView, R.id.test_open_url);
            mTestOpenUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SignInViewProxy viewProxy = getDefaultViewProxy();
                    if (viewProxy == null) {
                        return;
                    }

                    String url = "https://www.baidu.com";
                    url = "market://details?id=com.zcool.community";
                    SystemUtil.openView(url);
                }
            });

            mTestTakePhoto = ViewUtil.findViewByID(mRootView, R.id.test_take_photo);
            mTestTakePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPermissionAndContinueTakePhoto();
                }
            });

            mTestDataList = ViewUtil.findViewByID(mRootView, R.id.test_data_list);
            mTestDataList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SignInViewProxy viewProxy = getDefaultViewProxy();
                    if (viewProxy == null) {
                        return;
                    }

                    Intent intent = new Intent(mActivity, DataListActivity.class);
                    startActivity(intent);
                }
            });

            mTestInstallApk = ViewUtil.findViewByID(mRootView, R.id.test_install_apk);
            mTestInstallApk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkDownloadAndInstallApkPermissions();
                }
            });

            mCircleTextView = ViewUtil.findViewByID(mRootView, R.id.circle_text);
            mCircleTextView.setText("left top text 换\n行");
            mCircleTextView.setGravity(Gravity.LEFT | Gravity.TOP);
            mCircleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int flag = (int) ((Math.random() * 20) % 9);
                    switch (flag) {
                        case 0:
                            mCircleTextView.setText("A left top text A");
                            mCircleTextView.setGravity(Gravity.LEFT | Gravity.TOP);
                            break;
                        case 1:
                            mCircleTextView.setText("A center_h top text A");
                            mCircleTextView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
                            break;
                        case 2:
                            mCircleTextView.setText("A right top text A");
                            mCircleTextView.setGravity(Gravity.RIGHT | Gravity.TOP);
                            break;
                        case 3:
                            mCircleTextView.setText("世界 left center_v text A");
                            mCircleTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                            break;
                        case 4:
                            mCircleTextView.setText("9 center text M");
                            mCircleTextView.setGravity(Gravity.CENTER);
                            break;
                        case 5:
                            mCircleTextView.setText("3.0 center_v right text 中文");
                            mCircleTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                            break;
                        case 6:
                            mCircleTextView.setText("B left bottom text C");
                            mCircleTextView.setGravity(Gravity.LEFT | Gravity.BOTTOM);
                            break;
                        case 7:
                            mCircleTextView.setText("D center_h bottom text 哈哈");
                            mCircleTextView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
                            break;
                        case 8:
                            mCircleTextView.setText("预定 right bottom 贝多芬 text");
                            mCircleTextView.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                            break;
                        default:
                            mCircleTextView.setText("default center text");
                            mCircleTextView.setGravity(Gravity.CENTER);
                            break;
                    }
                }
            });

            invokeAutoRefresh();

            printSystemUserAgent();
        }

        private void invokeAutoRefresh() {
            mPtrLayout.setRefreshing(true);
            mRootView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPtrLayout.setRefreshing(false);
                }
            }, 2000L);
        }

    }

    private void printSystemUserAgent() {
        Log.d("system user agent:", SystemUtil.getSystemUserAgent());
        Log.d("webview user agent:", SystemUtil.getSystemWebViewUserAgent());
    }

    private void downloadAndInstallApk() {
        SignInViewProxy viewProxy = getDefaultViewProxy();
        if (viewProxy == null) {
            return;
        }

        final String apkUrl = "http://static.zcool.com.cn/zcool/client/app/1000/community/1.8.3/com.zcool.community-338-1.8.3-zcool-release.apk";
        viewProxy.startDownloadAndInstallApk(apkUrl);
    }

    private static final int REQUEST_PERMISSION_CODE_TAKE_PHOTO = 1;
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    private static final int REQUEST_PERMISSION_CODE_DOWNLOAD_APK = 2;

    private void checkDownloadAndInstallApkPermissions() {
        SignInViewProxy viewProxy = getDefaultViewProxy();
        if (viewProxy == null) {
            return;
        }

        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.INTERNET);
        permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);

        if (Build.VERSION.SDK_INT >= 23) {
            permissions.add(Manifest.permission.REQUEST_INSTALL_PACKAGES);
        }

        requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_PERMISSION_CODE_DOWNLOAD_APK);
    }

    private final File[] mTakePhotoFiles = new File[1];

    private void checkPermissionAndContinueTakePhoto() {
        SignInViewProxy viewProxy = getDefaultViewProxy();
        if (viewProxy == null) {
            return;
        }

        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE_TAKE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE_TAKE_PHOTO) {
            if (GrantResultUtil.isAllGranted(grantResults)) {
                SystemUtil.takePhoto(this, REQUEST_CODE_TAKE_PHOTO, mTakePhotoFiles);
            } else {
                Toast.makeText(getActivity(), "权限被拒绝", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_PERMISSION_CODE_DOWNLOAD_APK) {
            if (GrantResultUtil.isAllGranted(grantResults)) {
                downloadAndInstallApk();
            } else {
                Toast.makeText(getActivity(), "权限被拒绝", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
            Log.d(TAG + " requestCode: " + requestCode + ", resultCode: " + resultCode + ", file: " + mTakePhotoFiles[0]);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
