package com.incar.rkylog.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.incar.rkylog.R;

public class WiredChargingAnimation {

    public static final long DURATION = 3333;
    private static final String TAG = "WiredChargingAnimation";
    private static final boolean DEBUG = true || Log.isLoggable(TAG, Log.DEBUG);

    private final WiredChargingView mCurrentWirelessChargingView;
    private static WiredChargingView mPreviousWirelessChargingView;
    private static boolean mShowingWiredChargingAnimation;

    public static boolean isShowingWiredChargingAnimation(){
        return mShowingWiredChargingAnimation;
    }

    public WiredChargingAnimation(@NonNull Context context, @Nullable Looper looper, int
            batteryLevel, boolean isDozing) {
        mCurrentWirelessChargingView = new WiredChargingView(context, looper,
                batteryLevel, isDozing);
    }

    public static WiredChargingAnimation makeWiredChargingAnimation(@NonNull Context context,
                                                                    @Nullable Looper looper, int batteryLevel, boolean isDozing) {
        mShowingWiredChargingAnimation = true;
        android.util.Log.d(TAG,"makeWiredChargingAnimation batteryLevel="+batteryLevel);
        return new WiredChargingAnimation(context, looper, batteryLevel, isDozing);
    }

    /**
     * Show the view for the specified duration.
     */
    public void show() {
        if (mCurrentWirelessChargingView == null ||
                mCurrentWirelessChargingView.mNextView == null) {
            throw new RuntimeException("setView must have been called");
        }

        /*if (mPreviousWirelessChargingView != null) {
            mPreviousWirelessChargingView.hide(0);
        }*/

        mPreviousWirelessChargingView = mCurrentWirelessChargingView;
        mCurrentWirelessChargingView.show();
        mCurrentWirelessChargingView.hide(DURATION);
    }

    private static class WiredChargingView {
        private static final int SHOW = 0;
        private static final int HIDE = 1;

        private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        private final Handler mHandler;

        private int mGravity;
        private View mView;
        private View mNextView;
        private WindowManager mWM;

        public WiredChargingView(Context context, @Nullable Looper looper, int batteryLevel, boolean isDozing) {
            //mNextView = new WirelessChargingLayout(context, batteryLevel, isDozing);
            mNextView = LayoutInflater.from(context).inflate(R.layout.wired_charging_layout, null, false);
            BubbleViscosity shcyBubbleViscosity = mNextView.findViewById(R.id.shcy_bubble_view);
            shcyBubbleViscosity.setBatteryLevel(batteryLevel+"");

            mGravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER;

            final WindowManager.LayoutParams params = mParams;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.format = PixelFormat.TRANSLUCENT;

            params.type = WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG;
            params.setTitle("Charging Animation");
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_DIM_BEHIND;

            params.dimAmount = .3f;

            if (looper == null) {
                // Use Looper.myLooper() if looper is not specified.
                looper = Looper.myLooper();
                if (looper == null) {
                    throw new RuntimeException(
                            "Can't display wireless animation on a thread that has not called "
                                    + "Looper.prepare()");
                }
            }

            mHandler = new Handler(looper, null) {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case SHOW: {
                            handleShow();
                            break;
                        }
                        case HIDE: {
                            handleHide();
                            // Don't do this in handleHide() because it is also invoked by
                            // handleShow()
                            mNextView = null;
                            mShowingWiredChargingAnimation = false;
                            break;
                        }
                    }
                }
            };
        }

        public void show() {
            if (DEBUG) Log.d(TAG, "SHOW: " + this);
            mHandler.obtainMessage(SHOW).sendToTarget();
        }

        public void hide(long duration) {
            mHandler.removeMessages(HIDE);

            if (DEBUG) Log.d(TAG, "HIDE: " + this);
            mHandler.sendMessageDelayed(Message.obtain(mHandler, HIDE), duration);
        }

        private void handleShow() {
            if (DEBUG) {
                Log.d(TAG, "HANDLE SHOW: " + this + " mView=" + mView + " mNextView="
                        + mNextView);
            }

            if (mView != mNextView) {
                // remove the old view if necessary
                handleHide();
                mView = mNextView;
                Context context = mView.getContext().getApplicationContext();
                String packageName = mView.getContext().getOpPackageName();
                if (context == null) {
                    context = mView.getContext();
                }
                mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                mParams.packageName = packageName;
                //mParams.hideTimeoutMilliseconds = DURATION;

                if (mView.getParent() != null) {
                    if (DEBUG) Log.d(TAG, "REMOVE! " + mView + " in " + this);
                    mWM.removeView(mView);
                }
                if (DEBUG) Log.d(TAG, "ADD! " + mView + " in " + this);

                try {
                    mWM.addView(mView, mParams);
                } catch (WindowManager.BadTokenException e) {
                    Log.d(TAG, "Unable to add wireless charging view. " + e);
                }
            }
        }

        private void handleHide() {
            if (DEBUG) Log.d(TAG, "HANDLE HIDE: " + this + " mView=" + mView);
            if (mView != null) {
                if (mView.getParent() != null) {
                    if (DEBUG) Log.d(TAG, "REMOVE! " + mView + " in " + this);
                    mWM.removeViewImmediate(mView);
                }

                mView = null;
            }
        }
    }
}
