package com.mp.common.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.ImageView;

import com.mp.BuildConfig;
import com.mp.R;

public class BlindsImageView extends ImageView {

	private static final boolean LOG_ON = BuildConfig.DEBUG;
	private static final String LOG_TAG = BlindsImageView.class.getSimpleName();
	
	private static final float CONFIG_MAX_ROTATION = 45f;
	
	private static float mMaxAffectRadius;
	
	private Bitmap mUndistortedBitmap;
	private Canvas mUndistortedCanvas;
	private BitmapDrawable mBgDrawable;
	private Paint mBlindPaint;
	private Camera mCamera = new Camera();
	
	private boolean mIsInBlindMode;
	
	private ArrayList<BlindInfo> mBlindSet = null;
	
	private Animation firstFramAnima;
	
	public BlindsImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public BlindsImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BlindsImageView(Context context) {
		super(context);
		init();
	}

	private void init() {
		
		mMaxAffectRadius = getResources().getDimension(R.dimen.touchEffectRadius);
		
		mBlindPaint = new Paint();
		mBlindPaint.setStyle(Style.FILL);
		mBlindPaint.setAntiAlias(true);
		mBlindPaint.setFilterBitmap(true);
		
        
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		setupBlinds((int)getResources().getDimension(R.dimen.blindHeight));
		if (LOG_ON ) {
			Log. i(LOG_TAG,
					"onLayout. Layout properties changed - blinds set rebuilt. New set contains "
							+ mBlindSet.size() + " blinds");	
		}
	}
	
