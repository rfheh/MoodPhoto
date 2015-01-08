
/**  
 * @Description: TODO
 * @author hx Lu
 * @date 2014-11-27 下午4:40:22
 */ 
package com.mp.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.mp.R;
import com.mp.application.MPApplication;
import com.mp.util.BitmapUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

/**
 * @Description: 缩放效果
 * @Author:hx Lu
 * @Since:2014-11-27
 */

public class ZoomImageFromThumbWorker {

	/**
     * Hold a reference to the current animator, so that it can be canceled mid-way.
     */
    private static Animator mCurrentAnimator;
	
    /**
     * The system "short" animation time duration, in milliseconds. This duration is ideal for
     * subtle animations or animations that occur very frequently.
     */
    private static int mShortAnimationDuration = 200;
    
    private View mThumbView, mExpandedView, mExpandedParentView;
    private ImageView mExpandedImageView;
    private String mImageUri;
    
    // Calculate the starting and ending bounds for the zoomed-in image. This step
    // involves lots of math. Yay, math.
    final Rect mStartBounds = new Rect();
    final Rect mFinalBounds = new Rect();
    final Point mGlobalOffset = new Point();
    float mStartScale;
    AnimatorSet mExpandedSet;
    AnimatorSet mCloseSet;
    boolean mHasExpanded;
    
    public ZoomImageFromThumbWorker(View thumbView, ImageView expandedImageView, View expandedView, View expandedParentView, String imageUri) {
    	mThumbView = thumbView;
    	mExpandedImageView = expandedImageView;
    	mExpandedView = expandedView;
    	mExpandedParentView = expandedParentView;
    	mImageUri = imageUri;
    	
    	initAndCalculateViewAreas();
    }
    
    private void initAndCalculateViewAreas() {
    	// The start bounds are the global visible rectangle of the thumbnail, and the
        // final bounds are the global visible rectangle of the container view. Also
        // set the container view's offset as the origin for the bounds, since that's
        // the origin for the positioning animation properties (X, Y).
        mThumbView.getGlobalVisibleRect(mStartBounds);
        mExpandedParentView.getGlobalVisibleRect(mFinalBounds, mGlobalOffset);
        mStartBounds.offset(-mGlobalOffset.x, -mGlobalOffset.y);
        mFinalBounds.offset(-mGlobalOffset.x, -mGlobalOffset.y);
        
        // Adjust the start bounds to be the same aspect ratio as the final bounds using the
        // "center crop" technique. This prevents undesirable stretching during the animation.
        // Also calculate the start scaling factor (the end scaling factor is always 1.0).
        
        if ((float) mFinalBounds.width() / mFinalBounds.height()
                > (float) mStartBounds.width() / mStartBounds.height()) {
            // Extend start bounds horizontally
        	mStartScale = (float) mStartBounds.height() / mFinalBounds.height();
            float startWidth = mStartScale * mFinalBounds.width();
            float deltaWidth = (startWidth - mStartBounds.width()) / 2;
            mStartBounds.left -= deltaWidth;
            mStartBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
        	mStartScale = (float) mStartBounds.width() / mFinalBounds.width();
            float startHeight = mStartScale * mFinalBounds.height();
            float deltaHeight = (startHeight - mStartBounds.height()) / 2;
            mStartBounds.top -= deltaHeight;
            mStartBounds.bottom += deltaHeight;
        }
    }
    
    private void initExpandedSet(AnimatorSet expandedSet) {
    	if(expandedSet == null) return;
    	expandedSet
    	.play(ObjectAnimator.ofFloat(mExpandedView, View.X, mStartBounds.left,
                mFinalBounds.left))
        .with(ObjectAnimator.ofFloat(mExpandedView, View.Y, mStartBounds.top,
                mFinalBounds.top))
        .with(ObjectAnimator.ofFloat(mExpandedView, View.SCALE_X, mStartScale, 1f))
        .with(ObjectAnimator.ofFloat(mExpandedView, View.SCALE_Y, mStartScale, 1f));
        mExpandedSet.setDuration(mShortAnimationDuration);
        mExpandedSet.setInterpolator(new DecelerateInterpolator());
        mExpandedSet.addListener(new AnimatorListenerAdapter() {
		    @Override
		    public void onAnimationEnd(Animator animation) {
		        mCurrentAnimator = null;
		    }
		
		    @Override
		    public void onAnimationCancel(Animator animation) {
		        mCurrentAnimator = null;
		    }
		});
    }
    
    private void initCloseSet(AnimatorSet closeSet) {
    	if(closeSet == null) return;
    	closeSet
    	.play(ObjectAnimator.ofFloat(mExpandedView, View.X, mStartBounds.left))
        .with(ObjectAnimator.ofFloat(mExpandedView, View.Y, mStartBounds.top))
        .with(ObjectAnimator.ofFloat(mExpandedView, View.SCALE_X, mStartScale))
        .with(ObjectAnimator.ofFloat(mExpandedView, View.SCALE_Y, mStartScale));
        mCloseSet.setDuration(mShortAnimationDuration);
        mCloseSet.setInterpolator(new DecelerateInterpolator());
        mCloseSet.addListener(new AnimatorListenerAdapter() {
		    @Override
		    public void onAnimationEnd(Animator animation) {
		        mThumbView.setAlpha(1f);
		        mExpandedView.setVisibility(View.GONE);
		        mCurrentAnimator = null;
		    }
		
		    @Override
		    public void onAnimationCancel(Animator animation) {
		        mThumbView.setAlpha(1f);
		        mExpandedView.setVisibility(View.GONE);
		        mCurrentAnimator = null;
		    }
		});
    }
    
