package com.incar.rkylog.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BubbleViscosity extends SurfaceView implements
        SurfaceHolder.Callback, Runnable {
    private static ScheduledExecutorService scheduledThreadPool;
    private Context context;
    private String paintColor = "#25DA29";
    private String centreColor = "#00000000";
    private String minCentreColor = "#9025DA29";
    private int screenHeight;
    private int screenWidth;

    private float lastRadius;
    private float rate = 0.32f;
    private float rate2 = 0.45f;
    private PointF lastCurveStrat = new PointF();
    private PointF lastCurveEnd = new PointF();
    private PointF centreCirclePoint = new PointF();
    private float centreRadius;
    private float bubbleRadius;

    private PointF[] arcPointStrat = new PointF[8];
    private PointF[] arcPointEnd = new PointF[8];
    private PointF[] control = new PointF[8];
    private PointF arcStrat = new PointF();
    private PointF arcEnd = new PointF();
    private PointF controlP = new PointF();

    List<PointF> bubbleList = new ArrayList<>();
    List<BubbleBean> bubbleBeans = new ArrayList<>();

    private int rotateAngle = 0;
    private float controlrate = 1.66f;
    private float controlrateS = 1.3f;
    private int i = 0;
    private SurfaceHolder mHolder;
    private float scale = 0;

    private Paint arcPaint;
    private Paint minCentrePaint;
    private Paint bubblePaint;
    private Paint centrePaint;
    private Paint lastPaint;
    private Path lastPath;
    private Random random;
    private Paint textPaint;
    private String text = "78 %";
    private Rect rect;

    public BubbleViscosity(Context context) {
        this(context, null);
    }

    public BubbleViscosity(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleViscosity(Context context, AttributeSet attrs,
                           int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initTool();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        screenHeight = getMeasuredHeight();
        screenWidth = getMeasuredWidth();
    }

    private void initTool() {
        rect = new Rect();
        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        mHolder.setFormat(PixelFormat.TRANSPARENT);
        setZOrderOnTop(true);
        lastRadius = dip2Dimension(40f, context);
        centreRadius = dip2Dimension(100f, context);
        bubbleRadius = dip2Dimension(15f, context);
        random = new Random();
        lastPaint = new Paint();
        lastPaint.setAntiAlias(true);
        lastPaint.setStyle(Paint.Style.FILL);
        lastPaint.setColor(Color.parseColor(paintColor));
        lastPaint.setStrokeWidth(2);

        lastPath = new Path();

        centrePaint = new Paint();
        centrePaint.setAntiAlias(true);
        centrePaint.setStyle(Paint.Style.FILL);
        centrePaint.setStrokeWidth(2);
        centrePaint
                .setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        centrePaint.setColor(Color.parseColor(centreColor));
        arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        arcPaint.setStyle(Paint.Style.FILL);
        arcPaint.setColor(Color.parseColor(paintColor));
        arcPaint.setStrokeWidth(2);
        minCentrePaint = new Paint();
        minCentrePaint.setAntiAlias(true);
        minCentrePaint.setStyle(Paint.Style.FILL);
        minCentrePaint.setColor(Color.parseColor(minCentreColor));
        minCentrePaint.setStrokeWidth(2);
        bubblePaint = new Paint();
        bubblePaint.setAntiAlias(true);
        bubblePaint.setStyle(Paint.Style.FILL);
        bubblePaint.setColor(Color.parseColor(minCentreColor));
        bubblePaint.setStrokeWidth(2);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.parseColor("#FFFFFF"));
        textPaint.setStrokeWidth(2);
        textPaint.setTextSize(dip2Dimension(40f, context));

    }

    private void onMDraw() {
        Canvas canvas = mHolder.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        //bubbleDraw(canvas);
        //lastCircleDraw(canvas);
        centreCircleDraw(canvas);
        textPaint.getTextBounds(text, 0, text.length(), rect);
        canvas.drawText(text, centreCirclePoint.x - rect.width() / 2,
                centreCirclePoint.y + rect.height() / 2, textPaint);
        mHolder.unlockCanvasAndPost(canvas);
    }

    public void setBatteryLevel(String level){
        this.text=level+"%";
        postInvalidate();
    }
    private void centreCircleDraw(Canvas canvas) {
        centreCirclePoint.set(screenWidth / 2, screenHeight / 2);
        circleInCoordinateDraw(canvas);
        canvas.drawCircle(centreCirclePoint.x, centreCirclePoint.y,
                centreRadius, centrePaint);

    }

    private void lastCircleDraw(Canvas canvas) {
        lastCurveStrat.set(screenWidth / 2 - lastRadius, screenHeight);
        lastCurveEnd.set((screenWidth / 2), screenHeight);

        float k = (lastRadius / 2) / lastRadius;

        float aX = lastRadius - lastRadius * rate2;
        float aY = lastCurveStrat.y - aX * k;
        float bX = lastRadius - lastRadius * rate;
        float bY = lastCurveEnd.y - bX * k;

        lastPath.rewind();
        lastPath.moveTo(lastCurveStrat.x, lastCurveStrat.y);
        lastPath.cubicTo(lastCurveStrat.x + aX, aY, lastCurveEnd.x - bX, bY,
                lastCurveEnd.x, lastCurveEnd.y - lastRadius / 2);
        lastPath.cubicTo(lastCurveEnd.x + bX, bY, lastCurveEnd.x + lastRadius
                - aX, aY, lastCurveEnd.x + lastRadius, lastCurveEnd.y);

        lastPath.lineTo(lastCurveStrat.x, lastCurveStrat.y);
        canvas.drawPath(lastPath, lastPaint);

    }

    private int bubbleIndex = 0;

    private void bubbleDraw(Canvas canvas) {

        for (int i = 0; i < bubbleBeans.size(); i++) {
            if (bubbleBeans.get(i).getY() <= (int) (screenHeight / 2 + centreRadius)) {
                bubblePaint.setAlpha(000);
                canvas.drawCircle(bubbleBeans.get(i).getX(), bubbleBeans.get(i)
                        .getY(), bubbleRadius, bubblePaint);
            } else {
                bubblePaint.setAlpha(150);
                canvas.drawCircle(bubbleBeans.get(i).getX(), bubbleBeans.get(i)
                        .getY(), bubbleRadius, bubblePaint);
            }
        }

    }

    /**
     * @param dip
     * @param context
     * @return
     */
    public float dip2Dimension(float dip, Context context) {
        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
                displayMetrics);
    }

    /**
     * @param canvas
     */
    public void circleInCoordinateDraw(Canvas canvas) {
        int angle;
        for (int i = 0; i < arcPointStrat.length; i++) {
            if (i > 3 && i < 6) {
                if (i == 4) {
                    angle = rotateAngle + i * 60;

                } else {
                    angle = rotateAngle + i * 64;
                }
            } else if (i > 5) {
                if (i == 6) {
                    angle = rotateAngle + i * 25;
                } else {
                    angle = rotateAngle + i * 48;
                }

            } else {
                angle = rotateAngle + i * 90;
            }

            float radian = (float) Math.toRadians(angle);
            float adjacent = (float) Math.cos(radian) * centreRadius;
            float right = (float) Math.sin(radian) * centreRadius;
            float radianControl = (float) Math.toRadians(90 - (45 + angle));
            float xStrat = (float) Math.cos(radianControl) * centreRadius;
            float yEnd = (float) Math.sin(radianControl) * centreRadius;
            if (i == 0 || i == 1) {
                if (i == 1) {
                    arcStrat.set(centreCirclePoint.x + adjacent - scale,
                            centreCirclePoint.y + right + scale);
                    arcEnd.set(centreCirclePoint.x - right, centreCirclePoint.y
                            + adjacent);

                } else {
                    arcStrat.set(centreCirclePoint.x + adjacent,
                            centreCirclePoint.y + right);
                    arcEnd.set(centreCirclePoint.x - right - scale,
                            centreCirclePoint.y + adjacent + scale);

                }
                controlP.set(centreCirclePoint.x + yEnd * controlrate,
                        centreCirclePoint.y + xStrat * controlrate);
            } else {
                arcStrat.set(centreCirclePoint.x + adjacent,
                        centreCirclePoint.y + right);
                arcEnd.set(centreCirclePoint.x - right, centreCirclePoint.y
                        + adjacent);
                if (i > 5) {
                    controlP.set(centreCirclePoint.x + yEnd * controlrateS,
                            centreCirclePoint.y + xStrat * controlrateS);
                } else {
                    controlP.set(centreCirclePoint.x + yEnd * controlrate,
                            centreCirclePoint.y + xStrat * controlrate);
                }
            }
            arcPointStrat[i] = arcStrat;
            arcPointEnd[i] = arcEnd;
            control[i] = controlP;

            lastPath.rewind();
            lastPath.moveTo(arcPointStrat[i].x, arcPointStrat[i].y);
            lastPath.quadTo(control[i].x, control[i].y, arcPointEnd[i].x,
                    arcPointEnd[i].y);

            if (i > 3 && i < 6) {
                canvas.drawPath(lastPath, minCentrePaint);
            } else {
                canvas.drawPath(lastPath, arcPaint);
            }
            lastPath.rewind();
        }
    }

    private void setAnimation() {
        setScheduleWithFixedDelay(this, 0, 5);
        setScheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (bubbleIndex > 2)
                    bubbleIndex = 0;
                if (bubbleBeans.size() < 8) {
                    bubbleBeans.add(new BubbleBean(
                            bubbleList.get(bubbleIndex).x, bubbleList
                            .get(bubbleIndex).y, random.nextInt(4) + 2,
                            bubbleIndex));
                } else {
                    for (int i = 0; i < bubbleBeans.size(); i++) {
                        if (bubbleBeans.get(i).getY() <= (int) (screenHeight / 2 + centreRadius)) {
                            bubbleBeans.get(i).set(
                                    bubbleList.get(bubbleIndex).x,
                                    bubbleList.get(bubbleIndex).y,
                                    random.nextInt(4) + 2, bubbleIndex);
                            if (random.nextInt(bubbleBeans.size()) + 3 == 3 ? true
                                    : false) {
                            } else {
                                break;
                            }
                        }
                    }
                }
                bubbleIndex++;
            }
        }, 0, 300);
    }

    private static ScheduledExecutorService getInstence() {
        if (scheduledThreadPool == null) {
            synchronized (BubbleViscosity.class) {
                if (scheduledThreadPool == null) {
                    scheduledThreadPool = Executors
                            .newSingleThreadScheduledExecutor();
                }
            }
        }
        return scheduledThreadPool;
    }

    private static void setScheduleWithFixedDelay(Runnable var1, long var2,
                                                  long var4) {
        getInstence().scheduleWithFixedDelay(var1, var2, var4,
                TimeUnit.MILLISECONDS);

    }

    public static void onDestroyThread() {
        getInstence().shutdownNow();
        if (scheduledThreadPool != null) {
            scheduledThreadPool = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        bubbleList.clear();
        setBubbleList();
        startBubbleRunnable();
        setAnimation();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        onDestroyThread();
    }

    @Override
    public void run() {
        i++;
        rotateAngle = i;
        if (i > 90 && i < 180) {
            scale += 0.25;
            if (controlrateS < 1.66)
                controlrateS += 0.005;
        } else if (i >= 180) {
            scale -= 0.12;
            if (i > 300)
                controlrateS -= 0.01;
        }
        onMDraw();
        if (i == 360) {
            i = 0;
            rotateAngle = 0;
            controlrate = 1.66f;
            controlrateS = 1.3f;
            scale = 0;
        }

    }

    public void setBubbleList() {
        float radian = (float) Math.toRadians(35);
        float adjacent = (float) Math.cos(radian) * lastRadius / 3;
        float right = (float) Math.sin(radian) * lastRadius / 3;
        if (!bubbleList.isEmpty())
            return;
        bubbleList.add(new PointF(screenWidth / 2 - adjacent, screenHeight
                - right));
        bubbleList.add(new PointF(screenWidth / 2, screenHeight - lastRadius
                / 4));
        bubbleList.add(new PointF(screenWidth / 2 + adjacent, screenHeight
                - right));
        startBubbleRunnable();
    }

    public void startBubbleRunnable(){
        setScheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < bubbleBeans.size(); i++) {
                    bubbleBeans.get(i).setMove(screenHeight,
                            (int) (screenHeight / 2 + centreRadius));
                }
            }
        }, 0, 4);
    }
}