	/*@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		for (int i = 0; i < mBlindSet.size(); i++) {
			final float x = -55f + (float)i / ((float)mBlindSet.size() - 1f) * 110f;
			mBlindSet.get(i).setRotations(x, 0f, 0f);
		}
	}*/
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			mIsInBlindMode = true;
			calculateBlindRotations(event.getY());
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mIsInBlindMode = false;
			break;
		default:
			super.onTouchEvent(event);
			break;
		}
		invalidate();
		return true;
	}
	
	private void calculateBlindRotations(float yPos) {
		//当前叶中心
		float currentBlindPivotY;
		float normalizedVerticalDistanceFromTouch;
		
		for (BlindInfo currentBlind : mBlindSet) {
			currentBlindPivotY = currentBlind.getTop() +
					
					(float)currentBlind.getHeight() / 2f;
			normalizedVerticalDistanceFromTouch = Math.abs(
					(yPos - currentBlindPivotY) / mMaxAffectRadius);
			float xRotation = 0;
			//only rotate if within valid rang
			if (normalizedVerticalDistanceFromTouch <= 1f) {
				// rot(d) = -((d-0.55)*2)^2+1 where 0<=d
				final double normalizedRotationX = Math.max(0d, 
						(-Math. pow(
                                ((normalizedVerticalDistanceFromTouch - 0.55f) * 2f),
                                2) + 1));
				// Blind above touch means negative angle
				if (currentBlindPivotY < yPos) {
					xRotation = (float) - (CONFIG_MAX_ROTATION * normalizedRotationX);
				} else {
					xRotation = (float) (CONFIG_MAX_ROTATION * normalizedRotationX);
				}
			}
			currentBlind.setRotations(xRotation, 0f, 0f);
		}
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		drawCustomStuff(canvas);
	}
	
	private void drawCustomStuff(Canvas screenCanvas) {
		if (LOG_ON) {
			Log.d(LOG_TAG, "drawCustomStuff (doing the custom drawing of this ViewGroup)");
		}
		
		final boolean initBmpAndCanvas = (mIsInBlindMode && (!(mUndistortedBitmap != null && !mUndistortedBitmap.isRecycled())));
		
		if (!mIsInBlindMode || (mIsInBlindMode && initBmpAndCanvas)) {
			//Draw normally
			if (mIsInBlindMode && initBmpAndCanvas) {				
				mUndistortedBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
				mUndistortedCanvas = new Canvas(mUndistortedBitmap);
			}
			Canvas canvasToDrawto = mIsInBlindMode ? mUndistortedCanvas : screenCanvas;
			drawUndistorted(canvasToDrawto);
		}
		if (mIsInBlindMode) {
			drawBlinds(screenCanvas);
		}
	}
	
	private void drawUndistorted(Canvas canvas) {
		Log. d( LOG_TAG, "Performing undistorted draw" );
		if (mBgDrawable != null) {
			mBgDrawable.draw(canvas);
		}
		super.dispatchDraw(canvas);
	}
	
	private void setupBlinds(int blindHeight) {
		if (blindHeight == 0) {
			throw new IllegalArgumentException("blindHeight must be >0");
		}
		ArrayList<BlindInfo> bi = new ArrayList<BlindInfo>();
		int accumulatedHeight = 0;
		do {
			BlindInfo blindInfo = new BlindInfo(0, accumulatedHeight, getWidth(), accumulatedHeight + blindHeight);
			bi.add(blindInfo);
			accumulatedHeight += blindHeight;
		} while (accumulatedHeight < getHeight());
		mBlindSet = bi;
		
		startOneWayBlindAnimation();
	}
	
	private void calculateOneWayBlindRotations(float interpolatedTime) {
		//0 <= interpolatedTime <= 1
		float currentImitatePosY = interpolatedTime * getHeight();
		//当前叶中心
		float currentBlindPivotY;
		float normalizedVerticalDistanceFromTouch;
		
		for (BlindInfo currentBlind : mBlindSet) {
			currentBlindPivotY = currentBlind.getTop() +
					
					(float)currentBlind.getHeight() / 2f;
			normalizedVerticalDistanceFromTouch = (
					(currentImitatePosY - currentBlindPivotY) / mMaxAffectRadius);
			float xRotation = currentBlind.getmRotationX();
			//only rotate if within valid rang
			if (normalizedVerticalDistanceFromTouch >= 0 && normalizedVerticalDistanceFromTouch <= 1f) {
				// rot(d) = -((d-0.55)*2)^2+1 where 0<=d
				final double normalizedRotationX = Math.max(0d, 
						(-Math. pow(
                                ((normalizedVerticalDistanceFromTouch - 0.55f) * 2f),
                                2) + 1));
				// Blind above touch means negative angle
				/*if (currentBlindPivotY < currentImitatePosY) {
					xRotation = (float) - (CONFIG_MAX_ROTATION * normalizedRotationX);
				} else {
				}*/
				xRotation = (float) (CONFIG_MAX_ROTATION * normalizedRotationX);
			} else if (xRotation < 90f) {
				xRotation = 0f;
			}
			currentBlind.setRotations(xRotation, 0f, 0f);
		}
	}
	
	private void drawBlinds(Canvas canvas) {
		Log. d( LOG_TAG, "Performing draw in blinds mode (well, not really, but it will be!)" );
		for (BlindInfo info : mBlindSet) {
			drawBlind(info, canvas);
		};
	}
	
	private void drawBlind(BlindInfo blindInfo, Canvas canvas) {
		
		final int height = blindInfo.getHeight();
		final int width = blindInfo.getWidth();
		final int coordX = blindInfo.getLeft();
		final int coordY = blindInfo.getTop();
		final float xRotation = blindInfo.getmRotationX();
		final float yRotation = blindInfo.getmRotationY();
		final float zRotation = blindInfo.getmRotationZ();
		
		mBlindPaint.setColorFilter(calculateLight(xRotation));

		//prepare canvas and camera
        canvas.save();
        mCamera.save();
        // Preconcat the current matrix with the specified translation
        canvas.translate(coordX + width / 2f, coordY + height / 2f);
        
        //apply tramsformations
        mCamera.rotateX(xRotation);
        
        Matrix cameraMatrix = new Matrix();
        mCamera.getMatrix(cameraMatrix);
        canvas.concat(cameraMatrix);
        
        //src位图的指定区域 dst画板区域
        //截取位图指定src区域绘制到画板dst区域
        final Rect src = new Rect(coordX, coordY, coordX + width, coordY + height);
        final RectF dst = new RectF(-width / 2f, -height / 2f, width / 2f, height / 2f);
        canvas.drawBitmap(mUndistortedBitmap, src, dst, mBlindPaint);
        
        canvas.restore();
        mCamera.restore();
	}
	
	public void startOneWayBlindAnimation() {
		
		if(firstFramAnima != null && firstFramAnima.hasStarted()) return;
		
		firstFramAnima = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				super.applyTransformation(interpolatedTime, t);
				Log.i(LOG_TAG, "applyTransformation: " + interpolatedTime);
				calculateOneWayBlindRotations(interpolatedTime);
				postInvalidate();
			}
		};
        //firstFramAnima.setRepeatCount(mBlindSet.size());
		firstFramAnima.setDuration(4000);
		firstFramAnima.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				Log.i(LOG_TAG, "onAnimationStart ");
				if (mBlindSet != null) {
					mIsInBlindMode = true;
					for (BlindInfo info : mBlindSet) {
						info.setRotations(90f, 0f, 0f);
					}
				}
				postInvalidate();
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				Log.i(LOG_TAG, "onAnimationRepeat ");
				/*if(countAnima >= 0)
					mBlindSet.get(countAnima).setRotations(0f, 0f, 0f);
				countAnima--;
				postInvalidate();*/
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				Log.i(LOG_TAG, "onAnimationEnd");
				mIsInBlindMode = false;
			}
		});
		startAnimation(firstFramAnima);
	}
	
	public void setBackground(int id) {
		mBgDrawable = (BitmapDrawable) getResources().getDrawable(id);
		centerBgDrawable();
	}
	
	@Override
	public void setBackground(Drawable background) {
		mBgDrawable = (BitmapDrawable) background;
		centerBgDrawable();
	}
	
	private void centerBgDrawable() {
		if (mBgDrawable != null) {
			final DisplayMetrics dm = getResources().getDisplayMetrics();
			mBgDrawable.setTargetDensity(dm);
			mBgDrawable.setGravity(Gravity.CENTER);
			mBgDrawable.setBounds(0, 0, dm.widthPixels, dm.heightPixels);
		}
		postInvalidate();
	}
	
	//计算光暗
    /** Ambient light intensity */
    private static final int AMBIENT_LIGHT = 55;
     
    /** Diffuse light intensity */
    private static final int DIFFUSE_LIGHT = 255;
     
    /** Specular light intensity */
    private static final float SPECULAR_LIGHT = 70;
     
    /** Shininess constant */
    private static final float SHININESS = 255;
     
    /** The max intensity of the light */
    private static final int MAX_INTENSITY = 0xFF;
     
    /** Light source angular offset*/
    private static final float LIGHT_SOURCE_ANGLE = 38f;
     
    private LightingColorFilter calculateLight(float rotation) {
            rotation -= LIGHT_SOURCE_ANGLE;
            final double cosRotation = Math.cos (Math.PI * rotation / 180);
            int intensity = AMBIENT_LIGHT + (int) ( DIFFUSE_LIGHT * cosRotation);
            int highlightIntensity = (int) (SPECULAR_LIGHT * Math.pow(cosRotation,
                            SHININESS));
     
            if (intensity > MAX_INTENSITY) {
                    intensity = MAX_INTENSITY;
            }
            if (highlightIntensity > MAX_INTENSITY) {
                    highlightIntensity = MAX_INTENSITY;
            }
     
            final int light = Color.rgb (intensity, intensity, intensity);
            final int highlight = Color.rgb (highlightIntensity, highlightIntensity,
                            highlightIntensity);
     
            return new LightingColorFilter(light, highlight);
    }
    
    private static class BlindInfo {
    	private final Rect mBounds;
    	private float mRotationX, mRotationY, mRotationZ;
    	
    	public BlindInfo(int l, int t, int r, int b) {
    		mBounds = new Rect(l, t, r, b);
    	}
    	
    	public void setRotations(float rotationX, float rotationY, float rotationZ) {
    		this.mRotationX = rotationX;
    		this.mRotationY = rotationY;
    		this.mRotationZ = rotationZ;
    	}

    	public int getHeight() {
    		return mBounds.height();
    	}

    	public int getWidth() {
    		return mBounds.width();
    	}

    	public int getLeft() {
    		return mBounds.left;
    	}

    	public int getRight() {
    		return mBounds.right;
    	}

    	public int getTop() {
    		return mBounds.top;
    	}

    	public int getBottom() {
    		return mBounds.bottom;
    	}

    	public float getmRotationX() {
    		return mRotationX;
    	}

    	public float getmRotationY() {
    		return mRotationY;
    	}

    	public float getmRotationZ() {
    		return mRotationZ;
    	}
    }
}