    private void displayImage() {
    	ImageLoader.getInstance().displayImage(
        		mImageUri, 
        		mExpandedImageView, 
        		new DisplayImageOptions.Builder()
        		.showImageOnLoading(R.drawable.empty_photo)
				.cacheInMemory(true)
				.cacheInMemory(true)
				.considerExifParams(true)
				.displayer(new BitmapDisplayer() {
					@Override
					public void display(Bitmap bitmap, ImageAware imageAware,
							LoadedFrom loadedFrom) {
						Bitmap borderBitmap = BitmapUtil.drawableToBitmap(MPApplication.getContext().getResources().getDrawable(R.drawable.bg_white_border));
						Bitmap rotaBitmap = BitmapUtil.isHorizontalBitmap(bitmap) ? bitmap : BitmapUtil.adjustPhotoRotation(bitmap, 90);
						Bitmap newBitmap = BitmapUtil.drawImageBorder(rotaBitmap, borderBitmap);
						imageAware.setImageBitmap(newBitmap);
					}
				})
				.build());
    }
    
    public void expandedImageFromThumb() {
    	if(mHasExpanded) return;
    	
    	if (mCurrentAnimator != null) {
			mCurrentAnimator.cancel();
		}
    	
    	displayImage();
    	
    	mThumbView.setAlpha(0f);
        mExpandedView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCaALE_X and SCALE_Y transformations to the top-left corner of
        // the zoomed-in view (the default is the center of the view).
        mExpandedView.setPivotX(0f);
        mExpandedView.setPivotY(0f);
    	
    	mExpandedSet = new AnimatorSet();
    	initExpandedSet(mExpandedSet);
    	mCurrentAnimator = mExpandedSet;
    	mCurrentAnimator.start();
    	mHasExpanded = true;
    }
    
    public void closeImageToThumb() {
    	if(!mHasExpanded) return;
    	
    	if (mCurrentAnimator != null) {
			mCurrentAnimator.cancel();
		}
    	
    	if (mThumbView == null) {
    		mExpandedView.setVisibility(View.GONE);
	        mCurrentAnimator = null;
	        return;
		}
    	
    	mCloseSet = new AnimatorSet();
    	initCloseSet(mCloseSet);
    	mCurrentAnimator = mCloseSet;
    	mCurrentAnimator.start();
    	mHasExpanded = false;
    }
    
    public void resetImageUri(View newThumbView, String uri) {
    	mThumbView = newThumbView;
    	mImageUri = uri;
    	initAndCalculateViewAreas();
    }
    
    public boolean hasExpanded() {
    	return mHasExpanded;
    }
    
	/**
     * "Zooms" in a thumbnail view by assigning the high resolution image to a hidden "zoomed-in"
     * image view and animating its bounds to fit the entire activity content area. More
     * specifically:
     *
     * <ol>
     *   <li>Assign the high-res image to the hidden "zoomed-in" (expanded) image view.</li>
     *   <li>Calculate the starting and ending bounds for the expanded view.</li>
     *   <li>Animate each of four positioning/sizing properties (X, Y, SCALE_X, SCALE_Y)
     *       simultaneously, from the starting bounds to the ending bounds.</li>
     *   <li>Zoom back out by running the reverse animation on click.</li>
     * </ol>
     *
     * @param thumbView  The thumbnail view to zoom in.
     * @param imageResId The high-resolution version of the image represented by the thumbnail.
     */
    public static void zoomImageFromThumb(final View thumbView, ImageView expandedImageView, final View expandedView, 
    		View parentView, String uri) {
    	// If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        
        ImageLoader.getInstance().displayImage(
        		uri, 
        		expandedImageView, 
        		new DisplayImageOptions.Builder()
        		.showImageOnLoading(R.drawable.empty_photo)
				.cacheInMemory(true)
				.cacheInMemory(true)
				.considerExifParams(true)
				.displayer(new BitmapDisplayer() {
					@Override
					public void display(Bitmap bitmap, ImageAware imageAware,
							LoadedFrom loadedFrom) {
						Bitmap borderBitmap = BitmapUtil.drawableToBitmap(MPApplication.getContext().getResources().getDrawable(R.drawable.bg_white_border));
						Bitmap rotaBitmap = BitmapUtil.isHorizontalBitmap(bitmap) ? bitmap : BitmapUtil.adjustPhotoRotation(bitmap, 90);
						Bitmap newBitmap = BitmapUtil.drawImageBorder(rotaBitmap, borderBitmap);
						imageAware.setImageBitmap(newBitmap);
					}
				})
				.build());
        
        // Calculate the starting and ending bounds for the zoomed-in image. This step
        // involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();
        
        // The start bounds are the global visible rectangle of the thumbnail, and the
        // final bounds are the global visible rectangle of the container view. Also
        // set the container view's offset as the origin for the bounds, since that's
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        parentView.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);
        
        // Adjust the start bounds to be the same aspect ratio as the final bounds using the
        // "center crop" technique. This prevents undesirable stretching during the animation.
        // Also calculate the start scaling factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation begins,
        // it will position the zoomed-in view in the place of the thumbnail.
        thumbView.setAlpha(0f);
        expandedView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
        // the zoomed-in view (the default is the center of the view).
        expandedView.setPivotX(0f);
        expandedView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedView, View.X, startBounds.left,
                        finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedView, View.Y, startBounds.top,
                        finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down to the original bounds
        // and show the thumbnail instead of the expanded image.
        final float startScaleFinal = startScale;
        expandedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel, back to their
                // original values.
                AnimatorSet set = new AnimatorSet();
                set
                        .play(ObjectAnimator.ofFloat(expandedView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedView, View.Y, startBounds.top))
                        .with(ObjectAnimator.ofFloat(expandedView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator.ofFloat(expandedView, View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}

    